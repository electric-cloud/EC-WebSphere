package com.electriccloud.plugin.spec

import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import com.electriccloud.plugin.spec.PluginTestHelper

@Requires({ System.getenv('IS_WAS_ND') == "1"})
class CreateClusterMembersSpecSuite extends WebSphereClusterHelper {

    @Shared
    def mainProcedure = procCreateClusterMembers

    @Shared
    def firstClusterServerName = mainProcedure+'FirstClusterMember'

    @Shared
    def TC = [
            C367309: [ ids: 'C367309, C367317', description: 'default values - add one server'],
            C367310: [ ids: 'C367310', description: 'default values - add more the one server'],
            C367311: [ ids: 'C367311', description: 'empty cluster - add one server '],
            C367312: [ ids: 'C367312', description: 'empty cluster - add more the one server'],
            C367313: [ ids: 'C367313', description: 'server weight'],
            C367314: [ ids: 'C367314', description: 'uniqure ports - true'],
            C367315: [ ids: 'C367315', description: 'uniqure ports - false'],
            C367316: [ ids: 'C367316', description: 'sync node - false'],
            C367318: [ ids: 'C367318', description: 'add servers - twice'],
            C367319: [ ids: 'C367319', description: 'empty required fields'],
            C367350: [ ids: 'C367350', description: 'wrong values'],
    ]

    @Shared
    def summaries = [
            'default': "Server serverClusterMember1 on node ${nodes.default} has been created and added to CLUSTERNAME cluster\n",
            'twoMembers': 'Server serverClusterMember0(1|2) on node '+"${nodes.default} has been created and added to CLUSTERNAME cluster\n"+
            'Server serverClusterMember0(1|2) on node '+"${nodes.default} has been created and added to CLUSTERNAME cluster\n",
            'emptyConfig': "Configuration '' doesn't exist",
            'emptyList': 'Failed to create a cluster members.\n' +
                    'Exception: Expected nodename:servername record, got\n' +
                    'Error: Expected nodename:servername record, got \n',
            'emptyClusterName': "Failed to create a cluster members.\n" +
                    "Error: Cluster  does not exist\n",
            'wrongConfig': "Configuration '${confignames.incorrect}' doesn't exist",
            'wrongCluster': "Failed to create a cluster members.\n" +
                    "Error: Cluster wrongCluster does not exist\n",
            'wrongFormat': "Failed to create a cluster members.\n" +
                    "Exception: Expected nodename:servername record, got wrongFormat\n" +
                    "Error: Expected nodename:servername record, got wrongFormat\n",
    ]

    @Shared
    def jobLogs = [
            syncNodes: ["Synchronizing configuration repository with nodes now.", "The following nodes have been synchronized: ${nodes.default}"],
    ]

    def doSetupSpec() {
        def wasResourceName = wasHost
        createWorkspace(wasResourceName)
        createConfiguration(confignames.correctSOAP, [doNotRecreate: false])
        importProcedure(projectName, wasResourceName, procDeleteCluster)
        importProcedure(projectName, wasResourceName, procStartCluster)
        importProcedure(projectName, wasResourceName, procStopCluster)
        importProcedure(projectName, wasResourceName, procCreateClusterMembers)
        importProcedure(projectName, wasResourceName, procCreateCluster)
        importProcedure(projectName, wasResourceName, procRunJob)
        dsl 'setProperty(propertyName: "/plugins/EC-WebSphere/project/ec_debug_logToProperty", value: "/myJob/debug_logs")'
    }

    def doCleanupSpec() {
    }

    @Unroll
    def "CreateClusterMembers  - Positive #testCaseID.ids #testCaseID.description"() {
        def testNumber = specificationContext.currentIteration.parent.iterationNameProvider.iterationCount
        clusterName += testNumber

        given: "Create cluster with server"
        if (testCaseID in [TC.C367311, TC.C367312]) {
            createEmptyCluster(clusterName)
        }
        else {
            createCluster(clusterName)
        }

        and: "Parameters for procedure"
        def runParams = [
                configname : conf,
                wasClusterMembersGenUniquePorts : uniquePorts,
                wasClusterMembersList : list,
                wasClusterMemberWeight : weight,
                wasClusterName : clusterName,
                wasSyncNodes : syncNodes,
        ]

        when: "Run procedure and wait until job is completed"
        def result = runProcedure(runParams)

        then: "Get and compare results"
        def outcome = getJobProperty('/myJob/outcome', result.jobId)
        def jobSummary = getJobProperty("/myJob/jobSteps/$procCreateClusterMembers/summary", result.jobId)
        def debugLog = getJobLogs(result.jobId)
        def clusterInfo = getClusterBaseInfo()

        def portsOfMember, portsOfFirstMember, portsOfDefaultServer, diff1, diff2
        if (testCaseID in [TC.C367314, TC.C367315]) {
            portsOfMember = getServerPorts(list.split(":")[1])
            portsOfFirstMember = getServerPorts(firstClusterServerName)
            portsOfDefaultServer = getServerPorts(servers.default)
            diff1 = portsOfMember - portsOfFirstMember
            diff2 = portsOfMember - portsOfDefaultServer
        }
        verifyAll {
            outcome == status
            jobSummary ==~ expectedSummary.
                    replace('CLUSTERNAME', clusterName)
            for (log in logs){
                debugLog =~ log.
                        replace('CLUSTERNAME', clusterName)
            }
            if (!list.isEmpty()){
                for (server in list.split(",").collect { it.split(":")[1]}){
                    clusterInfo[clusterName].servers.any {(it.memberName == server)}

                }
            }

            if (!weight.isEmpty()){
                clusterInfo[clusterName].servers.any {(it.weight == weight)}
            }

            if (testCaseID in [TC.C367314]) {
                (diff1 != []) && (diff2 != [])
            }
            if (testCaseID in [TC.C367315]) {
                (diff1 == []) || (diff2 == [])
            }

        }

        cleanup:
        deleteCluster(clusterName)

        where: 'The following params will be: '
        testCaseID | conf                     | uniquePorts | list                     | weight | clusterName      | syncNodes | status    | expectedSummary      | logs
        TC.C367309 | confignames.correctSOAP  | '1'         | membersLists.oneMember   | ''     | 'CreateMembers'  | '1'       | 'success' | summaries.default    | [summaries.default]+jobLogs.syncNodes
        TC.C367316 | confignames.correctSOAP  | '1'         | membersLists.oneMember   | ''     | 'CreateMembers'  | '0'       | 'success' | summaries.default    | [summaries.default]
        TC.C367310 | confignames.correctSOAP  | '1'         | membersLists.twoMembers  | ''     | 'CreateMembers'  | '1'       | 'success' | summaries.twoMembers | [summaries.twoMembers]
        TC.C367313 | confignames.correctSOAP  | '1'         | membersLists.oneMember   | '5'    | 'CreateMembers'  | '1'       | 'success' | summaries.default    | [summaries.default]
        TC.C367314 | confignames.correctSOAP  | '1'         | membersLists.oneMember   | ''     | 'CreateMembers'  | '1'       | 'success' | summaries.default    | [summaries.default]
        TC.C367315 | confignames.correctSOAP  | '0'         | membersLists.oneMember   | ''     | 'CreateMembers'  | '1'       | 'success' | summaries.default    | [summaries.default]
    }

    @Unroll
    def "CreateClusterMembers, twice  - Positive #testCaseID.ids #testCaseID.description"() {
        def testNumber = specificationContext.currentIteration.parent.iterationNameProvider.iterationCount
        clusterName += testNumber

        given: "Create cluster with server"
        createCluster(clusterName)

        and: "Parameters for procedure"
        def runParams = [
                configname : conf,
                wasClusterMembersGenUniquePorts : uniquePorts,
                wasClusterMembersList : list,
                wasClusterMemberWeight : weight,
                wasClusterName : clusterName,
                wasSyncNodes : syncNodes,
        ]

        when: "Run procedure first time"
        runProcedure(runParams)

        and: "Run it again but create another servers"
        runParams['wasClusterMembersList'] = secondList
        def result = runProcedure(runParams)
        list += ','+secondList

        then: "Get and compare results"
        def outcome = getJobProperty('/myJob/outcome', result.jobId)
        def jobSummary = getJobProperty("/myJob/jobSteps/$procCreateClusterMembers/summary", result.jobId)
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
            if (!list.isEmpty()){
                for (server in list.split(",").collect { it.split(":")[1]}){
                    clusterInfo[clusterName].servers.any {(it.memberName == server)}

                }
            }

        }

        cleanup:
        deleteCluster(clusterName)

        where: 'The following params will be: '
        testCaseID | conf                     | uniquePorts | list                     | secondList              | weight | clusterName      | syncNodes | status    | expectedSummary      | logs
        TC.C367318 | confignames.correctSOAP  | '1'         | membersLists.oneMember   | membersLists.twoMembers | ''     | 'CreateMembers2' | '1'       | 'success' | summaries.twoMembers | [summaries.twoMembers]
    }

    @Unroll
    def "CreateClusterMembers, Negative #testCaseID.ids #testCaseID.description"() {
        def testNumber = specificationContext.currentIteration.parent.iterationNameProvider.iterationCount

        given: "Create cluster with server"
        if (testNumber == 1) {
            createCluster(clusterName)
        }
        and: "Parameters for procedure"
        def runParams = [
                configname : conf,
                wasClusterMembersGenUniquePorts : uniquePorts,
                wasClusterMembersList : list,
                wasClusterMemberWeight : weight,
                wasClusterName : clusterName,
                wasSyncNodes : syncNodes,
        ]

        when: "Run procedure first time"
        def result = runProcedure(runParams)


        then: "Get and compare results"
        def outcome = getJobProperty('/myJob/outcome', result.jobId)
        def jobSummary = getJobProperty("/myJob/jobSteps/$procCreateClusterMembers/summary", result.jobId)
        def debugLog = getJobLogs(result.jobId)

        verifyAll {
            outcome == status
            jobSummary == expectedSummary.
                    replace('CLUSTERNAME', clusterName)
            for (log in logs){
                debugLog =~ log.
                        replace('CLUSTERNAME', clusterName)
            }

        }

        cleanup:
        if (testNumber == 6) {
            deleteCluster(clusterName)
        }

        where: 'The following params will be: '
        testCaseID | conf                     | uniquePorts | list                     | weight | clusterName      | syncNodes | status    | expectedSummary             | logs
        TC.C367319 | ''                       | '1'         | membersLists.oneMember   | ''     | 'CreateMembers3' | '1'       | 'error'   | summaries.emptyConfig       | [summaries.emptyConfig]
        TC.C367319 | confignames.correctSOAP  | '1'         | ''                       | ''     | 'CreateMembers3' | '1'       | 'error'   | summaries.emptyList         | [summaries.emptyList]
        TC.C367319 | confignames.correctSOAP  | '1'         | membersLists.oneMember   | ''     | ''               | '1'       | 'error'   | summaries.emptyClusterName  | [summaries.emptyClusterName]
        TC.C367350 | confignames.incorrect    | '1'         | membersLists.oneMember   | ''     | 'CreateMembers3' | '1'       | 'error'   | summaries.wrongConfig       | [summaries.wrongConfig]
        TC.C367350 | confignames.correctSOAP  | '1'         | membersLists.oneMember   | ''     | 'wrongCluster'   | '1'       | 'error'   | summaries.wrongCluster      | [summaries.wrongCluster]
        TC.C367350 | confignames.correctSOAP  | '1'         | 'wrongFormat'            | ''     | 'CreateMembers3' | '1'       | 'error'   | summaries.wrongFormat       | [summaries.wrongFormat]

    }

}
