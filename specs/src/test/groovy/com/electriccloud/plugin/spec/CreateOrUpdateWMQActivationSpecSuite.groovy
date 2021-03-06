package com.electriccloud.plugin.spec

import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import com.electriccloud.plugin.spec.PluginTestHelper


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
    def testProcedureName = 'CreateorUpdateWMQActivationSpec'

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
        correct:                        '-providerVersion 1.2.3',
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
        incorrectJNDIName:              "A resource with JNDI name $specJNDINames.correct already exists as a different resource type. You must use a unique name",
        incorrectDestJNDIType:          "The value \"$destinationJNDITypes.incorrect\" is not valid for the destinationType attribute. Valid values are \"javax.jms.Queue\" and \"javax.jms.Topic\".",
        incorrectClientChDefURL:        "Neither the ccdtUrl or ccdtQmgrName attributes may be specified for non-CCDT WebSphere MQ server connections.",
        incorrectclientChDefQM:         "Neither the ccdtUrl or ccdtQmgrName attributes may be specified for non-CCDT WebSphere MQ server connections.",
        incorrectAO:                    "incorrect Additional Options",
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
            assert upperStepSummary.contains(expectedSummaryMessage)

        where: 'The following params will be: '
            pluginConfigurationName                 | specScope                 | specAdministrativeName                    | specJNDIName                  | destinationJNDIName                   | destinationJNDIType                   /*Not required Parameters*/ | specAdministrativeDescription                 | clientChannelDefinitionURL                   | clientChannelDefinitionQueueManager                   | additionalOption                  | expectedOutcome           | expectedSummaryMessage
            pluginConfigurationNames.correctSOAP    | specScopes.correctOneNode | specAdministrativeNames.correct           | specJNDINames.correct         | destinationJNDINames.correct          | destinationJNDITypes.correct          /*Not required Parameters*/ | specAdministrativeDescriptions.empty          | clientChannelDefinitionURLs.empty            | clientChannelDefinitionQueueManagers.empty            | additionalOptions.empty           | expectedOutcomes.success  | expectedSummaryMessages.successCreate
            pluginConfigurationNames.correctSOAP    | specScopes.correctOneNode | specAdministrativeNames.correct           | specJNDINames.correct         | destinationJNDINames.correct          | destinationJNDITypes.correct          /*Not required Parameters*/ | specAdministrativeDescriptions.empty          | clientChannelDefinitionURLs.empty            | clientChannelDefinitionQueueManagers.empty            | additionalOptions.empty           | expectedOutcomes.success  | expectedSummaryMessages.successUpdate
            pluginConfigurationNames.correctSOAP    | specScopes.correctOneNode | specAdministrativeNames.correct           | specJNDINames.correct         | destinationJNDINames.correct          | destinationJNDITypes.correct          /*Not required Parameters*/ | specAdministrativeDescriptions.correct        | clientChannelDefinitionURLs.empty            | clientChannelDefinitionQueueManagers.empty            | additionalOptions.empty           | expectedOutcomes.success  | expectedSummaryMessages.successUpdate
            pluginConfigurationNames.correctSOAP    | specScopes.correctOneNode | specAdministrativeNames.correct           | specJNDINames.correct         | destinationJNDINames.correct          | destinationJNDITypes.correct          /*Not required Parameters*/ | specAdministrativeDescriptions.correct        | clientChannelDefinitionURLs.empty            | clientChannelDefinitionQueueManagers.empty            | additionalOptions.correct         | expectedOutcomes.success  | expectedSummaryMessages.successUpdate
            //TODO:
            // clientChannelDefinitionURL, clientChannelDefinitionQueueManager - No example of the correct data
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
            assert upperStepSummary.contains(expectedSummaryMessage)

        where: 'The following params will be: '
            pluginConfigurationName                 | specScope                 | specAdministrativeName                    | specJNDIName                  | destinationJNDIName                   | destinationJNDIType                   /*Not required Parameters*/ | specAdministrativeDescription                 | clientChannelDefinitionURL                   | clientChannelDefinitionQueueManager                   | additionalOption                  | expectedOutcome           | expectedSummaryMessage
            pluginConfigurationNames.incorrect      | specScopes.correctOneNode | specAdministrativeNames.correct           | specJNDINames.correct         | destinationJNDINames.correct          | destinationJNDITypes.correct          /*Not required Parameters*/ | specAdministrativeDescriptions.empty          | clientChannelDefinitionURLs.empty            | clientChannelDefinitionQueueManagers.empty            | additionalOptions.empty           | expectedOutcomes.error    | expectedSummaryMessages.incorrectConfiguration
            pluginConfigurationNames.correctSOAP    | specScopes.incorrect      | specAdministrativeNames.correct           | specJNDINames.correct         | destinationJNDINames.correct          | destinationJNDITypes.correct          /*Not required Parameters*/ | specAdministrativeDescriptions.empty          | clientChannelDefinitionURLs.empty            | clientChannelDefinitionQueueManagers.empty            | additionalOptions.empty           | expectedOutcomes.error    | expectedSummaryMessages.incorrectScope
            pluginConfigurationNames.correctSOAP    | specScopes.correctOneNode | specAdministrativeNames.incorrect         | specJNDINames.correct         | destinationJNDINames.correct          | destinationJNDITypes.correct          /*Not required Parameters*/ | specAdministrativeDescriptions.empty          | clientChannelDefinitionURLs.empty            | clientChannelDefinitionQueueManagers.empty            | additionalOptions.empty           | expectedOutcomes.error    | expectedSummaryMessages.incorrectJNDIName
 //           pluginConfigurationNames.correctSOAP    | specScopes.correctOneNode | specAdministrativeNames.correct           | specJNDINames.incorrect       | destinationJNDINames.correct          | destinationJNDITypes.correct          /*Not required Parameters*/ | specAdministrativeDescriptions.empty          | clientChannelDefinitionURLs.empty            | clientChannelDefinitionQueueManagers.empty            | additionalOptions.empty           | expectedOutcomes.error    | expectedSummaryMessages.successCreate
 //           pluginConfigurationNames.correctSOAP    | specScopes.correctOneNode | specAdministrativeNames.correct           | specJNDINames.correct         | destinationJNDINames.incorrect        | destinationJNDITypes.correct          /*Not required Parameters*/ | specAdministrativeDescriptions.empty          | clientChannelDefinitionURLs.empty            | clientChannelDefinitionQueueManagers.empty            | additionalOptions.empty           | expectedOutcomes.error    | expectedSummaryMessages.successCreate
            pluginConfigurationNames.correctSOAP    | specScopes.correctOneNode | specAdministrativeNames.correct           | specJNDINames.correct         | destinationJNDINames.correct          | destinationJNDITypes.incorrect        /*Not required Parameters*/ | specAdministrativeDescriptions.empty          | clientChannelDefinitionURLs.empty            | clientChannelDefinitionQueueManagers.empty            | additionalOptions.empty           | expectedOutcomes.error    | expectedSummaryMessages.incorrectDestJNDIType
            pluginConfigurationNames.correctSOAP    | specScopes.correctOneNode | specAdministrativeNames.correct           | specJNDINames.correct         | destinationJNDINames.correct          | destinationJNDITypes.correct          /*Not required Parameters*/ | specAdministrativeDescriptions.empty          | clientChannelDefinitionURLs.incorrect        | clientChannelDefinitionQueueManagers.empty            | additionalOptions.empty           | expectedOutcomes.error    | expectedSummaryMessages.incorrectClientChDefURL
            pluginConfigurationNames.correctSOAP    | specScopes.correctOneNode | specAdministrativeNames.correct           | specJNDINames.correct         | destinationJNDINames.correct          | destinationJNDITypes.correct          /*Not required Parameters*/ | specAdministrativeDescriptions.empty          | clientChannelDefinitionURLs.empty            | clientChannelDefinitionQueueManagers.incorrect        | additionalOptions.empty           | expectedOutcomes.error    | expectedSummaryMessages.incorrectclientChDefQM
            pluginConfigurationNames.correctSOAP    | specScopes.correctOneNode | specAdministrativeNames.correct           | specJNDINames.correct         | destinationJNDINames.correct          | destinationJNDITypes.correct          /*Not required Parameters*/ | specAdministrativeDescriptions.empty          | clientChannelDefinitionURLs.empty            | clientChannelDefinitionQueueManagers.empty            | additionalOptions.incorrect       | expectedOutcomes.error    | expectedSummaryMessages.incorrectAO

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
        return dslWithTimeout(code)
    }
}
