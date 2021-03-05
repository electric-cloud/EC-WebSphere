package com.cloudbees.plugin.spec

import com.cloudbees.pdk.hen.Credential
import com.cloudbees.pdk.hen.ServerHandler
import com.cloudbees.pdk.hen.Utils
import com.cloudbees.pdk.hen.WebSphere
import com.cloudbees.pdk.hen.JobOutcome
import com.cloudbees.pdk.hen.procedures.WebSphereConfig
import com.electriccloud.plugins.annotations.NewFeature
import com.electriccloud.plugins.annotations.Sanity
import spock.lang.Ignore
import spock.lang.Requires
import spock.lang.Specification
import spock.lang.Unroll
import org.junit.Assume

import static com.cloudbees.pdk.hen.Utils.setConfigurationValues
import static com.cloudbees.pdk.hen.Utils.verifyExistenceOfPluginsCredentials

class CreateConfigurationWithExtCredsSuite extends Specification {

    static ServerHandler serverHandler = ServerHandler.getInstance()

    static final String wsUsername = System.getenv("WAS_USERNAME") ?: ""
    static final String wsPassword = System.getenv("WAS_PASSWORD") ?: ""

    static final String wsHost = System.getenv('WAS_HOST') ?: ""
    static final String wsAgentPort =  System.getenv('WEBSPHERE_RESOURCE_PORT') ?: '7808'
    static final String wsAdminPath = System.getenv('WSADMIN_PATH')
    static final String wasPort = System.getenv('WAS_PORT')

//    TODO: Proxy is not implemented in EC-WebSphere and all lines codes related to proxy are commented
//    TODO: they can be deleted or be left for better times
//    static final String proxyUrl = System.getenv("EF_PROXY_URL") ?: ""
//    static final String proxyUsername = System.getenv("EF_PROXY_USERNAME") ?: ""
//    static final String proxyPassword = System.getenv("EF_PROXY_PASSWORD") ?: ""

    static String cdFlowProject = "WebSphereCredsProject"
    static String cdFlowProjectCredsName = "WebSphereCreds"
    static String cdFlowProjectNegativeCredsName = "negativeWebSphereCreds"

//    static String cdFlowProjectNegativeProxyCredsName = "negativeProxyCreds"
//    static String cdFlowProjectProxyCredsName = "proxyCreds"

    static String wrongUsername = "wrongUser"
    static String wrongPassword = "wrongPassword"

    static String credsFieldName = "credential"
    static String credsProxyFieldName = "proxy_credential"

    static String deleteCredentialDsl = """
        def credProject = args.project
        def credName = args.credName

        
        deleteCredential([
            projectName: credProject, 
            credentialName: credName, 
        ])
    """

    static Credential creds = new Credential(userName: wsUsername, password: wsPassword)
    static Credential wrongCreds = new Credential(userName: wrongUsername, password: wrongPassword)
    static Map<Utils.CredsStates, Credential> credsByState = [:]

//    static Credential proxyCreds = new Credential(userName: proxyUsername, password: proxyPassword)
//    static Credential wrongProxyCreds = new Credential(userName: wrongUsername, password: wrongPassword)
//    static Map<Utils.CredsStates, Credential> proxyCredsByState = [:]


    def setupSpec() {
        // Create credential in the project
        serverHandler.dsl("createProject([projectName: '${cdFlowProject}', ])")
        serverHandler.setupResource(wsHost, wsHost, wsAgentPort.toInteger())

        Map<String, Credential> referenceCreds  = serverHandler.createCredentials(
                cdFlowProject, [
                (cdFlowProjectCredsName): new Credential(userName: wsUsername, password: wsPassword),
                (cdFlowProjectNegativeCredsName): new Credential(userName: wrongUsername, password: wrongPassword),
        ])

        credsByState[Utils.CredsStates.RUNTIME] = creds
        credsByState[Utils.CredsStates.REFERENCE] = referenceCreds[cdFlowProjectCredsName]
        credsByState[Utils.CredsStates.WRONG_RUNTIME] = wrongCreds
        credsByState[Utils.CredsStates.WRONG_REFERENCE] = referenceCreds[cdFlowProjectNegativeCredsName]
    }

    def cleanupSpec() {
//        <--deletion of creds, they will be deleted anyway if project is deleted-->
        serverHandler.dsl(deleteCredentialDsl, [project: cdFlowProject, credName: cdFlowProjectCredsName,])
        serverHandler.dsl(deleteCredentialDsl, [project: cdFlowProject, credName: cdFlowProjectNegativeCredsName,])

        serverHandler.dsl("deleteProject([projectName: '${cdFlowProject}', ])")
    }

    @Sanity
    @Requires({!System.getenv("EF_PROXY_URL")})
    @Unroll
    def "Sanity without proxy. Create Config with credential reference/runtime credentials"() {
        when: "Create plugin config"
        WebSphere webSpherePlugin = WebSphere.createWithoutConfig()
        WebSphereConfig webSphereConfig = WebSphereConfig.create(webSpherePlugin)

        webSphereConfig.wsadminabspath(wsAdminPath)
                .conntype("SOAP")
                .websphereurl(wsHost)
                .websphereport(System.getenv('WAS_PORT'))
                .testconnection(initTestConn)
                .testconnectionres(wsHost)
                .debug("1")


        setConfigurationValues(initCredential, credsFieldName, credsByState[initCredential], webSphereConfig)
//        setConfigurationValues(initProxyCredential, credsProxyFieldName, proxyCredsByState[initProxyCredential], gitConfig)
        webSpherePlugin.configure(webSphereConfig)

        and: "Run Plugin Procedure - CheckNodeStatus"

//        I assume that  checkServerStatus check rest api without using creds
//        def result = webSpherePlugin.checkServerStatus
//                .maxelapsedtime("30")
//                .successcriteria("response")
//                .run()

        def result = webSpherePlugin.checkNodeStatus
            .wsadminAbsPath(wsAdminPath)
        // TODO: currently we don't have license for the default environment (v90nd)
        // TODO: and I wrote tests for v90s, pattern of node name can be different from {wshost + "Node1"}
            .nodeName(wsHost + "Node01")
            .successCriteria("ALL_RUNNING")
            .run()

        then: "Verify that procedure completed as expected and credentials in plugin exist or not exist"
        assert result.isSuccessful()
        verifyExistenceOfPluginsCredentials(initCredential, webSpherePlugin.configName, webSpherePlugin)
//        verifyExistenceOfPluginsCredentials(initProxyCredential, webSpherePlugin.configName + "_" + credsProxyFieldName, webSpherePlugin)
//         ---------------------------------------------------------------

        when: "Edit Configuration to second state"
        setConfigurationValues(toCredential, credsFieldName, credsByState[toCredential], webSpherePlugin.editConfiguration)
//        setConfigurationValues(toProxyCredential, credsProxyFieldName, proxyCredsByState[toProxyCredential], gitPlugin.editConfiguration)
        def resultOfEditConfig = webSpherePlugin.editConfig()
                .wsadminabspath(wsAdminPath)
                .conntype("SOAP")
                .websphereurl(wsHost)
                .websphereport(System.getenv('WAS_PORT'))
                .testconnection(toTestConn)
                .testconnectionres(wsHost)
                .debug("1")
//                .httpproxy(toProxyCredential == Utils.CredsStates.EMPTY ? "" : proxyUrl)
                .runNaked()

        and: "run plugin procedure again with edited configuration"
        def secondResult = webSpherePlugin.checkNodeStatus
                .run()

        then: "Verify that procedure completed as expected and credentials in plugin exist or not exist"
        assert resultOfEditConfig.isSuccessful()
        assert secondResult.isSuccessful()
        verifyExistenceOfPluginsCredentials(toCredential, webSpherePlugin.configName, webSpherePlugin)
//        verifyExistenceOfPluginsCredentials(toProxyCredential, gitPlugin.configName+ "_" + credsProxyFieldName, gitPlugin)
        // ---------------------------------------------------------------

        when: "Edit Configuration - back to initial state"
        setConfigurationValues(initCredential, credsFieldName, credsByState[initCredential], webSpherePlugin.editConfiguration)
//        setConfigurationValues(initProxyCredential, credsProxyFieldName, proxyCredsByState[initProxyCredential], gitPlugin.editConfiguration)
        def resultOfEditConfigToInitState = webSpherePlugin.editConfig()
                .wsadminabspath(wsAdminPath)
                .conntype("SOAP")
                .websphereurl(wsHost)
                .websphereport(System.getenv('WAS_PORT'))
                .testconnection(initTestConn)
                .testconnectionres(wsHost)
                .debug("1")
//                .httpproxy(toProxyCredential == Utils.CredsStates.EMPTY ? "" : proxyUrl)
                .runNaked()

        and: "run plugin procedure again with edited configuration"
        def thirdResult = webSpherePlugin.checkNodeStatus
                .run()

        then: "Verify that procedure completed as expected and credentials in plugin exist or not exist"
        assert resultOfEditConfigToInitState.isSuccessful()
        assert thirdResult.isSuccessful()
        verifyExistenceOfPluginsCredentials(initCredential, webSpherePlugin.configName, webSpherePlugin)
//        verifyExistenceOfPluginsCredentials(initProxyCredential, gitPlugin.configName + "_" + credsProxyFieldName, gitPlugin)
        // ---------------------------------------------------------------

        when: "Delete plugin configuration"
        webSpherePlugin.deleteConfiguration(webSpherePlugin.configName)

        then: "Verify that there are no  credential and configuration after configuration deleting"
        verifyExistenceOfPluginsCredentials(Utils.CredsStates.DELETED, webSpherePlugin.configName, webSpherePlugin)
//        verifyExistenceOfPluginsCredentials(Utils.CredsStates.DELETED, gitPlugin.configName + '_' + credsProxyFieldName, gitPlugin)

        cleanup:
//         if test failed for some reason and configuration wasn't delete in the last step of test,
//         the configuration anyway will be deleted to not break next tests
        webSpherePlugin.deleteConfiguration(webSpherePlugin.configName)

        where:
        initCredential              | initProxyCredential     | toCredential                | toProxyCredential       | initTestConn    | toTestConn
        Utils.CredsStates.RUNTIME   | Utils.CredsStates.EMPTY | Utils.CredsStates.REFERENCE | Utils.CredsStates.EMPTY | false           | false
        Utils.CredsStates.RUNTIME   | Utils.CredsStates.EMPTY | Utils.CredsStates.REFERENCE | Utils.CredsStates.EMPTY | false           | true
        Utils.CredsStates.REFERENCE | Utils.CredsStates.EMPTY | Utils.CredsStates.RUNTIME   | Utils.CredsStates.EMPTY | true            | false
        Utils.CredsStates.REFERENCE | Utils.CredsStates.EMPTY | Utils.CredsStates.RUNTIME   | Utils.CredsStates.EMPTY | true            | true
    }

    // TODO: remove ignore after bug https://cloudbees.atlassian.net/browse/FLOWPLUGIN-9056 will be fixed
    @Ignore
    @NewFeature(pluginVersion = "2.9.0")
    @Requires({!System.getenv("EF_PROXY_URL")})
    @Unroll
    def "Negative without proxy. Create Config with credential reference/runtime credentials"() {
        // TODO uncomment lines with proxyCreds, when proxy creds will be added to Plugin
        when: "Create plugin config"
        WebSphere webSpherePlugin = WebSphere.createWithoutConfig()
        WebSphereConfig webSphereConfig = WebSphereConfig.create(webSpherePlugin)

        webSphereConfig.wsadminabspath(wsAdminPath)
                .conntype("SOAP")
                .websphereurl(wsHost)
                .websphereport(System.getenv('WAS_PORT'))
                .testconnection(initTestConn)
                .testconnectionres(wsHost)
                .debug(true)

        setConfigurationValues(initCredential, credsFieldName, credsByState[initCredential], webSphereConfig)
//        setConfigurationValues(initProxyCredential, credsProxyFieldName, proxyCredsByState[initProxyCredential], gitConfig)

        def configCreationResult = webSpherePlugin.configure(webSphereConfig, expResultOfCreatingConfig)

        and: "Run Plugin Procedure - GetProjectVersions"
        def result = webSpherePlugin.checkNodeStatus
                .wsadminAbsPath(wsAdminPath)
                .nodeName(wsHost + "Node01")
                .successCriteria("ALL_RUNNING")
                .run()

        then: "Verify that procedure completed as expected and credentials in plugin exist or not exist"

        assert result.outcome == initExpectedJobResult
        assert configCreationResult.outcome == expResultOfCreatingConfig
        if (expResultOfCreatingConfig == JobOutcome.SUCCESS) {
            verifyExistenceOfPluginsCredentials(initCredential, webSpherePlugin.configName, webSpherePlugin)
//            verifyExistenceOfPluginsCredentials(initProxyCredential, gitPlugin.configName + "_" + credsProxyFieldName, gitPlugin)
        }
        if (expResultOfCreatingConfig == JobOutcome.ERROR) {
            verifyExistenceOfPluginsCredentials(Utils.CredsStates.DELETED, webSpherePlugin.configName, webSpherePlugin)
//            verifyExistenceOfPluginsCredentials(Utils.CredsStates.DELETED, gitPlugin.configName + "_" + credsProxyFieldName, gitPlugin)
            // no sense to continue test scenario, because configuration wasn't created
            // so test will be skipped
            Assume.assumeFalse(true)
        }

        // ---------------------------------------------------------------

        when: "Edit Configuration to second state"
        setConfigurationValues(toCredential, credsFieldName, credsByState[toCredential], webSpherePlugin.editConfiguration)
//        setConfigurationValues(toProxyCredential, credsProxyFieldName, proxyCredsByState[toProxyCredential], gitPlugin.editConfiguration)
        def resultOfEditConfig = webSpherePlugin.editConfig()
                .wsadminabspath(wsAdminPath)
                .conntype("SOAP")
                .websphereurl(wsHost)
                .websphereport(System.getenv('WAS_PORT'))
                .testconnection(toTestConn)
                .testconnectionres(wsHost)
                .debug(true)
//                .httpproxy(toProxyCredential == Utils.CredsStates.EMPTY ? "" : proxyUrl)
                .runNaked()

        and: "run plugin procedure again with edited configuration"
        def secondResult = webSpherePlugin.checkNodeStatus
                .run()

        then: "Verify that procedure completed as expected and credentials in plugin exist or not exist"
        assert resultOfEditConfig.outcome == expResultOfEditingConfig
        assert secondResult.outcome == afterEditingJobResult

        if (expResultOfEditingConfig == JobOutcome.SUCCESS) {
            verifyExistenceOfPluginsCredentials(toCredential, webSpherePlugin.configName, webSpherePlugin)
//            verifyExistenceOfPluginsCredentials(toProxyCredential, gitPlugin.configName + "_" + credsProxyFieldName, gitPlugin)
        }
        if (expResultOfEditingConfig == JobOutcome.ERROR) {
            verifyExistenceOfPluginsCredentials(initCredential, webSpherePlugin.configName, webSpherePlugin)
//            verifyExistenceOfPluginsCredentials(initProxyCredential, gitPlugin.configName + "_" + credsProxyFieldName, gitPlugin)
        }

        // ---------------------------------------------------------------

        when: "Edit Configuration - back to initial state"
        setConfigurationValues(initCredential, credsFieldName, credsByState[initCredential], webSpherePlugin.editConfiguration)
//        setConfigurationValues(initProxyCredential, credsProxyFieldName, proxyCredsByState[initProxyCredential], gitPlugin.editConfiguration)
        def resultOfEditConfigToInitState = webSpherePlugin.editConfig()
                .wsadminabspath(wsAdminPath)
                .conntype("SOAP")
                .websphereurl(wsHost)
                .websphereport(System.getenv('WAS_PORT'))
                .testconnection(initTestConn)
                .testconnectionres(wsHost)
                .debug(true)
//                .httpproxy(toProxyCredential == Utils.CredsStates.EMPTY ? "" : proxyUrl)
                .runNaked()

        and: "run plugin procedure again with edited configuration"
        def thirdResult = webSpherePlugin.checkNodeStatus
                .run()

        then: "Verify that procedure completed as expected and credentials in plugin exist or not exist"
        assert resultOfEditConfigToInitState.outcome == expResultOfCreatingConfig
        assert thirdResult.outcome == initExpectedJobResult

        if (expResultOfCreatingConfig == JobOutcome.SUCCESS) {
            verifyExistenceOfPluginsCredentials(initCredential, webSpherePlugin.configName, webSpherePlugin)
//            verifyExistenceOfPluginsCredentials(initProxyCredential, gitPlugin.configName + "_" + credsProxyFieldName, gitPlugin)
        }
        if (expResultOfCreatingConfig == JobOutcome.ERROR) {
            verifyExistenceOfPluginsCredentials(toCredential, webSpherePlugin.configName, webSpherePlugin)
//            verifyExistenceOfPluginsCredentials(toProxyCredential, gitPlugin.configName + "_" + credsProxyFieldName, gitPlugin)
        }

        // ---------------------------------------------------------------

        when: "Delete plugin configuration"
        webSpherePlugin.deleteConfiguration(webSpherePlugin.configName)

        then: "Verify that there are no  credential and configuration after configuration deleting"
        verifyExistenceOfPluginsCredentials(Utils.CredsStates.DELETED, webSpherePlugin.configName, webSpherePlugin)
//        verifyExistenceOfPluginsCredentials(Utils.CredsStates.DELETED, gitPlugin.configName + '_' + credsProxyFieldName, gitPlugin)

        cleanup:
//         if test failed for some reason and configuration wasn't delete in the last step of test,
//         the configuration anyway will be deleted to not break next tests
        webSpherePlugin.deleteConfiguration(webSpherePlugin.configName)

        where:
        initCredential                  | initProxyCredential     | toCredential                    | toProxyCredential       | initTestConn | toTestConn | expResultOfCreatingConfig | expResultOfEditingConfig | initExpectedJobResult | afterEditingJobResult
        Utils.CredsStates.RUNTIME       | Utils.CredsStates.EMPTY | Utils.CredsStates.WRONG_RUNTIME | Utils.CredsStates.EMPTY | false        | false      | JobOutcome.SUCCESS        | JobOutcome.SUCCESS       | JobOutcome.SUCCESS    | JobOutcome.ERROR
        Utils.CredsStates.RUNTIME       | Utils.CredsStates.EMPTY | Utils.CredsStates.WRONG_RUNTIME | Utils.CredsStates.EMPTY | false        | true       | JobOutcome.SUCCESS        | JobOutcome.ERROR         | JobOutcome.SUCCESS    | JobOutcome.SUCCESS
        Utils.CredsStates.EMPTY         | Utils.CredsStates.EMPTY | Utils.CredsStates.RUNTIME       | Utils.CredsStates.EMPTY | false        | false      | JobOutcome.SUCCESS        | JobOutcome.SUCCESS       | JobOutcome.ERROR      | JobOutcome.SUCCESS
        Utils.CredsStates.WRONG_RUNTIME | Utils.CredsStates.EMPTY | Utils.CredsStates.RUNTIME       | Utils.CredsStates.EMPTY | true         | false      | JobOutcome.ERROR          | JobOutcome.SUCCESS       | JobOutcome.ERROR      | JobOutcome.SUCCESS
        Utils.CredsStates.WRONG_RUNTIME | Utils.CredsStates.EMPTY | Utils.CredsStates.RUNTIME       | Utils.CredsStates.EMPTY | false        | true       | JobOutcome.SUCCESS        | JobOutcome.SUCCESS       | JobOutcome.ERROR      | JobOutcome.SUCCESS
    }


}
