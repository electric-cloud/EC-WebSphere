package com.electriccloud.plugin.spec

import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import com.electriccloud.plugin.spec.PluginTestHelper
import groovy.json.JsonSlurper

@Requires({ System.getenv('IS_WAS_ND') == "1"})
class CreateFirstClusterMemberSpecSuite extends PluginTestHelper {

    // Environments Variables
    @Shared
    def wasUserName = System.getenv('WAS_USERNAME'),
        wasPassword = System.getenv('WAS_PASSWORD'),
        wasHost     = System.getenv('WAS_HOST'),
        wasPort     = System.getenv('WAS_PORT'),
        wasConnType = System.getenv('WAS_CONNTYPE'),
        wasDebug    = System.getenv('WAS_DEBUG'),
        wasPath     = System.getenv('WSADMIN_PATH'),
        wasAppPath  = System.getenv('WAS_APPPATH'),
        is_windows  = System.getenv("IS_WINDOWS")

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
    def creationPolicy = [
            existing: "existing",
            template: "template",
            convert: "convert",
            wrong: "wrong",
    ]

    @Shared
    def promotionPolicy = [
            both: 'both',
            server: 'server',
            cluster: 'cluster',
            wrong: "wrong",
    ]

    @Shared
    def servers = [
            'default': 'server1',
            'convert': 'convertServer',
            'wrong': 'wrong',
    ]

    @Shared
    def nodes = [
            'default': wasHost + 'Node01',
            'wrong': 'wrong',
    ]

    @Shared
    def TC = [
            C367242: [ ids: 'C367242, C367245, C367250, C367251', description: 'only required fields: creation policy : template'],
            C367243: [ ids: 'C367243', description: 'only required fields: creation policy : existing'],
            C367244: [ ids: 'C367244', description: 'clusterWeight'],
            C367246: [ ids: 'C367246', description: 'promotion Policy: server'],
            C367247: [ ids: 'C367247', description: 'promotion Policy: both'],
            C367248: [ ids: 'C367248', description: 'UniquePorts - 1'],
            C367249: [ ids: 'C367249', description: 'UniquePorts - 0'],
            C367252: [ ids: 'C367252', description: 'Sync Nodes? - false'],
            C367256: [ ids: 'C367256', description: 'cluster already has member'],
            C367257: [ ids: 'C367257', description: 'add server with the same name twice to the cluster'],
            C367264: [ ids: 'C367264', description: 'empty required parameters'],
            C367267: [ ids: 'C367267', description: 'wrong parameters'],

    ]

    @Shared
    def procCreateFirstClusterMember = 'CreateFirstClusterMember',
        procCreateCluster = 'CreateCluster',
        procDeleteCluster = 'DeleteCluster',
        procStartCluster = 'StartCluster',
        procStopCluster = 'StopCluster',
        procRunJob = 'RunCustomJob',
        projectName = "EC-WebSphere Specs $procCreateFirstClusterMember Project"

    def doSetupSpec() {
        def wasResourceName = wasHost
        createWorkspace(wasResourceName)
        createConfiguration(confignames.correctSOAP, [doNotRecreate: false])
        importProject(projectName, 'dsl/RunProcedure.dsl', [projName: projectName,
                                                           resName : wasResourceName,
                                                           procName: procCreateFirstClusterMember,
                                                           params  : [
                                                                   configname: '',
                                                                   wasClusterName: '',
                                                                   wasFirstClusterMemberCreationPolicy: '',
                                                                   wasFirstClusterMemberGenUniquePorts: '',
                                                                   wasFirstClusterMemberName: '',
                                                                   wasFirstClusterMemberNode: '',
                                                                   wasFirstClusterMemberTemplateName: '',
                                                                   wasFirstClusterMemberWeight: '',
                                                                   wasServerResourcesPromotionPolicy: '',
                                                                   wasSourceServerName: '',
                                                                   wasSyncNodes: '',
                                                           ]
        ])
        importProject(projectName, 'dsl/RunProcedure.dsl', [projName: projectName,
                                                            resName : wasResourceName,
                                                            procName: procDeleteCluster,
                                                            params  : [
                                                                    configname: '',
                                                                    wasClusterName: '',
                                                                    wasSyncNodes: '',
                                                            ]
        ])
        importProject(projectName, 'dsl/RunProcedure.dsl', [projName: projectName,
                                                            resName : wasResourceName,
                                                            procName: procCreateCluster,
                                                            params  : [
                                                                    configname: '',
                                                                    wasAddClusterMembers: '',
                                                                    wasClusterMembersGenUniquePorts: '',
                                                                    wasClusterMembersList: '',
                                                                    wasClusterMemberWeight: '',
                                                                    wasClusterName: '',
                                                                    wasCreateFirstClusterMember: '',
                                                                    wasFirstClusterMemberCreationPolicy: '',
                                                                    wasFirstClusterMemberGenUniquePorts:  '',
                                                                    wasFirstClusterMemberName: '',
                                                                    wasFirstClusterMemberNode: '',
                                                                    wasFirstClusterMemberTemplateName: '',
                                                                    wasFirstClusterMemberWeight: '',
                                                                    wasPreferLocal: '',
                                                                    wasServerResourcesPromotionPolicy: '',
                                                                    wasSourceServerName: '',
                                                                    wasSyncNodes: '',
                                                            ]
        ])

        importProject(projectName, 'dsl/RunProcedure.dsl', [projName: projectName,
                                                            resName : wasResourceName,
                                                            procName: procStartCluster,
                                                            params  : [
                                                                    configName: '',
                                                                    wasClusterName: '',
                                                                    wasTimeout: '',
                                                            ]
        ])

        importProject(projectName, 'dsl/RunProcedure.dsl', [projName: projectName,
                                                            resName : wasResourceName,
                                                            procName: procStopCluster,
                                                            params  : [
                                                                    configName: '',
                                                                    wasClusterName: '',
                                                                    wasTimeout: '',
                                                                    wasRippleStart: ''
                                                            ]
        ])

        importProject(projectName, 'dsl/RunProcedure.dsl', [projName: projectName,
                                                            resName : wasResourceName,
                                                            procName: procRunJob,
                                                            params  : [
                                                                    configname: '',
                                                                    scriptfile: '',
                                                                    scriptfilesource: '',
                                                            ]
        ])

        dsl 'setProperty(propertyName: "/plugins/EC-WebSphere/project/ec_debug_logToProperty", value: "/myJob/debug_logs")'
    }

    @Shared
    def summaries = [
            'template': "First cluster member SERVERNAME has been created on ${nodes.default} node and added to cluster CLUSTERNAME using default template\n",
            'existing': "First cluster member SERVERNAME has been created on ${nodes.default} node and added to cluster CLUSTERNAME using server ${nodes.default+":"+servers.default} as template\n",
            'errorSecondMember': "Failed to create a first cluster member.\n" +
                    "Exception: ADMG9222E: The firstMember command step and its parameters cannot be specified because one or more cluster members are already configured.\n",
            'errorAlreadyExists': "Failed to create a first cluster member.\n" +
                    "Exception: ADMG9249E: Exception caught validating the memberConfig step of the createClusterMember task command: com.ibm.websphere.management.cmdframework.CommandValidationException: ADMG9217E: Server SERVERNAME already exists on node ${nodes.default}.\n",
            'emptyConfig': "Configuration '' doesn't exist",
            'emptyClusterName': "Failed to create a first cluster member.\n" +
                    "Exception: 1\n" +
                    "Error: Missing clusterName parameter\n",
            'emptyTemplateName': "Failed to create a first cluster member.\n" +
                    "Exception: ADMG9223E: Cannot find server template .\n",
            'emptyCreationPolicy': "Failed to create a first cluster member.\n" +
                    "Exception: 1\n" +
                    "Error: Creation Policy should be existing or template\n",
            'emptyMemberName': "Failed to create a first cluster member.\n" +
                    "Exception: ADMF0002E: Required parameter memberName is not found for command memberConfig.\n",
            'emptyMemberNode': "Failed to create a first cluster member.\n" +
                    "Exception: ADMF0002E: Required parameter memberNode is not found for command memberConfig.\n",
            'emptySourceServerName': "Failed to create a first cluster member.\n" +
                    "Exception: 1\n" +
                    "Error: Source Node is mandatory\n",
            'wrongConfig': "Configuration '${confignames.incorrect}' doesn't exist",
            'wrongCluster': "Failed to create a first cluster member.\n" +
                    "Exception: ADMG9216E: Cannot find cluster wrong.\n",
            'wrongPolicy': "Failed to create a first cluster member.\n" +
                    "Exception: 1\n" +
                    "Error: Creation Policy should be existing or template\n",
            'wrongNode': "Failed to create a first cluster member.\n" +
                    "Exception: ADMG9249E: Exception caught validating the memberConfig step of the createClusterMember task command: com.ibm.websphere.management.cmdframework.CommandValidationException: ADMG9218E: Cannot find node wrong.\n",
            'wrongTemplate': "Failed to create a first cluster member.\n" +
                    "Exception: ADMG9223E: Cannot find server template wrong.\n",
            'wrongPromotionPolicy': "Failed to create a first cluster member.\n" +
                    "Exception: com.ibm.ws.scripting.ScriptingException: com.ibm.ws.management.commands.cluster.ClusterConfigException: resourcesScope parameter value: wrong is not valid\n",
            'wrongFormat': "Failed to create a first cluster member.\n" +
                    "Exception: 1\n" +
                    "Error: Expected nodename:servername, got wrong\n",
            'wrongSource': "Failed to create a first cluster member.\n" +
                    "Exception: ADMG9202E: Cannot find server wrong on node wrong.\n"
    ]

    @Shared
    def jobLogs = [
        weight: "'-memberWeight': '5'",
        resScopeBoth:    ["'-resourcesScope': 'both'"],
        resScopeServer:  ["'-resourcesScope': 'server'"],
        resScopeCluster: ["'-resourcesScope': 'cluster'"],
        syncNodes:       ["Synchronizing configuration repository with nodes now.", "The following nodes have been synchronized: ${nodes.default}"]
    ]

    def doCleanupSpec() {
    }

    @Unroll
    def "Create First Cluster Member - Positive #testCaseID.ids #testCaseID.description"() {
        def testNumber = specificationContext.currentIteration.parent.iterationNameProvider.iterationCount
        clusterName += testNumber
        firstMemberName += testNumber

        given: "Create an empty cluster"
        createCluster(clusterName)

        and: "Parameters for procedure"
        def runParams = [
                configname: conf,
                wasClusterName: clusterName,
                wasFirstClusterMemberCreationPolicy: firstMemberCreationPolicy,
                wasFirstClusterMemberGenUniquePorts: firstMemberGenUniquePorts,
                wasFirstClusterMemberName: firstMemberName,
                wasFirstClusterMemberNode: clusterMemberNode,
                wasFirstClusterMemberTemplateName: clusterMemberTemplateName,
                wasFirstClusterMemberWeight: firstMemberWeight,
                wasServerResourcesPromotionPolicy: serverResourcesPromotionPolicy,
                wasSourceServerName: sourceServerName,
                wasSyncNodes: syncNodes,
        ]

        when: "Run procedure and wait until job is completed"
        def result = runProcedure(runParams)

        then: "Get and compare results"
        def outcome = getJobProperty('/myJob/outcome', result.jobId)
        def jobSummary = getJobProperty("/myJob/jobSteps/$procCreateFirstClusterMember/summary", result.jobId)
        def debugLog = getJobLogs(result.jobId)

        def clusterInfo = getClusterBaseInfo()

        def portsOfFirstMember, portsOfDefaultServer, defaultPorts2
        if (testCaseID in [TC.C367248, TC.C367249]) {
            portsOfFirstMember = getServerPorts(firstMemberName)
            portsOfDefaultServer = getServerPorts(servers.default)
            defaultPorts2 = ["8879", "5061", "5060", "7276", "9443", "9352", "5578", "9043", "9402", "9100", "9809", "9403", "9632", "7286", "9080", "5558", "9401", "11005", "11006", "9060"]
        }

        verifyAll {
            outcome == status
            jobSummary == expectedSummary.
                    replace('CLUSTERNAME', clusterName).
                    replace('SERVERNAME', firstMemberName)
            for (log in logs){
                debugLog =~ log.
                        replace('CLUSTERNAME', clusterName).
                        replace('SERVERNAME', firstMemberName)
            }

            clusterInfo[clusterName].servers[0].memberName == firstMemberName

            if (firstMemberWeight.isEmpty()){
                clusterInfo[clusterName].servers[0].weight == '2'
            }
            else {
                clusterInfo[clusterName].servers[0].weight == firstMemberWeight
            }

            if (testCaseID in [TC.C367248]){
                portsOfFirstMember - portsOfDefaultServer != []
            }

            if (testCaseID in [TC.C367249]){
                (portsOfFirstMember - portsOfDefaultServer == []) || (portsOfFirstMember - defaultPorts2 == [])
            }

        }

        cleanup: "delete cluster"
        deleteServer(clusterName)

        where: 'The following params will be: '
        testCaseID | conf                    | clusterName          | firstMemberCreationPolicy | firstMemberGenUniquePorts | firstMemberName      | clusterMemberNode | clusterMemberTemplateName | firstMemberWeight | serverResourcesPromotionPolicy | sourceServerName                  | syncNodes | status    | expectedSummary      | logs
        TC.C367242 | confignames.correctSOAP | 'FirstMemberCluster' | creationPolicy.template   | '1'                       | 'FirstClusterServer' | nodes.default     | 'default'                 | ''                | promotionPolicy.cluster        | ''                                | '1'       | 'success' | summaries.template   | [summaries.template]+jobLogs.resScopeCluster+jobLogs.syncNodes
        TC.C367252 | confignames.correctSOAP | 'FirstMemberCluster' | creationPolicy.template   | '1'                       | 'FirstClusterServer' | nodes.default     | 'default'                 | ''                | promotionPolicy.cluster        | ''                                | '0'       | 'success' | summaries.template   | [summaries.template]
        TC.C367243 | confignames.correctSOAP | 'FirstMemberCluster' | creationPolicy.existing   | '1'                       | 'FirstClusterServer' | nodes.default     | ''                        | ''                | promotionPolicy.cluster        | nodes.default+":"+servers.default | '1'       | 'success' | summaries.existing   | [summaries.existing]
        TC.C367244 | confignames.correctSOAP | 'FirstMemberCluster' | creationPolicy.template   | '1'                       | 'FirstClusterServer' | nodes.default     | 'default'                 | '5'               | promotionPolicy.cluster        | ''                                | '1'       | 'success' | summaries.template   | [summaries.template]+jobLogs.weight
        TC.C367246 | confignames.correctSOAP | 'FirstMemberCluster' | creationPolicy.template   | '1'                       | 'FirstClusterServer' | nodes.default     | 'default'                 | ''                | promotionPolicy.both           | ''                                | '1'       | 'success' | summaries.template   | [summaries.template]+jobLogs.resScopeBoth
        TC.C367247 | confignames.correctSOAP | 'FirstMemberCluster' | creationPolicy.template   | '1'                       | 'FirstClusterServer' | nodes.default     | 'default'                 | ''                | promotionPolicy.server         | ''                                | '1'       | 'success' | summaries.template   | [summaries.template]+jobLogs.resScopeServer
        TC.C367248 | confignames.correctSOAP | 'FirstMemberCluster' | creationPolicy.template   | '1'                       | 'FirstClusterServer' | nodes.default     | 'default'                 | ''                | promotionPolicy.cluster        | ''                                | '1'       | 'success' | summaries.template   | [summaries.template]+jobLogs.resScopeCluster
        TC.C367249 | confignames.correctSOAP | 'FirstMemberCluster' | creationPolicy.template   | '0'                       | 'FirstClusterServer' | nodes.default     | 'default'                 | ''                | promotionPolicy.cluster        | ''                                | '1'       | 'success' | summaries.template   | [summaries.template]+jobLogs.resScopeCluster
    }

    @Unroll
    def "Create First Cluster Member - Negative, #testCaseID.ids #testCaseID.description"() {
        def testNumber = specificationContext.currentIteration.parent.iterationNameProvider.iterationCount

        given: "Create cluster"
        if ( testNumber == 1) {
            createCluster(clusterName)
        }
        and: "Parameters for procedure"
        def runParams = [
                configname: conf,
                wasClusterName: clusterName,
                wasFirstClusterMemberCreationPolicy: firstMemberCreationPolicy,
                wasFirstClusterMemberGenUniquePorts: firstMemberGenUniquePorts,
                wasFirstClusterMemberName: firstMemberName,
                wasFirstClusterMemberNode: clusterMemberNode,
                wasFirstClusterMemberTemplateName: clusterMemberTemplateName,
                wasFirstClusterMemberWeight: firstMemberWeight,
                wasServerResourcesPromotionPolicy: serverResourcesPromotionPolicy,
                wasSourceServerName: sourceServerName,
                wasSyncNodes: syncNodes,
        ]

        when: "Run procedure and wait until job is completed"
        def result = runProcedure(runParams)

        then: "Get and compare results"
        def outcome = getJobProperty('/myJob/outcome', result.jobId)
        def jobSummary = getJobProperty("/myJob/jobSteps/$procCreateFirstClusterMember/summary", result.jobId)
        def debugLog = getJobLogs(result.jobId)

        verifyAll {
            outcome == status
            jobSummary == expectedSummary.
                    replace('CLUSTERNAME', clusterName).
                    replace('SERVERNAME', firstMemberName)
            for (log in logs){
                debugLog =~ log.
                        replace('CLUSTERNAME', clusterName).
                        replace('SERVERNAME', firstMemberName)
            }

        }
        cleanup: "delete cluster"
        if (testNumber == 15){
            deleteServer(clusterName)
        }

        where: 'The following params will be: '
        testCaseID | conf                    | clusterName                  | firstMemberCreationPolicy | firstMemberGenUniquePorts | firstMemberName      | clusterMemberNode | clusterMemberTemplateName | firstMemberWeight | serverResourcesPromotionPolicy | sourceServerName                  | syncNodes | status    | expectedSummary                   | logs
        TC.C367264 | ''                      | 'FirstMemberClusterNegative' | creationPolicy.template   | '1'                       | 'FirstClusterServer' | nodes.default     | 'default'                 | ''                | promotionPolicy.cluster        | ''                                | '1'       | 'error'   | summaries.emptyConfig             | [summaries.emptyConfig]
        TC.C367264 | confignames.correctSOAP | ''                           | creationPolicy.template   | '1'                       | 'FirstClusterServer' | nodes.default     | 'default'                 | ''                | promotionPolicy.cluster        | ''                                | '1'       | 'error'   | summaries.emptyClusterName        | [summaries.emptyClusterName]
        TC.C367264 | confignames.correctSOAP | 'FirstMemberClusterNegative' | ''                        | '1'                       | 'FirstClusterServer' | nodes.default     | 'default'                 | ''                | promotionPolicy.cluster        | ''                                | '1'       | 'error'   | summaries.emptyCreationPolicy     | [summaries.emptyCreationPolicy]
        TC.C367264 | confignames.correctSOAP | 'FirstMemberClusterNegative' | creationPolicy.template   | '1'                       | ''                   | nodes.default     | 'default'                 | ''                | promotionPolicy.cluster        | ''                                | '1'       | 'error'   | summaries.emptyMemberName         | [summaries.emptyMemberName]
        TC.C367264 | confignames.correctSOAP | 'FirstMemberClusterNegative' | creationPolicy.template   | '1'                       | 'FirstClusterServer' | ''                | 'default'                 | ''                | promotionPolicy.cluster        | ''                                | '1'       | 'error'   | summaries.emptyMemberNode         | [summaries.emptyMemberNode]
        TC.C367264 | confignames.correctSOAP | 'FirstMemberClusterNegative' | creationPolicy.template   | '1'                       | 'FirstClusterServer' | nodes.default     | ''                        | ''                | promotionPolicy.cluster        | ''                                | '1'       | 'error'   | summaries.emptyTemplateName       | [summaries.emptyTemplateName]
        TC.C367264 | confignames.correctSOAP | 'FirstMemberClusterNegative' | creationPolicy.existing   | '1'                       | 'FirstClusterServer' | nodes.default     | ''                        | ''                | promotionPolicy.cluster        | ''                                | '1'       | 'error'   | summaries.emptySourceServerName   | [summaries.emptySourceServerName]

        TC.C367267 | confignames.incorrect   | 'FirstMemberClusterNegative' | creationPolicy.template   | '1'                       | 'FirstClusterServer' | nodes.default     | 'default'                 | ''                | promotionPolicy.cluster        | ''                                | '1'       | 'error'   | summaries.wrongConfig             | [summaries.wrongConfig]
        TC.C367267 | confignames.correctSOAP | 'wrong'                      | creationPolicy.template   | '1'                       | 'FirstClusterServer' | nodes.default     | 'default'                 | ''                | promotionPolicy.cluster        | ''                                | '1'       | 'error'   | summaries.wrongCluster            | [summaries.wrongCluster]
        TC.C367267 | confignames.correctSOAP | 'FirstMemberClusterNegative' | creationPolicy.wrong      | '1'                       | 'FirstClusterServer' | nodes.default     | 'default'                 | ''                | promotionPolicy.cluster        | ''                                | '1'       | 'error'   | summaries.wrongPolicy             | [summaries.wrongPolicy]
        TC.C367267 | confignames.correctSOAP | 'FirstMemberClusterNegative' | creationPolicy.template   | '1'                       | 'FirstClusterServer' | nodes.wrong       | 'default'                 | ''                | promotionPolicy.cluster        | ''                                | '1'       | 'error'   | summaries.wrongNode               | [summaries.wrongNode]
        TC.C367267 | confignames.correctSOAP | 'FirstMemberClusterNegative' | creationPolicy.template   | '1'                       | 'FirstClusterServer' | nodes.default     | 'wrong'                   | ''                | promotionPolicy.cluster        | ''                                | '1'       | 'error'   | summaries.wrongTemplate           | [summaries.wrongTemplate]
        TC.C367267 | confignames.correctSOAP | 'FirstMemberClusterNegative' | creationPolicy.template   | '1'                       | 'FirstClusterServer' | nodes.default     | 'default'                 | ''                | promotionPolicy.wrong          | ''                                | '1'       | 'error'   | summaries.wrongPromotionPolicy    | [summaries.wrongPromotionPolicy]
        TC.C367267 | confignames.correctSOAP | 'FirstMemberClusterNegative' | creationPolicy.existing   | '1'                       | 'FirstClusterServer' | nodes.default     | 'default'                 | ''                | promotionPolicy.cluster        | 'wrong'                           | '1'       | 'error'   | summaries.wrongFormat             | [summaries.wrongFormat]
        TC.C367267 | confignames.correctSOAP | 'FirstMemberClusterNegative' | creationPolicy.existing   | '1'                       | 'FirstClusterServer' | nodes.default     | 'default'                 | ''                | promotionPolicy.cluster        | 'wrong:wrong'                     | '1'       | 'error'   | summaries.wrongSource             | [summaries.wrongSource]

    }

    @Unroll
    def "Create First Cluster Member - Negative, existing, #testCaseID.ids #testCaseID.description"() {
        def testNumber = specificationContext.currentIteration.parent.iterationNameProvider.iterationCount
        clusterName += testNumber
        firstMemberName += testNumber

        given: "Create an empty cluster"
        createCluster(clusterName)

        and: "Parameters for procedure"
        def runParams = [
                configname: conf,
                wasClusterName: clusterName,
                wasFirstClusterMemberCreationPolicy: firstMemberCreationPolicy,
                wasFirstClusterMemberGenUniquePorts: firstMemberGenUniquePorts,
                wasFirstClusterMemberName: firstMemberName,
                wasFirstClusterMemberNode: clusterMemberNode,
                wasFirstClusterMemberTemplateName: clusterMemberTemplateName,
                wasFirstClusterMemberWeight: firstMemberWeight,
                wasServerResourcesPromotionPolicy: serverResourcesPromotionPolicy,
                wasSourceServerName: sourceServerName,
                wasSyncNodes: syncNodes,
        ]

        when: "Run procedure and wait until job is completed"
        runProcedure(runParams)
        if (testCaseID == TC.C367256) {
            runParams.wasFirstClusterMemberName += 'new'
        }
        def result = runProcedure(runParams)

        then: "Get and compare results"
        def outcome = getJobProperty('/myJob/outcome', result.jobId)
        def jobSummary = getJobProperty("/myJob/jobSteps/$procCreateFirstClusterMember/summary", result.jobId)
        def debugLog = getJobLogs(result.jobId)

        verifyAll {
            outcome == status
            jobSummary == expectedSummary.
                    replace('CLUSTERNAME', clusterName).
                    replace('SERVERNAME', firstMemberName)
            for (log in logs){
                debugLog =~ log.
                        replace('CLUSTERNAME', clusterName).
                        replace('SERVERNAME', firstMemberName)
            }

        }

        cleanup: "delete cluster"
        deleteServer(clusterName)

        where: 'The following params will be: '
        testCaseID | conf                    | clusterName                  | firstMemberCreationPolicy | firstMemberGenUniquePorts | firstMemberName      | clusterMemberNode | clusterMemberTemplateName | firstMemberWeight | serverResourcesPromotionPolicy | sourceServerName                  | syncNodes | status    | expectedSummary              | logs
        TC.C367256 | confignames.correctSOAP | 'FirstMemberClusterNegative' | creationPolicy.template   | '1'                       | 'FirstClusterServer' | nodes.default     | 'default'                 | ''                | promotionPolicy.cluster        | ''                                | '1'       | 'error'   | summaries.errorSecondMember  | [summaries.errorSecondMember]
        TC.C367257 | confignames.correctSOAP | 'FirstMemberClusterNegative' | creationPolicy.template   | '1'                       | 'FirstClusterServer' | nodes.default     | 'default'                 | ''                | promotionPolicy.cluster        | ''                                | '1'       | 'error'   | summaries.errorAlreadyExists | [summaries.errorAlreadyExists]
    }

    def getServerPorts(def serverName){
        // procedure return array of server ports
        def jythonScript = "print \\'STARTLINE\\'; print AdminTask.listServerPorts(\\'${serverName}\\', \\'[-nodeName ${nodes.default}]\\')"

        def scriptParams = [
                configname: confignames.correctSOAP,
                scriptfile: jythonScript,
                scriptfilesource: 'newscriptfile',
        ]
        def scriptResult = runProcedure(scriptParams, procRunJob)
        def scriptLog = getJobLogs(scriptResult.jobId)
        def serversInfo = scriptLog.split("STARTLINE\n")[1].split("\n")
        def ports = []
        for (server in serversInfo){
            ports.add(server.split("port ")[1].split("]")[0])
        }
        return ports
    }


    def getClusterBaseInfo(){
// Example of jython script output
// {'testCluster2':
//     {'preferLocal': 'true', 'servers':
//         [{'nodeName': 'websphere90ndNode01', 'cluster': 'testCluster2(cells/websphere90ndCell01/clusters/testCluster2|cluster.xml#ServerCluster_1539612766814)', 'memberName': 'clusterServer2', 'weight': '2', 'uniqueId': '1539612768940'},
//         {'nodeName': 'websphere90ndNode01', 'cluster': 'testCluster2(cells/websphere90ndCell01/clusters/testCluster2|cluster.xml#ServerCluster_1539612766814)', 'memberName': 'serverC366970', 'weight': '2', 'uniqueId': '1539612769337'}],
//     'name': 'testCluster2'}}

        def jythonScrpit = '''\'\'
clusterList = AdminClusterManagement.listClusters()
clustersInfo = {}
for cluster in clusterList:
    clusterInfo = {}
    clusterInfo["preferLocal"] = AdminConfig.showAttribute(cluster, "preferLocal").encode("ascii")
    clusterName = AdminConfig.showAttribute(cluster, "name").encode("ascii")
    clusterInfo["name"] = clusterName
    clustersInfo[clusterInfo["name"]] = clusterInfo
    servers = []
    for server in AdminClusterManagement.listClusterMembers(clusterName):
        info = AdminConfig.show(server)
        tmp = [x.encode("ascii").replace("\\\\r", "") for x in info.split("\\\\n")]
        server_dict = dict((x[1:-1].split(" ")[0], x[1:-1].split(" ")[1],) for x in tmp)
        servers.append(server_dict)
    clusterInfo["servers"] = servers
print clustersInfo\'\''''

        def scriptParams = [
                configname: confignames.correctSOAP,
                scriptfile: jythonScrpit,
                scriptfilesource: 'newscriptfile',
        ]
        def scriptResult = runProcedure(scriptParams, procRunJob)
        def scriptLog = getJobLogs(scriptResult.jobId)
        def clusterInfo = new JsonSlurper().parseText(scriptLog.split("\n")[-1].replace("'", '"'))
        return clusterInfo
    }

    def startCluster(def clusterName) {
        def runParams = [
                configName: confignames.correctSOAP,
                wasClusterName: clusterName,
                wasTimeout: '120',
        ]
        def result = runProcedure(runParams, procStartCluster)
        return getJobProperty('/myJob/outcome', result.jobId)
    }

    def stopCluster(def clusterName){
        def runParams = [
                configName: confignames.correctSOAP,
                wasClusterName: clusterName,
                wasTimeout: '60',
                wasRippleStart: '0'
        ]
        runProcedure(runParams, procStopCluster)
    }

    def deleteServer(def clusterName){
        def deleteParams = [
                configname: confignames.correctSOAP,
                wasClusterName: clusterName,
                wasSyncNodes: 1,
        ]
        runProcedure(deleteParams, procDeleteCluster)
    }

    def createCluster(clusterName){
        def runParams = [
                configname                         : confignames.correctSOAP,
                wasAddClusterMembers               : '0',
                wasClusterMembersGenUniquePorts    : '1',
                wasClusterMembersList              : '',
                wasClusterMemberWeight             : '',
                wasClusterName                     : clusterName,
                wasCreateFirstClusterMember        : '0',
                wasFirstClusterMemberCreationPolicy: '',
                wasFirstClusterMemberGenUniquePorts: '1',
                wasFirstClusterMemberName          : '',
                wasFirstClusterMemberNode          : nodes.default,
                wasFirstClusterMemberTemplateName  : '',
                wasFirstClusterMemberWeight        : '',
                wasPreferLocal                     : '1',
                wasServerResourcesPromotionPolicy  : '',
                wasSourceServerName                : '',
                wasSyncNodes                       : '1',
        ]
        runProcedure(runParams, procCreateCluster)

    }

    def runProcedure(def parameters, def procedureName=procCreateFirstClusterMember) {
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