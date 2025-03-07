def call() {
    bat """
        @echo off
        SETLOCAL ENABLEDELAYEDEXPANSION
        
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
        REM cd ./${params.Module_Name}
        echo Directory paths have been extracted to %OUTPUT_FILE%.
        endlocal
    """
}