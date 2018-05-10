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
        correctQueue:                   "MyWMQJMSConnFactQueue",
        correctTopic:                   "MyWMQJMSConnFactTopic",
        correctForFactType:             "myWMQJMSAppFactType",
        incorrect:                      ":/:/: Incorrect Factory Admin Name",
    ]

    @Shared //* Required Parameter (need incorrect and empty value)
    def factoryTypes = [
        empty:                          "",
        correctConnFact:                "CF",
        correctQueue:                   "QCF",
        correctTopic:                   "TCF",
        incorrect:                      "/:/:/\" Incorrect Bus Name",
    ]

    @Shared //* Required Parameter (need incorrect and empty value)
    def jndiNames = [
        empty:                          "",
        correctQueue:                   "com.jndi.myWMQJMSAppFactQueue",
        correctTopic:                   "com.jndi.myWMQJMSAppFactTopic",
        correctForFactType:             "com.jndi.myWMQJMSAppFactType",
        incorrect:                      "/:/:/\" incorrect Factory JNDI Name",
    ]

        // Not required Parameter

    @Shared //* Optional Parameter
    def factoryAdministrativeDescriptions = [
        empty:                          "",
        correctQueue:                   "Factory Administrative Description Queue",
        correctTopic:                   "Factory Administrative Description Topic",
        incorrect:                      "What the incorrect value for this parameter?",
    ]

    @Shared //* Optional Parameter
    def clientChannelDefinitionUrls = [
        empty:                          "",
        correct:                        "",
        incorrect:                      "",
    ]

    @Shared //* Optional Parameter
    def clientChannelDefinitionQueueManagers = [
        empty:                          "",
        correct:                        "",
        incorrect:                      "",
    ]

    @Shared //* Optional Parameter
    def additionalOptions = [
        empty:                          "",
        correct:                        "",
        incorrect:                      "",
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
        successCreateQ:                 "SIB JMS Connection Factory $factoryAdministrativeNames.correctQueue has been created",
        successCreateT:                 "SIB JMS Connection Factory $factoryAdministrativeNames.correctTopic has been created",
        successUpdateQ:                 "SIB JMS Connection Factory $factoryAdministrativeNames.correctQueue has been updated",
        successUpdateT:                 "SIB JMS Connection Factory $factoryAdministrativeNames.correctTopic has been updated",
        incorrectConfiguration:         "Configuration '"+pluginConfigurationNames.incorrect+"' doesn't exist",
        incorrectScope:                 "Error found in String \"\"; cannot create ObjectName", //"target object is required",
        incorrectFactName:              "A resource with JNDI name $jndiNames.correctQueue already exists as a different resource type. You must use a unique name",
        incorrectBusName:               "Incorrect Bus Name",
        incorrectJNDIName:              "[-jndiName \"$jndiNames.incorrect\" -name \"$factoryAdministrativeNames.correctQueue\" -busName \"$busNames.correctQueue\" ]",
        incorrectConnFacType:           "Incorrect value for type parameter: incorrect Factory Type",
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
        importProject(testProjectName, 'dsl/CreateOrUpdateSIBConnectionFactory/CreateOrUpdateWMQConnectionFactory.dsl', [projectName: testProjectName, wasResourceName:wasResourceName])
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
            pluginConfigurationName                 | factoryScope              | factoryAdministrativeName             | factoryType               | jndiName           /*Not Required*/   | factoryAdministrativeDescription                  | clientChannelDefinitionUrl                | clientChannelDefinitionQueueManager               | additionalOption                  | wasHost                   | expectedOutcome                   | expectedSummaryMessage
//for Queue

//for Topic
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
            assert upperStepSummary =~ expectedSummaryMessage

        where: 'The following params will be: '
            pluginConfigurationName                 | factoryScope              | factoryAdministrativeName             | factoryType               | jndiName           /*Not Required*/   | factoryAdministrativeDescription                  | clientChannelDefinitionUrl                | clientChannelDefinitionQueueManager               | additionalOption                  | wasHost                   | expectedOutcome                   | expectedSummaryMessage
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
                    confignameCOUSIBCF:                             '$parameters.pluginConfigurationName',
                    factoryScopeCOUWMQCF:                           '$parameters.factoryScope',
                    factoryAdministrativeNameCOUWMQCF:              '$parameters.factoryAdministrativeName',
                    factoryTypeCOUWMQCF:                            '$parameters.factoryType',
                    jndiNameCOUWMQCF:                               '$parameters.jndiName',
                    factoryAdministrativeDescriptionCOUWMQCF:       '$parameters.factoryAdministrativeDescription',
                    clientChannelDefinitionUrlCOUWMQCF:             '$parameters.clientChannelDefinitionUrl,
                    clientChannelDefinitionQueueManagerCOUWMQCF:    '$parameters.clientChannelDefinitionQueueManager',
                    additionalOptionsCOUWMQCF:                      '$parameters.additionalOption',
                    wasResourceName:                                '$parameters.wasHost',
                ]
            )
        """
        return dsl(code)
    }
}