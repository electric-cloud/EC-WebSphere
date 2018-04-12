def testProjectName = args.projectName
def wasResourceName = args.wasResourceName
project testProjectName

procedure 'CreateOrUpdateWMQActivationSpec', {
  description = ''
  jobNameTemplate = ''
  projectName = testProjectName
  resourceName = ''
  timeLimit = ''
  timeLimitUnits = 'minutes'
  workspaceName = ''

  formalParameter 'additionalOptionsCOUWMQAS', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'clientChannelDefinitionQueueManagerCOUWMQAS', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'clientChannelDefinitionUrlCOUWMQAS', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'confignameCOUWMQAS', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'destinationJndiNameCOUWMQAS', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'destinationJndiTypeCOUWMQAS', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'jndiNameCOUWMQAS', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'specAdministartiveDescriptionCOUWMQAS', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'specAdministrativeNameCOUWMQAS', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'specScopeCOUWMQAS', defaultValue: '', {
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

  step 'CreateorUpdateWMQActivationSpec', {
    description = 'COUWMQAS'
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
    workspaceName = '$[wasResourceName]'
    actualParameter 'additionalOptions', '$[additionalOptionsCOUWMQAS]'
    actualParameter 'clientChannelDefinitionQueueManager', '$[clientChannelDefinitionQueueManagerCOUWMQAS]'
    actualParameter 'clientChannelDefinitionUrl', '$[clientChannelDefinitionUrlCOUWMQAS]'
    actualParameter 'configname', '$[confignameCOUWMQAS]'
    actualParameter 'destinationJndiName', '$[destinationJndiNameCOUWMQAS]'
    actualParameter 'destinationJndiType', '$[destinationJndiTypeCOUWMQAS]'
    actualParameter 'jndiName', '$[jndiNameCOUWMQAS]'
    actualParameter 'specAdministartiveDescription', '$[specAdministartiveDescriptionCOUWMQAS]'
    actualParameter 'specAdministrativeName', '$[specAdministrativeNameCOUWMQAS]'
    actualParameter 'specScope', '$[specScopeCOUWMQAS]'
  }

  // Custom properties

  property 'ec_customEditorData', {

    // Custom properties

    property 'parameters', {

      // Custom properties

      property 'additionalOptionsCOUWMQAS', {

        // Custom properties
        formType = 'standard'
      }

      property 'clientChannelDefinitionQueueManagerCOUWMQAS', {

        // Custom properties
        formType = 'standard'
      }

      property 'clientChannelDefinitionUrlCOUWMQAS', {

        // Custom properties
        formType = 'standard'
      }

      property 'confignameCOUWMQAS', {

        // Custom properties
        formType = 'standard'
      }

      property 'destinationJndiNameCOUWMQAS', {

        // Custom properties
        formType = 'standard'
      }

      property 'destinationJndiTypeCOUWMQAS', {

        // Custom properties
        formType = 'standard'
      }

      property 'jndiNameCOUWMQAS', {

        // Custom properties
        formType = 'standard'
      }

      property 'specAdministartiveDescriptionCOUWMQAS', {

        // Custom properties
        formType = 'standard'
      }

      property 'specAdministrativeNameCOUWMQAS', {

        // Custom properties
        formType = 'standard'
      }

      property 'specScopeCOUWMQAS', {

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
