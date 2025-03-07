def call() {
    def jsonFilePath = "${pwd()}/report.json"
    def module = params.Module_Name
    def version = params.Build_Version
    def currentTime = new Date().format("yyyy-MM-dd'T'HH:mm:ss.SSSZ", TimeZone.getTimeZone('Asia/Kolkata'))
    
    def jsonContent = """
    {
        "Module_Name": "${module}",
        "Build_Version": "${version}",
        "Time": "${currentTime}"
    }
    """
    
    writeFile file: jsonFilePath, text: jsonContent
}
