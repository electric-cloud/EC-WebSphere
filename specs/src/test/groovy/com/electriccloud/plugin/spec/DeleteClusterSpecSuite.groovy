package com.electriccloud.plugin.spec

import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import com.electriccloud.plugin.spec.PluginTestHelper


@Requires({ System.getenv('IS_WAS_ND') == "1"})
class DeleteClusterSpecSuite extends PluginTestHelper {
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
    def servers = [
        'default': 'server1',
    ]

    @Shared
    def nodes = [
        'default': wasHost + 'Node01',
    ]        

    @Shared
    def confignames = [
            /**
             * Required
             */
            empty: '',
            correctSOAP: 'Web-Sphere-SOAP',
            wrongConfig: 'wrongConfig'
    ]

    @Shared
    def TC = [
            C366939: [ ids: 'C366939', description: 'delete empty cluster'],
            C366940: [ ids: 'C366940', description: 'delete cluster with server'],
            C366941: [ ids: 'C366941', description: 'delete running cluster with server '],
            C366942: [ ids: 'C366942', description: 'SyncNodes - 0'],
            C366943: [ ids: 'C366943', description: 'empty required fields'],
    ]

    @Shared
    def summaries = [
            'default': "Cluster CLUSTERNAME has been deleted\n",
            'emptyConfig': "Configuration '' doesn't exist",
            'wrongConfig': "Configuration 'wrongConfig' doesn't exist",
            'wrongCluster': "Failed to delete cluster.\nException: ADMG9216E: Cannot find cluster CLUSTERNAME.\n",
    ]

    @Shared
    def jobLogs = [
        syncNodes: ["Synchronizing configuration repository with nodes now", "The following nodes have been synchronized: websphere90ndNode01", "syncNodes = '''\n1"],
        noSyncNodes: ["syncNodes = '''\n0"],
        wrongCluster: ["Failed to delete cluster", "Exception: ADMG9216E: Cannot find cluster CLUSTERNAME"],        
                ]    

    @Shared
    def procCreateCluster = 'CreateCluster',
        procDeleteCluster = 'DeleteCluster',
        procStartCluster = 'StartCluster',
        procRunJob = 'RunCustomJob',
        projectName = "EC-WebSphere Specs $procDeleteCluster Project"

    def doSetupSpec() {
        def wasResourceName = wasHost
        createWorkspace(wasResourceName)
        createConfiguration(confignames.correctSOAP, [doNotRecreate: false])
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
    def "Delete Cluster - Positive #testCaseID.id #testCaseID.description"(){
        def numberOfTest = specificationContext.currentIteration.parent.iterationNameProvider.iterationCount
        clusterName += numberOfTest

        given: "Create Test Cluster" 
        createCluster(clusterName, clusterType)
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
        TC.C366941 | confignames.correctSOAP | 'DeleteCluster' | '1'       | 'notEmpty'  | 'run'        | 'success' | summaries.default | [summaries.default]          
    }

    @Unroll
    def "Delete Cluster - Negative #testCaseID.name #testCaseID.description"(){
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
        TC.C366943 | confignames.correctSOAP | ''              | '1'       | 'error'   | summaries.wrongCluster | jobLogs.wrongCluster
        TC.C366943 | ''                      | 'DeleteCluster' | '1'       | 'error'   | summaries.emptyConfig  | [summaries.emptyConfig]
        TC.C366945 | confignames.correctSOAP | ''              | '1'       | 'error'   | summaries.wrongCluster | jobLogs.wrongCluster
        TC.C366945 | confignames.wrongConfig | 'DeleteCluster' | '1'       | 'error'   | summaries.wrongConfig  | [summaries.wrongConfig]        
    }

    def createCluster(def clusterName, def clusterType){
        def runEmptyParams = [
            configname: confignames.correctSOAP,
            wasAddClusterMembers: '0',
            wasClusterMembersGenUniquePorts: '1',
            wasClusterName: clusterName,
            wasCreateFirstClusterMember: '0',
            wasFirstClusterMemberGenUniquePorts:  '1',
            wasPreferLocal: '1',
            wasSyncNodes: '1',
        ]
        def runServerParams = [
            configname: confignames.correctSOAP,
            wasAddClusterMembers: '0',
            wasClusterMembersGenUniquePorts: '1',
            wasClusterName: clusterName,
            wasCreateFirstClusterMember: '1',
            wasFirstClusterMemberCreationPolicy: 'existing',
            wasFirstClusterMemberGenUniquePorts:  '1',
            wasFirstClusterMemberName: clusterName + 'Server1',
            wasFirstClusterMemberNode: nodes.default,
            wasPreferLocal: '1',
            wasSourceServerName: nodes.default + ':' + servers.default,
            wasSyncNodes: '1',
            wasServerResourcesPromotionPolicy: 'both',
        ]

        def runParams = runServerParams
        if (clusterType == "empty"){
            runParams = runEmptyParams
        }
        runProcedure(runParams, procCreateCluster)
        assert doesClusterExist(clusterName) == 'true'
    }

    def startCluster(def clusterName) {
        def runParams = [
            configName: confignames.correctSOAP,
            wasClusterName: clusterName,
            wasTimeout: '100',
        ]
        runProcedure(runParams, procStartCluster)
    }

    def doesClusterExist(clusterName){
        def script = "print AdminClusterManagement.checkIfClusterExists(\\'${clusterName}\\')"
        def runParams = [
            configname: confignames.correctSOAP,
            scriptfile: script,
            scriptfilesource: 'newscriptfile',
        ]        
        def result = runProcedure(runParams, procRunJob)
        def debugLog = getJobLogs(result.jobId)
        return debugLog.split('\n')[-1]
    }


    def runProcedure(def parameters, def procedureName=procDeleteCluster) {
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