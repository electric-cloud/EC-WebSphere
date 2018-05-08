import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import PluginTestHelper


@Stepwise
class CreateOrUpdateSIBActivationSpecSuite extends PluginTestHelper {

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
    def testProcedureName = 'CreateorUpdateSIBActivationSpec'

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

    @Shared     //* Required Parameter (need incorrect and empty value)
    def pluginConfigurationNames = [
        empty:                          '',
        correctSOAP:                    'Web-Sphere-SOAP',
        correctIPC:                     'Web-Sphere-IPC',
        correctJSR160RMI:               'Web-Sphere-JSR160RMI',
        correctNone:                    'Web-Sphere-None',
        correctRMI:                     'Web-Sphere-RMI',
        incorrect: 	                    'incorrect config Name',
    ]


    @Shared //* Required Parameter (need incorrect and empty value)
    def specScopes = [
        empty:                          '',
        correctOneNode:                 'Node='+wasHost+'Node01',
        incorrect:                      'Node=incorrectScope',
        correctOneNodeMessage:          'Node:'+wasHost+'Node01',
    ]

    @Shared //* Required Parameter (need incorrect and empty value)
    def specAdministrativeNames = [
        empty:                          '',
        correct:                        'MySIBJMSAppSpec',
        incorrect:                      ':/:/:',
    ]

    @Shared //* Required Parameter (need incorrect and empty value)
    def specJNDINames = [
        empty:                          '',
        correct:                        'com.jndi.mySIBJMSAppSpec',
        incorrect:                      'incorrect Spec JNDI Name',
    ]

    @Shared //* Required Parameter (need incorrect and empty value)
    def destinationJNDINames = [
        empty:                          '',
        correct:                        'com.jndi.mySIBJMSDestSpec',
        incorrect:                      'incorrect destination JNDI Name',
    ]

        // Not required Parameter
    @Shared //* Optional Parameter
    def specAdministrativeDescriptions = [
        empty:                          '',
        correct:                        'Spec Administrative Description',
        incorrect:                      '',
    ]

    @Shared //* Optional Parameter
    def destinationTypes = [
        empty:                          '',
        correctQueue:                   'Queue',
        correctTopic:                   'Topic',
        incorrect:                      'incorrect Destination Type',
    ]

    @Shared //* Optional Parameter
    def messageSelectors = [
        empty:                          '',
        correct:                        'Priority = 9',
        incorrect:                      'incorrect message Selector',
    ]

    @Shared //* Optional Parameter
    def additionalOptions = [
        empty:                          '',
        correct:                        '-subscriptionDurability Durable',
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
    // Procedure params
    def pluginConfigurationName
    def specScope
    def specAdministrativeName
    def specJNDIName
    def destinationJNDIName
    //optional parameters
    def specAdministrativeDescription
    def destinationType
    def messageSelector
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
        importProject(testProjectName, 'dsl/CreateOrUpdateSIBActivationSpec/CreateOrUpdateSIBActivationSpec.dsl', [projectName: testProjectName, wasResourceName:wasResourceName])
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
                specAdministrativeDescription:          specAdministrativeDescription,
                destinationType:                        destinationType
                messageSelector:                        messageSelector,
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
            pluginConfigurationName                 | specScope                 | specAdministrativeName                    | specJNDIName                  | destinationJNDIName                   | destinationJNDIType                   | specAdministrativeDescription                 | destinationType                   | messageSelector                   | additionalOption                  | outcome                   | upperStepSummary

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
                specAdministrativeDescription:          specAdministrativeDescription,
                destinationType:                        destinationType
                messageSelector:                        messageSelector,
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
            pluginConfigurationName                 | specScope                 | specAdministrativeName                    | specJNDIName                  | destinationJNDIName                   | destinationJNDIType                   | specAdministrativeDescription                 | destinationType                   | messageSelector                   | additionalOption                  | outcome                   | upperStepSummary

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
}