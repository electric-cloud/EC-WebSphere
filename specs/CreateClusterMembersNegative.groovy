import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import PluginTestHelper


// @IgnoreIf({ System.getenv('IS_WAS_ND') != "1"})
// Ignoring this for 2.5.0 release.
@Ignore
@Stepwise
class CreateClusterMembersNegative extends PluginTestHelper {

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


    /**
     * Dsl Parameters
     */

    @Shared
    def testProjectName =           'EC-WebSphere-SystemTests'
    @Shared
    def testProcedureName =         'CreateClusterMembers'

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
    def clusterNames = [
        incorrect: 'cluster-that-does-not-exist',
        correct: 'cluster1'
    ]
    @Shared
    def membersLists = [
        correct: "$nodeNames.correct:server2c1, $nodeNames.correct:server3c1",
        incorrect: 'totally wrong list',
        incorrect2: "$nodeNames.correct:server1"
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
        wrongCluster: "Cannot find cluster $clusterNames.incorrect",
        wrongList: "Expected nodename:servername record, got"
    ]

    /**
     * Test Parameters: for Where section 
     */ 
    /// cfgName | ASName | MST | expectedOutcome
    def expectedOutcome
    def cfgName;
    def clusterName;
    def membersList;

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
        importProject(testProjectName, 'dsl/CreateClusterMembers/Procedure.dsl', [projectName: testProjectName, wasResourceName:wasResourceName])
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
    def "CreateClusterMembers. Negative scenario." () {
        when: 'Procedure runs: '
            def runParams = [
            wasHost:    wasHost,
            configName: cfgName,
            clusterName: clusterName,
            clusterMembers: membersList
            
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
        def jobSummary = getJobProperty('/myJob/jobSteps/CreateClusterMembers/summary', result.jobId)
        assert jobSummary.contains(expectedJobSummary)
        where: 'The following params will be: '
        cfgName                              | clusterName            | membersList             | expectedJobSummary
        pluginConfigurationNames.incorrect   | clusterNames.correct   | membersLists.correct    | exs.wrongConfig
        pluginConfigurationNames.correctSOAP | clusterNames.incorrect | membersLists.correct    | exs.wrongCluster
        pluginConfigurationNames.correctSOAP | clusterNames.correct   | membersLists.incorrect  | exs.wrongList
        pluginConfigurationNames.correctSOAP | clusterNames.incorrect | membersLists.incorrect2 | exs.wrongCluster
        

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
                    wasResourceName:   '$parameters.wasHost',
                    wasConfigName:     '$parameters.configName',
                    wasClusterName:    '$parameters.clusterName',
                    wasClusterMembers: '$parameters.clusterMembers'

                ]
            )
        """
        return dslWithTimeout(code)
    }
}
