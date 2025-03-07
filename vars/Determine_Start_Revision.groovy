def call() {
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
