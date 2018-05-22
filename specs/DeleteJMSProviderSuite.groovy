import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import PluginTestHelper


@Stepwise
class DeleteJMSProvider extends PluginTestHelper {

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
    def testProcedureName =         'DeleteJMSProvider'
    @Shared
    def preparationProcedureName1 = 'CreateJMSProvider'

    /**
     * Common Maps: General Maps fpr different fields
     */

    @Shared
    def checkBoxValues = [
        unchecked: 	'0',
        checked: 	'1',
    ]

    /**
     * Parameters for Test Setup
     */
    @Shared
    def providerNames = [
        empty:      '',
        correct: 'ThirdPartyJMSProvider',
        incorrect:  'Incorrect JMS Provider name',
    ]


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

    @Shared // Required Parameter (need incorrect and empty value)
    def providerScopes = [
        empty:     '',
        correct:   'Node='+wasHost+'Node01',
        incorrect: 'Node=incorrectScope',
    ]

        // Not required Parameter (no need incorrect)


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
    
    @Shared
    def expectedSummaryMessages = [
        empty:                 "",
        successDeleteProvider: "JMS Provider $providerNames.correct has been deleted",
    ]
    
    @Shared expectedJobDetailedResults = [
        empty: '',
    ]


    /**
     * Test Parameters: for Where section 
     */ 
    def expectedOutcome
    def expectedSummaryMessage
    def expectedJobDetailedResult
    def cfgName;
    def providerScope;
    def providerName;

    /**
     * Preparation actions
     */

     def doSetupSpec() {
        def wasResourceName = wasHost
        createWorkspace(wasResourceName)
        createConfiguration(pluginConfigurationNames.correctSOAP, [doNotRecreate: false])
        createConfiguration(pluginConfigurationNames.correctIPC, [doNotRecreate: false])        
        createConfiguration(pluginConfigurationNames.correctJSR160RMI, [doNotRecreate: false])        
        createConfiguration(pluginConfigurationNames.correctNone, [doNotRecreate: false])        
        createConfiguration(pluginConfigurationNames.correctRMI, [doNotRecreate: false])       
        // preparation projects
        importProject(testProjectName, 'dsl/CreateJMSProvider/Procedure.dsl', [projectName: testProjectName, wasResourceName:wasResourceName]);
        // actual test project
        importProject(testProjectName, 'dsl/DeleteJMSProvider/Procedure.dsl', [projectName: testProjectName, wasResourceName:wasResourceName])
        def params = [
            wasHost: wasHost,
            configName: pluginConfigurationNames.correctSOAP,
            scope: providerScopes.correct,
            providerName: providerNames.correct
        ]
        createJMSProviderForDeletion(params);
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
    def "Delete JMS Provider. Negative" () {
        when: 'Procedure runs: '
             def runParams = [
                wasHost:    wasHost,
                configName: cfgName,
                name:       providerName,
                scope:      providerScope,

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
            def upperStepSummary = getJobUpperStepSummary(result.jobId)

            assert outcome == "error"

        where: 'The following params will be: '
            cfgName                              | providerName          | ProviderScope            
            pluginConfigurationNames.correctSOAP | providerNames.correct | providerScopes.incorrect

    }


    @Unroll
    def "Delete JMS Provider. Positive and Extended Scenarios" () {
        when: 'Procedure runs: '
             def runParams = [
                wasHost:    wasHost,
                configName: cfgName,
                name:       providerName,
                scope:      providerScope,

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
            def upperStepSummary = getJobUpperStepSummary(result.jobId)

            assert outcome == "success"
            assert upperStepSummary.contains(expectedSummaryMessage)

        where: 'The following params will be: '
            cfgName                              | providerName          | providerScope          | expectedSummaryMessage
            pluginConfigurationNames.correctSOAP | providerNames.correct | providerScopes.correct | expectedSummaryMessages.successDeleteProvider

    }

    /**
     * Extended Scenarios
     */

    //@Unroll
    //def "" ()


    /**
     * Additional (Required Parameters checks)  Scenarios
     */
    //@Unroll
    //def "" ()

    /**
     * Additional methods
     */
    //Predefined procedure for ConnectionFactorys creation
    def createJMSProviderForDeletion(def parameters) {
        
        def code = """
            runProcedure(
                projectName: '$testProjectName',
                procedureName: '$preparationProcedureName1',
                actualParameter: [
                    wasResourceName:             '$parameters.wasHost',
                    wasConfigName:               '$parameters.configName',
                    wasProviderName:             '$parameters.providerName',
                    wasProviderScope:            '$parameters.scope',
                    wasInitialConnectionFactory: 'DEFAULT',
                    wasJMSProviderURL:           'http://localhost',

                ]
            )
        """
        def result = dslWithTimeout(code)
        waitUntil {
            try {
                jobCompleted(result)
            } catch (Exception e) {
                println e.getMessage()
            }
        }
    }

    //Run Test Procedure
    def runProcedure(def parameters) {
        def code = """
            runProcedure(
                projectName: '$testProjectName',
                procedureName: '$testProcedureName',
                actualParameter: [
                    wasResourceName:  '$parameters.wasHost',
                    wasConfigName:    '$parameters.configName',
                    wasProviderName:  '$parameters.name',
                    wasProviderScope: '$parameters.scope'
                ]
            )
        """
        return dslWithTimeout(code)
    }
}