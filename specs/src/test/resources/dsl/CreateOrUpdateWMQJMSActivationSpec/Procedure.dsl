def testProjectName = args.projectName
def wasResourceName = args.wasResourceName

procedure 'CreateOrUpdateWMQJMSActivationSpec', {
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

  formalParameter 'wasASDescription', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'wasASJNDIName', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'wasASName', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'wasASScope', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
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

  formalParameter 'wasConfigName', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'wasDestinationJNDIName', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'wasDestinationJNDIType', defaultValue: '', {
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

  step 'CreateOrUpdateWMQJMSActivationSpec', {
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
    subprocedure = 'CreateOrUpdateWMQJMSActivationSpec'
    subproject = '/plugins/EC-WebSphere/project'
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workingDirectory = null
    workspaceName = ''
    actualParameter 'additionalOptions', '$[wasAdditionalOptions]'
    actualParameter 'clientChannelDefinitionQueueManager', '$[wasCCDQM]'
    actualParameter 'clientChannelDefinitionUrl', '$[wasCCDURL]'
    actualParameter 'configname', '$[wasConfigName]'
    actualParameter 'destinationJndiName', '$[wasDestinationJNDIName]'
    actualParameter 'destinationJndiType', '$[wasDestinationJNDIType]'
    actualParameter 'jndiName', '$[wasASJNDIName]'
    actualParameter 'specAdministrativeDescription', '$[wasASDescription]'
    actualParameter 'specAdministrativeName', '$[wasASName]'
    actualParameter 'specScope', '$[wasASScope]'
  }

  // Custom properties

  property 'ec_customEditorData', {

    // Custom properties

    property 'parameters', {

      // Custom properties

      property 'wasASDescription', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasASJNDIName', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasASName', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasASScope', {

        // Custom properties
        formType = 'standard'
      }

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

      property 'wasConfigName', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasDestinationJNDIName', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasDestinationJNDIType', {

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
