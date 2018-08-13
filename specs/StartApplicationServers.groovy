import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import PluginTestHelper

class StartApplicationServers extends PluginTestHelper {

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
            name: 'C363296 C363298',
            description: 'only required values, wait time - server starts in Time'],
        systemTest2: [
            name: 'C363297 C363302',
            description: 'multiple servers, wait time - servers start in time'],
        systemTest3: [
            name: 'C363298',
            description: 'wait time - server starts in Time'],
        systemTest4: [
            name: 'C363302',
            description: 'wait time - servers start in time'],
        systemTest5: [
            name: 'C363299',
            description: 'start running server'],
        systemTest6: [
            name: 'C363300',
            description: 'multiple servers (all servers run)'],
        systemTest7: [
            name: 'C363301',
            description: 'multiple servers (one server run, one server is stopped)'],
        systemTest8: [
            name: 'C363303',
            description: 'empty config'],
        systemTest9: [
            name: 'C363304',
            description: 'empty Server List'],
        systemTest10: [
            name: 'C363305',
            description: 'incorrect Config'],
        systemTest11: [
            name: 'C363306',
            description: 'incorrect Config Name '],
        systemTest12: [
            name: 'C363307',
            description: 'incorrect format of Server List'],
        systemTest13: [
            name: 'C363308',
            description: 'incorrect servers'],
        systemTest14: [
            name: 'C363309',
            description: 'incorrect server(multiple servers, one correct, one incorrect)'],
        systemTest16: [
            name: 'C363311',
            description: 'wait time - server doesn`t start in Time'],
        systemTest17: [
            name: 'C363312',
            description: 'wait time - servers don`t start in Time'],                                                                                                                        
    ]

    @Shared
    def procName = 'StartApplicationServers'

    @Shared
    def procStopName = 'StopApplicationServers'

    @Shared
    def procCreateName = 'CreateApplicationServer'

    @Shared
    def serverLists = [
        'default':      'websphere90ndNode01:server1',
        'multiple':     'websphere90ndNode01:server1,websphere90ndNode01:serverStartAppServer',
        'second':       'websphere90ndNode01:serverStartAppServer',
        'all':          'websphere90ndNode01:*',
        'wrong':        'websphere90ndNode01=server1',
        'wrongServer':  'websphere90ndNode01:wrong_server1',
        'wrongServers': 'websphere90ndNode01:server1,websphere90ndNode01=wrong_server1',

    ]

    @Shared
    def projectName = "EC-WebSphere Specs $procName Project"

    @Shared
    def summaries = [  
        'default': "Application Servers have been started:\nNode: websphere90ndNode01, Server: server1, State: STARTED",
        'started': "Application Servers have been started:\nAll servers are already STARTED\nWARNING: Server server1 on Node websphere90ndNode01 is already STARTED\nWARNING: Nothing to do, all servers are already STARTED",
        'started2': "Application Servers have been started:\nAll servers are already STARTED\nWARNING: Server server1 on Node websphere90ndNode01 is already STARTED\nWARNING: Server serverStartAppServer on Node websphere90ndNode01 is already STARTED\nWARNING: Nothing to do, all servers are already STARTED",
        'started3': "Application Servers have been started:\nNode: websphere90ndNode01, Server: serverStartAppServer, State: STARTED\nWARNING: Server server1 on Node websphere90ndNode01 is already STARTED",
        'multiple': "Application Servers have been started:\nNode: websphere90ndNode01, Server: server1, State: STARTED\nNode: websphere90ndNode01, Server: serverStartAppServer, State: STARTED",
        'first': "Application Servers have been started:\nNode: websphere90ndNode01, Server: server1, State: STARTED\nWARNING: Server serverStartAppServer on Node websphere90ndNode01 is already STARTED",    
        'emptyConfig': "Configuration '' doesn't exist",
        'failed': 'Failed to start servers:\nNode: websphere90ndNode01, Server: server1, State: Stopped\nFailed to start servers',
        'emptyServer': 'Failed to start servers:\nMissing servers list to be started',
        'wrongFormat': 'Failed to start servers:\nExpected nodename:servername record, got websphere90ndNode01=server1',
        'wrongServer':  "Failed to start servers",
        'wrongServers': 'Not added',
        'timeoutServer': 'Not added',
        'timeoutServers': 'Not added',
        'incorrectConfig': "Configuration 'incorrect' doesn't exist",
        ]

    @Shared
    def jobLogs = [
        'default':  ['Start completed for middleware server "server1" on node "websphere90ndNode01"', "Node: websphere90ndNode01, Server: server1, State: STARTED"],           
        'started':  ["Server server1 on Node websphere90ndNode01 is already STARTED", "Nothing to do, all servers are already STARTED"],
        'started2': ["Server server1 on Node websphere90ndNode01 is already STARTED", "Server serverStartAppServer on Node websphere90ndNode01 is already STARTED", "Nothing to do, all servers are already STARTED"],
        'started3': ["Node: websphere90ndNode01, Server: serverStartAppServer, State: STARTED", "Server server1 on Node websphere90ndNode01 is already STARTED'"],
        'multiple': ['Start completed for middleware server "server1" on node "websphere90ndNode01"', 'Start completed for middleware server "serverStartAppServer" on node "websphere90ndNode01"',
            "Node: websphere90ndNode01, Server: server1, State: STARTED", "Node: websphere90ndNode01, Server: serverStartAppServer, State: STARTED'"],
        'first': ['Server serverStartAppServer on Node websphere90ndNode01 is already STARTED', 'Start completed for middleware server "server1" on node "websphere90ndNode01"'],
        'emptyConfig': ["Error: Configuration '' doesn't exist"],
        'emptyServer': ['Missing servers list to be started'],
        'wrongFormat': ['ValueError: Expected nodename:servername record, got websphere90ndNode01=server1'],
        'wrongServer':  ['Invalid parameter value wrong_server1 for parameter serverName for command startMiddlewareServer.'],
        'wrongServers': ['Expected nodename:servername record, got websphere90ndNode01=wrong_server1'],
        'timeoutServer': ['Not added'],
        'timeoutServers': ['Not added'],
        'incorrectConfig': ["Configuration 'incorrect' doesn't exist"],
        'failed': ['Failed to start servers', 'Node: websphere90ndNode01, Server: server1, State: Stopped'],
    ]


    def doSetupSpec() {
        def wasResourceName = wasHost
        createWorkspace(wasResourceName)
        createConfiguration(confignames.correctSOAP, [doNotRecreate: false])

        importProject(projectName, 'dsl/RunProcedure.dsl', [projName: projectName,
                resName : wasResourceName,
                procName: procName,
                params  : [
                        configname: '',
                        wasServersList: '',
                        wasWaitTime: '',
                ]
        ])

        importProject(projectName, 'dsl/RunProcedure.dsl', [projName: projectName,
                resName : wasResourceName,
                procName: procStopName,
                params  : [
                        configname: '',
                        wasServersList: '',
                        wasWaitTime: '',
                ]
        ])

        importProject(projectName, 'dsl/RunProcedure.dsl', [projName: projectName,
                resName : wasResourceName,
                procName: procCreateName,
                params  : [
                    configname: '',
                    headerCreationSource: '',
                    wasArchivePath: '',
                    wasNodeName: '',
                    wasServerName: '',
                    wasSourceServerName: '',
                    wasSourceType: '',
                    wasTemplateName: '',
                ]
        ])

        importProject(projectName, 'dsl/RunCustomShellCommand/RunCustomShellCommand.dsl', [projectName: projectName,
            wasResourceName : wasResourceName,
            procName: 'runCustomShellCommand',
        ])

        dsl 'setProperty(propertyName: "/plugins/EC-WebSphere/project/ec_debug_logToProperty", value: "/myJob/debug_logs")' 

        // createAppServer('serverStartAppServer')   
    }

    def doCleanupSpec() {
        def runParams = [
            configname: confignames.correctSOAP,
            wasServersList: serverLists.'default',
            wasWaitTime: '300',
        ]

        def result = runProcedure(runParams)
        waitUntil {
        try {
                jobCompleted(result)
            } catch (Exception e) {
                println e.getMessage()
            }
        }

    }

    @Unroll
    def 'StartApplicationServer - Positive: #testCaseID.name #testCaseID.description'(){
        if (stoppedServers){
            stopApplicationServer(stoppedServers)
        }
        given: "Parameters for procedure"
        def runParams = [
            configname: configName,
            wasServersList: serverList,
            wasWaitTime: timeout,
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
            assert debugLog =~ log
        }


        where: 'The following params will be:'
        testCaseID             | configName              | serverList              | timeout   | expectedSummary       | status    | logs              | stoppedServers  
        testCases.systemTest1  | confignames.correctSOAP | serverLists.'default'   | '300'     | summaries.'default'   | "success" | jobLogs.'default' | serverLists.'default'
        testCases.systemTest2  | confignames.correctSOAP | serverLists.'multiple'  | '300'     | summaries.'multiple'  | "success" | jobLogs.'default' | serverLists.'multiple'
        testCases.systemTest2  | confignames.correctSOAP | serverLists.'all'       | '300'     | summaries.'multiple'  | "success" | jobLogs.'default' | serverLists.'multiple'
        // logic of this test cases(3,4) are included in the first two cases
        // testCases.systemTest3  | confignames.correctSOAP | serverLists.'default'   | '300'     | summaries.'default'   | "success" | jobLogs.'default' | serverLists.'default'
        // testCases.systemTest4  | confignames.correctSOAP | serverLists.'multiple'  | '300'     | summaries.'default'   | "success" | jobLogs.'default' | serverLists.'multiple'
        testCases.systemTest5  | confignames.correctSOAP | serverLists.'default'   | '300'     | summaries.'started'   | "warning" | jobLogs.'started' | null
        testCases.systemTest6  | confignames.correctSOAP | serverLists.'multiple'  | '300'     | summaries.'started2'  | "warning" | jobLogs.'started2'| null
        testCases.systemTest7  | confignames.correctSOAP | serverLists.'multiple'  | '300'     | summaries.'first'     | "warning" | jobLogs.'first'   | serverLists.'default'
        testCases.systemTest7  | confignames.correctSOAP | serverLists.'multiple'  | '300'     | summaries.'started3'  | "warning" | jobLogs.'started3'| serverLists.'second'
    }

    @Unroll
    def "StartApplicationServer - Negative: #testCaseID.name #testCaseID.description"(){
        if (stoppedServers){
            stopApplicationServer(stoppedServers)
        }
        given: "Parameters for procedure"
        def runParams = [
            configname: configName,
            wasServersList: serverList,
            wasWaitTime: timeout,
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
        assert jobSummary =~ expectedSummary
        for (log in logs){
            assert debugLog =~ log
        }


        where: 'The following params will be:'
        testCaseID             | configName              | serverList                  | timeout   | expectedSummary              | logs                      | stoppedServers  
        testCases.systemTest16 | confignames.correctSOAP | serverLists.'default'       | '0'       | summaries.'failed'           | jobLogs.'failed'          | serverLists.'default'
        testCases.systemTest16 | confignames.correctSOAP | serverLists.'default'       | ''        | summaries.'failed'           | jobLogs.'failed'   | serverLists.'default'
        // testCases.systemTest17 | confignames.correctSOAP | serverLists.'multiple'      | '0'       | summaries.'timeoutServers'   | jobLogs.'timeoutServers'  | serverLists.'multiple'
        testCases.systemTest8  | ''                      | serverLists.'default'       | ''        | summaries.'emptyConfig'      | jobLogs.'emptyConfig'     | null
        testCases.systemTest9  | confignames.correctSOAP | ''                          | ''        | summaries.'emptyServer'      | jobLogs.'emptyServer'     | null
        testCases.systemTest11 | confignames.incorrect   | serverLists.'default'       | ''        | summaries.'incorrectConfig'  | jobLogs.'incorrectConfig' | null
        testCases.systemTest12 | confignames.correctSOAP | serverLists.'wrong'         | ''        | summaries.'wrongFormat'      | jobLogs.'wrongFormat'     | null
        testCases.systemTest13 | confignames.correctSOAP | serverLists.'wrongServer'   | ''        | summaries.'wrongServer'      | jobLogs.'wrongServer'     | null
        testCases.systemTest14 | confignames.correctSOAP | serverLists.'wrongServers'  | ''        | summaries.'wrongServer'      | jobLogs.'wrongServers'     | serverLists.'default'
        testCases.systemTest14 | confignames.correctSOAP | serverLists.'wrongServers'  | ''        | summaries.'wrongServer'      | jobLogs.'wrongServers'     | serverLists.'second'

   }    

    def stopApplicationServer(serverList){
        def runParams = [
            configname: confignames.correctSOAP,
            wasServersList: serverList,
            wasWaitTime: '300',
        ]
        def result = runProcedure(runParams, procStopName)
        waitUntil {
        try {
                jobCompleted(result)
            } catch (Exception e) {
                println e.getMessage()
            }
        }
    }

    def createAppServer(server){
        def runParams = [
            configname: '',
            headerCreationSource: '',
            wasArchivePath: '',
            wasNodeName: '',
            wasServerName: '',
            wasSourceServerName: '',
            wasSourceType: '',
            wasTemplateName: '',
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