def testProjectName = args.projectName
def wasResourceName = args.wasResourceName

project testProjectName

  procedure 'runCustomShellCommand', {
    projectName = testProjectName

    formalParameter 'cli_command', defaultValue: '', {
        type = 'entry'
        }

    formalParameter 'stepRes', defaultValue: '', {
        type = 'entry'
        }

    formalParameter 'shellCommand', defaultValue: '', {
        type = 'entry'
        }

    resourceName = wasResourceName
 
    step 'runCustomShellCommand' , {
        projectName = testProjectName
        resourceName = '$[stepRes]'
        command = '$[cli_command]'
        shell = '$[shellCommand]'
        }
    

    }