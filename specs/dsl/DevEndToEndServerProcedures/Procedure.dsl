def testProjectName = args.projectName

procedure 'DevEndToEndServerProcedures', {
  description = ''
  jobNameTemplate = ''
  projectName = testProjectName
  resourceName = '$[wasResourceName]'
  timeLimit = ''
  timeLimitUnits = 'minutes'
  workspaceName = ''

  formalParameter 'wasArchivePath', defaultValue: '', {
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

  formalParameter 'wasResourceName', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'wasSourceNodeName', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'wasSourceServerName', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'wasTargetNodeName', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'wasTargetServerName', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'wasTemplateName', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  step 'CreateApplicationServerTemplate', {
    description = ''
    alwaysRun = '0'
    broadcast = '0'
    command = null
    condition = ''
    errorHandling = 'abortProcedure'
    exclusiveMode = 'none'
    logFileName = null
    parallel = '0'
    postProcessor = null
    precondition = ''
    projectName = testProjectName
    releaseMode = 'none'
    resourceName = ''
    shell = null
    subprocedure = 'CreateApplicationServerTemplate'
    subproject = '/plugins/EC-WebSphere/project'
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workingDirectory = null
    workspaceName = ''
    actualParameter 'configname', '$[wasConfigName]'
    actualParameter 'wasAppServerName', '$[wasSourceServerName]'
    actualParameter 'wasNodeName', '$[wasSourceNodeName]'
    actualParameter 'wasSyncNodes', '1'
    actualParameter 'wasTemplateDescription', ''
    actualParameter 'wasTemplateLocation', ''
    actualParameter 'wasTemplateName', '$[wasTemplateName]'
  }

  step 'ExportApplicationServer', {
    description = ''
    alwaysRun = '0'
    broadcast = '0'
    command = null
    condition = ''
    errorHandling = 'abortProcedure'
    exclusiveMode = 'none'
    logFileName = null
    parallel = '0'
    postProcessor = null
    precondition = ''
    projectName = testProjectName
    releaseMode = 'none'
    resourceName = ''
    shell = null
    subprocedure = 'ExportApplicationServer'
    subproject = '/plugins/EC-WebSphere/project'
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workingDirectory = null
    workspaceName = ''
    actualParameter 'configname', '$[wasConfigName]'
    actualParameter 'wasAppServerName', '$[wasSourceServerName]'
    actualParameter 'wasArchivePath', '$[wasArchivePath]'
    actualParameter 'wasNodeName', '$[wasSourceNodeName]'
  }

  step 'ImportApplicationServer', {
    description = ''
    alwaysRun = '0'
    broadcast = '0'
    command = null
    condition = ''
    errorHandling = 'abortProcedure'
    exclusiveMode = 'none'
    logFileName = null
    parallel = '0'
    postProcessor = null
    precondition = ''
    projectName = testProjectName
    releaseMode = 'none'
    resourceName = ''
    shell = null
    subprocedure = 'ImportApplicationServer'
    subproject = '/plugins/EC-WebSphere/project'
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workingDirectory = null
    workspaceName = ''
    actualParameter 'configname', '$[wasConfigName]'
    actualParameter 'wasAppServerName', '$[wasTargetServerName]'
    actualParameter 'wasAppServerNameInArchive', ''
    actualParameter 'wasArchivePath', '$[wasArchivePath]'
    actualParameter 'wasCoreGroup', ''
    actualParameter 'wasNodeName', '$[wasTargetNodeName]'
    actualParameter 'wasNodeNameInArchive', ''
    actualParameter 'wasSyncNodes', '1'
  }

  step 'StartApplicationServers', {
    description = ''
    alwaysRun = '0'
    broadcast = '0'
    command = null
    condition = ''
    errorHandling = 'abortProcedure'
    exclusiveMode = 'none'
    logFileName = null
    parallel = '0'
    postProcessor = null
    precondition = ''
    projectName = testProjectName
    releaseMode = 'none'
    resourceName = ''
    shell = null
    subprocedure = 'StartApplicationServers'
    subproject = '/plugins/EC-WebSphere/project'
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workingDirectory = null
    workspaceName = ''
    actualParameter 'configname', '$[wasConfigName]'
    actualParameter 'wasServersList', '$[wasTargetNodeName]:$[wasTargetServerName]'
    actualParameter 'wasWaitTime', '300'
  }

  step 'StopApplicationServers', {
    description = ''
    alwaysRun = '0'
    broadcast = '0'
    command = null
    condition = ''
    errorHandling = 'abortProcedure'
    exclusiveMode = 'none'
    logFileName = null
    parallel = '0'
    postProcessor = null
    precondition = ''
    projectName = testProjectName
    releaseMode = 'none'
    resourceName = ''
    shell = null
    subprocedure = 'StopApplicationServers'
    subproject = '/plugins/EC-WebSphere/project'
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workingDirectory = null
    workspaceName = ''
    actualParameter 'configname', '$[wasConfigName]'
    actualParameter 'wasServersList', '$[wasTargetNodeName]:$[wasTargetServerName]'
    actualParameter 'wasWaitTime', '300'
  }

  step 'DeleteApplicationServer', {
    description = ''
    alwaysRun = '0'
    broadcast = '0'
    command = null
    condition = ''
    errorHandling = 'abortProcedure'
    exclusiveMode = 'none'
    logFileName = null
    parallel = '0'
    postProcessor = null
    precondition = ''
    projectName = testProjectName
    releaseMode = 'none'
    resourceName = ''
    shell = null
    subprocedure = 'DeleteApplicationServer'
    subproject = '/plugins/EC-WebSphere/project'
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workingDirectory = null
    workspaceName = ''
    actualParameter 'configname', '$[wasConfigName]'
    actualParameter 'wasAppServerName', '$[wasTargetServerName]'
    actualParameter 'wasNodeName', '$[wasTargetNodeName]'
    actualParameter 'wasSyncNodes', '1'
  }

  step 'CreateApplicationServer', {
    description = ''
    alwaysRun = '0'
    broadcast = '0'
    command = null
    condition = ''
    errorHandling = 'abortProcedure'
    exclusiveMode = 'none'
    logFileName = null
    parallel = '0'
    postProcessor = null
    precondition = ''
    projectName = testProjectName
    releaseMode = 'none'
    resourceName = ''
    shell = null
    subprocedure = 'CreateApplicationServer'
    subproject = '/plugins/EC-WebSphere/project'
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workingDirectory = null
    workspaceName = ''
    actualParameter 'configname', '$[wasConfigName]'
    actualParameter 'wasAppServerName', '$[wasTargetServerName]'
    actualParameter 'wasGenUniquePorts', '1'
    actualParameter 'wasNodeName', '$[wasTargetNodeName]'
    actualParameter 'wasSourceServerName', ''
    actualParameter 'wasSourceType', 'template'
    actualParameter 'wasSyncNodes', '1'
    actualParameter 'wasTemplateLocation', ''
    actualParameter 'wasTemplateName', '$[wasTemplateName]'
  }

  step 'DeleteApplicationServer2', {
    description = ''
    alwaysRun = '0'
    broadcast = '0'
    command = null
    condition = ''
    errorHandling = 'abortProcedure'
    exclusiveMode = 'none'
    logFileName = null
    parallel = '0'
    postProcessor = null
    precondition = ''
    projectName = testProjectName
    releaseMode = 'none'
    resourceName = ''
    shell = null
    subprocedure = 'DeleteApplicationServer'
    subproject = '/plugins/EC-WebSphere/project'
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workingDirectory = null
    workspaceName = ''
    actualParameter 'configname', '$[wasConfigName]'
    actualParameter 'wasAppServerName', '$[wasTargetServerName]'
    actualParameter 'wasNodeName', '$[wasTargetNodeName]'
    actualParameter 'wasSyncNodes', '1'
  }

  step 'DeleteApplicationServrTemplate', {
    description = ''
    alwaysRun = '0'
    broadcast = '0'
    command = null
    condition = ''
    errorHandling = 'abortProcedure'
    exclusiveMode = 'none'
    logFileName = null
    parallel = '0'
    postProcessor = null
    precondition = ''
    projectName = testProjectName
    releaseMode = 'none'
    resourceName = ''
    shell = null
    subprocedure = 'DeleteApplicationServerTemplate'
    subproject = '/plugins/EC-WebSphere/project'
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workingDirectory = null
    workspaceName = ''
    actualParameter 'configname', '$[wasConfigName]'
    actualParameter 'wasSyncNodes', '1'
    actualParameter 'wasTemplateName', '$[wasTemplateName]'
  }

  // Custom properties

  property 'ec_customEditorData', {

    // Custom properties

    property 'parameters', {

      // Custom properties

      property 'wasArchivePath', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasConfigName', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasResourceName', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasSourceNodeName', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasSourceServerName', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasTargetNodeName', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasTargetServerName', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasTemplateName', {

        // Custom properties
        formType = 'standard'
      }
    }
  }
}
