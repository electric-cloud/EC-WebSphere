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
        incorrectTopicScope: '',
        incorrectTopicAdministrativeName: '',
        incorrectTopicName: '',
        incorrectJndiName: ''
    ]

    @Shared
    def testProjectName = 'EC-WebSphere-SystemTests'
    @Shared
    def testProcedureName = 'CreateOrUpdateJMSTopic'
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
        SIB: 'SIB',
        incorrect: 'incorrect',
    ]

    @Shared
    def topicScopes = [
        /**
         * Required
         */
        empty: '',
        correct: 'Node='+wasHost+'Node01',
        incorrect: 'Node=incorrectScope'
    ]

    @Shared
    def topicAdministrativeNames = [
        /**
         * Required
         */
        empty: '',
        correctWMQ: 'MyWMQTopic',
        correctSIB: 'MySIBTopic',
        incorrect: 'incorrect Administrative Name'
    ]

    @Shared
    def topicNames = [
        /**
         * Required
         */
        empty: '',
        correctWMQ: 'MyWMQTopic',
        correctSIB: 'MySIBTopic',
        incorrect: 'incorrect  Topic Name'
    ]

    @Shared
    def jndiNames = [
        /**
         * Required
         */
        empty: '',
        correctWMQ: 'com.jndi.myWMQTopic',
        correctSIB: 'com.jndi.mySIBTopic',
        incorrect: 'incorrect jndi Name'
    ]


    @Shared
    def topicAdministrativeDescriptions = [
        /**
         * Not Required
         * Free-type field - no need incorrect value - not relevant
         */
        empty: '',
        correctWMQ:'Some descriptions: WMQ JMS Topic for HelloWorld application',
        correctSIB:'Some descriptions: SIB JMS Topic for HelloWorld application',
    ]

    @Shared
    def additionalOptions = [
        /**
         * Not Required
         * Free-type field - no need incorrect value - not relevant
         */
        empty: '',
        correctWMQ:'-ccsid 819',
        correctSIB:'',
        incorrect: 'incorrect -ccsid 819 //-brokerVerion V1'
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
        importProject(testProjectName, 'dsl/CheckCreateOrUpdateJMSTopic/CreateOrUpdateJMSTopic.dsl', [projectName: testProjectName, wasResourceName:wasResourceName])
        dsl 'setProperty(propertyName: "/plugins/EC-WebSphere/project/ec_debug_logToProperty", value: "/myJob/debug_logs")'
    }

    def doCleanupSpec() {
    }
 

    @Unroll
    def "Create Or Update JMS Topic. Positive and Extended Scenarios"(){

        when: 'Proceure runs: '
            def runParams = [
                configname: configname,
                messagingSystemType: messagingSystemType,
                topicScope: topicScope,
                topicAdministrativeName: topicAdministrativeName,
                topicName: topicName,
                jndiName: jndiName,
                topicAdministrativeDescription: topicAdministrativeDescription,
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

        configname                      | messagingSystemType       | topicScope            | topicAdministrativeName              | topicName                | jndiName                 | topicAdministrativeDescription               | additionalOption              | expectedOutcome
        // SOAP Config Name
        confignames.correctSOAP         | messagingSystemTypes.WMQ  | topicScopes.correct   | topicAdministrativeNames.correctWMQ  | topicNames.correctWMQ    | jndiNames.correctWMQ     | topicAdministrativeDescriptions.empty        | additionalOptions.empty       | expectedOutcomes.success  
        confignames.correctSOAP         | messagingSystemTypes.WMQ  | topicScopes.correct   | topicAdministrativeNames.correctWMQ  | topicNames.correctWMQ    | jndiNames.correctWMQ     | topicAdministrativeDescriptions.empty        | additionalOptions.empty       | expectedOutcomes.success  
        confignames.correctSOAP         | messagingSystemTypes.WMQ  | topicScopes.correct   | topicAdministrativeNames.correctWMQ  | topicNames.correctWMQ    | jndiNames.correctWMQ     | topicAdministrativeDescriptions.correctWMQ   | additionalOptions.empty       | expectedOutcomes.success  
        confignames.correctSOAP         | messagingSystemTypes.WMQ  | topicScopes.correct   | topicAdministrativeNames.correctWMQ  | topicNames.correctWMQ    | jndiNames.correctWMQ     | topicAdministrativeDescriptions.correctWMQ   | additionalOptions.correctWMQ  | expectedOutcomes.success  
        confignames.correctSOAP         | messagingSystemTypes.SIB  | topicScopes.correct   | topicAdministrativeNames.correctSIB  | topicNames.correctSIB    | jndiNames.correctSIB     | topicAdministrativeDescriptions.empty        | additionalOptions.empty       | expectedOutcomes.success  
        confignames.correctSOAP         | messagingSystemTypes.SIB  | topicScopes.correct   | topicAdministrativeNames.correctSIB  | topicNames.correctSIB    | jndiNames.correctSIB     | topicAdministrativeDescriptions.empty        | additionalOptions.empty       | expectedOutcomes.success  
        confignames.correctSOAP         | messagingSystemTypes.SIB  | topicScopes.correct   | topicAdministrativeNames.correctSIB  | topicNames.correctSIB    | jndiNames.correctSIB     | topicAdministrativeDescriptions.correctSIB   | additionalOptions.empty       | expectedOutcomes.success  
        confignames.correctSOAP         | messagingSystemTypes.SIB  | topicScopes.correct   | topicAdministrativeNames.correctSIB  | topicNames.correctSIB    | jndiNames.correctSIB     | topicAdministrativeDescriptions.correctSIB   | additionalOptions.correctSIB  | expectedOutcomes.success  

        
    }

    @Unroll
    def "Create Or Update JMS Topic. Negative Scenarios"(){

        when: 'Proceure runs: '
            def runParams = [
                configname: configname,
                messagingSystemType: messagingSystemType,
                topicScope: topicScope,
                topicAdministrativeName: topicAdministrativeName,
                topicName: topicName,
                jndiName: jndiName,
                topicAdministrativeDescription: topicAdministrativeDescription,
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
        //assert upperStepSummary == expectedUpperStepSummary

        where: 'The following params will be: '

        configname                      | messagingSystemType               | topicScope            | topicAdministrativeName               | topicName                | jndiName                 | topicAdministrativeDescription                   | additionalOption              | expectedOutcome           
        confignames.incorrect           | messagingSystemTypes.WMQ          | topicScopes.correct   | topicAdministrativeNames.correctWMQ   | topicNames.correctWMQ    | jndiNames.correctWMQ     | topicAdministrativeDescriptions.empty         | additionalOptions.empty       | expectedOutcomes.error    
        confignames.correctSOAP         | messagingSystemTypes.incorrect    | topicScopes.correct   | topicAdministrativeNames.correctWMQ   | topicNames.correctWMQ    | jndiNames.correctWMQ     | topicAdministrativeDescriptions.empty            | additionalOptions.empty       | expectedOutcomes.error    
        confignames.correctSOAP         | messagingSystemTypes.WMQ          | topicScopes.incorrect | topicAdministrativeNames.correctWMQ   | topicNames.correctWMQ    | jndiNames.correctWMQ     | topicAdministrativeDescriptions.empty            | additionalOptions.empty       | expectedOutcomes.error    
        confignames.correctSOAP         | messagingSystemTypes.WMQ          | topicScopes.correct   | topicAdministrativeNames.incorrect    | topicNames.correctWMQ    | jndiNames.correctWMQ     | topicAdministrativeDescriptions.correctWMQ       | additionalOptions.empty       | expectedOutcomes.error    
        confignames.correctSOAP         | messagingSystemTypes.incorrect    | topicScopes.correct   | topicAdministrativeNames.correctSIB   | topicNames.correctSIB    | jndiNames.correctSIB     | topicAdministrativeDescriptions.empty            | additionalOptions.empty       | expectedOutcomes.error    
        confignames.correctSOAP         | messagingSystemTypes.SIB          | topicScopes.incorrect | topicAdministrativeNames.correctSIB   | topicNames.correctSIB    | jndiNames.correctSIB     | topicAdministrativeDescriptions.empty            | additionalOptions.empty       | expectedOutcomes.error    
        confignames.correctSOAP         | messagingSystemTypes.SIB          | topicScopes.correct   | topicAdministrativeNames.incorrect    | topicNames.correctSIB    | jndiNames.correctSIB     | topicAdministrativeDescriptions.correctSIB       | additionalOptions.empty       | expectedOutcomes.error    
    }

    //Run Test Procedure
    def runProcedure(def parameters) {
        def code = """
            runProcedure(
                projectName: '$testProjectName',
                procedureName: '$testProcedureName',
                actualParameter: [
                    confignameCOUJMST: '$parameters.configname',
                    messagingSystemTypeCOUJMST: '$parameters.messagingSystemType',
                    topicScopeCOUJMST: '$parameters.topicScope',
                    topicAdministrativeNameCOUJMST: '$parameters.topicAdministrativeName',
                    topicNameCOUJMST: '$parameters.topicName',
                    jndiNameCOUJMST: '$parameters.jndiName',
                    topicAdministrativeDescriptionCOUJMST: '$parameters.topicAdministrativeDescription',
                    additionalOptionsCOUJMST: '$parameters.additionalOption',
                    wasResourceName: '$parameters.wasHost'
                ]
            )
        """
        return dslWithTimeout(code)
    }

}