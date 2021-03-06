package com.electriccloud.plugin.spec

import spock.lang.*
import com.electriccloud.spec.*
import spock.util.concurrent.PollingConditions;

class PluginTestHelper extends PluginSpockTestSupport {

    static def helperProjName = 'WebSphere Helper Project'
    @Shared
    def currentProcedureName

    def redirectLogs(String parentProperty = '/myJob') {
        def propertyLogName = parentProperty + '/debug_logs'
        dsl """
            setProperty(
                propertyName: "/plugins/EC-WebSphere/project/ec_debug_logToProperty",
                value: "$propertyLogName"
            )
        """
        return propertyLogName
    }

    def redirectLogsToPipeline() {
        def propertyName = '/myPipelineRuntime/debugLogs'
        dsl """
            setProperty(
                propertyName: "/plugins/EC-WebSphere/project/ec_debug_logToProperty",
                value: "$propertyName"
            )
        """
        propertyName
    }
    def getCurrentProcedureName(def jobId){
        assert jobId
        def currentProcedureName
        def property = "/myJob/procedureName"
        try {
            currentProcedureName = getJobProperty(property, jobId)
            println("Current Procedure Name: " + currentProcedureName)
        } catch (Throwable e) {
            logger.debug("Can't retrieve Run Procedure Name from the property: '$property'; check job: " + jobId)
        }
        return currentProcedureName
    }


    def getJobUpperStepSummary(def jobId){
        assert jobId
        def summary
        def currentProcedureName = getCurrentProcedureName(jobId)
        def property = "/myJob/jobSteps/$currentProcedureName/summary"
        println "Trying to get the summary for Procedure: $currentProcedureName, property: $property, jobId: $jobId"
        try{
            summary = getJobProperty(property, jobId)
        } catch (Throwable e) {
            logger.debug("Can't retrieve Upper Step Summary from the property: '$property'; check job: " + jobId)
        }
        return summary
    }


    def getJobLogs(def jobId) {
        assert jobId
        def logs
        try {
            logs = getJobProperty("/myJob/debug_logs", jobId)
        } catch (Throwable e) {
            logs = "Possible exception in logs; check job"
        }
        logs
    }

    def getPipelineLogs(flowRuntimeId) {
        assert flowRuntimeId
        getPipelineProperty('/myPipelineRuntime/debugLogs', flowRuntimeId)
    }
    def dslWithTimeout(dslString, timeout = 3600) {
        def result = dsl(dslString)
        PollingConditions poll = new PollingConditions(timeout: timeout, initialDelay: 0,  factor: 1.25)
        // PollingConditions poll = createPoll(timeout)
        poll.eventually {
            jobStatus(result.jobId).status == 'completed'
        }
        return result
    }

    def runProcedureDsl(dslString) {
        redirectLogs()
        assert dslString

        def result = dsl(dslString)
        assert result.jobId
        waitUntil {
            jobCompleted result.jobId
        }
        def logs = getJobLogs(result.jobId)
        def outcome = jobStatus(result.jobId).outcome
        logger.debug("DSL: $dslString")
        logger.debug("Logs: $logs")
        logger.debug("Outcome: $outcome")
        [logs: logs, outcome: outcome, jobId: result.jobId]
    }

    def createWorkspace(def workspaceName) {
        def isWindows = System.getenv("IS_WINDOWS");
        def workspacePath = "/tmp";
        if (isWindows) {
            workspacePath = "C:/workspace";
        }
        def workspaceResult = dsl """
try {
            createWorkspace(
                workspaceName: '${workspaceName}',
                agentDrivePath: '${workspacePath}',
                agentUnixPath: '/tmp',
                local: '1'
            )
} catch (Exception e) {}
        """
    }

    // following envs should be present:
    // WAS_USERNAME
    // WAS_PASSWORD
    // WAS_HOST
    // WAS_PORT
    // WAS_CONNTYPE
    // WAS_DEBUG
    // WSADMIN_PATH
    //
    def createConfiguration(configName, props = [:])  {
        def username = System.getenv('WAS_USERNAME') ?: 'wsadmin'
        def password = System.getenv('WAS_PASSWORD') ?: 'changeme'
        def websphere_url = System.getenv('WAS_HOST') ?: ''
        def websphere_port = System.getenv('WAS_PORT') ?: ''
        def wsadmin_path = System.getenv('WSADMIN_PATH') ?: ''
        def conntype = System.getenv('WAS_CONNTYPE') ?: 'SOAP'
        def debug = System.getenv('WAS_DEBUG') ?: '0'
        // def snow_proxy = System.getenv('SNOW_PROXY')

        def configOpts = [
        websphere_url: websphere_url,
        websphere_port: websphere_port,
        wsadminabspath: wsadmin_path,
        conntype: conntype,
        debug: debug
        ]

        if (System.getenv('RECREATE_CONFIG')) {
            props.recreate = true
        }
        props.confPath = 'websphere_cfgs'
        createPluginConfiguration(
            'EC-WebSphere',
            configName,
            configOpts,
            username,
            password,
            props
        )
    }

    def createCustomConfiguration(configName, def inputData, props = [:])  {
        println "Log - InputData: $inputData"
        def configOpts = [
        websphere_url: inputData.websphere_url,
        websphere_port: inputData.websphere_port,
        wsadminabspath: inputData.wsadminabspath,
        conntype: inputData.conntype,
        debug: inputData.debug
        ]
        if (System.getenv('RECREATE_CONFIG')) {
            props.recreate = true
        }
        props.confPath = 'websphere_cfgs'
        createPluginConfiguration(
            'EC-WebSphere',
            configName,
            configOpts,
            inputData.username,
            inputData.password,
            props
        )
    }

    def createCustomConfigurationWithExternalCredentials(String configName, def parameters, def credentialReferences, boolean recreate=true) {
        parameters.config = configName

        if (recreate) {
            def deleteConfigCode = """
                runProcedure(
                    "projectName": '/plugins/EC-WebSphere/project',
                    "procedureName": 'DeleteConfiguration',
                    "actualParameter": [
                        "config": "$configName"
                    ]
                )
            """
            def deleteResult = dslWithTimeout(deleteConfigCode)
            waitUntil {
                try {
                    jobCompleted(deleteResult)
                } catch (Exception e) {
                    println e.getMessage()
                }
            }
        }
        def parametersString = parameters.collect { k, v -> "$k: '$v'" }.join(', ')
        def credRefString = credentialReferences.collect { k, v -> "$k: '$v'" }.join(', ')
        def code = """
            runProcedure(
                "projectName": '/plugins/EC-WebSphere/project',
                "procedureName": 'CreateConfiguration',
                "actualParameter": [
                    $parametersString                 
                ],
                "credentialReferenceParameter": [
                    $credRefString
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

    def createWebSphereResource() {
        def hostname = System.getenv('WEBSPHERE_RESOURCE_HOST')
        def resources = dsl "getResources()"
        logger.debug(objectToJson(resources))

        def resource = resources.resource.find {
            it.hostName == hostname || it.resourceName == 'WebSphere'
        }
        if (resource) {
            logger.debug("WebSphere resource already exists")
            return resource.resourceName
        }

        def port = System.getenv('WEBSPHERE_RESOURCE_PORT') ?: '7800'
        def workspaceName = randomize("WebSphere")

        def workspaceResult = dsl """
try {
            createWorkspace(
                workspaceName: '${workspaceName}',
                agentDrivePath: '/tmp',
                agentUncPath: '/tmp',
                local: '1'
            )
} catch (Exception e) {}
        """
        logger.debug(objectToJson(workspaceResult))

        def result = dsl """
            createResource(
                resourceName: '${randomize("WebSphere")}',
                hostName: '$hostname',
                port: '$port',
                workspaceName: '$workspaceName'
            )
        """

        logger.debug(objectToJson(result))
        def resName = result?.resource?.resourceName
        assert resName
        resName
    }

    def createHelperProject(resName) {
        // TODO:
    }

    def checkExpectedException(def e, def errorDesc, def errorDetails){
        // TODO:
    }

    def runGetResourcesProcedure(def params){
       importProject(params.projectName, 'dsl/GetResources.dsl', [projectName: params.projectName, wasResourceName: params.wasResourceName])
        def code = """
            runProcedure(
                projectName: '$params.projectName',
                procedureName: '$params.resourceProcedureName',
                actualParameter: [
                    filePath: '$params.filePath',
                    fileURL: '$params.fileURL',
                    wasResourceName: '$params.wasResourceName',
                ]
            )
        """
        return dsl(code)
    }

    def runCustomShellCommand(command, res='') {
        def shell = System.getenv('IS_WINDOWS') == "1" ? 'powershell' : 'bash'
        def result = """
            runProcedure(
                "projectName": '$projectName',
                "procedureName": 'runCustomShellCommand',
                "actualParameter": [
                    "cli_command": '$command',
                    "stepRes":     '$res',
                    "shellCommand":'$shell',
                ]
            )
        """
        return dslWithTimeout(result)
    }

    def createCDCredential(projectName, credentialName, userName, password) {
        def result = """
            try {
                deleteCredential([
                    "projectName": "$projectName",
                    "credentialName": "$credentialName",
                ])
            } catch (Exception e) {}
            try {
                createCredential([
                    "projectName": "$projectName",
                    "credentialName": "$credentialName",
                    "userName" : "$userName",
                    "password" : "$password"
                ])
                } catch (Exception e) {
                    println(e.getMessage())
            }
        """
        return dsl(result)
    }
    def createProjectForCreateConfiguration(projectName) {
        def code = """
            try {
                createProject("$projectName")
            }
            catch (Exception e) {}
        """
        return dsl(code)
    }

}
