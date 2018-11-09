package com.electriccloud.plugin.spec

import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import com.electriccloud.plugin.spec.PluginTestHelper
import groovy.json.JsonSlurper

@Requires({ System.getenv('IS_WAS_ND') == "1"})
class StartClusterSpecSuite extends WebSphereClusterHelper {

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
    def projectName = "EC-WebSphere Specs $procStartCluster Project"

    @Shared
    def TC = [
            C367272: [ ids: 'C367272', description: 'start cluster with one server '],
            C367279: [ ids: 'C367279', description: 'set Timeout '],
            C367282: [ ids: 'C367282', description: 'start empty cluster '],
            C367284: [ ids: 'C367284', description: 'not enough time '],
            C367286: [ ids: 'C367286', description: 'wrong values'],
            C367287: [ ids: 'C367287', description: 'non existing cluster'],
    ]

    @Shared
    def summaries = [
            'default': 'Cluster CLUSTERNAME has been started\n',
            'timeout': 'Cluster was not started, exited by timeout\n',
            'emptyConfig': "Configuration '' doesn't exist",
            'emptyCluster': "Failed to start cluster .\n" +
                    "Cluster  was not found.\n" +
                    "Tip: Available clusters: <none>\n",
            'wrongConfig': "Configuration '${confignames.incorrect}' doesn't exist",
            'wrongCluster': "Failed to start cluster wrongCluster.\n" +
                    "Cluster wrongCluster was not found.\n" +
                    "Tip: Available clusters: <none>\n",
            'wrongCluster2': "Failed to start cluster wrongCluster.\n" +
                    "Cluster wrongCluster was not found.\n" +
                    "Tip: Available clusters: TestCluster\n",
    ]

    @Shared
    def jobLogs = [
            timeout: "timeout = r'''\n" +
                    "120\n" +
                    "'''.strip()"
    ]

    def doSetupSpec() {
        def wasResourceName = wasHost
        createWorkspace(wasResourceName)
        createConfiguration(confignames.correctSOAP, [doNotRecreate: false])
        importProcedure(projectName, wasResourceName, procStartCluster)
        importProcedure(projectName, wasResourceName, procCreateCluster)
        importProcedure(projectName, wasResourceName, procDeleteCluster)
        importProcedure(projectName, wasResourceName, procStopCluster)
        importProcedure(projectName, wasResourceName, procRunJob)
        dsl 'setProperty(propertyName: "/plugins/EC-WebSphere/project/ec_debug_logToProperty", value: "/myJob/debug_logs")'
    }

    def doCleanupSpec() {
    }

    @Unroll
    def "StartCluster  - Positive #testCaseID.ids #testCaseID.description"() {
        def testNumber = specificationContext.currentIteration.parent.iterationNameProvider.iterationCount

        given: "Create cluster with server"
        if ( testNumber == 1) {
            createCluster(clusterName)
        }

        and: "Parameters for procedure"
        def runParams = [
                configName: conf,
                wasClusterName: clusterName,
                wasTimeout: timeout,
        ]

        when: "Run procedure and wait until job is completed"
        def result = runProcedure(runParams, procStartCluster)

        then: "Get and compare results"
        def outcome = getJobProperty('/myJob/outcome', result.jobId)
        def jobSummary = getJobProperty("/myJob/jobSteps/$procStartCluster/summary", result.jobId)
        def debugLog = getJobLogs(result.jobId)
        def clusterState = getClusterState(clusterName)

        verifyAll {
            outcome == status
            jobSummary == expectedSummary.
                    replace('CLUSTERNAME', clusterName)
            for (log in logs){
                debugLog =~ log.
                        replace('CLUSTERNAME', clusterName)
            }
            clusterState == 'running'
        }

        cleanup:
        stopCluster(clusterName)
        if (testNumber == 2){
            deleteCluster(clusterName)
        }

        where: 'The following params will be: '
        testCaseID | conf                    | clusterName          | timeout | status    | expectedSummary      | logs
        TC.C367242 | confignames.correctSOAP | 'StartCluster'       | '300'   | 'success' | summaries.default    | [summaries.default]
        TC.C367279 | confignames.correctSOAP | 'StartCluster'       | '120'   | 'success' | summaries.default    | [summaries.default]+jobLogs.timeout
    }

    @Unroll
    def "StartCluster  - Negative #testCaseID.ids #testCaseID.description"() {
        def testNumber = specificationContext.currentIteration.parent.iterationNameProvider.iterationCount

        given: "Create a cluster"
        if ( testCaseID == TC.C367282) {
            createEmptyCluster(clusterName)
        }
        if ( testCaseID == TC.C367284) {
            createCluster(clusterName)
        }
        if ( testCaseID == TC.C367287) {
            createCluster('TestCluster')
        }

        and: "Parameters for procedure"
        def runParams = [
                configName: conf,
                wasClusterName: clusterName,
                wasTimeout: timeout,
        ]

        when: "Run procedure and wait until job is completed"
        def result = runProcedure(runParams, procStartCluster)

        then: "Get and compare results"
        def outcome = getJobProperty('/myJob/outcome', result.jobId)
        def jobSummary = getJobProperty("/myJob/jobSteps/$procStartCluster/summary", result.jobId)
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
        if ( testCaseID == TC.C367284) {
            stopCluster(clusterName)
        }
        if (testCaseID in [TC.C367282, TC.C367284]){
            deleteCluster(clusterName)
        }
        if ( testCaseID == TC.C367287) {
            deleteCluster('TestCluster')
        }

        where: 'The following params will be: '
        testCaseID | conf                    | clusterName                  | timeout | status    | expectedSummary         | logs
        TC.C367282 | confignames.correctSOAP | 'StartClusterNegative'       | '60'    | 'error'   | summaries.timeout       | [summaries.timeout]
        TC.C367284 | confignames.correctSOAP | 'StartClusterNegative'       | '5'     | 'error'   | summaries.timeout       | [summaries.timeout]
        TC.C367285 | ''                      | 'StartClusterNegative'       | '300'   | 'error'   | summaries.emptyConfig   | [summaries.emptyConfig]
        TC.C367285 | confignames.correctSOAP | ''                           | '300'   | 'error'   | summaries.emptyCluster  | [summaries.emptyCluster]
        TC.C367286 | confignames.incorrect   | 'StartClusterNegative'       | '300'   | 'error'   | summaries.wrongConfig   | [summaries.wrongConfig]
        TC.C367286 | confignames.correctSOAP | 'wrongCluster'               | '300'   | 'error'   | summaries.wrongCluster  | [summaries.wrongCluster]
        TC.C367287 | confignames.correctSOAP | 'wrongCluster'               | '300'   | 'error'   | summaries.wrongCluster2 | [summaries.wrongCluster2]
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
                wasFirstClusterMemberName          : 'StartClusterServer',
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


}