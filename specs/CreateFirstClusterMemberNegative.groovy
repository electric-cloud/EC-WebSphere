import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import PluginTestHelper


@IgnoreIf({ System.getenv('IS_WAS_ND') != "1"})
@Stepwise
class CreateFirstClusterMemberNegative extends PluginTestHelper {

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


    /**
     * Dsl Parameters
     */

    @Shared
    def testProjectName =           'EC-WebSphere-SystemTests'
    @Shared
    def testProcedureName =         'CreateFirstClusterMember'

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
    // clusterNames
    cNames = [
        correct: 'cluster1',
        incorrect: '}<<*(.^.||.^.)*>>{',
    ]

    @Shared
    // clusterMembersLists
    cml = [
        correct    : "$nodeNames.correct:server2c1, $nodeNames.correct:server3c1",
        incorrect  : "$nodeNames.correct:server1",
        wrongFormat: 'totally wrong list'
    ]

    @Shared
    //firstClusterMemberNames
    fNames = [
        correct: 'server1c1',
        incorrect: cNames.incorrect,
        existing: 'server1',
        missing:  'server1-does-not-exist'
    ]

    @Shared
    //firstClusterMemberNodes
    fNodes = [
        correct: wasHost + 'Node01',
        incorrect: wasHost + 'Node01WRONG'
    ]

    @Shared
    //firstClusterMemberTemplates
    fTemplates = [
        correct: 'default',
        incorrect: 'template-that-does-not-exist'
    ]


    @Shared
    sourceServerNames = [
        correct:   "$fNodes.correct:$fNames.existing",
        missing:   "$fNodes.correct:$fNames.missing",
        incorrect: "$fNodes.wrong:$fNames.missing"
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

        wrongClusterName: "Cannot find cluster $cNames.incorrect",
        missingServer: "Cannot find server $fNames.missing on node $fNodes.correct",
        missingCluster: "Cannot find cluster $cNames.correct",
        
        missingTemplate: "Cannot find server template $fTemplates.incorrect",
        missingServerNode: "First Member Name and First Member Node should be provided when create 1st cluster member is chosen",
        missingNode: "Cannot find node $fNodes.incorrect",
        wrongPromotionPolicy: "Creation Policy should be existing or template",
        serverAlreadyExist: "Server $fNames.existing already exists on node $fNodes.correct",
        missingMembersList: "No members to add",
        wrongMemberList: "Expected nodename:servername record, got $cml.wrongFormat",
    ]

    /**
     * Test Parameters: for Where section 
     */ 
    /// cfgName | ASName | MST | expectedOutcome
    def expectedOutcome
    def cfgName;
    def clusterName;
    def acm;
    def clusterMembersList;
    def cfm;
    def creationPolicy;
    def fName
    def fNode
    def fTemplate
    def promotionPolicy
    def sourceServerName
    def expectedJobSummary

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
        importProject(testProjectName, 'dsl/CreateFirstClusterMember/Procedure.dsl', [projectName: testProjectName, wasResourceName:wasResourceName])
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
    def "CreateCluster. Negative scenario." () {
        when: 'Procedure runs: '
        def runParams = [
            wasHost:                          wasHost,
            configName:                       cfgName,
            clusterName:                      clusterName,
            firstClusterMemberCreationPolicy: creationPolicy,
            firstClusterMemberName:           fName,
            firstClusterMemberNode:           fNode,
            firstClusterMemberTemplateName:   fTemplate,
            serverResourcesPromotionPolicy:   promotionPolicy,
            sourceServerName:                 sourceServerName,
            
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
        def jobSummary = getJobProperty('/myJob/jobSteps/CreateFirstClusterMember/summary', result.jobId)
        assert jobSummary.contains(expectedJobSummary)
        where: 'The following params will be: '
        cfgName                              |clusterName     |creationPolicy|fName           |fNode           |fTemplate           |promotionPolicy|sourceServerName           | expectedJobSummary
        pluginConfigurationNames.incorrect   |cNames.correct  |'existing'    |fNames.incorrect|fNodes.incorrect|''                  |'cluster'      |sourceServerNames.incorrect| exs.wrongConfig
        pluginConfigurationNames.correctSOAP |cNames.incorrect|'existing'    |''              |''              |''                  |'cluster'      |sourceServerNames.correct  | exs.wrongClusterName
        pluginConfigurationNames.correctSOAP |cNames.correct  |'existing'    |''              |''              |''                  |'cluster'      |sourceServerNames.correct  | exs.missingCluster
        // pluginConfigurationNames.correctSOAP |cNames.correct  |'existing'    |fNames.correct  |fNodes.correct  |''                  |'cluster'      |sourceServerNames.incorrect| exs.missingCluster
        // pluginConfigurationNames.correctSOAP |cNames.correct  |'template'    |fNames.correct  |fNodes.correct  |fTemplates.incorrect|'cluster'      |sourceServerNames.correct  | exs.missingCluster
        // pluginConfigurationNames.correctSOAP |cNames.correct  |'template'    |fNames.correct  |fNodes.incorrect|fTemplates.correct  |'cluster'      |sourceServerNames.correct  | exs.missingCluster
        pluginConfigurationNames.correctSOAP |cNames.incorrect|'existing'    |fNames.existing |fNodes.correct  |fTemplates.correct  |'cluster'      |sourceServerNames.incorrect| exs.wrongClusterName
        pluginConfigurationNames.correctSOAP |cNames.correct  |'convert'     |fNames.incorrect|fNodes.incorrect|fTemplates.incorrect|'cluster'      |sourceServerNames.missing  | exs.wrongPromotionPolicy
        // pluginConfigurationNames.correctSOAP |cNames.correct  |'existing'    |''              | ''             |''                  |'cluster'      |sourceServerNames.incorrect| exs.missingServerNode

        
        /*
         1. Wrong config
         2. Wrong cluster name
         3. Wrong member creation policy
         4. Missing source server
         5. Missing Template
         6. Wrong node
         7. Wrong Server (existing)
         8. Wrong resource promotion policy
         */
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
                    wasResourceName:                     '$parameters.wasHost',
                    wasConfigName:                       '$parameters.configName',
                    wasClusterName:                      '$parameters.clusterName',

                    wasFirstMemberCreationPolicy: '$parameters.firstClusterMemberCreationPolicy',
                    wasFirstMemberName:           '$parameters.firstClusterMemberName',
                    wasFirstMemberNode:           '$parameters.firstClusterMemberNode',
                    wasFirstClusterMemberTemplateName:   '$parameters.firstClusterMemberTemplateName',
                    wasServerResourcesPromotionPolicy:   '$parameters.serverResourcesPromotionPolicy',
                    wasSourceServerName:                 '$parameters.sourceServerName'
                ]
            )
        """
        return dslWithTimeout(code)
    }
}
