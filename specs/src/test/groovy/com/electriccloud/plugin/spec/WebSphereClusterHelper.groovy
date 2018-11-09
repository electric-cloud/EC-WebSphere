package com.electriccloud.plugin.spec

import spock.lang.Shared


class WebSphereClusterHelper extends PluginTestHelper {

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
    def procCreateCluster = 'CreateCluster',
        procCreateClusterMembers = 'CreateClusterMembers',
        procCreateFirstClusterMember = 'CreateFirstClusterMember',
        procDeleteCluster = 'DeleteCluster',
        procRemoveClusterMembers = 'RemoveClusterMembers',
        procListClusterMembers = 'ListClusterMembers',
        procStartCluster = 'StartCluster',
        procStopCluster = 'StopCluster',
        procRunJob = 'RunCustomJob'

    @Shared
    def procedureParameters = [
            (procDeleteCluster): [
                    configname: '',
                    wasClusterName: '',
                    wasSyncNodes: '',
            ],
            (procCreateCluster): [
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
            ],
            (procStartCluster): [
                    configName: '',
                    wasClusterName: '',
                    wasTimeout: '',
            ],
            (procStopCluster):  [
                    configName: '',
                    wasClusterName: '',
                    wasTimeout: '',
                    wasRippleStart: ''
            ],
            (procRunJob):   [
                    configname: '',
                    scriptfile: '',
                    scriptfilesource: '',
            ]
    ]

    def importProcedure(def projectName, def wasResourceName, def procName){
        importProject(projectName, 'dsl/RunProcedure.dsl', [projName: projectName,
                                                            resName : wasResourceName,
                                                            procName: procName,
                                                            params  : procedureParameters.(procName)
        ])
    }

    def getClusterState(def clusterName){
        def cell = wasHost + "Cell01"
        if (is_windows == "1"){
            cell = wasHost.toUpperCase() + "Cell01"
        }
        def script = """\'\'
cluster = AdminControl.completeObjectName('cell=${cell},type=Cluster,name=${clusterName},*')
info = AdminControl.getAttribute(cluster, "state")
print info.encode("ascii").split('.')[2]\'\'"""

        def runParams = [
                configname: confignames.correctSOAP,
                scriptfile: script,
                scriptfilesource: 'newscriptfile',
        ]
        def result = runProcedure(runParams, procRunJob)
        def debugLog = getJobLogs(result.jobId)
        return debugLog.split('\n')[-1]
    }


    def runProcedure(def parameters, def procedureName) {
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