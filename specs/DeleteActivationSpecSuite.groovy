import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import PluginTestHelper


@Stepwise
class DeleteActivationSpecSuite extends PluginTestHelper {

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
    def testProjectName =           "EC-WebSphere-SystemTests"
    @Shared
    def testProcedureName =         "DeleteActivationSpec"
    @Shared
    def preparationProcedureName1 = "CreateorUpdateSIBActivationSpec"
    @Shared
    def preparationProcedureName2 = "CreateorUpdateWMQActivationSpec"

    /**
     * Common Maps: General Maps fpr different fields
     */

    @Shared
    def checkBoxValues = [
        unchecked: 	"0",
        checked: 	"1",
    ]

    /**
     * Parameters for Test Setup
     */

    @Shared //* Required Parameter (need incorrect and empty value)
    def specJNDINames = [
        empty:                          "",
        correctSIBQueue:                "com.jndi.mySIBJMSAppSpecQueueForDelete",
        correctSIBTopic:                "com.jndi.mySIBJMSAppSpecTopicForDelete",
        correctWMQQueue:                "com.jndi.myWMQJMSAppSpecQueueForDelete",
        correctWMQTopic:                "com.jndi.myWMQJMSAppSpecTopicForDelete",
        incorrect:                      "incorrect Spec JNDI Name ForDelete",
    ]

    @Shared //* Required Parameter (need incorrect and empty value)
    def destinationJNDINames = [
        empty:                          "",
        correctSIBQueue:                "com.jndi.mySIBJMSDestSpecQueueForDelete",
        correctSIBTopic:                "com.jndi.mySIBJMSDestSpecTopicForDelete",
        correctWMQQueue:                "com.jndi.myWMQJMSDestSpecQueueForDelete",
        correctWMQTopic:                "com.jndi.myWMQJMSDestSpecTopicForDelete",
        incorrect:                      "incorrect destination JNDI Name ForDelete",
    ]

    @Shared // Optional Parameter
    def specAdministrativeDescriptions = [
        empty:                          "",
        correct:                        "Spec Administrative Description for Delete",
        incorrect:                      "",
    ]

    @Shared // Required Parameter (need incorrect and empty value)
    def destinationTypes = [
        empty:                          "",
        correctWMQQueue:                "javax.jms.Queue",
        correctWMQTopic:                "javax.jms.Topic",
        correctSIBQueue:                "Queue",
        correctSIBTopic:                "Topic",
        incorrect:                      "Incorrect.destinationTypes",
    ]

    @Shared // Optional Parameter
    def messageSelectors = [
        empty:                          "",
        correct:                        "Priority = 9",
        incorrect:                      "/// incorrect \" message Selector",
    ]

    @Shared // Optional Parameter
    def clientChannelDefinitionURLs = [
        empty:                          "",
        correct:                        "http://",
        incorrect:                      "incorrect Client Channel Definition URL",
    ]

    @Shared // Optional Parameter
    def clientChannelDefinitionQueueManagers = [
        empty:                          "",
        correct:                        "",
        incorrect:                      "incorrect Client Channel Definition Queue Manager",
    ]

    @Shared // Optional Parameter
    def additionalOptions = [
        empty:                          "",
        correct:                        "-providerVersion 1.2.3",
        incorrect:                      "incorrect Additional Options",
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
        incorrect: 	                    "incorrect config Name",
    ]

    @Shared     // Required Parameter (need incorrect and empty value)
    def specAdministrativeNames = [
        empty:                          "",
        correctSIBQueue:                "MySIBJMSAppSpecQueueForDelete",
        correctSIBTopic:                "MySIBJMSAppSpecTopicForDelete",
        correctWMQQueue:                "MyWMQJMSAppSpecQueueForDelete",
        correctWMQTopic:                "MyWMQJMSAppSpecTopicForDelete",
        incorrect:                      "Incorrect ForDelete",
    ]

    @Shared     // Required Parameter (need incorrect and empty value)
    def specScopes = [
        empty:                          "",
        correctOneNode:                 "Node="+wasHost+"Node01",
        incorrect:                      "Node=incorrectScopeForDelete",
        correctOneNodeMessage:          "Node:"+wasHost+"Node01",
    ]

    @Shared // Required Parameter (ComboBox, no need incorrect value the incorrect - empty)
    def messagingSystemTypes = [
        empty:                          "",
        correctWMQ:                     "WMQ",
        correctSIB:                     "SIB",
        incorrect:                      "Incorrect Mes sys Type",
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
        successDeleteSIBQ:              "SIB JMS Activation Spec $specAdministrativeNames.correctSIBQueue has been deleted",
        successDeleteSIBT:              "SIB JMS Activation Spec $specAdministrativeNames.correctSIBTopic has been deleted",
        successDeleteWMQQ:              "WMQ JMS Activation Spec $specAdministrativeNames.correctWMQQueue has been deleted",
        successDeleteWMQT:              "WMQ JMS Activation Spec $specAdministrativeNames.correctWMQTopic has been deleted",
        incorrectConfiguration:         "Configuration '"+pluginConfigurationNames.incorrect+"' doesn't exist",
        incorrectScope:                 "",
        incorrectAdmName:               "",
        incorrectMesSysType:            "",
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
    def specJNDIName
    def destinationJNDIName
    def specAdministrativeDescription
    def destinationType
    def messageSelector
    def clientChannelDefinitionURL
    def clientChannelDefinitionQueueManager
    def additionalOption
    // Procedure params
    def pluginConfigurationName
    def specAdministrativeName
    def specScope
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
        importProject(testProjectName, 'dsl/DeleteActivationSpec/CreateOrUpdateSIBActivationSpec.dsl', [projectName: testProjectName, wasResourceName:wasResourceName])
        importProject(testProjectName, 'dsl/DeleteActivationSpec/CreateOrUpdateWMQActivationSpec.dsl', [projectName: testProjectName, wasResourceName:wasResourceName])
        importProject(testProjectName, 'dsl/DeleteActivationSpec/DeleteActivationSpec.dsl', [projectName: testProjectName, wasResourceName:wasResourceName])
        def params = [
            pluginConfigurationName:                pluginConfigurationNames.correctSOAP,
            specScope:                              specScopes.correctOneNode,
            specAdministrativeName:                 specAdministrativeNames.correctSIBQueue,
            specJNDIName:                           specJNDINames.correctSIBQueue,
            destinationJNDIName:                    destinationJNDINames.correctSIBQueue,
            specAdministrativeDescription:          specAdministrativeDescriptions.correct,
            destinationType:                        destinationTypes.correctSIBQueue,
            messageSelector:                        messageSelectors.empty,
            additionalOption:                       additionalOptions.empty,
            wasHost:                                wasHost,
        ]

        def result
        result = createSIBASForDelete(params)
        waitUntil {
            try {
                jobCompleted(result)
            } catch (Exception e) {
                println e.getMessage()
            }
        }

        params = [
            pluginConfigurationName:                pluginConfigurationNames.correctSOAP,
            specScope:                              specScopes.correctOneNode,
            specAdministrativeName:                 specAdministrativeNames.correctSIBTopic,
            specJNDIName:                           specJNDINames.correctSIBTopic,
            destinationJNDIName:                    destinationJNDINames.correctSIBTopic,
            specAdministrativeDescription:          specAdministrativeDescriptions.empty,
            destinationType:                        destinationTypes.correctSIBTopic,
            messageSelector:                        messageSelectors.empty,
            additionalOption:                       additionalOptions.empty,
            wasHost:                                wasHost,
        ]
        result = createSIBASForDelete(params)
        waitUntil {
            try {
                jobCompleted(result)
            } catch (Exception e) {
                println e.getMessage()
            }
        }

        params = [
                pluginConfigurationName:                pluginConfigurationNames.correctSOAP,
                specScope:                              specScopes.correctOneNode,
                specAdministrativeName:                 specAdministrativeNames.correctWMQQueue,
                specJNDIName:                           specJNDINames.correctWMQQueue,
                destinationJNDIName:                    destinationJNDINames.correctWMQQueue,
                destinationType:                        destinationTypes.correctWMQQueue,
                specAdministrativeDescription:          specAdministrativeDescriptions.correct,
                clientChannelDefinitionURL:             clientChannelDefinitionURLs.empty,
                clientChannelDefinitionQueueManager:    clientChannelDefinitionQueueManagers.empty,
                additionalOption:                       additionalOptions.empty,
                wasHost:                                wasHost,
        ]
        result = createWMQASForDelete(params)
        waitUntil {
            try {
                jobCompleted(result)
            } catch (Exception e) {
                println e.getMessage()
            }
        }

        params = [
                pluginConfigurationName:                pluginConfigurationNames.correctSOAP,
                specScope:                              specScopes.correctOneNode,
                specAdministrativeName:                 specAdministrativeNames.correctWMQTopic,
                specJNDIName:                           specJNDINames.correctWMQTopic,
                destinationJNDIName:                    destinationJNDINames.correctWMQTopic,
                destinationType:                        destinationTypes.correctWMQTopic,
                specAdministrativeDescription:          specAdministrativeDescriptions.correct,
                clientChannelDefinitionURL:             clientChannelDefinitionURLs.empty,
                clientChannelDefinitionQueueManager:    clientChannelDefinitionQueueManagers.empty,
                additionalOption:                       additionalOptions.empty,
                wasHost:                                wasHost,
        ]
        result = createWMQASForDelete(params)
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
    def "Delete Activation Spec. Negative Scenarios" (){
        setup: 'Define the parameters for Procedure running'
             def runParams = [
                pluginConfigurationName:                pluginConfigurationName,
                specScope:                              specScope,
                specAdministrativeName:                 specAdministrativeName,
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
            //assert upperStepSummary.contains(expectedSummaryMessage)

        where: 'The following params will be: '
            pluginConfigurationName                 | specScope                         | specAdministrativeName                    | messagingSystemType                   | expectedOutcome                   | expectedSummaryMessage
            pluginConfigurationNames.incorrect      | specScopes.correctOneNode         | specAdministrativeNames.correctSIBQueue   | messagingSystemTypes.correctWMQ       | expectedOutcomes.error            | expectedSummaryMessages.incorrectConfiguration
            pluginConfigurationNames.correctSOAP    | specScopes.incorrect              | specAdministrativeNames.correctSIBQueue   | messagingSystemTypes.correctWMQ       | expectedOutcomes.error            | expectedSummaryMessages.incorrectScope
            pluginConfigurationNames.correctSOAP    | specScopes.correctOneNode         | specAdministrativeNames.incorrect         | messagingSystemTypes.correctWMQ       | expectedOutcomes.error            | expectedSummaryMessages.incorrectAdmName
            pluginConfigurationNames.correctSOAP    | specScopes.correctOneNode         | specAdministrativeNames.correctSIBQueue   | messagingSystemTypes.incorrect        | expectedOutcomes.error            | expectedSummaryMessages.incorrectMesSysType
    }

    /**
     * Positive Scenarios
     */

    @Unroll //Positive Scenarios for delete should be first
    def "Delete Activation Spec. Positive Scenarios" (){
        setup: 'Define the parameters for Procedure running'
             def runParams = [
                pluginConfigurationName:                pluginConfigurationName,
                specScope:                              specScope,
                specAdministrativeName:                 specAdministrativeName,
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
            //assert upperStepSummary.contains(expectedSummaryMessage)

        where: 'The following params will be: '
            pluginConfigurationName                 | specScope                         | specAdministrativeName                    | messagingSystemType                   | expectedOutcome                   | expectedSummaryMessage
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
    def createSIBASForDelete(def parameters) {
        def code = """
            runProcedure(
                projectName:                            '$testProjectName',
                procedureName:                          '$preparationProcedureName1',
                actualParameter: [
                    confignameCOUSIBAS:                     '$parameters.pluginConfigurationName',
                    specScopeCOUSIBAS:                      '$parameters.specScope',
                    specAdministrativeNameCOUSIBAS:         '$parameters.specAdministrativeName',
                    jndiNameCOUSIBAS:                       '$parameters.specJNDIName',
                    destinationJndiNameCOUSIBAS:            '$parameters.destinationJNDIName',
                    specAdministrativeDescriptionCOUSIBAS:  '$parameters.specAdministrativeDescription',
                    destinationTypeCOUSIBAS:                '$parameters.destinationType',
                    messageSelectorCOUSIBAS:                '$parameters.messageSelector',
                    additionalOptionsCOUSIBAS:              '$parameters.additionalOption',
                    wasResourceName:                        '$parameters.wasHost',
                ]
            )
        """
        return dsl(code)
    }

    //Predefined procedure for Creation SIB Activation Spec
    def createWMQASForDelete(def parameters) {
        def code = """
            runProcedure(
                projectName:                            '$testProjectName',
                procedureName:                          '$preparationProcedureName2',
                actualParameter: [
                    confignameCOUWMQAS:                             '$parameters.pluginConfigurationName',
                    specScopeCOUWMQAS:                              '$parameters.specScope',
                    specAdministrativeNameCOUWMQAS:                 '$parameters.specAdministrativeName',
                    jndiNameCOUWMQAS:                               '$parameters.specJNDIName',
                    destinationJndiNameCOUWMQAS:                    '$parameters.destinationJNDIName',
                    destinationJndiTypeCOUWMQAS:                    '$parameters.destinationType',
                    specAdministrativeDescriptionCOUWMQAS:          '$parameters.specAdministrativeDescription',
                    clientChannelDefinitionUrlCOUWMQAS:             '$parameters.clientChannelDefinitionURL',
                    clientChannelDefinitionQueueManagerCOUWMQAS:    '$parameters.clientChannelDefinitionQueueManager',
                    additionalOptionsCOUWMQAS:                      '$parameters.additionalOption',
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
                    confignameDAS:                          '$parameters.pluginConfigurationName',
                    messagingSystemTypeDAS:                 '$parameters.messagingSystemType',
                    activationSpecScopeDAS:                 '$parameters.specScope',
                    activationSpecAdministrativeNameDAS:    '$parameters.specAdministrativeName',
                    wasResourceName:                        '$parameters.wasHost'
                 ]
            )
        """
        return dsl(code)
    }

}