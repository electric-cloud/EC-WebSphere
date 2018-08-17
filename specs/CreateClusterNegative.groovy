import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import PluginTestHelper


@IgnoreIf({ System.getenv('IS_WAS_ND') != "1"})
@Stepwise
class CreateClusterNegative extends PluginTestHelper {

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
    def testProcedureName =         'CreateCluster'

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

        wrongClusterName: "The attribute value for attribute $cNames.incorrect is not valid.",
        missingServer: "Cannot find server $fNames.missing on node $fNodes.correct",

        missingTemplate: "Cannot find server template $fTemplates.incorrect",
        missingServerNode: "First Member Name and First Member Node should be provided when create 1st cluster member is chosen",
        missingNode: "Cannot find node $fNodes.incorrect",
        wrongPromotionPolicy: "Promotion policy should be cluster, server, or both, got wrong",
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
        importProject(testProjectName, 'dsl/CreateCluster/Procedure.dsl', [projectName: testProjectName, wasResourceName:wasResourceName])
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
            addClusterMembers:                acm,
            clusterMembersList:               clusterMembersList,
            createFirstClusterMember:         cfm,
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
        def jobSummary = getJobProperty('/myJob/jobSteps/CreateCluster/summary', result.jobId)
        assert jobSummary.contains(expectedJobSummary)
        where: 'The following params will be: '
        cfgName                              |clusterName     |acm|clusterMembersList|cfm|creationPolicy|fName           |fNode           |fTemplate           |promotionPolicy|sourceServerName           | expectedJobSummary
        pluginConfigurationNames.incorrect   |cNames.correct  |'1'|cml.correct       |'1'|'existing'    |fNames.incorrect|fNodes.incorrect|''                  |'cluster'      |sourceServerNames.incorrect| exs.wrongConfig
        pluginConfigurationNames.correctSOAP |cNames.incorrect|'0'|cml.correct       |'1'|'convert'     |''              |''              |''                  |'cluster'      |sourceServerNames.correct  | exs.wrongClusterName
        pluginConfigurationNames.correctSOAP |cNames.correct  |'0'|cml.correct       |'1'|'convert'     |''              |''              |''                  |'cluster'      |sourceServerNames.missing  | exs.missingServer
        
        pluginConfigurationNames.correctSOAP |cNames.incorrect|'0'|cml.correct       |'1'|'template'    |fNames.correct  |fNodes.correct  |fTemplates.correct  |'cluster'      |sourceServerNames.correct  | exs.wrongClusterName
        pluginConfigurationNames.correctSOAP |cNames.correct  |'0'|cml.correct       |'1'|'template'    |fNames.correct  |fNodes.correct  |fTemplates.incorrect|'cluster'      |sourceServerNames.correct  | exs.missingTemplate
        pluginConfigurationNames.correctSOAP |cNames.correct  |'0'|cml.correct       |'1'|'template'    |''              |''              |fTemplates.incorrect|'cluster'      |sourceServerNames.correct  | exs.missingServerNode

        pluginConfigurationNames.correctSOAP |cNames.incorrect|'0'|cml.correct       |'1'|'existing'    |fNames.incorrect|fNodes.incorrect|fTemplates.incorrect|'cluster'      |sourceServerNames.incorrect| exs.wrongClusterName
        pluginConfigurationNames.correctSOAP |cNames.correct  |'0'|cml.correct       |'1'|'existing'    |fNames.incorrect|fNodes.incorrect|fTemplates.incorrect|'cluster'      |sourceServerNames.missing  | exs.missingNode
        pluginConfigurationNames.correctSOAP |cNames.correct  |'0'|cml.correct       |'1'|'existing'    |''              | ''             |''                  |'cluster'      |sourceServerNames.incorrect| exs.missingServerNode

        pluginConfigurationNames.correctSOAP |cNames.correct  |'0'|cml.correct       |'1'|'existing'    |fNames.correct  |fNodes.correct  |fTemplates.correct  |'wrong'        |''                         | exs.wrongPromotionPolicy
        pluginConfigurationNames.correctSOAP |cNames.correct  |'0'|cml.correct       |'1'|'template'    |fNames.existing |fNodes.correct  |fTemplates.correct  |'cluster'      |''                         | exs.serverAlreadyExist
        pluginConfigurationNames.correctSOAP |cNames.correct  |'1'|''                |'1'|'existing'    |fNames.incorrect|fNodes.incorrect|fTemplates.incorrect|'cluster'      |sourceServerNames.missing  | exs.missingMembersList
        pluginConfigurationNames.correctSOAP |cNames.correct  |'1'|cml.wrongFormat   |'1'|'existing'    |fNames.incorrect|fNodes.incorrect|fTemplates.incorrect|'cluster'      |sourceServerNames.missing  | exs.wrongMemberList
        
        /*
         1. Missing configuration
         2. Creation = convert, wrong cluster name
         3. Creation = convert, missing server


         4. Creation = template, wrong cluster name
         5. Creation = template, missing template
         6. Missing servers

         7. Creation = existing, wrong cluster name
         8. Creation = existing, missing server
         9. Missing servers

         10. Creation = existing, promotion policy is wrong
         11. Creation = template, target server already exists

         12. Add cluster members, missing list
         13. Add cluster members, wrong list
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
                    wasAddClusterMembers:                '$parameters.addClusterMembers',
                    wasClusterMembersList:               '$parameters.clusterMembersList',
                    wasCreateFirstClusterMember:         '$parameters.createFirstClusterMember',
                    wasFirstClusterMemberCreationPolicy: '$parameters.firstClusterMemberCreationPolicy',
                    wasFirstClusterMemberName:           '$parameters.firstClusterMemberName',
                    wasFirstClusterMemberNode:           '$parameters.firstClusterMemberNode',
                    wasFirstClusterMemberTemplateName:   '$parameters.firstClusterMemberTemplateName',
                    wasServerResourcesPromotionPolicy:   '$parameters.serverResourcesPromotionPolicy',
                    wasSourceServerName:                 '$parameters.sourceServerName'
                ]
            )
        """
        return dslWithTimeout(code)
    }
}
