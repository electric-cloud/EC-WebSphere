package com.electriccloud.plugin.spec

import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import com.electriccloud.plugin.spec.PluginTestHelper


@Stepwise
class DeleteJMSQueueSuite extends PluginTestHelper {

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
    def testProcedureName =         'DeleteJMSQueue'
    @Shared
    def preparationProcedureName1 = 'CreateOrUpdateJMSQueue'

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
    def queueNames = [
        empty:      '',
        correctWMQ: 'MyWMQQueueForDelete',
        correctSIB: 'MySIBQueueForDelete',
        incorrect:  'Incorrect queueName',
    ]

    @Shared
    def jndiNames = [
        empty:      '',
        correctWMQ: 'com.jndi.myWMQQueueForDelete',
        correctSIB: 'com.jndi.mySIBQueueForDelete',
        incorrect:  'incorrect jndiNames',
    ]

    @Shared
    def queueAdministrativeDescriptions = [
        empty:      '',
        correctWMQ: 'Some descriptions for My WMQ JMS Queue For Delete',
        correctSIB: 'Some descriptions for My SIB JMS Queue For Delete',
    ]

    @Shared
    def queueManagerNames = [
        empty:      '',
        correct:    ''
    ]

    @Shared
    def additionalOptions = [
        empty:      '',
        correctWMQ: '-ccsid 1208',
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
    def queueAdministrativeNames = [
        empty:      '',
        correctWMQ: 'MyWMQQueueForDelete',
        correctSIB: 'MySIBQueueForDelete',
        incorrect:  'Incorrect Administrative Name',
    ]

    @Shared // Required Parameter (need incorrect and empty value)
    def queueScopes = [
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
        successDeleteWMQ:                                   "WMQ JMS Queue "+queueAdministrativeNames.correctWMQ+" has been deleted for /"+queueScopes.correctOneNodeMessage+"/ scope",
        successDeleteSIB:                                   "SIB JMS Queue "+queueAdministrativeNames.correctSIB+" has been deleted for /"+queueScopes.correctOneNodeMessage+"/ scope",        
        incorrectConfiguration:                             "Configuration '"+pluginConfigurationNames.incorrect+"' doesn't exist",
        incorrectQueueNameWMQ:                              "Resource "+queueAdministrativeNames.incorrect+" with type WMQ_Queue does not exist, can't delete",
        incorrectQueueNameSIB:                              "Resource "+queueAdministrativeNames.incorrect+" with type SIB_Queue does not exist, can't delete",
        incorrectMessagingSystemTypeMismatchWMQ_SIB:        "Resource "+queueAdministrativeNames.correctWMQ+" with type SIB_Queue does not exist, can't delete",
        incorrectMessagingSystemTypeMismatchSIB_WMQ:        "Resource "+queueAdministrativeNames.correctSIB+" with type WMQ_Queue does not exist, can't delete",
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
    def queueAdministrativeName
    def queueScope

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
        importProject(testProjectName, 'dsl/CheckCreateOrUpdateJMSQueue/CreateOrUpdateJMSQueue.dsl', [projectName: testProjectName, wasResourceName:wasResourceName])
        importProject(testProjectName, 'dsl/DeleteJMSQueue/DeleteJMSQueue.dsl', [projectName: testProjectName, wasResourceName:wasResourceName])
        def params = [
            pluginConfigurationName:        pluginConfigurationNames.correctSOAP,
            messagingSystemType:            messagingSystemTypes.correctWMQ,
            queueScope:                     queueScopes.correctOneNode,
            queueAdministrativeName:        queueAdministrativeNames.correctWMQ,
            queueName:                      queueNames.correctWMQ,
            jndiName:                       jndiNames.correctWMQ,
            queueManagerName:               queueManagerNames.correct,
            queueAdministrativeDescription: queueAdministrativeDescriptions.correctWMQ,
            additionalOption:               additionalOptions.correctWMQ,
            wasHost:                        wasHost,
        ]        
        def result 
        result = createQueueForDelete(params)
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
            queueScope:                     queueScopes.correctOneNode,
            queueAdministrativeName:        queueAdministrativeNames.correctSIB,
            queueName:                      queueNames.correctSIB,
            jndiName:                       jndiNames.correctSIB,
            queueManagerName:               queueManagerNames.correct,
            queueAdministrativeDescription: queueAdministrativeDescriptions.correctSIB,
            additionalOption:               additionalOptions.correctSIB,
            wasHost:                        wasHost,
        ]
        result = createQueueForDelete(params)
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
    def "Delete JMS Queue. Negative Scenarios" (){
        when: 'Procedure runs: '
             def runParams = [
                pluginConfigurationName:    pluginConfigurationName,
                messagingSystemType:        messagingSystemType,
                queueAdministrativeName:    queueAdministrativeName,
                queueScope:                 queueScope,
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
            pluginConfigurationName                 | messagingSystemType               | queueAdministrativeName               | queueScope                    | expectedOutcome           | expectedSummaryMessage
            //pluginConfigurationNames.correctSOAP    | messagingSystemTypes.correctWMQ   | queueAdministrativeNames.correctWMQ   | queueScopes.correctOneNode    | expectedOutcomes.error    | expectedSummaryMessages.empty
            pluginConfigurationNames.incorrect      | messagingSystemTypes.correctSIB   | queueAdministrativeNames.correctWMQ   | queueScopes.correctOneNode    | expectedOutcomes.error    | expectedSummaryMessages.incorrectConfiguration
            pluginConfigurationNames.correctSOAP    | messagingSystemTypes.correctSIB   | queueAdministrativeNames.correctWMQ   | queueScopes.correctOneNode    | expectedOutcomes.error    | expectedSummaryMessages.incorrectMessagingSystemTypeMismatchWMQ_SIB            
            pluginConfigurationNames.correctSOAP    | messagingSystemTypes.correctWMQ   | queueAdministrativeNames.correctSIB   | queueScopes.correctOneNode    | expectedOutcomes.error    | expectedSummaryMessages.incorrectMessagingSystemTypeMismatchSIB_WMQ
            pluginConfigurationNames.correctSOAP    | messagingSystemTypes.correctWMQ   | queueAdministrativeNames.incorrect    | queueScopes.correctOneNode    | expectedOutcomes.error    | expectedSummaryMessages.incorrectQueueNameWMQ
            pluginConfigurationNames.correctSOAP    | messagingSystemTypes.correctSIB   | queueAdministrativeNames.incorrect    | queueScopes.correctOneNode    | expectedOutcomes.error    | expectedSummaryMessages.incorrectQueueNameSIB
            pluginConfigurationNames.correctSOAP    | messagingSystemTypes.correctWMQ   | queueAdministrativeNames.correctWMQ   | queueScopes.incorrect         | expectedOutcomes.error    | expectedSummaryMessages.incorrectScope
    }

    /**
     * Positive Scenarios
     */

    @Unroll
    def "Delete JMS Queue. Positive and Extended Scenarios" () {
        when: 'Procedure runs: '
             def runParams = [
                pluginConfigurationName:    pluginConfigurationName,
                messagingSystemType:        messagingSystemType,
                queueAdministrativeName:    queueAdministrativeName,
                queueScope:                 queueScope,
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
            pluginConfigurationName                 | messagingSystemType               | queueAdministrativeName               | queueScope                    | expectedOutcome           | expectedSummaryMessage
            pluginConfigurationNames.correctSOAP    | messagingSystemTypes.correctWMQ   | queueAdministrativeNames.correctWMQ   | queueScopes.correctOneNode    | expectedOutcomes.success  | expectedSummaryMessages.successDeleteWMQ
            pluginConfigurationNames.correctSOAP    | messagingSystemTypes.correctSIB   | queueAdministrativeNames.correctSIB   | queueScopes.correctOneNode    | expectedOutcomes.success  | expectedSummaryMessages.successDeleteSIB

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
    def createQueueForDelete(def parameters) {
        def code = """
            runProcedure(
                projectName:                            '$testProjectName',
                procedureName:                          '$preparationProcedureName1',
                actualParameter: [
                    confignameCOUJMSQ:                      '$parameters.pluginConfigurationName',
                    messagingSystemTypeCOUJMSQ:             '$parameters.messagingSystemType',
                    queueScopeCOUJMSQ:                      '$parameters.queueScope',
                    queueAdministrativeNameCOUJMSQ:         '$parameters.queueAdministrativeName',
                    queueNameCOUJMSQ:                       '$parameters.queueName',
                    jndiNameCOUJMSQ:                        '$parameters.jndiName',
                    queueManagerNameCOUJMSQ:                '$parameters.queueManagerName',
                    queueAdministrativeDescriptionCOUJMSQ:  '$parameters.queueAdministrativeDescription',
                    additionalOptionsCOUJMSQ:               '$parameters.additionalOption',
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
                    confignameDJMSQ:                '$parameters.pluginConfigurationName',
                    messagingSystemTypeDJMSQ:       '$parameters.messagingSystemType',
                    queueAdministrativeNameDJMSQ:   '$parameters.queueAdministrativeName',
                    queueScopeDJMSQ:                '$parameters.queueScope',
                    wasResourceName:                '$parameters.wasHost',                   
                ]
            )
        """
        return dslWithTimeout(code)
    }
}
