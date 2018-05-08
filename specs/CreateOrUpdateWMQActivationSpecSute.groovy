import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import PluginTestHelper


@Stepwise
class CreateOrUpdateWMQActivationSpecSuite extends PluginTestHelper {

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
    def testProcedureName = 'CreateOrUpdateWMQActivationSpec'

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
        empty:                          '',
        correctSOAP:                    'Web-Sphere-SOAP',
        correctIPC:                     'Web-Sphere-IPC',
        correctJSR160RMI:               'Web-Sphere-JSR160RMI',
        correctNone:                    'Web-Sphere-None',
        correctRMI:                     'Web-Sphere-RMI',
        incorrect: 	                    'incorrect config Name',
    ]


    @Shared // Required Parameter (need incorrect and empty value)
    def specScopes = [
        empty:                          '',
        correctOneNode:                 'Node='+wasHost+'Node01',
        incorrect:                      'Node=incorrectScope',
        correctOneNodeMessage:          'Node:'+wasHost+'Node01',
    ]

    @Shared // Required Parameter (need incorrect and empty value)
    def specAdministrativeNames = [
        empty:                          '',
        correct:                        'MyWMQJMSAppSpec',
        incorrect:                      ':/:/:',
    ]

    @Shared // Required Parameter (need incorrect and empty value)
    def specJNDINames = [
        empty:                          '',
        correct:                        'com.jndi.myWMQJMSAppSpec',
        incorrect:                      'incorrect WMQ JMS Spec JNDI Name',
    ]

    @Shared // Required Parameter (need incorrect and empty value)
    def destinationJNDINames = [
        empty:                          '',
        correct:                        'com.jndi.myWMQJMSDestSpec',
        incorrect:                      'incorrect WMQ Destination JNDI Name',
    ]

    @Shared // Required Parameter (need incorrect and empty value)
    def destinationJNDITypes = [
        empty:                          '',
        correct:                        'javax.jms.Queue',
        incorrect:                      'javax.jms.Incorrect',
    ]

        // Not required Parameter
    @Shared // Optional Parameter
    def specAdministrativeDescriptions = [
        empty:                          '',
        correct:                        'Spec Administrative Description',
        incorrect:                      '',
    ]

    @Shared // Optional Parameter
    def clientChannelDefinitionURLs = [
        empty:                          '',
        correct:                        'http://',
        incorrect:                      'incorrect Client Channel Definition URL',
    ]

    @Shared // Optional Parameter
    def clientChannelDefinitionQueueManagers = [
        empty:                          '',
        correct:                        '',
        incorrect:                      'incorrect Client Channel Definition Queue Manager',
    ]

    @Shared // Optional Parameter
    def additionalOptions = [
        empty:                          '',
        correct:                        '',
        incorrect:                      'incorrect Additional Options',
    ]
    /**
     * Verification Values: Assert values
    */

    @Shared
    def expectedOutcomes = [
        success: 	                    'success',
        error: 		                    'error',
        warning: 	                    'warning',
        running: 	                    'running',
    ]

    @Shared
    def expectedSummaryMessages = [
        empty:                          "",
        successCreate:                  "WMQ JMS Activation Spec $specAdministrativeNames.correct has been created",
        successUpdate:                  "WMQ JMS Activation Spec $specAdministrativeNames.correct has been updated",
        incorrectConfiguration:         "Configuration '"+pluginConfigurationNames.incorrect+"' doesn't exist",
        incorrectScope:                 "target object is required",
    ]

    @Shared expectedJobDetailedResults = [
        empty: '',
    ]

    @Shared expectedLogParts = [
        empty: '',
    ]

    /**
     * Test Parameters: for Where section
     */
    // Procedure params
    def pluginConfigurationName
    def specScope
    def specAdministrativeName
    def specJNDIName
    def destinationJNDIName
    def destinationJNDIType
    //optional parameters
    def specAdministrativeDescription
    def clientChannelDefinitionURL
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
        importProject(testProjectName, 'dsl/CreateOrUpdateWMQActivationSpec/CreateOrUpdateWMQActivationSpec.dsl', [projectName: testProjectName, wasResourceName:wasResourceName])
        dsl 'setProperty(propertyName: "/plugins/EC-WebSphere/project/ec_debug_logToProperty", value: "/myJob/debug_logs")'
     }

    /**
     * Clean Up actions after test will finished
     */

    def doCleanupSpec() {

    }

    /**
     * SimpleTestScenarios
     */


    /**
     * Positive Scenarios
     */

    @Unroll //Positive Scenarios for delete should be first
    def "Create Or Update WMQ Activation Spec. Positive Scenarios and Extended Scenarios" (){
        //setup: 'Define the parameters for Procedure running'
        when: 'Procedure runs: '
             def runParams = [
                pluginConfigurationName:                pluginConfigurationName,
                specScope:                              specScope,
                specAdministrativeName:                 specAdministrativeName,
                specJNDIName:                           specJNDIName,
                destinationJNDIName:                    destinationJNDIName,
                destinationJNDIType:                    destinationJNDIType,
                specAdministrativeDescription:          specAdministrativeDescription,
                clientChannelDefinitionURL:            clientChannelDefinitionURL,
                clientChannelDefinitionQueueManager:    clientChannelDefinitionQueueManager,
                additionalOption:                       additionalOption,
                wasHost:                                wasHost,
            ]
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

        //expect: 'Outcome and Upper Summary verification'
            assert outcome == expectedOutcome
            //assert upperStepSummary.contains(expectedSummaryMessage)

        where: 'The following params will be: '
            pluginConfigurationName                 | specScope                 | specAdministrativeName                    | specJNDIName                  | destinationJNDIName                   | destinationJNDIType                   /*Not required Parameters*/ | specAdministrativeDescription                 | clientChannelDefinitionURL                   | clientChannelDefinitionQueueManager                   | additionalOption                  | expectedOutcome           | expectedSummaryMessage
            pluginConfigurationNames.correctSOAP    | specScopes.correctOneNode | specAdministrativeNames.correct           | specJNDINames.correct         | destinationJNDINames.correct          | destinationJNDITypes.correct          /*Not required Parameters*/ | specAdministrativeDescriptions.correct        | clientChannelDefinitionURLs.empty            | clientChannelDefinitionQueueManagers.empty            | additionalOptions.empty           | expectedOutcomes.success  | expectedSummaryMessages.successCreate
    }

    /**
     * Negative Scenarios
     */

    @Unroll
    def "Create Or Update WMQ Activation Spec. Negative Scenarios and Extended Scenarios" () {
        //setup: 'Define the parameters for Procedure running'
        when: 'Procedure runs: '
             def runParams = [
                pluginConfigurationName:                pluginConfigurationName,
                specScope:                              specScope,
                specAdministrativeName:                 specAdministrativeName,
                specJNDIName:                           specJNDIName,
                destinationJNDIName:                    destinationJNDIName,
                destinationJNDIType:                    destinationJNDIType,
                specAdministrativeDescription:          specAdministrativeDescription,
                clientChannelDefinitionURL:            clientChannelDefinitionURL,
                clientChannelDefinitionQueueManager:    clientChannelDefinitionQueueManager,
                additionalOption:                       additionalOption,
                wasHost:                                wasHost,
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

        //expect: 'Outcome and Upper Summary verification'
            assert outcome == expectedOutcome
            //upperStepSummary.contains(expectedSummaryMessage)

        where: 'The following params will be: '
            pluginConfigurationName                 | specScope                 | specAdministrativeName                    | specJNDIName                  | destinationJNDIName                   | destinationJNDIType                   | specAdministrativeDescription                 | clientChannelDefinitionURLs                   | clientChannelDefinitionQueueManager                   | additionalOption                  | expectedOutcome           | expectedSummaryMessage
            //pluginConfigurationName                 | specScope                 | specAdministrativeName                    | specJNDIName                  | destinationJNDIName                   | destinationJNDIType                   | specAdministrativeDescription                 | clientChannelDefinitionURLs                   | clientChannelDefinitionQueueManager                   | additionalOption                  | outcome                   | upperStepSummary

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
                    confignameCOUWMQAS:                             '$parameters.pluginConfigurationName',
                    specScopeCOUWMQAS:                              '$parameters.specScope',
                    specAdministrativeNameCOUWMQAS:                 '$parameters.specAdministrativeName',
                    jndiNameCOUWMQAS:                               '$parameters.specJNDIName',
                    destinationJndiNameCOUWMQAS:                    '$parameters.destinationJNDIName',
                    destinationJndiTypeCOUWMQAS:                    '$parameters.destinationJNDIType',
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
}