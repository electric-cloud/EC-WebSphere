package com.electriccloud.plugin.spec

import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import com.electriccloud.plugin.spec.PluginTestHelper
import groovy.json.JsonSlurper

@Requires({ System.getenv('IS_WAS_ND') == "1"})
class StopClusterSpecSuite extends PluginTestHelper {

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
    def procCreateCluster = 'CreateCluster',
        procDeleteCluster = 'DeleteCluster',
        procStartCluster = 'StartCluster',
        procStopCluster = 'StopCluster',
        procRunJob = 'RunCustomJob',
        projectName = "EC-WebSphere Specs $procStartCluster Project"

    @Shared
    def TC = [
            C367299: [ ids: 'C367299', description: 'Stop Running cluster'],
            C367302: [ ids: 'C367302', description: 'Stop Running cluster'],
            C367303: [ ids: 'C367303', description: 'RippleStart -True '],
            C367304: [ ids: 'C367304', description: 'Stop stopped cluster -default values'],
            C367305: [ ids: 'C367305', description: 'empty required fields'],
            C367306: [ ids: 'C367306', description: 'not enough time '],
            C367307: [ ids: 'C367307', description: 'wrong values'],
    ]

    @Shared
    def summaries = [
            'default': 'Cluster CLUSTERNAME has been stopped\n',
            'RippleStart': 'Cluster CLUSTERNAME has been restarted using RippleStart\n',
            'timeout': 'Failed to stop cluster.\n' +
                    'Error: Cluster CLUSTERNAME has not been stopped, exited by timeout\n',
            'emptyConfig': "Configuration '' doesn't exist",
            'emptyCluster': "Failed to start cluster .\n" +
                    "Cluster  was not found.\n" +
                    "Tip: Available clusters: <none>\n",
            'wrongConfig': "Configuration '${confignames.incorrect}' doesn't exist",
            'wrongCluster': "Failed to start cluster CLUSTERNAME.\n" +
                    "Cluster CLUSTERNAME was not found.\n" +
                    "Tip: Available clusters: <none>\n",
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

    def doCleanupSpec() {
    }

    @Unroll
    def "StopCluster  - Positive #testCaseID.ids #testCaseID.description"() {
        def testNumber = specificationContext.currentIteration.parent.iterationNameProvider.iterationCount

        given: "Create cluster with server"
        if ( testNumber == 1) {
            createCluster(clusterName)
        }
        if (initialState == 'started') {
            startCluster(clusterName)
        }

        and: "Parameters for procedure"
        def runParams = [
                configName: conf,
                wasClusterName: clusterName,
                wasTimeout: timeout,
                wasRippleStart: rippleStart,
        ]

        when: "Run procedure and wait until job is completed"
        def result = runProcedure(runParams)

        then: "Get and compare results"
        def outcome = getJobProperty('/myJob/outcome', result.jobId)
        def jobSummary = getJobProperty("/myJob/jobSteps/$procStopCluster/summary", result.jobId)
        def debugLog = getJobLogs(result.jobId)
        def clusterState = getClusterState()

        verifyAll {
            outcome == status
            jobSummary == expectedSummary.
                    replace('CLUSTERNAME', clusterName)
            for (log in logs){
                debugLog =~ log.
                        replace('CLUSTERNAME', clusterName)
            }
            clusterState == expectedClusterState
        }

        cleanup:
        if (testNumber == 4){
            deleteCluster(clusterName)
        }

        where: 'The following params will be: '
        testCaseID | conf                    | clusterName          | timeout | rippleStart | expectedClusterState | initialState | status    | expectedSummary       | logs
        TC.C367242 | confignames.correctSOAP | 'StopCluster'        | '300'   | '0'         |  'stopped'           | 'started'    | 'success' | summaries.default     | [summaries.default]
        TC.C367302 | confignames.correctSOAP | 'StopCluster'        | '120'   | '0'         |  'stopped'           | 'started'    | 'success' | summaries.default     | [summaries.default]+jobLogs.timeout
        TC.C367303 | confignames.correctSOAP | 'StopCluster'        | '300'   | '1'         |  'running'           | 'started'    | 'success' | summaries.RippleStart | [summaries.RippleStart]
        TC.C367304 | confignames.correctSOAP | 'StopCluster'        | '300'   | '0'         |  'stopped'           | 'stopped'    | 'success' | summaries.default     | [summaries.default]
    }

    @Unroll
    def "StopCluster  - Negative #testCaseID.ids #testCaseID.description"() {
        def testNumber = specificationContext.currentIteration.parent.iterationNameProvider.iterationCount

        given: "Create cluster with server"
        if (testCaseID == TC.C367306) {
            createCluster(clusterName)
        }
        if (testCaseID == TC.C367306) {
            startCluster(clusterName)
        }

        and: "Parameters for procedure"
        def runParams = [
                configName: conf,
                wasClusterName: clusterName,
                wasTimeout: timeout,
                wasRippleStart: rippleStart,
        ]

        when: "Run procedure and wait until job is completed"
        def result = runProcedure(runParams)

        then: "Get and compare results"
        def outcome = getJobProperty('/myJob/outcome', result.jobId)
        def jobSummary = getJobProperty("/myJob/jobSteps/$procStopCluster/summary", result.jobId)
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
        if (testCaseID == TC.C367306){
            deleteCluster(clusterName)
        }

        where: 'The following params will be: '
        testCaseID | conf                    | clusterName           | timeout | rippleStart | initialState | status    | expectedSummary        | logs
        TC.C367305 | ''                      | 'StopClusterNegative' | '300'   | '0'         | 'stopped'    | 'error'   | summaries.emptyConfig  | [summaries.emptyConfig]
        TC.C367305 | confignames.correctSOAP | ''                    | '300'   | '0'         | 'stopped'    | 'error'   | summaries.emptyCluster | [summaries.emptyCluster]
        TC.C367306 | confignames.correctSOAP | 'StopClusterNegative' | '5'     | '0'         | 'stopped'    | 'error'   | summaries.timeout      | [summaries.timeout]
        TC.C367307 | confignames.incorrect   | 'StopClusterNegative' | '300'   | '0'         | 'stopped'    | 'error'   | summaries.wrongConfig  | [summaries.wrongConfig]
        TC.C367307 | confignames.correctSOAP | 'wrongCluster'        | '300'   | '0'         | 'stopped'    | 'error'   | summaries.wrongCluster | [summaries.wrongCluster]
    }

    def getClusterState(){
        def script = '''\'\'
cluster = AdminControl.completeObjectName('cell=websphere90ndCell01,type=Cluster,name=StopCluster,*')
info = AdminControl.getAttribute(cluster, "state")
print info.encode("ascii").split('.')[2]\'\''''

        def runParams = [
                configname: confignames.correctSOAP,
                scriptfile: script,
                scriptfilesource: 'newscriptfile',
        ]
        def result = runProcedure(runParams, procRunJob)
        def debugLog = getJobLogs(result.jobId)
        return debugLog.split('\n')[-1]
    }

    def startCluster(def clusterName){
        def runParams = [
                configName: confignames.correctSOAP,
                wasClusterName: clusterName,
                wasTimeout: '60',
        ]
        runProcedure(runParams, procStartCluster)
    }

    def deleteCluster(def clusterName){
        def deleteParams = [
                configname: confignames.correctSOAP,
                wasClusterName: clusterName,
                wasSyncNodes: 1,
        ]
        runProcedure(deleteParams, procDeleteCluster)
    }

    def createCluster(def clusterName){
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

    def createEmptyCluster(def clusterName){
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
                wasServerResourcesPromotionPolicy  : 'both',
                wasSourceServerName                : '',
                wasSyncNodes                       : '1',
        ]
        runProcedure(emptyCluster, procCreateCluster)
    }

    def runProcedure(def parameters, def procedureName=procStopCluster) {
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