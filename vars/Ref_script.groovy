
def call() {
    // Workspace setup and cleanup
    stage('Workspace_Cleanup') {
        script {
            dir("${params.Module_Name}") {
                cleanWs()
            }
        }
    }

        
        //REM Set the working directory to the specific path
       // REM cd /D D:\\Jenkins\\workspace\\GRC
        
    stage('Git_Clone') {
        script {
        echo "Cloning repository based on Module_Name parameter..."
        def repoUrl

        // Determine which repository URL to use based on Module_Name parameter
        switch (params.Module_Name) {
            case 'grc':
                repoUrl = env.grc_repo
                break
            case 'issue':
                repoUrl = env.issue_repo
                break
            default:
                error "Unsupported Module_Name parameter value: ${params.Module_Name}"
        }

        // Clone the selected repository
        bat "git clone ${repoUrl}"
    } 
        }
    
    // Determine Start Revision
    stage('Determine_Start_Revision') {
    dir("${params.Module_Name}") {
        if (params.Start_Revision == 'TAIL') {
        // Fetch initial commit ID
        def Initial_commit = bat(script: """
            @echo off
            git rev-list --max-parents=0 HEAD > commit.txt
            set /p Initial_commit=<commit.txt
            echo %Initial_commit%
        """, returnStdout: true).trim()

        // Assign initial commit ID to Revision
        env.Start_Revision = Initial_commit
    } else {
        // Use provided revision from parameters
        env.Start_Revision = params.Start_Revision
    }

    // Echo the Revision with the desired prefix
    echo "Revision: ${env.Start_Revision}"
    }
}

    // Git checkout
    stage('Git_Checkout') {
        script {
            dir("${params.Module_Name}") {
               bat """
        @echo off
        :: Checkout the specified branch
        echo Checking out branch...
        git checkout ${params.Branch_Name} || exit /b 1

        :: Checkout the specified revision
        echo Checking out revision...
        git checkout ${params.End_Revision} || exit /b 1

        :: Display the start revision
        echo Revision from previous stage: %Start_Revision%

        :: List modified files between revisions and output to diff_list.txt
        echo Listing modified files...
        git diff --name-only %Start_Revision% ${params.End_Revision} > diff_list.txt || exit /b 1
    """
            }
        }
    }

    // Copy changes to temp folder
    stage('Copy_Changes_to_temp_folder') {
        script {
        dir("${params.Module_Name}") {
          bat """
        @echo off
        SETLOCAL ENABLEDELAYEDEXPANSION
        
        REM Set the working directory to the specific path
        REM cd /D D:\\Jenkins\\workspace\\GRC
        
        REM Temporary file for output
        SET TempFile=%TEMP%\\${params.Module_Name}_temp_diff_list.txt
        
        REM Process each line and replace slashes
        IF EXIST "%TempFile%" del "%TempFile%"
        
        FOR /F "tokens=*" %%A in (diff_list.txt) DO (
            SET "line=%%A"
            SET "line=!line:/=\\\\!"
            echo !line! >> "%TempFile%"
        )
        
        REM Move the temporary file back to the original
        MOVE /Y "%TempFile%" diff_list.txt > NUL
        
        echo ForwardSlash changed to Backwardslash, Conversion completed.
        
        REM Create an output file to store results
        set OUTPUT_FILE=dir_list.txt
        
        REM Check if output file already exists and delete if it does
        if exist "%OUTPUT_FILE%" del "%OUTPUT_FILE%"
        
        REM Process each line from diff_list.txt
        for /F "tokens=*" %%A in (diff_list.txt) do (
            REM Get the directory path by removing the filename
            set "LINE=%%A"
            for %%B in ("!LINE!") do (
                REM Set the directory by removing the last token
                set "DIR_PATH=!LINE:%%~nxB=!"
            )
            
            REM Write the directory path to the output file
            echo !DIR_PATH!>> "%OUTPUT_FILE%"
            echo !DIR_PATH!
            
            REM Check if the file exists before copying
            if exist "%%A" (
                xcopy %%A temp\\!DIR_PATH!
                echo Copied %%A
            ) else (
                echo File %%A does not exist, skipping.
            )
        )
        
        echo Directory paths have been extracted to %OUTPUT_FILE%.
        endlocal
    """
        }
    }
}
       
    // Create module folder structure and copy updated files //
    stage('Create_Module_folder_structure') {
        script {
            dir("${params.Module_Name}") {
                bat """
        @echo off
        :: Create the main directory with module name and build version
        mkdir "%Module_Name%-%Build_Version%"
        cd "%Module_Name%-%Build_Version%"

        :: Create subdirectories
        mkdir client objects scripts server
        mkdir scripts\\post scripts\\pre

        echo Directory structure created successfully.
    """
            }
        }
    }
        
    //Build_jar_copy_changes
    stage('Build_jar_copy_changes') {
        script {
            dir("${params.Module_Name}") {
        bat """
            setlocal enabledelayedexpansion
            set found=0
            for %%f in (temp\\server\\*) do (
                if not "%%~nxf"=="README.txt" (
                    set found=1
                )
            )
            if !found! neq 0 (
                cd server
                mvn package
                echo "Module jar build success"
                cd target
                for %%f in (*.jar) do ren "%%f" ${params.Module_Name}.jar
                cd ..
                cd ..
                copy "server\\target\\${params.Module_Name}.jar" "${params.Module_Name}-${params.Build_Version}\\server" 
            ) else (
                echo "There are no changes in the server"
            )
            endlocal    
            """
    
    def result = bat(
        script: """
            robocopy temp ${params.Module_Name}-${params.Build_Version} /E /XF Jenkinsfile /XD server
        """,
        returnStatus: true
    )

    if (result != 0 && result != 1 && result != 3) {
        error "Robocopy failed with exit code ${result}"
    }
            }
        }
    }
    
    // Zip_the_module_folder
    stage('Zip_the_module_folder') {
        script {
            dir("${params.Module_Name}") {
              bat """
        @echo off
        set "source_folder=${params.Module_Name}-${params.Build_Version}"
        set "destination_tar=${params.Module_Name}-${params.Build_Version}"

        if not exist "%source_folder%" (
            echo Source folder does not exist.
            exit /b 1
        )

        echo Creating tar archive...
        tar -a -c -f "%destination_tar%.zip" "%source_folder%"

        if errorlevel 1 (
            echo Error occurred while creating the tar archive.
        ) else (
            echo Folder successfully archived using tar.
        )
    """ 
            }
        }
    }

    // Upload_to_Nexus
    stage('Upload_to_Nexus') {
        script {
            dir("${params.Module_Name}") {
             // Use credentials to authenticate with Nexus repository
    withCredentials([usernamePassword(credentialsId: 'Nexus', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
        bat """
        curl -u %USERNAME%:%PASSWORD% ^
            --upload-file ${params.Module_Name}-${params.Build_Version}.zip ^
            https://rndnexus.progrec.com/repository/Products/${params.Module_Name}/${params.Module_Name}-${params.Build_Version}.zip
        """
        echo "Module ${params.Module_Name}-${params.Build_Version} uploaded to Nexus repository."
    }   
            }
        }
    }
}
