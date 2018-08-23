def testProjectName = args.projectName
def wasResourceName = args.wasResourceName
project testProjectName

procedure 'CreateorUpdateSIBConnectionFactory', {
  description = ''
  jobNameTemplate = ''
  projectName = testProjectName
  resourceName = ''
  timeLimit = ''
  timeLimitUnits = 'minutes'
  workspaceName = ''

  formalParameter 'additionalOptionsCOUSIBCF', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'busNameCOUSIBCF', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'confignameCOUSIBCF', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'factoryAdministrativeDescriptionCOUSIBCF', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'factoryAdministrativeNameCOUSIBCF', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'factoryScopeCOUSIBCF', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'factoryTypeCOUSIBCF', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'jndiNameCOUSIBCF', defaultValue: '', {
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

  step 'CreateorUpdateSIBConnectionFactory', {
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
    subprocedure = 'CreateOrUpdateSIBJMSConnectionFactory'
    subproject = '/plugins/EC-WebSphere/project'
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workingDirectory = null
    workspaceName = '$[wasResourceName]'
    actualParameter 'additionalOptions', '$[additionalOptionsCOUSIBCF]'
    actualParameter 'busName', '$[busNameCOUSIBCF]'
    actualParameter 'configname', '$[confignameCOUSIBCF]'
    actualParameter 'factoryAdministrativeDescription', '$[factoryAdministrativeDescriptionCOUSIBCF]'
    actualParameter 'factoryAdministrativeName', '$[factoryAdministrativeNameCOUSIBCF]'
    actualParameter 'factoryScope', '$[factoryScopeCOUSIBCF]'
    actualParameter 'factoryType', '$[factoryTypeCOUSIBCF]'
    actualParameter 'jndiName', '$[jndiNameCOUSIBCF]'
  }

  // Custom properties

  property 'ec_customEditorData', {

    // Custom properties

    property 'parameters', {

      // Custom properties

      property 'additionalOptionsCOUSIBCF', {

        // Custom properties
        formType = 'standard'
      }

      property 'busNameCOUSIBCF', {

        // Custom properties
        formType = 'standard'
      }

      property 'confignameCOUSIBCF', {

        // Custom properties
        formType = 'standard'
      }

      property 'factoryAdministrativeDescriptionCOUSIBCF', {

        // Custom properties
        formType = 'standard'
      }

      property 'factoryAdministrativeNameCOUSIBCF', {

        // Custom properties
        formType = 'standard'
      }

      property 'factoryScopeCOUSIBCF', {

        // Custom properties
        formType = 'standard'
      }

      property 'factoryTypeCOUSIBCF', {

        // Custom properties
        formType = 'standard'
      }

      property 'jndiNameCOUSIBCF', {

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
