package com.electriccloud.plugin.spec

import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import com.electriccloud.plugin.spec.PluginTestHelper

class ExportApplicationServer extends PluginTestHelper {

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
        C363382: [
            id: 'C363382, C363844',
            description: 'Export Server, Export Server - file already exists '],
        C363383: [
            id: 'C363383',
            description: 'empty required fields'],
        C363386: [
            id: 'C363386',
            description: 'incorrect Config Name '],
        C363387: [
            id: 'C363387',
            description: 'server doesn`t exist'],                                         
        C363388: [
            id: 'C363388',
            description: 'Node doesn`t exist'],                                
    ]

    @Shared
    def procName = 'ExportApplicationServer'


    @Shared
    def projectName = "EC-WebSphere Specs $procName Project"    

    @Shared
    def servers = [
        'default': 'server1',
        'wrong': 'wrong',
        'ivnalid': '?*&erver1!'
    ]

    @Shared
    def nodes = [
        'default': 'websphere90ndNode01',
        'wrong': 'wrong',
    ]

    @Shared
    def paths = [
        'default': '/tmp/test/',
        'wrong': 'E:/tmp/'
    ]

    @Shared
    def summaries = [  
        'default': "Application server serverReplace from node nodeReplace has been exported\n",
        'emptyConfig': "Configuration '' doesn't exist",
        'incorrectConfig': "Configuration 'incorrect' doesn't exist", 
        'emptyServer':'Failed to export application server  from node nodeReplace\nException: ADMF0002E: Required parameter serverName is not found for command exportServer.\n',
        'emptyNode':'Failed to export application server serverReplace from node \nException: ADMF0002E: Required parameter nodeName is not found for command exportServer.\n',
        'emptyPath':'Failed to export application server serverReplace from node nodeReplace\nException: com.ibm.ws.scripting.ScriptingException: java.io.FileNotFoundException: java.io.FileNotFoundException:  (No such file or directory)\n',
        'wrongServer': 'Failed to export application server serverReplace from node nodeReplace\nException: ADMB0005E: Server serverReplace on Node nodeReplace does not exist.\n',
        'wrongNode': 'Failed to export application server serverReplace from node nodeReplace\nException: ADMB0005E: Server serverReplace on Node nodeReplace does not exist.\n',
    ]

    @Shared
    def jobLogs = [
        'default': ["Application server serverReplace from node nodeReplace has been exported"],
        'emptyConfig': ["Configuration '' doesn't exist"],
        'incorrectConfig': ["Configuration 'incorrect' doesn't exist"],                 
        'emptyServer':['Failed to export application server  from node nodeReplace', 'Required parameter serverName is not found for command exportServer'],
        'emptyNode':['Failed to export application server serverReplace from node','Exception: ADMF0002E: Required parameter nodeName is not found for command exportServer'],
        'emptyPath':['Failed to export application server serverReplace from node nodeReplace', 'com.ibm.ws.scripting.ScriptingException: java.io.FileNotFoundException: java.io.FileNotFoundException'],
        'wrongServer': ['Server serverReplace on Node nodeReplace does not exist'],
        'wrongNode': ['Server serverReplace on Node nodeReplace does not exist'],
    ]

    def doSetupSpec() {
        def wasResourceName = wasHost
        createWorkspace(wasResourceName)
        createConfiguration(confignames.correctSOAP, [doNotRecreate: false])

        importProject(projectName, 'dsl/RunProcedure.dsl', [projName: projectName,
                resName : wasResourceName,
                procName: procName,
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

    @Unroll
    def 'ExportApplicationServer - Positive: #testCaseID.id #testCaseID.description'(){
        def numberOfTest = specificationContext.currentIteration.parent.iterationNameProvider.iterationCount
        def newPath = path + numberOfTest

        given: "Parameters for procedure"
        def runParams = [
                    configname:  configName,
                    wasAppServerName: serverName,
                    wasArchivePath: newPath,
                    wasNodeName:  nodeName,
        ]
        when: "Run procedure and wait until job is completed"
        def result = runProcedure(runParams)

        then: "Get and compare results"
        def outcome = getJobProperty('/myJob/outcome', result.jobId)
        def jobSummary = getJobProperty("/myJob/jobSteps/$procName/summary", result.jobId)
        def debugLog = getJobLogs(result.jobId)
        assert outcome == status
        assert jobSummary == expectedSummary.replace("serverReplace", serverName).replace("nodeReplace", nodeName)
        for (log in logs){
            assert debugLog =~ log.replace("serverReplace", serverName).replace("nodeReplace", nodeName)
        }
 
        then: "does file exist on file system?"
        def command = 'ls '+ newPath
        def outcome2 = getJobProperty('/myJob/outcome', runCliCommand(command, wasHost).jobId)
        assert outcome2 == status


        where: 'The following params will be:'
        testCaseID         | configName              | serverName        | nodeName        | path               | status      | expectedSummary     | logs 
        testCases.C363382  | confignames.correctSOAP | servers.'default' | nodes.'default' | paths.'default'    | "success"   | summaries.'default' | jobLogs.'default'
        testCases.C363382  | confignames.correctSOAP | servers.'default' | nodes.'default' | paths.'default'    | "success"   | summaries.'default' | jobLogs.'default'
    }

    @Unroll
    def 'ExportApplicationServer - Negative: #testCaseID.id #testCaseID.description'(){
        given: "Parameters for procedure"
        path = (path != '') ? path + testCaseID.id : path
        def runParams = [
                    configname:  configName,
                    wasAppServerName: serverName,
                    wasArchivePath: path,
                    wasNodeName:  nodeName,
        ]
        when: "Run procedure and wait until job is completed"
        def result = runProcedure(runParams)

        then: "Get and compare results"
        def outcome = getJobProperty('/myJob/outcome', result.jobId)
        def jobSummary = getJobProperty("/myJob/jobSteps/$procName/summary", result.jobId)
        def debugLog = getJobLogs(result.jobId)
        assert outcome == status
        assert jobSummary == expectedSummary.replace("serverReplace", serverName).replace("nodeReplace", nodeName)
        for (log in logs){
            assert debugLog =~ log.replace("serverReplace", serverName).replace("nodeReplace", nodeName)
        }

        where: 'The following params will be:'
        testCaseID         | configName              | serverName        | nodeName        | path               | status      | expectedSummary              | logs 
        testCases.C363383  | ''                      | servers.'default' | nodes.'default' | paths.'default'    | "error"     | summaries.'emptyConfig'      | jobLogs.'emptyConfig'
        testCases.C363383  | confignames.correctSOAP | ''                | nodes.'default' | paths.'default'    | "error"     | summaries.'emptyServer'      | jobLogs.'emptyServer'
        testCases.C363383  | confignames.correctSOAP | servers.'default' | ''              | paths.'default'    | "error"     | summaries.'emptyNode'        | jobLogs.'emptyNode'
        testCases.C363383  | confignames.correctSOAP | servers.'default' | nodes.'default' | ''                 | "error"     | summaries.'emptyPath'        | jobLogs.'emptyPath'
        testCases.C363386  | confignames.incorrect   | servers.'default' | nodes.'default' | paths.'default'    | "error"     | summaries.'incorrectConfig'  | jobLogs.'incorrectConfig'
        testCases.C363387  | confignames.correctSOAP | servers.'wrong'   | nodes.'default' | paths.'default'    | "error"     | summaries.'wrongServer'      | jobLogs.'wrongServer'
        testCases.C363387  | confignames.correctSOAP | servers.'default' | nodes.'wrong'   | paths.'default'    | "error"     | summaries.'wrongNode'        | jobLogs.'wrongNode'
    }


    def runCliCommand(command, res = ''){
        def result = runCustomShellCommand(command, res)
        waitUntil {
        try {
                jobCompleted(result)
            } catch (Exception e) {
                println e.getMessage()
            }
        }
        return result
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
