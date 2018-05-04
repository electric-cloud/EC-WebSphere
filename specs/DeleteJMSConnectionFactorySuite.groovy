import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import PluginTestHelper


@Stepwise
class DeleteJMSConnectionFactorySuite extends PluginTestHelper {

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
    def testProcedureName =         'DeleteJMSConnectionFactory'
    @Shared
    def preparationProcedureName1 = 'CreateOrUpdateSIBJMSConnectionFactory'
    @Shared
    def preparationProcedureName2 = 'CreateOrUpdateWMQJMSConnectionFactory'

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
    def CFNames = [
        empty:      '',
        correctWMQ: 'MyWMQConnectionFactoryForDeletion',
        correctSIB: 'MySIBConnectionFactoryForDeletion',
        incorrect:  'Incorrect Connection Factory',
    ]

    @Shared
    def jndiNames = [
        empty:      '',
        correctWMQ: 'com.jndi.myWMQConnectionFactoryForDeletion',
        correctSIB: 'com.jndi.mySIBConnectionFactoryForDeletion',
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
    def CFScopes = [
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
        successDeleteWMQ:                                   "WMQ JMS Connection Factory $CFNames.correctWMQ has been deleted",
        successDeleteSIB:                                   "SIB JMS Connection Factory $CFNames.correctSIB has been deleted",
        incorrectScope:                                     "target object is required",
    ]
    
    @Shared expectedJobDetailedResults = [
        empty: '',
    ]


    /**
     * Test Parameters: for Where section 
     */ 
    /// cfgName | CFName | MST | expectedOutcome
    def expectedOutcome
    def expectedSummaryMessage
    def expectedJobDetailedResult
    def cfgName;
    def CFScope;
    def CFName;

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
        importProject(testProjectName, 'dsl/CreateOrUpdateSIBJMSConnectionFactory/Procedure.dsl', [projectName: testProjectName, wasResourceName:wasResourceName]);
        importProject(testProjectName, 'dsl/CreateOrUpdateWMQJMSConnectionFactory/Procedure.dsl', [projectName: testProjectName, wasResourceName:wasResourceName]);
        // actual test project
        importProject(testProjectName, 'dsl/DeleteJMSConnectionFactory/Procedure.dsl', [projectName: testProjectName, wasResourceName:wasResourceName])
        def params = [
            wasHost: wasHost,
            configName: pluginConfigurationNames.correctSOAP,
            scope: CFScopes.correctOneNode,
            sibCFName: CFNames.correctSIB,
            wmqCFName: CFNames.correctWMQ,
        ]
        createConnectionFactoriesForDelete(params);
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
                name:       CFName,
                scope:      CFScope,

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
            cfgName                              | CFName             | mst   | CFScope                 | expectedSummaryMessage
            pluginConfigurationNames.correctSOAP | CFNames.correctSIB | 'SMQ' | CFScopes.correctOneNode | expectedSummaryMessages.successDeleteSIB
            pluginConfigurationNames.correctSOAP | CFNames.correctWMQ | 'WMQ' | CFScopes.incorrect      | expectedSummaryMessages.successDeleteWMQ

    }


    @Unroll
    def "Delete JMS Activation Spec. Positive and Extended Scenarios" () {
        when: 'Procedure runs: '
             def runParams = [
                wasHost:    wasHost,
                configName: cfgName,
                mst:        mst,
                name:       CFName,
                scope:      CFScope,

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
            cfgName                              | CFName             | mst   | CFScope                 | expectedSummaryMessage
            pluginConfigurationNames.correctSOAP | CFNames.correctSIB | 'SIB' | CFScopes.correctOneNode | expectedSummaryMessages.successDeleteSIB
            pluginConfigurationNames.correctSOAP | CFNames.correctWMQ | 'WMQ' | CFScopes.correctOneNode | expectedSummaryMessages.successDeleteWMQ

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
    def createConnectionFactoriesForDelete(def parameters) {
        
        def sibCFCode = """
            runProcedure(
                projectName: '$testProjectName',
                procedureName: '$preparationProcedureName1',
                actualParameter: [
                    wasResourceName:          '$parameters.wasHost',
                    wasConfigName:            '$parameters.configName',
                    wasBusName:               'DEFAULT',
                    wasAdditionalOptions:     '',
                    wasCFDescription:         '',
                    wasCFName:                '$parameters.sibCFName',
                    wasJNDIName:              '$parameters.sibCFName',
                    wasCFScope:               '$parameters.scope',
                    wasCFType:                ''
                ]
            )
        """
        def sibResult = dsl(sibCFCode)
        waitUntil {
            try {
                jobCompleted(sibResult)
            } catch (Exception e) {
                println e.getMessage()
            }
        }

        def wmqCFCode = """
            runProcedure(
                projectName: '$testProjectName',
                procedureName: '$preparationProcedureName2',
                actualParameter: [
                    wasResourceName:      '$parameters.wasHost',
                    wasAdditionalOptions: '',
                    wasCCDQM:             '',
                    wasCCDURL:            '',
                    wasConfigName:        '$parameters.configName',
                    wasCFDescription:     '',
                    wasCFName:            '$parameters.wmqCFName',
                    wasCFScope:           '$parameters.scope',
                    wasCFType:            'CF',
                    wasJNDIName:          '$parameters.wmqCFName',
                ]
            )
        """
        def wmqResult = dsl(wmqCFCode)
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
                    wasCFScope:      '$parameters.scope',
                    wasConfigName:   '$parameters.configName',
                    wasMST:          '$parameters.mst',
                    wasCFName:       '$parameters.name',
                ]
            )
        """
        return dsl(code)
    }
}