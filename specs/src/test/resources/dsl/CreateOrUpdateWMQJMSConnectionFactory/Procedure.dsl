def testProjectName = args.projectName
def wasResourceName = args.wasResourceName

procedure 'CreateOrUpdateWMQJMSConnectionFactory', {
  description = ''
  jobNameTemplate = ''
  projectName = testProjectName
  resourceName = ''
  timeLimit = ''
  timeLimitUnits = 'minutes'
  workspaceName = ''

  formalParameter 'wasAdditionalOptions', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'wasCCDQM', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'wasCCDURL', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'wasCFDescription', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'wasCFName', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'wasCFScope', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'wasCFType', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'wasConfigName', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'wasJNDIName', defaultValue: '', {
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

  step 'CreateOrUpdateWMQJMSConnectionFactory', {
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
    subprocedure = 'CreateOrUpdateWMQJMSConnectionFactory'
    subproject = '/plugins/EC-WebSphere/project'
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workingDirectory = null
    workspaceName = ''
    actualParameter 'additionalOptions', '$[wasAdditionalOptions]'
    actualParameter 'clientChannelDefinitionQueueManager', '$[wasCCDQM]'
    actualParameter 'clientChannelDefinitionUrl', '$[wasCCDURL]'
    actualParameter 'configname', '$[wasConfigName]'
    actualParameter 'factoryAdministrativeDescription', '$[wasCFDescription]'
    actualParameter 'factoryAdministrativeName', '$[wasCFName]'
    actualParameter 'factoryScope', '$[wasCFScope]'
    actualParameter 'factoryType', '$[wasCFType]'
    actualParameter 'jndiName', '$[wasJNDIName]'
  }

  // Custom properties

  property 'ec_customEditorData', {

    // Custom properties

    property 'parameters', {

      // Custom properties

      property 'wasAdditionalOptions', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasCCDQM', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasCCDURL', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasCFDescription', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasCFName', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasCFScope', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasCFType', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasConfigName', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasJNDIName', {

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
