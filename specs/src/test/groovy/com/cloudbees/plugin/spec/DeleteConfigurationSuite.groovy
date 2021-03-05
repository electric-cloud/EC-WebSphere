package com.cloudbees.plugin.spec

import com.cloudbees.pdk.hen.Credential
import com.cloudbees.pdk.hen.JobOutcome
import com.cloudbees.pdk.hen.ServerHandler
import com.cloudbees.pdk.hen.Utils
import com.cloudbees.pdk.hen.WebSphere
import com.cloudbees.pdk.hen.procedures.WebSphereConfig
import com.electriccloud.plugins.annotations.Sanity
import spock.lang.Requires
import spock.lang.Specification
import spock.lang.Unroll

import static com.cloudbees.pdk.hen.Utils.setConfigurationValues
import static com.cloudbees.pdk.hen.Utils.setConfigurationValues
import static com.cloudbees.pdk.hen.Utils.setConfigurationValues
import static com.cloudbees.pdk.hen.Utils.verifyExistenceOfPluginsCredentials
import static com.cloudbees.pdk.hen.Utils.verifyExistenceOfPluginsCredentials
import static com.cloudbees.pdk.hen.Utils.verifyExistenceOfPluginsCredentials
import static com.cloudbees.pdk.hen.Utils.verifyExistenceOfPluginsCredentials

class DeleteConfigurationSuite extends Specification{

    static ServerHandler serverHandler = ServerHandler.getInstance()

    static final String wsUsername = System.getenv("WAS_USERNAME") ?: ""
    static final String wsPassword = System.getenv("WAS_PASSWORD") ?: ""

    static final String wsHost = System.getenv('WAS_HOST') ?: ""
    static final String wsAgentPort =  System.getenv('WEBSPHERE_RESOURCE_PORT') ?: '7808'
    static final String wsAdminPath = System.getenv('WSADMIN_PATH')
    static final String wasPort = System.getenv('WAS_PORT')

    static String cdFlowProject = "WebSphereDeleteConfigurationProject"


    def setupSpec() {
        // Create credential in the project
        serverHandler.dsl("createProject([projectName: '${cdFlowProject}', ])")
        serverHandler.setupResource(wsHost, wsHost, wsAgentPort.toInteger())

    }

    def cleanupSpec() {
        serverHandler.dsl("deleteProject([projectName: '${cdFlowProject}', ])")
    }

    @Sanity
    @Requires({!System.getenv("EF_PROXY_URL")})
    @Unroll
    def "Sanity without proxy. Delete Configuration"() {
        when: "Create plugin config"
        WebSphere webSpherePlugin = WebSphere.createWithoutConfig()
        WebSphereConfig webSphereConfig = WebSphereConfig.create(webSpherePlugin)

        webSphereConfig.wsadminabspath(wsAdminPath)
                .conntype("SOAP")
                .websphereurl(wsHost)
                .websphereport(System.getenv('WAS_PORT'))
                .testconnection(true)
                .testconnectionres(wsHost)
                .debug("1")


        setConfigurationValues(Utils.CredsStates.RUNTIME, "credential", new Credential(userName: wsUsername, password: wsPassword), webSphereConfig)
//        setConfigurationValues(initProxyCredential, credsProxyFieldName, proxyCredsByState[initProxyCredential], gitConfig)
        webSpherePlugin.configure(webSphereConfig)

        and: "Run Plugin Procedure - checkNodeStatus"

        def result = webSpherePlugin.checkNodeStatus
                .wsadminAbsPath(wsAdminPath)
        // TODO: currently we don't have license for the default environment (v90nd)
        // TODO: and I wrote tests for v90s, pattern of node name can be different from {wshost + "Node1"}
                .nodeName(wsHost + "Node01")
                .successCriteria("ALL_RUNNING")
                .run()

        then: "Verify that procedure completed as expected and credentials in plugin exist or not exist"
        assert result.isSuccessful()

        when: "Delete  Configuration "
        webSpherePlugin.deleteConfiguration(webSpherePlugin.configName)

        and: "run plugin procedure again without configuration"
        def secondResult = webSpherePlugin.checkNodeStatus
                .run()

        then: "Verify that procedure fails"
        assert secondResult.outcome == JobOutcome.ERROR
        assert secondResult.jobLog =~ "Configuration '${webSpherePlugin.configName}' doesn't exist"

        cleanup:
//         if test failed for some reason and configuration wasn't delete in the last step of test,
//         the configuration anyway will be deleted to not break next tests
        webSpherePlugin.deleteConfiguration(webSpherePlugin.configName)

        where:
        TC | description
        "1"| "DeleteConfiguration"
    }

}
