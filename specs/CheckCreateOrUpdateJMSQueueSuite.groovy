import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import PluginTestHelper


@Stepwise
class CheckCreateOrUpdateJMSQueue extends PluginTestHelper {
    
    //verification values
    @Shared
    def expectedOutcomes = [
        success: 'success',
        error: 'error',
        warning: 'warning',
        running: 'running'
    ]
    
    @Shared
    def expectedUpperStepSummaries = [
        fieldRequired: 'Some message',
        incorrectComfigname: '',
        incorrectMessagingSystemType: '',
        incorrectQueueScope: '',
        incorrectQueueAdministrativeName: '',
        incorrectQueueName: '',
        incorrectJndiName: ''
    ]

    @Shared
    def testProjectName = 'EC-WebSphere-SystemTests'
    @Shared
    def testProcedureName = 'CreateOrUpdateJMSQueue'
    @Shared 
    def wasHost = System.getenv('WAS_HOST')
    @Shared 
    def wasPath = System.getenv('WSADMIN_PATH')
/**
 * Maps for Possible Values
 */
    @Shared
    def confignames = [
        /**
         * Required
         */
        empty: '',
        correctSOAP: 'Web-Sphere-SOAP',
        correctIPC: 'Web-Sphere-IPC',
        correctJSR160RMI: 'Web-Sphere-JSR160RMI',
        correctNone: 'Web-Sphere-None',
        correctRMI: 'Web-Sphere-RMI',                        
        incorrect: 'incorrect'
    ]

    @Shared
    def messagingSystemTypes = [
        /**
         * Combobox, no need incorrect value
         * Required
         */
        empty: '',
        WMQ: 'WMQ',
        SIB: 'SIB'
    ]

    @Shared
    def queueScopes = [
        /**
         * Required
         */
        empty: '',
        correct: 'Node='+wasHost+'Node01',
        incorrect: 'Node=incorrectScope'
    ]

    @Shared
    def queueAdministrativeNames = [
        /**
         * Required
         */
        empty: '',
        correctWMQ: 'MyWMQQueue',
        correctSIB: 'MySIBQueue',
        incorrect: 'Incorrect Admin Name'
    ]

    @Shared
    def queueNames = [
        /**
         * Required
         */
        empty: '',
        correctWMQ: 'MyWMQQueue',
        correctSIB: 'MySIBQueue',
        incorrect: 'Incorerct queueName'
    ]

    @Shared
    def jndiNames = [
        /**
         * Required
         */
        empty: '',
        correctWMQ: 'com.jndi.myWMQQueue',
        correctSIB: 'com.jndi.mySIBQueue',
        incorrect: 'incorrect jndiNames'
    ]


    @Shared
    def queueAdministrativeDescriptions = [
        /**
         * Not Required
         * Free-type field - no need incorrect value - not relevant
         */
        empty: '',
        correctWMQ:'Some descriptions for My WMQ JMS Queue',
        correctSIB:'Some descriptions for My SIB JMS Queue',
    ]

    @Shared
    def queueManagerNames = [
        /**
         * Not Required
         * Free-type field - no need incorrect value - not relevant
         */
        empty: '',
        correct:''
    ]

    @Shared
    def additionalOptions = [
        /**
         * Not Required
         * Free-type field - no need incorrect value - not relevant
         */
        empty: '',
        correctWMQ:'-ccsid 1208',
        correctSIB:'',
        incorrect: 'incorrect param'
    ]

    // params for where section
    def expectedOutcome
    def expectedUpperStepSummary

    def configname
    def jndiName
    def messagingSystemType
    def queueAdministrativeDescription
    def queueAdministrativeName
    def queueManagerName
    def queueName
    def queueScope
    def additionalOption

   def doSetupSpec() {
        def wasResourceName = System.getenv('WAS_HOST');
        createWorkspace(wasResourceName)
        createConfiguration(confignames.correctSOAP, [doNotRecreate: false])
        createConfiguration(confignames.correctIPC, [doNotRecreate: false])        
        createConfiguration(confignames.correctJSR160RMI, [doNotRecreate: false])        
        createConfiguration(confignames.correctNone, [doNotRecreate: false])        
        createConfiguration(confignames.correctRMI, [doNotRecreate: false])        
        importProject(testProjectName, 'dsl/CheckCreateOrUpdateJMSQueue/CreateOrUpdateJMSQueue.dsl', [projectName: testProjectName, wasResourceName:wasResourceName])
        dsl 'setProperty(propertyName: "/plugins/EC-WebSphere/project/ec_debug_logToProperty", value: "/myJob/debug_logs")'
    }

    def doCleanupSpec() {
    }

    @Unroll
    def "Create Or Update JMS Queue.  Positive and Extended Scenarios"(){

        when: 'Proceure runs: '
            def runParams = [
                configname: configname,
                messagingSystemType: messagingSystemType,
                queueScope: queueScope,
                queueAdministrativeName: queueAdministrativeName,
                queueName: queueName,
                jndiName: jndiName,
                queueManagerName: queueManagerName,
                queueAdministrativeDescription: queueAdministrativeDescription,
                additionalOption: additionalOption,
                wasHost: wasHost,
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

        assert outcome == expectedOutcome

        where: 'The following params will be: '

        configname                      | messagingSystemType         | queueScope          | queueAdministrativeName               | queueName                 | jndiName                  | queueManagerName              | queueAdministrativeDescription                | additionalOption              | expectedOutcome
        // SOAP Config Name
        confignames.correctSOAP         | messagingSystemTypes.WMQ  | queueScopes.correct   | queueAdministrativeNames.correctWMQ   | queueNames.correctWMQ     | jndiNames.correctWMQ     | queueManagerNames.empty        | queueAdministrativeDescriptions.empty         | additionalOptions.empty       | expectedOutcomes.success  
        confignames.correctSOAP         | messagingSystemTypes.WMQ  | queueScopes.correct   | queueAdministrativeNames.correctWMQ   | queueNames.correctWMQ     | jndiNames.correctWMQ     | queueManagerNames.correct      | queueAdministrativeDescriptions.empty         | additionalOptions.empty       | expectedOutcomes.success  
        confignames.correctSOAP         | messagingSystemTypes.WMQ  | queueScopes.correct   | queueAdministrativeNames.correctWMQ   | queueNames.correctWMQ     | jndiNames.correctWMQ     | queueManagerNames.correct      | queueAdministrativeDescriptions.correctWMQ    | additionalOptions.empty       | expectedOutcomes.success  
        confignames.correctSOAP         | messagingSystemTypes.WMQ  | queueScopes.correct   | queueAdministrativeNames.correctWMQ   | queueNames.correctWMQ     | jndiNames.correctWMQ     | queueManagerNames.correct      | queueAdministrativeDescriptions.correctWMQ    | additionalOptions.correctWMQ  | expectedOutcomes.success  
        confignames.correctSOAP         | messagingSystemTypes.SIB  | queueScopes.correct   | queueAdministrativeNames.correctSIB   | queueNames.correctSIB     | jndiNames.correctSIB     | queueManagerNames.empty        | queueAdministrativeDescriptions.empty         | additionalOptions.empty       | expectedOutcomes.success  
        confignames.correctSOAP         | messagingSystemTypes.SIB  | queueScopes.correct   | queueAdministrativeNames.correctSIB   | queueNames.correctSIB     | jndiNames.correctSIB     | queueManagerNames.correct      | queueAdministrativeDescriptions.empty         | additionalOptions.empty       | expectedOutcomes.success  
        confignames.correctSOAP         | messagingSystemTypes.SIB  | queueScopes.correct   | queueAdministrativeNames.correctSIB   | queueNames.correctSIB     | jndiNames.correctSIB     | queueManagerNames.correct      | queueAdministrativeDescriptions.correctSIB    | additionalOptions.empty       | expectedOutcomes.success  
        confignames.correctSOAP         | messagingSystemTypes.SIB  | queueScopes.correct   | queueAdministrativeNames.correctSIB   | queueNames.correctSIB     | jndiNames.correctSIB     | queueManagerNames.correct      | queueAdministrativeDescriptions.correctSIB    | additionalOptions.correctSIB  | expectedOutcomes.success  

        
    }
    

    @Unroll
    def "Create Or Update JMS Queue. Negarive Scenarios"(){

        when: 'Proceure runs: '
            def runParams = [
                configname: configname,
                messagingSystemType: messagingSystemType,
                queueScope: queueScope,
                queueAdministrativeName: queueAdministrativeName,
                queueName: queueName,
                jndiName: jndiName,
                queueManagerName: queueManagerName,
                queueAdministrativeDescription: queueAdministrativeDescription,
                additionalOption: additionalOption,
                wasHost: wasHost,
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

        assert outcome == expectedOutcome
        

        where: 'The following params will be:'
 
        configname                      | messagingSystemType               | queueScope            | queueAdministrativeName                  | queueName                | jndiName                 | queueManagerName              | queueAdministrativeDescription                   | additionalOption              | expectedOutcome           
        confignames.incorrect           | messagingSystemTypes.WMQ          | queueScopes.correct   | queueAdministrativeNames.correctWMQ      | queueNames.correctWMQ    | jndiNames.correctWMQ     | queueManagerNames.empty       | queueAdministrativeDescriptions.empty            | additionalOptions.empty       | expectedOutcomes.error    
        confignames.correctSOAP         | messagingSystemTypes.incorrect    | queueScopes.correct   | queueAdministrativeNames.correctWMQ      | queueNames.correctWMQ    | jndiNames.correctWMQ     | queueManagerNames.empty       | queueAdministrativeDescriptions.empty            | additionalOptions.empty       | expectedOutcomes.error    
        confignames.correctSOAP         | messagingSystemTypes.WMQ          | queueScopes.incorrect | queueAdministrativeNames.correctWMQ      | queueNames.correctWMQ    | jndiNames.correctWMQ     | queueManagerNames.correct     | queueAdministrativeDescriptions.empty            | additionalOptions.empty       | expectedOutcomes.error    
        confignames.correctSOAP         | messagingSystemTypes.WMQ          | queueScopes.correct   | queueAdministrativeNames.incorrect       | queueNames.correctWMQ    | jndiNames.correctWMQ     | queueManagerNames.correct     | queueAdministrativeDescriptions.correctWMQ       | additionalOptions.empty       | expectedOutcomes.error    
        confignames.correctSOAP         | messagingSystemTypes.WMQ          | queueScopes.correct   | queueAdministrativeNames.correctWMQ      | queueNames.incorrect     | jndiNames.correctWMQ     | queueManagerNames.correct     | queueAdministrativeDescriptions.correctWMQ       | additionalOptions.correctWMQ  | expectedOutcomes.error    
        confignames.correctSOAP         | messagingSystemTypes.incorrect    | queueScopes.correct   | queueAdministrativeNames.correctSIB      | queueNames.correctSIB    | jndiNames.correctSIB     | queueManagerNames.empty       | queueAdministrativeDescriptions.empty            | additionalOptions.empty       | expectedOutcomes.error    
        confignames.correctSOAP         | messagingSystemTypes.SIB          | queueScopes.incorrect | queueAdministrativeNames.correctSIB      | queueNames.correctSIB    | jndiNames.correctSIB     | queueManagerNames.correct     | queueAdministrativeDescriptions.empty            | additionalOptions.empty       | expectedOutcomes.error    
        confignames.correctSOAP         | messagingSystemTypes.SIB          | queueScopes.correct   | queueAdministrativeNames.incorrect       | queueNames.correctSIB    | jndiNames.correctSIB     | queueManagerNames.correct     | queueAdministrativeDescriptions.correctSIB       | additionalOptions.empty       | expectedOutcomes.error    
    }

    //Run Test Procedure
    def runProcedure(def parameters) {
        def code = """
            runProcedure(
                projectName: '$testProjectName',
                procedureName: '$testProcedureName',
                actualParameter: [
                    confignameCOUJMSQ: '$parameters.configname',
                    messagingSystemTypeCOUJMSQ: '$parameters.messagingSystemType',
                    queueScopeCOUJMSQ: '$parameters.queueScope',
                    queueAdministrativeNameCOUJMSQ: '$parameters.queueAdministrativeName',
                    queueNameCOUJMSQ: '$parameters.queueName',
                    jndiNameCOUJMSQ: '$parameters.jndiName',
                    queueManagerNameCOUJMSQ: '$parameters.queueManagerName',
                    queueAdministrativeDescriptionCOUJMSQ: '$parameters.queueAdministrativeDescription',
                    additionalOptionsCOUJMSQ: '$parameters.additionalOption',
                    wasResourceName: '$parameters.wasHost'                   
                ]
            )
        """
        return dslWithTimeout(code)
    }
    

}