
procedure 'DeleteActivationSpec', {
  description = ''
  jobNameTemplate = ''
  projectName = 'WebSphere-Test-JMS-Application'
  resourceName = ''
  timeLimit = ''
  timeLimitUnits = 'minutes'
  workspaceName = ''

  formalParameter 'activationSpecAdministrativeNameDAS', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'activationSpecScopeDAS', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'confignameDAS', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'messagingSystemTypeDAS', defaultValue: '', {
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

  step 'DeleteActivationSpec', {
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
    subprocedure = 'DeleteJMSActivationSpec'
    subproject = '/plugins/EC-WebSphere/project'
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workingDirectory = null
    workspaceName = '$[wasResourceName]'
    actualParameter 'activationSpecAdministrativeName', '$[activationSpecAdministrativeNameDAS]'
    actualParameter 'activationSpecScope', '$[activationSpecScopeDAS]'
    actualParameter 'configname', '$[confignameDAS]'
    actualParameter 'messagingSystemType', '$[messagingSystemTypeDAS]'
  }

  // Custom properties

  property 'ec_customEditorData', {

    // Custom properties

    property 'parameters', {

      // Custom properties

      property 'activationSpecAdministrativeNameDAS', {

        // Custom properties
        formType = 'standard'
      }

      property 'activationSpecScopeDAS', {

        // Custom properties
        formType = 'standard'
      }

      property 'confignameDAS', {

        // Custom properties
        formType = 'standard'
      }

      property 'messagingSystemTypeDAS', {

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
