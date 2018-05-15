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
        correctQueue:                   "DefaultBusQ",
        correctTopic:                   "DefaultBusT",
        incorrect:                      "/:/:/\" Incorrect Bus Name",
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
        correctWMQQueue:                "MyWMQJMSConnFactQueueForDelete",
        correctWMQTopic:                "MyWMQJMSConnFactTopicForDelete",
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
        correctWMQ:                     "WMQ",
        correctSIB:                     "SIB",
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
        successDeleteSIBQ:              "SIB JMS Connection Factory $specAdministrativeNames.correctSIBQueue has been deleted for /$specScopes.correctOneNodeMessage/ scope",
        successDeleteSIBT:              "SIB JMS Connection Factory $specAdministrativeNames.correctSIBTopic has been deleted for /$specScopes.correctOneNodeMessage/ scope",
        successDeleteWMQQ:              "WMQ JMS Connection Factory $specAdministrativeNames.correctWMQQueue has been deleted for /$specScopes.correctOneNodeMessage/ scope",
        successDeleteWMQT:              "WMQ JMS Connection Factory $specAdministrativeNames.correctWMQTopic has been deleted for /$specScopes.correctOneNodeMessage/ scope",
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
        importProject(testProjectName, 'dsl/DeleteConnectionFactory/CreateOrUpdateSIBConnectionFactory.dsl', [projectName: testProjectName, wasResourceName:wasResourceName])
        importProject(testProjectName, 'dsl/DeleteConnectionFactory/CreateOrUpdateWMQConnectionFactory.dsl', [projectName: testProjectName, wasResourceName:wasResourceName])
        importProject(testProjectName, 'dsl/DeleteConnectionFactory/DeleteСonnectionFactory.dsl', [projectName: testProjectName, wasResourceName:wasResourceName])
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



}
