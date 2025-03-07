def call() {
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
