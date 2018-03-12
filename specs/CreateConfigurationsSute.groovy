import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import PluginTestHelper

@Stepwise
class CreateConfigurationsSute extends PluginTestHelper {
    def doSetupSpec() {
    }

    def doCleanupSpec() {
    }
    String  conntype
    def "Creation SOAP configuration for testing - Positive" () {
        conntype = "SOAP"

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
            debug: 1
        ]
        println "Log: inputData:: $inputData"
        def confName = 'Web-Sphere-' + conntype;
        createCustomConfiguration(confName, inputData, [doNotRecreate: false])

        then: 'Wait until job is Completed:'
    }
}