package com.electriccloud.plugin.spec

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

    def doSetupSpec() {
        // TODO: Move credentials from Default project to the project that has been created specially for CreateConfiguration tests.
        def username = System.getenv('WAS_USERNAME') ?: 'wsadmin'
        def password = System.getenv('WAS_PASSWORD') ?: 'changeme'
        createCDCredential("Default", "websphere_credential", username, password)
        resourceName = createWebSphereResource()
    }

    def doCleanupSpec() {
    }

    String  conntype
    @IgnoreRest
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
                credential: 'websphere_credential',
                test_connection_res: wasResourceName,
                debug: 1
        ]
        def credentialReferences = [
                websphere_credential: "/projects/Default/credentials/websphere_credential"
        ]
        createCustomConfigurationWithExternalCredentials("ConfigWithExternalCred", inputData, credentialReferences)
        then: 'Wait until job is completed.'
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
