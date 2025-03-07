def call() {
    // Use credentials to authenticate with Nexus repository
    withCredentials([usernamePassword(credentialsId: 'Nexus', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
        bat """
        curl -u %USERNAME%:%PASSWORD% ^
            --upload-file ${params.Module_Name}-${params.Build_Version}.zip ^
            https://rndnexus.progrec.com/repository/Module-ProGReC-APPS/${params.Module_Name}/${params.Module_Name}-${params.Build_Version}.zip
        """
        echo "Module ${params.Module_Name}-${params.Build_Version} uploaded to Nexus repository."
    }
}
