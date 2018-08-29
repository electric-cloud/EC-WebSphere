package com.electriccloud.plugin.spec

import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import com.electriccloud.plugin.spec.PluginTestHelper

@IgnoreIf({ System.getenv('IS_WAS_ND') != "1"})
@Stepwise
@Unroll
class DeleteApplicationServerTemplate extends PluginTestHelper {

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
    def configname = 'Web-Sphere-SOAP'

    @Shared
    def templateName = 'template1'

    @Shared
    def templateNode = wasHost + 'Node01'

    @Shared
    def templateServer = 'server1'


    @Shared
    def procName = 'DeleteApplicationServerTemplate'

    @Shared
    def procCreateTemplate = 'CreateApplicationServerTemplate'

    @Shared
    def projectName = "EC-WebSphere Specs $procName Project"

    @Shared
    def testCases = [
            systemTest1: [
                    name: 'C363373',
                    description: 'DeleteAppServerTemplate - sync Nodes'],
            systemTest2: [
                    name: 'C363846',
                    description: 'DeleteAppServerTemplate - no sync Nodes'],
            systemTest3: [
                    name: 'Negative',
                    description: 'delete template that was already deleted'],
            systemTest4: [
                    name: 'C363376',
                    description: 'empty required fields - empty config'],
            systemTest5: [
                    name: 'C363376',
                    description: 'empty required fields - empty template name'],
            systemTest6: [
                    name: 'C363378',
                    description: 'incorrect Config Name'],
            systemTest7: [
                    name: 'C363380',
                    description: "template doesn't exist"]
    ]

    @Shared
    def summaries = [
            'default': "Application server template $templateName has been deleted\n",
            'error_empty_config': "Configuration '' doesn't exist",
            'error_empty_name': "Failed to delete application server template \nException: WASL6041E: The following argument value is not valid: templateName:.",
            'error_invalid_config': "Configuration 'incorrectConfig' doesn't exist",
            'error_invalid_name': "Failed to delete application server template incorrectName\nException: WASL6040E: The templateName:incorrectName specified argument does not exist.",
            'error_name': "Failed to delete application server template $templateName\nException: WASL6040E: The templateName:$templateName specified argument does not exist."
    ]

    @Shared
    def jobLogs = [
            'default_synch':  ['success',"Application server template $templateName has been deleted","The following nodes have been synchronized: $templateNode"],
            'default_synch_version_s': ['success',"Application server template $templateName has been deleted"],
            'default_no_synch':  ['success',"Application server template $templateName has been deleted"],
            'error_empty_name':  ['error','Failed to delete application server template',"Exception: WASL6041E: The following argument value is not valid: templateName:."],
            'error_name':  ['error',"Failed to delete application server template (incorrectName|$templateName)","Exception: WASL6040E: The templateName:(incorrectName|$templateName) specified argument does not exist."],
            'error_config':  ["Error: Configuration ('incorrectConfig'|'') doesn't exist"],
    ]

    def doSetupSpec() {
        def wasResourceName = wasHost
        createWorkspace(wasResourceName)
        createConfiguration(configname, [doNotRecreate: false])
        change_summary(wasHost)
        change_logs(wasHost)

        importProject(projectName, 'dsl/RunProcedure.dsl', [projName: projectName,
                                                            resName : wasResourceName,
                                                            procName: procName,
                                                            params  : [
                                                                    configname: '',
                                                                    wasSyncNodes: '',
                                                                    wasTemplateName: '',
                                                            ]
        ])

        importProject(projectName, 'dsl/RunProcedure.dsl', [projName: projectName,
                                                            resName : wasResourceName,
                                                            procName: procCreateTemplate,
                                                            params  : [
                                                                    configname: '',
                                                                    wasAppServerName: '',
                                                                    wasNodeName: '',
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

    def doCleanupSpec() {
    }

    @Unroll
    def 'DeleteApplicationServerTemplate - #testCaseID.name #testCaseID.description'() {
        if (createTemplate) {
            createApplicationServerTemplate(template)
        }

        given: "Parameters for procedure"
        def runParams = [
                configname: configName,
                wasSyncNodes: syncNodes,
                wasTemplateName: template,
        ]

        when: "Run procedure and wait until job is completed"
        def result = runProcedure(runParams)

        then: "Get and compare results"
        def outcome = getJobProperty('/myJob/outcome', result.jobId)
        def jobSummary = getJobProperty("/myJob/jobSteps/$procName/summary", result.jobId)
        def debugLog = getJobLogs(result.jobId)
        assert outcome == expectedOutcome
        assert jobSummary == expectedSummary
        for (log in logs) {
            assert debugLog =~ log
        }

        where: 'The following params will be:'
        testCaseID            | configName        | template        | syncNodes | expectedSummary                | logs                     | expectedOutcome | createTemplate
        testCases.systemTest1 | configname        | templateName    | '1'       | summaries.default              | jobLogs.default_synch    | 'success'       | 1
        testCases.systemTest2 | configname        | templateName    | '0'       | summaries.default              | jobLogs.default_no_synch | 'success'       | 1
        testCases.systemTest3 | configname        | templateName    | '0'       | summaries.error_name           | jobLogs.error_name       | 'error'         | null
        testCases.systemTest4 | ''                | templateName    | '1'       | summaries.error_empty_config   | jobLogs.error_config     | 'error'         | null
        testCases.systemTest5 | configname        | ''              | '1'       | summaries.error_empty_name     | jobLogs.error_empty_name | 'error'         | null
        testCases.systemTest6 | 'incorrectConfig' | templateName    | '1'       | summaries.error_invalid_config | jobLogs.error_config     | 'error'         | null
        testCases.systemTest7 | configname        | 'incorrectName' | '1'       | summaries.error_invalid_name   | jobLogs.error_name       | 'error'         | null
    }

    def createApplicationServerTemplate(template){
        def runParams = [
                configname: configname,
                wasAppServerName: templateServer,
                wasNodeName: templateNode,
                wasSyncNodes: '1',
                wasTemplateDescription: 'Template for testing',
                wasTemplateLocation: '',
                wasTemplateName: template,
        ]
        runProcedure(runParams, procCreateTemplate)
    }

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
    def change_summary(version){
        if (version == "websphere80nd" || version == "websphere85nd"){
            summaries.error_empty_name += "'\n"
            summaries.error_invalid_name += "'\n"
            summaries.error_name += "'\n"

        } else {
            summaries.error_empty_name += "\n"
            summaries.error_invalid_name += "\n"
            summaries.error_name += "\n"
        }
    }
    def change_logs(version){
        if (version == "websphere80s" || version == "websphere85s" || version == "websphere90s"){
            jobLogs.default_synch = jobLogs.default_synch_version_s
        }
    }

}

