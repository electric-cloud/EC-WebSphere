package com.electriccloud.plugin.spec

import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import com.electriccloud.plugin.spec.PluginTestHelper


@Stepwise
class CheckCreateOrUpdateSIBJMSConnectionFactory extends PluginTestHelper {
    
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
    def testProcedureName = 'CreateOrUpdateSIBJMSConnectionFactory'
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
    def CFScopes = [
        /**
         * Required
         */
        empty: '',
        correct: 'Node='+wasHost+'Node01',
        incorrect: 'Node=incorrectScope'
    ]

    @Shared
    def CFAdministrativeNames = [
        /**
         * Required
         */
        empty: '',
        correct: 'MySIBCF',
    ]

    @Shared
    def jndiNames = [
        /**
         * Required
         */
        empty: '',
        correct: 'MySIBCF',
        incorrect: 'incorrect SIB CF NAME'
    ]

    @Shared
    def destJndiNames = [
        /**
         * Required
         */
        empty: '',
        correct: 'com.jndi.sib.myCF',
    ]


    @Shared
    def CFDescriptions = [
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
        created: "SIB JMS Connection Factory $CFAdministrativeNames.correct has been created",
        updated: "SIB JMS Connection Factory $CFAdministrativeNames.correct has been updated",
    ]
    @Shared
    def busNames = [
        correct: 'Default',
        // TODO: Add incorrect bus name
        incorrect: 'Incorrect bus name'
    ]
    // params for where section
    def expectedOutcome
    def expectedUpperStepSummary

    def configName
    def jndiName
    def busName
    def CFDescription
    def CFAdministrativeName
    def CFScope
    // def additionalOptions

    def doSetupSpec() {
        def wasResourceName = System.getenv('WAS_HOST');
        createWorkspace(wasResourceName)
        createConfiguration(configNames.correctSOAP, [doNotRecreate: false])
        createConfiguration(configNames.correctIPC, [doNotRecreate: false])        
        createConfiguration(configNames.correctJSR160RMI, [doNotRecreate: false])        
        createConfiguration(configNames.correctNone, [doNotRecreate: false])        
        createConfiguration(configNames.correctRMI, [doNotRecreate: false])        
        importProject(testProjectName, 'dsl/CreateOrUpdateSIBJMSConnectionFactory/Procedure.dsl', [projectName: testProjectName, wasResourceName:wasResourceName])
        dsl 'setProperty(propertyName: "/plugins/EC-WebSphere/project/ec_debug_logToProperty", value: "/myJob/debug_logs")'
    }

    def doCleanupSpec() {
    }
 

    @Unroll
    def "Create Or Update JMS Connection Factory. Simplest Scenarios"() {

        when: 'Proceure runs: '
        print "WASHOST: $wasHost"
        def runParams = [
            wasHost: wasHost,
            configName: configName,
            busName: busName,
            additionalOptions: '',
            description: '',
            name: CFAdministrativeName,
            jndiName: jndiName,
            scope: CFScope,
            type: '',
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
        def jobSummary = getJobProperty('/myJob/jobSteps/CreateOrUpdateSIBJMSConnectionFactory/summary', result.jobId)
        print "JobSummary: $jobSummary";
        def debugLog = getJobLogs(result.jobId)
        println "Procedure log:\n$debugLog\n"

        assert outcome == "success"
        assert jobSummary == expectedSummary
        where: 'The following params will be: '

        configName              | CFScope          | CFAdministrativeName          | busName          | jndiName          | expectedOutcome | expectedSummary
        configNames.correctSOAP | CFScopes.correct | CFAdministrativeNames.correct | busNames.correct | jndiNames.correct | 'success'       | summaries.created
        configNames.correctSOAP | CFScopes.correct | CFAdministrativeNames.correct | busNames.correct | jndiNames.correct | 'success'       | summaries.updated
        
    }

    @Unroll
    def "Create Or Update JMS Connection Factory. Simplest Negative Scenario"() {

        when: 'Proceure runs: '
        print "WASHOST: $wasHost"
        def runParams = [
            wasHost: wasHost,
            configName: configName,
            busName: busName,
            additionalOptions: '',
            description: '',
            name: CFAdministrativeName,
            jndiName: jndiName,
            scope: CFScope,
            type: '',
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

        configName              | CFScope            | CFAdministrativeName          | destJndiName          | jndiName          | expectedOutcome
        configNames.correctSOAP | CFScopes.incorrect | CFAdministrativeNames.correct | destJndiNames.correct | jndiNames.correct | 'error'

        
    }

    //Run Test Procedure
    def runProcedure(def parameters) {
        def code = """
            runProcedure(
                projectName: '$testProjectName',
                procedureName: '$testProcedureName',
                actualParameter: [
                    wasResourceName:          '$parameters.wasHost',
                    wasConfigName:            '$parameters.configName',
                    wasBusName:               '$parameters.busName',
                    wasAdditionalOptions:     '$parameters.additionalOptions',
                    wasCFDescription:         '$parameters.description',
                    wasCFName:                '$parameters.name',
                    wasJNDIName:            '$parameters.jndiName',
                    wasCFScope:               '$parameters.scope',
                    wasCFType:                '$parameters.type'
                ]
            )
        """
        return dslWithTimeout(code)
    }

}
