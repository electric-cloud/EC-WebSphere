import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import PluginTestHelper


@IgnoreIf({ System.getenv('IS_WAS_ND') != "1"})
@Stepwise
class CreateApplicationServerNegative extends PluginTestHelper {

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

    /**
     * Dsl Parameters
     */

    @Shared
    def testProjectName =           'EC-WebSphere-SystemTests'
    @Shared
    def testProcedureName =         'CreateApplicationServer'

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
    def templateNames = [
        correct: 'default',
        incorrect: 'template-that-does-not-exist'
    ]
    @Shared
    def nodeNames = [
        correct: wasHost + 'Node01',
        incorrect: wasHost + 'Node01WRONG'
    ]
    @Shared
    def serverNames = [
        correct: 'server1new',
        incorrect: 'server1'
    ]
    @Shared
    def sourceServerNames = [
        correct: nodeNames.correct + ':' + 'server1',
        incorrect: 'wrongNode01:server1w'
    ]
    @Shared
    def sourceTypes = [
        template: 'template',
        server: 'server',
        incorrect: 'incorrect'
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
        wrongType: "Source type $sourceTypes.incorrect is not valid, server or template expected",
        wrongTemplate: "Matching template $templateNames.incorrect could not be found",
        emptySource: "Source server name is required when source type is set to server",
        wrongNode: "Node $nodeNames.incorrect is not a valid node",
        wrongServer: "$serverNames.incorrect exists within node $nodeNames.correct",
        wrongSource: "Failed to create intermediate template",

    ]

    /**
     * Test Parameters: for Where section 
     */ 
    /// cfgName | ASName | MST | expectedOutcome
    def expectedOutcome
    def cfgName;
    def serverName;
    def nodeName;
    def sourceServerName;
    def templateName;
    def sourceType;

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
        importProject(testProjectName, 'dsl/CreateApplicationServer/Procedure.dsl', [projectName: testProjectName, wasResourceName:wasResourceName])
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
    def "CreateApplicationServer. Negative scenario." () {
        when: 'Procedure runs: '

        def runParams = [
            wasHost:    wasHost,
            configName: cfgName,
            nodeName: nodeName,
            serverName: serverName,
            sourceServerName: sourceServerName,
            templateName: templateName,
            sourceType: sourceType

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
        def jobSummary = getJobProperty('/myJob/jobSteps/CreateApplicationServer/summary', result.jobId)
        assert jobSummary.contains(expectedJobSummary)
        where: 'The following params will be: '
        cfgName                             |sourceType           |nodeName           |serverName           |sourceServerName           |templateName           |expectedJobSummary
        pluginConfigurationNames.incorrect  |sourceTypes.correct  |nodeNames.correct  |serverNames.correct  |sourceServerNames.correct  |templateNames.correct  |exs.wrongConfig
        pluginConfigurationNames.correctSOAP|sourceTypes.incorrect|nodeNames.correct  |serverNames.correct  |sourceServerNames.correct  |templateNames.correct  |exs.wrongType
        pluginConfigurationNames.correctSOAP|sourceTypes.template |nodeNames.correct  |serverNames.correct  |''                         |templateNames.incorrect|exs.wrongTemplate
        pluginConfigurationNames.correctSOAP|sourceTypes.server   |nodeNames.correct  |serverNames.correct  |''                         |''                     |exs.emptySource
        pluginConfigurationNames.correctSOAP|sourceTypes.server   |nodeNames.incorrect|serverNames.correct  |sourceServerNames.correct  |''                     |exs.wrongNode
        pluginConfigurationNames.correctSOAP|sourceTypes.server   |nodeNames.correct  |serverNames.incorrect|sourceServerNames.correct  |''                     |exs.wrongServer
        pluginConfigurationNames.correctSOAP|sourceTypes.server   |nodeNames.correct  |serverNames.correct  |sourceServerNames.incorrect|''                     |exs.wrongSource



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
                    wasResourceName:     '$parameters.wasHost',
                    wasConfigName:       '$parameters.configName',
                    wasNodeName:         '$parameters.nodeName',
                    wasServerName:       '$parameters.serverName',
                    wasSourceServerName: '$parameters.sourceServerName',
                    wasSourceType:       '$parameters.sourceType',
                    wasTemplateName:     '$parameters.templateName'

                ]
            )
        """
        return dslWithTimeout(code)
    }
}
