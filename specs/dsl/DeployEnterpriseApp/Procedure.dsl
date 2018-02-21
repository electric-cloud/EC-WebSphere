
procedure 'DeployEnterpriseApp', {
  description = ''
  jobNameTemplate = ''
  projectName = 'EC-WebSphere-Specs-CheckApp'
  resourceName = ''
  timeLimit = ''
  timeLimitUnits = 'minutes'
  workspaceName = ''

  formalParameter 'appName', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'apppath', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'cluster', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'commands', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'configName', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'contextRoot', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'createMBeans', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'customFilePermissions', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'deployBeans', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'deployClientMod', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'deployWS', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'distributeApp', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'filePermissions', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'installDir', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'javaparams', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'MapModulesToServers', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'overrideClassReloading', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'precompileJSP', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'processEmbConfig', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'reloadInterval', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'serverList', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'startApp', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'syncActiveNodes', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'validateRefs', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'validateSchema', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '0'
    type = 'entry'
  }

  formalParameter 'wsadminAbsPath', defaultValue: '', {
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
    projectName = 'EC-WebSphere-Specs-CheckApp'
    releaseMode = 'none'
    resourceName = '$[wasResourceName]'
    shell = null
    subprocedure = 'DeployEnterpriseApp'
    subproject = '/plugins/EC-WebSphere/project'
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workingDirectory = null
    workspaceName = ''
    actualParameter 'additionalcommands', '$[additionalcommands]'
    actualParameter 'additionalDeployParams', '$[additionalDeployParams]'
    actualParameter 'appName', '$[appName]'
    actualParameter 'apppath', '$[apppath]'
    actualParameter 'autoResolveEJBRef', '$[autoResolveEJBRef]'
    actualParameter 'binaryConfig', '$[binaryConfig]'
    actualParameter 'blaName', '$[blaName]'
    actualParameter 'classpath', '$[classpath]'
    actualParameter 'clientDeployMode', '$[clientDeployMode]'
    actualParameter 'cluster', '$[cluster]'
    actualParameter 'commands', '$[commands]'
    actualParameter 'configName', '$[configName]'
    actualParameter 'contextRoot', '$[contextRoot]'
    actualParameter 'createMBeans', '$[createMBeans]'
    actualParameter 'customFilePermissions', '$[customFilePermissions]'
    actualParameter 'deployBeans', '$[deployBeans]'
    actualParameter 'deployClientMod', '$[deployClientMod]'
    actualParameter 'deployWS', '$[deployWS]'
    actualParameter 'distributeApp', '$[distributeApp]'
    actualParameter 'filePermissions', '$[filePermissions]'
    actualParameter 'installDir', '$[installDir]'
    actualParameter 'javaparams', '$[javaparams]'
    actualParameter 'MapModulesToServers', '$[MapModulesToServers]'
    actualParameter 'overrideClassReloading', '$[overrideClassReloading]'
    actualParameter 'precompileJSP', '$[precompileJSP]'
    actualParameter 'processEmbConfig', '$[processEmbConfig]'
    actualParameter 'reloadInterval', '$[reloadInterval]'
    actualParameter 'serverList', '$[serverList]'
    actualParameter 'startApp', '$[startApp]'
    actualParameter 'syncActiveNodes', '$[syncActiveNodes]'
    actualParameter 'validateRefs', '$[validateRefs]'
    actualParameter 'validateSchema', '$[validateSchema]'
    actualParameter 'wsadminAbsPath', '$[wsadminAbsPath]'
  }

  // Custom properties

  property 'ec_customEditorData', {

    // Custom properties

    property 'parameters', {

      // Custom properties

      property 'MapModulesToServers', {

        // Custom properties
        formType = 'standard'
      }

      property 'appName', {

        // Custom properties
        formType = 'standard'
      }

      property 'apppath', {

        // Custom properties
        formType = 'standard'
      }

      property 'cluster', {

        // Custom properties
        formType = 'standard'
      }

      property 'commands', {

        // Custom properties
        formType = 'standard'
      }

      property 'configName', {

        // Custom properties
        formType = 'standard'
      }

      property 'contextRoot', {

        // Custom properties
        formType = 'standard'
      }

      property 'createMBeans', {

        // Custom properties
        formType = 'standard'
      }

      property 'customFilePermissions', {

        // Custom properties
        formType = 'standard'
      }

      property 'deployBeans', {

        // Custom properties
        formType = 'standard'
      }

      property 'deployClientMod', {

        // Custom properties
        formType = 'standard'
      }

      property 'deployWS', {

        // Custom properties
        formType = 'standard'
      }

      property 'distributeApp', {

        // Custom properties
        formType = 'standard'
      }

      property 'filePermissions', {

        // Custom properties
        formType = 'standard'
      }

      property 'installDir', {

        // Custom properties
        formType = 'standard'
      }

      property 'javaparams', {

        // Custom properties
        formType = 'standard'
      }

      property 'overrideClassReloading', {

        // Custom properties
        formType = 'standard'
      }

      property 'precompileJSP', {

        // Custom properties
        formType = 'standard'
      }

      property 'processEmbConfig', {

        // Custom properties
        formType = 'standard'
      }

      property 'reloadInterval', {

        // Custom properties
        formType = 'standard'
      }

      property 'serverList', {

        // Custom properties
        formType = 'standard'
      }

      property 'startApp', {

        // Custom properties
        formType = 'standard'
      }

      property 'syncActiveNodes', {

        // Custom properties
        formType = 'standard'
      }

      property 'validateRefs', {

        // Custom properties
        formType = 'standard'
      }

      property 'validateSchema', {

        // Custom properties
        formType = 'standard'
      }

      property 'wsadminAbsPath', {

        // Custom properties
        formType = 'standard'
      }
    }
  }
}
