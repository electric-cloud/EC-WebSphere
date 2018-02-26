import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import PluginTestHelper


@Stepwise
class CheckCreateOrUpdateJMSTopic extends PluginTestHelper {
    
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
    def testProcedureName = 'CheckCreateOrUpdateJMSTopic'
    /**
    @Shared
    def preProcedureName = 'CreateOrUpdateJMSTopic'    
    */
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
    def topicScopes = [
        /**
         * Required
         */
        empty: '',
        correct: 'Node=websphere85ndNode01',
        incorrect: 'incorrect-Scope'
    ]

    @Shared
    def topicAdministrativeNames = [
        /**
         * Required
         */
        empty: '',
        correct: 'MyWMQTopic',
        incorrect: 'incorrect test1'
    ]

    @Shared
    def topicNames = [
        /**
         * Required
         */
        empty: '',
        correct: '',
        incorrect: 'incorrect test2'
    ]

    @Shared
    def jndiNames = [
        /**
         * Required
         */
        empty: '',
        correct: 'com.jndi.myTopic',
        incorrect: 'incorrect'
    ]


    @Shared
    def topicAdministrativeDescriptions = [
        /**
         * Not Required
         * Free-type field - no need incorrect value - not relevant
         */
        empty: '',
        correct:'Some descriptions: WMQ JMS Topic for HelloWorld application',
    ]

    @Shared
    def additionalOptions = [
        /**
         * Not Required
         * Free-type field - no need incorrect value - not relevant
         */
        empty: '',
        correct:'-ccsid 819',
        incorrect: '-ccsid 819 -brokerVerion V1'
    ]

    // params for where section
    def expectedOutcome
    def expectedUpperStepSummary

    def configname
    def jndiName
    def messagingSystemType
    def topicAdministrativeDescription
    def topicAdministrativeName
    def topicName
    def topicScope
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
    def "Create Or Update JMS Queue. Required paramenetrs Veriication"(){

        when: 'Proceure runs: '
            def runParams = [
                configname: configname,
                messagingSystemType: messagingSystemType,
                topicScope: topicScope,
                topicAdministrativeName: topicAdministrativeName,
                jndiName: jndiName,
                topicAdministrativeDescription: topicAdministrativeDescription,
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

        configname                      | messagingSystemType             | topicScope            | topicAdministrativeName           | jndiName              | topicAdministrativeDescription            | additionalOption          | expectedOutcome           | expectedUpperStepSummary
        confignames.empty               | messagingSystemTypes.WMQ        | topicScopes.correct   | topicAdministrativeNames.correct  | jndiNames.correct     | topicAdministrativeDescriptions.empty     | additionalOptions.empty   | expectedOutcomes.error    | expectedUpperStepSummaries.fieldRequired
        confignames.correctSOAP         | messagingSystemTypes.empty      | topicScopes.empty     | topicAdministrativeNames.correct  | jndiNames.correct     | topicAdministrativeDescriptions.empty     | additionalOptions.empty   | expectedOutcomes.error    | expectedUpperStepSummaries.fieldRequired
        confignames.correctSOAP         | messagingSystemTypes.WMQ        | topicScopes.correct   | topicAdministrativeNames.empty    | jndiNames.correct     | topicAdministrativeDescriptions.empty     | additionalOptions.empty   | expectedOutcomes.error    | expectedUpperStepSummaries.fieldRequired
        confignames.correctSOAP         | messagingSystemTypes.WMQ        | topicScopes.correct   | topicAdministrativeNames.correct  | jndiNames.empty       | topicAdministrativeDescriptions.empty     | additionalOptions.empty   | expectedOutcomes.error    | expectedUpperStepSummaries.fieldRequired
    }


    @Unroll
    def "Create Or Update JMS Queue. Positive Scenarious"(){

        when: 'Proceure runs: '
            def runParams = [
                configname: configname,
                messagingSystemType: messagingSystemType,
                topicScope: topicScope,
                topicAdministrativeName: topicAdministrativeName,
                jndiName: jndiName,
                topicAdministrativeDescription: topicAdministrativeDescription,
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

        configname                      | messagingSystemType       | topicScope            | topicAdministrativeName             | jndiName              | topicAdministrativeDescription            | additionalOption              | expectedOutcome
        // SOAP Config Name
        confignames.correctSOAP         | wsMessagingSystemTypes.WMQ  | topicScopes.correct   | topicAdministrativeNames.correct  | jndiNames.correct     | topicAdministrativeDescriptions.empty     | additionalOptions.empty       | expectedOutcomes.success  
        confignames.correctSOAP         | wsMessagingSystemTypes.SIB  | topicScopes.correct   | topicAdministrativeNames.correct  | jndiNames.correct     | topicAdministrativeDescriptions.empty     | additionalOptions.empty       | expectedOutcomes.success  
        confignames.correctSOAP         | wsMessagingSystemTypes.WMQ  | topicScopes.correct   | topicAdministrativeNames.correct  | jndiNames.correct     | topicAdministrativeDescriptions.empty     | additionalOptions.empty       | expectedOutcomes.success  
        confignames.correctSOAP         | wsMessagingSystemTypes.WMQ  | topicScopes.correct   | topicAdministrativeNames.correct  | jndiNames.correct     | topicAdministrativeDescriptions.correct   | additionalOptions.empty       | expectedOutcomes.success  
        confignames.correctSOAP         | wsMessagingSystemTypes.WMQ  | topicScopes.correct   | topicAdministrativeNames.correct  | jndiNames.correct     | topicAdministrativeDescriptions.correct   | additionalOptions.correct     | expectedOutcomes.success  
        // TODO: IPC Config Name
        // TODO: JSR160RMI Config Name
        // TODO: None Config Name
        // TODO: RMI Config Name
        
    }

    @Unroll
    def "Create Or Update JMS Queue. Negarive Scenarious"(){

        when: 'Proceure runs: '
            def runParams = [
                configname: configname,
                messagingSystemType: messagingSystemType,
                topicScope: topicScope,
                topicAdministrativeName: topicAdministrativeName,
                jndiName: jndiName,
                topicAdministrativeDescription: topicAdministrativeDescription,
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

        configname                      | messagingSystemType               | topicScope            | topicAdministrativeName                 | topicName             | jndiName              | topicAdministrativeDescription                | additionalOption              | expectedOutcome           | expectedUpperStepSummary
        confignames.incorrect           | wsMessagingSystemTypes.WMQ          | topicScopes.correct   | topicAdministrativeNames.correct      | topicNames.correct    | jndiNames.correct     | topicAdministrativeDescriptions.empty         | additionalOptions.empty       | expectedOutcomes.error    | expectedUpperStepSummaries.incorrectComfigname
        confignames.correctSOAP         | wsMessagingSystemTypes.incorrect    | topicScopes.correct   | topicAdministrativeNames.correct      | topicNames.correct    | jndiNames.correct     | topicAdministrativeDescriptions.empty         | additionalOptions.empty       | expectedOutcomes.error    | expectedUpperStepSummaries.incorrectMessagingSystemType 
        confignames.correctSOAP         | wsMessagingSystemTypes.WMQ          | topicScopes.incorrect | topicAdministrativeNames.correct      | topicNames.correct    | jndiNames.correct     | topicAdministrativeDescriptions.empty         | additionalOptions.empty       | expectedOutcomes.error    | expectedUpperStepSummaries.incorrectQueueScope
        confignames.correctSOAP         | wsMessagingSystemTypes.WMQ          | topicScopes.correct   | topicAdministrativeNames.incorrect    | topicNames.correct    | jndiNames.correct     | topicAdministrativeDescriptions.correct       | additionalOptions.empty       | expectedOutcomes.error    | expectedUpperStepSummaries.incorrectQueueAdministrativeName
        confignames.correctSOAP         | wsMessagingSystemTypes.WMQ          | topicScopes.correct   | topicAdministrativeNames.correct      | topicNames.incorrect  | jndiNames.correct     | topicAdministrativeDescriptions.correct       | additionalOptions.correct     | expectedOutcomes.error    | expectedUpperStepSummaries.incorrectQueueName
        confignames.correctSOAP         | wsMessagingSystemTypes.WMQ          | topicScopes.correct   | topicAdministrativeNames.correct      | topicNames.correct    | jndiNames.incorrect   | topicAdministrativeDescriptions.correct       | additionalOptions.correct     | expectedOutcomes.error    | expectedUpperStepSummaries.incorrectJndiName      
        // TODO: IPC Config Name
        // TODO: JSR160RMI Config Name
        // TODO: None Config Name
        // TODO: RMI Config Name
    }

    @IgnoreIf({false})
    @Unroll
    //Extented Test Part 
    def "Create Or Update JMS Queue. Extended"(){

        when: 'Proceure runs: '
            def runParams = [
                configname: configname,
                messagingSystemType: messagingSystemType,
                topicScope: topicScope,
                topicAdministrativeName: topicAdministrativeName,
                jndiName: jndiName,
                topicAdministrativeDescription: topicAdministrativeDescription,
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

        where: 'The following params will be: '
        configname                      | messagingSystemType           | topicScope            | topicAdministrativeName           | jndiName              | topicAdministrativeDescription            | additionalOption              | expectedOutcome
        // SOAP Config Name
        confignames.correctSOAP         | wsMessagingSystemTypes.WMQ    | topicScopes.correct   | topicAdministrativeNames.correct  | jndiNames.correct     | topicAdministrativeDescriptions.empty     | additionalOptions.empty       | expectedOutcomes.success  
    }

    //Run Test Procedure
    def runProcedure(def parameters) {
        def code = """
            runProcedure(
                projectName: '$testProjectName',
                procedureName: '$teProcedureName',
                actualParameter: [
                    confignameCOUJMST: '$parameters.configname',
                    messagingSystemTypeCOUJMST: '$parameters.messagingSystemType',
                    topicScopeCOUJMST: '$parameters.topicScope',
                    topicAdministrativeNameCOUJMST: '$parameters.topicAdministrativeName',
                    topicNameCOUJMST: '$parameters.topicName',
                    jndiNameCOUJMST: '$parameters.jndiName',
                    topicAdministrativeDescriptionCOUJMST: '$parameters.topicAdministrativeDescription',
                    additionalOptionsCOUJMST: '$parameters.additionalOptions',
                ]
            )
        """
        return dsl(code)
    }

}