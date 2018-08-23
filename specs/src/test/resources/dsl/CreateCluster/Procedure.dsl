def testProjectName = args.projectName

procedure 'CreateCluster', {
  description = ''
  jobNameTemplate = ''
  projectName = testProjectName
  resourceName = '$[wasResourceName]'
  timeLimit = ''
  timeLimitUnits = 'minutes'
  workspaceName = ''

  formalParameter 'wasAddClusterMembers', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'wasClusterMembersList', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

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

  formalParameter 'wasCreateFirstClusterMember', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'wasFirstClusterMemberCreationPolicy', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'wasFirstClusterMemberName', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'wasFirstClusterMemberNode', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'wasFirstClusterMemberTemplateName', defaultValue: '', {
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

  formalParameter 'wasServerResourcesPromotionPolicy', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'wasSourceServerName', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
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
    actualParameter 'wasAddClusterMembers', '$[wasAddClusterMembers]'
    actualParameter 'wasClusterMembersGenUniquePorts', '1'
    actualParameter 'wasClusterMembersList', '$[wasClusterMembersList]'
    actualParameter 'wasClusterMemberWeight', ''
    actualParameter 'wasClusterName', '$[wasClusterName]'
    actualParameter 'wasCreateFirstClusterMember', '$[wasCreateFirstClusterMember]'
    actualParameter 'wasFirstClusterMemberCreationPolicy', '$[wasFirstClusterMemberCreationPolicy]'
    actualParameter 'wasFirstClusterMemberGenUniquePorts', '1'
    actualParameter 'wasFirstClusterMemberName', '$[wasFirstClusterMemberName]'
    actualParameter 'wasFirstClusterMemberNode', '$[wasFirstClusterMemberNode]'
    actualParameter 'wasFirstClusterMemberTemplateName', '$[wasFirstClusterMemberTemplateName]'
    actualParameter 'wasFirstClusterMemberWeight', ''
    actualParameter 'wasPreferLocal', '1'
    actualParameter 'wasServerResourcesPromotionPolicy', '$[wasServerResourcesPromotionPolicy]'
    actualParameter 'wasSourceServerName', '$[wasSourceServerName]'
    actualParameter 'wasSyncNodes', '1'
  }

  // Custom properties

  property 'ec_customEditorData', {

    // Custom properties

    property 'parameters', {

      // Custom properties

      property 'wasAddClusterMembers', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasClusterMembersList', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasClusterName', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasConfigName', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasCreateFirstClusterMember', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasFirstClusterMemberCreationPolicy', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasFirstClusterMemberName', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasFirstClusterMemberNode', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasFirstClusterMemberTemplateName', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasResourceName', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasServerResourcesPromotionPolicy', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasSourceServerName', {

        // Custom properties
        formType = 'standard'
      }
    }
  }
}
