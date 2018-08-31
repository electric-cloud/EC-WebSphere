package com.electriccloud.plugin.spec

import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import com.electriccloud.plugin.spec.PluginTestHelper

@Unroll
@Stepwise
@Requires({ System.getenv('IS_WAS_ND') == "1"})
class StartDeploymentManager extends PluginTestHelper {

    // Environments Variables
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
    @Shared
    def is_windows = System.getenv("IS_WINDOWS")

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
    def testCases = [
        systemTest1: [
            name: 'C363492',
            description: 'only required values'],
        systemTest2: [
            name: 'C363493',
            description: 'specify Manager Profile'],
        systemTest3: [
            name: 'C363494',
            description: 'Log File Location'],
        systemTest4: [
            name: 'C363495',
            description: 'Timeout - DeploymentManager starts in Time'],
        systemTest5: [
            name: 'C363497',
            description: 'Additional Parameters'],
        systemTest6: [
            name: 'C363498',
            description: 'All parameters'],
        systemTest7: [
            name: 'C363499',
            description: 'empty required fields'],
        systemTest8: [
            name: 'C363502',
            description: 'wrong values'],
        systemTest9: [
            name: 'C363501',
            description: 'incorrect Config Name'],
        systemTest10: [
            name: 'C363683',
            description: 'Start already started server'],
        systemTest11: [
            name: 'C363505',
            description: "Timeout - DeploymentManager doesn't start in Time"],
    ]

    @Shared
    def stopLocations = [
        defaultLocation: is_windows ? 'C:/IBM/WebSphere/bin/stopManager.bat' : '/opt/IBM/WebSphere/AppServer/bin/stopManager.sh',
    ]

    @Shared
    def startLocations = [
        defaultLocation: is_windows ? 'C:/IBM/WebSphere/bin/startManager.bat' : '/opt/IBM/WebSphere/AppServer/bin/startManager.sh',
        wrong: 'wrong/path/startManager.sh'
    ]

    @Shared
    def procName = 'StartDeploymentManager'

    @Shared
    def stopProcName = 'StopDeploymentManager'

    @Shared
    def projectName = "EC-WebSphere Specs $procName Project"

	@Shared
    def profiles = [
        dmgr01: 'Dmgr01',
        wrong:  'wrong'
    ]

    @Shared
    def logLocations = [
        tmp: is_windows ? 'C:/IBM/startLog.log' : '/tmp/startLog.log'
    ]

    @Shared
    def additionalParameters = [
        quiet: "-trace",
        wrong: "-wrong",
        some: "-trace -replacelog"
    ]

    @Shared
    def summaries = [
        'default': "Deployment Manager has been successfully started.",
        'emptyConfig': "Configuration '' doesn't exist",
        'incorrectConfig': "Configuration 'incorrect' doesn't exist",
        'mandatoryShell': "Shell is mandatory",
        'wrongStartLocation': "Shell $startLocations.wrong does not exist",
        'fail': "Failed to start Deployment Manager",    
    ]

    @Shared
    def jobLogs = [
        'default':  ["Generated command line: 'startManagerReplace' -logfile '.*startServer.log'",
                    "ADMU0128I: Starting tool with the Dmgr01 profile", "ADMU3200I: Server launched. Waiting for initialization status"],
        'profile':  ["Generated command line: 'startManagerReplace' -profileName '$profiles.dmgr01'  -logfile '.*startServer.log'",
                    "ADMU0128I: Starting tool with the Dmgr01 profile", "ADMU3200I: Server launched. Waiting for initialization status"],
        'log':      ["Generated command line: 'startManagerReplace' -logfile '$logLocations.tmp'",
                    "ADMU0116I: Tool information is being logged in file $logLocations.tmp", "ADMU3200I: Server launched. Waiting for initialization status"],
        'timeOk':   ["Generated command line: 'startManagerReplace' -logfile '.*startServer.log'  -timeout '70'",
                    "ADMU3200I: Server launched. Waiting for initialization status"],
        'addParam': ["Generated command line: 'startManagerReplace' -logfile '.*startServer.log'  -trace",
                    "ADMU0128I: Starting tool with the Dmgr01 profile", "ADMU3200I: Server launched. Waiting for initialization status"],
        'addParams': ["Generated command line: 'startManagerReplace' -logfile '.*startServer.log'  -trace -replacelog",
                    "ADMU0128I: Starting tool with the Dmgr01 profile", "ADMU3200I: Server launched. Waiting for initialization status"],
        'all':      ["Generated command line: 'startManagerReplace' -profileName '$profiles.dmgr01'  -logfile '$logLocations.tmp'  -timeout '70'  -trace -replacelog",
                    "ADMU0128I: Starting tool with the Dmgr01 profile", "ADMU3200I: Server launched. Waiting for initialization status"],
        'emptyConfig': ["Error: Configuration '' doesn't exist"],
        'incorrectConfig': ["Configuration 'incorrect' doesn't exist"],
        'mandatoryShell': ["Shell is mandatory"],
        'wrongStartLocation': ["Shell $startLocations.wrong does not exist"],
        'wrongProfile': ['The specified profile "wrong" cannot be found'],
        'wrongOption': ["ADMU9991E: Unknown option: -wrong"],
        'timeout': ["Timed out waiting for server initialization: 5 seconds"], 
    ]

	// Preparation actions
    def doSetupSpec() {
        def wasResourceName = wasHost
        createWorkspace(wasResourceName)
        createConfiguration(confignames.correctSOAP, [doNotRecreate: false])
        createConfiguration(confignames.correctIPC, [doNotRecreate: false])
        createConfiguration(confignames.correctJSR160RMI, [doNotRecreate: false])
        createConfiguration(confignames.correctNone, [doNotRecreate: false])
        createConfiguration(confignames.correctRMI, [doNotRecreate: false])

        importProject(projectName, 'dsl/RunProcedure.dsl', [projName: projectName,
                resName : wasResourceName,
                procName: procName,
                params  : [
                        configname: '',
                        wasAdditionalParameters: '',
                        wasDeploymentManagerProfile: '',
                        wasLogFileLocation: '',
                        wasStartManagerLocation: '',
                        wasTimeout: ''
                ]
        ])

        importProject(projectName, 'dsl/RunProcedure.dsl', [projName: projectName,
                resName : wasResourceName,
                procName: stopProcName,
                params  : [
                        configname: '',
                        wasAdditionalParameters: '',
                        wasDeploymentManagerProfile: '',
                        wasLogFileLocation: '',
                        wasStopManagerLocation: '',
                        wasTimeout: ''
                ]
        ])

        importProject(projectName, 'dsl/RunCustomShellCommand/RunCustomShellCommand.dsl', [projectName: projectName,
            wasResourceName : wasResourceName,
            procName: 'runCustomShellCommand',
        ])

        dsl 'setProperty(propertyName: "/plugins/EC-WebSphere/project/ec_debug_logToProperty", value: "/myJob/debug_logs")'    
    }

    def doCleanupSpec() {
    }

    @Unroll
    def "StartDeploymentManager - Positive"(){
        // setup: "Stop DeploymentManager"
        stopDeploymentManager()

        given: "Parameters for procedure"
        def runParams = [
            configname: configName,
            wasAdditionalParameters: addParameters,
            wasDeploymentManagerProfile: profile,
            wasLogFileLocation: logLocation,
            wasStartManagerLocation: startLocation,
            wasTimeout: timeout
        ]

        when: "Run procedure and wait until job is completed"
        def result = runProcedure(runParams)
        waitUntil {
        try {
                jobCompleted(result)
            } catch (Exception e) {
                println e.getMessage()
            }
        }

        then: "Get and compare results"
        def outcome = getJobProperty('/myJob/outcome', result.jobId)
        def jobSummary = getJobProperty("/myJob/jobSteps/$procName/summary", result.jobId)
        def reportUrls  = getJobProperty('/myJob/report-urls/startServer.log', result.jobId)
        def debugLog = getJobLogs(result.jobId)
        assert reportUrls =~ "jobSteps/.*/startServer.log" 
        assert outcome == "success"
        assert jobSummary == expectedSummary
        for (log in logs){
            def text = log.replace("startManagerReplace", startLocation)      
            assert debugLog =~ text
        }

        where: 'The following params will be:'
        testCaseID                  | configName                  | addParameters               | profile         | logLocation       | startLocation                  | timeout   | expectedSummary       | logs
        testCases.systemTest1.name  | confignames.correctSOAP     | ''                          | ''              | ''                | startLocations.defaultLocation | ''        | summaries.'default'   | jobLogs.'default'
        testCases.systemTest2.name  | confignames.correctSOAP     | ''                          | profiles.dmgr01 | ''                | startLocations.defaultLocation | ''        | summaries.'default'   | jobLogs.'profile'
        testCases.systemTest3.name  | confignames.correctSOAP     | ''                          | ''              | logLocations.tmp  | startLocations.defaultLocation | ''        | summaries.'default'   | jobLogs.'log'
        testCases.systemTest4.name  | confignames.correctSOAP     | ''                          | ''              | ''                | startLocations.defaultLocation | '70'      | summaries.'default'   | jobLogs.'timeOk'
        testCases.systemTest5.name  | confignames.correctSOAP     | additionalParameters.quiet  | ''              | ''                | startLocations.defaultLocation | ''        | summaries.'default'   | jobLogs.'addParam'
        testCases.systemTest5.name  | confignames.correctSOAP     | additionalParameters.some   | ''              | ''                | startLocations.defaultLocation | ''        | summaries.'default'   | jobLogs.'addParams'
        testCases.systemTest6.name  | confignames.correctSOAP     | additionalParameters.some   | profiles.dmgr01 | logLocations.tmp  | startLocations.defaultLocation | '70'      | summaries.'default'   | jobLogs.'all'
	}

    @Unroll
    def "StartDeploymentManager - Negative"(){
        given: "Parameters for procedure"
        def runParams = [
            configname: configName,
            wasAdditionalParameters: addParameters,
            wasDeploymentManagerProfile: profile,
            wasLogFileLocation: logLocation,
            wasStartManagerLocation: startLocation,
            wasTimeout: timeout
        ]

        when: "Run procedure and wait until job is completed"
        def result = runProcedure(runParams)
        waitUntil {
        try {
                jobCompleted(result)
            } catch (Exception e) {
                println e.getMessage()
            }
        }

        then: "Get and compare results"
        def outcome = getJobProperty('/myJob/outcome', result.jobId)
        def jobSummary = getJobProperty("/myJob/jobSteps/$procName/summary", result.jobId)
        def debugLog = getJobLogs(result.jobId)

        assert outcome == "error"
        assert jobSummary == expectedSummary
        for (log in logs){
            def text = log.replace("startManagerReplace", startLocation)      
            assert debugLog =~ text
        }

        where: 'The following params will be:'
        testCaseID                  | configName                  | addParameters               | profile         | logLocation       | startLocation                  | timeout   | expectedSummary                 | logs
        testCases.systemTest7.name  | ''                          | ''                          | ''              | ''                | startLocations.defaultLocation | ''        | summaries.'emptyConfig'         | jobLogs.'emptyConfig'
        testCases.systemTest7.name  | confignames.correctSOAP     | ''                          | ''              | ''                | ''                             | ''        | summaries.'mandatoryShell'      | jobLogs.'mandatoryShell'
        testCases.systemTest8.name  | confignames.correctSOAP     | ''                          | ''              | ''                | startLocations.wrong           | ''        | summaries.'wrongStartLocation'  | jobLogs.'wrongStartLocation'
        testCases.systemTest8.name  | confignames.correctSOAP     | ''                          | profiles.wrong  | ''                | startLocations.defaultLocation | ''        | summaries.'fail'                | jobLogs.'wrongProfile'
        testCases.systemTest8.name  | confignames.correctSOAP     | additionalParameters.wrong  | ''              | ''                | startLocations.defaultLocation | ''        | summaries.'fail'                | jobLogs.'wrongOption'
        testCases.systemTest9.name  | confignames.incorrect       | ''                          | ''              | ''                | startLocations.defaultLocation | ''        | summaries.'incorrectConfig'     | jobLogs.'incorrectConfig'
        testCases.systemTest10.name | confignames.correctSOAP     | ''                          | ''              | ''                | startLocations.defaultLocation | ''        | summaries.'fail'                | jobLogs.'started'
    }

    @Unroll
    def "Timeout - DeploymentManager doesn't start in Time"(){
        // setup: "Stop DeploymentManager"
        stopDeploymentManager()

        given: "Parameters for procedure"
        def runParams = [
            configname: configName,
            wasAdditionalParameters: addParameters,
            wasDeploymentManagerProfile: profile,
            wasLogFileLocation: logLocation,
            wasStartManagerLocation: startLocation,
            wasTimeout: timeout
        ]

        when: "Run procedure and wait until job is completed"
        def result = runProcedure(runParams)
        waitUntil {
        try {
                jobCompleted(result)
            } catch (Exception e) {
                println e.getMessage()
            }
        }

        then: "Get and compare results"
        def outcome = getJobProperty('/myJob/outcome', result.jobId)
        def jobSummary = getJobProperty("/myJob/jobSteps/$procName/summary", result.jobId)
        def reportUrls  = getJobProperty('/myJob/report-urls/startServer.log', result.jobId)
        def debugLog = getJobLogs(result.jobId)
        assert reportUrls =~ "jobSteps/.*/startServer.log" 
        assert outcome == "error"
        assert jobSummary == expectedSummary
        for (log in logs){
            def text = log.replace("startManagerReplace", startLocation)      
            assert debugLog =~ text
        }

        where: 'The following params will be:'
        testCaseID                  | configName                  | addParameters               | profile         | logLocation       | startLocation                  | timeout   | expectedSummary       | logs
        testCases.systemTest11.name | confignames.correctSOAP     | ''                          | ''              | ''                | startLocations.defaultLocation | '5'       | summaries.'fail'      | jobLogs.'timeout'
    }


    def stopDeploymentManager(){
        def runParams = [
            configname: confignames.correctSOAP,
            wasAdditionalParameters: '',
            wasDeploymentManagerProfile: '',
            wasLogFileLocation: '',
            wasStopManagerLocation: stopLocations.defaultLocation,
            wasTimeout: ''
        ]
        def result = runProcedure(runParams, stopProcName)
        waitUntil {
        try {
                jobCompleted(result)
            } catch (Exception e) {
                println e.getMessage()
            }
        }
    }

    //Run Test Procedure
    def runProcedure(def parameters, def procedureName=procName) {
        def parametersString = parameters.collect { k, v -> "$k: '$v'" }.join(', ')
        def code = """
            runProcedure(
                projectName: '$projectName',
                procedureName: '$procedureName',
                actualParameter: [
                    $parametersString                 
                ]
            )
        """
        return dslWithTimeout(code)
    }



}
