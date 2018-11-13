package com.electriccloud.plugin.spec

import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import com.electriccloud.plugin.spec.PluginTestHelper
import groovy.json.JsonSlurper

@Requires({ System.getenv('IS_WAS_ND') == "1"})
class RemoveClusterMembersSpecSuite extends WebSphereClusterHelper {

    @Shared
    def mainProcedure = procRemoveClusterMembers

    @Shared
    def projectName = "EC-WebSphere Specs $mainProcedure Project"

    @Shared
    def TC = [
            C367453: [ ids: 'C367453, C367457', description: 'Delete some members from cluster'],
            C367454: [ ids: 'C367454', description: 'Delete one member from cluster'],
            C367455: [ ids: 'C367455', description: 'Delete first member from cluster, left cluster empty'],
            C367457: [ ids: 'C367457', description: 'Delete first member from cluster, left cluster empty'],
            C367458: [ ids: 'C367458', description: 'Delete first member from cluster, left cluster empty'],
            C367459: [ ids: 'C367459', description: "delete extra servers - one server exists, another server doesn't"],
            C367460: [ ids: 'C367460', description: "delete extra servers - use empty cluster"],
            C367461: [ ids: 'C367461', description: "delete extra servers - first server doesn't exist, second server exist"],
            C367462: [ ids: 'C367462', description: "empty required fields"],
            C367463: [ ids: 'C367463', description: "wrong values"],
    ]

    @Shared
    def summaries = [
            deleteTwoClusters: 'Cluster member serverClusterMember0(1|2)'+" on node ${nodes.default} has been removed from cluster CLUSTERNAME and deleted\n" +
                    'Cluster member serverClusterMember0(1|2)'+" on node ${nodes.default} has been removed from cluster CLUSTERNAME and deleted\n",
            deleteOneCluster:  "Cluster member serverClusterMember1 on node ${nodes.default} has been removed from cluster CLUSTERNAME and deleted\n",
            deleteFirstCluster: "Cluster member ${mainProcedure}FirstClusterMember on node ${nodes.default} has been removed from cluster CLUSTERNAME and deleted\n",
            deleteOneExtraMember: "Failed to remove cluster members.\n" +
                    "Error: Server ${nodes.default}:serverClusterMember2 (does not exist) is not a member of cluster CLUSTERNAME, please, check your input\n",
            deleteOneExtraMember2: "Failed to remove cluster members.\n" +
                    "Error: Server ${nodes.default}:serverClusterMember1 (does not exist) is not a member of cluster CLUSTERNAME, please, check your input\n",
            deleteFromEmptyCluster: "Failed to remove cluster members.\n" +
                    "Error: Server ${nodes.default}:serverClusterMember1 (does not exist) is not a member of cluster CLUSTERNAME, please, check your input\n" +
                    "Error: Server ${nodes.default}:serverClusterMember2 (does not exist) is not a member of cluster CLUSTERNAME, please, check your input\n",
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
            deleteFirstCluster: ["Cluster member ${mainProcedure}FirstClusterMember on node ${nodes.default} has been removed from cluster CLUSTERNAME and deleted"],
            syncNodes: ["Synchronizing configuration repository with nodes now.", "The following nodes have been synchronized: ${nodes.default}"],
            deleteOneExtraMember: ["Cluster member serverClusterMember1 on node ${nodes.default} has been removed from cluster CLUSTERNAME and deleted",
                    "Exception: WASL6040E: The serverMember:serverClusterMember2 specified argument does not exist."],
            deleteFromEmptyCluster: [""],
            emptyClustername: ["WASL6041E: The following argument value is not valid: clusterName:"],
    ]

        def doSetupSpec() {
        def wasResourceName = wasHost
        createWorkspace(wasResourceName)
        createConfiguration(confignames.correctSOAP, [doNotRecreate: false])
        importProcedure(projectName, wasResourceName, procDeleteCluster)
        importProcedure(projectName, wasResourceName, procStartCluster)
        importProcedure(projectName, wasResourceName, procStopCluster)
        importProcedure(projectName, wasResourceName, procCreateClusterMembers)
        importProcedure(projectName, wasResourceName, procRemoveClusterMembers)
        importProcedure(projectName, wasResourceName, procCreateCluster)
        importProcedure(projectName, wasResourceName, procRunJob)
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
            jobSummary ==~ expectedSummary.
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

    @Unroll
    def "RemoveClusterMembers  - Negative extra servers #testCaseID.ids #testCaseID.description"() {
        def testNumber = specificationContext.currentIteration.parent.iterationNameProvider.iterationCount
        clusterName += testNumber

        given: "Create cluster with server"
        def initialClusterInfo
        if (testCaseID in [TC.C367459, TC.C367461]) {
            createClusterWithAdditionalServers(clusterName, listToCreate)
            initialClusterInfo = getClusterBaseInfo()
        }
        if (testCaseID == TC.C367460) {
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
            for (log in logs){
                debugLog.contains(log.
                        replace('CLUSTERNAME', clusterName)
                )
            }
            if (testCaseID in [TC.C367459, TC.C367461]) {
                // created servers shouldn't be deleted
                for (server in listToCreate.split(",").collect { it.split(":")[1] }) {
                    initialClusterInfo[clusterName].servers.any { (it.memberName == server) }
                }
            }

        }

        cleanup:
        deleteCluster(clusterName)

        where: 'The following params will be: '
        testCaseID | conf                     | listToCreate                | list                        | clusterName      | syncNodes | status    | expectedSummary                  | logs
        TC.C367459 | confignames.correctSOAP  | membersLists.oneMember      | membersLists.oneExtraMember | 'RemoveMembers'  | '1'       | 'error'   | summaries.deleteOneExtraMember   | [summaries.deleteOneExtraMember]
        TC.C367461 | confignames.correctSOAP  | membersLists.oneMember2     | membersLists.oneExtraMember | 'RemoveMembers'  | '1'       | 'error'   | summaries.deleteOneExtraMember2  | [summaries.deleteOneExtraMember2]
        TC.C367460 | confignames.correctSOAP  | ''                          | membersLists.oneExtraMember | 'RemoveMembers'  | '1'       | 'error'   | summaries.deleteFromEmptyCluster | [summaries.deleteFromEmptyCluster]
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

}
