package com.electriccloud.plugin.spec

import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import com.electriccloud.plugin.spec.PluginTestHelper
import groovy.json.JsonSlurper


@Requires({ System.getenv('IS_WAS_ND') == "1"})
class CreateClusterSpecSuite extends PluginTestHelper {

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
            convert: "convert"
    ]

    @Shared
    def promotionPolicy = [
            both: 'both',
            server: 'server',
            cluster: 'cluster',
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
    def membersLists = [
            'oneMember1':   nodes.default+":"+'serverC366970',
            'someMembers1': nodes.default+":"+'serverC3669711'+','+nodes.default+":"+'serverC3669712',
    ]

    @Shared
    def TC = [
            C366925: [ ids: 'C366925, C366934, C366979', description: 'create empty cluster'],
            C366932: [ ids: 'C366932', description: 'prefer local - false '],
            C366946: [ ids: 'C366946', description: 'creationPolicy: existing, create cluster with server, source: existing server'],
            C366947: [ ids: 'C366947', description: 'creationPolicy: template, create cluster with server, source: default template'],
            C366949: [ ids: 'C366949', description: 'creationPolicy: convert, create cluster with server, source: add existing server'],
            C366955: [ ids: 'C366955', description: 'First Cluster Member Weight. creationPolicy: existing'],
            C366956: [ ids: 'C366956', description: 'UniquePorts - true'],
            C366957: [ ids: 'C366957', description: 'UniquePorts - false'],
            C366965: [ ids: 'C366965', description: 'Server Resources Promotion Policy: both; source: existing server'],
            C366966: [ ids: 'C366966', description: 'Server Resources Promotion Policy: cluster; source: existing server'],
            C366967: [ ids: 'C366967', description: 'Server Resources Promotion Policy: server; source: existing server'],
            C366970: [ ids: 'C366970', description: 'add cluster member '],
            C366973: [ ids: 'C366973', description: 'add cluster members'],
            C366974: [ ids: 'C366974', description: 'add cluster member'],
            C366975: [ ids: 'C366975', description: 'add cluster members'],
            C366976: [ ids: 'C366976', description: 'add cluster member, wasClusterMembersGenUniquePorts - 1'],
            C366977: [ ids: 'C366977', description: 'add cluster member, wasClusterMembersGenUniquePorts - 0'],
            C366978: [ ids: 'C366978', description: 'add cluster member, wasClusterMemberWeight'],
            C366980: [ ids: 'C366980', description: 'sync nodes - 0'],
    ]

    @Shared
    def procName = 'CreateCluster',
        procDeleteCluster = 'DeleteCluster',
        procCreateServer = 'CreateApplicationServer',
        procStartCluster = 'StartCluster',
        procStopCluster = 'StopCluster',
        procRunJob = 'RunCustomJob',
        projectName = "EC-WebSphere Specs $procName Project"


    def doSetupSpec() {
        def wasResourceName = wasHost
        createWorkspace(wasResourceName)
        createConfiguration(confignames.correctSOAP, [doNotRecreate: false])
        importProject(projectName, 'dsl/RunProcedure.dsl', [projName: projectName,
                                                            resName : wasResourceName,
                                                            procName: procName,
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
                                                            procName: procDeleteCluster,
                                                            params  : [
                                                                    configname: '',
                                                                    wasClusterName: '',
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
            'default': "Cluster CLUSTERNAME has been created\n",
            'serverSource': "First cluster member SERVERNAME1 has been created on node NODENAME using server SERVERNAME2 on node NODENAME as source\n",
            'templateSource': "First cluster member SERVERNAME1 has been created on node NODENAME from template default\n",
            'convertSource': "Server convertServer on node NODENAME has been converted to be the first member of cluster CLUSTERNAME\n",
            'addMember': "Server serverC366970 on node NODENAME has been created and added as cluster member\n",
            'addMembers': "Server serverC3669712 on node NODENAME has been created and added as cluster member\n" +
                    "Server serverC3669711 on node NODENAME has been created and added as cluster member\n"
    ]

    @Shared
    def jobLogs = [
            serverSource:    ['Creating first member from template or existing server', "'-templateServerName': 'server1'"],
            templateSource:  ["Creating first member from template or existing server", "'-templateName': 'default'"],
            convertSource:   ["Creating first member from template or existing server", "-convertServer", "-serverName 'convertServer'"],
            weight:          ["'-memberWeight': '5'"],
            memberWeight:    ["'-memberWeight': '6'"],
            uniquePorts:     ["'-genUniquePorts': 'true'"],
            nonUniquePorts:  ["'-genUniquePorts': 'false'"],
            resScopeBoth:    ["'-resourcesScope': 'both'"],
            resScopeServer:  ["'-resourcesScope': 'server'"],
            resScopeCluster: ["'-resourcesScope': 'cluster'"],
            syncNodes:       ["Synchronizing configuration repository with nodes now.", "The following nodes have been synchronized: NODENAME"]
    ]

    def doCleanupSpec() {
    }

    @Unroll
    def "Create Cluster - Positive #testCaseID.ids #testCaseID.description"(){
        def testNumber = specificationContext.currentIteration.parent.iterationNameProvider.iterationCount
        clusterName += testNumber
        if (!firstMemberName.isEmpty()){
            firstMemberName += testNumber
        }

        given: "Create a server , if it's needed"
        if (firstMemberCreationPolicy == creationPolicy.convert) {
            createServer(servers.convert)
        }
        and: "Parameters for procedure"
        def runParams = [
                configname: conf,
                wasAddClusterMembers: addMembers,
                wasClusterMembersGenUniquePorts: membersUniquePorts,
                wasClusterMembersList: membersList,
                wasClusterMemberWeight: memberWeight,
                wasClusterName: clusterName,
                wasCreateFirstClusterMember: createFirstMember,
                wasFirstClusterMemberCreationPolicy: firstMemberCreationPolicy,
                wasFirstClusterMemberGenUniquePorts:  firstMemberGenUniquePorts,
                wasFirstClusterMemberName: firstMemberName,
                wasFirstClusterMemberNode: clusterMemberNode,
                wasFirstClusterMemberTemplateName: clusterMemberTemplateName,
                wasFirstClusterMemberWeight: firstMemberWeight,
                wasPreferLocal: preferLocal,
                wasServerResourcesPromotionPolicy: serverResourcesPromotionPolicy,
                wasSourceServerName: sourceServerName,
                wasSyncNodes: syncNodes,
        ]

        when: "Run procedure and wait until job is completed"
        def result = runProcedure(runParams)

        then: "Get and compare results"
        def outcome = getJobProperty('/myJob/outcome', result.jobId)
        def jobSummary = getJobProperty("/myJob/jobSteps/$procName/summary", result.jobId)
        def debugLog = getJobLogs(result.jobId)

        def clusterInfo = getClusterBaseInfo()
        verifyAll {
            assert outcome == status
            assert jobSummary == expectedSummary.
                    replace('CLUSTERNAME', clusterName).
                    replace('SERVERNAME1', firstMemberName).
                    replace('SERVERNAME2', sourceServerName.isEmpty() ? "" : sourceServerName.split(":")[1]).
                    replace('NODENAME', clusterMemberNode.isEmpty() ? nodes.default : clusterMemberNode)
            for (log in logs){
                assert debugLog =~ log.
                        replace('CLUSTERNAME', clusterName).
                        replace('SERVERNAME1', firstMemberName).
                        replace('SERVERNAME2', sourceServerName.isEmpty() ? "" : sourceServerName.split(":")[1]).
                        replace('NODENAME', clusterMemberNode.isEmpty() ? nodes.default : clusterMemberNode)
            }
            assert clusterInfo[clusterName].name == clusterName
            assert clusterInfo[clusterName].preferLocal == (preferLocal == '1') ? 'true' : 'false'
            if (!firstMemberName.isEmpty()){
                assert clusterInfo[clusterName].servers.any {(it.memberName == firstMemberName)}
            }
            if (!memberWeight.isEmpty()){
                assert clusterInfo[clusterName].servers.any {(it.weight == memberWeight)}
            }
            if (!membersList.isEmpty()){
                for (server in membersList.split(",").collect { it.split(":")[1]}){
                    assert clusterInfo[clusterName].servers.any {(it.memberName == server)}
                }
            }

            // start first cluster member, if it's ports are unitue
            // it will start
            if (testCaseID.ids in ['C366956', 'C366957']) {
                def resultOfStartCluster = startCluster(clusterName)
                assert resultOfStartCluster == ((testCaseID.ids == 'C366956') ? 'success' : 'error')
            }

            if (testCaseID.ids in ['C366976', 'C366977']) {
                def resultOfStartCluster = startCluster(clusterName)
                assert resultOfStartCluster == ((testCaseID.ids == 'C366976') ? 'success' : 'error')
            }            


        }

        cleanup: "delete cluster"
        if (testCaseID.ids in ['C366956', 'C366976']){
            stopCluster(clusterName)
        }
        deleteServer(clusterName)

        where: 'The following params will be: '
        testCaseID | conf                    | addMembers | membersUniquePorts | membersList               | memberWeight | clusterName   | createFirstMember | firstMemberCreationPolicy | firstMemberGenUniquePorts | firstMemberName | clusterMemberNode | clusterMemberTemplateName | firstMemberWeight | preferLocal  | serverResourcesPromotionPolicy | sourceServerName                   | syncNodes | status    | expectedSummary                                                  | logs
        TC.C366925 | confignames.correctSOAP | '0'        | '1'                | ''                        | ''           | 'testCluster' | '0'               | ''                        | '1'                       | ''              | ''                | ''                        | ''                | '1'          | ''                             | ''                                 | '1'       | "success" | summaries.default                                                | [summaries.default]+jobLogs.syncNodes
        TC.C366980 | confignames.correctSOAP | '0'        | '1'                | ''                        | ''           | 'testCluster' | '0'               | ''                        | '1'                       | ''              | ''                | ''                        | ''                | '1'          | ''                             | ''                                 | '0'       | "success" | summaries.default                                                | [summaries.default]
        TC.C366932 | confignames.correctSOAP | '0'        | '1'                | ''                        | ''           | 'testCluster' | '0'               | ''                        | '1'                       | ''              | ''                | ''                        | ''                | '0'          | ''                             | ''                                 | '1'       | "success" | summaries.default                                                | [summaries.default]
        TC.C366946 | confignames.correctSOAP | '0'        | '1'                | ''                        | ''           | 'testCluster' | '1'               | creationPolicy.existing   | '1'                       | 'clusterServer' | nodes.default     | ''                        | ''                | '1'          | ''                             | nodes.default+":"+servers.default  | '1'       | "success" | summaries.default+summaries.serverSource                         | [summaries.default]+[summaries.serverSource]+jobLogs.serverSource
        TC.C366947 | confignames.correctSOAP | '0'        | '1'                | ''                        | ''           | 'testCluster' | '1'               | creationPolicy.template   | '1'                       | 'clusterServer' | nodes.default     | 'default'                 | ''                | '1'          | ''                             | ''                                 | '1'       | "success" | summaries.default+summaries.templateSource                       | [summaries.default]+[summaries.templateSource]+jobLogs.templateSource
        TC.C366949 | confignames.correctSOAP | '0'        | '1'                | ''                        | ''           | 'testCluster' | '1'               | creationPolicy.convert    | '1'                       | ''              | ''                | ''                        | ''                | '1'          | ''                             | nodes.default+":"+servers.convert  | '1'       | "success" | summaries.default+summaries.convertSource                        | [summaries.default]+[summaries.convertSource]+jobLogs.convertSource
        TC.C366955 | confignames.correctSOAP | '0'        | '1'                | ''                        | '5'          | 'testCluster' | '1'               | creationPolicy.existing   | '1'                       | 'clusterServer' | nodes.default     | ''                        | '5'               | '1'          | ''                             | nodes.default+":"+servers.default  | '1'       | "success" | summaries.default+summaries.serverSource                         | [summaries.default]+[summaries.serverSource]+jobLogs.serverSource+jobLogs.weight
        TC.C366956 | confignames.correctSOAP | '0'        | '1'                | ''                        | ''           | 'testCluster' | '1'               | creationPolicy.existing   | '1'                       | 'clusterServer' | nodes.default     | ''                        | ''                | '1'          | ''                             | nodes.default+":"+servers.default  | '1'       | "success" | summaries.default+summaries.serverSource                         | [summaries.default]+[summaries.serverSource]+jobLogs.serverSource+jobLogs.uniquePorts
        TC.C366957 | confignames.correctSOAP | '0'        | '1'                | ''                        | ''           | 'testCluster' | '1'               | creationPolicy.existing   | '0'                       | 'clusterServer' | nodes.default     | ''                        | ''                | '1'          | ''                             | nodes.default+":"+servers.default  | '1'       | "success" | summaries.default+summaries.serverSource                         | [summaries.default]+[summaries.serverSource]+jobLogs.serverSource+jobLogs.nonUniquePorts
        TC.C366965 | confignames.correctSOAP | '0'        | '1'                | ''                        | ''           | 'testCluster' | '1'               | creationPolicy.template   | '1'                       | 'clusterServer' | nodes.default     | 'default'                 | ''                | '1'          | promotionPolicy.both           | ''                                 | '1'       | "success" | summaries.default+summaries.templateSource                       | [summaries.default]+[summaries.templateSource]+jobLogs.templateSource+jobLogs.resScopeBoth
        TC.C366966 | confignames.correctSOAP | '0'        | '1'                | ''                        | ''           | 'testCluster' | '1'               | creationPolicy.template   | '1'                       | 'clusterServer' | nodes.default     | 'default'                 | ''                | '1'          | promotionPolicy.cluster        | ''                                 | '1'       | "success" | summaries.default+summaries.templateSource                       | [summaries.default]+[summaries.templateSource]+jobLogs.templateSource+jobLogs.resScopeCluster
        TC.C366967 | confignames.correctSOAP | '0'        | '1'                | ''                        | ''           | 'testCluster' | '1'               | creationPolicy.template   | '1'                       | 'clusterServer' | nodes.default     | 'default'                 | ''                | '1'          | promotionPolicy.server         | ''                                 | '1'       | "success" | summaries.default+summaries.templateSource                       | [summaries.default]+[summaries.templateSource]+jobLogs.templateSource+jobLogs.resScopeServer
        TC.C366970 | confignames.correctSOAP | '1'        | '1'                | membersLists.oneMember1   | ''           | 'testCluster' | '0'               | ''                        | '1'                       | ''              | ''                | ''                        | ''                | '1'          | ''                             | ''                                 | '1'       | "success" | summaries.default+summaries.addMember                            | [summaries.default]
        TC.C366973 | confignames.correctSOAP | '1'        | '1'                | membersLists.someMembers1 | ''           | 'testCluster' | '0'               | ''                        | '1'                       | ''              | ''                | ''                        | ''                | '1'          | ''                             | ''                                 | '1'       | "success" | summaries.default+summaries.addMembers                           | [summaries.default]
        TC.C366974 | confignames.correctSOAP | '1'        | '1'                | membersLists.oneMember1   | ''           | 'testCluster' | '1'               | creationPolicy.template   | '1'                       | 'clusterServer' | nodes.default     | 'default'                 | ''                | '1'          | promotionPolicy.both           | ''                                 | '1'       | "success" | summaries.default+summaries.templateSource+summaries.addMember   | [summaries.default]
        TC.C366975 | confignames.correctSOAP | '1'        | '1'                | membersLists.someMembers1 | ''           | 'testCluster' | '1'               | creationPolicy.template   | '1'                       | 'clusterServer' | nodes.default     | 'default'                 | ''                | '1'          | promotionPolicy.both           | ''                                 | '1'       | "success" | summaries.default+summaries.templateSource+summaries.addMembers  | [summaries.default]
        TC.C366976 | confignames.correctSOAP | '1'        | '1'                | membersLists.oneMember1   | ''           | 'testCluster' | '1'               | creationPolicy.template   | '1'                       | 'clusterServer' | nodes.default     | 'default'                 | ''                | '1'          | promotionPolicy.both           | ''                                 | '1'       | "success" | summaries.default+summaries.templateSource+summaries.addMember   | [summaries.default]
        TC.C366977 | confignames.correctSOAP | '1'        | '0'                | membersLists.oneMember1   | ''           | 'testCluster' | '1'               | creationPolicy.template   | '1'                       | 'clusterServer' | nodes.default     | 'default'                 | ''                | '1'          | promotionPolicy.both           | ''                                 | '1'       | "success" | summaries.default+summaries.templateSource+summaries.addMember   | [summaries.default]
        TC.C366978 | confignames.correctSOAP | '1'        | '1'                | membersLists.oneMember1   | '6'          | 'testCluster' | '1'               | creationPolicy.template   | '1'                       | 'clusterServer' | nodes.default     | 'default'                 | ''                | '1'          | promotionPolicy.both           | ''                                 | '1'       | "success" | summaries.default+summaries.templateSource+summaries.addMember   | [summaries.default]+jobLogs.memberWeight
    }

    def createServer(def serverName){
        def createParams = [
                configname: confignames.correctSOAP,
                wasAppServerName: serverName,
                wasGenUniquePorts: 1,
                wasNodeName: nodes.default,
                wasSourceServerName: '',
                wasSourceType: 'template',
                wasSyncNodes: '1',
                wasTemplateLocation: '',
                wasTemplateName: 'default',
        ]
        runProcedure(createParams, procCreateServer)
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
        tmp = [x.encode("ascii") for x in info.split("\\\\n")]
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