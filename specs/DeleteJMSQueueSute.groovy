import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import PluginTestHelper


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
    @shared
    def wasDebug =      System.getenv('WAS_DEBUG')
    @shared
    def wasPath =       System.getenv('WSADMIN_PATH')
    @Shared
    def wasAppPath =    System.getenv('WAS_APPPATH')

    /**
     * Dsl Parameters
     */

    @Shared
    def testProjectName =   'EC-WebSphere-SystemTests'
    @Shared
    def testProcedureName = 'DeleteJMSQueue'

    /**
     * Common Maps: General Maps fpr different fields
     */

    @Shared
    def checkBoxValues = [
        unchecked: 	'0',
        checked: 	'1',
    ]

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
        empty:                                  "",
        messagingSystemTypeMismatchSIB_WMQ:     "Resource "+queueNames.correctWMQ+" with type SIB_Queue does not exist, can't delete",
        messagingSystemTypeMismatchWMQ_SIB:     "Resource "+queueNames.correctSIB+" with type WMQ_Queue does not exist, can't delete", 
    ]
    
    @Shared expectedJobDetailedResults = [
        empty: '',
    ]

    /**
     * Parameters for Test Setup
     */
    @Shared
    def queueNames = [
        empty: '',
        correctWMQ: 'MyWMQQueueForDelete',
        correctSIB: 'MySIBQueueForDelete',
        incorrect:  'Incorrect queueName',
    ]

    @Shared
    def jndiNames = [
        empty: '',
        correctWMQ: 'com.jndi.myWMQQueueForDelete',
        correctSIB: 'com.jndi.mySIBQueueForDelete',
        incorrect:  'incorrect jndiNames',
    ]

    @Shared
    def queueAdministrativeDescriptions = [
        empty: '',
        correctWMQ: 'Some descriptions for My WMQ JMS Queue For Delete',
        correctSIB: 'Some descriptions for My SIB JMS Queue For Delete',
    ]

    @Shared
    def queueManagerNames = [
        empty:  '',
        correct:''
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
    def pluginConfigurationNames =[
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
        empty:          '',
        correctOneNode: 'Node='+wasHost+'Node01',
        incorrect:      'Node=incorrectScope',
    ]

        // Not required Parameter (no need incorrect)

    /**
     * Test Parameters: for Where section 
     */ 

    def expectedOutcome
    def expectedSummaryMessage
    def expectedJobDetailedResult

    /**
     * Preparation actions
     */

     def doSetupSpec() {
        def wasResourceName = wasHost
        createWorkspace(wasResourceName)
        createConfiguration(confignames.correctSOAP, [doNotRecreate: false])
        createConfiguration(confignames.correctIPC, [doNotRecreate: false])        
        createConfiguration(confignames.correctJSR160RMI, [doNotRecreate: false])        
        createConfiguration(confignames.correctNone, [doNotRecreate: false])        
        createConfiguration(confignames.correctRMI, [doNotRecreate: false])        
        importProject(testProjectName, 'dsl/CheckCreateOrUpdateJMSQueue/CreateOrUpdateJMSQueue.dsl', [projectName: testProjectName, wasResourceName:wasResourceName])
        importProject(testProjectName, 'dsl/DeleteJMSQueue/DeleteJMSQueue.dsl', [projectName: testProjectName, wasResourceName:wasResourceName])
        def params = [
            configName:                     pluginConfigurationNames.correctSOAP,
            messagingSystemType:            messagingSystemTypes.correctWMQ,
            queueScope:                     queueScopes.correctOneNode,
            queueAdministrativeName:        queueAdministrativeNames.correctWMQ,
            queueName:                      queueNames.correctWMQ,
            jndiName:                       jndiNames.correctWMQ,
            queueManagerName:               queueManagerNames.correct,
            queueAdministrativeDescription: queueAdministrativeDescriptions.correctWMQ,
            additionalOption:               additionalOptions.correctWMQ,
        ]
        createQueueForDelete(params)
        params = [
            configName:                     pluginConfigurationNames.correctSOAP,
            messagingSystemType:            messagingSystemTypes.correctSIB,
            queueScope:                     queueScopes.correctOneNode,
            queueAdministrativeName:        queueAdministrativeNames.correctSIB,
            queueName:                      queueNames.correctSIB,
            jndiName:                       jndiNames.correctSIB,
            queueManagerName:               queueManagerNames.correct,
            queueAdministrativeDescription: queueAdministrativeDescriptions.correctSIB,
            additionalOption:               additionalOptions.correctSIB,
        ]
        createQueueForDelete(params)
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
    def "Delete JMS Queue.  Positive and Extended Scenarios" (){
        when: 'Procedure runs: '
        then: 'Wait until job is completed: '
        where: 'The following params will be: '
    }

    /**
     * Positive Scenarios
     */

    //@Unroll
    //def "" ()

    /**
     * Negative Scenarios
     */

    //@Unroll
    //def "" ()


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
     * Additional procedures
     */
    //Predefined procedure for Creation Queue
    def createQueueForDelete(def parameters) {
        def code = """
            runProcedure(
                projectName:                            '$testProjectName',
                procedureName:                          '$testProcedureName',
                actualParameter: [
                    confignameCOUJMSQ:                      '$parameters.configName',
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
        return dsl(code)
    }

    //Run Test Procedure
    def runProcedure(def parameters) {
        def code = """
            runProcedure(
                projectName:                    '$testProjectName',
                procedureName:                  '$testProcedureName',
                actualParameter: [
                    confignameDJMSQ:                '$parameters.configName',
                    messagingSystemTypeDJMSQ:       '$parameters.messagingSystemType',
                    queueAdministrativeNameDJMSQ:   '$parameters.queueAdministrativeName',
                    queueScopeDJMSQ:                '$parameters.queueScope',
                    wasResourceName:                '$parameters.wasHost'                   
                ]
            )
        """
        return dsl(code)
    }
}