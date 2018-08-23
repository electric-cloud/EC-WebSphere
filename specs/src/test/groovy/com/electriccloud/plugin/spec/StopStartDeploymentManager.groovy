package com.electriccloud.plugin.spec

import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import com.electriccloud.plugin.spec.PluginTestHelper


@IgnoreIf({ System.getenv('IS_WAS_ND') != "1"})
@Stepwise
class StopStartDeploymentManager extends PluginTestHelper {

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
    @Shared
    def wasStartScript = System.getenv('WAS_START_DM_SCRIPT')
    @Shared
    def wasStopScript  = System.getenv('WAS_STOP_DM_SCRIPT')

    /**
     * Dsl Parameters
     */

    @Shared
    def testProjectName =           'EC-WebSphere-SystemTests'
    @Shared
    def testProcedureName =         'StopStartDeploymentManager'

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
        successStartDM:                                   "Deployment Manager has been successfully started.",
        successStopDM:                                    "Deployment Manager has been successfully stopped.",
        errorStartDM: 'Shell ' + wasStartScript + '-wrong does not exist',
        errorStopDM:  'Shell ' + wasStopScript + '-wrong does not exist',
        incorrectScope:                                     "target object is required",
    ]
    
    @Shared expectedJobDetailedResults = [
        empty: '',
    ]


    /**
     * Test Parameters: for Where section 
     */ 
    /// cfgName | ASName | MST | expectedOutcome
    def expectedOutcome
    def expectedStartSummaryMessage
    def expectedStopSummaryMessage
    def expectedJobDetailedResult
    def cfgName;

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
        // actual test project
        importProject(testProjectName, 'dsl/StopStartDeploymentManager/Procedure.dsl', [projectName: testProjectName, wasResourceName:wasResourceName])
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

    @Unroll
    def "Stop And Start Deployment Manager. Basic scenario." () {
        when: 'Procedure runs: '
             def runParams = [
                wasHost:    wasHost,
                configName: cfgName,
                wasStartScriptLocation: wasStartScript,
                wasStopScriptLocation: wasStopScript,

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


            assert outcome == "success"
            // assert upperStepSummary.contains(expectedSummaryMessage)
            def jobStopSummary = getJobProperty('/myJob/jobSteps/StopDeploymentManager/summary', result.jobId)
            def jobStartSummary = getJobProperty('/myJob/jobSteps/StartDeploymentManager/summary', result.jobId)
            assert jobStartSummary.contains(expectedStartSummaryMessage)
            assert jobStopSummary.contains(expectedStopSummaryMessage)

        where: 'The following params will be: '
            cfgName                              | expectedStartSummaryMessage            | expectedStopSummaryMessage
            pluginConfigurationNames.correctSOAP | expectedSummaryMessages.successStartDM | expectedSummaryMessages.successStopDM

    }

    @Unroll
    def "Stop And Start Deployment Manager. Negative scenario." () {
        when: 'Procedure runs: '
             def runParams = [
                wasHost:    wasHost,
                configName: cfgName,
                wasStartScriptLocation: wasStartScript + "-wrong",
                wasStopScriptLocation: wasStopScript + "-wrong",

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


            assert outcome == "error"
            // assert upperStepSummary.contains(expectedSummaryMessage)
            def jobStopSummary = getJobProperty('/myJob/jobSteps/StopDeploymentManager/summary', result.jobId)
            def jobStartSummary = getJobProperty('/myJob/jobSteps/StartDeploymentManager/summary', result.jobId)
            
            assert jobStartSummary.contains(expectedStartSummaryMessage)
            assert jobStopSummary.contains(expectedStopSummaryMessage)
        where: 'The following params will be: '
            cfgName                              | expectedStartSummaryMessage          | expectedStopSummaryMessage
            pluginConfigurationNames.correctSOAP | expectedSummaryMessages.errorStartDM | expectedSummaryMessages.errorStopDM

    }

    /**
     * Extended Scenarios
     */

    //@Unroll
    //def "" ()



    //Run Test Procedure
    def runProcedure(def parameters) {
        def code = """
            runProcedure(
                projectName:                    '$testProjectName',
                procedureName:                  '$testProcedureName',
                actualParameter: [
                    wasResourceName: '$parameters.wasHost',
                    wasConfigName:   '$parameters.configName',
                    wasStartScriptLocation: '$parameters.wasStartScriptLocation',
                    wasStopScriptLocation: '$parameters.wasStopScriptLocation',
                ]
            )
        """
        return dslWithTimeout(code)
    }
}
