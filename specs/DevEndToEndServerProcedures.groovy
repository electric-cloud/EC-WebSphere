import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import PluginTestHelper


@IgnoreIf({ System.getenv('IS_WAS_ND') != "1"})
@Stepwise
class DevEndToEndServerProcedures extends PluginTestHelper {

    /**
     * Environments Variables
     */ 

    @Shared 
    def wasUserName =   System.getenv('WAS_USERNAME')
    @Shared 
    def wasPassword =   System.getenv('WAS_PASSWORD')
    @Shared 
    def wasHost =       System.getenv('WAS_HOST')
    @Shared
    def wasPort =       System.getenv('WAS_PORT')
    @Shared
    def wasConnType =   System.getenv('WAS_CONNTYPE')
    @Shared
    def wasDebug =      System.getenv('WAS_DEBUG')
    @Shared
    def wasPath =       System.getenv('WSADMIN_PATH')
    @Shared
    def wasAppPath =    System.getenv('WAS_APPPATH')
    @Shared
    def wasStartScript = System.getenv('WAS_START_DM_SCRIPT')
    @Shared
    def wasStopScript  = System.getenv('WAS_STOP_DM_SCRIPT')
    @Shared
    def wasTargetNodeName = wasHost + 'Node01'
    @Shared
    def wasTargetServerName  = 'server1-td'
    @Shared
    def wasSourceNodeName = wasHost + 'Node01'
    @Shared
    def wasSourceServerName = 'server1'
    @Shared
    def wasTemplateName = 'template-1-td'
    // TODO: change it for windows
    def wasArchivePath = '/tmp/server1.car'

    /**
     * Dsl Parameters
     */

    @Shared
    def testProjectName =           'EC-WebSphere-SystemTests'
    @Shared
    def testProcedureName =         'DevEndToEndServerProcedures'

    /**
     * Procedure Values: test parameters Procedure values
    */
    
    @Shared     // Required Parameter (need incorrect and empty value)
    def pluginConfigurationNames = [
        empty:              '',
        correctSOAP:        'Web-Sphere-SOAP',
        correctIPC:         'Web-Sphere-IPC',
        correctJSR160RMI:   'Web-Sphere-JSR160RMI',
        correctNone:        'Web-Sphere-None',
        correctRMI:         'Web-Sphere-RMI', 
        incorrect: 	        'incorrect config Name',
    ]


    /**
     * Verification Values: Assert values 
    */

    @Shared
    def expectedOutcomes = [
        success: 	'success',
        error: 		'error',
        warning: 	'warning',
        running: 	'running',
    ]
    
    
    @Shared expectedJobDetailedResults = [
        empty: '',
    ]


    /**
     * Test Parameters: for Where section 
     */ 
    /// cfgName | ASName | MST | expectedOutcome
    def expectedOutcome
    def expectedStartSummaryMessage
    def expectedStopSummaryMessage
    def expectedJobDetailedResult
    def cfgName;

    /**
     * Preparation actions
     */

     def doSetupSpec() {
        def wasResourceName = wasHost
        createWorkspace(wasResourceName)
        createConfiguration(pluginConfigurationNames.correctSOAP, [doNotRecreate: true])
        createConfiguration(pluginConfigurationNames.correctIPC, [doNotRecreate: true])
        createConfiguration(pluginConfigurationNames.correctJSR160RMI, [doNotRecreate: true])
        createConfiguration(pluginConfigurationNames.correctNone, [doNotRecreate: true])
        createConfiguration(pluginConfigurationNames.correctRMI, [doNotRecreate: true])
        // actual test project
        importProject(testProjectName, 'dsl/DevEndToEndServerProcedures/Procedure.dsl', [projectName: testProjectName, wasResourceName:wasResourceName])
        dsl 'setProperty(propertyName: "/plugins/EC-WebSphere/project/ec_debug_logToProperty", value: "/myJob/debug_logs")'
     }

    /**
     * Clean Up actions after test will finished
     */

    def doCleanupSpec() {

    }

    @Unroll
    def "Dev server related end to end scenario" () {
        when: 'Procedure runs: '
             def runParams = [
                wasHost:    wasHost,
                configName: pluginConfigurationNames.correctSOAP,
                sourceNodeName: wasSourceNodeName,
                sourceServerName: wasSourceServerName,
                templateName: wasTemplateName,
                archivePath: wasArchivePath,
                targetNodeName: wasTargetNodeName,
                targetServerName: wasTargetServerName
        ]
        print "Run params:"
        print runParams
            def result = runProcedure(runParams)

        then: 'Wait until job is completed: '
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


            assert outcome == "success"
            // 1 create application template
            def summary1 = getJobProperty("/myJob/jobSteps/CreateApplicationServerTemplate/summary", result.jobId)
            assert summary1.contains("Application Server template $wasTemplateName has been created")
            // 2 export server
            def summary2 = getJobProperty("/myJob/jobSteps/ExportApplicationServer/summary", result.jobId)
            assert summary2.contains("Application server $wasSourceServerName from node $wasSourceNodeName has been exported")
            // 3 import server
            def summary3 = getJobProperty("/myJob/jobSteps/ImportApplicationServer/summary", result.jobId)
            assert summary3.contains("Application server $wasTargetServerName has been imported to node $wasTargetNodeName")
            // 4 start server
            def summary4 = getJobProperty("/myJob/jobSteps/StartApplicationServers/summary", result.jobId)
            assert summary4.contains("Node: $wasTargetNodeName, Server: $wasTargetServerName, State: STARTED")
            // 5 stop server
            def summary5 = getJobProperty("/myJob/jobSteps/StopApplicationServers/summary", result.jobId)
            assert summary5.contains("Node: $wasTargetNodeName, Server: $wasTargetServerName, State: Stopped")
            // 6 delete server
            def summary6 = getJobProperty("/myJob/jobSteps/DeleteApplicationServer/summary", result.jobId)
            assert summary6.contains("Server $wasTargetServerName on node $wasTargetNodeName has been deleted")
            // 7 create application template
            def summary7 = getJobProperty("/myJob/jobSteps/CreateApplicationServer/summary", result.jobId)
            assert summary7.contains("Application server $wasTargetServerName has been created on node $wasTargetNodeName")
            // 8 delete server
            def summary8 = getJobProperty("/myJob/jobSteps/DeleteApplicationServer2/summary", result.jobId)
            assert summary8.contains("Server $wasTargetServerName on node $wasTargetNodeName has been deleted")
            // 9 delete template
            def summary9 = getJobProperty("/myJob/jobSteps/DeleteApplicationServrTemplate/summary", result.jobId)
            assert summary9.contains("Application server template $wasTemplateName has been deleted")
    }


    /**
     * Extended Scenarios
     */

    //@Unroll
    //def "" ()



    //Run Test Procedure
    def runProcedure(def parameters) {
        def code = """
            runProcedure(
                projectName:                    '$testProjectName',
                procedureName:                  '$testProcedureName',
                actualParameter: [
                    wasResourceName: '$parameters.wasHost',
                    wasConfigName:   '$parameters.configName',
                    wasSourceNodeName: '$parameters.sourceNodeName',
                    wasSourceServerName: '$parameters.sourceServerName',
                    wasTemplateName: '$parameters.templateName',
                    wasArchivePath: '$parameters.archivePath',
                    wasTargetNodeName:'$parameters.targetNodeName',
                    wasTargetServerName: '$parameters.targetServerName'
                ]
            )
        """
        return dslWithTimeout(code)
    }
}
