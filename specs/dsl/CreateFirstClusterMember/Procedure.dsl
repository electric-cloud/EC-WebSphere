def testProjectName = args.projectName

procedure 'CreateFirstClusterMember', {
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

  formalParameter 'wasFirstClusterMemberTemplateName', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'wasFirstMemberCreationPolicy', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'wasFirstMemberName', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'wasFirstMemberNode', defaultValue: '', {
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

  formalParameter 'wasServerResourcesPromotionPolicy', defaultValue: '', {
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
    required = '0'
    type = 'entry'
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
    actualParameter 'wasFirstClusterMemberCreationPolicy', '$[wasFirstMemberCreationPolicy]'
    actualParameter 'wasFirstClusterMemberGenUniquePorts', '1'
    actualParameter 'wasFirstClusterMemberName', '$[wasFirstMemberName]'
    actualParameter 'wasFirstClusterMemberNode', '$[wasFirstMemberNode]'
    actualParameter 'wasFirstClusterMemberTemplateName', '$[wasFirstClusterMemberTemplateName]'
    actualParameter 'wasFirstClusterMemberWeight', ''
    actualParameter 'wasServerResourcesPromotionPolicy', '$[wasServerResourcesPromotionPolicy]'
    actualParameter 'wasSourceServerName', '$[wasSourceServerName]'
    actualParameter 'wasSyncNodes', '0'
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

      property 'wasFirstClusterMemberTemplateName', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasFirstMemberCreationPolicy', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasFirstMemberName', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasFirstMemberNode', {

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
