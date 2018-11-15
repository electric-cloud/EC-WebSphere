package com.electriccloud.plugin.spec

import groovy.json.JsonSlurper
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
        procCreateServer = 'CreateApplicationServer',
        procRunJob = 'RunCustomJob'


    @Shared
    def confignames = [
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
    def membersLists = [
            'oneMember':   nodes.default+":"+'serverClusterMember1',
            'oneMember1':   nodes.default+":"+'serverC366970',
            'someMembers1': nodes.default+":"+'serverC3669711'+','+nodes.default+":"+'serverC3669712',
            'oneMember2':  nodes.default+":"+'serverClusterMember2',
            'twoMembers':  nodes.default+":"+'serverClusterMember01'+','+nodes.default+":"+'serverClusterMember02',
            'oneExtraMember':   nodes.default+":"+'serverClusterMember1'+','+nodes.default+":"+'serverClusterMember2',
            'firstMember': nodes.default+":"+"${procRemoveClusterMembers}FirstClusterMember",
    ]

    @Shared
    def creationPolicy = [
            existing: "existing",
            template: "template",
            convert: "convert",
            wrong: "wrong",
    ]

    @Shared
    def promotionPolicy = [
            both: 'both',
            server: 'server',
            cluster: 'cluster',
            wrong: "wrong",
    ]


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
            (procCreateFirstClusterMember): [
                    configname: '',
                    wasClusterName: '',
                    wasFirstClusterMemberCreationPolicy: '',
                    wasFirstClusterMemberGenUniquePorts: '',
                    wasFirstClusterMemberName: '',
                    wasFirstClusterMemberNode: '',
                    wasFirstClusterMemberTemplateName: '',
                    wasFirstClusterMemberWeight: '',
                    wasServerResourcesPromotionPolicy: '',
                    wasSourceServerName: '',
                    wasSyncNodes: '',
            ],
            (procListClusterMembers): [
                    configName: '',
                    wasClusterName: '',
                    wasOutputPropertyPath: '',
            ],
            (procCreateClusterMembers): [
                    configname : '',
                    wasClusterMembersGenUniquePorts : '',
                    wasClusterMembersList : '',
                    wasClusterMemberWeight : '',
                    wasClusterName : '',
                    wasSyncNodes : '',
            ],
            (procRemoveClusterMembers): [
                    configName : '',
                    wasClusterMembers : '',
                    wasClusterName : '',
                    wasSyncNodes : '',
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
            (procCreateServer): [
                    configname: '',
                    wasAppServerName: '',
                    wasGenUniquePorts: '',
                    wasNodeName: '',
                    wasSourceServerName: '',
                    wasSourceType: '',
                    wasSyncNodes: '',
                    wasTemplateLocation: '',
                    wasTemplateName: '',
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
                                                            params  : procedureParameters[procName]
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
        tmp = [x.encode("ascii").replace("\\\\r", "") for x in info.split("\\\\n")]
        server_dict = {}
        for x in tmp:
            server_dict[x[1:-1].split(" ")[0]] = x[1:-1].split(" ")[1]
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


    def getServerPorts(def serverName){
        // procedure return array of server ports
        def jythonScript = "print \\'STARTLINE\\'; print AdminTask.listServerPorts(\\'${serverName}\\', \\'[-nodeName ${nodes.default}]\\')"

        def scriptParams = [
                configname: confignames.correctSOAP,
                scriptfile: jythonScript,
                scriptfilesource: 'newscriptfile',
        ]
        def scriptResult = runProcedure(scriptParams, procRunJob)
        def scriptLog = getJobLogs(scriptResult.jobId)
        def serversInfo = scriptLog.split("STARTLINE\n")[1].split("\n")
        def ports = []
        for (server in serversInfo){
            ports.add(server.split("port ")[1].split("]")[0])
        }
        return ports
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

    def stopCluster(def clusterName){
        def runParams = [
                configName: confignames.correctSOAP,
                wasClusterName: clusterName,
                wasTimeout: '60',
                wasRippleStart: '0'
        ]
        runProcedure(runParams, procStopCluster)
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

    def createCluster(def clusterName, def emptyCluster = false, def additionalServers = false, def listMembers=''){
        def params = [
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
        if (!emptyCluster){
            params.wasCreateFirstClusterMember        = '1'
            params.wasFirstClusterMemberCreationPolicy= 'template'
            params.wasFirstClusterMemberName          = mainProcedure+'FirstClusterMember'
            params.wasFirstClusterMemberNode          = nodes.default
            params.wasFirstClusterMemberTemplateName  = 'default'
            params.wasServerResourcesPromotionPolicy  = 'both'
        }
        if (additionalServers){
            params.wasAddClusterMembers                = '1'
            params.wasClusterMembersList               = listMembers
            params.wasCreateFirstClusterMember         = '1'
            params.wasFirstClusterMemberCreationPolicy = 'template'
            params.wasFirstClusterMemberName           = mainProcedure+'FirstClusterMember'
            params.wasFirstClusterMemberNode           = nodes.default
            params.wasFirstClusterMemberTemplateName   = 'default'
            params.wasServerResourcesPromotionPolicy   = 'both'
        }
        runProcedure(params, procCreateCluster)
    }

    def createEmptyCluster(clusterName){
        createCluster(clusterName, true, false)
    }

    def createClusterWithAdditionalServers(clusterName, listMembers=''){
        createCluster(clusterName, false, true, listMembers)
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

    def deleteServer(def clusterName){
        def deleteParams = [
                configname: confignames.correctSOAP,
                wasClusterName: clusterName,
                wasSyncNodes: 1,
        ]
        runProcedure(deleteParams, procDeleteCluster)
    }

    def runProcedure(def parameters, def procedureName=mainProcedure) {
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