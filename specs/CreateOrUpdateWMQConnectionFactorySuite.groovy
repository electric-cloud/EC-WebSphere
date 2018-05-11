import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import PluginTestHelper


@Stepwise
class CreateOrUpdateWMQConnectionFactorySuite extends PluginTestHelper {

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
    def testProcedureName = 'CreateorUpdateWMQConnectionFactory'

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
        correctCF:                      "MyWMQJMSConnFact",
        correctQCF:                     "MyWMQJMSQueueConnFact",
        correctTCF:                     "MyWMQJMSTopicConnFact",
        correctForFactType:             "myWMQJMSAppFactType",
        incorrect:                      ":/:/:\" Incorrect Factory Admin Name",
    ]

    @Shared //* Required Parameter (need incorrect and empty value)
    def factoryTypes = [
        empty:                          "",
        correctCF:                      "CF",
        correctQCF:                     "QCF",
        correctTCF:                     "TCF",
        incorrect:                      "Incorrect Factory Type",
    ]

    @Shared //* Required Parameter (need incorrect and empty value)
    def jndiNames = [
        empty:                          "",
        correctCF:                      "com.jndi.myWMQJMSAppConnFact",
        correctQCF:                     "com.jndi.myWMQJMSAppQConnFact",
        correctTCF:                     "com.jndi.myWMQJMSAppTConnFact",
        correctForFactType:             "com.jndi.myWMQJMSAppFactType",
        incorrect:                      "/:/:/\" incorrect Factory JNDI Name",
    ]

        // Not required Parameter

    @Shared //* Optional Parameter
    def factoryAdministrativeDescriptions = [
        empty:                          "",
        correctCF:                      "Factory Administrative Description Conn Fact",
        correctQCF:                     "Factory Administrative Description Queue Conn Fact",
        correctTCF:                     "Factory Administrative Description Topic Conn Fact",
        incorrect:                      "What the incorrect value for this parameter?",
    ]

    //TODO:
    // Need to know the relevant positive values
    @Shared //* Optional Parameter
    def clientChannelDefinitionUrls = [
        empty:                          "",
        correct:                        "",
        incorrect:                      "http://test.com",
    ]

    //TODO:
    // Need to know the relevant positive values
    @Shared //* Optional Parameter
    def clientChannelDefinitionQueueManagers = [
        empty:                          "",
        correct:                        "",
        incorrect:                      "",
    ]

    @Shared //* Optional Parameter
    def additionalOptions = [
        empty:                          "",
        correctCF:                      "-ccsid 819 -msgSelection BROKER",
        correctQCF:                     "-ccsid 819",
        correctTCF:                     "-ccsid 819 -msgSelection BROKER",
        incorrect:                      "Incorrect additional Options",
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
        successCreateCF:                "WMQ JMS Connection Factory $factoryAdministrativeNames.correctCF has been created",
        successCreateQCF:               "WMQ JMS Connection Factory $factoryAdministrativeNames.correctQCF has been created",
        successCreateTCF:               "WMQ JMS Connection Factory $factoryAdministrativeNames.correctTCF has been created",
        successUpdateCF:                "WMQ JMS Connection Factory $factoryAdministrativeNames.correctCF has been updated",
        successUpdateQCF:               "WMQ JMS Connection Factory $factoryAdministrativeNames.correctQCF has been updated",
        successUpdateTCF:               "WMQ JMS Connection Factory $factoryAdministrativeNames.correctTCF has been updated",
        incorrectConfiguration:         "Configuration '"+pluginConfigurationNames.incorrect+"' doesn't exist",
        incorrectScope:                 "Error found in String \"\"; cannot create ObjectName", //"target object is required",
        incorrectFactAdmName:           "A resource with JNDI name $jndiNames.correctCF already exists as a different resource type. You must use a unique name",
        incorrectFactType:              "",
        incorrectJNDIName:              "",
        incorrectFactAdmnDescr:         "",
        incorrectCCDUrl:                "",
        incorrectCCDQM:                 "",
        incorrectAdOps:                 "Incorrect Additional Options",
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
    def factoryType
    def jndiName
    //optional parameters
    def factoryAdministrativeDescription
    def clientChannelDefinitionUrl
    def clientChannelDefinitionQueueManager
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
        importProject(testProjectName, 'dsl/CreateOrUpdateWMQConnectionFactory/CreateOrUpdateWMQConnectionFactory.dsl', [projectName: testProjectName, wasResourceName:wasResourceName])
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
    def "Create Or Update WMQ JMS Connection Factory. Positive Scenarios and Extended Scenarios" (){
        setup: 'Define the parameters for Procedure running'
             def runParams = [
                pluginConfigurationName:                pluginConfigurationName,
                factoryScope:                           factoryScope,
                factoryAdministrativeName:              factoryAdministrativeName,
                factoryType:                            factoryType,
                jndiName:                               jndiName,
                factoryAdministrativeDescription:       factoryAdministrativeDescription,
                clientChannelDefinitionUrl:             clientChannelDefinitionUrl,
                clientChannelDefinitionQueueManager:    clientChannelDefinitionQueueManager,
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
            assert upperStepSummary =~ expectedSummaryMessage

        where: 'The following params will be: '
            pluginConfigurationName                 | factoryScope                      | factoryAdministrativeName                 | factoryType                   | jndiName         /*Not Required*/ | factoryAdministrativeDescription                  | clientChannelDefinitionUrl                | clientChannelDefinitionQueueManager               | additionalOption                  | expectedOutcome                   | expectedSummaryMessage
//for CF
            pluginConfigurationNames.correctSOAP    | factoryScopes.correctOneNode      | factoryAdministrativeNames.correctCF      | factoryTypes.correctCF        | jndiNames.correctCF               | factoryAdministrativeDescriptions.empty           | clientChannelDefinitionUrls.empty         | clientChannelDefinitionQueueManagers.empty        | additionalOptions.empty           | expectedOutcomes.success          | expectedSummaryMessages.successCreateCF
            pluginConfigurationNames.correctSOAP    | factoryScopes.correctOneNode      | factoryAdministrativeNames.correctCF      | factoryTypes.correctCF        | jndiNames.correctCF               | factoryAdministrativeDescriptions.correctCF       | clientChannelDefinitionUrls.empty         | clientChannelDefinitionQueueManagers.empty        | additionalOptions.empty           | expectedOutcomes.success          | expectedSummaryMessages.successUpdateCF
            pluginConfigurationNames.correctSOAP    | factoryScopes.correctOneNode      | factoryAdministrativeNames.correctCF      | factoryTypes.correctCF        | jndiNames.correctCF               | factoryAdministrativeDescriptions.empty           | clientChannelDefinitionUrls.correct       | clientChannelDefinitionQueueManagers.empty        | additionalOptions.empty           | expectedOutcomes.success          | expectedSummaryMessages.successUpdateCF
            pluginConfigurationNames.correctSOAP    | factoryScopes.correctOneNode      | factoryAdministrativeNames.correctCF      | factoryTypes.correctCF        | jndiNames.correctCF               | factoryAdministrativeDescriptions.empty           | clientChannelDefinitionUrls.empty         | clientChannelDefinitionQueueManagers.correct      | additionalOptions.empty           | expectedOutcomes.success          | expectedSummaryMessages.successUpdateCF
            pluginConfigurationNames.correctSOAP    | factoryScopes.correctOneNode      | factoryAdministrativeNames.correctCF      | factoryTypes.correctCF        | jndiNames.correctCF               | factoryAdministrativeDescriptions.empty           | clientChannelDefinitionUrls.empty         | clientChannelDefinitionQueueManagers.empty        | additionalOptions.correctCF       | expectedOutcomes.success          | expectedSummaryMessages.successUpdateCF
            pluginConfigurationNames.correctSOAP    | factoryScopes.correctOneNode      | factoryAdministrativeNames.correctCF      | factoryTypes.correctCF        | jndiNames.correctCF               | factoryAdministrativeDescriptions.correctCF       | clientChannelDefinitionUrls.correct       | clientChannelDefinitionQueueManagers.empty        | additionalOptions.empty           | expectedOutcomes.success          | expectedSummaryMessages.successUpdateCF
            pluginConfigurationNames.correctSOAP    | factoryScopes.correctOneNode      | factoryAdministrativeNames.correctCF      | factoryTypes.correctCF        | jndiNames.correctCF               | factoryAdministrativeDescriptions.correctCF       | clientChannelDefinitionUrls.correct       | clientChannelDefinitionQueueManagers.correct      | additionalOptions.empty           | expectedOutcomes.success          | expectedSummaryMessages.successUpdateCF
            pluginConfigurationNames.correctSOAP    | factoryScopes.correctOneNode      | factoryAdministrativeNames.correctCF      | factoryTypes.correctCF        | jndiNames.correctCF               | factoryAdministrativeDescriptions.correctCF       | clientChannelDefinitionUrls.correct       | clientChannelDefinitionQueueManagers.correct      | additionalOptions.correctCF       | expectedOutcomes.success          | expectedSummaryMessages.successUpdateCF
//for QCF
            pluginConfigurationNames.correctSOAP    | factoryScopes.correctOneNode      | factoryAdministrativeNames.correctQCF     | factoryTypes.correctQCF       | jndiNames.correctQCF              | factoryAdministrativeDescriptions.empty           | clientChannelDefinitionUrls.empty         | clientChannelDefinitionQueueManagers.empty        | additionalOptions.empty           | expectedOutcomes.success          | expectedSummaryMessages.successCreateQCF
            pluginConfigurationNames.correctSOAP    | factoryScopes.correctOneNode      | factoryAdministrativeNames.correctQCF     | factoryTypes.correctQCF       | jndiNames.correctQCF              | factoryAdministrativeDescriptions.correctQCF      | clientChannelDefinitionUrls.empty         | clientChannelDefinitionQueueManagers.empty        | additionalOptions.empty           | expectedOutcomes.success          | expectedSummaryMessages.successUpdateQCF
            pluginConfigurationNames.correctSOAP    | factoryScopes.correctOneNode      | factoryAdministrativeNames.correctQCF     | factoryTypes.correctQCF       | jndiNames.correctQCF              | factoryAdministrativeDescriptions.empty           | clientChannelDefinitionUrls.correct       | clientChannelDefinitionQueueManagers.empty        | additionalOptions.empty           | expectedOutcomes.success          | expectedSummaryMessages.successUpdateQCF
            pluginConfigurationNames.correctSOAP    | factoryScopes.correctOneNode      | factoryAdministrativeNames.correctQCF     | factoryTypes.correctQCF       | jndiNames.correctQCF              | factoryAdministrativeDescriptions.empty           | clientChannelDefinitionUrls.empty         | clientChannelDefinitionQueueManagers.correct      | additionalOptions.empty           | expectedOutcomes.success          | expectedSummaryMessages.successUpdateQCF
            pluginConfigurationNames.correctSOAP    | factoryScopes.correctOneNode      | factoryAdministrativeNames.correctQCF     | factoryTypes.correctQCF       | jndiNames.correctQCF              | factoryAdministrativeDescriptions.empty           | clientChannelDefinitionUrls.empty         | clientChannelDefinitionQueueManagers.empty        | additionalOptions.correctQCF      | expectedOutcomes.success          | expectedSummaryMessages.successUpdateQCF
            pluginConfigurationNames.correctSOAP    | factoryScopes.correctOneNode      | factoryAdministrativeNames.correctQCF     | factoryTypes.correctQCF       | jndiNames.correctQCF              | factoryAdministrativeDescriptions.correctQCF      | clientChannelDefinitionUrls.correct       | clientChannelDefinitionQueueManagers.empty        | additionalOptions.empty           | expectedOutcomes.success          | expectedSummaryMessages.successUpdateQCF
            pluginConfigurationNames.correctSOAP    | factoryScopes.correctOneNode      | factoryAdministrativeNames.correctQCF     | factoryTypes.correctQCF       | jndiNames.correctQCF              | factoryAdministrativeDescriptions.correctQCF      | clientChannelDefinitionUrls.correct       | clientChannelDefinitionQueueManagers.correct      | additionalOptions.empty           | expectedOutcomes.success          | expectedSummaryMessages.successUpdateQCF
            pluginConfigurationNames.correctSOAP    | factoryScopes.correctOneNode      | factoryAdministrativeNames.correctQCF     | factoryTypes.correctQCF       | jndiNames.correctQCF              | factoryAdministrativeDescriptions.correctQCF      | clientChannelDefinitionUrls.correct       | clientChannelDefinitionQueueManagers.correct      | additionalOptions.correctQCF      | expectedOutcomes.success          | expectedSummaryMessages.successUpdateQCF
//for TCF
            pluginConfigurationNames.correctSOAP    | factoryScopes.correctOneNode      | factoryAdministrativeNames.correctTCF     | factoryTypes.correctTCF       | jndiNames.correctTCF              | factoryAdministrativeDescriptions.empty           | clientChannelDefinitionUrls.empty         | clientChannelDefinitionQueueManagers.empty        | additionalOptions.empty           | expectedOutcomes.success          | expectedSummaryMessages.successCreateTCF
            pluginConfigurationNames.correctSOAP    | factoryScopes.correctOneNode      | factoryAdministrativeNames.correctTCF     | factoryTypes.correctTCF       | jndiNames.correctTCF              | factoryAdministrativeDescriptions.correctTCF      | clientChannelDefinitionUrls.empty         | clientChannelDefinitionQueueManagers.empty        | additionalOptions.empty           | expectedOutcomes.success          | expectedSummaryMessages.successUpdateTCF
            pluginConfigurationNames.correctSOAP    | factoryScopes.correctOneNode      | factoryAdministrativeNames.correctTCF     | factoryTypes.correctTCF       | jndiNames.correctTCF              | factoryAdministrativeDescriptions.empty           | clientChannelDefinitionUrls.correct       | clientChannelDefinitionQueueManagers.empty        | additionalOptions.empty           | expectedOutcomes.success          | expectedSummaryMessages.successUpdateTCF
            pluginConfigurationNames.correctSOAP    | factoryScopes.correctOneNode      | factoryAdministrativeNames.correctTCF     | factoryTypes.correctTCF       | jndiNames.correctTCF              | factoryAdministrativeDescriptions.empty           | clientChannelDefinitionUrls.empty         | clientChannelDefinitionQueueManagers.correct      | additionalOptions.empty           | expectedOutcomes.success          | expectedSummaryMessages.successUpdateTCF
            pluginConfigurationNames.correctSOAP    | factoryScopes.correctOneNode      | factoryAdministrativeNames.correctTCF     | factoryTypes.correctTCF       | jndiNames.correctTCF              | factoryAdministrativeDescriptions.empty           | clientChannelDefinitionUrls.empty         | clientChannelDefinitionQueueManagers.empty        | additionalOptions.correctTCF      | expectedOutcomes.success          | expectedSummaryMessages.successUpdateTCF
            pluginConfigurationNames.correctSOAP    | factoryScopes.correctOneNode      | factoryAdministrativeNames.correctTCF     | factoryTypes.correctTCF       | jndiNames.correctTCF              | factoryAdministrativeDescriptions.correctTCF      | clientChannelDefinitionUrls.correct       | clientChannelDefinitionQueueManagers.empty        | additionalOptions.empty           | expectedOutcomes.success          | expectedSummaryMessages.successUpdateTCF
            pluginConfigurationNames.correctSOAP    | factoryScopes.correctOneNode      | factoryAdministrativeNames.correctTCF     | factoryTypes.correctTCF       | jndiNames.correctTCF              | factoryAdministrativeDescriptions.correctTCF      | clientChannelDefinitionUrls.correct       | clientChannelDefinitionQueueManagers.correct      | additionalOptions.empty           | expectedOutcomes.success          | expectedSummaryMessages.successUpdateTCF
            pluginConfigurationNames.correctSOAP    | factoryScopes.correctOneNode      | factoryAdministrativeNames.correctTCF     | factoryTypes.correctTCF       | jndiNames.correctTCF              | factoryAdministrativeDescriptions.correctTCF      | clientChannelDefinitionUrls.correct       | clientChannelDefinitionQueueManagers.correct      | additionalOptions.correctTCF      | expectedOutcomes.success          | expectedSummaryMessages.successUpdateTCF
    }

    /**
     * Negative Scenarios
     */

    @Unroll
    def "Create Or Update WMQ JMS Connection Factory. Negative Scenarios and Extended Scenarios" () {
        setup: 'Define the parameters for Procedure running'
             def runParams = [
                pluginConfigurationName:                pluginConfigurationName,
                factoryScope:                           factoryScope,
                factoryAdministrativeName:              factoryAdministrativeName,
                factoryType:                            factoryType,
                jndiName:                               jndiName,
                factoryAdministrativeDescription:       factoryAdministrativeDescription,
                clientChannelDefinitionUrl:             clientChannelDefinitionUrl,
                clientChannelDefinitionQueueManager:    clientChannelDefinitionQueueManager,
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
            //assert upperStepSummary =~ expectedSummaryMessage

        where: 'The following params will be: '
            pluginConfigurationName                 | factoryScope              | factoryAdministrativeName             | factoryType               | jndiName           /*Not Required*/   | factoryAdministrativeDescription                  | clientChannelDefinitionUrl                | clientChannelDefinitionQueueManager               | additionalOption                  | wasHost                   | expectedOutcome                   | expectedSummaryMessage
//for CF
//for QCF
//for TCF

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
                    confignameCOUWMQCF:                             '$parameters.pluginConfigurationName',
                    factoryScopeCOUWMQCF:                           '$parameters.factoryScope',
                    factoryAdministrativeNameCOUWMQCF:              '$parameters.factoryAdministrativeName',
                    factoryTypeCOUWMQCF:                            '$parameters.factoryType',
                    jndiNameCOUWMQCF:                               '$parameters.jndiName',
                    factoryAdministrativeDescriptionCOUWMQCF:       '$parameters.factoryAdministrativeDescription',
                    clientChannelDefinitionUrlCOUWMQCF:             '$parameters.clientChannelDefinitionUrl',
                    clientChannelDefinitionQueueManagerCOUWMQCF:    '$parameters.clientChannelDefinitionQueueManager',
                    additionalOptionsCOUWMQCF:                      '$parameters.additionalOption',
                    wasResourceName:                                '$parameters.wasHost',
                ]
            )
        """
        return dsl(code)
    }
}