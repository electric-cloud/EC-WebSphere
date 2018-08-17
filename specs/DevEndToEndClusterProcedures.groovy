import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import PluginTestHelper

@IgnoreIf({ System.getenv('IS_WAS_ND') != "1"})
@Stepwise
class DevEndToEndClusterProcedures extends PluginTestHelper {

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
    def wasClusterName = 'cluster1'
    @Shared
    def wasSourceNodeName = wasHost + 'Node01'
    @Shared
    def wasSourceNodeNameConvert = wasSourceNodeName
    @Shared
    def wasSourceServerName = 'server1'
    @Shared
    def wasSourceServerNameConvert = 'server2'
    @Shared
    def wasTargetNodeName = wasSourceNodeNameConvert
    @Shared
    def wasTargetServerName = wasSourceServerNameConvert
    @Shared
    def wasTemplateName = 'default'
    

    /**
     * Dsl Parameters
     */

    @Shared
    def testProjectName =           'EC-WebSphere-SystemTests'
    @Shared
    def testProcedureName =         'DevEndToEndClusterProcedures'

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
        importProject(testProjectName, 'dsl/DevEndToEndClusterProcedures/Procedure.dsl', [projectName: testProjectName, wasResourceName:wasResourceName])
        dsl 'setProperty(propertyName: "/plugins/EC-WebSphere/project/ec_debug_logToProperty", value: "/myJob/debug_logs")'
     }

    /**
     * Clean Up actions after test will finished
     */

    def doCleanupSpec() {

    }

    @Unroll
    def "Dev cluster related end to end scenario" () {
        when: 'Procedure runs: '
        def runParams = [
            wasHost:    wasHost,
            configName: pluginConfigurationNames.correctSOAP,
            clusterName: wasClusterName,
            
            sourceNodeName: wasSourceNodeName,
            sourceServerName: wasSourceServerName,
            
            sourceNodeNameConvert  : wasSourceNodeNameConvert,
            sourceServerNameConvert: wasSourceServerNameConvert,
            
            targetNodeName: wasTargetNodeName,
            targetServerName: wasTargetServerName,
            
            templateName: wasTemplateName,

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
        // 1 Create application server
        def summary1 = getJobProperty("/myJob/jobSteps/CreateApplicationServer/summary", result.jobId)
        assert summary1.contains("Application server $wasSourceServerNameConvert has been created on node $wasSourceNodeNameConvert")
        
        // 2 Create cluster and convert server2 to be a first cluster member and add 3 servers
        def summary2 = getJobProperty("/myJob/jobSteps/CreateCluster/summary", result.jobId)
        assert summary2.contains("Cluster $wasClusterName has been created")
        assert summary2.contains("Server $wasSourceServerNameConvert on node $wasSourceNodeNameConvert has been converted to be the first member of cluster $wasClusterName")
        assert summary2 =~ 'Server .*? on node .*? has been created and added as cluster member'
        
        // 3 DeleteCluster
        def summary3 = getJobProperty("/myJob/jobSteps/DeleteCluster/summary", result.jobId)
        assert summary3.contains("Cluster $wasClusterName has been deleted")
        
        // 4 create cluster again
        def summary4 = getJobProperty("/myJob/jobSteps/CreateCluster2/summary", result.jobId)
        assert summary4.contains("Cluster $wasClusterName has been created")
        assert summary4.contains("First cluster member $wasSourceServerNameConvert has been created on node $wasSourceNodeNameConvert from template $wasTemplateName")
        
        // 5 add cluster members
        def summary5 = getJobProperty("/myJob/jobSteps/CreateClusterMembers/summary", result.jobId)
        assert summary5 =~ 'Server .*? on node .*? has been created and added';
        
        // 6 is delete server, no reason to check it right now
        
        // 7 Create cluster with 1st member using existing server as template
        def summary7 = getJobProperty("/myJob/jobSteps/CreateCluster3/summary", result.jobId)
        assert summary7.contains("Cluster $wasClusterName has been created");
        assert summary7.contains("First cluster member $wasSourceServerNameConvert has been created on node $wasSourceNodeNameConvert using server $wasSourceServerName on node $wasSourceNodeName as source")

        // 8 Delete cluster again

        // 9 Create empty cluster
        def summary9 = getJobProperty("/myJob/jobSteps/CreateEmptyCluster/summary", result.jobId)
        assert summary9.contains("Cluster $wasClusterName has been created")

        // 10 Create 1st cluster member
        def summary10 = getJobProperty("/myJob/jobSteps/CreateFirstClusterMember/summary", result.jobId)
        assert summary10.contains("First cluster member $wasSourceServerNameConvert has been created on $wasSourceNodeNameConvert node and added to cluster $wasClusterName using $wasTemplateName template")

        // 11 Create cluster members
        def summary11 = getJobProperty("/myJob/jobSteps/CreateClusterMembers2/summary", result.jobId)
        assert summary11 =~ 'Server .*? on node .*? has been created and added'

        // 12 Delete cluster

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
                    wasClusterName: '$parameters.clusterName',

                    wasSourceNodeName: '$parameters.sourceNodeName',
                    wasSourceServerName: '$parameters.sourceServerName',

                    wasSourceNodeNameConvert: '$parameters.sourceNodeNameConvert',
                    wasSourceServerNameConvert: '$parameters.sourceServerNameConvert',

                    wasTargetNodeName:'$parameters.targetNodeName',
                    wasTargetServerName: '$parameters.targetServerName',

                    wasTemplateName: '$parameters.templateName',
                ]
            )
        """
        return dslWithTimeout(code)
    }
}
