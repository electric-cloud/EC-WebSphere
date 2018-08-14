import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import PluginTestHelper


@IgnoreIf({ System.getenv('IS_WAS_ND') != "1"})
@Stepwise
class DeleteApplicationServerNegative extends PluginTestHelper {

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

    /**
     * Dsl Parameters
     */

    @Shared
    def testProjectName =           'EC-WebSphere-SystemTests'
    @Shared
    def testProcedureName =         'DeleteApplicationServer'

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

    @Shared
    def nodeNames = [
        correct: wasHost + 'Node01',
        incorrect: wasHost + 'Node01WRONG'
    ]
    @Shared
    def serverNames = [
        correct: 'server2',
        incorrect: 'server-that-does-not-exist'
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

    @Shared
    def exs = [
        wrongConfig: "Configuration '$pluginConfigurationNames.incorrect' doesn't exist",
        wrongNode: "Failed to delete application server $serverNames.correct on node $nodeNames.incorrect",
        wrongServer: "Failed to delete application server $serverNames.incorrect on node $nodeNames.correct",
        allWrong: "Failed to delete application server $serverNames.incorrect on node $nodeNames.incorrect",
    ]

    /**
     * Test Parameters: for Where section 
     */ 
    /// cfgName | ASName | MST | expectedOutcome
    def expectedOutcome
    def cfgName;
    def serverName;
    def nodeName;
    def archivePath

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
        importProject(testProjectName, 'dsl/DeleteApplicationServer/Procedure.dsl', [projectName: testProjectName, wasResourceName:wasResourceName])
        dsl 'setProperty(propertyName: "/plugins/EC-WebSphere/project/ec_debug_logToProperty", value: "/myJob/debug_logs")'
     }

    /**
     * Clean Up actions after test will finished 
     */

    def doCleanupSpec() {

    }

    /**
     * Negative Scenarios
     */

    @Unroll
    def "DeleteApplicationServer. Negative scenario." () {
        when: 'Procedure runs: '
            def runParams = [
                wasHost:    wasHost,
                configName: cfgName,
                nodeName: nodeName,
                serverName: serverName
            ]
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


        assert outcome == "error"
        def jobSummary = getJobProperty('/myJob/jobSteps/DeleteApplicationServer/summary', result.jobId)
        assert jobSummary.contains(expectedJobSummary)
        where: 'The following params will be: '
            cfgName                              | nodeName            | serverName            | expectedJobSummary
            pluginConfigurationNames.incorrect   | nodeNames.correct   | serverNames.correct   | exs.wrongConfig
            pluginConfigurationNames.correctSOAP | nodeNames.incorrect | serverNames.correct   | exs.wrongNode
            pluginConfigurationNames.correctSOAP | nodeNames.correct   | serverNames.incorrect | exs.wrongServer
            pluginConfigurationNames.correctSOAP | nodeNames.incorrect | serverNames.incorrect | exs.allWrong

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
                    wasNodeName:     '$parameters.nodeName',
                    wasServerName:   '$parameters.serverName'

                ]
            )
        """
        return dslWithTimeout(code)
    }
}
