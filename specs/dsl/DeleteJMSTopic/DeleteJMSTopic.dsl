def testProjectName = args.projectName
def wasResourceName = args.wasResourceName
project testProjectName

procedure 'DeleteJMSTopic', {
  description = ''
  jobNameTemplate = ''
  projectName = testProjectName
  resourceName = ''
  timeLimit = ''
  timeLimitUnits = 'minutes'
  workspaceName = ''

  formalParameter 'confignameDJMST', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'messagingSystemTypeDJMST', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'queueAdministrativeNameDJMST', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'queueScopeDJMST', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  step 'DeleteJMSTopic', {
    description = ''
    alwaysRun = '0'
    broadcast = '0'
    command = null
    condition = ''
    errorHandling = 'failProcedure'
    exclusiveMode = 'none'
    logFileName = null
    parallel = '0'
    postProcessor = null
    precondition = ''
    projectName = testProjectName
    releaseMode = 'none'
    resourceName = '$[wasResourceName]'
    shell = null
    subprocedure = 'DeleteJMSTopic'
    subproject = '/plugins/EC-WebSphere/project'
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workingDirectory = null
    workspaceName = '$[wasResourceName]'
    actualParameter 'configname', '$[confignameDJMST]'
    actualParameter 'messagingSystemType', '$[messagingSystemTypeDJMST]'
    actualParameter 'topicAdministrativeName', '$[queueAdministrativeNameDJMST]'
    actualParameter 'topicScope', '$[queueScopeDJMST]'
  }

  // Custom properties

  property 'ec_customEditorData', {

    // Custom properties

    property 'parameters', {

      // Custom properties

      property 'confignameDJMST', {

        // Custom properties
        formType = 'standard'
      }

      property 'messagingSystemTypeDJMST', {

        // Custom properties
        formType = 'standard'
      }

      property 'queueAdministrativeNameDJMST', {

        // Custom properties
        formType = 'standard'
      }

      property 'queueScopeDJMST', {

        // Custom properties
        formType = 'standard'
      }
    }
  }
}
