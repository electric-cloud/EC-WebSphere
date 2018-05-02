def testProjectName = args.projectName
def wasResourceName = args.wasResourceName
project testProjectName

procedure 'CreateOrUpdateSIBJMSActivationSpec', {
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

  formalParameter 'wasASDestinationJNDIName', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
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

  formalParameter 'wasConfigName', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'wasDestinationType', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'wasMessageSelector', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
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

  step 'CreateOrUpdateSIBJMSActivationSpec', {
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
    actualParameter 'additionalOptions', '$[wasAdditionalOptions]'
    actualParameter 'configname', '$[wasConfigName]'
    actualParameter 'destinationJndiName', '$[wasASDestinationJNDIName]'
    actualParameter 'destinationType', '$[wasDestinationType]'
    actualParameter 'jndiName', '$[wasASJNDIName]'
    actualParameter 'messageSelector', '$[wasMessageSelector]'
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

      property 'wasASDestinationJNDIName', {

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

      property 'wasConfigName', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasDestinationType', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasMessageSelector', {

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
