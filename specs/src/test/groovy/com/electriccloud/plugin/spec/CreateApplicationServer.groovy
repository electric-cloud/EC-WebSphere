package com.electriccloud.plugin.spec

import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import com.electriccloud.plugin.spec.PluginTestHelper


class CreateApplicationServer extends PluginTestHelper {

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
    def serverName = 'serverCreateApplicationServer'
    @Shared
    def serverNode = 'websphere90ndNode01'
    @Shared
    def sourceTemplateName = 'default'
    @Shared
    def sourceServerName = 'websphere90ndNode01:server1'

    @Shared
    def procName = 'CreateApplicationServer'
    @Shared
    def procDeleteServer = 'DeleteApplicationServer'
    @Shared
    def procStartServer = 'StartApplicationServers'
    @Shared
    def projectName = "EC-WebSphere Specs $procName Project"

    @Shared
    String invalidServer = "\\!\\#\\*\\&server1"
    @Shared
    String invalidNode = "\\!\\#\\*\\&Node1"
    @Shared
    String intermediateTemplate = "[a-z0-9]{8}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{12}"

    @Shared
    def testCases = [
            C363899_1: [
                    id: 'C363899 step 1',
                    description: 'required values - template'],
            C363899_2: [
                    id: 'C363899 step 2',
                    description: 'required values - server'],
            C363901_1: [
                    id: 'C363901 step 1',
                    description: 'all fields (required + Source fields)'],
            C363901_2: [
                    id: 'C363901 step 2',
                    description: 'all fields (required + Source fields)'],
            C363901_3: [
                    id: 'C363901 step 3',
                    description: 'all fields (required + Source fields)'],
            C363901_4: [
                    id: 'C363901 step 4',
                    description: 'all fields (required + Source fields)'],
            C363901_5: [
                    id: 'C363901 step 5',
                    description: 'all fields (required + Source fields)'],
            C363901_6: [
                    id: 'C363901 step 6',
                    description: 'all fields (required + Source fields) - Source Type=template, template name='],
            C363892_1: [
                    id: 'C363892 step 1',
                    description: 'empty required fields - config'],
            C363892_2: [
                    id: 'C363892 step 2',
                    description: 'empty required fields - node'],
            C363892_3: [
                    id: 'C363892 step 3',
                    description: 'empty required fields - server'],
            C363892_4: [
                    id: 'C363892 step 4',
                    description: 'empty required fields - Source Type=server, server name='],
            C363893: [
                    id: 'C363893',
                    description: 'incorrect Config Name'],
            C363894_1: [
                    id: 'C363894 step 1',
                    description: 'incorrect Node & Server - Node Name = !#*&Node1'],
            C363894_2: [
                    id: 'C363894 step 2',
                    description: 'incorrect Node & Server - Server Name = !#*&server2'],
            C363894_3: [
                    id: 'C363894 step 3',
                    description: 'incorrect Node & Server - Source Server Name = notExist'],
            C363894_4: [
                    id: 'C363894 step 4',
                    description: 'incorrect Node & Server - Node Name = notExist'],
            C363894_5: [
                    id: 'C363894 step 5',
                    description: 'incorrect Node & Server - Source Template Name = incorrectTemplate'],

            C363895: [
                    id: 'C363895',
                    description: 'Server name already exists']
    ]

    @Shared
    def summaries = [
            'default': "Application server $serverName has been created on node $serverNode\n",
            'error_server_exist': "Failed to create $serverName server on $serverNode node\nException: ADMG0248E: $serverName exists within node $serverNode.\n",
            'error_config_empty': "Configuration '' doesn't exist",
            'error_empty_node': "Failed to create $serverName server on  node\nException: ADMG0250E: Node websphere90ndCellManager01 is not a valid node.\n",
            'error_empty_s_name': "Failed to create  server on $serverNode node\nException: ADMF0002E: Required parameter name is not found for command createApplicationServer.\n",
            'error_empty_source_s': "Failed to create an application server.\nError: Source server name is required when source type is set to server\n",
            'error_config': "Configuration 'incorrectConfig' doesn't exist",
            'error_invalid_node': "Failed to create $serverName server on !#*&Node1 node\nException: ADMG0250E: Node !#*&Node1 is not a valid node.\n",
            'error_invalid_s_name':  "Failed to create !#*&server1 server on $serverNode node\nException: ADMG0249E: The server name !#*&server1 is invalid.\n",
            'error_not_ex_node': "Failed to create $serverName server on notExist node\nException: ADMG0250E: Node notExist is not a valid node.\n",
            'error_not_ex_s_name': "Failed to create intermediate template $intermediateTemplate\nException: index out of range: 1\n",
            'error_invalid_template': "Failed to create $serverName server on $serverNode node\nException: ADMG0253E: Matching template incorrectTemplate could not be found or is not valid for this server.\n",
    ]

    @Shared
    def jobLogs = [
            'default_cynch':  ['success',"Application server $serverName has been created on node $serverNode","The following nodes have been synchronized: $serverNode"],
            'default_no_cynch':  ['success',"Application server $serverName has been created on node $serverNode"],
            'error_server_exist':['error', "Failed to create $serverName server on $serverNode node", "Exception: ADMG0248E: $serverName exists within node $serverNode"],
            'error_empty_node':  ['error',"Failed to create $serverName server on  node","Exception: ADMG0250E: Node websphere90ndCellManager01 is not a valid node."],
            'error_empty_s_name':  ['error',"Failed to create  server on $serverNode node","Exception: ADMF0002E: Required parameter name is not found for command createApplicationServer."],
            'error_empty_source_s': ['error',"Failed to create an application server.","Error: Source server name is required when source type is set to server"],
            'error_config':  ["Error: Configuration ('incorrectConfig'|'') doesn't exist"],
            'error_invalid_node':  ['error',"Failed to create $serverName server on $invalidNode node","Exception: ADMG0250E: Node $invalidNode is not a valid node."],
            'error_invalid_s_name':  ['error',"Failed to create $invalidServer server on $serverNode node","Exception: ADMG0249E: The server name $invalidServer is invalid."],
            'error_not_ex_node':  ['error',"Failed to create $serverName server on notExist node","Exception: ADMG0250E: Node notExist is not a valid node."],
            'error_not_ex_s_name':  ['error',"Failed to create intermediate template $intermediateTemplate","Exception: index out of range: 1"],
            'error_invalid_template': ['error',"Failed to create $serverName server on $serverNode node","Exception: ADMG0253E: Matching template incorrectTemplate could not be found or is not valid for this server."],
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
                                                            procName: procDeleteServer,
                                                            params  : [
                                                                    configname: '',
                                                                    wasAppServerName: '',
                                                                    wasNodeName: '',
                                                                    wasSyncNodes: '',
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
    def 'CreateApplicationServer - #testCaseID.id #testCaseID.description'() {
        if (deleteServer) {
            deleteAppServer(node,server)
        }

        given: "Parameters for procedure"
        def runParams = [
                configname: configName,
                wasAppServerName: server,
                wasGenUniquePorts: uniquePort,
                wasNodeName: node,
                wasSourceServerName: sourceServer,
                wasSourceType: sourceType,
                wasSyncNodes: syncNodes,
                wasTemplateLocation: '',
                wasTemplateName: sourceTemplate,
        ]

        when: "Run procedure and wait until job is completed"
        def result = runProcedure(runParams)

        then: "Get and compare results"
        def outcome = getJobProperty('/myJob/outcome', result.jobId)
        def jobSummary = getJobProperty("/myJob/jobSteps/$procName/summary", result.jobId)
        def debugLog = getJobLogs(result.jobId)
        assert outcome == expectedOutcome
        if (testCaseID.id == 'C363894 step 3'){
            assert jobSummary ==~ expectedSummary
        }else{
            assert jobSummary == expectedSummary
        }
        for (log in logs) {
            assert debugLog =~ log
        }

        where: 'The following params will be:'
        testCaseID          | configName        | server        | node        | uniquePort | syncNodes| sourceType | sourceTemplate     | sourceServer     | expectedSummary                 | logs                          | expectedOutcome | deleteServer
        testCases.C363899_1 | configname        | serverName    | serverNode  | '1'        | '1'      | 'template' | sourceTemplateName | ''               | summaries.default               | jobLogs.default_cynch         | 'success'       | null
        testCases.C363899_2 | configname        | serverName    | serverNode  | '1'        | '0'      | 'server'   | ''                 | sourceServerName | summaries.default               | jobLogs.default_no_cynch      | 'success'       | 1
        testCases.C363895   | configname        | serverName    | serverNode  | '1'        | '0'      | 'server'   | ''                 | sourceServerName | summaries.error_server_exist    | jobLogs.error_server_exist    | 'error'         | null
        testCases.C363901_1 | configname        | serverName    | serverNode  | '1'        | '0'      | 'template' | sourceTemplateName | sourceServerName | summaries.default               | jobLogs.default_no_cynch      | 'success'       | 1
        testCases.C363901_2 | configname        | serverName    | serverNode  | '1'        | '1'      | 'server'   | sourceTemplateName | sourceServerName | summaries.default               | jobLogs.default_cynch         | 'success'       | 1
        testCases.C363901_3 | configname        | serverName    | serverNode  | '1'        | '1'      | ''         | sourceTemplateName | sourceServerName | summaries.default               | jobLogs.default_cynch         | 'success'       | 1
        testCases.C363901_4 | configname        | serverName    | serverNode  | '1'        | '1'      | ''         | ''                 | sourceServerName | summaries.default               | jobLogs.default_cynch         | 'success'       | 1
        testCases.C363901_5 | configname        | serverName    | serverNode  | '1'        | '1'      | ''         | ''                 | ''               | summaries.default               | jobLogs.default_cynch         | 'success'       | 1
        testCases.C363901_6 | configname        | serverName    | serverNode  | '1'        | '1'      | 'template' | ''                 | sourceServerName | summaries.default               | jobLogs.default_cynch         | 'success'       | 1
        testCases.C363892_1 | ''                | serverName    | serverNode  | '1'        | '1'      | 'template' | sourceTemplateName | ''               | summaries.error_config_empty    | jobLogs.error_config          | 'error'         | 1
        testCases.C363892_2 | configname        | serverName    | ''          | '1'        | '1'      | 'server'   | ''                 | sourceServerName | summaries.error_empty_node      | jobLogs.error_empty_node      | 'error'         | null
        testCases.C363892_3 | configname        | ''            | serverNode  | '1'        | '1'      | 'template' | sourceTemplateName | ''               | summaries.error_empty_s_name    | jobLogs.error_empty_s_name    | 'error'         | null
        testCases.C363892_4 | configname        | serverName    | serverNode  | '1'        | '1'      | 'server'   | sourceTemplateName | ''               | summaries.error_empty_source_s  | jobLogs.error_empty_source_s  | 'error'         | null
        testCases.C363893   | 'incorrectConfig' | serverName    | serverNode  | '1'        | '1'      | 'template' | sourceTemplateName | ''               | summaries.error_config          | jobLogs.error_config          | 'error'         | null
        testCases.C363894_1 | configname        | serverName    | '!#*&Node1' | '1'        | '1'      | 'template' | sourceTemplateName | ''               | summaries.error_invalid_node    | jobLogs.error_invalid_node    | 'error'         | null
        testCases.C363894_2 | configname        | '!#*&server1' | serverNode  | '1'        | '1'      | 'server'   | ''                 | sourceServerName | summaries.error_invalid_s_name  | jobLogs.error_invalid_s_name  | 'error'         | null
        testCases.C363894_3 | configname        | serverName    | serverNode  | '1'        | '1'      | 'server'   | ''                 | 'notExist'       | summaries.error_not_ex_s_name   | jobLogs.error_not_ex_s_name   | 'error'         | null
        testCases.C363894_4 | configname        | serverName    | 'notExist'  | '1'        | '1'      | 'template' | sourceTemplateName | ''               | summaries.error_not_ex_node     | jobLogs.error_not_ex_node     | 'error'         | null
        testCases.C363894_5 | configname        | serverName    | serverNode  | '1'        | '1'      | 'template' | 'incorrectTemplate'| ''               | summaries.error_invalid_template| jobLogs.error_invalid_template| 'error'         | null
    }

    def deleteAppServer(node,server){
        def runParams = [
                configname: configname,
                wasAppServerName: server,
                wasNodeName: node,
                wasSyncNodes: '1',
        ]
        runProcedure(runParams, procDeleteServer)
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

