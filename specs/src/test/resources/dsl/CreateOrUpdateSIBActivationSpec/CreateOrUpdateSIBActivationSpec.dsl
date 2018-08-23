def testProjectName = args.projectName
def wasResourceName = args.wasResourceName
project testProjectName

procedure 'CreateorUpdateSIBActivationSpec', {
    description = ''
  jobNameTemplate = ''
  projectName = testProjectName
  resourceName = ''
  timeLimit = ''
  timeLimitUnits = 'minutes'
  workspaceName = ''

  formalParameter 'additionalOptionsCOUSIBAS', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'confignameCOUSIBAS', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'destinationJndiNameCOUSIBAS', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'destinationTypeCOUSIBAS', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'jndiNameCOUSIBAS', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'messageSelectorCOUSIBAS', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'specAdministrativeDescriptionCOUSIBAS', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'specAdministrativeNameCOUSIBAS', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'specScopeCOUSIBAS', defaultValue: '', {
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

  step 'CreateorUpdateSIBActivationSpec', {
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
    subprocedure = 'CreateOrUpdateSIBJMSActivationSpec'
    subproject = '/plugins/EC-WebSphere/project'
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workingDirectory = null
    workspaceName = '$[wasResourceName]'
    actualParameter 'additionalOptions', '$[additionalOptionsCOUSIBAS]'
    actualParameter 'configname', '$[confignameCOUSIBAS]'
    actualParameter 'destinationJndiName', '$[destinationJndiNameCOUSIBAS]'
    actualParameter 'destinationType', '$[destinationTypeCOUSIBAS]'
    actualParameter 'jndiName', '$[jndiNameCOUSIBAS]'
    actualParameter 'messageSelector', '$[messageSelectorCOUSIBAS]'
    actualParameter 'specAdministrativeDescription', '$[specAdministrativeDescriptionCOUSIBAS]'
    actualParameter 'specAdministrativeName', '$[specAdministrativeNameCOUSIBAS]'
    actualParameter 'specScope', '$[specScopeCOUSIBAS]'
  }

  // Custom properties

  property 'ec_customEditorData', {

    // Custom properties

    property 'parameters', {

      // Custom properties

      property 'additionalOptionsCOUSIBAS', {

        // Custom properties
        formType = 'standard'
      }

      property 'confignameCOUSIBAS', {

        // Custom properties
        formType = 'standard'
      }

      property 'destinationJndiNameCOUSIBAS', {

        // Custom properties
        formType = 'standard'
      }

      property 'destinationTypeCOUSIBAS', {

        // Custom properties
        formType = 'standard'
      }

      property 'jndiNameCOUSIBAS', {

        // Custom properties
        formType = 'standard'
      }

      property 'messageSelectorCOUSIBAS', {

        // Custom properties
        formType = 'standard'
      }

      property 'specAdministrativeDescriptionCOUSIBAS', {

        // Custom properties
        formType = 'standard'
      }

      property 'specAdministrativeNameCOUSIBAS', {

        // Custom properties
        formType = 'standard'
      }

      property 'specScopeCOUSIBAS', {

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
