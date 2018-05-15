def testProjectName = args.projectName
def wasResourceName = args.wasResourceName
project testProjectName

procedure 'DeleteConnectionFactory', {
  description = ''
  jobNameTemplate = ''
  projectName = testProjectName
  resourceName = ''
  timeLimit = ''
  timeLimitUnits = 'minutes'
  workspaceName = ''

  formalParameter 'confignameDCF', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'factoryAdministrativeNameDCF', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'factoryScopeDCF', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'messagingSystemTypeDCF', defaultValue: '', {
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

  step 'DeleteConnectionFactory', {
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
    subprocedure = 'DeleteJMSConnectionFactory'
    subproject = '/plugins/EC-WebSphere/project'
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workingDirectory = null
    workspaceName = '$[wasResourceName]'
    actualParameter 'configname', '$[confignameDCF]'
    actualParameter 'factoryAdministrativeName', '$[factoryAdministrativeNameDCF]'
    actualParameter 'factoryScope', '$[factoryScopeDCF]'
    actualParameter 'messagingSystemType', '$[messagingSystemTypeDCF]'
  }

  // Custom properties

  property 'ec_customEditorData', {

    // Custom properties

    property 'parameters', {

      // Custom properties

      property 'confignameDCF', {

        // Custom properties
        formType = 'standard'
      }

      property 'factoryAdministrativeNameDCF', {

        // Custom properties
        formType = 'standard'
      }

      property 'factoryScopeDCF', {

        // Custom properties
        formType = 'standard'
      }

      property 'messagingSystemTypeDCF', {

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
