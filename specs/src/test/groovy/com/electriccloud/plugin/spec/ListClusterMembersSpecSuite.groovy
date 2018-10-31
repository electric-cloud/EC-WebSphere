package com.electriccloud.plugin.spec

import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import com.electriccloud.plugin.spec.PluginTestHelper
import groovy.json.JsonSlurper

@Requires({ System.getenv('IS_WAS_ND') == "1"})
class ListClusterMembersSpecSuite extends PluginTestHelper {

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
        procListClusterMembers = 'ListClusterMembers',
        procDeleteCluster = 'DeleteCluster',
        procRunJob = 'RunCustomJob',
        projectName = "EC-WebSphere Specs $procListClusterMembers Project"

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
                    "Server FirstClusterServer on node ${nodes.default}\n",
            'clusterWithAddServers': "Cluster CLUSTERNAME has following members:\n" +
                    "Server FirstClusterServer on node ${nodes.default}\n" +
                    "Server serverClusterMember02 on node ${nodes.default}\n" +
                    "Server serverClusterMember01 on node ${nodes.default}\n",
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
            clusterWithFirstMember: "${nodes.default}:FirstClusterServer",
            clusterWithAddServers:  "${nodes.default}:FirstClusterServer,${nodes.default}:serverClusterMember02,${nodes.default}:serverClusterMember01",
            'emptyCluster': "",
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
                                                            procName: procListClusterMembers,
                                                            params  : [
                                                                    configName: '',
                                                                    wasClusterName: '',
                                                                    wasOutputPropertyPath: '',
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
    def "ListClusterMembers  - Positive #testCaseID.ids #testCaseID.description"() {
        def testNumber = specificationContext.currentIteration.parent.iterationNameProvider.iterationCount
        clusterName += testNumber

        given: "Create cluster with server"
        if (testCaseID in  [TC.C367445, TC.C367448, TC.C367452]){
            createCluster(clusterName)
        }

        if (testCaseID == TC.C367446){
            createClusterWithAdditionalServers(clusterName)
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
            jobSummary == expectedSummary.
                    replace('CLUSTERNAME', clusterName)
            if (testCaseID != TC.C367447) {
                output == expectedOutput
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

    def createClusterWithAdditionalServers(clusterName){
        def clusterWithServer = [
                configname                         : confignames.correctSOAP,
                wasAddClusterMembers               : '1',
                wasClusterMembersGenUniquePorts    : '1',
                wasClusterMembersList              : membersLists.twoMembers,
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

    def runProcedure(def parameters, def procedureName=procListClusterMembers) {
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