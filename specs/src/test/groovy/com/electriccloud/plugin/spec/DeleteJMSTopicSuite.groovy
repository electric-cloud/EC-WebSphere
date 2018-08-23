package com.electriccloud.plugin.spec

import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import com.electriccloud.plugin.spec.PluginTestHelper


@Stepwise
class DeleteJMSTopicSuite extends PluginTestHelper {

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
    def testProcedureName =         'DeleteJMSTopic'
    @Shared
    def preparationProcedureName1 = 'CreateOrUpdateJMSTopic'

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
    def topicNames = [
        empty:      '',
        correctWMQ: 'MyWMQTopicForDelete',
        correctSIB: 'MySIBTopicForDelete',
        incorrect:  'Incorrect topicName',
    ]

    @Shared
    def jndiNames = [
        empty:      '',
        correctWMQ: 'com.jndi.myWMQTopicForDelete',
        correctSIB: 'com.jndi.mySIBTopicForDelete',
        incorrect:  'incorrect jndiNames',
    ]

    @Shared
    def topicAdministrativeDescriptions = [
        empty:      '',
        correctWMQ: 'Some descriptions for My WMQ JMS Topic For Delete',
        correctSIB: 'Some descriptions for My SIB JMS Topic For Delete',
    ]


    @Shared
    def additionalOptions = [
        empty:      '',
        correctWMQ: '-ccsid 819',
        correctSIB: '',
        incorrect:  'incorrect additional options',
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
    def topicAdministrativeNames = [
        empty:      '',
        correctWMQ: 'MyWMQTopicForDelete',
        correctSIB: 'MySIBTopicForDelete',
        incorrect:  'Incorrect Administrative Name',
    ]

    @Shared // Required Parameter (need incorrect and empty value)
    def topicScopes = [
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
        successDeleteWMQ:                                   "WMQ JMS Topic "+topicAdministrativeNames.correctWMQ+" has been deleted for /"+topicScopes.correctOneNodeMessage+"/ scope",
        successDeleteSIB:                                   "SIB JMS Topic "+topicAdministrativeNames.correctSIB+" has been deleted for /"+topicScopes.correctOneNodeMessage+"/ scope",        
        incorrectConfiguration:                             "Configuration '"+pluginConfigurationNames.incorrect+"' doesn't exist",
        incorrectTopicNameWMQ:                              "Resource "+topicAdministrativeNames.incorrect+" with type WMQ_Topic does not exist, can't delete",
        incorrectTopicNameSIB:                              "Resource "+topicAdministrativeNames.incorrect+" with type SIB_Topic does not exist, can't delete",
        incorrectMessagingSystemTypeMismatchWMQ_SIB:        "Resource "+topicAdministrativeNames.correctWMQ+" with type SIB_Topic does not exist, can't delete",
        incorrectMessagingSystemTypeMismatchSIB_WMQ:        "Resource "+topicAdministrativeNames.correctSIB+" with type WMQ_Topic does not exist, can't delete",
        incorrectScope:                                     "target object is required",
    ]
    
    @Shared expectedJobDetailedResults = [
        empty: '',
    ]


    /**
     * Test Parameters: for Where section 
     */ 

    def expectedOutcome
    def expectedSummaryMessage
    def expectedJobDetailedResult
    def pluginConfigurationName
    def messagingSystemType
    def topicAdministrativeName
    def topicScope

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
        //D:\Repositories\Git\EC-WebSphere\specs\dsl\CheckCreateOrUpdateJMSTopic\CreateOrUpdateJMSTopic.dsl 
        importProject(testProjectName, 'dsl/CheckCreateOrUpdateJMSTopic/CreateOrUpdateJMSTopic.dsl', [projectName: testProjectName, wasResourceName:wasResourceName])
        importProject(testProjectName, 'dsl/DeleteJMSTopic/DeleteJMSTopic.dsl', [projectName: testProjectName, wasResourceName:wasResourceName])
        def params = [
            pluginConfigurationName:        pluginConfigurationNames.correctSOAP,
            messagingSystemType:            messagingSystemTypes.correctWMQ,
            topicScope:                     topicScopes.correctOneNode,
            topicAdministrativeName:        topicAdministrativeNames.correctWMQ,
            topicName:                      topicNames.correctWMQ,
            jndiName:                       jndiNames.correctWMQ,
            topicAdministrativeDescription: topicAdministrativeDescriptions.correctWMQ,
            additionalOption:               additionalOptions.correctWMQ,
            wasHost:                        wasHost,
        ]

        def result
        result = createTopicForDelete(params)
        waitUntil {
            try {
                jobCompleted(result)
            } catch (Exception e) {
                println e.getMessage()
            }
        }

        params = [
            pluginConfigurationName:        pluginConfigurationNames.correctSOAP,
            messagingSystemType:            messagingSystemTypes.correctSIB,
            topicScope:                     topicScopes.correctOneNode,
            topicAdministrativeName:        topicAdministrativeNames.correctSIB,
            topicName:                      topicNames.correctSIB,
            jndiName:                       jndiNames.correctSIB,
            topicAdministrativeDescription: topicAdministrativeDescriptions.correctSIB,
            additionalOption:               additionalOptions.correctSIB,
            wasHost:                        wasHost,
        ]
        result = createTopicForDelete(params)
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
    def "Delete JMS Topic. Negative Scenarios" (){
        when: 'Procedure runs: '
             def runParams = [
                pluginConfigurationName:    pluginConfigurationName,
                messagingSystemType:        messagingSystemType,
                topicAdministrativeName:    topicAdministrativeName,
                topicScope:                 topicScope,
                wasHost:                    wasHost,
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

            assert outcome == expectedOutcome
            assert upperStepSummary.contains(expectedSummaryMessage)

        where: 'The following params will be: '
            pluginConfigurationName                 | messagingSystemType               | topicAdministrativeName               | topicScope                    | expectedOutcome           | expectedSummaryMessage
            //pluginConfigurationNames.correctSOAP    | messagingSystemTypes.correctWMQ   | topicAdministrativeNames.correctWMQ   | topicScopes.correctOneNode    | expectedOutcomes.error    | expectedSummaryMessages.empty
            pluginConfigurationNames.incorrect      | messagingSystemTypes.correctSIB   | topicAdministrativeNames.correctWMQ   | topicScopes.correctOneNode    | expectedOutcomes.error    | expectedSummaryMessages.incorrectConfiguration
            pluginConfigurationNames.correctSOAP    | messagingSystemTypes.correctSIB   | topicAdministrativeNames.correctWMQ   | topicScopes.correctOneNode    | expectedOutcomes.error    | expectedSummaryMessages.incorrectMessagingSystemTypeMismatchWMQ_SIB            
            pluginConfigurationNames.correctSOAP    | messagingSystemTypes.correctWMQ   | topicAdministrativeNames.correctSIB   | topicScopes.correctOneNode    | expectedOutcomes.error    | expectedSummaryMessages.incorrectMessagingSystemTypeMismatchSIB_WMQ
            pluginConfigurationNames.correctSOAP    | messagingSystemTypes.correctWMQ   | topicAdministrativeNames.incorrect    | topicScopes.correctOneNode    | expectedOutcomes.error    | expectedSummaryMessages.incorrectTopicNameWMQ
            pluginConfigurationNames.correctSOAP    | messagingSystemTypes.correctSIB   | topicAdministrativeNames.incorrect    | topicScopes.correctOneNode    | expectedOutcomes.error    | expectedSummaryMessages.incorrectTopicNameSIB
            pluginConfigurationNames.correctSOAP    | messagingSystemTypes.correctWMQ   | topicAdministrativeNames.correctWMQ   | topicScopes.incorrect         | expectedOutcomes.error    | expectedSummaryMessages.incorrectScope
    }

    /**
     * Positive Scenarios
     */

    @Unroll
    def "Delete JMS Topic. Positive and Extended Scenarios" () {
        when: 'Procedure runs: '
             def runParams = [
                pluginConfigurationName:    pluginConfigurationName,
                messagingSystemType:        messagingSystemType,
                topicAdministrativeName:    topicAdministrativeName,
                topicScope:                 topicScope,
                wasHost:                    wasHost,
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

            assert outcome == expectedOutcome
            assert upperStepSummary.contains(expectedSummaryMessage)

        where: 'The following params will be: '
            pluginConfigurationName                 | messagingSystemType               | topicAdministrativeName               | topicScope                    | expectedOutcome           | expectedSummaryMessage
            pluginConfigurationNames.correctSOAP    | messagingSystemTypes.correctWMQ   | topicAdministrativeNames.correctWMQ   | topicScopes.correctOneNode    | expectedOutcomes.success  | expectedSummaryMessages.successDeleteWMQ
            pluginConfigurationNames.correctSOAP    | messagingSystemTypes.correctSIB   | topicAdministrativeNames.correctSIB   | topicScopes.correctOneNode    | expectedOutcomes.success  | expectedSummaryMessages.successDeleteSIB

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
    //Predefined procedure for Creation Topic
    def createTopicForDelete(def parameters) {
        def code = """
            runProcedure(
                projectName:                            '$testProjectName',
                procedureName:                          '$preparationProcedureName1',
                actualParameter: [
                    confignameCOUJMST:                      '$parameters.pluginConfigurationName',
                    messagingSystemTypeCOUJMST:             '$parameters.messagingSystemType',
                    topicScopeCOUJMST:                      '$parameters.topicScope',
                    topicAdministrativeNameCOUJMST:         '$parameters.topicAdministrativeName',
                    topicNameCOUJMST:                       '$parameters.topicName',
                    jndiNameCOUJMST:                        '$parameters.jndiName',
                    topicAdministrativeDescriptionCOUJMST:  '$parameters.topicAdministrativeDescription',
                    additionalOptionsCOUJMST:               '$parameters.additionalOption',
                    wasResourceName:                        '$parameters.wasHost'                   
                ]
            )
        """
        return dslWithTimeout(code)
    }

    //Run Test Procedure
    def runProcedure(def parameters) {
        def code = """
            runProcedure(
                projectName:                    '$testProjectName',
                procedureName:                  '$testProcedureName',
                actualParameter: [
                    confignameDJMST:                '$parameters.pluginConfigurationName',
                    messagingSystemTypeDJMST:       '$parameters.messagingSystemType',
                    topicAdministrativeNameDJMST:   '$parameters.topicAdministrativeName',
                    topicScopeDJMST:                '$parameters.topicScope',
                    wasResourceName:                '$parameters.wasHost'                   
                ]
            )
        """
        return dslWithTimeout(code)
    }
}
