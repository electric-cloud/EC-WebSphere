package com.cloudbees.pdk.hen

import com.cloudbees.pdk.hen.procedures.*
import com.cloudbees.pdk.hen.Plugin

import static com.cloudbees.pdk.hen.Utils.env

class WebSphere extends Plugin {

    String resourceName = System.getenv('WAS_HOST') ?: "wsResource"
    String wsUsername = System.getenv('WAS_USERNAME') ?: ""
    String wsPassword = System.getenv('WAS_PASSWORD') ?: ""
    String wsHost = System.getenv('WAS_HOST') ?: ""
    String wsPort = System.getenv('WAS_PORT') ?: ""
    String wsAdminPath = System.getenv('WSADMIN_PATH') ?: ""
    String wsConnType = System.getenv('WAS_CONNTYPE') ?: ""
    Credential cred = new Credential(userName: wsUsername, password: wsPassword)

    WebSphereConfig newConfig = WebSphereConfig
        .create(this)
        .conntype(wsConnType)
        .wsadminabspath(wsAdminPath)
        .websphereurl(wsHost)
        .websphereport(wsPort)
        .testconnectionres(wsHost)
        .debug("1")
        .credential(cred.userName, cred.password)

    WebSphereConfig newWrongConfig = WebSphereConfig
        .create(this)
        .conntype(wsConnType)
        .wsadminabspath(wsAdminPath)
        .websphereurl(wsHost + "zxcds")
        .websphereport(wsPort)
        .testconnectionres(wsHost)
        .debug("1")
        .credential(cred.userName, cred.password)

    static WebSphere create() {
        WebSphere plugin = new WebSphere(name: 'EC-WebSphere')
        plugin.configure(plugin.config)
        return plugin
    }
    static WebSphere createWithoutConfig() {
        // todo: possible problem is that different procedures contains different field name  for configuration value
        WebSphere plugin = new WebSphere(
                name: 'EC-WebSphere',
                configPath: 'websphere_cfgs',
                configFieldName: 'configurationName',
                configurationHandling: ConfigurationHandling.OLD
        )
        return plugin
    }

    static WebSphere createWithNewConfig() {
        WebSphere plugin = new WebSphere(
                name: 'EC-WebSphere',
                configPath: 'websphere_cfgs',
                configFieldName: 'configurationName',
                configurationHandling: ConfigurationHandling.NEW
        )
        plugin.configure(plugin.newConfig)
        return plugin
    }

    static WebSphere createWithNewWrongConfig() {
        WebSphere plugin = new WebSphere(
                name: 'EC-WebSphere',
                configPath: 'websphere_cfgs',
                configFieldName: 'configurationName',
                configurationHandling: ConfigurationHandling.NEW
        )
        plugin.configure(plugin.newWrongConfig)
        return plugin
    }

    //user-defined after boilerplate was generated, default parameters setup
    WebSphereConfig config = WebSphereConfig
        .create(this)
        //.parameter(value) add parameters here

    EditConfiguration editConfig(){
        editConfiguration.addParam(this.configFieldNameCreateConfiguration, this.configName)
        return editConfiguration
    }


    CheckApp checkApp = CheckApp.create(this)

    CheckNodeStatus checkNodeStatus = CheckNodeStatus.create(this, resourceName)

    CheckPageStatus checkPageStatus = CheckPageStatus.create(this)

    CheckServerStatus checkServerStatus = CheckServerStatus.create(this)

    ConfigEJBContainer configEJBContainer = ConfigEJBContainer.create(this)

    ConfigureSession configureSession = ConfigureSession.create(this)

    CreateApplicationServer createApplicationServer = CreateApplicationServer.create(this)

    CreateApplicationServerTemplate createApplicationServerTemplate = CreateApplicationServerTemplate.create(this)

    CreateCluster createCluster = CreateCluster.create(this)

    CreateClusterMembers createClusterMembers = CreateClusterMembers.create(this)

    CreateDatasource createDatasource = CreateDatasource.create(this)

    CreateEndToEndMailProvider createEndToEndMailProvider = CreateEndToEndMailProvider.create(this)

    CreateFirstClusterMember createFirstClusterMember = CreateFirstClusterMember.create(this)

    CreateJDBCProvider createJDBCProvider = CreateJDBCProvider.create(this)

    CreateJMSProvider createJMSProvider = CreateJMSProvider.create(this)

    CreateMailSession createMailSession = CreateMailSession.create(this)

    CreateOrUpdateJMSQueue createOrUpdateJMSQueue = CreateOrUpdateJMSQueue.create(this)

    CreateOrUpdateJMSTopic createOrUpdateJMSTopic = CreateOrUpdateJMSTopic.create(this)

    CreateOrUpdateSIBJMSActivationSpec createOrUpdateSIBJMSActivationSpec = CreateOrUpdateSIBJMSActivationSpec.create(this)

    CreateOrUpdateSIBJMSConnectionFactory createOrUpdateSIBJMSConnectionFactory = CreateOrUpdateSIBJMSConnectionFactory.create(this)

    CreateOrUpdateWMQJMSActivationSpec createOrUpdateWMQJMSActivationSpec = CreateOrUpdateWMQJMSActivationSpec.create(this)

    CreateOrUpdateWMQJMSConnectionFactory createOrUpdateWMQJMSConnectionFactory = CreateOrUpdateWMQJMSConnectionFactory.create(this)

    DeleteApplicationServer deleteApplicationServer = DeleteApplicationServer.create(this)

    DeleteApplicationServerTemplate deleteApplicationServerTemplate = DeleteApplicationServerTemplate.create(this)

    DeleteCluster deleteCluster = DeleteCluster.create(this)

    DeleteDatasource deleteDatasource = DeleteDatasource.create(this)

    DeleteJDBCProvider deleteJDBCProvider = DeleteJDBCProvider.create(this)

    DeleteJMSActivationSpec deleteJMSActivationSpec = DeleteJMSActivationSpec.create(this)

    DeleteJMSConnectionFactory deleteJMSConnectionFactory = DeleteJMSConnectionFactory.create(this)

    DeleteJMSProvider deleteJMSProvider = DeleteJMSProvider.create(this)

    DeleteJMSQueue deleteJMSQueue = DeleteJMSQueue.create(this)

    DeleteJMSTopic deleteJMSTopic = DeleteJMSTopic.create(this)

    DeployApp deployApp = DeployApp.create(this)

    DeployEnterpriseApp deployEnterpriseApp = DeployEnterpriseApp.create(this)

    DeployOSGiApp deployOSGiApp = DeployOSGiApp.create(this)

    Discover discover = Discover.create(this)

    DiscoverResource discoverResource = DiscoverResource.create(this)

    EditConfiguration editConfiguration = EditConfiguration.create(this)

    ExportApplicationServer exportApplicationServer = ExportApplicationServer.create(this)

    ImportApplicationServer importApplicationServer = ImportApplicationServer.create(this)

    ListClusterMembers listClusterMembers = ListClusterMembers.create(this)

    MapSharedLibrary mapSharedLibrary = MapSharedLibrary.create(this)

    ModifyApplicationClassLoader modifyApplicationClassLoader = ModifyApplicationClassLoader.create(this)

    PublishWSDL publishWSDL = PublishWSDL.create(this)

    RemoveClusterMembers removeClusterMembers = RemoveClusterMembers.create(this)

    RunCustomJob runCustomJob = RunCustomJob.create(this)

    StartApp startApp = StartApp.create(this)

    StartApplicationServers startApplicationServers = StartApplicationServers.create(this)

    StartCluster startCluster = StartCluster.create(this)

    StartDeploymentManager startDeploymentManager = StartDeploymentManager.create(this)

    StartNode startNode = StartNode.create(this)

    StartServer startServer = StartServer.create(this)

    StopApp stopApp = StopApp.create(this)

    StopApplicationServers stopApplicationServers = StopApplicationServers.create(this)

    StopCluster stopCluster = StopCluster.create(this)

    StopDeploymentManager stopDeploymentManager = StopDeploymentManager.create(this)

    StopNode stopNode = StopNode.create(this)

    StopServer stopServer = StopServer.create(this)

    SyncNodes syncNodes = SyncNodes.create(this)

    UndeployApp undeployApp = UndeployApp.create(this)

    UpdateApp updateApp = UpdateApp.create(this)

    TestConfiguration testConfiguration = TestConfiguration.create(this)
}