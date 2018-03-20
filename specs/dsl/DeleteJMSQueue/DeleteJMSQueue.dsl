def testProjectName = args.projectName
def wasResourceName = args.wasResourceName
project testProjectName

procedure 'DeleteJMSQueue', {
  description = ''
  jobNameTemplate = ''
  projectName = testProjectName
  resourceName = ''
  timeLimit = ''
  timeLimitUnits = 'minutes'
  workspaceName = ''

  formalParameter 'confignameDJMSQ', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'messagingSystemTypeDJMSQ', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'queueAdministrativeNameDJMSQ', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'queueScopeDJMSQ', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'wasResourceName', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  step 'DeleteJMSQueue', {
    description = 'DJMSQ'
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
    subprocedure = 'DeleteJMSQueue'
    subproject = '/plugins/EC-WebSphere/project'
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workingDirectory = null
    workspaceName = '$[wasResourceName]'
    actualParameter 'configname', '$[confignameDJMSQ]'
    actualParameter 'messagingSystemType', '$[messagingSystemTypeDJMSQ]'
    actualParameter 'queueAdministrativeName', '$[queueAdministrativeNameDJMSQ]'
    actualParameter 'queueScope', '$[queueScopeDJMSQ]'
  }

  // Custom properties

  property 'ec_customEditorData', {

    // Custom properties

    property 'parameters', {

      // Custom properties

      property 'confignameDJMSQ', {

        // Custom properties
        formType = 'standard'
      }

      property 'messagingSystemTypeDJMSQ', {

        // Custom properties
        formType = 'standard'
      }

      property 'queueAdministrativeNameDJMSQ', {

        // Custom properties
        formType = 'standard'
      }

      property 'queueScopeDJMSQ', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasResourceName', {

        // Custom properties
        formType = 'standard'
      }
    }
  }
}
