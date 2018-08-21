import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import PluginTestHelper

class CreateApplicationServerTemplate extends PluginTestHelper {


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
        systemTest1: [
            name: 'C363352',
            description: 'required values'],
        systemTest2: [
            name: 'C363353',
            description: 'all fields (required + Description)'],
        systemTest3: [
            name: 'C363354',
            description: 'empty required fields'],   
        systemTest4: [
            name: 'C363357',
            description: 'incorrect Config Name'],
        systemTest5: [
            name: 'C363358',
            description: 'incorrect Node & Server'],
        systemTest6: [
            name: 'C363360',
            description: 'Template name already exists'],                     
    ]

    @Shared
    def procName = 'CreateApplicationServerTemplate'


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
    def names = [
        template1: 'template1',
        template2: 'template2',
        template3: 'template3',
        template4: 'template4',        
    ]

    @Shared
    def summaries = [  
        'default': "Application server template templateReplace has been created\n",
        'emptyConfig': "Configuration '' doesn't exist",
        'incorrectConfig': "Configuration 'incorrect' doesn't exist",
        'emptyServer': "Failed to create application server template templateReplace\nException: ADMG0261E: Could not validate Create server template command java.lang.NullPointerException.\n",
        'emptyNode': "Failed to create application server template templateReplace\nException: ADMG0504E: Cannot create server template using a server in a node  whose operating system is not known.\n",
        'emptyName': "Failed to create application server template \nException: ADMG0255E: Template Name is required.\n",
        'wrongServer': 'Failed to create application server template templateReplace\nException: ADMG0256E: wrong does not exist within node websphere90ndNode01.\n',
        'wrongNode': 'Failed to create application server template templateReplace\nException: ADMG0258E: Node wrong is not a valid node.\n',
        'alreadyExist': 'Failed to create application server template templateReplace\nException: ADMG0262E: Template templateReplace already exists.\n'
    ]

    @Shared
    def jobLogs = [
        'default': ["Application server template templateReplace has been created", "(?!Synchronizing configuration repository with nodes now.)", "(?!The following nodes have been synchronized: websphere90ndNode01)"],
        'syncNode': ["Application server template templateReplace has been created", "Synchronizing configuration repository with nodes now.", "The following nodes have been synchronized: websphere90ndNode01"],
        'description': ["if templateDescription:\n    params.append\\('-description'\\)\n    params.append\\(templateDescription\\)"],
        'emptyConfig': ["Configuration '' doesn't exist"],
        'incorrectConfig': ["Configuration 'incorrect' doesn't exist"],
        'emptyServer': ["Failed to create application server template templateReplace", "Could not validate Create server template command java.lang.NullPointerException."],
        'emptyNode': ["Exception: ADMG0504E: Cannot create server template using a server in a node  whose operating system is not known."],
        'emptyName': ["Failed to create application server template", "Template Name is required."],
        'wrongServer': ['Failed to create application server template templateReplace', 'wrong does not exist within node websphere90ndNode01.'],
        'wrongNode': ['Failed to create application server template templateReplace', 'Node wrong is not a valid node.'],  
        'alreadyExist': ['Failed to create application server template templateReplace', 'Exception: ADMG0262E: Template templateReplace already exists.']                             
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
                    wasNodeName:  '',
                    wasSyncNodes: '',
                    wasTemplateDescription: '',
                    wasTemplateLocation: '',
                    wasTemplateName: '',
                ]
        ])

        importProject(projectName, 'dsl/RunCustomShellCommand/RunCustomShellCommand.dsl', [projectName: projectName,
            wasResourceName : wasResourceName,
            procName: 'runCustomShellCommand',
        ])

        dsl 'setProperty(propertyName: "/plugins/EC-WebSphere/project/ec_debug_logToProperty", value: "/myJob/debug_logs")' 
    }

    @Unroll
    def 'CreateApplicationServerTemplate - Positive: #testCaseID.name #testCaseID.description'(){
        given: "Parameters for procedure"
        def runParams = [
            configname:  configName,
            wasAppServerName: serverName,
            wasNodeName:  nodeName,
            wasSyncNodes: syncNode,
            wasTemplateDescription: description,
            wasTemplateLocation: location,
            wasTemplateName: name,
        ]
        expectedSummary = expectedSummary.replace("templateReplace", name) 

        when: "Run procedure and wait until job is completed"
        def result = runProcedure(runParams)

        then: "Get and compare results"
        def outcome = getJobProperty('/myJob/outcome', result.jobId)
        def jobSummary = getJobProperty("/myJob/jobSteps/$procName/summary", result.jobId)
        def debugLog = getJobLogs(result.jobId)
        assert outcome == status
        assert jobSummary == expectedSummary
        for (log in logs){
            assert debugLog =~ log.replace("templateReplace", name)
        }


        where: 'The following params will be:'
        testCaseID             | configName              | serverName        | nodeName        | syncNode | description | location | name            | status      | expectedSummary     | logs 
        testCases.systemTest1  | confignames.correctSOAP | servers.'default' | nodes.'default' | '1'      | ''          | ''       | names.template1 | "success"   | summaries.'default' | jobLogs.'syncNode'
        testCases.systemTest1  | confignames.correctSOAP | servers.'default' | nodes.'default' | '0'      | ''          | ''       | names.template2 | "success"   | summaries.'default' | jobLogs.'default'
        testCases.systemTest2  | confignames.correctSOAP | servers.'default' | nodes.'default' | '1'      | 'the best'  | ''       | names.template3 | "success"   | summaries.'default' | jobLogs.'description'
    }

    @Unroll
    def 'CreateApplicationServerTemplate - Negative: #testCaseID.name #testCaseID.description'(){
        given: "Parameters for procedure"
        def runParams = [
            configname:  configName,
            wasAppServerName: serverName,
            wasNodeName:  nodeName,
            wasSyncNodes: syncNode,
            wasTemplateDescription: description,
            wasTemplateLocation: location,
            wasTemplateName: name,
        ]
        expectedSummary = expectedSummary.replace("templateReplace", name) 

        when: "Run procedure and wait until job is completed"
        def result = runProcedure(runParams)

        then: "Get and compare results"
        def outcome = getJobProperty('/myJob/outcome', result.jobId)
        def jobSummary = getJobProperty("/myJob/jobSteps/$procName/summary", result.jobId)
        def debugLog = getJobLogs(result.jobId)
        assert outcome == status
        assert jobSummary == expectedSummary
        for (log in logs){
            assert debugLog =~ log.replace("templateReplace", name)
        }


        where: 'The following params will be:'
        testCaseID             | configName              | serverName        | nodeName        | syncNode | description | location | name            | status              | expectedSummary                 | logs 
        testCases.systemTest3  | ''                      | servers.'default' | nodes.'default' | '1'      | ''          | ''       | names.template1 | "error"             | summaries.'emptyConfig'         | jobLogs.'emptyConfig'
        // testCases.systemTest3  | confignames.correctSOAP | ''                | nodes.'default' | '1'      | ''          | ''       | names.template1 | "error"             | summaries.'emptyServer'         | jobLogs.'emptyServer'
        testCases.systemTest3  | confignames.correctSOAP | servers.'default' | ''              | '1'      | ''          | ''       | names.template1 | "error"             | summaries.'emptyNode'           | jobLogs.'emptyNode'
        testCases.systemTest3  | confignames.correctSOAP | servers.'default' | nodes.'default' | '1'      | ''          | ''       | ''              | "error"             | summaries.'emptyName'           | jobLogs.'emptyName'
        testCases.systemTest4  | confignames.incorrect   | servers.'default' | nodes.'default' | '1'      | ''          | ''       | names.template1 | "error"             | summaries.'incorrectConfig'     | jobLogs.'incorrectConfig'
        testCases.systemTest5  | confignames.correctSOAP | servers.'wrong'   | nodes.'default' | '1'      | ''          | ''       | names.template1 | "error"             | summaries.'wrongServer'         | jobLogs.'wrongServer'
        testCases.systemTest5  | confignames.correctSOAP | servers.'default' | nodes.'wrong'   | '1'      | ''          | ''       | names.template1 | "error"             | summaries.'wrongNode'           | jobLogs.'wrongNode'
        testCases.systemTest5  | confignames.correctSOAP | servers.'wrong'   | nodes.'wrong'   | '1'      | ''          | ''       | names.template1 | "error"             | summaries.'wrongNode'           | jobLogs.'wrongNode'
    }

    @Unroll
    def 'CreateApplicationServerTemplate - Negative: #testCaseID.name #testCaseID.description'(){
        given: "Parameters for procedure"
        def runParams = [
            configname:  configName,
            wasAppServerName: serverName,
            wasNodeName:  nodeName,
            wasSyncNodes: syncNode,
            wasTemplateDescription: description,
            wasTemplateLocation: location,
            wasTemplateName: name,
        ]
        expectedSummary = expectedSummary.replace("templateReplace", name) 

        when: "Run procedure and wait until job is completed"
        def result = runProcedure(runParams)
        result = runProcedure(runParams)
        then: "Get and compare results"
        def outcome = getJobProperty('/myJob/outcome', result.jobId)
        def jobSummary = getJobProperty("/myJob/jobSteps/$procName/summary", result.jobId)
        def debugLog = getJobLogs(result.jobId)
        assert outcome == status
        assert jobSummary == expectedSummary
        for (log in logs){
            assert debugLog =~ log.replace("templateReplace", name)
        }


        where: 'The following params will be:'
        testCaseID             | configName              | serverName        | nodeName        | syncNode | description | location | name            | status      | expectedSummary          | logs 
        testCases.systemTest6  | confignames.correctSOAP | servers.'default' | nodes.'default' | '1'      | ''          | ''       | names.template4 | "error"     | summaries.'alreadyExist' | jobLogs.'alreadyExist'
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
