import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import PluginTestHelper


@Stepwise
class CreateOrUpdateSIBConnectionFactorySuite extends PluginTestHelper {

    /**
     * Environments Variables
     */

    @Shared
    def wasUserName = System.getenv('WAS_USERNAME')
    @Shared
    def wasPassword = System.getenv('WAS_PASSWORD')
    @Shared
    def wasHost =     System.getenv('WAS_HOST')
    @Shared
    def wasPort =     System.getenv('WAS_PORT')
    @Shared
    def wasConnType = System.getenv('WAS_CONNTYPE')
    @Shared
    def wasDebug =    System.getenv('WAS_DEBUG')
    @Shared
    def wasPath =     System.getenv('WSADMIN_PATH')
    @Shared
    def wasAppPath =  System.getenv('WAS_APPPATH')

    /**
     * Dsl Parameters
     */

    @Shared
    def testProjectName =   'EC-WebSphere-SystemTests'
    @Shared
    def testProcedureName = 'CreateorUpdateSIBConnectionFactory'

    /**
     * Common Maps: General Maps fpr different fields
     */

    @Shared
    def checkBoxValues = [
        unchecked: 	                    '0',
        checked: 	                    '1',
    ]

    /**
     * Parameters for Test Setup
     */

    /**
     * Procedure Values: test parameters Procedure values
    */

    @Shared     // Required Parameter (need incorrect and empty value)
    def pluginConfigurationNames = [
        empty:                          "",
        correctSOAP:                    "Web-Sphere-SOAP",
        correctIPC:                     "Web-Sphere-IPC",
        correctJSR160RMI:               "Web-Sphere-JSR160RMI",
        correctNone:                    "Web-Sphere-None",
        correctRMI:                     "Web-Sphere-RMI",
        incorrect: 	                    "incorrect config Name",
    ]


    @Shared // Required Parameter (need incorrect and empty value)
    def factoryScopes = [
        empty:                          "",
        correctOneNode:                 "Node="+wasHost+"Node01",
        incorrect:                      "Node=incorrectScope",
        correctOneNodeMessage:          "Node:"+wasHost+"Node01",
    ]

    @Shared // Required Parameter (need incorrect and empty value)
    def factoryAdministrativeNames = [
        empty:                          "",
        correctQueue:                   "MySIBJMSConnFactQueue",
        correctTopic:                   "MySIBJMSConnFactTopic",
        incorrect:                      ":/:/: Incorrect",
    ]

    @Shared //* Required Parameter (need incorrect and empty value)
    def busNames = [
        empty:                          "",
        correctQueue:                   "DefaultBusQ",
        correctTopic:                   "DefaultBusT",
        incorrect:                      "/:/:/\" Incorrect Bus Name",
    ]

    @Shared //* Required Parameter (need incorrect and empty value)
    def jndiNames = [
        empty:                          "",
        correctQueue:                   "com.jndi.mySIBJMSAppFactQueue",
        correctTopic:                   "com.jndi.mySIBJMSAppFactTopic",
        incorrect:                      "/:/:/\" incorrect Factory JNDI Name",
    ]

        // Not required Parameter
    @Shared //* Optional Parameter
    def factoryTypes = [
        empty:                          "",
        correctQueue:                   "Queue",
        correctTopic:                   "Topic",
        incorrect:                      "incorrect Factory Type",
    ]

    @Shared //* Optional Parameter
    def factoryAdministrativeDescriptions = [
        empty:                          "",
        correctQueue:                   "Factory Administrative Description Queue",
        correctTopic:                   "Factory Administrative Description Topic",
        incorrect:                      "What the incorrect value for this parameter?",
    ]

    @Shared //* Optional Parameter
    def additionalOptions = [
        empty:                          "",
        correct:                        "-shareDataSourceWithCMP True -connectionProximity Host",
        incorrect:                      "Incorrect Additional Options",
    ]

    /**
     * Verification Values: Assert values
    */

    @Shared
    def expectedOutcomes = [
        success: 	                    "success",
        error: 		                    "error",
        warning: 	                    "warning",
        running: 	                    "running",
    ]

    @Shared
    def expectedSummaryMessages = [
        empty:                          "",
        successCreateQ:                 "",
        successCreateT:                 "",
        successUpdateQ:                 "",
        successUpdateT:                 "",
        incorrectConfiguration:         "Configuration '"+pluginConfigurationNames.incorrect+"' doesn't exist",
        incorrectScope:                 "target object is required",
        incorrectFactName:              "",
        incorrectBuName:                "",
        incorrectJNDIName:              "",
        incorrectConnFacType:           "",
        incorrectAdOps:                 "",
        correctOneNodeMessage:          "Node:"+wasHost+"Node01",
    ]

    @Shared expectedJobDetailedResults = [
        empty: "",
    ]

    @Shared expectedLogParts = [
        empty: "",
    ]

    /**
     * Test Parameters: for Where section
     */
    // Procedure params
    def pluginConfigurationName
    def factoryScope
    def factoryAdministrativeName
    def busName
    def jndiName
    //optional parameters
    def factoryType
    def factoryAdministrativeDescription
    def additionalOption

    // expected results
    def expectedOutcome
    def expectedSummaryMessage
    def expectedJobDetailedResult

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
        importProject(testProjectName, 'dsl/CreateOrUpdateSIBConnectionFactory/CreateOrUpdateSIBConnectionFactory.dsl', [projectName: testProjectName, wasResourceName:wasResourceName])
        dsl 'setProperty(propertyName: "/plugins/EC-WebSphere/project/ec_debug_logToProperty", value: "/myJob/debug_logs")'
     }

    /**
     * Clean Up actions after test will finished
     */

    def doCleanupSpec() {

    }

    /**
     * Positive Scenarios
     */

    @Unroll //Positive Scenarios for delete should be first
    def "Create Or Update SIB JMS Connection Factory. Positive Scenarios and Extended Scenarios" (){
        setup: 'Define the parameters for Procedure running'
             def runParams = [
                pluginConfigurationName:                pluginConfigurationName,
                factoryScope:                           factoryScope,
                factoryAdministrativeName:              factoryAdministrativeName,
                busName:                                busName,
                jndiName:                               jndiName,
                factoryType:                            factoryType,
                factoryAdministrativeDescription:       factoryAdministrativeDescription,
                additionalOption:                       additionalOption,
                wasHost:                                wasHost,
            ]

        when: 'Procedure runs: '
            def result = runProcedure(runParams)

        then: 'Wait until job run is completed: '
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

        expect: 'Outcome and Upper Summary verification'
            assert outcome == expectedOutcome
            //assert upperStepSummary.contains(expectedSummaryMessage)

        where: 'The following params will be: '
            pluginConfigurationName                 | factoryScope                      | factoryAdministrativeName                 | busName                   | jndiName              /* Not Required */  | factoryType                   | factoryAdministrativeDescription                      | additionalOption                  | expectedOutcome                   | expectedSummaryMessage
//for Queue
            pluginConfigurationNames.correctSOAP    | factoryScopes.correctOneNode      | factoryAdministrativeNames.correctQueue   | busNames.correctQueue     | jndiNames.correctQueue                    | factoryTypes.empty            | factoryAdministrativeDescriptions.empty               | additionalOptions.empty           | expectedOutcomes.success          | expectedSummaryMessages.successCreateQ
            pluginConfigurationNames.correctSOAP    | factoryScopes.correctOneNode      | factoryAdministrativeNames.correctQueue   | busNames.correctQueue     | jndiNames.correctQueue                    | factoryTypes.correctQueue     | factoryAdministrativeDescriptions.empty               | additionalOptions.empty           | expectedOutcomes.success          | expectedSummaryMessages.successUpdateQ
            pluginConfigurationNames.correctSOAP    | factoryScopes.correctOneNode      | factoryAdministrativeNames.correctQueue   | busNames.correctQueue     | jndiNames.correctQueue                    | factoryTypes.empty            | factoryAdministrativeDescriptions.correctQueue        | additionalOptions.empty           | expectedOutcomes.success          | expectedSummaryMessages.successUpdateQ
            pluginConfigurationNames.correctSOAP    | factoryScopes.correctOneNode      | factoryAdministrativeNames.correctQueue   | busNames.correctQueue     | jndiNames.correctQueue                    | factoryTypes.empty            | factoryAdministrativeDescriptions.empty               | additionalOptions.correct         | expectedOutcomes.success          | expectedSummaryMessages.successUpdateQ
            pluginConfigurationNames.correctSOAP    | factoryScopes.correctOneNode      | factoryAdministrativeNames.correctQueue   | busNames.correctQueue     | jndiNames.correctQueue                    | factoryTypes.correctQueue     | factoryAdministrativeDescriptions.correctQueue        | additionalOptions.empty           | expectedOutcomes.success          | expectedSummaryMessages.successUpdateQ
            pluginConfigurationNames.correctSOAP    | factoryScopes.correctOneNode      | factoryAdministrativeNames.correctQueue   | busNames.correctQueue     | jndiNames.correctQueue                    | factoryTypes.correctQueue     | factoryAdministrativeDescriptions.correctQueue        | additionalOptions.correct         | expectedOutcomes.success          | expectedSummaryMessages.successUpdateQ
//for Topic
            pluginConfigurationNames.correctSOAP    | factoryScopes.correctOneNode      | factoryAdministrativeNames.correctTopic   | busNames.correctTopic     | jndiNames.correctTopic                    | factoryTypes.empty            | factoryAdministrativeDescriptions.empty               | additionalOptions.empty           | expectedOutcomes.success          | expectedSummaryMessages.successCreateT
            pluginConfigurationNames.correctSOAP    | factoryScopes.correctOneNode      | factoryAdministrativeNames.correctTopic   | busNames.correctTopic     | jndiNames.correctTopic                    | factoryTypes.correctTopic     | factoryAdministrativeDescriptions.empty               | additionalOptions.empty           | expectedOutcomes.success          | expectedSummaryMessages.successUpdateT
            pluginConfigurationNames.correctSOAP    | factoryScopes.correctOneNode      | factoryAdministrativeNames.correctTopic   | busNames.correctTopic     | jndiNames.correctTopic                    | factoryTypes.empty            | factoryAdministrativeDescriptions.correctTopic        | additionalOptions.empty           | expectedOutcomes.success          | expectedSummaryMessages.successUpdateT
            pluginConfigurationNames.correctSOAP    | factoryScopes.correctOneNode      | factoryAdministrativeNames.correctTopic   | busNames.correctTopic     | jndiNames.correctTopic                    | factoryTypes.empty            | factoryAdministrativeDescriptions.empty               | additionalOptions.correct         | expectedOutcomes.success          | expectedSummaryMessages.successUpdateT
            pluginConfigurationNames.correctSOAP    | factoryScopes.correctOneNode      | factoryAdministrativeNames.correctTopic   | busNames.correctTopic     | jndiNames.correctTopic                    | factoryTypes.correctTopic     | factoryAdministrativeDescriptions.correctTopic        | additionalOptions.empty           | expectedOutcomes.success          | expectedSummaryMessages.successUpdateT
            pluginConfigurationNames.correctSOAP    | factoryScopes.correctOneNode      | factoryAdministrativeNames.correctTopic   | busNames.correctTopic     | jndiNames.correctTopic                    | factoryTypes.correctTopic     | factoryAdministrativeDescriptions.correctTopic        | additionalOptions.correct         | expectedOutcomes.success          | expectedSummaryMessages.successUpdateT
    }

    /**
     * Negative Scenarios
     */

    @Unroll
    def "Create Or Update SIB JMS Connection Factory. Negative Scenarios and Extended Scenarios" () {
        setup: 'Define the parameters for Procedure running'
             def runParams = [
                pluginConfigurationName:                pluginConfigurationName,
                factoryScope:                           factoryScope,
                factoryAdministrativeName:              factoryAdministrativeName,
                busName:                                busName,
                jndiName:                               jndiName,
                factoryType:                            factoryType,
                factoryAdministrativeDescription:       factoryAdministrativeDescription,
                additionalOption:                       additionalOption,
                wasHost:                                wasHost,
            ]

        when: 'Procedure runs: '
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

        expect: 'Outcome and Upper Summary verification'
            assert outcome == expectedOutcome
            //assert upperStepSummary.contains(expectedSummaryMessage)

        where: 'The following params will be: '
            pluginConfigurationName                 | factoryScope                  | factoryAdministrativeName                 | busName                   | jndiName              /* Not Required */| factoryType                     | factoryAdministrativeDescription                      | additionalOption                  | expectedOutcome                   | expectedSummaryMessage
//for Queue

//for Topic

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
    //Predefined procedure for Creation Queue

    //Run Test Procedure
    def runProcedure(def parameters) {
        def code = """
            runProcedure(
                projectName:                        '$testProjectName',
                procedureName:                      '$testProcedureName',
                actualParameter: [
                    confignameCOUSIBCF:                        '$parameters.pluginConfigurationName',
                    factoryScopeCOUSIBCF:                       '$parameters.factoryScope',
                    factoryAdministrativeNameCOUSIBCF:          '$parameters.factoryAdministrativeName',
                    busNameCOUSIBCF:                            '$parameters.busName',
                    jndiNameCOUSIBCF:                           '$parameters.jndiName',
                    factoryTypeCOUSIBCF:                        '$parameters.factoryType',
                    factoryAdministrativeDescriptionCOUSIBCF:   '$parameters.factoryAdministrativeDescription',
                    additionalOptionsCOUSIBCF:                  '$parameters.additionalOption',
                    wasResourceName:                            '$parameters.wasHost',
                ]
            )
        """
        return dsl(code)
    }
}