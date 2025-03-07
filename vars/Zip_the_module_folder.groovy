def call() {
    bat """
        @echo off
        set "source_folder=${params.Module_Name}"
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
