def testProjectName = args.projectName

procedure 'DevEndToEndClusterProcedures', {
  description = ''
  jobNameTemplate = ''
  projectName = testProjectName
  resourceName = '$[wasResourceName]'
  timeLimit = ''
  timeLimitUnits = 'minutes'
  workspaceName = ''

  formalParameter 'wasClusterName', defaultValue: '', {
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

  formalParameter 'wasSourceNodeNameConvert', defaultValue: '', {
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

  formalParameter 'wasSourceServerNameConvert', defaultValue: '', {
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

  step 'CreateApplicationServer', {
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
    resourceName = ''
    shell = null
    subprocedure = 'CreateApplicationServer'
    subproject = '/plugins/EC-WebSphere/project'
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workingDirectory = null
    workspaceName = ''
    actualParameter 'configname', '$[wasConfigName]'
    actualParameter 'wasAppServerName', '$[wasSourceServerNameConvert]'
    actualParameter 'wasGenUniquePorts', '1'
    actualParameter 'wasNodeName', '$[wasSourceNodeNameConvert]'
    actualParameter 'wasSourceServerName', ''
    actualParameter 'wasSourceType', ''
    actualParameter 'wasSyncNodes', '1'
    actualParameter 'wasTemplateLocation', ''
    actualParameter 'wasTemplateName', ''
  }

  step 'CreateCluster', {
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
    resourceName = ''
    shell = null
    subprocedure = 'CreateCluster'
    subproject = '/plugins/EC-WebSphere/project'
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workingDirectory = null
    workspaceName = ''
    actualParameter 'configname', '$[wasConfigName]'
    actualParameter 'wasAddClusterMembers', '1'
    actualParameter 'wasClusterMembersGenUniquePorts', '1'
    actualParameter 'wasClusterMembersList', '''$[wasTargetNodeName]:server1c, $[wasTargetNodeName]:server2c,
$[wasTargetNodeName]:server3c'''
    actualParameter 'wasClusterMemberWeight', ''
    actualParameter 'wasClusterName', '$[wasClusterName]'
    actualParameter 'wasCreateFirstClusterMember', '1'
    actualParameter 'wasFirstClusterMemberCreationPolicy', 'convert'
    actualParameter 'wasFirstClusterMemberGenUniquePorts', '1'
    actualParameter 'wasFirstClusterMemberName', ''
    actualParameter 'wasFirstClusterMemberNode', ''
    actualParameter 'wasFirstClusterMemberTemplateName', ''
    actualParameter 'wasFirstClusterMemberWeight', ''
    actualParameter 'wasPreferLocal', '1'
    actualParameter 'wasServerResourcesPromotionPolicy', 'cluster'
    actualParameter 'wasSourceServerName', '$[wasSourceNodeNameConvert]:$[wasSourceServerNameConvert]'
    actualParameter 'wasSyncNodes', '1'
  }

  step 'DeleteCluster', {
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
    resourceName = ''
    shell = null
    subprocedure = 'DeleteCluster'
    subproject = '/plugins/EC-WebSphere/project'
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workingDirectory = null
    workspaceName = ''
    actualParameter 'configname', '$[wasConfigName]'
    actualParameter 'wasClusterName', '$[wasClusterName]'
    actualParameter 'wasSyncNodes', '1'
  }

  step 'CreateCluster2', {
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
    resourceName = ''
    shell = null
    subprocedure = 'CreateCluster'
    subproject = '/plugins/EC-WebSphere/project'
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workingDirectory = null
    workspaceName = ''
    actualParameter 'configname', '$[wasConfigName]'
    actualParameter 'wasAddClusterMembers', '0'
    actualParameter 'wasClusterMembersGenUniquePorts', '1'
    actualParameter 'wasClusterMembersList', ''
    actualParameter 'wasClusterMemberWeight', ''
    actualParameter 'wasClusterName', '$[wasClusterName]'
    actualParameter 'wasCreateFirstClusterMember', '1'
    actualParameter 'wasFirstClusterMemberCreationPolicy', 'template'
    actualParameter 'wasFirstClusterMemberGenUniquePorts', '1'
    actualParameter 'wasFirstClusterMemberName', '$[wasTargetServerName]'
    actualParameter 'wasFirstClusterMemberNode', '$[wasTargetNodeName]'
    actualParameter 'wasFirstClusterMemberTemplateName', '$[wasTemplateName]'
    actualParameter 'wasFirstClusterMemberWeight', ''
    actualParameter 'wasPreferLocal', '1'
    actualParameter 'wasServerResourcesPromotionPolicy', 'cluster'
    actualParameter 'wasSourceServerName', ''
    actualParameter 'wasSyncNodes', '1'
  }

  step 'CreateClusterMembers', {
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
    resourceName = ''
    shell = null
    subprocedure = 'CreateClusterMembers'
    subproject = '/plugins/EC-WebSphere/project'
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workingDirectory = null
    workspaceName = ''
    actualParameter 'configname', '$[wasConfigName]'
    actualParameter 'wasClusterMembersGenUniquePorts', '1'
    actualParameter 'wasClusterMembersList', '''$[wasTargetNodeName]:server1c, $[wasTargetNodeName]:server2c,
$[wasTargetNodeName]:server3c'''
    actualParameter 'wasClusterMemberWeight', ''
    actualParameter 'wasClusterName', '$[wasClusterName]'
    actualParameter 'wasSyncNodes', '1'
  }

  step 'DeleteCluster2', {
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
    resourceName = ''
    shell = null
    subprocedure = 'DeleteCluster'
    subproject = '/plugins/EC-WebSphere/project'
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workingDirectory = null
    workspaceName = ''
    actualParameter 'configname', '$[wasConfigName]'
    actualParameter 'wasClusterName', '$[wasClusterName]'
    actualParameter 'wasSyncNodes', '1'
  }

  step 'CreateCluster3', {
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
    resourceName = ''
    shell = null
    subprocedure = 'CreateCluster'
    subproject = '/plugins/EC-WebSphere/project'
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workingDirectory = null
    workspaceName = ''
    actualParameter 'configname', '$[wasConfigName]'
    actualParameter 'wasAddClusterMembers', '0'
    actualParameter 'wasClusterMembersGenUniquePorts', '1'
    actualParameter 'wasClusterMembersList', ''
    actualParameter 'wasClusterMemberWeight', ''
    actualParameter 'wasClusterName', '$[wasClusterName]'
    actualParameter 'wasCreateFirstClusterMember', '1'
    actualParameter 'wasFirstClusterMemberCreationPolicy', 'existing'
    actualParameter 'wasFirstClusterMemberGenUniquePorts', '1'
    actualParameter 'wasFirstClusterMemberName', '$[wasTargetServerName]'
    actualParameter 'wasFirstClusterMemberNode', '$[wasTargetNodeName]'
    actualParameter 'wasFirstClusterMemberTemplateName', ''
    actualParameter 'wasFirstClusterMemberWeight', ''
    actualParameter 'wasPreferLocal', '1'
    actualParameter 'wasServerResourcesPromotionPolicy', 'cluster'
    actualParameter 'wasSourceServerName', '$[wasSourceNodeName]:$[wasSourceServerName]'
    actualParameter 'wasSyncNodes', '1'
  }

  step 'DeleteCluster3', {
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
    resourceName = ''
    shell = null
    subprocedure = 'DeleteCluster'
    subproject = '/plugins/EC-WebSphere/project'
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workingDirectory = null
    workspaceName = ''
    actualParameter 'configname', '$[wasConfigName]'
    actualParameter 'wasClusterName', '$[wasClusterName]'
    actualParameter 'wasSyncNodes', '1'
  }

  step 'CreateEmptyCluster', {
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
    resourceName = ''
    shell = null
    subprocedure = 'CreateCluster'
    subproject = '/plugins/EC-WebSphere/project'
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workingDirectory = null
    workspaceName = ''
    actualParameter 'configname', '$[wasConfigName]'
    actualParameter 'wasAddClusterMembers', '0'
    actualParameter 'wasClusterMembersGenUniquePorts', '1'
    actualParameter 'wasClusterMembersList', ''
    actualParameter 'wasClusterMemberWeight', ''
    actualParameter 'wasClusterName', '$[wasClusterName]'
    actualParameter 'wasCreateFirstClusterMember', '0'
    actualParameter 'wasFirstClusterMemberCreationPolicy', ''
    actualParameter 'wasFirstClusterMemberGenUniquePorts', '1'
    actualParameter 'wasFirstClusterMemberName', ''
    actualParameter 'wasFirstClusterMemberNode', ''
    actualParameter 'wasFirstClusterMemberTemplateName', ''
    actualParameter 'wasFirstClusterMemberWeight', ''
    actualParameter 'wasPreferLocal', '1'
    actualParameter 'wasServerResourcesPromotionPolicy', ''
    actualParameter 'wasSourceServerName', ''
    actualParameter 'wasSyncNodes', '1'
  }

  step 'CreateFirstClusterMember', {
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
    resourceName = ''
    shell = null
    subprocedure = 'CreateFirstClusterMember'
    subproject = '/plugins/EC-WebSphere/project'
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workingDirectory = null
    workspaceName = ''
    actualParameter 'configname', '$[wasConfigName]'
    actualParameter 'wasClusterName', '$[wasClusterName]'
    actualParameter 'wasFirstClusterMemberCreationPolicy', 'template'
    actualParameter 'wasFirstClusterMemberGenUniquePorts', '1'
    actualParameter 'wasFirstClusterMemberName', '$[wasSourceServerNameConvert]'
    actualParameter 'wasFirstClusterMemberNode', '$[wasSourceNodeNameConvert]'
    actualParameter 'wasFirstClusterMemberTemplateName', 'default'
    actualParameter 'wasFirstClusterMemberWeight', ''
    actualParameter 'wasServerResourcesPromotionPolicy', 'cluster'
    actualParameter 'wasSourceServerName', ''
    actualParameter 'wasSyncNodes', '1'
  }

  step 'CreateClusterMembers2', {
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
    resourceName = ''
    shell = null
    subprocedure = 'CreateClusterMembers'
    subproject = '/plugins/EC-WebSphere/project'
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workingDirectory = null
    workspaceName = ''
    actualParameter 'configname', '$[wasConfigName]'
    actualParameter 'wasClusterMembersGenUniquePorts', '1'
    actualParameter 'wasClusterMembersList', '''$[wasTargetNodeName]:server1c, $[wasTargetNodeName]:server2c,
$[wasTargetNodeName]:server3c'''
    actualParameter 'wasClusterMemberWeight', ''
    actualParameter 'wasClusterName', '$[wasClusterName]'
    actualParameter 'wasSyncNodes', '1'
  }

  step 'DeleteCluster4', {
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
    resourceName = ''
    shell = null
    subprocedure = 'DeleteCluster'
    subproject = '/plugins/EC-WebSphere/project'
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workingDirectory = null
    workspaceName = ''
    actualParameter 'configname', '$[wasConfigName]'
    actualParameter 'wasClusterName', '$[wasClusterName]'
    actualParameter 'wasSyncNodes', '1'
  }

  // Custom properties

  property 'ec_customEditorData', {

    // Custom properties

    property 'parameters', {

      // Custom properties

      property 'wasClusterName', {

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

      property 'wasSourceNodeNameConvert', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasSourceServerName', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasSourceServerNameConvert', {

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
