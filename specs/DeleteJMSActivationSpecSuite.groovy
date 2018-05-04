import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import PluginTestHelper


@Stepwise
class DeleteJMSActivationSpecSuite extends PluginTestHelper {

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
    def testProcedureName =         'DeleteJMSActivationSpec'
    @Shared
    def preparationProcedureName1 = 'CreateOrUpdateSIBJMSActivationSpec'
    @Shared
    def preparationProcedureName2 = 'CreateOrUpdateWMQJMSActivationSpec'

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
    def ASNames = [
        empty:      '',
        correctWMQ: 'MyWMQActivationSpecForDeletion',
        correctSIB: 'MySIBActivationSpecForDeletion',
        incorrect:  'Incorrect Activation Spec',
    ]

    @Shared
    def jndiNames = [
        empty:      '',
        correctWMQ: 'com.jndi.myWMQActivationSpecForDeletion',
        correctSIB: 'com.jndi.mySIBActivationSpecForDeletion',
        incorrect:  'incorrect jndiNames',
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

    @Shared // Required Parameter (ComboBox, no need incorrect value the incorrect - empty)
    def messagingSystemTypes = [
        empty:      '',
        correctWMQ: 'WMQ',
        correctSIB: 'SIB',
    ]

    @Shared // Required Parameter (need incorrect and empty value)
    def ASScopes = [
        empty:                      '',
        correctOneNode:             'Node='+wasHost+'Node01',
        incorrect:                  'Node=incorrectScope',
        correctOneNodeMessage:      'Node:'+wasHost+'Node01',
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
        empty:                                              "",
        successDeleteWMQ:                                   "WMQ JMS Activation Spec $ASNames.correctWMQ has been deleted",
        successDeleteSIB:                                   "SIB JMS Activation Spec $ASNames.correctSIB has been deleted",
        incorrectScope:                                     "target object is required",
    ]
    
    @Shared expectedJobDetailedResults = [
        empty: '',
    ]


    /**
     * Test Parameters: for Where section 
     */ 
    /// cfgName | ASName | MST | expectedOutcome
    def expectedOutcome
    def expectedSummaryMessage
    def expectedJobDetailedResult
    def cfgName;
    def ASScope;
    def ASName;

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
        importProject(testProjectName, 'dsl/CreateOrUpdateSIBJMSActivationSpec/Procedure.dsl', [projectName: testProjectName, wasResourceName:wasResourceName]);
        importProject(testProjectName, 'dsl/CreateOrUpdateWMQJMSActivationSpec/Procedure.dsl', [projectName: testProjectName, wasResourceName:wasResourceName]);
        // actual test project
        importProject(testProjectName, 'dsl/DeleteJMSActivationSpec/Procedure.dsl', [projectName: testProjectName, wasResourceName:wasResourceName])
        def params = [
            wasHost: wasHost,
            configName: pluginConfigurationNames.correctSOAP,
            scope: ASScopes.correctOneNode,
            sibASName: ASNames.correctSIB,
            wmqASName: ASNames.correctWMQ,
        ]
        createActivationSpecsForDelete(params);
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
    def "Delete JMS Activation Spec. Negative" () {
        when: 'Procedure runs: '
             def runParams = [
                wasHost:    wasHost,
                configName: cfgName,
                mst:        mst,
                name:       ASName,
                scope:      ASScope,

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
            cfgName                              | ASName             | mst   | ASScope                 | expectedSummaryMessage
            pluginConfigurationNames.correctSOAP | ASNames.correctSIB | 'SMQ' | ASScopes.correctOneNode | expectedSummaryMessages.successDeleteSIB
            pluginConfigurationNames.correctSOAP | ASNames.correctWMQ | 'WMQ' | ASScopes.incorrect      | expectedSummaryMessages.successDeleteWMQ

    }


    @Unroll
    def "Delete JMS Activation Spec. Positive and Extended Scenarios" () {
        when: 'Procedure runs: '
             def runParams = [
                wasHost:    wasHost,
                configName: cfgName,
                mst:        mst,
                name:       ASName,
                scope:      ASScope,

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
            cfgName                              | ASName             | mst   | ASScope                 | expectedSummaryMessage
            pluginConfigurationNames.correctSOAP | ASNames.correctSIB | 'SIB' | ASScopes.correctOneNode | expectedSummaryMessages.successDeleteSIB
            pluginConfigurationNames.correctSOAP | ASNames.correctWMQ | 'WMQ' | ASScopes.correctOneNode | expectedSummaryMessages.successDeleteWMQ

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
    //Predefined procedure for ActivationSpecs creation
    def createActivationSpecsForDelete(def parameters) {
        
        def sibASCode = """
            runProcedure(
                projectName: '$testProjectName',
                procedureName: '$preparationProcedureName1',
                actualParameter: [
                    wasResourceName:          '$parameters.wasHost',
                    wasConfigName:            '$parameters.configName',
                    wasAdditionalOptions:     '',
                    wasASDescription:         '',
                    wasASDestinationJNDIName: 'SIBASDestinationJNDIName',
                    wasASJNDIName:            '$parameters.sibASName',
                    wasASName:                '$parameters.sibASName',
                    wasASScope:               '$parameters.scope',
                    wasDestinationType:       '',
                    wasMessageSelector:       '',
                ]
            )
        """
        def sibResult = dsl(sibASCode)
        waitUntil {
            try {
                jobCompleted(sibResult)
            } catch (Exception e) {
                println e.getMessage()
            }
        }

        def wmqASCode = """
            runProcedure(
                projectName: '$testProjectName',
                procedureName: '$preparationProcedureName2',
                actualParameter: [
                    wasResourceName:        '$parameters.wasHost',
                    wasAdditionalOptions:   '',
                    wasCCDQM:               '',
                    wasCCDURL:              '',
                    wasConfigName:          '$parameters.configName',
                    wasDestinationJNDIName: 'DestJNDIName',
                    wasDestinationJNDIType: 'javax.jms.Queue',
                    wasASJNDIName:          '$parameters.wmqASName',
                    wasASName:              '$parameters.wmqASName',
                    wasASDescription:       '',
                    wasASScope:             '$parameters.scope',
                ]
            )
        """
        def wmqResult = dsl(wmqASCode)
        waitUntil {
            try {
                jobCompleted(wmqResult)
            } catch (Exception e) {
                println e.getMessage()
            }
        }
    }

    //Run Test Procedure
    def runProcedure(def parameters) {
        def code = """
            runProcedure(
                projectName:                    '$testProjectName',
                procedureName:                  '$testProcedureName',
                actualParameter: [
                    wasResourceName: '$parameters.wasHost',
                    wasASScope:      '$parameters.scope',
                    wasConfigName:   '$parameters.configName',
                    wasMST:          '$parameters.mst',
                    wasASName:       '$parameters.name',
                ]
            )
        """
        return dsl(code)
    }
}