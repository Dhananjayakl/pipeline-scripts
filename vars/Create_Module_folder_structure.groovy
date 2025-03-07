def call() {
    bat """
        @echo off
        :: Create the main directory with module name and build version
        mkdir "%Module_Name%"
        cd "%Module_Name%"

        :: Create subdirectories
        mkdir client objects scripts server
        mkdir scripts\\post scripts\\pre

        echo Directory structure created successfully.
    """
}
