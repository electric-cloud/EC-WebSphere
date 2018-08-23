package com.electriccloud.plugin.spec

import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import com.electriccloud.plugin.spec.PluginTestHelper


@Stepwise
class CheckCreateOrUpdateWMQJMSActivationSpec extends PluginTestHelper {
    
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
        incorrectJndiName: ''
    ]

    @Shared
    def testProjectName = 'EC-WebSphere-SystemTests'
    @Shared
    def testProcedureName = 'CreateOrUpdateWMQJMSActivationSpec'
    @Shared 
    def wasHost = System.getenv('WAS_HOST')
    @Shared 
    def wasPath = System.getenv('WSADMIN_PATH')
/**
 * Maps for Possible Values
 */
    @Shared
    def configNames = [
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
    def ASScopes = [
        /**
         * Required
         */
        empty: '',
        correct: 'Node='+wasHost+'Node01',
        incorrect: 'Node=incorrectScope'
    ]

    @Shared
    def ASAdministrativeNames = [
        /**
         * Required
         */
        empty: '',
        correct: 'MyWMQAS',
    ]

    @Shared
    def jndiNames = [
        /**
         * Required
         */
        empty: '',
        correct: 'MyWMQAS',
        incorrect: 'incorrect WMQ AS NAME'
    ]

    @Shared
    def destJndiNames = [
        /**
         * Required
         */
        empty: '',
        correct: 'com.jndi.wmq.myAS',
    ]
    @Shared
    def destJndiTypes = [
        empty: '',
        correct: 'javax.jms.Queue',
        incorrect: 'javax.jms.Incorrect',
    ]

    @Shared
    def ASDescriptions = [
        /**
         * Not Required
         * Free-type field - no need incorrect value - not relevant
         */
        empty: '',
    ]

    @Shared
    def additionalOptions = [
        /**
         * Not Required
         * Free-type field - no need incorrect value - not relevant
         */
    ]

    @Shared
    def summaries = [
        created: "WMQ JMS Activation Spec $ASAdministrativeNames.correct has been created",
        updated: "WMQ JMS Activation Spec $ASAdministrativeNames.correct has been updated",
    ]
    // params for where section
    def expectedOutcome
    def expectedUpperStepSummary

    def configName
    def jndiName
    def destJndiName
    def destJndiType
    def ASDescription
    def ASAdministrativeName
    def ASScope
    // def additionalOptions

    def doSetupSpec() {
        def wasResourceName = System.getenv('WAS_HOST');
        createWorkspace(wasResourceName)
        createConfiguration(configNames.correctSOAP, [doNotRecreate: false])
        createConfiguration(configNames.correctIPC, [doNotRecreate: false])        
        createConfiguration(configNames.correctJSR160RMI, [doNotRecreate: false])        
        createConfiguration(configNames.correctNone, [doNotRecreate: false])        
        createConfiguration(configNames.correctRMI, [doNotRecreate: false])        
        importProject(testProjectName, 'dsl/CreateOrUpdateWMQJMSActivationSpec/Procedure.dsl', [projectName: testProjectName, wasResourceName:wasResourceName])
        dsl 'setProperty(propertyName: "/plugins/EC-WebSphere/project/ec_debug_logToProperty", value: "/myJob/debug_logs")'
    }

    def doCleanupSpec() {
    }
 

    @Unroll
    def "Create Or Update WMQ Activation Spec. Simplest Scenarios"() {

        when: 'Proceure runs: '
        print "WASHOST: $wasHost"
        def runParams = [
            wasHost: wasHost,
            additionalOptions: '',
            ccdqm: '',
            ccdurl: '',
            configName: configName,
            destinationJndiName: destJndiName,
            destinationJndiType: destJndiType,
            jndiName: jndiName,
            name: ASAdministrativeName,
            description:'',
            scope: ASScope,
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
        def jobSummary = getJobProperty('/myJob/jobSteps/CreateOrUpdateWMQJMSActivationSpec/summary', result.jobId)
        print "JobSummary: $jobSummary";
        def debugLog = getJobLogs(result.jobId)
        println "Procedure log:\n$debugLog\n"
        // expectedOutcome = "success"
        assert outcome == "success"
        assert jobSummary == expectedSummary
        where: 'The following params will be: '

        configName             |ASScope         |ASAdministrativeName         |destJndiName         |destJndiType         |jndiName         |expectedSummary
        configNames.correctSOAP|ASScopes.correct|ASAdministrativeNames.correct|destJndiNames.correct|destJndiTypes.correct|jndiNames.correct|summaries.created
        configNames.correctSOAP|ASScopes.correct|ASAdministrativeNames.correct|destJndiNames.correct|destJndiTypes.correct|jndiNames.correct|summaries.updated
    }

    @Unroll
    def "Create Or Update JMS Activation Spec. Simplest Negative Scenario"() {

        when: 'Proceure runs: '
        print "WASHOST: $wasHost"
        def runParams = [
            wasHost: wasHost,
            additionalOptions: '',
            ccdqm: '',
            ccdurl: '',
            configName: configName,
            destinationJndiName: destJndiName,
            destinationJndiType: destJndiType,
            jndiName: jndiName,
            name: ASAdministrativeName,
            description:'',
            scope: ASScope,
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
        // assert jobSummary == expectedSummary
        where: 'The following params will be: '

        configName             |ASScope            |ASAdministrativeName         |destJndiName           |destJndiType           |jndiName         |expectedOutcome
        configNames.correctSOAP| ASScopes.incorrect|ASAdministrativeNames.correct|destJndiNames.correct  |destJndiTypes.correct  |jndiNames.correct|'error'
        configNames.correctSOAP| ASScopes.correct  |ASAdministrativeNames.correct|destJndiNames.incorrect|destJndiTypes.incorrect|jndiNames.correct|'error'
    }

    //Run Test Procedure
    def runProcedure(def parameters) {
        def code = """
            runProcedure(
                projectName: '$testProjectName',
                procedureName: '$testProcedureName',
                actualParameter: [
                    wasResourceName:        '$parameters.wasHost',
                    wasAdditionalOptions:   '$parameters.additionalOptions',
                    wasCCDQM:               '$parameters.ccdqm',
                    wasCCDURL:              '$parameters.ccdurl',
                    wasConfigName:          '$parameters.configName',
                    wasDestinationJNDIName: '$parameters.destinationJndiName',
                    wasDestinationJNDIType: '$parameters.destinationJndiType',
                    wasASJNDIName:          '$parameters.jndiName',
                    wasASDescription:       '$parameters.description',
                    wasASName:              '$parameters.name',
                    wasASScope:             '$parameters.scope',
                ]
            )
        """
        return dslWithTimeout(code)
    }

}
