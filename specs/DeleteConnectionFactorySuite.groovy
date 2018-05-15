import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import PluginTestHelper

@Stepwise
class DeleteConnectionFactorySuite extends PluginTestHelper {

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

    /**
     * Dsl Parameters
     */

    @Shared
    def testProjectName =           "EC-WebSphere-SystemTests"
    @Shared
    def testProcedureName =         "DeleteConnectionFactory"
    @Shared
    def preparationProcedureName1 = "CreateorUpdateSIBConnectionFactory"
    @Shared
    def preparationProcedureName2 = "CreateorUpdateWMQConnectionFactory"

    /**
     * Common Maps: General Maps fpr different fields
     */

    @Shared
    def checkBoxValues = [
        unchecked:  "0",
        checked:    "1",
    ]

    /**
     * Parameters for Test Setup
    */

    @Shared //* Required Parameter (need incorrect and empty value) {SIB Creation Parameter}
    def busNames = [
        empty:                          "",
        correctSIBQueue:                "DefaultBusQ",
        correctSIBTopic:                "DefaultBusT",
        incorrect:                      "/:/:/\" Incorrect Bus Name",
    ]

    @Shared //* Required Parameter (need incorrect and empty value)  {SIB/WMQ Creation Parameter}
    def jndiNames = [
        empty:                          "",
        correctSIBQueue:                "com.jndi.mySIBJMSConnFactQueue",
        correctSIBTopic:                "com.jndi.mySIBJMSConnFactTopic",
        correctWMQCF:                   "com.jndi.myWMQJMSConnFact",
        correctWMQQCF:                  "com.jndi.myWMQJMSQConnFact",
        correctWMQTСF:                  "com.jndi.myWMQJMSTConnFact",
        incorreWMQct:                   "/:/:/\" incorrect Factory JNDI Name",
    ]

    @Shared //* Required Parameter (need incorrect and empty value)  {WMQ Creation Parameter - Req/SIB - No}
    def factoryTypes = [
        empty:                          "",
        correctSIBQueue:                "Queue",
        correctSIBTopic:                "Topic",
        correctWMQCF:                   "CF",
        correctWMQQCF:                  "QCF",
        correctWMQTСF:                  "TCF",
        incorrect:                      "Incorrect Factory Type",
    ]


    @Shared //* Optional Parameter
    def clientChannelDefinitionUrls = [
        empty:                          "",
        correct:                        "",
        incorrect:                      "http://test.com",
    ]

    @Shared //* Optional Parameter
    def clientChannelDefinitionQueueManagers = [
        empty:                          "",
        correct:                        "",
        incorrect:                      "Incorrect CCD Q Manag",
    ]


    @Shared //* Optional Parameter {SIB/WMQ}
    def factoryAdministrativeDescriptions = [
        empty:                          "",
        correctSIBQueue:                "Factory Administrative Description Queue",
        correctSIBTopic:                "Factory Administrative Description Topic",
        correctWMQCF:                   "Factory Administrative Description Conn Fact",
        correctWMQQCF:                  "Factory Administrative Description Queue Conn Fact",
        correctWMQTСF:                  "Factory Administrative Description Topic Conn Fact",
        incorrect:                      "What the incorrect value for this parameter?",
    ]


    @Shared //* Optional Parameter {SIB/WMQ}
    def additionalOptions = [
        empty:                          "",
        incorrect:                      "Incorrect Additional Options",
    ]

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
        incorrect:                      "incorrect config Name",
    ]

    @Shared     // Required Parameter (need incorrect and empty value)
    def factoryAdministrativeNames = [
        empty:                          "",
        correctSIBQueue:                "MySIBJMSConnFactQueueForDelete",
        correctSIBTopic:                "MySIBJMSConnFactTopicForDelete",
        correctWMQCF:                   "MyWMQJMSConnFact",
        correctWMQQCF:                  "MyWMQJMSQueueConnFact",
        correctWMQTСF:                  "MyWMQJMSTopicConnFact",

        incorrect:                      "Incorrect ConnFact ForDelete",
    ]

    @Shared     // Required Parameter (need incorrect and empty value)
    def factoryScopes = [
        empty:                          "",
        correctOneNode:                 "Node="+wasHost+"Node01",
        incorrect:                      "Node=incorrectScopeForDelete",
        correctOneNodeMessage:          "Node:"+wasHost+"Node01",
    ]

    @Shared // Required Parameter (ComboBox, no need incorrect value the incorrect - empty)
    def messagingSystemTypes = [
        empty:                          "",
        correctSIB:                     "SIB",
        correctWMQ:                     "WMQ",
        incorrect:                      "Incorrect Mes sys Type",
    ]


    /**
     * Verification Values: Assert values
    */

    @Shared
    def expectedOutcomes = [
        success:                        "success",
        error:                          "error",
        warning:                        "warning",
        running:                        "running",
    ]

    @Shared
    def expectedSummaryMessages = [
        empty:                          "",
        successDeleteSIBQ:              "SIB JMS Connection Factory $factoryAdministrativeNames.correctSIBQueue has been deleted for /$factoryScopes.correctOneNodeMessage/ scope",
        successDeleteSIBT:              "SIB JMS Connection Factory $factoryAdministrativeNames.correctSIBTopic has been deleted for /$factoryScopes.correctOneNodeMessage/ scope",
        successDeleteWMQCF:             "WMQ JMS Connection Factory $factoryAdministrativeNames.correctWMQCF has been deleted for /$factoryScopes.correctOneNodeMessage/ scope",
        successDeleteWMQQCF:            "WMQ JMS Connection Factory $factoryAdministrativeNames.correctWMQQCF has been deleted for /$factoryScopes.correctOneNodeMessage/ scope",
        successDeleteWMQTCF:            "WMQ JMS Connection Factory $factoryAdministrativeNames.correctWMQTСF has been deleted for /$factoryScopes.correctOneNodeMessage/ scope",
        incorrectConfiguration:         "Configuration '"+pluginConfigurationNames.incorrect+"' doesn't exist",
        incorrectScope:                 "target object is required",
        incorrectAdmName:               "does not exist, can't delete",
        incorrectMesSysType:            "Wrong Messaging System Type",
    ]

    @Shared expectedJobDetailedResults = [
        empty:                          "",
    ]

    @Shared expectedLogParts = [
        empty:                          "",
    ]

    /**
     * Test Parameters: for Where section
     */
    //Pre Procedure Params
    def busName
    def jndiName
    def factoryType
    def clientChannelDefinitionUrl
    def clientChannelDefinitionQueueManager
    def factoryAdministrativeDescription
    def additionalOption
    // Procedure params
    def pluginConfigurationName
    def factoryAdministrativeName
    def factoryScope
    def messagingSystemType
    // expected results
    def expectedOutcome
    def expectedSummaryMessage
    def expectedJobDetailedResult


      def doSetupSpec() {
        def wasResourceName = wasHost
        createWorkspace(wasResourceName)
        createConfiguration(pluginConfigurationNames.correctSOAP, [doNotRecreate: false])
        createConfiguration(pluginConfigurationNames.correctIPC, [doNotRecreate: false])
        createConfiguration(pluginConfigurationNames.correctJSR160RMI, [doNotRecreate: false])
        createConfiguration(pluginConfigurationNames.correctNone, [doNotRecreate: false])
        createConfiguration(pluginConfigurationNames.correctRMI, [doNotRecreate: false])
        //D:\Repositories\Git\EC-WebSphere\specs\dsl\CheckCreateOrUpdateJMSTopic\CreateOrUpdateJMSTopic.dsl
        importProject(testProjectName, 'dsl/DeleteConnectionFactory/CreateOrUpdateSIBConnectionFactory.dsl', [projectName: testProjectName, wasResourceName:wasResourceName])
        importProject(testProjectName, 'dsl/DeleteConnectionFactory/CreateOrUpdateWMQConnectionFactory.dsl', [projectName: testProjectName, wasResourceName:wasResourceName])
        importProject(testProjectName, 'dsl/DeleteConnectionFactory/DeleteConnectionFactory.dsl', [projectName: testProjectName, wasResourceName:wasResourceName])
        def params = [
            pluginConfigurationName:                pluginConfigurationNames.correctSOAP,
            factoryScope:                           factoryScopes.correctOneNode,
            factoryAdministrativeName:              factoryAdministrativeNames.correctSIBQueue,
            busName:                                busNames.correctSIBQueue,
            jndiName:                               jndiNames.correctSIBQueue,
            factoryType:                            factoryTypes.correctSIBQueue,
            factoryAdministrativeDescription:       factoryAdministrativeDescriptions.empty,
            additionalOption:                       additionalOptions.empty,
            wasHost:                                wasHost,
        ]

        def result
        result = createSIBCFForDelete(params)
        waitUntil {
            try {
                jobCompleted(result)
            } catch (Exception e) {
                println e.getMessage()
            }
        }

        params = [
            pluginConfigurationName:                pluginConfigurationNames.correctSOAP,
            factoryScope:                           factoryScopes.correctOneNode,
            factoryAdministrativeName:              factoryAdministrativeNames.correctSIBTopic,
            busName:                                busNames.correctSIBTopic,
            jndiName:                               jndiNames.correctSIBTopic,
            factoryType:                            factoryTypes.correctSIBTopic,
            factoryAdministrativeDescription:       factoryAdministrativeDescriptions.empty,
            additionalOption:                       additionalOptions.empty,
            wasHost:                                wasHost,
        ]
        result = createSIBCFForDelete(params)
        waitUntil {
            try {
                jobCompleted(result)
            } catch (Exception e) {
                println e.getMessage()
            }
        }

        params = [
                pluginConfigurationName:                pluginConfigurationNames.correctSOAP,
                factoryScope:                           factoryScopes.correctOneNode,
                factoryAdministrativeName:              factoryAdministrativeNames.correctWMQCF,
                factoryType:                            factoryTypes.correctWMQCF,
                jndiName:                               jndiNames.correctWMQCF,
                factoryAdministrativeDescription:       factoryAdministrativeDescriptions.empty,
                clientChannelDefinitionUrl:             clientChannelDefinitionUrls.empty,
                clientChannelDefinitionQueueManager:    clientChannelDefinitionQueueManagers.empty,
                additionalOption:                       additionalOptions.empty,
                wasHost:                                wasHost,
        ]
        result = createWMQCFForDelete(params)
        waitUntil {
            try {
                jobCompleted(result)
            } catch (Exception e) {
                println e.getMessage()
            }
        }

        params = [
                pluginConfigurationName:                pluginConfigurationNames.correctSOAP,
                factoryScope:                           factoryScopes.correctOneNode,
                factoryAdministrativeName:              factoryAdministrativeNames.correctWMQQCF,
                factoryType:                            factoryTypes.correctWMQQCF,
                jndiName:                               jndiNames.correctWMQQCF,
                factoryAdministrativeDescription:       factoryAdministrativeDescriptions.empty,
                clientChannelDefinitionUrl:             clientChannelDefinitionUrls.empty,
                clientChannelDefinitionQueueManager:    clientChannelDefinitionQueueManagers.empty,
                additionalOption:                       additionalOptions.empty,
                wasHost:                                wasHost,
        ]
        result = createWMQCFForDelete(params)
        waitUntil {
            try {
                jobCompleted(result)
            } catch (Exception e) {
                println e.getMessage()
            }
        }

        params = [
                pluginConfigurationName:                pluginConfigurationNames.correctSOAP,
                factoryScope:                           factoryScopes.correctOneNode,
                factoryAdministrativeName:              factoryAdministrativeNames.correctWMQTСF,
                factoryType:                            factoryTypes.correctWMQTСF,
                jndiName:                               jndiNames.correctWMQTСF,
                factoryAdministrativeDescription:       factoryAdministrativeDescriptions.empty,
                clientChannelDefinitionUrl:             clientChannelDefinitionUrls.empty,
                clientChannelDefinitionQueueManager:    clientChannelDefinitionQueueManagers.empty,
                additionalOption:                       additionalOptions.empty,
                wasHost:                                wasHost,
        ]
        result = createWMQCFForDelete(params)
        waitUntil {
            try {
                jobCompleted(result)
            } catch (Exception e) {
                println e.getMessage()
            }
        }

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

    @Unroll //Negative Scenarios for delete should be first
    def "Delete Connection Factory. Negative Scenarios" (){
        setup: 'Define the parameters for Procedure running'
             def runParams = [
                pluginConfigurationName:                pluginConfigurationName,
                factoryScope:                           factoryScope,
                factoryAdministrativeName:             factoryAdministrativeName,
                messagingSystemType:                    messagingSystemType,
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
            pluginConfigurationName                 | factoryScope                      | factoryAdministrativeName                     | messagingSystemType                   | expectedOutcome                   | expectedSummaryMessage
            pluginConfigurationNames.incorrect      | factoryScopes.correctOneNode      | factoryAdministrativeNames.correctSIBQueue    | messagingSystemTypes.correctSIB       | expectedOutcomes.error            | expectedSummaryMessages.incorrectConfiguration
            pluginConfigurationNames.correctSOAP    | factoryScopes.incorrect           | factoryAdministrativeNames.correctSIBQueue    | messagingSystemTypes.correctSIB       | expectedOutcomes.error            | expectedSummaryMessages.incorrectScope
            pluginConfigurationNames.correctSOAP    | factoryScopes.correctOneNode      | factoryAdministrativeNames.incorrect          | messagingSystemTypes.correctSIB       | expectedOutcomes.error            | expectedSummaryMessages.incorrectAdmName
            pluginConfigurationNames.correctSOAP    | factoryScopes.correctOneNode      | factoryAdministrativeNames.correctSIBQueue    | messagingSystemTypes.incorrect        | expectedOutcomes.error            | expectedSummaryMessages.incorrectMesSysType
    }

    /**
     * Positive Scenarios
     */

    @Unroll //Positive Scenarios for delete should be first
    def "Delete Connection Factory. Positive Scenarios" (){
        setup: 'Define the parameters for Procedure running'
             def runParams = [
                pluginConfigurationName:                pluginConfigurationName,
                factoryScope:                           factoryScope,
                factoryAdministrativeName:              factoryAdministrativeName,
                messagingSystemType:                    messagingSystemType,
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
            //assert upperStepSummary =~ expectedSummaryMessage

        where: 'The following params will be: '
            pluginConfigurationName                 | factoryScope                      | factoryAdministrativeName                    | messagingSystemType                   | expectedOutcome                   | expectedSummaryMessage
            pluginConfigurationNames.correctSOAP    | factoryScopes.correctOneNode      | factoryAdministrativeNames.correctSIBQueue    | messagingSystemTypes.correctSIB       | expectedOutcomes.success          | expectedSummaryMessages.successDeleteSIBQ
            pluginConfigurationNames.correctSOAP    | factoryScopes.correctOneNode      | factoryAdministrativeNames.correctSIBTopic    | messagingSystemTypes.correctSIB       | expectedOutcomes.success          | expectedSummaryMessages.successDeleteSIBT
            pluginConfigurationNames.correctSOAP    | factoryScopes.correctOneNode      | factoryAdministrativeNames.correctSIBQueue    | messagingSystemTypes.correctWMQCF     | expectedOutcomes.success          | expectedSummaryMessages.successDeleteWMQCF
            pluginConfigurationNames.correctSOAP    | factoryScopes.correctOneNode      | factoryAdministrativeNames.correctSIBQueue    | messagingSystemTypes.correctWMQQCF    | expectedOutcomes.success          | expectedSummaryMessages.successDeleteWMQQCF
            pluginConfigurationNames.correctSOAP    | factoryScopes.correctOneNode      | factoryAdministrativeNames.correctSIBQueue    | messagingSystemTypes.correctWMQTСF    | expectedOutcomes.success          | expectedSummaryMessages.successDeleteWMQTCF

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
    //Predefined procedure for Creation SIB Activation Spec
    def createSIBCFForDelete(def parameters) {
        def code = """
            runProcedure(
                projectName:                            '$testProjectName',
                procedureName:                          '$preparationProcedureName1',
                actualParameter: [
                    confignameCOUSIBCF:                         '$parameters.pluginConfigurationName',
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

    //Predefined procedure for Creation SIB Activation Spec
    def createWMQCFForDelete(def parameters) {
        def code = """
            runProcedure(
                projectName:                            '$testProjectName',
                procedureName:                          '$preparationProcedureName2',
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



    //Run Test Procedure
    def runProcedure(def parameters) {
        def code = """
            runProcedure(
                projectName:                    '$testProjectName',
                procedureName:                  '$testProcedureName',
                actualParameter: [
                    confignameDCF:                          '$parameters.pluginConfigurationName',
                    factoryAdministrativeNameDCF:           '$parameters.factoryAdministrativeName',
                    factoryScopeDCF:                        '$parameters.factoryScope',
                    messagingSystemTypeDCF:                 '$parameters.messagingSystemType',
                    wasResourceName:                        '$parameters.wasHost'
                 ]
            )
        """
        return dsl(code)
    }


}
