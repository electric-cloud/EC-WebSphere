package com.cloudbees.plugin.spec

import com.cloudbees.pdk.hen.ConfigurationHandling
import com.cloudbees.pdk.hen.JobOutcome
import com.cloudbees.pdk.hen.WebSphere
import spock.lang.Specification
import spock.lang.Unroll

class TestConfigurationSuite extends Specification {

    String wsUsername = System.getenv('WAS_USERNAME') ?: ""
    String wsPassword = System.getenv('WAS_PASSWORD') ?: ""
    String wsHost = System.getenv('WAS_HOST') ?: ""
    String wsPort = System.getenv('WAS_PORT') ?: ""
    String wsAdminPath = System.getenv('WSADMIN_PATH') ?: ""
    String wsConnType = System.getenv('WAS_CONNTYPE') ?: ""

    WebSphere webSpherePlugin = new WebSphere(
            name: 'EC-WebSphere',
            configurationHandling: ConfigurationHandling.NEW
    )

    @Unroll
    def "Sanity. Test new configuration"() {
        when:  "Run Plugin Procedure - TestConfiguration"
        def result = webSpherePlugin.testConfiguration
                                    .conntype(wsConnType)
                                    .wsadminabspath(wsAdminPath)
                                    .websphereurl(wsHost)
                                    .websphereport(wsPort)
                                    .testconnectionres(wsHost)
                                    .debug(true)
                                    .credential(wsUsername, wsPassword)
                                    .run()

        then: "Verify that procedure completed as expected"
        assert result.isSuccessful()
        assert result.getJobLog() =~ 'Connection succeeded'
    }

    @Unroll
    def "Negative. Test new configuration"() {
        when:  "Run Plugin Procedure - TestConfiguration"
        def result = webSpherePlugin.testConfiguration
                                    .conntype(wsConnType)
                                    .wsadminabspath(wsAdminPath)
                                    .websphereurl(wsHost)
                                    .websphereport(wsPort)
                                    .testconnectionres(wsHost)
                                    .debug(true)
                                    .credential(wsUsername, wsPassword + "sgdgdf")
                                    .run()

        then: "Verify that procedure completed as expected"
        assert result.outcome == JobOutcome.ERROR
        assert result.getJobLog() =~ 'Error occurred while trying to create the configuration'
    }
}
