def testProjectName = args.projectName
def wasResourceName = args.wasResourceName
project testProjectName

procedure 'CreateOrUpdateJMSQueue', {
  description = ''
  jobNameTemplate = ''
  projectName = testProjectName
  wasResourceName = ''
  timeLimit = ''
  timeLimitUnits = 'minutes'
  workspaceName = ''

  formalParameter 'additionalOptionsCOUJMSQ', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'confignameCOUJMSQ', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'jndiNameCOUJMSQ', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'messagingSystemTypeCOUJMSQ', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'queueAdministrativeDescriptionCOUJMSQ', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'queueAdministrativeNameCOUJMSQ', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'queueManagerNameCOUJMSQ', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'queueNameCOUJMSQ', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'queueScopeCOUJMSQ', defaultValue: '', {
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

  step 'CreateOrUpdateJMSQueue', {
    description = 'COUJMSQ'
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
    subprocedure = 'CreateOrUpdateJMSQueue'
    subproject = '/plugins/EC-WebSphere/project'
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workingDirectory = null
    workspaceName = '$[wasResourceName]'
    actualParameter 'additionalOptions', '$[additionalOptionsCOUJMSQ]'
    actualParameter 'configname', '$[confignameCOUJMSQ]'
    actualParameter 'jndiName', '$[jndiNameCOUJMSQ]'
    actualParameter 'messagingSystemType', '$[messagingSystemTypeCOUJMSQ]'
    actualParameter 'queueAdministrativeDescription', '[queueAdministrativeDescriptionCOUJMSQ]'
    actualParameter 'queueAdministrativeName', '$[queueAdministrativeNameCOUJMSQ]'
    actualParameter 'queueManagerName', '$[queueManagerNameCOUJMSQ]'
    actualParameter 'queueName', '$[queueNameCOUJMSQ]'
    actualParameter 'queueScope', '$[queueScopeCOUJMSQ]'
  }

  // Custom properties

  property 'ec_customEditorData', {

    // Custom properties

    property 'parameters', {

      // Custom properties

      property 'additionalOptionsCOUJMSQ', {

        // Custom properties
        formType = 'standard'
      }

      property 'confignameCOUJMSQ', {

        // Custom properties
        formType = 'standard'
      }

      property 'jndiNameCOUJMSQ', {

        // Custom properties
        formType = 'standard'
      }

      property 'messagingSystemTypeCOUJMSQ', {

        // Custom properties
        formType = 'standard'
      }

      property 'queueAdministrativeDescriptionCOUJMSQ', {

        // Custom properties
        formType = 'standard'
      }

      property 'queueAdministrativeNameCOUJMSQ', {

        // Custom properties
        formType = 'standard'
      }

      property 'queueManagerNameCOUJMSQ', {

        // Custom properties
        formType = 'standard'
      }

      property 'queueNameCOUJMSQ', {

        // Custom properties
        formType = 'standard'
      }

      property 'queueScopeCOUJMSQ', {

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
