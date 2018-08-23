def testProjectName = args.projectName
def wasResourceName = args.wasResourceName
project testProjectName

procedure 'CreateOrUpdateJMSTopic', {
  description = ''
  jobNameTemplate = ''
  projectName = testProjectName
  wasResourceName = ''
  timeLimit = ''
  timeLimitUnits = 'minutes'
  workspaceName = ''

  formalParameter 'additionalOptionsCOUJMST', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'confignameCOUJMST', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'jndiNameCOUJMST', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'messagingSystemTypeCOUJMST', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'topicAdministrativeDescriptionCOUJMST', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'topicAdministrativeNameCOUJMST', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'topicNameCOUJMST', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'topicScopeCOUJMST', defaultValue: '', {
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

  step 'CreateOrUpdateJMSTopic', {
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
    subprocedure = 'CreateOrUpdateJMSTopic'
    subproject = '/plugins/EC-WebSphere/project'
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workingDirectory = null
    workspaceName = '$[wasResourceName]'
    actualParameter 'additionalOptions', '$[additionalOptionsCOUJMST]'
    actualParameter 'configname', '$[confignameCOUJMST]'
    actualParameter 'jndiName', '$[jndiNameCOUJMST]'
    actualParameter 'messagingSystemType', '$[messagingSystemTypeCOUJMST]'
    actualParameter 'topicAdministrativeDescription', '$[topicAdministrativeDescriptionCOUJMST]'
    actualParameter 'topicAdministrativeName', '$[topicAdministrativeNameCOUJMST]'
    actualParameter 'topicName', '$[topicNameCOUJMST]'
    actualParameter 'topicScope', '$[topicScopeCOUJMST]'
  }

  // Custom properties

  property 'ec_customEditorData', {

    // Custom properties

    property 'parameters', {

      // Custom properties

      property 'additionalOptionsCOUJMST', {

        // Custom properties
        formType = 'standard'
      }

      property 'confignameCOUJMST', {

        // Custom properties
        formType = 'standard'
      }

      property 'jndiNameCOUJMST', {

        // Custom properties
        formType = 'standard'
      }

      property 'messagingSystemTypeCOUJMST', {

        // Custom properties
        formType = 'standard'
      }

      property 'topicAdministrativeDescriptionCOUJMST', {

        // Custom properties
        formType = 'standard'
      }

      property 'topicAdministrativeNameCOUJMST', {

        // Custom properties
        formType = 'standard'
      }

      property 'topicNameCOUJMST', {

        // Custom properties
        formType = 'standard'
      }

      property 'topicScopeCOUJMST', {

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
