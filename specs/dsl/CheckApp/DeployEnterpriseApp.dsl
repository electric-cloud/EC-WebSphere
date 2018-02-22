def testProjectName = args.projectName
def wasResourceName = args.wasResourceName
project testProjectName

procedure 'DeployEnterpriseApp', {
  description = ''
  jobNameTemplate = ''
  projectName = testProjectName
  wasResourceName = ''
  timeLimit = ''
  timeLimitUnits = 'minutes'
  workspaceName = ''

  formalParameter 'additionalcommandsDEA', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'additionalDeployParamsDEA', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'appNameDEA', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'apppathDEA', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'autoResolveEJBRefDEA', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'binaryConfigDEA', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'blaNameDEA', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'classpathDEA', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'clientDeployModeDEA', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'clusterDEA', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'commandsDEA', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'configNameDEA', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'contextRootDEA', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'createMBeansDEA', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'customFilePermissionsDEA', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'deployBeansDEA', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'deployClientModDEA', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'deployWSDEA', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'distributeAppDEA', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'filePermissionsDEA', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'installDirDEA', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'javaparamsDEA', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'MapModulesToServersDEA', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'overrideClassReloadingDEA', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'precompileJSPDEA', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'processEmbConfigDEA', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'reloadIntervalDEA', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'serverListDEA', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'startAppDEA', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'syncActiveNodesDEA', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'validateRefsDEA', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'validateSchemaDEA', defaultValue: '', {
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

  formalParameter 'wsadminAbsPathDEA', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  step 'DeployEnterpriseApp', {
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
    subprocedure = 'DeployEnterpriseApp'
    subproject = '/plugins/EC-WebSphere/project'
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workingDirectory = null
    workspaceName = '$[wasResourceName]'
    actualParameter 'additionalcommands', '$[additionalcommandsDEA]'
    actualParameter 'additionalDeployParams', '$[additionalDeployParamsDEA]'
    actualParameter 'appName', '$[appNameDEA]'
    actualParameter 'apppath', '$[apppathDEA]'
    actualParameter 'autoResolveEJBRef', '$[autoResolveEJBRefDEA]'
    actualParameter 'binaryConfig', '$[binaryConfigDEA]'
    actualParameter 'blaName', '$[blaNameDEA]'
    actualParameter 'classpath', '$[classpathDEA]'
    actualParameter 'clientDeployMode', '$[clientDeployModeDEA]'
    actualParameter 'cluster', '$[clusterDEA]'
    actualParameter 'commands', '$[commandsDEA]'
    actualParameter 'configName', '$[configNameDEA]'
    actualParameter 'contextRoot', '$[contextRootDEA]'
    actualParameter 'createMBeans', '$[createMBeansDEA]'
    actualParameter 'customFilePermissions', '$[customFilePermissionsDEA]'
    actualParameter 'deployBeans', '$[deployBeansDEA]'
    actualParameter 'deployClientMod', '$[deployClientModDEA]'
    actualParameter 'deployWS', '$[deployWSDEA]'
    actualParameter 'distributeApp', '$[distributeAppDEA]'
    actualParameter 'filePermissions', '$[filePermissionsDEA]'
    actualParameter 'installDir', '$[installDirDEA]'
    actualParameter 'javaparams', '$[javaparamsDEA]'
    actualParameter 'MapModulesToServers', '$[MapModulesToServersDEA]'
    actualParameter 'overrideClassReloading', '$[overrideClassReloadingDEA]'
    actualParameter 'precompileJSP', '$[precompileJSPDEA]'
    actualParameter 'processEmbConfig', '$[processEmbConfigDEA]'
    actualParameter 'reloadInterval', '$[reloadIntervalDEA]'
    actualParameter 'serverList', '$[serverListDEA]'
    actualParameter 'startApp', '$[startAppDEA]'
    actualParameter 'syncActiveNodes', '$[syncActiveNodesDEA]'
    actualParameter 'validateRefs', '$[validateRefsDEA]'
    actualParameter 'validateSchema', '$[validateSchemaDEA]'
    actualParameter 'wsadminAbsPath', '$[wsadminAbsPathDEA]'
  }

  // Custom properties

  property 'ec_customEditorData', {

    // Custom properties

    property 'parameters', {

      // Custom properties

      property 'MapModulesToServersDEA', {

        // Custom properties
        formType = 'standard'
      }

      property 'additionalDeployParamsDEA', {

        // Custom properties
        formType = 'standard'
      }

      property 'additionalcommandsDEA', {

        // Custom properties
        formType = 'standard'
      }

      property 'appNameDEA', {

        // Custom properties
        formType = 'standard'
      }

      property 'apppathDEA', {

        // Custom properties
        formType = 'standard'
      }

      property 'autoResolveEJBRefDEA', {

        // Custom properties
        formType = 'standard'
      }

      property 'binaryConfigDEA', {

        // Custom properties
        formType = 'standard'
      }

      property 'blaNameDEA', {

        // Custom properties
        formType = 'standard'
      }

      property 'classpathDEA', {

        // Custom properties
        formType = 'standard'
      }

      property 'clientDeployModeDEA', {

        // Custom properties
        formType = 'standard'
      }

      property 'clusterDEA', {

        // Custom properties
        formType = 'standard'
      }

      property 'commandsDEA', {

        // Custom properties
        formType = 'standard'
      }

      property 'configNameDEA', {

        // Custom properties
        formType = 'standard'
      }

      property 'contextRootDEA', {

        // Custom properties
        formType = 'standard'
      }

      property 'createMBeansDEA', {

        // Custom properties
        formType = 'standard'
      }

      property 'customFilePermissionsDEA', {

        // Custom properties
        formType = 'standard'
      }

      property 'deployBeansDEA', {

        // Custom properties
        formType = 'standard'
      }

      property 'deployClientModDEA', {

        // Custom properties
        formType = 'standard'
      }

      property 'deployWSDEA', {

        // Custom properties
        formType = 'standard'
      }

      property 'distributeAppDEA', {

        // Custom properties
        formType = 'standard'
      }

      property 'filePermissionsDEA', {

        // Custom properties
        formType = 'standard'
      }

      property 'installDirDEA', {

        // Custom properties
        formType = 'standard'
      }

      property 'javaparamsDEA', {

        // Custom properties
        formType = 'standard'
      }

      property 'overrideClassReloadingDEA', {

        // Custom properties
        formType = 'standard'
      }

      property 'precompileJSPDEA', {

        // Custom properties
        formType = 'standard'
      }

      property 'processEmbConfigDEA', {

        // Custom properties
        formType = 'standard'
      }

      property 'reloadIntervalDEA', {

        // Custom properties
        formType = 'standard'
      }

      property 'serverListDEA', {

        // Custom properties
        formType = 'standard'
      }

      property 'startAppDEA', {

        // Custom properties
        formType = 'standard'
      }

      property 'syncActiveNodesDEA', {

        // Custom properties
        formType = 'standard'
      }

      property 'validateRefsDEA', {

        // Custom properties
        formType = 'standard'
      }

      property 'validateSchemaDEA', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasResourceName', {

        // Custom properties
        formType = 'standard'
      }

      property 'wsadminAbsPathDEA', {

        // Custom properties
        formType = 'standard'
      }
    }
  }
}
