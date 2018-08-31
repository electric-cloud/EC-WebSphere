package com.electriccloud.plugin.spec

import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import com.electriccloud.plugin.spec.PluginTestHelper

@Requires({ System.getenv('IS_WAS_ND') == "1"})
class StartNode extends PluginTestHelper {

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
            name: 'C363526',
            description: 'only required values'],
        systemTest2: [
            name: 'C363527',
            description: 'specify Node Profile'],
        systemTest3: [
            name: 'C363528',
            description: 'Log File Location'],
        systemTest4: [
            name: 'C363529',
            description: 'Timeout - Node is started in Time '],
        systemTest5: [
            name: 'C363530',
            description: 'Start all application servers - false'],
        systemTest6: [
            name: 'C363531',
            description: 'Start all application servers- true, without NodeName'],
        systemTest7: [
            name: 'C363735',
            description: 'Start all application servers- true, specify NodeName'],        
        systemTest8: [
            name: 'C363532',
            description: 'Additional Options'],
        systemTest9: [
            name: 'C363533',
            description: 'All parameters '],
        systemTest10: [
            name: 'C363541',
            description: 'empty required fields'],
        systemTest11: [
            name: 'C363550',
            description: 'wrong values'],
        systemTest12: [
            name: 'C363552',
            description: 'incorrect Config Name'],
        systemTest13: [
            name: 'C363553',
            description: 'Start already started server'],
        systemTest14: [
            name: 'C363554',
            description: "Timeout - Node isn't started in Time"],
        systemTest15: [
            name: 'C363556',
            description: "use WebSphere user with limited rights"],                                                                         
    ]

    @Shared
    def procName = 'StartNode'

    @Shared
    def procStopNodeName = 'StopNode'

    @Shared
    def projectName = "EC-WebSphere Specs $procName Project"

    @Shared
    stopPolicies = [ 
        'none': '',
        'default': 'Default',
        'stop': 'stop_application_servers',
        'save': 'save_node_state',
    ]

    @Shared
    def nodes = [
        'default': wasHost + 'Node01',
        'wrong': 'wrong',
    ]

    @Shared
    def profiles = [
        appSrv01: 'AppSrv01',
        dmgr01: 'Dmgr01',
        wrong:  'wrong'
    ]

    @Shared
    def logLocations = [
        tmp: is_windows ? 'C:/IBM/startLog.log' : '/tmp/startLog.log'
    ]

    @Shared
    def additionalParameters = [
        trace: "-trace",
        wrong: "-wrong",
        some: "-trace -replacelog"
    ]

    @Shared
    stopLocations = [
        'default': is_windows ? 'C:/IBM/WebSphere/bin/stopNode.bat' : '/opt/IBM/WebSphere/AppServer/bin/stopNode.sh',
        'AppSrv01': is_windows ? 'C:/IBM/WebSphere/profiles/AppSrv01/bin/stopNode.bat' : '/opt/IBM/WebSphere/AppServer/profiles/AppSrv01/bin/stopNode.sh',
        'wrong': '/wrong/stopNode.sh',
    ]

    @Shared
    startLocations = [
        'default': is_windows ? 'C:/IBM/WebSphere/bin/startNode.bat' : '/opt/IBM/WebSphere/AppServer/bin/startNode.sh',
        'AppSrv01': is_windows ? 'C:/IBM/WebSphere/profiles/AppSrv01/bin/startNode.bat' : '/opt/IBM/WebSphere/AppServer/profiles/AppSrv01/bin/startNode.sh',
        'wrong': '/wrong/startNode.sh',
    ]

    @Shared
    def summaries = [  
        'default': "Node has been successfully started.",
        'emptyConfig': "Configuration '' doesn't exist",
        'incorrectConfig': "Configuration 'incorrect' doesn't exist",
        'mandatoryShell': "Shell is mandatory",
        'mandatoryNode': 'Node Name is mandatory when Start Servers is checked',
        'childSummaryWarning': "Application servers have been started:\nAll servers are already STARTED\nWARNING: Server server1 on Node ${nodes.'default'} is already STARTED\nWARNING: Nothing to do, all servers are already STARTED",
        'childSummarySuccess': "Application servers have been started:\nNode: ${nodes.'default'}, Server: server1, State: STARTED",
        'childSummaryError':   "Failed to start servers:\nNode wrongNodeName does not exist or does not have servers",
        'wrongStartLocation': "Shell $startLocations.wrong does not exist",
        'fail': "Failed to start Node",    

    ]

    @Shared
    def jobLogs = [
        'default':  ["Generated command line: 'startNodeReplace' -logfile '.*startServer.log'",
            "Server launched. Waiting for initialization status", "Node has been successfully started"],
        'profile':  ["Generated command line: 'startNodeReplace' -profileName 'AppSrv01'  -logfile '.*startServer.log'",
            "Server launched. Waiting for initialization status", "Node has been successfully started"],
        'log':      ["Generated command line: 'startNodeReplace' -logfile '$logLocations.tmp'",
            "Server launched. Waiting for initialization status", "Node has been successfully started"],         
        'timeOk':   ["Generated command line: 'startNodeReplace' -logfile '.*startServer.log'  -timeout '300'",
            "Server launched. Waiting for initialization status", "Node has been successfully started"],
        'addParam': ["Generated command line: 'startNodeReplace' -logfile '.*startServer.log'  -trace",
            "Server launched. Waiting for initialization status", "Node has been successfully started"],
        'addParams':["Generated command line: 'startNodeReplace' -logfile '.*startServer.log'  -trace -replacelog",
            "Server launched. Waiting for initialization status", "Node has been successfully started"],
        'all':      ["Generated command line: 'startNodeReplace' -profileName 'AppSrv01'  -logfile '$logLocations.tmp'  -timeout '300'  -trace -replacelog",
            "Server launched. Waiting for initialization status", "Node has been successfully started"],               
         'emptyConfig': ["Error: Configuration '' doesn't exist"],
        'incorrectConfig': ["Configuration 'incorrect' doesn't exist"],
        'mandatoryShell': ["Shell is mandatory"],
        'mandatoryNode': ['Node Name is mandatory when Start Servers is checked'],
        'wrongStartLocation': ["Shell $startLocations.wrong does not exist"],
        'wrongProfile': ['The specified profile "wrong" cannot be found'],
        'wrongOption': ["Unknown option: -wrong"],
        'started': ['An instance of the server may already be running: nodeagent'],
        'timeout': ["Timed out waiting for server initialization"],                  
    ]

    def doSetupSpec() {
        def wasResourceName = wasHost
        createWorkspace(wasResourceName)
        createConfiguration(confignames.correctSOAP, [doNotRecreate: false])
        change_logs()

        importProject(projectName, 'dsl/RunProcedure.dsl', [projName: projectName,
                resName : wasResourceName,
                procName: procName,
                params  : [
                        configname: '',
                        wasAdditionalParameters: '',
                        wasLogFileLocation: '',
                        wasNodeName: '',
                        wasNodeProfile    : '',
                        wasStartNodeLocation: '',
                        wasStartServers: '',
                        wasTimeout: ''
                ]
        ])    

        importProject(projectName, 'dsl/RunProcedure.dsl', [projName: projectName,
                resName : wasResourceName,
                procName: procStopNodeName,
                params  : [
                        configname: '',
                        wasAdditionalParameters: '',
                        wasLogFileLocation: '',
                        wasNodeProfile    : '',
                        wasStopNodeLocation: '',
                        wasStopNodePolicy: '',
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
    def "StartNode - Positive #testCaseID.name #testCaseID.description"(){
        stopNode(stopServers)

        given: "Parameters for procedure"
        def runParams = [
            configname: configName,
            wasAdditionalParameters: addParameters,
            wasLogFileLocation: logLocation,
            wasNodeName: node,
            wasNodeProfile    : profile,
            wasStartNodeLocation: startLocation,
            wasStartServers: start,
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
        assert outcome == status
        assert jobSummary == expectedSummary
        for (log in logs){
            def text = log.replace("startNodeReplace", startLocation)      
            assert debugLog =~ text
        }
        if (start.toInteger()){
            def jobChildSummary = getJobProperty("/myJob/jobSteps/$procName/jobSteps/StartApplicationServers/summary", result.jobId)
            def jobChildOutcome = getJobProperty("/myJob/jobSteps/$procName/jobSteps/StartApplicationServers/outcome", result.jobId)
            def childStatus = "warning"
            def childSummary = summaries.'childSummaryWarning'
            if (stopServers){
                childStatus = "success"
                childSummary = summaries.'childSummarySuccess'
            }
            assert jobChildOutcome == childStatus
            assert jobChildSummary == childSummary
        }
        where: 'The following params will be:'
        testCaseID             | configName                  | addParameters               | node                  | profile           | logLocation       | startLocation                   | start  | stopServers | timeout   | expectedSummary       | status    | logs
        testCases.systemTest1  | confignames.correctSOAP     | ''                          | ''                    | ''                | ''                | startLocations.'AppSrv01'       | '0'    | '0'         | ''        | summaries.'default'   | "success" | jobLogs.'default'
        testCases.systemTest2  | confignames.correctSOAP     | ''                          | ''                    | profiles.appSrv01 | ''                | startLocations.'default'        | '0'    | '0'         | ''        | summaries.'default'   | "success" | jobLogs.'profile'
        testCases.systemTest2  | confignames.correctSOAP     | ''                          | ''                    | profiles.appSrv01 | ''                | startLocations.'AppSrv01'       | '0'    | '0'         | ''        | summaries.'default'   | "success" | jobLogs.'profile'
        testCases.systemTest3  | confignames.correctSOAP     | ''                          | ''                    | ''                | logLocations.tmp  | startLocations.'AppSrv01'       | '0'    | '0'         | ''        | summaries.'default'   | "success" | jobLogs.'log'
        testCases.systemTest4  | confignames.correctSOAP     | ''                          | ''                    | ''                | ''                | startLocations.'AppSrv01'       | '0'    | '0'         | '300'     | summaries.'default'   | "success" | jobLogs.'timeOk'
        testCases.systemTest7  | confignames.correctSOAP     | ''                          | nodes.'default'       | ''                | ''                | startLocations.'AppSrv01'       | '1'    | 1           | ''        | summaries.'default'   | "success" | jobLogs.'default'
        testCases.systemTest7  | confignames.correctSOAP     | ''                          | nodes.'default'       | ''                | ''                | startLocations.'AppSrv01'       | '1'    | 0           | ''        | summaries.'default'   | "warning" | jobLogs.'default'
        testCases.systemTest8  | confignames.correctSOAP     | additionalParameters.trace  | ''                    | ''                | ''                | startLocations.'AppSrv01'       | '0'    | '0'         | ''        | summaries.'default'   | "success" | jobLogs.'addParam'
        testCases.systemTest8  | confignames.correctSOAP     | additionalParameters.some   | ''                    | ''                | ''                | startLocations.'AppSrv01'       | '0'    | '0'         | ''        | summaries.'default'   | "success" | jobLogs.'addParams'
        testCases.systemTest9  | confignames.correctSOAP     | additionalParameters.some   | nodes.'default'       | profiles.appSrv01 | logLocations.tmp  | startLocations.'default'        | '1'    | '1'         | '300'     | summaries.'default'   | "success" | jobLogs.'all'
    }

    @Unroll
    def "StartNode - Negative #testCaseID.name #testCaseID.description"(){
        if (timeout == '10'){
            stopNode()
        }
        // for version 80, node starts in 10 seconds, so we need to reduce the time
        if (wasHost == 'websphere80nd' && testCaseID == testCases.systemTest14){
            timeout = '1'
        }

        // // http://jira.electric-cloud.com/browse/ECPAPPSERVERWEBSPHERE-563
        // if (wasHost == 'websphere80nd' && (profile == profiles.'wrong') && is_windows){
        //     status = "success"
        //     expectedSummary = summaries.'default'
        // }

        given: "Parameters for procedure"
        def runParams = [
            configname: configName,
            wasAdditionalParameters: addParameters,
            wasLogFileLocation: logLocation,
            wasNodeName: node,
            wasNodeProfile    : profile,
            wasStartNodeLocation: startLocation,
            wasStartServers: start,
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
        assert outcome == status
        assert jobSummary == expectedSummary
        for (log in logs){
            def text = log.replace("startNodeReplace", startLocation)      
            assert debugLog =~ text
        }
        where: 'The following params will be:'
        testCaseID              | configName                  | addParameters               | node                  | profile           | logLocation       | startLocation                   | start  | timeout   | expectedSummary                | status    | logs
        // commenting this out because this test is fragile and this will be working on 8.0
        // testCases.systemTest14  | confignames.correctSOAP     | ''                          | ''                    | ''                | ''                | startLocations.'AppSrv01'       | '0'    | '10'      | summaries.'fail'               | "error"   | jobLogs.'timeout'
        testCases.systemTest10  | ''                          | ''                          | ''                    | ''                | ''                | startLocations.'AppSrv01'       | '0'    | ''        | summaries.'emptyConfig'        | "error"   | jobLogs.'emptyConfig'
        testCases.systemTest10  | confignames.correctSOAP     | ''                          | ''                    | ''                | ''                | ''                              | '0'    | ''        | summaries.'mandatoryShell'     | "error"   | jobLogs.'mandatoryShell'
        testCases.systemTest10  | confignames.correctSOAP     | ''                          | ''                    | ''                | ''                | startLocations.'AppSrv01'       | '1'    | ''        | summaries.'mandatoryNode'      | "error"   | jobLogs.'mandatoryNode'
        testCases.systemTest12  | confignames.incorrect       | ''                          | ''                    | ''                | ''                | startLocations.'AppSrv01'       | '0'    | ''        | summaries.'incorrectConfig'    | "error"   | jobLogs.'incorrectConfig'
        testCases.systemTest11  | confignames.correctSOAP     | ''                          | ''                    | ''                | ''                | startLocations.'wrong'          | '0'    | ''        | summaries.'wrongStartLocation' | "error"   | jobLogs.'wrongStartLocation'
        testCases.systemTest11  | confignames.correctSOAP     | ''                          | ''                    | profiles.'wrong'  | ''                | startLocations.'default'        | '0'    | ''        | summaries.'fail'               | "error"   | jobLogs.'wrongProfile'
        testCases.systemTest11  | confignames.correctSOAP     | additionalParameters.wrong  | ''                    | ''                | ''                | startLocations.'AppSrv01'       | '0'    | '0'       | summaries.'fail'               | "error"   | jobLogs.'wrongOption'
        testCases.systemTest13  | confignames.correctSOAP     | ''                          | ''                    | ''                | ''                | startLocations.'AppSrv01'       | '0'    | ''        | summaries.'fail'               | "error"   | jobLogs.'started'

    }

    @Unroll
    def "StartNode - Negative, wrongNode #testCaseID.name #testCaseID.description"(){
        stopNode()
        given: "Parameters for procedure"
        def runParams = [
            configname: configName,
            wasAdditionalParameters: addParameters,
            wasLogFileLocation: logLocation,
            wasNodeName: node,
            wasNodeProfile    : profile,
            wasStartNodeLocation: startLocation,
            wasStartServers: start,
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
        def reportUrls  = getJobProperty('/myJob/report-urls/startServer.log', result.jobId)
        assert reportUrls =~ "jobSteps/.*/startServer.log" 
        assert outcome == status
        assert jobSummary == expectedSummary
        for (log in logs){
            def text = log.replace("startNodeReplace", startLocation)      
            assert debugLog =~ text
        }

        def jobChildSummary = getJobProperty("/myJob/jobSteps/$procName/jobSteps/StartApplicationServers/summary", result.jobId)
        def jobChildOutcome = getJobProperty("/myJob/jobSteps/$procName/jobSteps/StartApplicationServers/outcome", result.jobId)
        assert jobChildOutcome == "error"
        assert jobChildSummary == summaries.'childSummaryError' 

        where: 'The following params will be:'
        testCaseID              | configName                  | addParameters               | node                  | profile           | logLocation       | startLocation                   | start  | timeout   | expectedSummary                | status    | logs
        testCases.systemTest11  | confignames.correctSOAP     | ''                          | 'wrongNodeName'       | ''                | ''                | startLocations.'AppSrv01'       | '1'    | ''        | summaries.'default'            | "error"   | jobLogs.'default'
    }

    def stopNode(def serverState=0){
        def stopPolicy = serverState ? stopPolicies.'stop' : stopPolicies.'default'   
        def runParams = [
            configname: confignames.correctSOAP,
            wasAdditionalParameters: '',
            wasLogFileLocation: '',
            wasNodeProfile    : '',
            wasStopNodeLocation: stopLocations.'AppSrv01',
            wasStopNodePolicy: stopPolicy,
            wasTimeout: ''
        ]
        def result = runProcedure(runParams, procStopNodeName)
        waitUntil {
        try {
                jobCompleted(result)
            } catch (Exception e) {
                println e.getMessage()
            }
        }
    }    


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

    def change_logs(version){
        if (is_windows){
            jobLogs.'default'[0] = jobLogs.'default'[0].replace("'", "\"")
            jobLogs.'profile'[0] = jobLogs.'profile'[0].replace("'", "\"")
            jobLogs.'log'[0] = jobLogs.'log'[0].replace("'", "\"")
            jobLogs.'timeOk'[0] = jobLogs.'timeOk'[0].replace("'", "\"")
            jobLogs.'addParam'[0] = jobLogs.'addParam'[0].replace("'", "\"")
            jobLogs.'addParams'[0] = jobLogs.'addParams'[0].replace("'", "\"")
            jobLogs.'all'[0] = jobLogs.'all'[0].replace("'", "\"")
        }
    }

}
