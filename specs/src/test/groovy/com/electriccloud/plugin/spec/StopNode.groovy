package com.electriccloud.plugin.spec

import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import com.electriccloud.plugin.spec.PluginTestHelper

@Requires({ System.getenv('IS_WAS_ND') == "1"})
class StopNode extends PluginTestHelper {

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
            name: 'C363510',
            description: 'only required values'],
        systemTest2: [
            name: 'C363511',
            description: 'specify Node Profile'],
        systemTest3: [
            name: 'C363512',
            description: 'Log File Location'],
        systemTest4: [
            name: 'C363513',
            description: 'Timeout - Node is stopped in Time'],
        systemTest5: [
            name: 'C363514',
            description: 'save node state'],
        systemTest6: [
            name: 'C363515',
            description: 'stop application servers'],
        systemTest7: [
            name: 'C363516',
            description: 'Additional Options'],
        systemTest8: [
            name: 'C363517',
            description: 'All parameters '],
        systemTest9: [
            name: 'C363517',
            description: 'empty required fields'],
        systemTest10: [
            name: 'C363519',
            description: 'wrong values'],
        systemTest11: [
            name: 'C363521',
            description: 'incorrect Config Name'],
        systemTest12: [
            name: 'C363524',
            description: 'Stop already stopped Node'],
        systemTest13: [
            name: 'C363522',
            description: "Timeout - Node isn't stopped in Time"],
        systemTest14: [
            name: 'C363523',
            description: "use WebSphere user with limited rights"],                                                                         
    ]

    @Shared
    def procName = 'StopNode'

    @Shared
    def procStartNodeName = 'StartNode'

    @Shared
    def projectName = "EC-WebSphere Specs $procName Project"

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
    def nodes = [
        'default': wasHost + 'Node01',
        'wrong': 'wrong',
    ]

    @Shared
    stopLocations = [
        'default': is_windows ? 'C:/IBM/WebSphere/AppServer/bin/stopNode.bat' : '/opt/IBM/WebSphere/AppServer/bin/stopNode.sh',
        'AppSrv01': is_windows ? 'C:/IBM/WebSphere/AppServer/profiles/AppSrv01/bin/stopNode.bat' : '/opt/IBM/WebSphere/AppServer/profiles/AppSrv01/bin/stopNode.sh',
        'wrong': '/wrong/stopNode.sh',
    ]

    @Shared
    startLocations = [
        'default': is_windows ? 'C:/IBM/WebSphere/AppServer/bin/startNode.bat' : '/opt/IBM/WebSphere/AppServer/bin/startNode.sh',
        'AppSrv01': is_windows ? 'C:/IBM/WebSphere/AppServer/profiles/AppSrv01/bin/startNode.bat' : '/opt/IBM/WebSphere/AppServer/profiles/AppSrv01/bin/startNode.sh',
    ]

    @Shared
    startServerLocations = [
        'default': is_windows ? 'C:/IBM/WebSphere/AppServer/bin/startServer.bat' : '/opt/IBM/WebSphere/AppServer/bin/startServer.sh',
        'AppSrv01': is_windows ? 'C:/IBM/WebSphere/AppServer/profiles/AppSrv01/bin/startServer.bat' : '/opt/IBM/WebSphere/AppServer/profiles/AppSrv01/bin/startServer.sh',
        'Dmgr01': is_windows ? 'C:/IBM/WebSphere/AppServer/profiles/Dmgr01/bin/startServer.bat' : '/opt/IBM/WebSphere/AppServer/profiles/Dmgr01/bin/startServer.sh'
    ]

    @Shared
    stopPolicies = [ 
        'none': '',
        'default': 'Default',
        'stop': 'stop_application_servers',
        'save': 'save_node_state',
    ]

    @Shared
    def summaries = [  
        'default': "Node has been successfully stopped.",
        'emptyConfig': "Configuration '' doesn't exist",
        'incorrectConfig': "Configuration 'incorrect' doesn't exist",
        'mandatoryShell': "Shell is mandatory",
        'wrongStopLocation': "Shell $stopLocations.wrong does not exist",
        'fail': "Failed to stop Node",

    ]

    @Shared
    def jobLogs = [
        'default': [
            "Generated command line: 'stopNodeReplace' -user 'wsadmin'  -password '\\*\\*\\*\\*\\*'  -conntype 'SOAP'  -logfile '.*stopServer.log'",
            "Server nodeagent stop completed",
            "Node has been successfully stopped"
        ],
        'profile': [
            "Generated command line: 'stopNodeReplace' -user 'wsadmin'  -password '\\*\\*\\*\\*\\*'  -conntype 'SOAP'  -profileName 'AppSrv01'  -logfile '.*stopServer.log'",
            "Server nodeagent stop completed",
            "Node has been successfully stopped"
        ],
        'log': [
            "Generated command line: 'stopNodeReplace' -user 'wsadmin'  -password '\\*\\*\\*\\*\\*'  -conntype 'SOAP'  -logfile '$logLocations.tmp'",
            "Server nodeagent stop completed",
            "Node has been successfully stopped"
        ],
        'timeOk': [
            "Generated command line: 'stopNodeReplace' -user 'wsadmin'  -password '\\*\\*\\*\\*\\*'  -conntype 'SOAP'  -logfile '.*stopServer.log'  -timeout '70'",
            "Server nodeagent stop completed",
            "Node has been successfully stopped"
        ],
        'saveNode':  [
            "Generated command line: 'stopNodeReplace' -user 'wsadmin'  -password '\\*\\*\\*\\*\\*'  -conntype 'SOAP'  -logfile '.*stopServer.log'  -saveNodeState",
            "Server nodeagent stop completed",
            "Node has been successfully stopped"
        ],
        'stop': [
            "Generated command line: 'stopNodeReplace' -user 'wsadmin'  -password '\\*\\*\\*\\*\\*'  -conntype 'SOAP'  -logfile '.*stopServer.log'  -stopservers",
            "(?:Server server1 is now STOPPED|Server server1 cannot be reached. It appears to be stopped.)",
            "Stopping all server processes for node ${nodes.'default'}", "Server nodeagent is now STOPPED"
        ],
        'addParam': [
            "Generated command line: 'stopNodeReplace' -user 'wsadmin'  -password '\\*\\*\\*\\*\\*'  -conntype 'SOAP'  -logfile '.*stopServer.log'   -trace",
            "Server nodeagent stop completed",
            "Node has been successfully stopped"
        ],
        'addParams': [
            "Generated command line: 'stopNodeReplace' -user 'wsadmin'  -password '\\*\\*\\*\\*\\*'  -conntype 'SOAP'  -logfile '.*stopServer.log'   -trace -replacelog",
            "Server nodeagent stop completed",
            "Node has been successfully stopped"
        ],
        'all': [
            "Generated command line: 'stopNodeReplace' -user 'wsadmin'  -password '\\*\\*\\*\\*\\*'  -conntype 'SOAP'  -profileName 'AppSrv01'  -logfile '$logLocations.tmp'  -timeout '70'  -trace  -stopservers",
            "Server server1 is now STOPPED", "Stopping all server processes for node ${nodes.'default'}",
            "Server nodeagent is now STOPPED"
        ],
        'emptyConfig': ["Error: Configuration '' doesn't exist"],
        'incorrectConfig': ["Configuration 'incorrect' doesn't exist"],
        'mandatoryShell': ["Shell is mandatory"],
        'wrongStopLocation': ["Shell $stopLocations.wrong does not exist"],
        'wrongProfile': ['The specified profile "wrong" cannot be found'],
        'wrongOption': ["Unknown option: -wrong"],
        'stopped': ['The server "nodeagent" cannot be reached. It appears to be stopped'],
        'timeout': ["Timed out waiting for server shutdown"],
    ]

    // Preparation actions
    def doSetupSpec() {
        def wasResourceName = wasHost
        createWorkspace(wasResourceName)
        createConfiguration(confignames.correctSOAP, [doNotRecreate: false])

        importProject(projectName, 'dsl/RunProcedure.dsl', [projName: projectName,
                resName : wasResourceName,
                procName: procName,
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

        importProject(projectName, 'dsl/RunProcedure.dsl', [projName: projectName,
                resName : wasResourceName,
                procName: procStartNodeName,
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


        importProject(projectName, 'dsl/RunCustomShellCommand/RunCustomShellCommand.dsl', [projectName: projectName,
            wasResourceName : wasResourceName,
            procName: 'runCustomShellCommand',
        ])

        dsl 'setProperty(propertyName: "/plugins/EC-WebSphere/project/ec_debug_logToProperty", value: "/myJob/debug_logs")'    
    }

    def doCleanupSpec() {
    }

    @Unroll
    def "StopNode - Positive"(){
        given: "Parameters for procedure"
        def runParams = [
            configname: configName,
            wasAdditionalParameters: addParameters,
            wasLogFileLocation: logLocation,
            wasNodeProfile    : profile,
            wasStopNodeLocation: stopLocation,
            wasStopNodePolicy: stopPolicy,
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
        def reportUrls  = getJobProperty('/myJob/report-urls/stopServer.log', result.jobId)
        def debugLog = getJobLogs(result.jobId)
        assert reportUrls =~ "jobSteps/.*/stopServer.log"
        assert outcome == "success"
        assert jobSummary == expectedSummary
        for (log in logs){
            def text = log.replace("stopNodeReplace", stopLocation)
            assert debugLog =~ text
        }

        cleanup:
        startNode(stopPolicy)

        where: 'The following params will be:'
        testCaseID                  | configName                  | addParameters               | profile           | logLocation       | stopLocation                   | stopPolicy             | timeout   | expectedSummary       | logs
        testCases.systemTest1.name  | confignames.correctSOAP     | ''                          | ''                | ''                | stopLocations.'AppSrv01'       | stopPolicies.'none'    | ''        | summaries.'default'   | jobLogs.'default'
        testCases.systemTest1.name  | confignames.correctSOAP     | ''                          | ''                | ''                | stopLocations.'AppSrv01'       | stopPolicies.'default' | ''        | summaries.'default'   | jobLogs.'default'
        testCases.systemTest2.name  | confignames.correctSOAP     | ''                          | profiles.appSrv01 | ''                | stopLocations.'default'        | stopPolicies.'default' | ''        | summaries.'default'   | jobLogs.'profile'
        testCases.systemTest2.name  | confignames.correctSOAP     | ''                          | profiles.appSrv01 | ''                | stopLocations.'AppSrv01'       | stopPolicies.'default' | ''        | summaries.'default'   | jobLogs.'profile'
        testCases.systemTest3.name  | confignames.correctSOAP     | ''                          | ''                | logLocations.tmp  | stopLocations.'AppSrv01'       | stopPolicies.'default' | ''        | summaries.'default'   | jobLogs.'log'
        testCases.systemTest4.name  | confignames.correctSOAP     | ''                          | ''                | ''                | stopLocations.'AppSrv01'       | stopPolicies.'default' | '70'      | summaries.'default'   | jobLogs.'timeOk'
        testCases.systemTest5.name  | confignames.correctSOAP     | ''                          | ''                | ''                | stopLocations.'AppSrv01'       | stopPolicies.'save'    | ''        | summaries.'default'   | jobLogs.'saveNode'
        testCases.systemTest6.name  | confignames.correctSOAP     | ''                          | ''                | ''                | stopLocations.'AppSrv01'       | stopPolicies.'stop'    | ''        | summaries.'default'   | jobLogs.'stop'
        testCases.systemTest7.name  | confignames.correctSOAP     | additionalParameters.trace  | ''                | ''                | stopLocations.'AppSrv01'       | stopPolicies.'default' | ''        | summaries.'default'   | jobLogs.'default'
        testCases.systemTest7.name  | confignames.correctSOAP     | additionalParameters.some   | ''                | ''                | stopLocations.'AppSrv01'       | stopPolicies.'default' | ''        | summaries.'default'   | jobLogs.'default'
        testCases.systemTest8.name  | confignames.correctSOAP     | additionalParameters.trace  | profiles.appSrv01 | logLocations.tmp  | stopLocations.'default'        | stopPolicies.'stop'    | '70'      | summaries.'default'   | jobLogs.'all'
    }

    @Unroll
    def "StopNode - Negative"(){
        given: "Parameters for procedure"
        def runParams = [
            configname: configName,
            wasAdditionalParameters: addParameters,
            wasLogFileLocation: logLocation,
            wasNodeProfile    : profile,
            wasStopNodeLocation: stopLocation,
            wasStopNodePolicy: stopPolicy,
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
            def text = log.replace("stopNodeReplace", stopLocation)      
            assert debugLog =~ text
        }



        where: 'The following params will be:'
        testCaseID                  | configName                  | addParameters              | profile           | logLocation       | stopLocation                   | stopPolicy               | timeout   | expectedSummary                   | logs
        testCases.systemTest9.name  | ''                          | ''                         | ''                | ''                | stopLocations.'AppSrv01'       | stopPolicies.'none'    | ''        | summaries.'emptyConfig'             | jobLogs.'emptyConfig'
        testCases.systemTest9.name  | confignames.correctSOAP     | ''                         | ''                | ''                | ''                             | stopPolicies.'none'    | ''        | summaries.'mandatoryShell'          | jobLogs.'mandatoryShell'
        testCases.systemTest10.name | confignames.correctSOAP     | ''                         | ''                | ''                | stopLocations.'wrong'          | stopPolicies.'none'    | ''        | summaries.'wrongStopLocation'       | jobLogs.'wrongStopLocation'
        testCases.systemTest10.name | confignames.correctSOAP     | ''                         | profiles.'wrong'  | ''                | stopLocations.'AppSrv01'       | stopPolicies.'none'    | ''        | summaries.'fail'                    | jobLogs.'wrongProfile'
        testCases.systemTest10.name | confignames.correctSOAP     | additionalParameters.wrong | ''                | ''                | stopLocations.'AppSrv01'       | stopPolicies.'none'    | ''        | summaries.'fail'                    | jobLogs.'wrongOption'
        testCases.systemTest11.name | confignames.incorrect       | ''                         | ''                | ''                | stopLocations.'AppSrv01'       | stopPolicies.'none'    | ''        | summaries.'incorrectConfig'         | jobLogs.'incorrectConfig'
    }

    @Unroll
    def "StopNode 1 - Negative"(){
        given: "Parameters for procedure"
        def runParams = [
            configname: configName,
            wasAdditionalParameters: addParameters,
            wasLogFileLocation: logLocation,
            wasNodeProfile    : profile,
            wasStopNodeLocation: stopLocation,
            wasStopNodePolicy: stopPolicy,
            wasTimeout: timeout
        ]

        when: "Run procedure and wait until job is completed"
        def result 
        for (def i=0; i<2; i++){
            result = runProcedure(runParams)
            waitUntil {
            try {
                    jobCompleted(result)
                } catch (Exception e) {
                    println e.getMessage()
                }
            }
        }
        then: "Get and compare results"
        def outcome = getJobProperty('/myJob/outcome', result.jobId)
        def jobSummary = getJobProperty("/myJob/jobSteps/$procName/summary", result.jobId)
        def debugLog = getJobLogs(result.jobId)

        assert outcome == "error"
        assert jobSummary == expectedSummary
        for (log in logs){
            def text = log.replace("stopNodeReplace", stopLocation)      
            assert debugLog =~ text
        }

        cleanup: 
        startNode(stopPolicy)
        where: 'The following params will be:'
        testCaseID                  | configName                  | addParameters               | profile           | logLocation       | stopLocation                   | stopPolicy             | timeout   | expectedSummary       | logs
        testCases.systemTest12.name | confignames.correctSOAP     | ''                          | ''                | ''                | stopLocations.'AppSrv01'       | stopPolicies.'none'    | ''        | summaries.'fail'      | jobLogs.'stopped'
      }

    @Unroll
    def "StopNode 2 - Negative"(){
        given: "Parameters for procedure"
        def runParams = [
            configname: configName,
            wasAdditionalParameters: addParameters,
            wasLogFileLocation: logLocation,
            wasNodeProfile    : profile,
            wasStopNodeLocation: stopLocation,
            wasStopNodePolicy: stopPolicy,
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
            def text = log.replace("stopNodeReplace", stopLocation)      
            assert debugLog =~ text
        }
        cleanup: 
        startNode(stopPolicy)
        where: 'The following params will be:'
        testCaseID                  | configName                  | addParameters               | profile           | logLocation       | stopLocation                   | stopPolicy             | timeout   | expectedSummary       | logs
        testCases.systemTest13.name | confignames.correctSOAP     | ''                          | ''                | ''                | stopLocations.'AppSrv01'       | stopPolicies.'none'    | '5'       | summaries.'fail'      | jobLogs.'timeout'
      }


    def startNode(def stopPolicy){
        def startServer = stopPolicy == stopPolicies.'stop' ? 1 : 0
        def runParams = [
            configname: confignames.correctSOAP,
            wasAdditionalParameters: '',
            wasLogFileLocation: '',
            wasNodeName: nodes.'default',
            wasNodeProfile    : '',
            wasStartNodeLocation: startLocations.'AppSrv01',
            wasStartServers: startServer,
            wasTimeout: '300'
        ]
        def result = runProcedure(runParams, procStartNodeName)
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

}
