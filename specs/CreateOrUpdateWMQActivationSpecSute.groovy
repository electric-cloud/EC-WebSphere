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
    def testProjectName = 'EC-WebSphere-SystemTests'
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
        correct:                        '',
        incorrect:                      '',
    ]

    @Shared // Required Parameter (need incorrect and empty value)
    def specJNDINames = [
        empty:                          '',
        correct:                        'com.jndi.myWMQSpec',
        incorrect:                      'incorrect Spec JNDI Name',
    ]
    
    @Shared // Required Parameter (need incorrect and empty value)
    def destinationJNDINames = [
        empty:                          '',
        correct:                        'com.jndi.myWMQDestSpec',
        incorrect:                      'incorrect destination JNDI Name',
    ]

    @Shared // Required Parameter (need incorrect and empty value)
    def destinationJNDITypes = [
        empty:                          '',
        correct:                        'com.jndi.myWMQDestType',
        incorrect:                      'incorrect destination JNDI Types',
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
        successCreate:                  "",
        successUpdate:                  "",
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
    def pluginConfigurationName
    def specScope
    def specAdministrativeName
    def specJNDIName
    def destinationJNDIName
    def destinationJNDIType
    def specAdministrativeDescription
    def clientChannelDefinitionURLs
    def clientChannelDefinitionQueueManager
    def additionalOption


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
        importProject(testProjectName, 'dsl/CheckCreateOrUpdateJMSQueue/CreateOrUpdateJMSQueue.dsl', [projectName: testProjectName, wasResourceName:wasResourceName])
        importProject(testProjectName, 'dsl/CreateOrUpdateWMQActivationSpec/.dsl', [projectName: testProjectName, wasResourceName:wasResourceName])
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
    def "Create Or Update WMQ Activation Spec. Positive Scenarios and Extended Scenarios" (){
        setup: 'Define the parameters for Procedure running'
             def runParams = [
                pluginConfigurationName:                pluginConfigurationName,
                specScope:                              specScope,
                specAdministrativeName:                 specAdministrativeName,
                specJNDIName:                           specJNDIName,
                destinationJNDIName:                    destinationJNDIName,
                destinationJNDIType:                    destinationJNDIType,
                specAdministrativeDescription:          specAdministrativeDescription,
                clientChannelDefinitionURLs:            clientChannelDefinitionURLs,
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
            outcome == expectedOutcome
            upperStepSummary.contains(expectedSummaryMessage)

        where: 'The following params will be: '
            pluginConfigurationName                 | specScope                 | specAdministrativeName                    | specJNDIName                  | destinationJNDIName                   | destinationJNDIType                   | specAdministrativeDescription                 | clientChannelDefinitionURLs                   | clientChannelDefinitionQueueManager                   | additionalOption                  | outcome                   | upperStepSummary              
            //pluginConfigurationName                 | specScope                 | specAdministrativeName                    | specJNDIName                  | destinationJNDIName                   | destinationJNDIType                   | specAdministrativeDescription                 | clientChannelDefinitionURLs                   | clientChannelDefinitionQueueManager                   | additionalOption                  | outcome                   | upperStepSummary              

    }

    /**
     * Negative Scenarios
     */

    @Unroll
    def "Create Or Update WMQ Activation Spec. Negative Scenarios and Extended Scenarios" () {
        setup: 'Define the parameters for Procedure running'
             def runParams = [
                pluginConfigurationName:                pluginConfigurationName,
                specScope:                              specScope,
                specAdministrativeName:                 specAdministrativeName,
                specJNDIName:                           specJNDIName,
                destinationJNDIName:                    destinationJNDIName,
                destinationJNDIType:                    destinationJNDIType,
                specAdministrativeDescription:          specAdministrativeDescription,
                clientChannelDefinitionURLs:            clientChannelDefinitionURLs,
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
            outcome == expectedOutcome
            upperStepSummary.contains(expectedSummaryMessage)

        where: 'The following params will be: '
            pluginConfigurationName                 | specScope                 | specAdministrativeName                    | specJNDIName                  | destinationJNDIName                   | destinationJNDIType                   | specAdministrativeDescription                 | clientChannelDefinitionURLs                   | clientChannelDefinitionQueueManager                   | additionalOption                  | outcome                   | upperStepSummary              
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
                    confignameCOUWMQAS:                 '$parameters.pluginConfigurationName',
                    wasResourceName:                    '$parameters.wasHost',                   
                ]
            )
        """
        return dsl(code)
    }
}