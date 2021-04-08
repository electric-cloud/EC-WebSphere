package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class DeployEnterpriseApp extends Procedure {

    static DeployEnterpriseApp create(Plugin plugin) {
        return new DeployEnterpriseApp(procedureName: 'DeployEnterpriseApp', plugin: plugin, )
    }


    DeployEnterpriseApp flush() {
        this.flushParams()
        return this
    }

    //Generated
    
    DeployEnterpriseApp additionalcommands(String additionalcommands) {
        this.addParam('additionalcommands', additionalcommands)
        return this
    }
    
    
    DeployEnterpriseApp additionalDeployParams(String additionalDeployParams) {
        this.addParam('additionalDeployParams', additionalDeployParams)
        return this
    }
    
    
    DeployEnterpriseApp appName(String appName) {
        this.addParam('appName', appName)
        return this
    }
    
    
    DeployEnterpriseApp apppath(String apppath) {
        this.addParam('apppath', apppath)
        return this
    }
    
    
    DeployEnterpriseApp autoResolveEJBRef(boolean autoResolveEJBRef) {
        this.addParam('autoResolveEJBRef', autoResolveEJBRef)
        return this
    }
    
    
    DeployEnterpriseApp binaryConfig(boolean binaryConfig) {
        this.addParam('binaryConfig', binaryConfig)
        return this
    }
    
    
    DeployEnterpriseApp blaName(String blaName) {
        this.addParam('blaName', blaName)
        return this
    }
    
    
    DeployEnterpriseApp classpath(String classpath) {
        this.addParam('classpath', classpath)
        return this
    }
    
    
    DeployEnterpriseApp clientDeployMode(String clientDeployMode) {
        this.addParam('clientDeployMode', clientDeployMode)
        return this
    }
    
    DeployEnterpriseApp clientDeployMode(ClientDeployModeOptions clientDeployMode) {
        this.addParam('clientDeployMode', clientDeployMode.toString())
        return this
    }
    
    
    DeployEnterpriseApp cluster(String cluster) {
        this.addParam('cluster', cluster)
        return this
    }
    
    
    DeployEnterpriseApp commands(String commands) {
        this.addParam('commands', commands)
        return this
    }
    
    
    DeployEnterpriseApp configName(String configName) {
        this.addParam('configName', configName)
        return this
    }
    
    
    DeployEnterpriseApp contextRoot(String contextRoot) {
        this.addParam('contextRoot', contextRoot)
        return this
    }
    
    
    DeployEnterpriseApp createMBeans(boolean createMBeans) {
        this.addParam('createMBeans', createMBeans)
        return this
    }
    
    
    DeployEnterpriseApp customFilePermissions(String customFilePermissions) {
        this.addParam('customFilePermissions', customFilePermissions)
        return this
    }
    
    
    DeployEnterpriseApp deployBeans(boolean deployBeans) {
        this.addParam('deployBeans', deployBeans)
        return this
    }
    
    
    DeployEnterpriseApp deployClientMod(boolean deployClientMod) {
        this.addParam('deployClientMod', deployClientMod)
        return this
    }
    
    
    DeployEnterpriseApp deployWS(boolean deployWS) {
        this.addParam('deployWS', deployWS)
        return this
    }
    
    
    DeployEnterpriseApp distributeApp(boolean distributeApp) {
        this.addParam('distributeApp', distributeApp)
        return this
    }
    
    
    DeployEnterpriseApp filePermissions(String filePermissions) {
        this.addParam('filePermissions', filePermissions)
        return this
    }
    
    DeployEnterpriseApp filePermissions(FilePermissionsOptions filePermissions) {
        this.addParam('filePermissions', filePermissions.toString())
        return this
    }
    
    
    DeployEnterpriseApp installDir(String installDir) {
        this.addParam('installDir', installDir)
        return this
    }
    
    
    DeployEnterpriseApp javaparams(String javaparams) {
        this.addParam('javaparams', javaparams)
        return this
    }
    
    
    DeployEnterpriseApp mapModulesToServers(String mapModulesToServers) {
        this.addParam('MapModulesToServers', mapModulesToServers)
        return this
    }
    
    
    DeployEnterpriseApp overrideClassReloading(boolean overrideClassReloading) {
        this.addParam('overrideClassReloading', overrideClassReloading)
        return this
    }
    
    
    DeployEnterpriseApp precompileJSP(boolean precompileJSP) {
        this.addParam('precompileJSP', precompileJSP)
        return this
    }
    
    
    DeployEnterpriseApp processEmbConfig(boolean processEmbConfig) {
        this.addParam('processEmbConfig', processEmbConfig)
        return this
    }
    
    
    DeployEnterpriseApp reloadInterval(String reloadInterval) {
        this.addParam('reloadInterval', reloadInterval)
        return this
    }
    
    
    DeployEnterpriseApp serverList(String serverList) {
        this.addParam('serverList', serverList)
        return this
    }
    
    
    DeployEnterpriseApp startApp(boolean startApp) {
        this.addParam('startApp', startApp)
        return this
    }
    
    
    DeployEnterpriseApp syncActiveNodes(boolean syncActiveNodes) {
        this.addParam('syncActiveNodes', syncActiveNodes)
        return this
    }
    
    
    DeployEnterpriseApp validateRefs(String validateRefs) {
        this.addParam('validateRefs', validateRefs)
        return this
    }
    
    DeployEnterpriseApp validateRefs(ValidateRefsOptions validateRefs) {
        this.addParam('validateRefs', validateRefs.toString())
        return this
    }
    
    
    DeployEnterpriseApp validateSchema(boolean validateSchema) {
        this.addParam('validateSchema', validateSchema)
        return this
    }
    
    
    DeployEnterpriseApp wsadminAbsPath(String wsadminAbsPath) {
        this.addParam('wsadminAbsPath', wsadminAbsPath)
        return this
    }
    
    
    
    
    enum ClientDeployModeOptions {
    
    ISOLATED("isolated"),
    
    FEDERATED("federated"),
    
    SERVER_DEPLOYED("server_deployed")
    
    private String value
    ClientDeployModeOptions(String value) {
        this.value = value
    }

    String toString() {
        return this.value
    }
}
    
    enum FilePermissionsOptions {
    
    ALLOW_ALL_FILES_TO_BE_READ_BUT_NOT_WRITTEN_TO_755_(".*=755"),

     ALLOW_EXECUTABLES_TO_EXECUTE_DLL_755_SO_755_A_755_SL_755_(".*\\.dll=755#.*\\.so=755#.*\\.a=755#.*\\.sl=755"),
    
     ALLOW_HTML_AND_IMAGE_FILES_TO_BE_READ_BY_EVERYONE_HTM_755_HTML_755_GIF_755_JPG_755_(".*\\.htm=755#.*\\.html=755#.*\\.gif=755#.*\\.jpg=755")
    
    private String value
    FilePermissionsOptions(String value) {
        this.value = value
    }

    String toString() {
        return this.value
    }
}
    
    enum ValidateRefsOptions {
    
    OFF("off"),
    
    WARN("warn"),
    
    FAIL("fail")
    
    private String value
    ValidateRefsOptions(String value) {
        this.value = value
    }

    String toString() {
        return this.value
    }
}
    
}