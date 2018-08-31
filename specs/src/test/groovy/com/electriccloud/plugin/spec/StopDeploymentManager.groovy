package com.electriccloud.plugin.spec

import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import com.electriccloud.plugin.spec.PluginTestHelper

@Unroll
@Stepwise
@Requires({ System.getenv('IS_WAS_ND') == "1"})
class StopDeploymentManager extends PluginTestHelper {

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
            name: 'C363479',
            description: 'only required values'],
        systemTest2: [
            name: 'C363480',
            description: 'specify Manager Profile'],
        systemTest3: [
            name: 'C363481',
            description: 'Log File Location'],
        systemTest4: [
            name: 'C363482',
            description: 'Timeout - DeploymentManager is stopped in Time'],
        systemTest5: [
            name: 'C363485',
            description: 'Additional Parameters'],
        systemTest6: [
            name: 'C363485',
            description: 'All parameters'],
        systemTest7: [
            name: 'C363487',
            description: 'empty required fields'],
        systemTest8: [
            name: 'C363489',
            description: 'incorrect Config Name'],
        systemTest9: [
            name: 'C363490',
            description: 'wrong values'],
        systemTest10: [
            name: 'C363525',
            description: 'Stop already stopped deployment manager'],
        systemTest11: [
            name: 'C363483',
            description: 'Timeout - DeploymentManager isn`t stopped in Time'],
    ]

    @Shared
    def stopLocations = [
        defaultLocation: is_windows ? 'C:/IBM/WebSphere/bin/stopManager.bat' : '/opt/IBM/WebSphere/AppServer/bin/stopManager.sh',
        wrong: '/wrong/path/stopManager.sh'
    ]

    @Shared
    def startLocations = [
        defaultLocation: is_windows ? 'C:/IBM/WebSphere/bin/startManager.bat' : '/opt/IBM/WebSphere/AppServer/bin/startManager.sh'
    ]

    @Shared
    def procName = 'StopDeploymentManager'

    @Shared
    def startProcName = 'StartDeploymentManager'

    @Shared
    def projectName = "EC-WebSphere Specs $procName Project"

    @Shared
    def profiles = [
        dmgr01: 'Dmgr01',
        wrong:  'wrong'
    ]

    @Shared
    def logLocations = [
        tmp: is_windows ? 'C:/IBM/stopLog.log' : "/tmp/stopLog.log"
    ]

    @Shared
    def additionalParameters = [
        quiet: "-quiet",
        wrong: "-wrong",
        some: "-quiet -nowait"
    ]

    @Shared
    def summaries = [
        'default': "Deployment Manager has been successfully stopped.",
        'emptyConfig': "Configuration '' doesn't exist",
        'incorrect': "Configuration 'incorrect' doesn't exist",
        'fail': "Failed to stop Deployment Manager",
        'mandatoryShell': "Shell is mandatory",
        'wrongStopLocation': "Shell $stopLocations.wrong does not exist",        
    ]

    @Shared
    def jobLogs = [
        'default':  ["Generated command line: 'stopManagerReplace' -user 'wsadmin'  -password '\\*\\*\\*\\*\\*'  -conntype 'SOAP'  -logfile '.*stopServer.log'",
                    "ADMU3201I: Server stop request issued. Waiting for stop status", "ADMU4000I: Server dmgr stop completed."],
        'profile':  ["Generated command line: 'stopManagerReplace' -user 'wsadmin'  -password '\\*\\*\\*\\*\\*'  -conntype 'SOAP'  -profileName 'Dmgr01'  -logfile '.*stopServer.log'",
                    "ADMU3201I: Server stop request issued. Waiting for stop status", "ADMU4000I: Server dmgr stop completed."],
        'logs':     ["Generated command line: 'stopManagerReplace' -user 'wsadmin'  -password '\\*\\*\\*\\*\\*'  -conntype 'SOAP'  -logfile '$logLocations.tmp'" ,
                    "ADMU0116I: Tool information is being logged in file $logLocations.tmp", "ADMU3201I: Server stop request issued. Waiting for stop status", "ADMU4000I: Server dmgr stop completed."],
        'timeOk':   ["Generated command line: 'stopManagerReplace' -user 'wsadmin'  -password '\\*\\*\\*\\*\\*'  -conntype 'SOAP'  -logfile '.*stopServer.log'",
                     "ADMU3201I: Server stop request issued. Waiting for stop status", "ADMU4000I: Server dmgr stop completed."],
        'addParam': ["Generated command line: 'stopManagerReplace' -user 'wsadmin'  -password '\\*\\*\\*\\*\\*'  -conntype 'SOAP'  -logfile '.*stopServer.log'  -quiet",
                    "(?!ADMU3201I: Server stop request issued. Waiting for stop status)", "(?!ADMU4000I: Server dmgr stop completed.)"],
        'addParams': ["Generated command line: 'stopManagerReplace' -user 'wsadmin'  -password '\\*\\*\\*\\*\\*'  -conntype 'SOAP'  -logfile '.*stopServer.log'  -quiet -nowait",
                    "(?!ADMU3201I: Server stop request issued. Waiting for stop status)", "(?!ADMU4000I: Server dmgr stop completed.)"],
        'all':      ["Generated command line: 'stopManagerReplace' -user 'wsadmin'  -password '\\*\\*\\*\\*\\*'  -conntype 'SOAP'  -profileName 'Dmgr01'  -logfile '$logLocations.tmp'  -timeout '140'  -quiet",
                    "(?!ADMU3201I: Server stop request issued. Waiting for stop status)", "(?!ADMU4000I: Server dmgr stop completed.)"],
        'emptyConfig': ["Error: Configuration '' doesn't exist"],
        'incorrect': ["Configuration 'incorrect' doesn't exist"],
        'wrongStopLocation': ["Shell $stopLocations.wrong does not exist"],
        'wrongProfile': ['The specified profile "wrong" cannot be found'],
        'wrongOption': ["ADMU9991E: Unknown option: -wrong"],
        'stopped': ['The server "dmgr" cannot be reached. It appears to be stopped'],
        'mandatoryShell': ["Shell is mandatory"],
        'timeout': ["Timed out waiting for server shutdown"]        
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
                        wasStopManagerLocation: '',
                        wasTimeout: ''
                ]
        ])

        importProject(projectName, 'dsl/RunProcedure.dsl', [projName: projectName,
                resName : wasResourceName,
                procName: startProcName,
                params  : [
                        configname: '',
                        wasAdditionalParameters: '',
                        wasDeploymentManagerProfile: '',
                        wasLogFileLocation: '',
                        wasStartManagerLocation: '',
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
    def "StopDeploymentManager - Positive"(){
        given: "Parameters for procedure"
        def runParams = [
            configname: configName,
            wasAdditionalParameters: addParameters,
            wasDeploymentManagerProfile: profile,
            wasLogFileLocation: logLocation,
            wasStopManagerLocation: stopLocation,
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
        print "JobSummary: $jobSummary";
        def debugLog = getJobLogs(result.jobId)
        assert outcome == "success"
        assert jobSummary == expectedSummary
        for (log in logs){
            def text = log.replace("stopManagerReplace", stopLocation)      
            assert debugLog =~ text
        }

        cleanup: "Start WebSphere DeploymentManager"
        startDeploymentManager()

        where: 'The following params will be:'
        testCaseID                  | configName                  | addParameters               | profile         | logLocation       | stopLocation                  | timeout   | expectedSummary       | logs
        testCases.systemTest1.name  | confignames.correctSOAP     | ''                          | ''              | ''                | stopLocations.defaultLocation | ''        | summaries.'default'   | jobLogs.'default'
        testCases.systemTest2.name  | confignames.correctSOAP     | ''                          | profiles.dmgr01 | ''                | stopLocations.defaultLocation | ''        | summaries.'default'   | jobLogs.'profile'
        testCases.systemTest3.name  | confignames.correctSOAP     | ''                          | ''              | logLocations.tmp  | stopLocations.defaultLocation | ''        | summaries.'default'   | jobLogs.'logs'
        testCases.systemTest4.name  | confignames.correctSOAP     | ''                          | ''              | ''                | stopLocations.defaultLocation | '140'     | summaries.'default'   | jobLogs.'timeOk'
        testCases.systemTest5.name  | confignames.correctSOAP     | additionalParameters.quiet  | ''              | ''                | stopLocations.defaultLocation | ''        | summaries.'default'   | jobLogs.'addParam'
        testCases.systemTest5.name  | confignames.correctSOAP     | additionalParameters.some   | ''              | ''                | stopLocations.defaultLocation | ''        | summaries.'default'   | jobLogs.'addParams'
        testCases.systemTest6.name  | confignames.correctSOAP     | additionalParameters.quiet  | profiles.dmgr01 | logLocations.tmp  | stopLocations.defaultLocation | '140'     | summaries.'default'   | jobLogs.'all'
    }

    @Unroll
    def "StopDeploymentManager - Negative"(){
        given: "Negative parameters for procedure"
        def runParams = [
            configname: configName,
            wasAdditionalParameters: addParameters,
            wasDeploymentManagerProfile: profile,
            wasLogFileLocation: logLocation,
            wasStopManagerLocation: stopLocation,
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
        print "JobSummary: $jobSummary";
        def debugLog = getJobLogs(result.jobId)
        assert outcome == "error"
        assert jobSummary == expectedSummary
        for (log in logs){
            def text = log.replace("stopManagerReplace", stopLocation)      
            assert debugLog =~ text
        }
        where: 'The following params will be:'
        testCaseID                  | configName                  | addParameters               | profile         | logLocation       | stopLocation                  | timeout   | expectedSummary                 | logs
        testCases.systemTest7.name  | confignames.correctSOAP     | ''                          | ''              | ''                | ''                            | ''        | summaries.'mandatoryShell'      | jobLogs.'mandatoryShell'
        testCases.systemTest7.name  | ''                          | ''                          | ''              | ''                | stopLocations.defaultLocation | ''        | summaries.'emptyConfig'         | jobLogs.'emptyConfig'
        testCases.systemTest9.name  | confignames.correctSOAP     | ''                          | ''              | ''                | stopLocations.wrong           | ''        | summaries.'wrongStopLocation'   | jobLogs.'wrongStopLocation'
        testCases.systemTest9.name  | confignames.correctSOAP     | ''                          | profiles.wrong  | ''                | stopLocations.defaultLocation | ''        | summaries.'fail'                | jobLogs.'wrongProfile'
        testCases.systemTest9.name  | confignames.correctSOAP     | additionalParameters.wrong  | ''              | ''                | stopLocations.defaultLocation | ''        | summaries.'fail'                | jobLogs.'wrongOption'
    }

    @Unroll
    def "Stop already stopped deployment manager"(){
        given: "Parameters for procedure"
        def runParams = [
            configname: configName,
            wasAdditionalParameters: addParameters,
            wasDeploymentManagerProfile: profile,
            wasLogFileLocation: logLocation,
            wasStopManagerLocation: stopLocation,
            wasTimeout: timeout
        ]

        when: "Run procedure two times"
        def result 
        for (def i=0; i<2; i++) {
            result = runProcedure(runParams)
            waitUntil {
            try {
                    jobCompleted(result)
                } 
            catch (Exception e) {
                    println e.getMessage()
                }
            }
        }
        then: "Get and compare results"
        def outcome = getJobProperty('/myJob/outcome', result.jobId)
        def jobSummary = getJobProperty("/myJob/jobSteps/$procName/summary", result.jobId)       
        print "JobSummary: $jobSummary";
        def debugLog = getJobLogs(result.jobId)
        assert outcome == "error"
        assert jobSummary == expectedSummary
        for (log in logs){
            def text = log.replace("stopManagerReplace", stopLocation)      
            assert debugLog =~ text
        }

        cleanup: "Start WebSphere DeploymentManager"
        startDeploymentManager()

        where: 'The following params will be:'
        testCaseID                  | configName                  | addParameters               | profile         | logLocation       | stopLocation                  | timeout   | expectedSummary       | logs
        testCases.systemTest10.name | confignames.correctSOAP     | ''                          | ''              | ''                | stopLocations.defaultLocation | ''        | summaries.'fail'      | jobLogs.'stopped'
    }

    @Unroll
    def "Timeout - DeploymentManager isn't stopped in Time"(){
        given: "Parameters for procedure"
        def runParams = [
            configname: configName,
            wasAdditionalParameters: addParameters,
            wasDeploymentManagerProfile: profile,
            wasLogFileLocation: logLocation,
            wasStopManagerLocation: stopLocation,
            wasTimeout: timeout
        ]

        when: "Run procedure two times"
        def result = runProcedure(runParams)
        waitUntil {
            try {
                    jobCompleted(result)
                } 
            catch (Exception e) {
                    println e.getMessage()
                }
        }

        then: "Get and compare results"
        def outcome = getJobProperty('/myJob/outcome', result.jobId)
        def jobSummary = getJobProperty("/myJob/jobSteps/$procName/summary", result.jobId)       
        print "JobSummary: $jobSummary";
        def debugLog = getJobLogs(result.jobId)
        assert outcome == "error"
        assert jobSummary == expectedSummary
        for (log in logs){
            def text = log.replace("stopManagerReplace", stopLocation)      
            assert debugLog =~ text
        }

        cleanup: "Start WebSphere DeploymentManager"
        startDeploymentManager()

        where: 'The following params will be:'
        testCaseID                   | configName                  | addParameters               | profile         | logLocation       | stopLocation                  | timeout   | expectedSummary       | logs
        testCases.systemTest11.name  | confignames.correctSOAP     | ''                          | ''              | ''                | stopLocations.defaultLocation | '5'       | summaries.'fail'      | jobLogs.'timeout'
   }

    def startDeploymentManager(){
        def runParams = [
            configname: confignames.correctSOAP,
            wasAdditionalParameters: '',
            wasDeploymentManagerProfile: '',
            wasLogFileLocation: '',
            wasStartManagerLocation: startLocations.defaultLocation,
            wasTimeout: ''
        ]
        def result = runProcedure(runParams, startProcName)
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
