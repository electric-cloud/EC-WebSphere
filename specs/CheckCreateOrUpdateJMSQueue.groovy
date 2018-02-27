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
    def testProjectName = 'EC-WebSphere-Specs-CheckApp'
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
        incorrect: 'incorrect-ConfigName'
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
        correct: 'Node=websphere85ndNode01',
        incorrect: ''
    ]

    @Shared
    def queueAdministrativeNames = [
        /**
         * Required
         */
        empty: '',
        correct: 'MyWMQQueue',
        incorrect: 'Incorrect Name'
    ]

    @Shared
    def queueNames = [
        /**
         * Required
         */
        empty: '',
        correct: 'MyWMQQueue',
        incorrect: '\\Incorerct'
    ]

    @Shared
    def jndiNames = [
        /**
         * Required
         */
        empty: '',
        correct: 'com.jndi.myQueue',
        incorrect: 'incorrect'
    ]


    @Shared
    def queueAdministrativeDescriptions = [
        /**
         * Not Required
         * Free-type field - no need incorrect value - not relevant
         */
        empty: '',
        correct:'Some descriptions for My WMQ JMS Queue',
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
        correct:'-ccsid 1208',
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

    @Ignore
    @Unroll
    def "Create Or Update JMS Queue. Required paramenetrs Veriication"(){

        when: 'Proceure runs: '
            def runParams = [
                configname: configname,
                messagingSystemType: messagingSystemType,
                queueScope: queueScope,
                queueAdministrativeName: queueAdministrativeName,
                jndiName: jndiName,
                queueManagerName: queueManagerName,
                queueAdministrativeDescription: queueAdministrativeDescription,
                additionalOption: additionalOption
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
        def upperStepSummary = getUpperStepSummary()
        println "Procedure log:\n$debugLog\n"

        //Verification part
        assert outcome == expectedOutcome
        assert upperStepSummary == expectedUpperStepSummary

        where: 'The following params will be: '

        configname                    | messagingSystemType             | queueScope            | queueAdministrativeName           | jndiName              | queueManagerName          | queueAdministrativeDescription            | additionalOption          | expectedOutcome           | expectedUpperStepSummary
        confmames.empty               | messagingSystemTypes.WMQ        | queueScopes.correct   | queueAdministrativeNames.correct  | jndiNames.correct     | queueManagerNames.empty   | queueAdministrativeDescriptions.empty     | additionalOptions.empty   | expectedOutcomes.error    | expectedUpperStepSummaries.fieldRequired
        confmames.correctSOAP         | messagingSystemTypes.empty      | queueScopes.empty     | queueAdministrativeNames.correct  | jndiNames.correct     | queueManagerNames.empty   | queueAdministrativeDescriptions.empty     | additionalOptions.empty   | expectedOutcomes.error    | expectedUpperStepSummaries.fieldRequired
        confmames.correctSOAP         | messagingSystemTypes.WMQ        | queueScopes.correct   | queueAdministrativeNames.empty    | jndiNames.correct     | queueManagerNames.empty   | queueAdministrativeDescriptions.empty     | additionalOptions.empty   | expectedOutcomes.error    | expectedUpperStepSummaries.fieldRequired
        confmames.correctSOAP         | messagingSystemTypes.WMQ        | queueScopes.correct   | queueAdministrativeNames.correct  | jndiNames.empty       | queueManagerNames.empty   | queueAdministrativeDescriptions.empty     | additionalOptions.empty   | expectedOutcomes.error    | expectedUpperStepSummaries.fieldRequired
    }


    @Unroll
    def "Create Or Update JMS Queue. Positive Scenarious"(){

        when: 'Proceure runs: '
            def runParams = [
                configname: configname,
                messagingSystemType: messagingSystemType,
                queueScope: queueScope,
                queueAdministrativeName: queueAdministrativeName,
                jndiName: jndiName,
                queueManagerName: queueManagerName,
                queueAdministrativeDescription: queueAdministrativeDescription,
                additionalOption: additionalOption
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

        configname                    | messagingSystemType       | queueScope            | queueAdministrativeName           | jndiName              | queueManagerName              | queueAdministrativeDescription            | additionalOption              | expectedOutcome
        // SOAP Config Name
        confmames.correctSOAP         | messagingSystemTypes.WMQ  | queueScopes.correct   | queueAdministrativeNames.correct  | jndiNames.correct     | queueManagerNames.empty       | queueAdministrativeDescriptions.empty     | additionalOptions.empty       | expectedOutcomes.success  
        confmames.correctSOAP         | messagingSystemTypes.SIB  | queueScopes.correct   | queueAdministrativeNames.correct  | jndiNames.correct     | queueManagerNames.empty       | queueAdministrativeDescriptions.empty     | additionalOptions.empty       | expectedOutcomes.success  
        confmames.correctSOAP         | messagingSystemTypes.WMQ  | queueScopes.correct   | queueAdministrativeNames.correct  | jndiNames.correct     | queueManagerNames.correct     | queueAdministrativeDescriptions.empty     | additionalOptions.empty       | expectedOutcomes.success  
        confmames.correctSOAP         | messagingSystemTypes.WMQ  | queueScopes.correct   | queueAdministrativeNames.correct  | jndiNames.correct     | queueManagerNames.correct     | queueAdministrativeDescriptions.correct   | additionalOptions.empty       | expectedOutcomes.success  
        confmames.correctSOAP         | messagingSystemTypes.WMQ  | queueScopes.correct   | queueAdministrativeNames.correct  | jndiNames.correct     | queueManagerNames.correct     | queueAdministrativeDescriptions.correct   | additionalOptions.correct     | expectedOutcomes.success  
        // TODO: IPC Config Name
        // TODO: JSR160RMI Config Name
        // TODO: None Config Name
        // TODO: RMI Config Name
        
    }
    @Ignore
    @Unroll
    def "Create Or Update JMS Queue. Negarive Scenarious"(){

        when: 'Proceure runs: '
            def runParams = [
                configname: configname,
                messagingSystemType: messagingSystemType,
                queueScope: queueScope,
                queueAdministrativeName: queueAdministrativeName,
                jndiName: jndiName,
                queueManagerName: queueManagerName,
                queueAdministrativeDescription: queueAdministrativeDescription,
                additionalOption: additionalOption
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
        def upperStepSummary = getUpperStepSummary()
        println "Procedure log:\n$debugLog\n"

        assert outcome == expectedOutcome
        assert upperStepSummary == expectedUpperStepSummary

        where: 'The following params will be: '

        configname                    | messagingSystemType               | queueScope            | queueAdministrativeName               | queueName             | jndiName              | queueManagerName              | queueAdministrativeDescription                | additionalOption              | expectedOutcome           | expectedUpperStepSummary
        confmames.incorrect           | messagingSystemTypes.WMQ          | queueScopes.correct   | queueAdministrativeNames.correct      | queueNames.correct    | jndiNames.correct     | queueManagerNames.empty       | queueAdministrativeDescriptions.empty         | additionalOptions.empty       | expectedOutcomes.error    | expectedUpperStepSummaries.incorrectComfigname
        confmames.correctSOAP         | messagingSystemTypes.incorrect    | queueScopes.correct   | queueAdministrativeNames.correct      | queueNames.correct    | jndiNames.correct     | queueManagerNames.empty       | queueAdministrativeDescriptions.empty         | additionalOptions.empty       | expectedOutcomes.error    | expectedUpperStepSummaries.incorrectMessagingSystemType 
        confmames.correctSOAP         | messagingSystemTypes.WMQ          | queueScopes.incorrect | queueAdministrativeNames.correct      | queueNames.correct    | jndiNames.correct     | queueManagerNames.correct     | queueAdministrativeDescriptions.empty         | additionalOptions.empty       | expectedOutcomes.error    | expectedUpperStepSummaries.incorrectQueueScope
        confmames.correctSOAP         | messagingSystemTypes.WMQ          | queueScopes.correct   | queueAdministrativeNames.incorrect    | queueNames.correct    | jndiNames.correct     | queueManagerNames.correct     | queueAdministrativeDescriptions.correct       | additionalOptions.empty       | expectedOutcomes.error    | expectedUpperStepSummaries.incorrectQueueAdministrativeName
        confmames.correctSOAP         | messagingSystemTypes.WMQ          | queueScopes.correct   | queueAdministrativeNames.correct      | queueNames.incorrect  | jndiNames.correct     | queueManagerNames.correct     | queueAdministrativeDescriptions.correct       | additionalOptions.correct     | expectedOutcomes.error    | expectedUpperStepSummaries.incorrectQueueName
        confmames.correctSOAP         | messagingSystemTypes.WMQ          | queueScopes.correct   | queueAdministrativeNames.correct      | queueNames.correct    | jndiNames.incorrect   | queueManagerNames.correct     | queueAdministrativeDescriptions.correct       | additionalOptions.correct     | expectedOutcomes.error    | expectedUpperStepSummaries.incorrectJndiName      
        // TODO: IPC Config Name
        // TODO: JSR160RMI Config Name
        // TODO: None Config Name
        // TODO: RMI Config Name
    }
  
    @IgnoreIf({false})
    @Unroll
    def "Create Or Update JMS Queue. Extended Scenario"(){

        when: 'Proceure runs: '
            def runParams = [
                configname: configname,
                messagingSystemType: messagingSystemType,
                queueScope: queueScope,
                queueAdministrativeName: queueAdministrativeName,
                jndiName: jndiName,
                queueManagerName: queueManagerName,
                queueAdministrativeDescription: queueAdministrativeDescription,
                additionalOption: additionalOption
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
        def upperStepSummary = getUpperStepSummary()
      
        println "Procedure log:\n$debugLog\n"
        assert outcome == expectedOutcome

        where: 'The following params will be:'

        configname                      | messagingSystemType           | queueScope            | queueAdministrativeName           | jndiName              | queueManagerName              | queueAdministrativeDescription            | additionalOption              | expectedOutcome
        // SOAP Config Name
        confignames.correctSOAP         | wsMessagingSystemTypes.WMQ    | queueScopes.correct   | queueAdministrativeNames.correct  | jndiNames.correct     | queueManagerNames.empty       | queueAdministrativeDescriptions.empty     | additionalOptions.empty       | expectedOutcomes.success  
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
                    additionalOptionsCOUJMSQ: '$parameters.additionalOptions',
                ]
            )
        """
        return dsl(code)
    }
    

}