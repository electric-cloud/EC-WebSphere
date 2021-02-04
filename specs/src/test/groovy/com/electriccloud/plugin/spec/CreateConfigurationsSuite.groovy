package com.electriccloud.plugin.spec

import jdk.nashorn.internal.objects.Global
import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import com.electriccloud.plugin.spec.PluginTestHelper
/**
 * following envs should be present:
 *WAS_USERNAME
 *WAS_PASSWORD
 *WAS_HOST
 *WAS_PORT
 *WAS_CONNTYPE
 *WAS_DEBUG
 *WSADMIN_PATH 
 *following envs should be present:
 */
@Stepwise
class CreateConfigurationsSuite extends PluginTestHelper {
    @Shared
    String resourceName
    @Shared
    String testProjectName = "CreateConfigurationTestProject"
    @Shared
    def external_credentials = [
            correct_cred_project: testProjectName,
            correct_cred_name: 'websphere_credential',
            incorrect_cred_project: testProjectName,
            incorrect_cred_name : 'websphere_credential_wrong'
    ]
    @Shared
    String correct_external_cred_path =
            "/projects/${external_credentials.correct_cred_project}/credentials/${external_credentials.correct_cred_name}"

    @Shared
    String incorrect_external_cred_path =
            "/projects/${external_credentials.incorrect_cred_project}/credentials/${external_credentials.incorrect_cred_name}"

    def doSetupSpec() {
        createProjectForCreateConfiguration(external_credentials.correct_cred_project)
        def username = System.getenv('WAS_USERNAME') ?: 'wsadmin'
        def password = System.getenv('WAS_PASSWORD') ?: 'changeme'
        def wrong_username = 'wcadmin'
        createCDCredential(
                external_credentials.correct_cred_project,
                external_credentials.correct_cred_name,
                username,
                password
        )
        createCDCredential(
                external_credentials.incorrect_cred_project,
                external_credentials.incorrect_cred_name,
                wrong_username,
                password
        )
        resourceName = createWebSphereResource()
    }

    def doCleanupSpec() {
    }

    String  conntype

    def "Creation of Configuration that has external credential - Positive" () {
        conntype = "SOAP"
        when: 'Creation Runs'
        def wasResourceName = System.getenv('WAS_HOST');
        createWorkspace(wasResourceName)
        def inputData = [
                websphere_url:       System.getenv('WAS_HOST'),
                websphere_port:      System.getenv('WAS_PORT') ?: '',

                conntype:            conntype,
                wsadminabspath:      System.getenv('WSADMIN_PATH') ?: '',
                test_connection:     1,
                credential:          external_credentials.correct_cred_name,
                test_connection_res: wasResourceName,
                debug: 1
        ]
        def credentialReferences = [
                "${external_credentials.correct_cred_name}": correct_external_cred_path
        ]
        def jobResult = createCustomConfigurationWithExternalCredentials("ConfigWithExternalCred", inputData, credentialReferences)
        then: 'Wait until job is completed.'
        def outcome = getJobProperty('/myJob/outcome', jobResult.jobId)
        assert outcome == "success"
    }


    def "Creation of Configuration that has external credential - Negative" () {
        conntype = "SOAP"
        when: 'Creation Runs'
        def wasResourceName = System.getenv('WAS_HOST');
        createWorkspace(wasResourceName)
        def inputData = [
                websphere_url:       System.getenv('WAS_HOST'),
                websphere_port:      System.getenv('WAS_PORT') ?: '',

                conntype:            conntype,
                wsadminabspath:      System.getenv('WSADMIN_PATH') ?: '',
                test_connection:     1,
                credential:          external_credentials.incorrect_cred_name,
                test_connection_res: wasResourceName,
                debug: 1
        ]
        def credentialReferences = [
                "${external_credentials.incorrect_cred_name}": incorrect_external_cred_path
        ]

        def jobResult = createCustomConfigurationWithExternalCredentials("ConfigWithExternalCred", inputData, credentialReferences)
        then: 'Wait until job is completed.'
        def outcome = getJobProperty('/myJob/outcome', jobResult.jobId)
        assert outcome == "error"
        def jobSummary = getJobProperty("/myJob/jobSteps/AttemptConnection/summary", jobResult.jobId)
        assert jobSummary =~ "Error occurred while trying to create the configuration. Check the job log for more details."
    }

    def "Creation SOAP configuration for testing - Positive" () {
        conntype = "SOAP"

        when: 'Creation Runs'
        def wasResourceName = System.getenv('WAS_HOST');
        createWorkspace(wasResourceName)
        def inputData = [
            username : System.getenv('WAS_USERNAME') ?: 'wsadmin',
            password : System.getenv('WAS_PASSWORD') ?: 'changeme',
            websphere_url : System.getenv('WAS_HOST'),
            websphere_port : System.getenv('WAS_PORT') ?: '',
            wsadminabspath : System.getenv('WSADMIN_PATH') ?: '',
            conntype: conntype,
            test_connection: 1,
            test_connection_res: resourceName,
            debug: 1
        ]
        println "Log: inputData:: $inputData"
        def confName = 'Web-Sphere-' + conntype;
        createCustomConfiguration(confName, inputData, [doNotRecreate: false])

        then: 'Wait until job is Completed:'
    }

    def "Creation None configuration for testing - Positive" () {
        conntype = "None"

        when: 'Creation Runs'
        def wasResourceName = System.getenv('WAS_HOST');
        createWorkspace(wasResourceName)
        def inputData =[
            username : System.getenv('WAS_USERNAME') ?: 'wsadmin',
            password : System.getenv('WAS_PASSWORD') ?: 'changeme',
            websphere_url : System.getenv('WAS_HOST'),
            websphere_port : System.getenv('WAS_PORT') ?: '',
            wsadminabspath : System.getenv('WSADMIN_PATH') ?: '',
            conntype: conntype,
            test_connection: 1,
            test_connection_res: resourceName,
            debug: 1
        ]
        println "Log: inputData:: $inputData"
        def confName = 'Web-Sphere-' + conntype;
        createCustomConfiguration(confName, inputData, [doNotRecreate: false])

        then: 'Wait until job is Completed:'
    }

    def "Creation RMI configuration for testing - Positive" () {
        conntype = "RMI"
        
        when: 'Creation Runs'
        def wasResourceName = System.getenv('WAS_HOST');
        createWorkspace(wasResourceName)
        def inputData =[
            username : System.getenv('WAS_USERNAME') ?: 'wsadmin',
            password : System.getenv('WAS_PASSWORD') ?: 'changeme',
            websphere_url : System.getenv('WAS_HOST'),
            websphere_port : System.getenv('WAS_PORT') ?: '',
            wsadminabspath : System.getenv('WSADMIN_PATH') ?: '',
            conntype: conntype,
            test_connection: 1,
            test_connection_res: resourceName,
            debug: 1
        ]
        println "Log: inputData:: $inputData"
        def confName = 'Web-Sphere-' + conntype;
        createCustomConfiguration(confName, inputData, [doNotRecreate: false])

        then: 'Wait until job is Completed:'
    }

    def "Creation JSR160RMI configuration for testing - Positive" () {
        conntype = "JSR160RMI"
        
        when: 'Creation Runs'
        def wasResourceName = System.getenv('WAS_HOST');
        createWorkspace(wasResourceName)
        def inputData =[
            username : System.getenv('WAS_USERNAME') ?: 'wsadmin',
            password : System.getenv('WAS_PASSWORD') ?: 'changeme',
            websphere_url : System.getenv('WAS_HOST'),
            websphere_port : System.getenv('WAS_PORT') ?: '',
            wsadminabspath : System.getenv('WSADMIN_PATH') ?: '',
            conntype: conntype,
            test_connection: 1,
            test_connection_res: resourceName,
            debug: 1
        ]
        println "Log: inputData:: $inputData"
        def confName = 'Web-Sphere-' + conntype;
        createCustomConfiguration(confName, inputData, [doNotRecreate: false])

        then: 'Wait until job is Completed:'
    }

    def "Creation IPC configuration for testing - Positive" () {
        conntype = "IPC"
        
        when: 'Creation Runs'
        def wasResourceName = System.getenv('WAS_HOST');
        createWorkspace(wasResourceName)
        def inputData =[
            username : System.getenv('WAS_USERNAME') ?: 'wsadmin',
            password : System.getenv('WAS_PASSWORD') ?: 'changeme',
            websphere_url : System.getenv('WAS_HOST'),
            websphere_port : System.getenv('WAS_PORT') ?: '',
            wsadminabspath : System.getenv('WSADMIN_PATH') ?: '',
            conntype: conntype,
            test_connection: 1,
            test_connection_res: resourceName,
            debug: 1
        ]
        println "Log: inputData:: $inputData"
        def confName = 'Web-Sphere-' + conntype;
        createCustomConfiguration(confName, inputData, [doNotRecreate: false])

        then: 'Wait until job is Completed:'
    }
}
