import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import PluginTestHelper


class DeleteApplicationServer extends PluginTestHelper {

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
    def configname = 'Web-Sphere-SOAP'
    @Shared
    def serverName = 'serverDeleteApplicationServer'
    @Shared
    def serverNode = 'websphere90ndNode01'

    @Shared
    def procName = 'DeleteApplicationServer'
    @Shared
    def procCreateServer = 'CreateApplicationServer'
    @Shared
    def procStartServer = 'StartApplicationServers'
    @Shared
    def projectName = "EC-WebSphere Specs $procName Project"

    @Shared
    String invalidServer = "\\!\\#\\*\\&server1"
    @Shared
    String invalidNode = "\\!\\#\\*\\&Node1"

    @Shared
    def testCases = [
            C363364: [
                    id: 'C363364',
                    description: 'Synchronize Nodes - True'],
            C363365: [
                    id: 'C363365',
                    description: 'Synchronize Nodes - False'],
            C363366_1: [
                    id: 'C363366 step 1',
                    description: 'empty required fields - Config Name'],
            C363366_2: [
                    id: 'C363366 step 2',
                    description: 'empty required fields - Node Name'],
            C363366_3: [
                    id: 'C363366 step 3',
                    description: 'empty required fields - Application Server Name'],
            C363366_4: [
                    id: 'C363366 step 4',
                    description: 'empty required fields - Synchronize Nodes?'],
            C363370_1: [
                    id: 'C363370 step 1',
                    description: 'wrong values - Node Name'],
            C363370_2: [
                    id: 'C363370 step 2',
                    description: 'wrong values - Application Server Name'],
            C363368: [
                    id: 'C363368',
                    description: 'incorrect Config Name'],
            C363369: [
                    id: 'C363369',
                    description: "server doesn't exist"],
            C363372: [
                    id: 'C363372',
                    description: "Node doesn't exist"],
            C363854: [
                    id: 'C363854',
                    description: 'delete running server']
    ]

    @Shared
    def summaries = [
            'default': "Application server $serverName on node $serverNode has been deleted\n",
            'error_config_empty': "Configuration '' doesn't exist",
            'error_config': "Configuration 'incorrectConfig' doesn't exist",
            'error_empty_s_name': "Failed to delete application server  on node $serverNode\nException: WASL6041E: The following argument value is not valid: serverName:.\n",
            'error_empty_node': "Failed to delete application server $serverName on node \nException: WASL6041E: The following argument value is not valid: nodeName:.\n",
            'error_invalid_node': "Failed to delete application server $serverName on node !#*&Node1\nException: WASL6040E: The nodeName:!#*&Node1 specified argument does not exist.\n",
            'error_not_ex_node': "Failed to delete application server $serverName on node notExist\nException: WASL6040E: The nodeName:notExist specified argument does not exist.\n",
            'error_invalid_s_name': "Failed to delete application server !#*&server1 on node $serverNode\nException: WASL6040E: The serverName:!#*&server1 specified argument does not exist.\n",
            'error_not_ex_s_name': "Failed to delete application server notExist on node $serverNode\nException: WASL6040E: The serverName:notExist specified argument does not exist.\n",

    ]

    @Shared
    def jobLogs = [
           'default_cynch':  ['success',"Application server $serverName on node $serverNode has been deleted","The following nodes have been synchronized: $serverNode"],
           'default_no_cynch':  ['success',"Application server $serverName on node $serverNode has been deleted"],
           'error_empty_node':  ['error',"Failed to delete application server $serverName on node ","Exception: WASL6041E: The following argument value is not valid: nodeName:."],
           'error_empty_s_name':  ['error',"Failed to delete application server  on node $serverNode","Exception: WASL6041E: The following argument value is not valid: serverName:."],
           'error_invalid_node':  ['error',"Failed to delete application server $serverName on node $invalidNode","Exception: WASL6040E: The nodeName:$invalidNode specified argument does not exist."],
           'error_not_ex_node':  ['error',"Failed to delete application server $serverName on node notExist","Exception: WASL6040E: The nodeName:notExist specified argument does not exist."],
           'error_invalid_s_name':  ['error',"Failed to delete application server $invalidServer on node $serverNode","Exception: WASL6040E: The serverName:$invalidServer specified argument does not exist."],
           'error_not_ex_s_name':  ['error',"Failed to delete application server notExist on node $serverNode","Exception: WASL6040E: The serverName:notExist specified argument does not exist."],
           'error_config':  ["Error: Configuration ('incorrectConfig'|'') doesn't exist"],
    ]

    def doSetupSpec() {
        def wasResourceName = wasHost
        createWorkspace(wasResourceName)
        createConfiguration(configname, [doNotRecreate: false])

        importProject(projectName, 'dsl/RunProcedure.dsl', [projName: projectName,
                                                            resName : wasResourceName,
                                                            procName: procName,
                                                            params  : [
                                                                    configname: '',
                                                                    wasAppServerName: '',
                                                                    wasNodeName: '',
                                                                    wasSyncNodes: '',
                                                            ]
        ])

        importProject(projectName, 'dsl/RunProcedure.dsl', [projName: projectName,
                                                            resName : wasResourceName,
                                                            procName: procCreateServer,
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

        importProject(projectName, 'dsl/RunProcedure.dsl', [projName: projectName,
                                                            resName : wasResourceName,
                                                            procName: procStartServer,
                                                            params  : [
                                                                    configname: '',
                                                                    wasServersList: '',
                                                                    wasWaitTime: '',
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
    def 'DeleteApplicationServer - #testCaseID.id #testCaseID.description'() {
        if (createServer) {
            createAppServer(node,server)
        }
        if (testCaseID.id == 'C363854'){
            startApplicationServer("$node:$server")
        }

        given: "Parameters for procedure"
        def runParams = [
                configname: configName,
                wasAppServerName: server,
                wasNodeName: node,
                wasSyncNodes: syncNodes,
        ]

        when: "Run procedure and wait until job is completed"
        def result = runProcedure(runParams)

        then: "Get and compare results"
        def outcome = getJobProperty('/myJob/outcome', result.jobId)
        def jobSummary = getJobProperty("/myJob/jobSteps/$procName/summary", result.jobId)
        def debugLog = getJobLogs(result.jobId)
        assert outcome == expectedOutcome
        assert jobSummary == expectedSummary
        for (log in logs) {
            assert debugLog =~ log
        }

        where: 'The following params will be:'
        testCaseID          | configName        | server        | node        | syncNodes | expectedSummary                | logs                         | expectedOutcome | createServer
        testCases.C363364   | configname        | serverName    | serverNode  | '1'       | summaries.default              | jobLogs.default_cynch        | 'success'       | 1
        testCases.C363365   | configname        | serverName    | serverNode  | '0'       | summaries.default              | jobLogs.default_no_cynch     | 'success'       | 1
        testCases.C363366_1 | ''                | serverName    | serverNode  | '1'       | summaries.error_config_empty   | jobLogs.error_config         | 'error'         | null
        testCases.C363366_2 | configname        | serverName    | ''          | '1'       | summaries.error_empty_node     | jobLogs.error_empty_node     | 'error'         | null
        testCases.C363366_3 | configname        | ''            | serverNode  | '1'       | summaries.error_empty_s_name   | jobLogs.error_empty_s_name   | 'error'         | null
        testCases.C363366_4 | configname        | serverName    | serverNode  | ''        | summaries.default              | jobLogs.default_no_cynch     | 'success'       | 1
        testCases.C363370_1 | configname        | serverName    | '!#*&Node1' | '1'       | summaries.error_invalid_node   | jobLogs.error_invalid_node   | 'error'         | null
        testCases.C363370_2 | configname        | '!#*&server1' | serverNode  | '1'       | summaries.error_invalid_s_name | jobLogs.error_invalid_s_name | 'error'         | null
        testCases.C363368   | 'incorrectConfig' | serverName    | serverNode  | '1'       | summaries.error_config         | jobLogs.error_config         | 'error'         | null
        testCases.C363369   | configname        | 'notExist'    | serverNode  | '1'       | summaries.error_not_ex_s_name  | jobLogs.error_not_ex_s_name  | 'error'         | null
        testCases.C363372   | configname        | serverName    | 'notExist'  | '1'       | summaries.error_not_ex_node    | jobLogs.error_not_ex_node    | 'error'         | null
        testCases.C363854   | configname        | serverName    | serverNode  | '1'       | summaries.default              | jobLogs.default_cynch        | 'success'       | 1
    }

    def createAppServer(node,server){
        def runParams = [
                configname: configname,
                wasAppServerName: server,
                wasGenUniquePorts: '1',
                wasNodeName: node,
                wasSourceServerName: '',
                wasSourceType: 'template',
                wasSyncNodes: '1',
                wasTemplateLocation: '',
                wasTemplateName: 'default',
        ]
        runProcedure(runParams, procCreateServer)
    }

    def startApplicationServer(serverList){
        def runParams = [
                configname: configname,
                wasServersList: serverList,
                wasWaitTime: '300',
        ]
        runProcedure(runParams, procStartServer)
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
        def result = dslWithTimeout(code)
        waitUntil {
            try {
                jobCompleted(result)
            } catch (Exception e) {
                println e.getMessage()
            }
        }
        return result
    }

}
