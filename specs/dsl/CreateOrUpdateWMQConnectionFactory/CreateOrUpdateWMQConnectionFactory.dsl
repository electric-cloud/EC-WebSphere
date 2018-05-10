
procedure 'CreateorUpdateWMQConnectionFactory', {
  description = ''
  jobNameTemplate = ''
  projectName = 'WebSphere-Test-JMS-Application'
  resourceName = ''
  timeLimit = ''
  timeLimitUnits = 'minutes'
  workspaceName = ''

  formalParameter 'additionalOptionsCOUWMQCF', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'clientChannelDefinitionQueueManagerCOUWMQCF', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'clientChannelDefinitionUrlCOUWMQCF', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'confignameCOUWMQCF', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'factoryAdministrativeDescriptionCOUWMQCF', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'factoryAdministrativeNameCOUWMQCF', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'factoryScopeCOUWMQCF', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'factoryTypeCOUWMQCF', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'jndiNameCOUWMQCF', defaultValue: '', {
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

  step 'CreateorUpdateWMQConnectionFactory', {
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
    projectName = 'WebSphere-Test-JMS-Application'
    releaseMode = 'none'
    resourceName = '$[wasResourceName]'
    shell = null
    subprocedure = 'CreateOrUpdateWMQJMSConnectionFactory'
    subproject = '/plugins/EC-WebSphere/project'
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workingDirectory = null
    workspaceName = '$[wasResourceName]'
    actualParameter 'additionalOptions', '$[additionalOptionsCOUWMQCF]'
    actualParameter 'clientChannelDefinitionQueueManager', '$[clientChannelDefinitionQueueManagerCOUWMQCF]'
    actualParameter 'clientChannelDefinitionUrl', '$[clientChannelDefinitionUrlCOUWMQCF]'
    actualParameter 'configname', '$[confignameCOUWMQCF]'
    actualParameter 'factoryAdministrativeDescription', '$[factoryAdministrativeDescriptionCOUWMQCF]'
    actualParameter 'factoryAdministrativeName', '$[factoryAdministrativeNameCOUWMQCF]'
    actualParameter 'factoryScope', '$[factoryScopeCOUWMQCF]'
    actualParameter 'factoryType', '$[factoryTypeCOUWMQCF]'
    actualParameter 'jndiName', '$[jndiNameCOUWMQCF]'
  }

  // Custom properties

  property 'ec_customEditorData', {

    // Custom properties

    property 'parameters', {

      // Custom properties

      property 'additionalOptionsCOUWMQCF', {

        // Custom properties
        formType = 'standard'
      }

      property 'clientChannelDefinitionQueueManagerCOUWMQCF', {

        // Custom properties
        formType = 'standard'
      }

      property 'clientChannelDefinitionUrlCOUWMQCF', {

        // Custom properties
        formType = 'standard'
      }

      property 'confignameCOUWMQCF', {

        // Custom properties
        formType = 'standard'
      }

      property 'factoryAdministrativeDescriptionCOUWMQCF', {

        // Custom properties
        formType = 'standard'
      }

      property 'factoryAdministrativeNameCOUWMQCF', {

        // Custom properties
        formType = 'standard'
      }

      property 'factoryScopeCOUWMQCF', {

        // Custom properties
        formType = 'standard'
      }

      property 'factoryTypeCOUWMQCF', {

        // Custom properties
        formType = 'standard'
      }

      property 'jndiNameCOUWMQCF', {

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
