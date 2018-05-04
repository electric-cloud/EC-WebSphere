def testProjectName = args.projectName
def wasResourceName = args.wasResourceName

procedure 'CreateJMSProvider', {
  description = ''
  jobNameTemplate = ''
  projectName = testProjectName
  resourceName = ''
  timeLimit = ''
  timeLimitUnits = 'minutes'
  workspaceName = ''

  formalParameter 'wasConfigName', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'wasInitialConnectionFactory', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'wasJMSProviderURL', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'wasProviderName', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'wasProviderScope', defaultValue: '', {
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

  step 'CreateJMSProvider', {
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
    subprocedure = 'CreateJMSProvider'
    subproject = '/plugins/EC-WebSphere/project'
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workingDirectory = null
    workspaceName = ''
    actualParameter 'classpath', ''
    actualParameter 'configName', '$[wasConfigName]'
    actualParameter 'description', ''
    actualParameter 'extContextFactory', '$[wasInitialConnectionFactory]'
    actualParameter 'extProviderURL', '$[wasJMSProviderURL]'
    actualParameter 'isolatedClassLoader', '0'
    actualParameter 'jmsProvider', '$[wasProviderName]'
    actualParameter 'nativepath', ''
    actualParameter 'propertySet', ''
    actualParameter 'providerType', ''
    actualParameter 'scope', '$[wasProviderScope]'
    actualParameter 'supportsASF', '0'
    actualParameter 'wsadminAbsPath', ''
  }

  // Custom properties

  property 'ec_customEditorData', {

    // Custom properties

    property 'parameters', {

      // Custom properties

      property 'wasConfigName', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasInitialConnectionFactory', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasJMSProviderURL', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasProviderName', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasProviderScope', {

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
