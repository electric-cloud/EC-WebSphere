package com.electriccloud.plugin.spec

import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import com.electriccloud.plugin.spec.PluginTestHelper
import groovy.json.JsonSlurper

@Requires({ System.getenv('IS_WAS_ND') == "1"})
class ListClusterMembersSpecSuite extends WebSphereClusterHelper {

    @Shared
    def mainProcedure = procListClusterMembers

    @Shared
    def TC = [
            C367445: [ ids: 'C367445', description: 'ListClusterMembers: Cluster with one first member'],
            C367446: [ ids: 'C367446', description: 'ListClusterMembers: Cluster with first member and additional servers'],
            C367447: [ ids: 'C367447', description: 'ListClusterMembers: Empty Cluster'],
            C367448: [ ids: 'C367448', description: 'ListClusterMembers: custom output property'],
            C367449: [ ids: 'C367449', description: 'empty required fields '],
            C367450: [ ids: 'C367450', description: 'wrong values'],
            C367452: [ ids: 'C367452', description: 'empty wasOutputPropertyPath'],
    ]

    @Shared
    def summaries = [
            'clusterWithFirstMember': "Cluster CLUSTERNAME has following members:\n" +
                    "Server ${mainProcedure}FirstClusterMember on node ${nodes.default}\n",
            'clusterWithAddServers': "Cluster CLUSTERNAME has following members:\n" +
                    "Server ${mainProcedure}FirstClusterMember on node ${nodes.default}\n" +
                    'Server serverClusterMember0(1|2)'+" on node ${nodes.default}\n" +
                    'Server serverClusterMember0(1|2)'+" on node ${nodes.default}\n",
            'emptyCluster': "Cluster CLUSTERNAME is empty\n",
            'emptyConfig': "Configuration '' doesn't exist",
            'emptyClusterName': "Failed to retrieve cluster members list.\n"+
                                "Error: Cluster  does not exist\n",
            'wrongClusterName': "Failed to retrieve cluster members list.\n"+
                                "Error: Cluster wrongCluster does not exist\n",
            'wrongConfig': "Configuration '${confignames.incorrect}' doesn't exist",
    ]

    @Shared
    def jobLogs = [
            syncNodes: ["Synchronizing configuration repository with nodes now.", "The following nodes have been synchronized: ${nodes.default}"],
            emptyClusterName : ["WASL6041E: The following argument value is not valid: clusterName:"],
            wrongCluster: ["WASL6040E: The clusterName:wrongCluster specified argument does not exist"]
    ]

    @Shared
    def outputProperties = [
            clusterWithFirstMember: "${nodes.default}:${mainProcedure}FirstClusterMember",
            clusterWithAddServers:  "${nodes.default}:${mainProcedure}FirstClusterMember,${nodes.default}:"+'serverClusterMember0(1|2),'+"${nodes.default}:"+'serverClusterMember0(1|2)',
            'emptyCluster': "",
    ]

    def doSetupSpec() {
        def wasResourceName = wasHost
        createWorkspace(wasResourceName)
        createConfiguration(confignames.correctSOAP, [doNotRecreate: false])
        importProcedure(projectName, wasResourceName, procDeleteCluster)
        importProcedure(projectName, wasResourceName, procListClusterMembers)
        importProcedure(projectName, wasResourceName, procCreateCluster)
        importProcedure(projectName, wasResourceName, procRunJob)
        dsl 'setProperty(propertyName: "/plugins/EC-WebSphere/project/ec_debug_logToProperty", value: "/myJob/debug_logs")'
    }

    def doCleanupSpec() {
    }

    @Unroll
    def "ListClusterMembers  - Positive #testCaseID.ids #testCaseID.description"() {
        def testNumber = specificationContext.currentIteration.parent.iterationNameProvider.iterationCount
        clusterName += testNumber

        given: "Create cluster with server"
        if (testCaseID in  [TC.C367445, TC.C367448, TC.C367452]){
            createCluster(clusterName)
        }

        if (testCaseID == TC.C367446){
            createClusterWithAdditionalServers(clusterName, membersLists.twoMembers)
        }

        if (testCaseID == TC.C367447){
            createEmptyCluster(clusterName)
        }

        and: "Parameters for procedure"
        def runParams = [
                configName : conf,
                wasClusterName : clusterName,
                wasOutputPropertyPath: propertyPath,
        ]

        when: "Run procedure and wait until job is completed"
        def result = runProcedure(runParams)
        if (propertyPath == ""){
            propertyPath = "/myJob/clusterMembers"
        }

        then: "Get and compare results"
        def outcome = getJobProperty('/myJob/outcome', result.jobId)
        def jobSummary = getJobProperty("/myJob/jobSteps/$procListClusterMembers/summary", result.jobId)
        def output
        def jobProperties
        if (testCaseID != TC.C367447) {
            output = getJobProperty(propertyPath, result.jobId)
        }
        else {
            jobProperties = getJobProperties(result.jobId)
        }
        def debugLog = getJobLogs(result.jobId)

        verifyAll {
            outcome == status
            jobSummary ==~ expectedSummary.
                    replace('CLUSTERNAME', clusterName)
            if (testCaseID != TC.C367447) {
                output ==~ expectedOutput
            }
            else {
                jobProperties.'clusterMembers' == null
            }
            for (log in logs){
                debugLog =~ log.
                        replace('CLUSTERNAME', clusterName)
            }
        }

        cleanup:
        deleteCluster(clusterName)

        where: 'The following params will be: '
        testCaseID | conf                     | clusterName      | propertyPath             | status    | expectedSummary                     | expectedOutput                          | logs
        TC.C367445 | confignames.correctSOAP  | 'ListMembers'    | '/myJob/clusterMembers'  | 'success' | summaries.clusterWithFirstMember    | outputProperties.clusterWithFirstMember | [summaries.clusterWithFirstMember]
        TC.C367446 | confignames.correctSOAP  | 'ListMembers'    | '/myJob/clusterMembers'  | 'success' | summaries.clusterWithAddServers    | outputProperties.clusterWithAddServers | [summaries.clusterWithAddServers]
        TC.C367447 | confignames.correctSOAP  | 'ListMembers'    | '/myJob/clusterMembers'  | 'success' | summaries.emptyCluster             | ''                                     | [summaries.emptyCluster]
        TC.C367448 | confignames.correctSOAP  | 'ListMembers'    | '/myJob/test'            | 'success' | summaries.clusterWithFirstMember    | outputProperties.clusterWithFirstMember | [summaries.clusterWithFirstMember]
        TC.C367452 | confignames.correctSOAP  | 'ListMembers'    | ''                       | 'success' | summaries.clusterWithFirstMember    | outputProperties.clusterWithFirstMember | [summaries.clusterWithFirstMember]
    }

    @Unroll
    def "ListClusterMembers  - Negative #testCaseID.ids #testCaseID.description"() {
        def testNumber = specificationContext.currentIteration.parent.iterationNameProvider.iterationCount

        given: "Create cluster with server"
        if (testNumber == 1) {
            createCluster(clusterName)
        }
        and: "Parameters for procedure"
        def runParams = [
                configName : conf,
                wasClusterName : clusterName,
                wasOutputPropertyPath: propertyPath,
        ]

        when: "Run procedure and wait until job is completed"
        def result = runProcedure(runParams)

        then: "Get and compare results"
        def outcome = getJobProperty('/myJob/outcome', result.jobId)
        def jobSummary = getJobProperty("/myJob/jobSteps/$procListClusterMembers/summary", result.jobId)
        def jobProperties = getJobProperties(result.jobId)
        def debugLog = getJobLogs(result.jobId)

        verifyAll {
            outcome == status
            jobSummary == expectedSummary.
                    replace('CLUSTERNAME', clusterName)
            jobProperties.'clusterMembers' == null
            for (log in logs){
                debugLog =~ log.
                        replace('CLUSTERNAME', clusterName)
            }
        }

        cleanup:
        if (testNumber == 4) {
            deleteCluster(clusterName)
        }
        where: 'The following params will be: '
        testCaseID | conf                     | clusterName      | propertyPath             | status    | expectedSummary               | logs
        TC.C367449 | ''                       | 'ListMembers01'  | '/myJob/clusterMembers'  | 'error'   | summaries.emptyConfig         | [summaries.emptyConfig]
        TC.C367449 | confignames.correctSOAP  | ''               | '/myJob/clusterMembers'  | 'error'   | summaries.emptyClusterName    | [summaries.emptyClusterName]+jobLogs.emptyClusterName
        TC.C367450 | confignames.correctSOAP  | 'wrongCluster'   | '/myJob/clusterMembers'  | 'error'   | summaries.wrongClusterName    | [summaries.wrongClusterName]
        TC.C367450 | confignames.incorrect    | 'ListMembers01'  | '/myJob/clusterMembers'  | 'error'   | summaries.wrongConfig         | [summaries.wrongConfig]
    }

}