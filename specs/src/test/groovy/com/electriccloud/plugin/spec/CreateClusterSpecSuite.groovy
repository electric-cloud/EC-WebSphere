package com.electriccloud.plugin.spec

import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import com.electriccloud.plugin.spec.PluginTestHelper
import groovy.json.JsonSlurper


@Requires({ System.getenv('IS_WAS_ND') == "1"})
class CreateClusterSpecSuite extends WebSphereClusterHelper {

    @Shared
    def mainProcedure = procCreateCluster

    @Shared
    def projectName = "EC-WebSphere Specs $mainProcedure Project"

    @Shared
    def TC = [
            C366925: [ ids: 'C366925, C366934, C366979', description: 'create empty cluster'],
            C366932: [ ids: 'C366932', description: 'prefer local - false '],
            C366946: [ ids: 'C366946', description: 'creationPolicy: existing, create cluster with server, source: existing server'],
            C366947: [ ids: 'C366947', description: 'creationPolicy: template, create cluster with server, source: default template'],
            C366949: [ ids: 'C366949', description: 'creationPolicy: convert, create cluster with server, source: add existing server'],
            C366955: [ ids: 'C366955', description: 'First Cluster Member Weight. creationPolicy: existing'],
            C366956: [ ids: 'C366956', description: 'UniquePorts - true'],
            C366957: [ ids: 'C366957', description: 'UniquePorts - false'],
            C366965: [ ids: 'C366965', description: 'Server Resources Promotion Policy: both; source: existing server'],
            C366966: [ ids: 'C366966', description: 'Server Resources Promotion Policy: cluster; source: existing server'],
            C366967: [ ids: 'C366967', description: 'Server Resources Promotion Policy: server; source: existing server'],
            C366970: [ ids: 'C366970', description: 'add cluster member '],
            C366973: [ ids: 'C366973', description: 'add cluster members'],
            C366974: [ ids: 'C366974', description: 'add cluster member'],
            C366975: [ ids: 'C366975', description: 'add cluster members'],
            C366976: [ ids: 'C366976', description: 'add cluster member, wasClusterMembersGenUniquePorts - 1'],
            C366977: [ ids: 'C366977', description: 'add cluster member, wasClusterMembersGenUniquePorts - 0'],
            C366978: [ ids: 'C366978', description: 'add cluster member, wasClusterMemberWeight'],
            C366980: [ ids: 'C366980', description: 'sync nodes - 0'],
            C367231: [ ids: 'C367231', description: 'empty required fields'],
            C367232: [ ids: 'C367232', description: 'wrong values'],
            C367233: [ ids: 'C367233', description: 'already exists'],
    ]

    def doSetupSpec() {
        def wasResourceName = wasHost
        createWorkspace(wasResourceName)
        createConfiguration(confignames.correctSOAP, [doNotRecreate: false])
        importProcedure(projectName, wasResourceName, procCreateCluster)
        importProcedure(projectName, wasResourceName, procDeleteCluster)
        importProcedure(projectName, wasResourceName, procCreateServer)
        importProcedure(projectName, wasResourceName, procStartCluster)
        importProcedure(projectName, wasResourceName, procStopCluster)
        importProcedure(projectName, wasResourceName, procRunJob)
        dsl 'setProperty(propertyName: "/plugins/EC-WebSphere/project/ec_debug_logToProperty", value: "/myJob/debug_logs")'
    }

    @Shared
    def summaries = [
            'default': 'Cluster CLUSTERNAME has been created\n',
            'serverSource': "First cluster member SERVERNAME1 has been created on node NODENAME using server SERVERNAME2 on node NODENAME as source\n",
            'templateSource': "First cluster member SERVERNAME1 has been created on node NODENAME from template default\n",
            'convertSource': "Server convertServer on node NODENAME has been converted to be the first member of cluster CLUSTERNAME\n",
            'addMember': "Server serverC366970 on node NODENAME has been created and added as cluster member\n",
            'addMembers': 'Server serverC366971(1|2) on node NODENAME has been created and added as cluster member\n' +
                    'Server serverC366971(1|2) on node NODENAME has been created and added as cluster member\n',
            'emptyConfig': "Configuration '' doesn't exist",
            'emptyClusterName': "Failed to create a cluster.\n" +
                    "Exception: ADMF0002E: Required parameter clusterName is not found for command clusterConfig.\n",
            'emptyPolicy': "Failed to create a cluster.\n" +
                    "Error: Creation Policy is mandatory when create 1st cluster member is chosen\n",
            'emptyTemplate': "Failed to create a cluster.\n" +
                    "Error: First Server Template Name is mandatory when Creation Policy is set to template\n",
            'emptyNameAndNode': "Failed to create a cluster.\n" +
                    "Error: First Member Name and First Member Node should be provided when create 1st cluster member is chosen\n",
            'emptySource': "Failed to create a cluster.\n" +
                    "Error: Expected nodename:servername, got \n",
            'emptySource2': "Failed to create a cluster.\n" +
                    "Error: Source Server Name is mandatory when Creation Policy is set to existing\n",
            'emptyMember': "Failed to create a cluster.\n" +
                    "Error: No members to add.\n",
            'wrongConfig': "Configuration '${confignames.incorrect}' doesn't exist",
            'wrongPolicy': "Failed to create a cluster.\n" +
                    "Error: Creation policy should be one of: existing, convert or template. Got wrong\n",
            'wrongTemplate': "Failed to create a cluster.\n" +
                    "Exception: ADMG9223E: Cannot find server template wrong.\n",
            'wrongNode': "Failed to create a cluster.\n" +
                    "Exception: ADMG9249E: Exception caught validating the memberConfig step of the createClusterMember task command: com.ibm.websphere.management.cmdframework.CommandValidationException: ADMG9218E: Cannot find node wrongNode.\n",
            'wrongSourceFormat': "Failed to create a cluster.\n" +
                    "Error: Expected nodename:servername, got wrongFormat\n",
            'wrongSourceFormat2': "Failed to create a cluster.\n" +
                    "Exception: 1\n" +
                    "Error: Expected nodename:servername, got wrongFormat\n",
            'wrongFormat': "Failed to create a cluster.\n" +
                    "Error: Expected nodename:servername, got wrongFormat\n",
            'wrongMember': "Failed to create a cluster.\n" +
                    "Exception: Expected nodename:servername record, got wrongFormat\n" +
                    "Error: Expected nodename:servername record, got wrongFormat\n",
            'wrongWeigth': "Failed to create a cluster.\n" +
                    "Exception: com.ibm.ws.scripting.ScriptingException: java.lang.NumberFormatException: For input string: \"wrong\"\n",
            'clusterExists': "Failed to create a cluster.\n" +
                    "Exception: ADMG9200E: Cluster CLUSTERNAME already exists.\n"

    ]

    @Shared
    def jobLogs = [
            serverSource:    ['Creating first member from template or existing server', "'-templateServerName': 'server1'"],
            templateSource:  ["Creating first member from template or existing server", "'-templateName': 'default'"],
            convertSource:   ["Creating first member from template or existing server", "-convertServer", "-serverName 'convertServer'"],
            weight:          ["'-memberWeight': '5'"],
            memberWeight:    ["'-memberWeight': '6'"],
            uniquePorts:     ["'-genUniquePorts': 'true'"],
            nonUniquePorts:  ["'-genUniquePorts': 'false'"],
            resScopeBoth:    ["'-resourcesScope': 'both'"],
            resScopeServer:  ["'-resourcesScope': 'server'"],
            resScopeCluster: ["'-resourcesScope': 'cluster'"],
            syncNodes:       ["Synchronizing configuration repository with nodes now.", "The following nodes have been synchronized: NODENAME"]
    ]

    def doCleanupSpec() {
    }

    @IgnoreRest
    @Unroll
    def "Create Cluster - Positive #testCaseID.ids #testCaseID.description"(){
        def testNumber = specificationContext.currentIteration.parent.iterationNameProvider.iterationCount
        clusterName += testNumber
        if (!firstMemberName.isEmpty()){
            firstMemberName += testNumber
        }

        given: "Create a server , if it's needed"
        if (firstMemberCreationPolicy == creationPolicy.convert) {
            createServer(servers.convert)
        }
        and: "Parameters for procedure"
        def runParams = [
                configname: conf,
                wasAddClusterMembers: addMembers,
                wasClusterMembersGenUniquePorts: membersUniquePorts,
                wasClusterMembersList: membersList,
                wasClusterMemberWeight: memberWeight,
                wasClusterName: clusterName,
                wasCreateFirstClusterMember: createFirstMember,
                wasFirstClusterMemberCreationPolicy: firstMemberCreationPolicy,
                wasFirstClusterMemberGenUniquePorts:  firstMemberGenUniquePorts,
                wasFirstClusterMemberName: firstMemberName,
                wasFirstClusterMemberNode: clusterMemberNode,
                wasFirstClusterMemberTemplateName: clusterMemberTemplateName,
                wasFirstClusterMemberWeight: firstMemberWeight,
                wasPreferLocal: preferLocal,
                wasServerResourcesPromotionPolicy: serverResourcesPromotionPolicy,
                wasSourceServerName: sourceServerName,
                wasSyncNodes: syncNodes,
        ]

        when: "Run procedure and wait until job is completed"
        def result = runProcedure(runParams)

        then: "Get and compare results"
        def outcome = getJobProperty('/myJob/outcome', result.jobId)
        def jobSummary = getJobProperty("/myJob/jobSteps/$procCreateCluster/summary", result.jobId)
        def debugLog = getJobLogs(result.jobId)

        def clusterInfo = getClusterBaseInfo()

        def portsOfMember, portsOfFirstMember, portsOfDefaultServer, diff1, diff2
        if (testCaseID in [TC.C366956, TC.C366957]) {
            portsOfFirstMember = getServerPorts(firstMemberName)
            portsOfDefaultServer = getServerPorts(servers.default)
        }
        if (testCaseID in [TC.C366976, TC.C366977]) {
            portsOfMember = getServerPorts(membersList.split(":")[1])
            portsOfFirstMember = getServerPorts(firstMemberName)
            portsOfDefaultServer = getServerPorts(servers.default)
        }
        verifyAll {
            outcome == status
            jobSummary ==~ expectedSummary.
                    replace('CLUSTERNAME', clusterName).
                    replace('SERVERNAME1', firstMemberName).
                    replace('SERVERNAME2', sourceServerName.isEmpty() ? "" : sourceServerName.split(":")[1]).
                    replace('NODENAME', clusterMemberNode.isEmpty() ? nodes.default : clusterMemberNode)
            for (log in logs){
                debugLog =~ log.
                        replace('CLUSTERNAME', clusterName).
                        replace('SERVERNAME1', firstMemberName).
                        replace('SERVERNAME2', sourceServerName.isEmpty() ? "" : sourceServerName.split(":")[1]).
                        replace('NODENAME', clusterMemberNode.isEmpty() ? nodes.default : clusterMemberNode)
            }
            if (testCaseID in [TC.C366956]){
                portsOfFirstMember - portsOfDefaultServer != []
            }
            if (testCaseID in [TC.C366957]){
                portsOfFirstMember - portsOfDefaultServer == []
            }
            if (testCaseID in [TC.C366976]){
                (portsOfMember - portsOfFirstMember != []) && (portsOfMember - portsOfDefaultServer != [])
            }
            if (testCaseID in [TC.C366977]){
                (portsOfMember - portsOfFirstMember == []) || (portsOfMember - portsOfDefaultServer == [])
            }
            clusterInfo[clusterName].name == clusterName
            clusterInfo[clusterName].preferLocal == (preferLocal == '1') ? 'true' : 'false'

            if (!firstMemberName.isEmpty()){
                clusterInfo[clusterName].servers.any {(it.memberName == firstMemberName)}
            }

            if (!memberWeight.isEmpty()){
                clusterInfo[clusterName].servers.any {(it.weight == memberWeight)}
            }

            if (!membersList.isEmpty()){
                for (server in membersList.split(",").collect { it.split(":")[1]}){
                    clusterInfo[clusterName].servers.any {(it.memberName == server)}
                }
            }

        }

        cleanup: "delete cluster"
        deleteServer(clusterName)

        where: 'The following params will be: '
        testCaseID | conf                    | addMembers | membersUniquePorts | membersList               | memberWeight | clusterName   | createFirstMember | firstMemberCreationPolicy | firstMemberGenUniquePorts | firstMemberName | clusterMemberNode | clusterMemberTemplateName | firstMemberWeight | preferLocal  | serverResourcesPromotionPolicy | sourceServerName                   | syncNodes | status    | expectedSummary                                                  | logs
        TC.C366925 | confignames.correctSOAP | '0'        | '1'                | ''                        | ''           | 'testCluster' | '0'               | ''                        | '1'                       | ''              | ''                | ''                        | ''                | '1'          | promotionPolicy.both           | ''                                 | '1'       | "success" | summaries.default                                                | [summaries.default]+jobLogs.syncNodes
        TC.C366980 | confignames.correctSOAP | '0'        | '1'                | ''                        | ''           | 'testCluster' | '0'               | ''                        | '1'                       | ''              | ''                | ''                        | ''                | '1'          | promotionPolicy.both           | ''                                 | '0'       | "success" | summaries.default                                                | [summaries.default]
        TC.C366932 | confignames.correctSOAP | '0'        | '1'                | ''                        | ''           | 'testCluster' | '0'               | ''                        | '1'                       | ''              | ''                | ''                        | ''                | '0'          | promotionPolicy.both           | ''                                 | '1'       | "success" | summaries.default                                                | [summaries.default]
        TC.C366946 | confignames.correctSOAP | '0'        | '1'                | ''                        | ''           | 'testCluster' | '1'               | creationPolicy.existing   | '1'                       | 'clusterServer' | nodes.default     | ''                        | ''                | '1'          | promotionPolicy.both           | nodes.default+":"+servers.default  | '1'       | "success" | summaries.default+summaries.serverSource                         | [summaries.default]+[summaries.serverSource]+jobLogs.serverSource
        TC.C366947 | confignames.correctSOAP | '0'        | '1'                | ''                        | ''           | 'testCluster' | '1'               | creationPolicy.template   | '1'                       | 'clusterServer' | nodes.default     | 'default'                 | ''                | '1'          | promotionPolicy.both           | ''                                 | '1'       | "success" | summaries.default+summaries.templateSource                       | [summaries.default]+[summaries.templateSource]+jobLogs.templateSource
        TC.C366949 | confignames.correctSOAP | '0'        | '1'                | ''                        | ''           | 'testCluster' | '1'               | creationPolicy.convert    | '1'                       | ''              | ''                | ''                        | ''                | '1'          | promotionPolicy.both           | nodes.default+":"+servers.convert  | '1'       | "success" | summaries.default+summaries.convertSource                        | [summaries.default]+[summaries.convertSource]+jobLogs.convertSource
        TC.C366955 | confignames.correctSOAP | '0'        | '1'                | ''                        | '5'          | 'testCluster' | '1'               | creationPolicy.existing   | '1'                       | 'clusterServer' | nodes.default     | ''                        | '5'               | '1'          | promotionPolicy.both           | nodes.default+":"+servers.default  | '1'       | "success" | summaries.default+summaries.serverSource                         | [summaries.default]+[summaries.serverSource]+jobLogs.serverSource+jobLogs.weight
        TC.C366956 | confignames.correctSOAP | '0'        | '1'                | ''                        | ''           | 'testCluster' | '1'               | creationPolicy.existing   | '1'                       | 'clusterServer' | nodes.default     | ''                        | ''                | '1'          | promotionPolicy.both           | nodes.default+":"+servers.default  | '1'       | "success" | summaries.default+summaries.serverSource                         | [summaries.default]+[summaries.serverSource]+jobLogs.serverSource+jobLogs.uniquePorts
        TC.C366957 | confignames.correctSOAP | '0'        | '1'                | ''                        | ''           | 'testCluster' | '1'               | creationPolicy.existing   | '0'                       | 'clusterServer' | nodes.default     | ''                        | ''                | '1'          | promotionPolicy.both           | nodes.default+":"+servers.default  | '1'       | "success" | summaries.default+summaries.serverSource                         | [summaries.default]+[summaries.serverSource]+jobLogs.serverSource+jobLogs.nonUniquePorts
        TC.C366965 | confignames.correctSOAP | '0'        | '1'                | ''                        | ''           | 'testCluster' | '1'               | creationPolicy.template   | '1'                       | 'clusterServer' | nodes.default     | 'default'                 | ''                | '1'          | promotionPolicy.both           | ''                                 | '1'       | "success" | summaries.default+summaries.templateSource                       | [summaries.default]+[summaries.templateSource]+jobLogs.templateSource+jobLogs.resScopeBoth
        TC.C366966 | confignames.correctSOAP | '0'        | '1'                | ''                        | ''           | 'testCluster' | '1'               | creationPolicy.template   | '1'                       | 'clusterServer' | nodes.default     | 'default'                 | ''                | '1'          | promotionPolicy.cluster        | ''                                 | '1'       | "success" | summaries.default+summaries.templateSource                       | [summaries.default]+[summaries.templateSource]+jobLogs.templateSource+jobLogs.resScopeCluster
        TC.C366967 | confignames.correctSOAP | '0'        | '1'                | ''                        | ''           | 'testCluster' | '1'               | creationPolicy.template   | '1'                       | 'clusterServer' | nodes.default     | 'default'                 | ''                | '1'          | promotionPolicy.server         | ''                                 | '1'       | "success" | summaries.default+summaries.templateSource                       | [summaries.default]+[summaries.templateSource]+jobLogs.templateSource+jobLogs.resScopeServer
        TC.C366970 | confignames.correctSOAP | '1'        | '1'                | membersLists.oneMember1   | ''           | 'testCluster' | '0'               | ''                        | '1'                       | ''              | ''                | ''                        | ''                | '1'          | promotionPolicy.both           | ''                                 | '1'       | "success" | summaries.default+summaries.addMember                            | [summaries.default]
        TC.C366973 | confignames.correctSOAP | '1'        | '1'                | membersLists.someMembers1 | ''           | 'testCluster' | '0'               | ''                        | '1'                       | ''              | ''                | ''                        | ''                | '1'          | promotionPolicy.both           | ''                                 | '1'       | "success" | summaries.default+summaries.addMembers                           | [summaries.default]
        TC.C366974 | confignames.correctSOAP | '1'        | '1'                | membersLists.oneMember1   | ''           | 'testCluster' | '1'               | creationPolicy.template   | '1'                       | 'clusterServer' | nodes.default     | 'default'                 | ''                | '1'          | promotionPolicy.both           | ''                                 | '1'       | "success" | summaries.default+summaries.templateSource+summaries.addMember   | [summaries.default]
        TC.C366975 | confignames.correctSOAP | '1'        | '1'                | membersLists.someMembers1 | ''           | 'testCluster' | '1'               | creationPolicy.template   | '1'                       | 'clusterServer' | nodes.default     | 'default'                 | ''                | '1'          | promotionPolicy.both           | ''                                 | '1'       | "success" | summaries.default+summaries.templateSource+summaries.addMembers  | [summaries.default]
        TC.C366976 | confignames.correctSOAP | '1'        | '1'                | membersLists.oneMember1   | ''           | 'testCluster' | '1'               | creationPolicy.template   | '1'                       | 'clusterServer' | nodes.default     | 'default'                 | ''                | '1'          | promotionPolicy.both           | ''                                 | '1'       | "success" | summaries.default+summaries.templateSource+summaries.addMember   | [summaries.default]
        TC.C366977 | confignames.correctSOAP | '1'        | '0'                | membersLists.oneMember1   | ''           | 'testCluster' | '1'               | creationPolicy.template   | '1'                       | 'clusterServer' | nodes.default     | 'default'                 | ''                | '1'          | promotionPolicy.both           | ''                                 | '1'       | "success" | summaries.default+summaries.templateSource+summaries.addMember   | [summaries.default]
        TC.C366978 | confignames.correctSOAP | '1'        | '1'                | membersLists.oneMember1   | '6'          | 'testCluster' | '1'               | creationPolicy.template   | '1'                       | 'clusterServer' | nodes.default     | 'default'                 | ''                | '1'          | promotionPolicy.both           | ''                                 | '1'       | "success" | summaries.default+summaries.templateSource+summaries.addMember   | [summaries.default]+jobLogs.memberWeight
    }

    @Unroll
    def "Create Cluster - Negative, #testCaseID.ids #testCaseID.description"() {
        given: "Parameters for procedure"
        def runParams = [
                configname                         : conf,
                wasAddClusterMembers               : addMembers,
                wasClusterMembersGenUniquePorts    : membersUniquePorts,
                wasClusterMembersList              : membersList,
                wasClusterMemberWeight             : memberWeight,
                wasClusterName                     : clusterName,
                wasCreateFirstClusterMember        : createFirstMember,
                wasFirstClusterMemberCreationPolicy: firstMemberCreationPolicy,
                wasFirstClusterMemberGenUniquePorts: firstMemberGenUniquePorts,
                wasFirstClusterMemberName          : firstMemberName,
                wasFirstClusterMemberNode          : clusterMemberNode,
                wasFirstClusterMemberTemplateName  : clusterMemberTemplateName,
                wasFirstClusterMemberWeight        : firstMemberWeight,
                wasPreferLocal                     : preferLocal,
                wasServerResourcesPromotionPolicy  : serverResourcesPromotionPolicy,
                wasSourceServerName                : sourceServerName,
                wasSyncNodes                       : syncNodes,
        ]

        when: "Run procedure and wait until job is completed"
        def result = runProcedure(runParams)

        then: "Get and compare results"
        def outcome = getJobProperty('/myJob/outcome', result.jobId)
        def jobSummary = getJobProperty("/myJob/jobSteps/$procCreateCluster/summary", result.jobId)
        def debugLog = getJobLogs(result.jobId)

        verifyAll {
            outcome == status
            jobSummary == expectedSummary
            for (log in logs) {
                debugLog =~ log
            }

        }
        where: 'The following params will be: '
        testCaseID | conf                    | addMembers | membersUniquePorts | membersList               | memberWeight | clusterName   | createFirstMember | firstMemberCreationPolicy | firstMemberGenUniquePorts | firstMemberName | clusterMemberNode | clusterMemberTemplateName | firstMemberWeight | preferLocal  | serverResourcesPromotionPolicy | sourceServerName                   | syncNodes | status    | expectedSummary              | logs
        TC.C367231 | ''                      | '0'        | '1'                | ''                        | ''           | 'testCluster' | '0'               | ''                        | '1'                       | ''              | ''                | ''                        | ''                | '1'          | promotionPolicy.both           | ''                                 | '1'       | "error"   | summaries.emptyConfig        | [summaries.emptyConfig]
        TC.C367231 | confignames.correctSOAP | '0'        | '1'                | ''                        | ''           | ''            | '0'               | ''                        | '1'                       | ''              | ''                | ''                        | ''                | '1'          | promotionPolicy.both           | ''                                 | '1'       | "error"   | summaries.emptyClusterName   | [summaries.emptyClusterName]
        TC.C367231 | confignames.correctSOAP | '0'        | '1'                | ''                        | ''           | 'testCluster' | '1'               | ''                        | '1'                       | 'clusterServer' | nodes.default     | 'default'                 | ''                | '1'          | promotionPolicy.both           | ''                                 | '1'       | "error"   | summaries.emptyPolicy        | [summaries.emptyPolicy]
        TC.C367231 | confignames.correctSOAP | '0'        | '1'                | ''                        | ''           | 'testCluster' | '1'               | creationPolicy.template   | '1'                       | 'clusterServer' | nodes.default     | ''                        | ''                | '1'          | promotionPolicy.both           | ''                                 | '1'       | "error"   | summaries.emptyTemplate      | [summaries.emptyTemplate]
        TC.C367231 | confignames.correctSOAP | '0'        | '1'                | ''                        | ''           | 'testCluster' | '1'               | creationPolicy.template   | '1'                       | 'clusterServer' | ''                | 'default'                 | ''                | '1'          | promotionPolicy.both           | ''                                 | '1'       | "error"   | summaries.emptyNameAndNode   | [summaries.emptyNameAndNode]
        TC.C367231 | confignames.correctSOAP | '0'        | '1'                | ''                        | ''           | 'testCluster' | '1'               | creationPolicy.template   | '1'                       | ''              | nodes.default     | 'default'                 | ''                | '1'          | promotionPolicy.both           | ''                                 | '1'       | "error"   | summaries.emptyNameAndNode   | [summaries.emptyNameAndNode]
        TC.C367231 | confignames.correctSOAP | '0'        | '1'                | ''                        | ''           | 'testCluster' | '1'               | creationPolicy.existing   | '1'                       | 'clusterServer' | nodes.default     | ''                        | ''                | '1'          | promotionPolicy.both           | ''                                 | '1'       | "error"   | summaries.emptySource2       | [summaries.emptySource2]
        TC.C367231 | confignames.correctSOAP | '0'        | '1'                | ''                        | ''           | 'testCluster' | '1'               | creationPolicy.convert    | '1'                       | ''              | ''                | ''                        | ''                | '1'          | promotionPolicy.both           | ''                                 | '1'       | "error"   | summaries.emptySource        | [summaries.emptySource]
        TC.C367231 | confignames.correctSOAP | '1'        | '1'                | ''                        | ''           | 'testCluster' | '0'               | ''                        | '1'                       | ''              | ''                | ''                        | ''                | '1'          | promotionPolicy.both           | ''                                 | '1'       | "error"   | summaries.emptyMember        | [summaries.emptyMember]
        TC.C367232 | confignames.incorrect   | '0'        | '1'                | ''                        | ''           | 'testCluster' | '0'               | ''                        | '1'                       | ''              | ''                | ''                        | ''                | '1'          | promotionPolicy.both           | ''                                 | '1'       | "error"   | summaries.wrongConfig        | [summaries.wrongConfig]
        TC.C367232 | confignames.correctSOAP | '0'        | '1'                | ''                        | ''           | 'testCluster' | '1'               | creationPolicy.wrong      | '1'                       | 'clusterServer' | nodes.default     | 'default'                 | ''                | '1'          | promotionPolicy.both           | ''                                 | '1'       | "error"   | summaries.wrongPolicy        | [summaries.wrongPolicy]
        TC.C367232 | confignames.correctSOAP | '0'        | '1'                | ''                        | ''           | 'testCluster' | '1'               | creationPolicy.template   | '1'                       | 'clusterServer' | nodes.default     | 'wrong'                   | ''                | '1'          | promotionPolicy.both           | ''                                 | '1'       | "error"   | summaries.wrongTemplate      | [summaries.wrongTemplate]
        TC.C367232 | confignames.correctSOAP | '0'        | '1'                | ''                        | ''           | 'testCluster' | '1'               | creationPolicy.template   | '1'                       | 'clusterServer' | 'wrongNode'       | 'default'                 | ''                | '1'          | promotionPolicy.both           | ''                                 | '1'       | "error"   | summaries.wrongNode          | [summaries.wrongNode]
        TC.C367232 | confignames.correctSOAP | '0'        | '1'                | ''                        | ''           | 'testCluster' | '1'               | creationPolicy.existing   | '1'                       | 'clusterServer' | nodes.default     | ''                        | ''                | '1'          | promotionPolicy.both           | 'wrongFormat'                      | '1'       | "error"   | summaries.wrongSourceFormat2 | [summaries.wrongSourceFormat2]
        TC.C367232 | confignames.correctSOAP | '0'        | '1'                | ''                        | ''           | 'testCluster' | '1'               | creationPolicy.convert    | '1'                       | ''              | ''                | ''                        | ''                | '1'          | promotionPolicy.both           | 'wrongFormat'                      | '1'       | "error"   | summaries.wrongSourceFormat  | [summaries.wrongSourceFormat]
        TC.C367232 | confignames.correctSOAP | '1'        | '1'                | 'wrongFormat'             | ''           | 'testCluster' | '0'               | ''                        | '1'                       | ''              | ''                | ''                        | ''                | '1'          | promotionPolicy.both           | ''                                 | '1'       | "error"   | summaries.wrongMember        | [summaries.wrongMember]
        TC.C367232 | confignames.correctSOAP | '1'        | '1'                | membersLists.oneMember1   | 'wrong'      | 'testCluster' | '0'               | ''                        | '1'                       | ''              | ''                | ''                        | ''                | '1'          | promotionPolicy.both           | ''                                 | '1'       | "error"   | summaries.wrongWeigth        | [summaries.wrongWeigth]
    }

    @Unroll
    def "Create Cluster - Negative, already exists #testCaseID.ids #testCaseID.description"() {
        given: "Parameters for procedure"
        def runParams = [
                configname                         : conf,
                wasAddClusterMembers               : addMembers,
                wasClusterMembersGenUniquePorts    : membersUniquePorts,
                wasClusterMembersList              : membersList,
                wasClusterMemberWeight             : memberWeight,
                wasClusterName                     : clusterName,
                wasCreateFirstClusterMember        : createFirstMember,
                wasFirstClusterMemberCreationPolicy: firstMemberCreationPolicy,
                wasFirstClusterMemberGenUniquePorts: firstMemberGenUniquePorts,
                wasFirstClusterMemberName          : firstMemberName,
                wasFirstClusterMemberNode          : clusterMemberNode,
                wasFirstClusterMemberTemplateName  : clusterMemberTemplateName,
                wasFirstClusterMemberWeight        : firstMemberWeight,
                wasPreferLocal                     : preferLocal,
                wasServerResourcesPromotionPolicy  : serverResourcesPromotionPolicy,
                wasSourceServerName                : sourceServerName,
                wasSyncNodes                       : syncNodes,
        ]

        when: "Run procedure and wait until job is completed"
        runProcedure(runParams)
        def result = runProcedure(runParams)

        then: "Get and compare results"
        def outcome = getJobProperty('/myJob/outcome', result.jobId)
        def jobSummary = getJobProperty("/myJob/jobSteps/$procCreateCluster/summary", result.jobId)
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
        cleanup: "delete cluster"
        deleteServer(clusterName)

        where: 'The following params will be: '
        testCaseID | conf                    | addMembers | membersUniquePorts | membersList               | memberWeight | clusterName   | createFirstMember | firstMemberCreationPolicy | firstMemberGenUniquePorts | firstMemberName | clusterMemberNode | clusterMemberTemplateName | firstMemberWeight | preferLocal  | serverResourcesPromotionPolicy | sourceServerName                   | syncNodes | status    | expectedSummary              | logs
        TC.C367233 | confignames.correctSOAP | '0'        | '1'                | ''                        | ''           | 'testCluster' | '0'               | ''                        | '1'                       | ''              | ''                | ''                        | ''                | '1'          | ''                             | ''                                 | '1'       | "error"   | summaries.clusterExists      | [summaries.clusterExists]
    }

}