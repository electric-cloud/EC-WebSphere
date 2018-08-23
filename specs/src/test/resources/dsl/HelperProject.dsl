project 'WebSphere Helper Project', {
  resourceName = null
  workspaceName = null

  procedure 'Deploy hello-world.war', {
    description = ''
    jobNameTemplate = ''
    resourceName = ''
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workspaceName = ''

    formalParameter 'application_path', defaultValue: '', {
      description = ''
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '1'
      type = 'entry'
    }

    formalParameter 'config_name', defaultValue: '', {
      description = ''
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '1'
      type = 'entry'
    }

    formalParameter 'resource_name', defaultValue: '', {
      description = ''
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '1'
      type = 'entry'
    }

    formalParameter 'target_server', defaultValue: '', {
      description = ''
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '1'
      type = 'entry'
    }

    formalParameter 'workspace_name', defaultValue: '', {
      description = ''
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '1'
      type = 'entry'
    }

    step 'Deploy hello-world.war', {
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
      releaseMode = 'none'
      resourceName = '$[resource_name]'
      shell = null
      subprocedure = 'DeployEnterpriseApp'
      subproject = '/plugins/EC-WebSphere/project'
      timeLimit = ''
      timeLimitUnits = 'minutes'
      workingDirectory = null
      workspaceName = '$[workspace_name]'
      actualParameter 'additionalcommands', ''
      actualParameter 'additionalDeployParams', '-MapWebModToVH [[hello-world.war hello-world.war,WEB-INF/web.xml default_host ]] '
      actualParameter 'appName', 'HelloWorld'
      actualParameter 'apppath', '$[application_path]'
      actualParameter 'autoResolveEJBRef', '0'
      actualParameter 'binaryConfig', '0'
      actualParameter 'blaName', ''
      actualParameter 'classpath', ''
      actualParameter 'clientDeployMode', ''
      actualParameter 'cluster', ''
      actualParameter 'commands', ''
      actualParameter 'configName', '$[config_name]'
      actualParameter 'contextRoot', '/hello-world'
      actualParameter 'createMBeans', '0'
      actualParameter 'customFilePermissions', ''
      actualParameter 'deployBeans', '0'
      actualParameter 'deployClientMod', '0'
      actualParameter 'deployWS', '0'
      actualParameter 'distributeApp', '1'
      actualParameter 'filePermissions', ''
      actualParameter 'installDir', ''
      actualParameter 'javaparams', ''
      actualParameter 'MapModulesToServers', ''
      actualParameter 'overrideClassReloading', '0'
      actualParameter 'precompileJSP', '0'
      actualParameter 'processEmbConfig', '0'
      actualParameter 'reloadInterval', ''
      actualParameter 'serverList', '$[target_server]'
      actualParameter 'startApp', '1'
      actualParameter 'syncActiveNodes', '1'
      actualParameter 'validateRefs', ''
      actualParameter 'validateSchema', '0'
      actualParameter 'wsadminAbsPath', ''
    }

    // Custom properties

    property 'ec_customEditorData', {

      // Custom properties

      property 'parameters', {

        // Custom properties

        property 'application_path', {

          // Custom properties
          formType = 'standard'
        }

        property 'config_name', {

          // Custom properties
          formType = 'standard'
        }

        property 'resource_name', {

          // Custom properties
          formType = 'standard'
        }

        property 'server_name', {

          // Custom properties
          formType = 'standard'
        }

        property 'target_server', {

          // Custom properties
          formType = 'standard'
        }

        property 'workspace_name', {

          // Custom properties
          formType = 'standard'
        }
      }
    }
  }

  procedure 'GetApplication', {
    description = ''
    jobNameTemplate = ''
    resourceName = ''
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workspaceName = ''

    formalParameter 'resource_name', defaultValue: '', {
      description = ''
      expansionDeferred = '0'
      label = null
      orderIndex = null
      required = '1'
      type = 'entry'
    }

    step 'GetApplication', {
      description = ''
      alwaysRun = '0'
      broadcast = '0'
      command = '''cd /tmp;
git clone https://github.com/justnoxx/hello-world-war;
cp /tmp/hello-world-war/dist/hello-world.war /tmp'''
      condition = ''
      errorHandling = 'failProcedure'
      exclusiveMode = 'none'
      logFileName = ''
      parallel = '0'
      postProcessor = ''
      precondition = ''
      releaseMode = 'none'
      resourceName = '$[resource_name]'
      shell = ''
      subprocedure = null
      subproject = null
      timeLimit = ''
      timeLimitUnits = 'minutes'
      workingDirectory = ''
      workspaceName = ''
    }

    // Custom properties

    property 'ec_customEditorData', {

      // Custom properties

      property 'parameters', {

        // Custom properties

        property 'resource_name', {

          // Custom properties
          formType = 'standard'
        }
      }
    }
  }
}
