package com.electriccloud.plugin.spec

import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import com.electriccloud.plugin.spec.PluginTestHelper

@Stepwise
class ImportApplicationServer extends PluginTestHelper {

    // Environments Variables
    @Shared
    def wasUserName = System.getenv('WAS_USERNAME')
    @Shared
    def wasPassword = System.getenv('WAS_PASSWORD')
    @Shared
    def wasHost =     System.getenv('WAS_HOST')
    @Shared
    def wasPort =     System.getenv('WAS_PORT')
    @Shared
    def wasConnType = System.getenv('WAS_CONNTYPE')
    @Shared
    def wasDebug =    System.getenv('WAS_DEBUG')
    @Shared
    def wasPath =     System.getenv('WSADMIN_PATH')
    @Shared
    def wasAppPath =  System.getenv('WAS_APPPATH')

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
    def testCases = [
        C363397: [
            id: 'C363397, C363399',
            description: 'ImportServer - required values, Syncronize Nodes - True'],
        C363400: [
            id: 'C363400',
            description: 'Syncronize Nodes - False'],
        C363401: [
            id: 'C363401',
            description: 'Provide Node name - archive contains only a one Node name'],
        C363434: [
            id: 'C363434',
            description: 'Provide Server name - archive contains only a one Node name, And One Server'],
        C363435: [
            id: 'C363435',
            description: 'Provide Server name - archive contains only a one Node name, And some Servers'],
        C363433: [
            id: 'C363433',
            description: 'Provide Node name - archive contains some Nodes names'],            
        C363436: [
            id: 'C363436',
            description: 'Provide Server name - archive contains some Node name, And some Servers'],
        C363437: [
            id: 'C363437',
            description: 'CoreGroup'],
        C363439: [
            id: 'C363439',
            description: 'Provide Server, Node names and CoreGroup'],
        C363441: [
            id: 'C363441',
            description: 'empty required fields'],
        C363441_2: [
            id: 'C363441',
            description: 'empty required fields'],            
        C363444: [
            id: 'C363444',
            description: 'incorrect Config Name'],                        
        C363445: [
            id: 'C363445',
            description: 'Node doesn`t exist in WebSphere'],            
        C363447: [
            id: 'C363447',
            description: 'Node doesn`t exist in archive'],                        
        C363448: [
            id: 'C363448, C363449',
            description: 'Server doesn`t exist in archive, Server doesn`t exist in archive, Node exists in archive'],                          
        C363451: [
            id: 'C363451',
            description: 'CoreGroup doesn`t exist'],                                             
    ]

    @Shared
    def procName = 'ImportApplicationServer'
    
    @Shared
    def procExportName = 'ExportApplicationServer'

    @Shared
    def procCreateServer = 'CreateApplicationServer'

    @Shared
    def procDeleteServer = 'DeleteApplicationServer'    

    @Shared
    def resourceProcedureName = 'GetResources'

    @Shared
    def projectName = "EC-WebSphere Specs $procName Project"

    @Shared
    // def archiveUrl = "https://github.com/electric-cloud/EC-WebSphere/specs/resource/WebSphereConfig_2018-08-17.zip"
    def archiveUrl = "https://github.com/electric-cloud/EC-WebSphere/blob/ECPAPPSERVERWEBSPHERE-528/specs/resource/WebSphereConfig_2018-08-17.zip?raw=true"

    @Shared
    def servers = [
        'default': 'server1',
        '2': 'server2',
        '3': 'server3',
        'wrong': 'wrong',
        'ivnalid': '?*&erver1!'
    ]

    @Shared
    def nodes = [
        'default': 'websphere90ndNode01',
        '2': 'websphere90ndNode02',
        'wrong': 'wrong',
    ]

    @Shared
    def paths = [
        'default': '/tmp/test/file',
        'backup': '/WebSphereConfig_2018-08-17.zip',
        'wrong': 'E:/tmp/'
    ]

    @Shared
    def coreGroups = [
        'default': 'DefaultCoreGroup',
        'wrong': 'wrong',
    ]

    @Shared
    def summaries = [
        'default': "Application server serverReplace has been imported to node nodeReplace\n",
        'emptyConfig': "Configuration '' doesn't exist",
        'incorrectConfig': "Configuration 'incorrect' doesn't exist", 
        'emptyServer':'Failed to import application server  to node nodeReplace\nException: ADMF0002E: Required parameter name is not found for command createApplicationServer.\n',
        'emptyNode':'Failed to import application server serverReplace to node \nException: ADMF0002E: Required parameter nodeName is not found for command importServer.\n',
        'emptyPath':'Failed to import application server serverReplace to node nodeReplace\nException: ADMF0003E: Invalid parameter value  for parameter archive for command importServer.\n',
        // 'wrongServer': 'Failed to export application server serverReplace from node nodeReplace\nException: ADMB0005E: Server serverReplace on Node nodeReplace does not exist.\n',
        'wrongNode': 'Failed to import application server serverReplace to node nodeReplace\nException: ADMF0003E: Invalid parameter value wrong for parameter nodeName for command importServer.\n',
        'wrongArcNode': 'Failed to import application server serverReplace to node nodeReplace\nException: ADMF0003E: Invalid parameter value wrong for parameter nodeInArchive for command importServer.\n',
        'wrongArcServer': 'Failed to import application server serverReplace to node nodeReplace\nException: ADMF0003E: Invalid parameter value wrong for parameter serverInArchive for command importServer.\n',
        'wrongGroup': 'Failed to import application server serverReplace to node nodeReplace\nException: ADMF0003E: Invalid parameter value wrong for parameter coreGroup for command importServer.\n',
        'wrongServerNodeInArch': "Failed to import application server serverReplace to node nodeReplace\nException: ADMF0003E: Invalid parameter value .* for parameter archive for command importServer.\n",
    ]

    @Shared
    def jobLogs = [
        'default': ["Application server serverReplace has been imported to node nodeReplace"],
        'syncNodes': ["Application server serverReplace has been imported to node nodeReplace", "Synchronizing configuration repository with nodes now.", "The following nodes have been synchronized: websphere90ndNode01"],
        'serverInArc': ["appServerNameInArchive = '''\nserverInArcReplace\n", "if appServerNameInArchive:\n    params.append\\('-serverInArchive'\\)\n    params.append\\(appServerNameInArchive\\)"],
        'coreGroup': ["coreGroup = '''\ncoreGroupReplace\n", "if coreGroup:\n    params.append\\('-coreGroup'\\)\n    params.append\\(coreGroup\\)"],
        'emptyConfig': ["Configuration '' doesn't exist"],
        'incorrectConfig': ["Configuration 'incorrect' doesn't exist"],
        'emptyServer':['Failed to import application server serverReplace to node nodeReplace', 'Required parameter name is not found for command createApplicationServer.'],
        'emptyNode':['Failed to import application server serverReplace to node','Exception: ADMF0002E: Required parameter nodeName is not found for command importServer.'],
        'emptyPath':['Failed to import application server serverReplace to node nodeReplace', 'Exception: ADMF0003E: Invalid parameter value  for parameter archive for command importServer.'],
        // 'wrongServer': ['Server serverReplace on Node nodeReplace does not exist'],
        'wrongNode': ['Invalid parameter value wrong for parameter nodeName for command importServer'],
        'wrongArcNode': ['Invalid parameter value wrong for parameter nodeInArchive for command importServer.'],
        'wrongArcServer': ['Invalid parameter value wrong for parameter serverInArchive for command importServer.'],
        'wrongGroup': ['Invalid parameter value wrong for parameter coreGroup for command importServer'], 
        'wrongServerNodeInArch': ['Failed to import application server serverReplace to node nodeReplace'],
    ]

    def doSetupSpec() {
        def wasResourceName = wasHost
        createWorkspace(wasResourceName)
        createConfiguration(confignames.correctSOAP, [doNotRecreate: false])
        runGetResourcesProcedure([
                                    projectName: projectName,
                                    resourceProcedureName: resourceProcedureName,
                                    filePath: paths.backup,
                                    fileURL: archiveUrl,
                                    wasResourceName: wasResourceName])

        importProject(projectName, 'dsl/RunProcedure.dsl', [projName: projectName,
            resName : wasResourceName,
            procName: procName,
            params  : [
                configname:  '',
                wasAppServerName: '',
                wasAppServerNameInArchive: '',
                wasArchivePath: '',
                wasCoreGroup: '',
                wasNodeName:  '',
                wasNodeNameInArchive: '',
                wasSyncNodes: '',
            ]
        ])

        importProject(projectName, 'dsl/RunProcedure.dsl', [projName: projectName,
            resName : wasResourceName,
            procName: procDeleteServer,
            params  : [
                    configname: '',
                    wasAppServerName: '',
                    wasNodeName: '',
                    wasSyncNodes: '',
            ]
        ])                


        importProject(projectName, 'dsl/RunProcedure.dsl', [projName: projectName,
            resName : wasResourceName,
            procName: procCreateServer,
            params  : [
                configname: '',
                wasAppServerName: '',
                wasGenUniquePorts: '',
                wasNodeName: '',
                wasSourceServerName: '',
                wasSourceType: '',
                wasSyncNodes: '',
                wasTemplateLocation: '',
                wasTemplateName: '',
            ]
        ])        

        importProject(projectName, 'dsl/RunProcedure.dsl', [projName: projectName,
            resName : wasResourceName,
            procName: procExportName,
            params  : [
                configname:  '',
                wasAppServerName: '',
                wasArchivePath: '',
                wasNodeName:  '',
            ]
        ])

        importProject(projectName, 'dsl/RunCustomShellCommand/RunCustomShellCommand.dsl', [projectName: projectName,
            wasResourceName : wasResourceName,
            procName: 'runCustomShellCommand',
        ])

        dsl 'setProperty(propertyName: "/plugins/EC-WebSphere/project/ec_debug_logToProperty", value: "/myJob/debug_logs")' 
    }

    def doCleanupSpec() {
    }    

    @Unroll
    def 'ImportApplicationServer - Positive: #testCaseID.id #testCaseID.description'(){
        def numberOfTest = specificationContext.currentIteration.parent.iterationNameProvider.iterationCount
        
        given: "Precondition - Export a server, Run procedure "
        if (exportServer){
            def runParamsForExport = [
                        configname:  configname,
                        wasAppServerName: serverExport,
                        wasArchivePath: arcExport,
                        wasNodeName:  nodeExport,
            ]
            runProcedure(runParamsForExport, procExportName)
        }

        and: "Run procedure ImportApplicationServer"
        def newServer = serverName + numberOfTest
        def runParams = [
                    configname:  configname,
                    wasAppServerName: newServer,
                    wasAppServerNameInArchive: serverNameInArchive,
                    wasArchivePath: arcPath, 
                    wasCoreGroup: coreGroup,
                    wasNodeName:  nodeName,
                    wasNodeNameInArchive: nodeNameInArchive,
                    wasSyncNodes: syncNodes,
        ]
        when: "Run procedure and wait until job is completed"
        def result = runProcedure(runParams)

        then: "Get and compare results"
        def outcome = getJobProperty('/myJob/outcome', result.jobId)
        def jobSummary = getJobProperty("/myJob/jobSteps/$procName/summary", result.jobId)
        def debugLog = getJobLogs(result.jobId)
        assert outcome == status
        assert jobSummary == expectedSummary.replace("serverReplace", newServer).replace("nodeReplace", nodeName)
        for (log in logs){
            assert debugLog =~ log.replace("serverReplace", newServer).replace("serverInArcReplace", serverNameInArchive).replace("nodeReplace", nodeName).replace("coreGroupReplace", coreGroup)
        }
        cleanup:
        deleteAppServer(nodeName, newServer)        
 
        where: 'The following params will be:'
        testCaseID        | configname              | serverName        | serverNameInArchive | arcPath         | coreGroup            | nodeName        | nodeNameInArchive | syncNodes | serverExport       | nodeExport      | arcExport       | status      | expectedSummary     | exportServer | logs                    
        testCases.C363397 | confignames.correctSOAP | servers.'default' | ''                  | paths.'default' | ''                   | nodes.'default' | ''                | '1'       | servers.'default'  | nodes.'default' | paths.'default' | "success"   | summaries.'default' | 1            | jobLogs.'syncNodes'     
        testCases.C363400 | confignames.correctSOAP | servers.'default' | ''                  | paths.'default' | ''                   | nodes.'default' | ''                | '0'       | servers.'default'  | nodes.'default' | paths.'default' | "success"   | summaries.'default' | 0            | jobLogs.'default'       
        testCases.C363401 | confignames.correctSOAP | servers.'default' | ''                  | paths.'default' | ''                   | nodes.'default' | nodes.'default'   | '1'       | servers.'default'  | nodes.'default' | paths.'default' | "success"   | summaries.'default' | 0            | jobLogs.'default'       
        testCases.C363434 | confignames.correctSOAP | servers.'default' | servers.'default'   | paths.'default' | ''                   | nodes.'default' | ''                | '1'       | servers.'default'  | nodes.'default' | paths.'default' | "success"   | summaries.'default' | 0            | jobLogs.'serverInArc'    
        testCases.C363435 | confignames.correctSOAP | servers.'default' | servers.'default'   | paths.'backup'  | ''                   | nodes.'default' | nodes.'default'   | '1'       | servers.'default'  | ''              | paths.'default' | "success"   | summaries.'default' | 0            | jobLogs.'serverInArc'   
        testCases.C363433 | confignames.correctSOAP | servers.'default' | servers.'2'         | paths.'backup'  | ''                   | nodes.'default' | nodes.'default'   | '1'       | servers.'default'  | ''              | paths.'default' | "success"   | summaries.'default' | 0            | jobLogs.'serverInArc'   
        testCases.C363436 | confignames.correctSOAP | servers.'default' | servers.'3'         | paths.'backup'  | ''                   | nodes.'default' | nodes.'2'         | '1'       | servers.'default'  | ''              | paths.'default' | "success"   | summaries.'default' | 0            | jobLogs.'serverInArc'   
        testCases.C363437 | confignames.correctSOAP | servers.'default' | ''                  | paths.'default' | coreGroups.'default' | nodes.'default' | ''                | '1'       | servers.'default'  | nodes.'default' | paths.'default' | "success"   | summaries.'default' | 0            | jobLogs.'coreGroup'     
        testCases.C363439 | confignames.correctSOAP | servers.'default' | servers.'default'   | paths.'backup'  | coreGroups.'default' | nodes.'default' | nodes.'2'         | '1'       | servers.'default'  | nodes.'default' | paths.'default' | "success"   | summaries.'default' | 0            | jobLogs.'default'+jobLogs.'syncNodes'+jobLogs.'serverInArc'+jobLogs.'coreGroup'       
    }

    @Unroll
    def 'ImportApplicationServer - Negative: #testCaseID.id #testCaseID.description'(){
        def numberOfTest = specificationContext.currentIteration.parent.iterationNameProvider.iterationCount
        
        given: "Precondition - Export a server, Run procedure "
        if (exportServer){
            def runParamsForExport = [
                        configname:  configname,
                        wasAppServerName: serverExport,
                        wasArchivePath: arcExport,
                        wasNodeName:  nodeExport,
            ]
            runProcedure(runParamsForExport, procExportName)
        }

        and: "Run procedure ImportApplicationServer"        
        def newServer = serverName == ''  ? serverName : (serverName + numberOfTest)
        def runParams = [
                    configname:  configname,
                    wasAppServerName: newServer,
                    wasAppServerNameInArchive: serverNameInArchive,
                    wasArchivePath: arcPath, 
                    wasCoreGroup: coreGroup,
                    wasNodeName:  nodeName,
                    wasNodeNameInArchive: nodeNameInArchive,
                    wasSyncNodes: syncNodes,
        ]
        when: "Run procedure and wait until job is completed"
        def result = runProcedure(runParams)

        then: "Get and compare results"
        def outcome = getJobProperty('/myJob/outcome', result.jobId)
        def jobSummary = getJobProperty("/myJob/jobSteps/$procName/summary", result.jobId)
        def debugLog = getJobLogs(result.jobId)
        assert outcome == status
        if (testCaseID != testCases.C363441_2){
            assert jobSummary == expectedSummary.replace("serverReplace", newServer).replace("nodeReplace", nodeName)   
        }
        else {
            assert jobSummary =~ expectedSummary.replace("serverReplace", newServer).replace("nodeReplace", nodeName)
        }
        for (log in logs){
            assert debugLog =~ log.replace("serverReplace", newServer).replace("serverInArcReplace", serverNameInArchive).replace("nodeReplace", nodeName)
        }
 
        where: 'The following params will be:'
        testCaseID          | configname              | serverName        | serverNameInArchive | arcPath         | coreGroup          | nodeName        | nodeNameInArchive | syncNodes | serverExport       | nodeExport      | arcExport       | status      | expectedSummary                    | logs                             | exportServer
        testCases.C363441   | ''                      | servers.'default' | ''                  | paths.'default' | ''                 | nodes.'default' | ''                | '1'       | servers.'default'  | nodes.'default' | paths.'default' | "error"     | summaries.'emptyConfig'            | jobLogs.'emptyConfig'            | 0
        testCases.C363441   | confignames.correctSOAP | ''                | ''                  | paths.'default' | ''                 | nodes.'default' | ''                | '1'       | servers.'default'  | nodes.'default' | paths.'default' | "error"     | summaries.'emptyServer'            | jobLogs.'emptyServer'            | 0
        testCases.C363441   | confignames.correctSOAP | servers.'default' | ''                  | ''              | ''                 | nodes.'default' | ''                | '1'       | servers.'default'  | nodes.'default' | paths.'default' | "error"     | summaries.'emptyPath'              | jobLogs.'emptyPath'              | 0
        testCases.C363441_2 | confignames.correctSOAP | servers.'default' | ''                  | paths.'default' | ''                 | ''              | ''                | '1'       | servers.'default'  | nodes.'default' | paths.'default' | "error"     | summaries.'emptyNode'              | jobLogs.'emptyNode'              | 0
        testCases.C363441_2 | confignames.correctSOAP | servers.'default' | ''                  | paths.'backup'  | ''                 | nodes.'default' | nodes.'default'   | '1'       | servers.'default'  | nodes.'default' | paths.'default' | "error"     | summaries.'wrongServerNodeInArch'  | jobLogs.'wrongServerNodeInArch'  | 0
        testCases.C363441   | confignames.correctSOAP | servers.'default' | servers.'default'   | paths.'backup'  | ''                 | nodes.'default' | ''                | '1'       | servers.'default'  | nodes.'default' | paths.'default' | "error"     | summaries.'wrongServerNodeInArch'  | jobLogs.'wrongServerNodeInArch'  | 0
        testCases.C363444   | confignames.incorrect   | servers.'default' | ''                  | paths.'default' | ''                 | nodes.'default' | ''                | '1'       | servers.'default'  | nodes.'default' | paths.'default' | "error"     | summaries.'incorrectConfig'        | jobLogs.'incorrectConfig'        | 0
        testCases.C363445   | confignames.correctSOAP | servers.'default' | ''                  | paths.'default' | ''                 | nodes.'wrong'   | ''                | '1'       | servers.'default'  | nodes.'default' | paths.'default' | "error"     | summaries.'wrongNode'              | jobLogs.'wrongNode'              | 1
        testCases.C363447   | confignames.correctSOAP | servers.'default' | ''                  | paths.'backup'  | ''                 | nodes.'default' | nodes.'wrong'     | '1'       | servers.'default'  | nodes.'default' | paths.'default' | "error"     | summaries.'wrongArcNode'           | jobLogs.'wrongArcNode'           | 0    
        testCases.C363448   | confignames.correctSOAP | servers.'default' | servers.'wrong'     | paths.'backup'  | ''                 | nodes.'default' | nodes.'default'   | '1'       | servers.'default'  | nodes.'default' | paths.'default' | "error"     | summaries.'wrongArcServer'         | jobLogs.'wrongArcServer'         | 0    
        testCases.C363451   | confignames.correctSOAP | servers.'default' | ''                  | paths.'default' | coreGroups.'wrong' | nodes.'default' | ''                | '1'       | servers.'default'  | nodes.'default' | paths.'default' | "error"     | summaries.'wrongGroup'             | jobLogs.'wrongGroup'             | 0
    }    

    def deleteAppServer(node,server){
        def runParams = [
                configname: confignames.correctSOAP,
                wasAppServerName: server,
                wasNodeName: node,
                wasSyncNodes: '1',
        ]
        runProcedure(runParams, procDeleteServer)
    }

    //Run Test Procedure
    def runProcedure(def parameters, def procedureName=procName) {
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
