def call() {
    // Find all pom.xml files in server directories
    def dirs = findFiles(glob: '**/server/pom.xml')
    
    dirs.each { dir ->
        def pomFile = dir.path
        echo "Reading POM file: ${pomFile}"
        
        // Read the pom.xml file
        def pom = readMavenPom(file: pomFile)
        def artifactId = pom.artifactId
        def version = pom.version

        // Echo the extracted values
        echo "artifactId: ${artifactId}"
        echo "version: ${version}"
    }
}
