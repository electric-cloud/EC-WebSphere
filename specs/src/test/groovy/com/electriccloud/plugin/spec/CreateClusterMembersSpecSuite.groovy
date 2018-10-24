package com.electriccloud.plugin.spec

import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import com.electriccloud.plugin.spec.PluginTestHelper
import groovy.json.JsonSlurper

@Requires({ System.getenv('IS_WAS_ND') == "1"})
class CreateClusterMembersSpecSuite extends PluginTestHelper {

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
        procCreateClusterMembers = 'CreateClusterMembers',
        procDeleteCluster = 'DeleteCluster',
        procStartCluster = 'StartCluster',
        procStopCluster = 'StopCluster',
        procRunJob = 'RunCustomJob',
        projectName = "EC-WebSphere Specs $procCreateClusterMembers Project"

    @Shared
    def TC = [
            C367309: [ ids: 'C367309, C367317', description: 'default values - add one server'],
            C367310: [ ids: 'C367310', description: 'default values - add more the one server'],
            C367311: [ ids: 'C367311', description: 'empty cluster - add one server '],
            C367312: [ ids: 'C367312', description: 'empty cluster - add more the one server'],
            C367313: [ ids: 'C367313', description: 'server weight'],
            C367314: [ ids: 'C367313', description: 'uniqure ports - true'],
            C367315: [ ids: 'C367313', description: 'uniqure ports - false'],
            C367316: [ ids: 'C367316', description: 'sync node - false'],
            C367318: [ ids: 'C367318', description: 'add servers - twice'],
            C367319: [ ids: 'C367319', description: 'empty required fields'],
            C367350: [ ids: 'C367350', description: 'wrong values'],
    ]

    @Shared
    def summaries = [
            'default': "Server serverClusterMember1 on node ${nodes.default} has been created and added to CLUSTERNAME cluster\n",
            'twoMembers': "Server serverClusterMember02 on node ${nodes.default} has been created and added to CLUSTERNAME cluster\n"+
            "Server serverClusterMember01 on node ${nodes.default} has been created and added to CLUSTERNAME cluster\n",
            'emptyConfig': "Configuration '' doesn't exist",
            'emptyList': 'Failed to create a cluster members.\n' +
                    'Exception: Expected nodename:servername record, got\n' +
                    'Error: Expected nodename:servername record, got \n',
            'emptyClusterName': "Failed to create a cluster members.\n" +
                    "Exception: 1\n" +
                    "Error: Missing clusterName parameter\n",
            'wrongConfig': "Configuration '${confignames.incorrect}' doesn't exist",
            'wrongCluster': "Failed to create a cluster members.\n" +
                    "Exception: ADMG9216E: Cannot find cluster wrongCluster.\n",
            'wrongFormat': "Failed to create a cluster members.\n" +
                    "Exception: Expected nodename:servername record, got wrongFormat\n" +
                    "Error: Expected nodename:servername record, got wrongFormat\n",
    ]

    @Shared
    def jobLogs = [
            syncNodes: ["Synchronizing configuration repository with nodes now.", "The following nodes have been synchronized: ${nodes.default}"],
    ]

    @Shared
    def membersLists = [
            'oneMember':   nodes.default+":"+'serverClusterMember1',
            'twoMembers':  nodes.default+":"+'serverClusterMember01'+','+nodes.default+":"+'serverClusterMember02',
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

        def startProcedureResult = 'error'
        if (testCaseID in [TC.C367314]){
            startProcedureResult = 'success'
        }

        verifyAll {
            outcome == status
            jobSummary == expectedSummary.
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

            // start first cluster member, if it's ports are unique
            // it will start
            if (testCaseID in [TC.C367314, TC.C367315]) {
                def resultOfStartCluster = startCluster(clusterName)
                assert resultOfStartCluster == startProcedureResult
            }

        }

        cleanup:
        if (testCaseID in [TC.C367314, TC.C367315]){
            stopCluster(clusterName)
        }
        deleteCluster(clusterName)

        where: 'The following params will be: '
        testCaseID | conf                     | uniquePorts | list                     | weight | clusterName      | syncNodes | status    | expectedSummary      | logs
        TC.C367309 | confignames.correctSOAP  | '1'         | membersLists.oneMember   | ''     | 'CreateMembers'  | '1'       | 'success' | summaries.default    | [summaries.default]+jobLogs.syncNodes
        TC.C367316 | confignames.correctSOAP  | '1'         | membersLists.oneMember   | ''     | 'CreateMembers'  | '0'       | 'success' | summaries.default    | [summaries.default]
        TC.C367310 | confignames.correctSOAP  | '1'         | membersLists.twoMembers  | ''     | 'CreateMembers'  | '1'       | 'success' | summaries.twoMembers | [summaries.twoMembers]
        TC.C367311 | confignames.correctSOAP  | '1'         | membersLists.oneMember   | ''     | 'CreateMembers'  | '1'       | 'success' | summaries.default    | [summaries.default]
        TC.C367312 | confignames.correctSOAP  | '1'         | membersLists.twoMembers  | ''     | 'CreateMembers'  | '1'       | 'success' | summaries.twoMembers | [summaries.twoMembers]
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
            jobSummary == expectedSummary.
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

    def runProcedure(def parameters, def procedureName=procCreateClusterMembers) {
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