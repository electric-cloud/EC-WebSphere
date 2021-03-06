package com.electriccloud.plugin.spec

import spock.lang.*
import com.electriccloud.plugin.spec.PluginTestHelper
import com.electriccloud.spec.SpockTestSupport
import static org.junit.Assume.*

@Unroll
@Stepwise
@Requires({ System.getenv('IS_WAS_ND') == "1"})
class StopApplicationServers extends PluginTestHelper {

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
                    name: 'C363335 C363337',
                    description: 'only required values,wait time - server is stopped in Time'],
            systemTest2: [
                    name: 'C363336 C363338',
                    description: 'multiple servers,wait time - servers are stopped in Time'],
            systemTest3: [
                    name: 'C363339',
                    description: 'stop stopped server'],
            systemTest4: [
                    name: 'C363340',
                    description: 'stop stopped servers (both servers are stopped)'],
            systemTest5: [
                    name: 'C363341',
                    description: 'stop multiple servers (first is running, second is stopped)'],
            systemTest6: [
                    name: 'C363730',
                    description: 'stop multiple servers (first is stopped, second is running)'],
            systemTest7: [
                    name: 'C363732',
                    description: 'stop all servers (all are running)'],
            systemTest8: [
                    name: 'C363733',
                    description: 'stop all servers (all are stopped)'],
            systemTest9: [
                    name: 'C363342',
                    description: 'empty config'],
            systemTest10: [
                    name: 'C363343',
                    description: 'empty Server List'],
            systemTest11: [
                    name: 'C363345',
                    description: 'incorrect Config Name'],
            systemTest12: [
                    name: 'C363346',
                    description: 'incorrect format of Server List'],
            systemTest13: [
                    name: 'C363347',
                    description: 'incorrect servers'],
            systemTest14: [
                    name: 'C363348',
                    description: 'incorrect server(multiple servers, one correct, one incorrect)'],
            systemTest15: [
                    name: 'C363349',
                    description: 'wrong time format'],
            systemTest16: [
                    name: 'C363350',
                    description: 'wait time - server isn`t stopped in Time'],
            systemTest17: [
                    name: 'C363351',
                    description: 'wait time - servers aren`t stopped in Time'],
    ]

    @Shared
    def procName = 'StopApplicationServers'

    @Shared
    def procStartName = 'StartApplicationServers'

    @Shared
    def procCreateName = 'CreateApplicationServer'

    @Shared
    def procDeleteName = 'DeleteApplicationServer'

    @Shared
    def serverNode = wasHost + 'Node01'

    @Shared
    def serverLists = [
            'default':      "$serverNode:server1",
            'second':       "$serverNode:serverStopAppServer",
            'multiple':     "$serverNode:server1,$serverNode:serverStopAppServer",
            'all':          "$serverNode:*",
            'wrong':        "$serverNode=server1",
            'wrongServer':  "$serverNode:wrong_server1",
            'wrongServers': "$serverNode:server1,$serverNode:wrong_server1"

    ]

    @Shared
    def projectName = "EC-WebSphere Specs $procName Project"

    @Shared
    def summaries = [
            'default': "Application servers have been stopped:\nNode: $serverNode, Server: server1, State: Stopped",
            'multiple': "Application servers have been stopped:\n(Node: $serverNode, Server: .*, State: Stopped\n?)+",
            'warning': "Application servers have been stopped:\nAll servers are already Stopped\nWARNING: Server server1 on Node $serverNode is already Stopped\nWARNING: Nothing to do, all servers are already Stopped",
            'warning_both': "Application servers have been stopped:\nAll servers are already Stopped\n(WARNING: Server .* on Node $serverNode is already Stopped\n)+WARNING: Nothing to do, all servers are already Stopped",
            'warning_second': "Application servers have been stopped:\nNode: $serverNode, Server: server1, State: Stopped\nWARNING: Server serverStopAppServer on Node $serverNode is already Stopped",
            'warning_first': "Application servers have been stopped:\nNode: $serverNode, Server: serverStopAppServer, State: Stopped\nWARNING: Server server1 on Node $serverNode is already Stopped"
    ]

    @Shared
    def jobLogs = [
            'default':  ["Stop completed for middleware server \"server1\" on node \"$serverNode\"", "Node: $serverNode, Server: server1, State: Stopped"],
            'default_80nd':  ["Node: $serverNode, Server: server1, State: Stopped"],
            'multiple': ["Stop completed for middleware server \"server1\" on node \"$serverNode\"","Stop completed for middleware server \"serverStopAppServer\" on node \"$serverNode\""],
            'warning': ["Server server1 on Node $serverNode is already Stopped",'Nothing to do, all servers are already Stopped','warning','Application servers have been stopped'],
            'warning_both': ["Server server1 on Node $serverNode is already Stopped","Server serverStopAppServer on Node $serverNode is already Stopped",'Nothing to do, all servers are already Stopped','warning','Application servers have been stopped'],
            'warning_second': ["Stop completed for middleware server \"server1\" on node \"$serverNode\"", "Node: $serverNode, Server: server1, State: Stopped" , "Server serverStopAppServer on Node $serverNode is already Stopped",'warning','Application servers have been stopped'],
            'warning_second_80nd': ["Node: $serverNode, Server: server1, State: Stopped" , "Server serverStopAppServer on Node $serverNode is already Stopped",'warning','Application servers have been stopped'],
            'warning_first': ["Stop completed for middleware server \"serverStopAppServer\" on node \"$serverNode\"", "Node: $serverNode, Server: serverStopAppServer, State: Stopped" , "Server server1 on Node $serverNode is already Stopped",'warning','Application servers have been stopped'],
            'warning_first_80nd': ["Node: $serverNode, Server: serverStopAppServer, State: Stopped" , "Server server1 on Node $serverNode is already Stopped",'warning','Application servers have been stopped'],
            'error': ['error','Failed to stop servers:',"Node: $serverNode, Server: server1, State: (STOPPING|STARTED)",'Some servers are failed to stop'],
            'error_both': ['error','Failed to stop servers:',"Node: $serverNode, Server: server1, State: (STOPPING|STARTED)","Server serverStopAppServer on Node $serverNode is already Stopped",'Some servers are failed to stop'],
            'error_empty_config': ["Error: Configuration '' doesn't exist"],
            'error_config': ["Error: Configuration 'incorrect' doesn't exist"],
            'error_empty_ServerList': ['error','Failed to stop servers:', 'Missing servers list to be stopped'],
            'error_ServerList': ['error','Failed to stop servers:', "Expected nodename:servername record, got $serverNode=server1"],
            'error_Server': ['error','Failed to stop servers:', "Failed to stop server wrong_server1 on node $serverNode"],
            'error_WaitTime': ['error','Wait time should be a positive integer, if present. Got: 9am']
    ]

    @Shared
    def errors = [
            'emptyConfig': "Configuration '' doesn't exist",
            'emptyServerList': "Failed to stop servers:\nMissing servers list to be stopped",
            'incorrectConfig': "Configuration 'incorrect' doesn't exist",
            'incorrectServerListFormat':"Failed to stop servers:\nExpected nodename:servername record, got $serverNode=server1",
            'incorrectServer':"Failed to stop servers:\nFailed to stop server wrong_server1 on node $serverNode(\nADMF0003E: Invalid parameter value wrong_server1 for parameter serverName for command stopMiddlewareServer.)?",
            'incorrectServers':"Failed to stop servers:\nFailed to stop server wrong_server1 on node $serverNode(\nADMF0003E: Invalid parameter value wrong_server1 for parameter serverName for command stopMiddlewareServer.)?",
            'incorrectWaitTime':"Wait time should be a positive integer, if present. Got: 9am",
            'zeroWaitTime':"Failed to stop servers:\nNode: $serverNode, Server: server1, State: (STOPPING|STARTED)\nSome servers are failed to stop",
            'zeroWaitTimeMultiple':"Failed to stop servers:\nNode: $serverNode, Server: server1, State: (STOPPING|STARTED)\nNode: $serverNode, Server: serverStopAppServer, State: (STOPPING|STARTED)\nSome servers are failed to stop"
    ]

    def doSetupSpec() {
        def wasResourceName = wasHost
        createWorkspace(wasResourceName)
        createConfiguration(confignames.correctSOAP, [doNotRecreate: false])
        change_logs(wasHost)

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
                                                            procName: procStartName,
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
                                                                    wasAppServerName: '',
                                                                    wasGenUniquePorts: '',
                                                                    wasNodeName: '',
                                                                    wasSourceServerName: '',
                                                                    wasSourceType: '',
                                                                    wasSyncNodes: '',
                                                                    wasTemplateLocation: '',
                                                                    wasTemplateName: '',
                                                            ]
        ])

        importProject(projectName, 'dsl/RunCustomShellCommand/RunCustomShellCommand.dsl', [projectName: projectName,
                                                                                           wasResourceName : wasResourceName,
                                                                                           procName: 'runCustomShellCommand',
        ])

        dsl 'setProperty(propertyName: "/plugins/EC-WebSphere/project/ec_debug_logToProperty", value: "/myJob/debug_logs")'

        createAppServer(serverNode,'serverStopAppServer')
    }

    def doCleanupSpec() {
        def wasResourceName = wasHost

        importProject(projectName, 'dsl/RunProcedure.dsl', [projName: projectName,
                                                            resName : wasResourceName,
                                                            procName: procDeleteName,
                                                            params  : [
                                                                    configname: '',
                                                                    wasAppServerName: '',
                                                                    wasNodeName: '',
                                                                    wasSyncNodes: '',
                                                            ]
        ])

        deleteApplicationServer(serverNode,'serverStopAppServer')
    }

    @Unroll
    def 'StopApplicationServer - Positive: #testCaseID.name #testCaseID.description'() {

        if (startedServers) {
            startApplicationServer(startedServers)
        }
        given: "Parameters for procedure"
        def runParams = [
                configname    : configName,
                wasServersList: serverList,
                wasWaitTime   : timeout,
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
        assert outcome == expectedOutcome

        if ( expectedSummary == summaries.warning_both){
            assert jobSummary ==~ expectedSummary
        } else {
            assert jobSummary == expectedSummary
        }

        for (log in logs) {
            assert debugLog =~ log
        }

        where: 'The following params will be:'
        testCaseID            | configName              | serverList            | timeout | expectedSummary           | logs                   | expectedOutcome | startedServers
        testCases.systemTest1 | confignames.correctSOAP | serverLists.'default' | '60'    | summaries.'default'       | jobLogs.'default'      | 'success'       | serverLists.default
        testCases.systemTest3 | confignames.correctSOAP | serverLists.'default' | '300'   | summaries.'warning'       | jobLogs.'warning'      | 'warning'       | null
        testCases.systemTest4 | confignames.correctSOAP | serverLists.'multiple'| '300'   | summaries.warning_both    | jobLogs.warning_both   | 'warning'       | null
        testCases.systemTest5 | confignames.correctSOAP | serverLists.'multiple'| '300'   | summaries.warning_second  | jobLogs.warning_second | 'warning'       | serverLists.default
        // commenting this because 5 already has this logic
        // testCases.systemTest6 | confignames.correctSOAP | serverLists.'multiple'| '300'   | summaries.warning_first   | jobLogs.warning_first  | 'warning'       | serverLists.second
        testCases.systemTest8 | confignames.correctSOAP | serverLists.all       | '300'   | summaries.warning_both    | jobLogs.warning_both   | 'warning'       | null
    }


    @Unroll
    def 'StopApplicationServer - Positive(for multiple): #testCaseID.name #testCaseID.description'() {

        startApplicationServer(startedServers)

        given: "Parameters for procedure"
        def runParams = [
                configname    : configName,
                wasServersList: serverList,
                wasWaitTime   : timeout,
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

        if (outcome == 'success'){
            assert jobSummary ==~ summaries.multiple
        }

        for (log in logs) {
            assert debugLog =~ logs
        }

        where: 'The following params will be:'
        testCaseID            | configName              | serverList            | timeout | logs             | startedServers
        testCases.systemTest2 | confignames.correctSOAP | serverLists.multiple  | '60'    | jobLogs.multiple | serverLists.multiple
        testCases.systemTest7 | confignames.correctSOAP | serverLists.all       | '300'   | jobLogs.multiple | serverLists.all
    }

    @Unroll
    def 'StopApplicationServer - Negative: : #testCaseID.name #testCaseID.description'(){
        assumeFalse(wasHost=='websphere80nd' && (testCaseID.name=='C363350' || testCaseID.name=='C363351'))
        assumeFalse(is_windows && (testCaseID.name=='C363350' || testCaseID.name=='C363351'))

        if (startedServers){
            startApplicationServer(startedServers)
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
        assert outcome == expectedOutcome
        if (testCaseID == testCases.systemTest16 || testCaseID == testCases.systemTest17 || testCaseID == testCases.systemTest13 || testCaseID == testCases.systemTest14 ){
            assert jobSummary =~ expectedSummary
        } else {
            assert jobSummary == expectedSummary
        }
        def debugLog = getJobLogs(result.jobId)
        for (log in logs){
            assert debugLog =~ log
        }
        cleanup:
        if (testCaseID.name=='C363351'){
            runParams.wasWaitTime = '50'
            runProcedure(runParams)
        }
        where: 'The following params will be:'
        testCaseID             | configName              | serverList                  | timeout   | expectedSummary                  | logs                           | expectedOutcome | startedServers
        testCases.systemTest9  | ''                      | serverLists.'default'       | '300'     | errors.emptyConfig               | jobLogs.error_empty_config     | 'error'         | null
        testCases.systemTest10 | confignames.correctSOAP | ''                          | '300'     | errors.emptyServerList           | jobLogs.error_empty_ServerList | 'error'         | null
        testCases.systemTest11 | confignames.incorrect   | serverLists.'default'       | '300'     | errors.incorrectConfig           | jobLogs.error_config           | 'error'         | null
        testCases.systemTest12 | confignames.correctSOAP | serverLists.'wrong'         | '300'     | errors.incorrectServerListFormat | jobLogs.error_ServerList       | 'error'         | null
        testCases.systemTest13 | confignames.correctSOAP | serverLists.'wrongServer'   | '300'     | errors.incorrectServer           | jobLogs.error_Server           | 'error'         | null
        testCases.systemTest14 | confignames.correctSOAP | serverLists.'wrongServers'  | '300'     | errors.incorrectServers          | jobLogs.error_Server           | 'error'         | serverLists.second
        testCases.systemTest15 | confignames.correctSOAP | serverLists.'default'       | '9am'     | errors.incorrectWaitTime         | jobLogs.error_WaitTime         | 'error'         | serverLists.default
        testCases.systemTest16 | confignames.correctSOAP | serverLists.'default'       | '0'       | errors.zeroWaitTime              | jobLogs.error                  | 'error'         | serverLists.default
        testCases.systemTest17 | confignames.correctSOAP | serverLists.'multiple'      | '0'       | errors.zeroWaitTimeMultiple      | [errors.zeroWaitTimeMultiple]  | 'error'         | serverLists.'multiple'
    }
    def startApplicationServer(serverList){
        def runParams = [
                configname: confignames.correctSOAP,
                wasServersList: serverList,
                wasWaitTime: '300',
        ]
        def result = runProcedure(runParams, procStartName)
        waitUntil {
            try {
                jobCompleted(result)
            } catch (Exception e) {
                println e.getMessage()
            }
        }
    }
    def createAppServer(node,server){
        def runParams = [
                configname: confignames.correctSOAP,
                wasAppServerName: server,
                wasGenUniquePorts: '1',
                wasNodeName: node,
                wasSourceServerName: "$serverNode:server1",
                wasSourceType: 'server',
                wasSyncNodes: '1',
                wasTemplateLocation: '',
                wasTemplateName: '',
        ]
        def result = runProcedure(runParams, procCreateName)
        waitUntil {
            try {
                jobCompleted(result)
            } catch (Exception e) {
                println e.getMessage()
            }
        }
    }
    def deleteApplicationServer(node,server){
        def runParams = [
                configname: confignames.correctSOAP,
                wasAppServerName: server,
                wasNodeName: node,
                wasSyncNodes: '1',
        ]
        def result = runProcedure(runParams, procDeleteName)
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

    def change_logs(version){
        if (version == "websphere80nd"){
            jobLogs.default = jobLogs.default_80nd
            jobLogs.warning_first = jobLogs.warning_first_80nd
            jobLogs.warning_second = jobLogs.warning_second_80nd
        }
    }
}
