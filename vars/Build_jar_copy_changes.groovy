def call() {
        bat """
            setlocal enabledelayedexpansion
            @echo off
            set found=0
            for %%f in (temp\\server\\*) do (
                if "%%~nxf"=="pom.xml" (
                    set found=1
                )
            )
            if !found! neq 0 (
                cd server
                mvn package
                echo "Module jar build success"
                cd server/target
                cd
                echo "before rename"
                for %%f in (*.jar) do ren "%%f" ${params.Module_Name}.jar
                echo "after rename"
                cd ..
                cd ..
                cd
                copy "server\\target\\${params.Module_Name}.jar" "${params.Module_Name}\\server" 
            ) else (
                echo "There are no changes in the server"
            )
            endlocal    
            """
    
    def result = bat(
        script: """
            robocopy temp ${params.Module_Name} /E /XF Jenkinsfile report.json Stash.bat .gitignore /XD server scripts
            robocopy scripts ${params.Module_Name}\\scripts /E
            REM robocopy . ${params.Module_Name} report.json
        """,
        returnStatus: true
    )

    if (result != 0 && result != 1 && result != 3) {
        error "Robocopy failed with exit code ${result}"
    }
}
