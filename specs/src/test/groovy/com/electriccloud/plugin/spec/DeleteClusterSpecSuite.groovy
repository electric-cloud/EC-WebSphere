package com.electriccloud.plugin.spec

import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import com.electriccloud.plugin.spec.PluginTestHelper


@Requires({ System.getenv('IS_WAS_ND') == "1"})
class DeleteClusterSpecSuite extends WebSphereClusterHelper {

    @Shared
    def mainProcedure = procDeleteCluster

    @Shared
    def TC = [
            C366939: [ ids: 'C366939', description: 'delete empty cluster'],
            C366940: [ ids: 'C366940', description: 'delete cluster with server'],
            C366941: [ ids: 'C366941', description: 'delete running cluster with server '],
            C366942: [ ids: 'C366942', description: 'SyncNodes - 0'],
            C366943: [ ids: 'C366943', description: 'empty required fields'],
            C366945: [ ids: 'C366945', description: 'wrong values'],
    ]

    @Shared
    def summaries = [
            'default': "Cluster CLUSTERNAME has been deleted\n",
            'emptyConfig': "Configuration '' doesn't exist",
            'wrongConfig': "Configuration 'incorrect' doesn't exist",
            'wrongCluster': "Failed to delete cluster.\n" +
                    "Error: Cluster CLUSTERNAME does not exist\n",
            'runningCluster': "Failed to delete cluster.\n" +
                    "Error: Cluster CLUSTERNAME can't be deleted because it is running.\n" +
                    "Deletion of running cluster may damage websphere instance and leads to undefined behaviour.\n" +
                    "Please, stop your cluster first.\n"
    ]

    @Shared
    def jobLogs = [
        syncNodes: ["Synchronizing configuration repository with nodes now", "The following nodes have been synchronized: ${nodes.default}", "syncNodes = '''\n1"],
        noSyncNodes: ["syncNodes = '''\n0"],
        wrongCluster: ["Failed to delete cluster", "Exception: ADMG9216E: Cannot find cluster CLUSTERNAME"],        
                ]    

    def doSetupSpec() {
        def wasResourceName = wasHost
        createWorkspace(wasResourceName)
        createConfiguration(confignames.correctSOAP, [doNotRecreate: false])
        importProcedure(projectName, wasResourceName, procCreateCluster)
        importProcedure(projectName, wasResourceName, procDeleteCluster)
        importProcedure(projectName, wasResourceName, procStartCluster)
        importProcedure(projectName, wasResourceName, procStopCluster)
        importProcedure(projectName, wasResourceName, procRunJob)
        dsl 'setProperty(propertyName: "/plugins/EC-WebSphere/project/ec_debug_logToProperty", value: "/myJob/debug_logs")'
    }        

    def doCleanupSpec() {
    }

    @Unroll
    def "Delete Cluster - Positive #testCaseID.id #testCaseID.description"(){
        def numberOfTest = specificationContext.currentIteration.parent.iterationNameProvider.iterationCount
        clusterName += numberOfTest

        given: "Create Test Cluster"
        if (clusterType == 'notEmpty'){
            createCluster(clusterName)
        }
        else {
            createEmptyCluster(clusterName)
        }
        assert doesClusterExist(clusterName) == 'true'
        if (clusterState == 'run'){
            startCluster(clusterName)
        }
        
        and: "Parameters for procedure"
        def runParams = [
            configname: conf,
            wasClusterName: clusterName,
            wasSyncNodes: syncNodes,
        ]
        
        when: "Run procedure and wait until job is completed"
        def result = runProcedure(runParams)

        then: "Get and compare results"
        def outcome = getJobProperty('/myJob/outcome', result.jobId)
        def jobSummary = getJobProperty("/myJob/jobSteps/$procDeleteCluster/summary", result.jobId)
        def debugLog = getJobLogs(result.jobId)

        verifyAll {
            assert outcome == status
            assert jobSummary == expectedSummary
                .replace('CLUSTERNAME', clusterName)

            for (log in logs){
                assert debugLog =~ log
                    .replace('CLUSTERNAME', clusterName)
            }            
            assert doesClusterExist(clusterName) == 'false'
        }
        where: 'The following params will be:'
        testCaseID | conf                    | clusterName     | syncNodes | clusterType | clusterState | status    | expectedSummary   | logs
        TC.C366939 | confignames.correctSOAP | 'DeleteCluster' | '1'       | 'empty'     | 'stopped'    | 'success' | summaries.default | [summaries.default] + jobLogs.syncNodes
        TC.C366942 | confignames.correctSOAP | 'DeleteCluster' | '0'       | 'empty'     | 'stopped'    | 'success' | summaries.default | [summaries.default] + jobLogs.noSyncNodes
        TC.C366940 | confignames.correctSOAP | 'DeleteCluster' | '1'       | 'notEmpty'  | 'stopped'    | 'success' | summaries.default | [summaries.default]
    }

    @Unroll
    def "Delete Cluster running cluster #testCaseID.id #testCaseID.description"(){
        def numberOfTest = specificationContext.currentIteration.parent.iterationNameProvider.iterationCount
        clusterName += numberOfTest

        given: "Create Test Cluster"
        if (clusterType == 'notEmpty'){
            createCluster(clusterName)
        }
        else {
            createEmptyCluster(clusterName)
        }
        assert doesClusterExist(clusterName) == 'true'
        startCluster(clusterName)

        and: "Parameters for procedure"
        def runParams = [
                configname: conf,
                wasClusterName: clusterName,
                wasSyncNodes: syncNodes,
        ]

        when: "Run procedure and wait until job is completed"
        def result = runProcedure(runParams)

        then: "Get and compare results"
        def outcome = getJobProperty('/myJob/outcome', result.jobId)
        def jobSummary = getJobProperty("/myJob/jobSteps/$procDeleteCluster/summary", result.jobId)
        def debugLog = getJobLogs(result.jobId)

        verifyAll {
            assert outcome == status
            assert jobSummary == expectedSummary
                    .replace('CLUSTERNAME', clusterName)

            for (log in logs){
                assert debugLog =~ log
                        .replace('CLUSTERNAME', clusterName)
            }
            assert doesClusterExist(clusterName) == 'true'
        }
        cleanup: "Stop and delete cluster"
        stopCluster(clusterName)
        runProcedure(runParams)
        where: 'The following params will be:'
        testCaseID | conf                    | clusterName            | syncNodes | clusterType | status    | expectedSummary          | logs
        TC.C366941 | confignames.correctSOAP | 'DeleteRunningCluster' | '1'       | 'notEmpty'  | 'error'   | summaries.runningCluster | [summaries.runningCluster]
    }

    @Unroll
    def "Delete Cluster - Negative #testCaseID.ids #testCaseID.description"(){
        given: "Parameters for procedure"
        def runParams = [
            configname: conf,
            wasClusterName: clusterName,
            wasSyncNodes: syncNodes,
        ]
        
        when: "Run procedure and wait until job is completed"
        def result = runProcedure(runParams)

        then: "Get and compare results"
        def outcome = getJobProperty('/myJob/outcome', result.jobId)
        def jobSummary = getJobProperty("/myJob/jobSteps/$procDeleteCluster/summary", result.jobId)
        def debugLog = getJobLogs(result.jobId)

        verifyAll {
            assert outcome == status
            assert jobSummary == expectedSummary
                .replace('CLUSTERNAME', clusterName)

            for (log in logs){
                assert debugLog =~ log
                    .replace('CLUSTERNAME', clusterName)
            }            
        }
        where: 'The following params will be:'
        testCaseID | conf                    | clusterName     | syncNodes | status    | expectedSummary        | logs
        TC.C366943 | confignames.correctSOAP | ''              | '1'       | 'error'   | summaries.wrongCluster | summaries.wrongCluster
        TC.C366943 | ''                      | 'DeleteCluster' | '1'       | 'error'   | summaries.emptyConfig  | [summaries.emptyConfig]
        TC.C366945 | confignames.correctSOAP | 'Wrong'         | '1'       | 'error'   | summaries.wrongCluster | summaries.wrongCluster
        TC.C366945 | confignames.incorrect   | 'DeleteCluster' | '1'       | 'error'   | summaries.wrongConfig  | [summaries.wrongConfig]
    }

}
