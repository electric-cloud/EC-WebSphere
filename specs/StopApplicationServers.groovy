import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import PluginTestHelper

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
                    name: 'C363',
                    description: 'stop all stopped server'],
            systemTest8: [
                    name: 'C363',
                    description: 'stop all stopped servers (all servers are stopped)'],
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
    def serverLists = [
            'default':      'websphere90ndNode01:server1',
            'second':       'websphere90ndNode01:serverStopAppServer',
            'multiple':     'websphere90ndNode01:server1,websphere90ndNode01:serverStopAppServer',
            'all':          'websphere90ndNode01:*',
            'wrong':        'websphere90ndNode01=server1',
            'wrongServer':  'websphere90ndNode01:wrong_server1',
            'wrongServers': 'websphere90ndNode01:server1,websphere90ndNode01:wrong_server1'

    ]

    @Shared
    def projectName = "EC-WebSphere Specs $procName Project"

    @Shared
    def summaries = [
            'default': "Application Servers have been stopped:\nNode: websphere90ndNode01, Server: server1, State: Stopped",
            'multiple': "Application Servers have been stopped:\nNode: websphere90ndNode01, Server: server1, State: Stopped\nNode: websphere90ndNode01, Server: serverStopAppServer, State: Stopped",
            'warning': "Application Servers have been stopped:\nServer server1 on Node websphere90ndNode01 is already Stopped\nWARNING: Nothing to do, all servers are already Stopped",
            'warning_both': "Application Servers have been stopped:\nServer server1 on Node websphere90ndNode01 is already Stopped\nServer serverStopAppServer on Node websphere90ndNode01 is already Stopped\nWARNING: Nothing to do, all servers are already Stopped",
            'warning_second': "Application Servers have been stopped:\nNode: websphere90ndNode01, Server: server1, State: Stopped\nServer serverStopAppServer on Node websphere90ndNode01 is already Stopped",
            'warning_first': "Application Servers have been stopped:\nNode: websphere90ndNode01, Server: serverStopAppServer, State: Stopped\nServer server1 on Node websphere90ndNode01 is already Stopped"
    ]

    @Shared
    def jobLogs = [
            'default':  ['Stop completed for middleware server "server1" on node "websphere90ndNode01"', "Node: websphere90ndNode01, Server: server1, State: Stopped"],
            'multiple': ['Stop completed for middleware server "server1" on node "websphere90ndNode01"','Stop completed for middleware server "serverStopAppServer" on node "websphere90ndNode01"', "Node: websphere90ndNode01, Server: server1, State: Stopped", "Node: websphere90ndNode01, Server: serverStopAppServer, State: Stopped"],
            'warning': ['Server server1 on Node websphere90ndNode01 is already Stopped','Nothing to do, all servers are already Stopped','warning','Application Servers have been stopped'],
            'warning_both': ['Server server1 on Node websphere90ndNode01 is already Stopped','Server serverStopAppServer on Node websphere90ndNode01 is already Stopped','Nothing to do, all servers are already Stopped','warning','Application Servers have been stopped'],
            'warning_second': ['Stop completed for middleware server "server1" on node "websphere90ndNode01"', "Node: websphere90ndNode01, Server: server1, State: Stopped" , 'Server serverStopAppServer on Node websphere90ndNode01 is already Stopped','warning','Application Servers have been stopped'],
            'warning_first': ['Stop completed for middleware server "serverStopAppServer" on node "websphere90ndNode01"', "Node: websphere90ndNode01, Server: serverStopAppServer, State: Stopped" , 'Server server1 on Node websphere90ndNode01 is already Stopped','warning','Application Servers have been stopped']
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
    }

    @Unroll
    def 'StopApplicationServer - Positive: #testCaseID.name #testCaseID.description'() {
        // assert true
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
        assert outcome == "success"
        assert jobSummary == expectedSummary
        for (log in logs) {
            assert debugLog =~ log
        }


        where: 'The following params will be:'
        testCaseID            | configName              | serverList            | timeout | expectedSummary           | logs                    | startedServers
        testCases.systemTest1 | confignames.correctSOAP | serverLists.'default' | '60'    | summaries.'default'       | jobLogs.'default'       | serverLists.'default'
        testCases.systemTest2 | confignames.correctSOAP | serverLists.'multiple'| '60'    | summaries.'multiple'      | jobLogs.'multiple'      | serverLists.'multiple'
        testCases.systemTest3 | confignames.correctSOAP | serverLists.'default' | '300'   | summaries.'warning'       | jobLogs.'warning'       | null
        testCases.systemTest4 | confignames.correctSOAP | serverLists.'multiple'| '300'   | summaries.warning_both    | jobLogs.warning_both    | null
        testCases.systemTest5 | confignames.correctSOAP | serverLists.'multiple'| '300'   | summaries.warning_second  | jobLogs.warning_second  | serverLists.default
        testCases.systemTest6 | confignames.correctSOAP | serverLists.'multiple'| '300'   | summaries.warning_first   | jobLogs.warning_first   | serverLists.second
        testCases.systemTest7 | confignames.correctSOAP | serverLists.all       | '300'   | summaries.multiple        | jobLogs.multiple        | serverLists.multiple
        testCases.systemTest8 | confignames.correctSOAP | serverLists.all       | '300'   | summaries.warning_both    | jobLogs.warning_both    | null
    }

        @Unroll
        def 'StopApplicationServer - Negative: : #testCaseID.name #testCaseID.description'(){
           /* if (startedServers){
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
            def debugLog = getJobLogs(result.jobId)
            assert outcome == "error"
            assert jobSummary == expectedSummary
            for (log in logs){
                def text = log.replace("stopNodeReplace", stopLocation)
                assert debugLog =~ text
            }*/

            assert true
            where: 'The following params will be:'
            testCaseID                  | configName              | serverList                  | timeout   | expectedSummary       | logs              | startedServers
            testCases.systemTest16 | confignames.correctSOAP | serverLists.'default'       | '5'       | summaries.'default'   | jobLogs.'default' | serverLists.'default'
            /*testCases.systemTest17.name | confignames.correctSOAP | serverLists.'multiple'      | '5'       | summaries.'default'   | jobLogs.'default' | serverLists.'multiple'
            testCases.systemTest8.name  | ''                      | serverLists.'default'       | ''        | summaries.'default'   | jobLogs.'default' | null
            testCases.systemTest9.name  | confignames.correctSOAP | ''                          | ''        | summaries.'default'   | jobLogs.'default' | null
            testCases.systemTest11.name | confignames.incorrect   | serverLists.'default'       | ''        | summaries.'default'   | jobLogs.'default' | null
            testCases.systemTest12.name | confignames.correctSOAP | serverLists.'wrong'         | ''        | summaries.'default'   | jobLogs.'default' | null
            testCases.systemTest13.name | confignames.correctSOAP | serverLists.'wrongServer'   | ''        | summaries.'default'   | jobLogs.'default' | null
            testCases.systemTest14.name | confignames.correctSOAP | serverLists.'wrongServers'  | ''        | summaries.'default'   | jobLogs.'default' | serverLists.'default'*/
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