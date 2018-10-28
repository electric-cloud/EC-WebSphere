package com.electriccloud.plugin.spec

import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import com.electriccloud.plugin.spec.PluginTestHelper
import groovy.json.JsonSlurper

@Requires({ System.getenv('IS_WAS_ND') == "1"})
class RemoveClusterMembersSpecSuite extends PluginTestHelper {

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
    def procRemoveClusterMembers = 'RemoveClusterMembers',
        procCreateCluster = 'CreateCluster',
        procCreateClusterMembers = 'CreateClusterMembers',
        procDeleteCluster = 'DeleteCluster',
        procStartCluster = 'StartCluster',
        procStopCluster = 'StopCluster',
        procRunJob = 'RunCustomJob',
        projectName = "EC-WebSphere Specs $procRemoveClusterMembers Project"

    @Shared
    def TC = [
            C367453: [ ids: 'C367453, C367457', description: 'Delete some members from cluster'],
            C367454: [ ids: 'C367454', description: 'Delete one member from cluster'],
            C367455: [ ids: 'C367455', description: 'Delete first member from cluster, left cluster empty'],
            C367457: [ ids: 'C367457', description: 'Delete first member from cluster, left cluster empty'],
            C367458: [ ids: 'C367458', description: 'Delete first member from cluster, left cluster empty'],
            C367459: [ ids: 'C367459', description: "delete extra servers - one server exists, another server doesn't"],
            C367462: [ ids: 'C367462', description: "empty required fields"],
            C367463: [ ids: 'C367463', description: "wrong values"],
    ]

    @Shared
    def summaries = [
            deleteTwoClusters: "Cluster member serverClusterMember02 on node ${nodes.default} has been removed from cluster CLUSTERNAME and deleted\n" +
                    "Cluster member serverClusterMember01 on node ${nodes.default} has been removed from cluster CLUSTERNAME and deleted\n",
            deleteOneCluster:  "Cluster member serverClusterMember1 on node ${nodes.default} has been removed from cluster CLUSTERNAME and deleted\n",
            deleteFirstCluster: "Cluster member FirstClusterServer on node ${nodes.default} has been removed from cluster CLUSTERNAME and deleted\n",
            deleteOneExtraMember: "Cluster member serverClusterMember1 on node websphere90ndNode01 has been removed from cluster RemoveMembers1 and deleted\n" +
                    "Exception: WASL6040E: The serverMember:serverClusterMember2 specified argument does not exist.\n",
            deleteFromEmptyCluster: "",
            emptyConfig: "Configuration '' doesn't exist",
            emptyClustername: "Failed to remove cluster members.\n" +
                        "Error: Cluster  does not exist\n",
            emptyList: "Failed to remove cluster members.\n" +
                    "Error: Expected nodename:servername record, got \n",
            wrongConfig: "Configuration 'incorrect' doesn't exist",
            wrongClustername: "Failed to remove cluster members.\n" +
                    "Error: Cluster wrongCluster does not exist\n",
            wrongList: "Failed to remove cluster members.\n" +
                    "Error: Expected nodename:servername record, got wrongFormat\n",
    ]

    @Shared
    def jobLogs = [
            deleteTwoClusters: ["Cluster member serverClusterMember02 on node ${nodes.default} has been removed from cluster CLUSTERNAME and deleted",
                                "Cluster member serverClusterMember01 on node ${nodes.default} has been removed from cluster CLUSTERNAME and deleted"],
            deleteOneCluster:  ["Cluster member serverClusterMember1 on node ${nodes.default} has been removed from cluster CLUSTERNAME and deleted"],
            deleteFirstCluster: ["Cluster member FirstClusterServer on node ${nodes.default} has been removed from cluster CLUSTERNAME and deleted"],
            syncNodes: ["Synchronizing configuration repository with nodes now.", "The following nodes have been synchronized: ${nodes.default}"],
            deleteOneExtraMember: ["Cluster member serverClusterMember1 on node ${nodes.default} has been removed from cluster CLUSTERNAME and deleted",
                    "Exception: WASL6040E: The serverMember:serverClusterMember2 specified argument does not exist."],
            deleteFromEmptyCluster: [""],
            emptyClustername: ["WASL6041E: The following argument value is not valid: clusterName:"],
    ]

    @Shared
    def membersLists = [
            'oneMember':   nodes.default+":"+'serverClusterMember1',
            'oneMember2':  nodes.default+":"+'serverClusterMember2',
            'oneExtraMember':   nodes.default+":"+'serverClusterMember1'+','+nodes.default+":"+'serverClusterMember2',
            'twoMembers':  nodes.default+":"+'serverClusterMember01'+','+nodes.default+":"+'serverClusterMember02',
            'firstMember': nodes.default+":"+'FirstClusterServer',
    ]

    def doSetupSpec() {
        def wasResourceName = wasHost
        createWorkspace(wasResourceName)
        createConfiguration(confignames.correctSOAP, [doNotRecreate: false])

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
                                                            procName: procCreateClusterMembers,
                                                            params  : [
                                                                    configname : '',
                                                                    wasClusterMembersGenUniquePorts : '',
                                                                    wasClusterMembersList : '',
                                                                    wasClusterMemberWeight : '',
                                                                    wasClusterName : '',
                                                                    wasSyncNodes : '',
                                                            ]
        ])

        importProject(projectName, 'dsl/RunProcedure.dsl', [projName: projectName,
                                                            resName : wasResourceName,
                                                            procName: procRemoveClusterMembers,
                                                            params  : [
                                                                    configName : '',
                                                                    wasClusterMembers : '',
                                                                    wasClusterName : '',
                                                                    wasSyncNodes : '',
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
                                                            procName: procRunJob,
                                                            params  : [
                                                                    configname: '',
                                                                    scriptfile: '',
                                                                    scriptfilesource: '',
                                                            ]
        ])

        dsl 'setProperty(propertyName: "/plugins/EC-WebSphere/project/ec_debug_logToProperty", value: "/myJob/debug_logs")'
    }

    def doCleanupSpec() {
    }

    @Unroll
    def "RemoveClusterMembers  - Positive #testCaseID.ids #testCaseID.description"() {
        def testNumber = specificationContext.currentIteration.parent.iterationNameProvider.iterationCount
        clusterName += testNumber

        given: "Create cluster with server"
        if (testCaseID == TC.C367455){
            createCluster(clusterName)
        }
        if (testCaseID in [TC.C367453, TC.C367454, TC.C367458]){
            createClusterWithAdditionalServers(clusterName, list)
        }
        def initiallClusterInfo = getClusterBaseInfo()

        and: "Parameters for procedure"
        def runParams = [
                configName : conf,
                wasClusterMembers : list,
                wasClusterName : clusterName,
                wasSyncNodes : syncNodes,
        ]

        when: "Run procedure and wait until job is completed"
        def result = runProcedure(runParams)

        then: "Get and compare results"
        def outcome = getJobProperty('/myJob/outcome', result.jobId)
        def jobSummary = getJobProperty("/myJob/jobSteps/$procRemoveClusterMembers/summary", result.jobId)
        def debugLog = getJobLogs(result.jobId)
        def clusterInfo = getClusterBaseInfo()


        verifyAll {
            outcome == status
            jobSummary == expectedSummary.
                    replace('CLUSTERNAME', clusterName)
            for (log in logs){
                debugLog =~ log.
                        replace('CLUSTERNAME', clusterName)
            }


            // verify that servers existed before the deletion
            for (server in list.split(",").collect { it.split(":")[1]}){
                initiallClusterInfo[clusterName].servers.any {(it.memberName == server)}
            }

            // verify that servers were removed after the procedure execution
            for (server in list.split(",").collect { it.split(":")[1]}){
                clusterInfo[clusterName].servers.every {(it.memberName != server)}
            }
        }

        cleanup:
        deleteCluster(clusterName)

        where: 'The following params will be: '
        testCaseID | conf                     | list                     | clusterName      | syncNodes | status    | expectedSummary                | logs
        TC.C367453 | confignames.correctSOAP  | membersLists.twoMembers  | 'RemoveMembers'  | '1'       | 'success' | summaries.deleteTwoClusters    | jobLogs.deleteTwoClusters+jobLogs.syncNodes
        TC.C367458 | confignames.correctSOAP  | membersLists.twoMembers  | 'RemoveMembers'  | '0'       | 'success' | summaries.deleteTwoClusters    | jobLogs.deleteTwoClusters
        TC.C367454 | confignames.correctSOAP  | membersLists.oneMember   | 'RemoveMembers'  | '1'       | 'success' | summaries.deleteOneCluster     | jobLogs.deleteOneCluster
        TC.C367455 | confignames.correctSOAP  | membersLists.firstMember | 'RemoveMembers'  | '1'       | 'success' | summaries.deleteFirstCluster   | jobLogs.deleteFirstCluster
    }

    @Ignore
    @Unroll
    def "RemoveClusterMembers  - Negative extra servers #testCaseID.ids #testCaseID.description"() {
        def testNumber = specificationContext.currentIteration.parent.iterationNameProvider.iterationCount
        clusterName += testNumber

        given: "Create cluster with server"
        if (testCaseID == TC.C367459) {
            createClusterWithAdditionalServers(clusterName, listToCreate)
        }
        if (testCaseID == TC.C367460) {
            createEmptyCluster(clusterName)
        }
        def initialClusterInfo = getClusterBaseInfo()

        and: "Parameters for procedure"
        def runParams = [
                configName : conf,
                wasClusterMembers : list,
                wasClusterName : clusterName,
                wasSyncNodes : syncNodes,
        ]

        when: "Run procedure and wait until job is completed"
        def result = runProcedure(runParams)

        then: "Get and compare results"
        def outcome = getJobProperty('/myJob/outcome', result.jobId)
        def jobSummary = getJobProperty("/myJob/jobSteps/$procRemoveClusterMembers/summary", result.jobId)
        def debugLog = getJobLogs(result.jobId)
        def clusterInfo = getClusterBaseInfo()


        verifyAll {
            outcome == status
            jobSummary == expectedSummary.
                    replace('CLUSTERNAME', clusterName)
            for (log in logs){
                debugLog =~ log.
                        replace('CLUSTERNAME', clusterName)
            }

            if (testCaseID == TC.C367459) {
                // verify that servers existed before the deletion
                for (server in listToCreate.split(",").collect { it.split(":")[1]}){
                    initialClusterInfo[clusterName].servers.any {(it.memberName == server)}
                }

                // verify that servers were removed after the procedure execution
                for (server in listToCreate.split(",").collect { it.split(":")[1]}){
                    clusterInfo[clusterName].servers.every {(it.memberName != server)}
                }
            }
        }

//        cleanup:
//        deleteCluster(clusterName)

        where: 'The following params will be: '
        testCaseID | conf                     | listToCreate                | list                        | clusterName      | syncNodes | status    | expectedSummary                | logs
        TC.C367459 | confignames.correctSOAP  | membersLists.oneMember      | membersLists.oneExtraMember | 'RemoveMembers'  | '1'       | 'error'   | summaries.deleteOneExtraMember | jobLogs.deleteOneExtraMember
//        TC.C367459 | confignames.correctSOAP  | membersLists.oneMember2     | membersLists.oneExtraMember | 'RemoveMembers'  | '1'       | 'error'   | summaries.deleteOneExtraMember | jobLogs.deleteOneExtraMember
//        TC.C367460 | confignames.correctSOAP  | ''                          | membersLists.oneExtraMember | 'RemoveMembers'  | '1'       | 'error'   | summaries.deleteOneExtraMember | jobLogs.deleteOneExtraMember
    }

    @Unroll
    def "RemoveClusterMembers  - Negative #testCaseID.ids #testCaseID.description"() {
        def testNumber = specificationContext.currentIteration.parent.iterationNameProvider.iterationCount

        given: "Create cluster with server"
        if (testNumber == 1) {
            createEmptyCluster(clusterName)
        }

        and: "Parameters for procedure"
        def runParams = [
                configName : conf,
                wasClusterMembers : list,
                wasClusterName : clusterName,
                wasSyncNodes : syncNodes,
        ]

        when: "Run procedure and wait until job is completed"
        def result = runProcedure(runParams)

        then: "Get and compare results"
        def outcome = getJobProperty('/myJob/outcome', result.jobId)
        def jobSummary = getJobProperty("/myJob/jobSteps/$procRemoveClusterMembers/summary", result.jobId)
        def debugLog = getJobLogs(result.jobId)


        verifyAll {
            outcome == status
            jobSummary == expectedSummary.
                    replace('CLUSTERNAME', clusterName)
            for (log in logs) {
                debugLog =~ log.
                        replace('CLUSTERNAME', clusterName)
            }
        }

        cleanup:
        if (testNumber == 6) {
            deleteCluster(clusterName)
        }

        where: 'The following params will be: '
        testCaseID | conf                     | list                     | clusterName      | syncNodes | status    | expectedSummary                | logs
        TC.C367462 | ''                       | membersLists.twoMembers  | 'RemoveMembers03'| '1'       | 'error'   | summaries.emptyConfig          | [summaries.emptyConfig]
        TC.C367462 | confignames.correctSOAP  | membersLists.twoMembers  | ''               | '1'       | 'error'   | summaries.emptyClustername     | jobLogs.emptyClustername
        TC.C367462 | confignames.correctSOAP  | ''                       | 'RemoveMembers03'| '1'       | 'error'   | summaries.emptyList            | [summaries.emptyList]
        TC.C367463 | confignames.incorrect    | membersLists.twoMembers  | 'RemoveMembers03'| '1'       | 'error'   | summaries.wrongConfig          | [summaries.wrongConfig]
        TC.C367463 | confignames.correctSOAP  | membersLists.twoMembers  | 'wrongCluster'   | '1'       | 'error'   | summaries.wrongClustername     | [summaries.wrongClustername]
        TC.C367463 | confignames.correctSOAP  | 'wrongFormat'            | 'RemoveMembers03'| '1'       | 'error'   | summaries.wrongList            | [summaries.wrongList]
    }


    def createClusterMembers(def clusterName, def listMembers){
        def runParams = [
                configname : confignames.correctSOAP,
                wasClusterMembersGenUniquePorts : '1',
                wasClusterMembersList : listMembers,
                wasClusterMemberWeight : '',
                wasClusterName : clusterName,
                wasSyncNodes : '1',
        ]
        runProcedure(runParams, procStopCluster)
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

    def deleteCluster(def clusterName){
        def deleteParams = [
                configname: confignames.correctSOAP,
                wasClusterName: clusterName,
                wasSyncNodes: 1,
        ]
        runProcedure(deleteParams, procDeleteCluster)
    }

    def createCluster(clusterName){
        def clusterWithServer = [
                configname                         : confignames.correctSOAP,
                wasAddClusterMembers               : '0',
                wasClusterMembersGenUniquePorts    : '1',
                wasClusterMembersList              : '',
                wasClusterMemberWeight             : '',
                wasClusterName                     : clusterName,
                wasCreateFirstClusterMember        : '1',
                wasFirstClusterMemberCreationPolicy: 'template',
                wasFirstClusterMemberGenUniquePorts: '1',
                wasFirstClusterMemberName          : 'FirstClusterServer',
                wasFirstClusterMemberNode          : nodes.default,
                wasFirstClusterMemberTemplateName  : 'default',
                wasFirstClusterMemberWeight        : '',
                wasPreferLocal                     : '1',
                wasServerResourcesPromotionPolicy  : 'both',
                wasSourceServerName                : '',
                wasSyncNodes                       : '1',
        ]
        runProcedure(clusterWithServer, procCreateCluster)
    }

    def createClusterWithAdditionalServers(def clusterName, def list){
        def clusterWithServer = [
                configname                         : confignames.correctSOAP,
                wasAddClusterMembers               : '1',
                wasClusterMembersGenUniquePorts    : '1',
                wasClusterMembersList              : list,
                wasClusterMemberWeight             : '',
                wasClusterName                     : clusterName,
                wasCreateFirstClusterMember        : '1',
                wasFirstClusterMemberCreationPolicy: 'template',
                wasFirstClusterMemberGenUniquePorts: '1',
                wasFirstClusterMemberName          : 'FirstClusterServer',
                wasFirstClusterMemberNode          : nodes.default,
                wasFirstClusterMemberTemplateName  : 'default',
                wasFirstClusterMemberWeight        : '',
                wasPreferLocal                     : '1',
                wasServerResourcesPromotionPolicy  : 'both',
                wasSourceServerName                : '',
                wasSyncNodes                       : '1',
        ]
        runProcedure(clusterWithServer, procCreateCluster)
    }

    def createEmptyCluster(clusterName){
        def emptyCluster = [
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
        runProcedure(emptyCluster, procCreateCluster)
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

    def runProcedure(def parameters, def procedureName=procRemoveClusterMembers) {
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