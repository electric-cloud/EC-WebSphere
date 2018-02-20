import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import PluginTestHelper

@Stepwise
class CheckApp extends PluginTestHelper {
    @Shared
    def testProjectName = 'EC-WebSphere-Specs-CheckApp'
    @Shared
    def testProcedureName = 'CheckApp'
    @Shared
    def сonfigName = 'specConfig'
    // params for where section
    @Shared
    def tNumber
    @Shared
    def wsApplicationNames = [
        nonExistApplication : 'Non-HelloWorld',
        existApplication: 'existHelloWorld',
        readyApplication: 'readyHelloWorld',
        runningApplication: ' runningHelloWorld'
    ]

    @Shared
    def wsApplicationStates = [
        notExist: 'NOT_EXISTS',
        exist: 'EXIST',
        notReady: 'NOT_READY',
        ready: 'READY',
        notRunning: 'NOT_RUNNING',
        running: 'RUNNING'
        
    ]
    @Shared
    def wsAdminAbsolutePathes = [
        empty: '',
        correct: '/opt/IBM/WebSphere/AppServer/profiles/AppSrv01/bin/wsadmin.sh',
        incorrect: '/opt/Incorrect/wsadmin.sh'
    ]

    // params ofr where section 
    @Shared
    def wsAdminAbsolutePath
    def wsApplicationName
    def wsApplicationState
    def tTime

   def doSetupSpec() {
        def wasResourceName = System.getenv('WAS_HOST');
        createWorkspace(wasResourceName)
        createConfiguration(сonfigName, [doNotRecreate: false])
        importProject(testProjectName, 'dsl/CheckApp/Procedure.dsl', [projectName: testProjectName, wasResourceName:wasResourceName])

        dsl 'setProperty(propertyName: "/plugins/EC-WebSphere/project/ec_debug_logToProperty", value: "/myJob/debug_logs")'
    }
    
    // def doCleanupSpec() {
    //     dsl "deleteProject '$testProjectName'"
    // }

    @Unroll
    def "Check Application Suite. Positive scenarios"(){

        when: 'Procedure runs'
        def wasResourceName=System.getenv('WAS_HOST');
        def runParams = [
            configName: wsConfigName,
            wsadminabspath: wsAdminAbsolutePath,
            applicationName: wsApplicationName,
            applicationState: wsApplicationState,
            wasResourceName: wasResourceName,
            waitTime: tTime
        ]
        def result = runProcedure(runParams)

        then: 'wait until job is completed:'
        waitUntil {
            try {
                jobCompleted(result)
            } catch (Exception e) {
                println e.getMessage()
            }
        }
        def outcome = getJobProperty('/myJob/outcome', result.jobId)
        def debugLog = getJobLogs(result.jobId)
        println "Procedure log:\n$debugLog\n"

        assert outcome == expectedOutcome

        where:
        wsConfigName    | wsAdminAbsolutePath           | wsApplicationName                      | wsApplicationState               | tTime    | expectedOutcome
        сonfigName      | wsAdminAbsolutePathes.empty   | wsApplicationNames.nonExistApplication | wsApplicationStates.notExist     | '0'      | 'success'
        сonfigName      | wsAdminAbsolutePathes.correct | wsApplicationNames.existApplication    | wsApplicationStates.exist        | '0'      | 'success'
        сonfigName      | wsAdminAbsolutePathes.empty   | wsApplicationNames.existApplication    | wsApplicationStates.notReady     | '0'      | 'success'
        сonfigName      | wsAdminAbsolutePathes.empty   | wsApplicationNames.existApplication    | wsApplicationStates.notRunning   | '0'      | 'success'
        сonfigName      | wsAdminAbsolutePathes.correct | wsApplicationNames.readyApplication    | wsApplicationStates.ready        | '0'      | 'success'
        сonfigName      | wsAdminAbsolutePathes.empty   | wsApplicationNames.readyApplication    | wsApplicationStates.notRunning   | '0'      | 'success'
        сonfigName      | wsAdminAbsolutePathes.empty   | wsApplicationNames.runningApplication  | wsApplicationStates.exist        | '0'      | 'success'
        сonfigName      | wsAdminAbsolutePathes.correct | wsApplicationNames.runningApplication  | wsApplicationStates.ready        | '0'      | 'success'
        сonfigName      | wsAdminAbsolutePathes.empty   | wsApplicationNames.runningApplication  | wsApplicationStates.running      | '100'    | 'success'
   }

    @Unroll
    def "Check Application Suite. Negative scenarios"(){

        when: 'Procedure runs'
        def wasResourceName=System.getenv('WAS_HOST');
        def runParams = [
            configName: wsConfigName,
            wsadminabspath: wsAdminAbsolutePath,
            applicationName: wsApplicationName,
            applicationState: wsApplicationState,
            wasResourceName: wasResourceName,
            waitTime: tTime
        ]
        def result = runProcedure(runParams)

        then: 'wait until job is completed:'
        waitUntil {
            try {
                jobCompleted(result)
            } catch (Exception e) {
                println e.getMessage()
            }
        }
        def outcome = getJobProperty('/myJob/outcome', result.jobId)
        def debugLog = getJobLogs(result.jobId)
        println "Procedure log:\n$debugLog\n"

        assert outcome == expectedOutcome

        where:
        wsConfigName            | wsAdminAbsolutePath               | wsApplicationName                         | wsApplicationState                | tTime      | expectedOutcome
        'specConfig-Incorrect'  | wsAdminAbsolutePathes.empty       | wsApplicationNames.existApplication       | wsApplicationStates.exist         | '0'        | 'error'
        сonfigName              | wsAdminAbsolutePathes.incorrect   | wsApplicationNames.existApplication       | wsApplicationStates.exist         | '0'        | 'error'
        сonfigName              | wsAdminAbsolutePathes.empty       | wsApplicationNames.existApplication       | wsApplicationStates.notExist      | '0'        | 'error'
        сonfigName              | wsAdminAbsolutePathes.empty       | wsApplicationNames.nonExistApplication    | wsApplicationStates.exist         | '0'        | 'error'
        сonfigName              | wsAdminAbsolutePathes.correct     | wsApplicationNames.existApplication       | wsApplicationStates.ready         | '0'        | 'error'
        сonfigName              | wsAdminAbsolutePathes.empty       | wsApplicationNames.readyApplication       | wsApplicationStates.notReady      | '0'        | 'error'
        сonfigName              | wsAdminAbsolutePathes.empty       | wsApplicationNames.runningApplication     | wsApplicationStates.notRunning    | '0'        | 'error'
        сonfigName              | wsAdminAbsolutePathes.correct     | wsApplicationNames.readyApplication       | wsApplicationStates.running       | '0'        | 'error'
        сonfigName              | wsAdminAbsolutePathes.empty       | wsApplicationNames.runningApplication     | wsApplicationStates.running       | '-1'       | 'error'
        сonfigName              | wsAdminAbsolutePathes.empty       | wsApplicationNames.runningApplication     | wsApplicationStates.running       | 'abs'      | 'error'
   }

    def runProcedure(def parameters) {
        def code = """
                runProcedure(
                    projectName: '$testProjectName',
                    procedureName: '$testProcedureName',
                    actualParameter: [
                        configName:       '$parameters.configName',
                        applicationName:  '$parameters.applicationName',
                        applicationState: '$parameters.applicationState',
                        wasResourceName:  '$parameters.wasResourceName',
                        waitTime:         '$parameters.propertyFormat'
                    ]
                )
        """
        return dsl(code)
    }

    /*
    def checkLogOutputByNumber(def log, def number, def context) {
        if (number == 1) {
            return log =~ 'Unable to find the credential'
        }
        else if (number == 2) {
            return log =~ "Jenkins job $context.jobName does not exist."
        }
        else if (number == 3) {
            return log =~ "Build #$context.buildNumber does not exist in existing Jenkins $context.jobName job."
        }
        else if (number == 4) {
            return log =~ "Build #$context.buildNumber does not exist in existing Jenkins $context.jobName job."
        }
        else if (number == 5) {
            return log =~ "Can't set property: Unrecognized path element in '$context.propertyPath"
        }
        else if (number == 6) {
            return log =~ "Can't set property: java.util.UnknownFormatConversionException: Conversion ="
        }
    }
    */

}
