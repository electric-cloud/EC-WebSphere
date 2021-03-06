package com.electriccloud.plugin.spec

import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import com.electriccloud.plugin.spec.PluginTestHelper


@Stepwise
class CheckApp extends PluginTestHelper {


    @Shared
    def expectedOutcomes = [
        success: 'success',
        error: 'error',
        warning: 'warning',
        running: 'running'
    ]
    
    @Shared
    def expectedUpperStepSummaries = [
        fieldRequired: '',
        incorrectComfigname: "Configuration 's' doesn't exists",
        incorectState: "is not valid application state to be checked. Expected on of: EXISTS, NOT_EXISTS, READY, NOT_READY, RUNNING, NOT_RUNNING",
        incorrectAppName: "is not installed, expected",
        appIsNotInstalled_IsInstalled: "is not installed, expected: application is installed",
        appIsNotInstalled_IsNotReady: "is not installed, expected: application is not ready",
        appIsNotInstalled_IsReady: "is not installed, expected: application is ready",
        appIsNotInstalled_IsNotRunning: "is not installed, expected: application is not running",
        appIsNotInstalled_IsRunning: "is not installed, expected: application is running",
        appIsInstalled_IsNotInstalled: "is installed, expected: application is not installed",
        appIsInstalled_IsNotReady: "is installed, expected: application is not ready",
        appIsInstalled_IsReady: "is installed, expected: application is ready",
        appIsInstalled_IsNotRunning: "is installed, expected: application is not running",
        appIsInstalled_IsRunning: "is installed, expected: application is running",
        appIsNotReady_IsNotInstalled: "is not ready, expected: application is not installed",
        //appIsNotReady_IsInstalled: "",
        appIsNotReady_IsReady: "is not ready, expected: application is ready",
        appIsNotReady_IsNotRunning: "is not ready, expected: application is not running",
        appIsNotReady_IsRunning: "is not ready, expected: application is running",
        appIsReady_IsNotInstalled: "is not ready, expected: application is not installed",
        //appIsReady_IsInstalled: "",
        appIsReady_IsNotReady: "is ready, expected: application is not ready",
        appIsReady_IsNotRunning: "is ready, expected: application is not running",
        appIsReady_IsRunning: "is ready, expected: application is running",
        appIsNotRunning_IsNotInstalled: "is not running, expected: application application is not installed",
        //appIsNotRunning_IsInstalled: "",
        appIsNotRunning_IsNotReady: "is not running, expected: application is not ready",
        //appIsNotRunning_IsReady: "",
        appIsNotRunning_IsRunning: "is not running, expected: application is running",
        appIsRunning_IsNotInstalled: "is running, expected: application application is not installed",
        //appIsRunning_IsInstalled: "",
        appIsRunning_IsNotReady: "is running, expected: application is not ready",
        //appIsRunning_IsReady: "",
        appIsRunning_IsNotRunning: "is running, expected: application is not running",
    ]

    @Shared
    def testProjectName = 'EC-WebSphere-SystemTests'
    @Shared
    def testProcedureName = 'CheckApp'
    @Shared
    def resourceProcedureName = 'GetResources'
    @Shared
    def applicationUrl = 'https://github.com/electric-cloud/TestApplications/raw/master/EC-WebSphere/Applications/hello-world.war'
    @Shared
    def preProcedureName = 'DeployEnterpriseApp'    
    @Shared
    def configName = 'specConfig'
    @Shared
    def configNames = [
        empty: '',
        correct: 'specConfig',
        incorect: 'incorrect',
    ]
    @Shared 
    def wasHost = System.getenv('WAS_HOST')
    @Shared 
    def wasPath = System.getenv('WSADMIN_PATH')
    @Shared 
    def wasAppPath = System.getenv('WAS_APPPATH')

    // params for where section
    @Shared
    def tNumber
    @Shared
    def checkBoxValules = [
        unchecked: '0',
        checked: '1'
    ]
    @Shared
    def wsApplicationNames = [
        notExistApplicationHW : 'notExistHelloWorld',
        existApplicationHW: 'existHelloWorld',
        notReadyApplicationHW: 'notReadyHelloWorld',
        readyApplicationHW: 'readyHelloWorld',
        notRunningApplicationHW: 'notRunningHelloWorld',        
        runningApplicationHW: 'runningHelloWorld'
    ]
    
    @Shared
    def wsApplicationStates = [
        empty: '',
        notExist: 'NOT_EXISTS',
        exist: 'EXISTS',
        notReady: 'NOT_READY',
        ready: 'READY',
        notRunning: 'NOT_RUNNING',
        running: 'RUNNING',
        incorect: 'INCORRECT'
        
    ]
    @Shared
    def wsAdminAbsolutePathes = [
        empty: '',
        correct: wasPath,
        incorrect: '/incorrect/wsadmin.sh'
    ]
    @Shared
    def wsContextRoots = wsApplicationNames

    @Shared
    def wsTargetServers =[
        empty: '',
        correct: wasHost + 'Node01=server1', // e.g. 'websphere85ndNode01=server1',
        incorect: 'incorrect=inciorrect'
    ]
    
    @Shared 
    def wsAppPathes = [
        empty: '',
        incorrect: wasAppPath +'/incorrect/appplication.war',
        helloWorld: wasAppPath +'hello-world.war',
        jPetStore: wasAppPath +'jpetstore-mysql-1.0.war'
    ]
    @Shared
    def wsAdditionalDeploymentParameterses = [
        empty: '',
        incorect: 'icorrect params',
        correctHellowWorld: '-MapWebModToVH [[hello-world.war hello-world.war,WEB-INF/web.xml default_host]]',
        correctJpetStore: '-MapWebModToVH [[jpetstore-mysql-1.0.war jpetstore-mysql-1.0.war,WEB-INF/web.xml default_host]]'
    ]
    
    @Shared
    def wsSynchronizeActiveNodes = checkBoxValules
    @Shared
    def wsDistributeApplications = checkBoxValules
    @Shared
    def wsStartApplications = checkBoxValules

    
    @Shared
    def wsAdminAbsolutePath
    def wsApplicationName
    def wsApplicationState
    def wsAppPath
    def wsAdditionalDeploymentParameters
    def wsContextRoot
    def wsTargetServer
    def tTime


    def wsSynchronizeActiveNode
    def wsDistributeApplication
    def wsStartApplication

   def doSetupSpec() {
        def wasResourceName = System.getenv('WAS_HOST');
        createWorkspace(wasResourceName)
        createConfiguration(configName, [doNotRecreate: false])
        importProject(testProjectName, 'dsl/CheckApp/Procedure.dsl', [projectName: testProjectName, wasResourceName:wasResourceName])
        importProject(testProjectName, 'dsl/CheckApp/DeployEnterpriseApp.dsl', [projectName: testProjectName, wasResourceName:wasResourceName])

        runGetResourcesProcedure([projectName: testProjectName, resourceProcedureName: resourceProcedureName, filePath: wsAppPathes.helloWorld, fileURL: applicationUrl, wasResourceName: wasResourceName])
        
        dsl 'setProperty(propertyName: "/plugins/EC-WebSphere/project/ec_debug_logToProperty", value: "/myJob/debug_logs")'


    }
    /**
    def doCleanupSpec() {
         dsl "deleteProject '$testProjectName'"
    }
    */
    @Unroll
    def "Deploy Enterprise Application. Positive and Extended Scenarios for Check Application Runs"(){
        
        when: 'Procedure runs'
        def wasResourceName=System.getenv('WAS_HOST');

        def runParams = [
            configName: wsConfigName,
            wsadminAbsPath: wsAdminAbsolutePath,
            appName: wsApplicationName,
            apppath: wsAppPath,
            wasResourceName: wasResourceName,
            additionalDeployParams: wsAdditionalDeploymentParameters,
            contextRoot: wsContextRoot,
            serverList: wsTargetServer,
            syncActiveNodes: wsSynchronizeActiveNode,
            distributeApp: wsDistributeApplication,
            startApp: wsStartApplication
        ]
        
        def result = runProcedureDeployApp(runParams)

        then: 'wait until job is completed:'
        
        waitUntil {
            try {
                jobCompleted(result)
            } catch (Exception e) {
                println e.getMessage()
            }
        }
        def outcome = getJobProperty('/myJob/outcome', result.jobId)
        def debugLog = getJobLogs(result.jobId)
        println "Procedure log:\n$debugLog\n"

        assert outcome == expectedOutcome

        where: 
        
        /**
         * DOTO: Deply Appliocation with all statuses:
         * EXISTS, NOT_EXISTS, READY, NOT_READY, RUNNING, NOT_RUNNING
         * For Now - we have just RUNNING and NOT_RUNNING
         */

        wsConfigName    | wsAdminAbsolutePath               | wsApplicationName                             | wsAppPath                 | wsAdditionalDeploymentParameters                      | wsContextRoot                             | wsTargetServer            | wsSynchronizeActiveNode               | wsDistributeApplication            | wsStartApplication               | expectedOutcome
        configName      | wsAdminAbsolutePathes.correct     | wsApplicationNames.notRunningApplicationHW    | wsAppPathes.helloWorld    | wsAdditionalDeploymentParameterses.correctHellowWorld | wsContextRoots.notRunningApplicationHW    | wsTargetServers.correct   | wsSynchronizeActiveNodes.unchecked    | wsDistributeApplications.unchecked | wsStartApplications.unchecked    | 'success'
        configName      | wsAdminAbsolutePathes.correct     | wsApplicationNames.runningApplicationHW       | wsAppPathes.helloWorld    | wsAdditionalDeploymentParameterses.correctHellowWorld | wsContextRoots.runningApplicationHW       | wsTargetServers.correct   | wsSynchronizeActiveNodes.checked      | wsDistributeApplications.checked   | wsStartApplications.checked      | 'success'
    }


    @Unroll
    def "Check Application Suite. Positive and Extended Scenarios."(){
        
        when: 'Procedure runs'
        def wasResourceName=System.getenv('WAS_HOST');

        def runParams = [
            configName: wsConfigName,
            wsadminabspath: wsAdminAbsolutePath,
            applicationName: wsApplicationName,
            applicationState: wsApplicationState,
            wasResourceName: wasResourceName,
            wasWaitTime: tTime
        ]
        
        def result = runProcedure(runParams)

        then: 'wait until job is completed:'
        waitUntil {
            try {
                jobCompleted(result)
            } catch (Exception e) {
                println e.getMessage()
            }
        }
        def outcome = getJobProperty('/myJob/outcome', result.jobId)
        def debugLog = getJobLogs(result.jobId)
        println "Procedure log:\n$debugLog\n"

        assert outcome == expectedOutcome

        where:
        wsConfigName      | wsAdminAbsolutePath             | wsApplicationName                           | wsApplicationState               | tTime    | expectedOutcome
        configName        | wsAdminAbsolutePathes.empty     | wsApplicationNames.notExistApplicationHW    | wsApplicationStates.notExist     | '0'      | 'success'
        configName        | wsAdminAbsolutePathes.correct   | wsApplicationNames.notRunningApplicationHW  | wsApplicationStates.ready        | '0'      | 'success'
        configName        | wsAdminAbsolutePathes.empty     | wsApplicationNames.notRunningApplicationHW  | wsApplicationStates.notRunning   | '0'      | 'success'
        configName        | wsAdminAbsolutePathes.empty     | wsApplicationNames.runningApplicationHW     | wsApplicationStates.exist        | '0'      | 'success'
        configName        | wsAdminAbsolutePathes.correct   | wsApplicationNames.runningApplicationHW     | wsApplicationStates.ready        | '0'      | 'success'
        configName        | wsAdminAbsolutePathes.empty     | wsApplicationNames.runningApplicationHW     | wsApplicationStates.running      | '100'    | 'success'
   }

    @Unroll
    def "Check Application Suite. Negative Scenarios"(){

        when: 'Procedure runs'
        def wasResourceName=System.getenv('WAS_HOST');

        def runParams = [
            configName: wsConfigName,
            wsadminabspath: wsAdminAbsolutePath,
            applicationName: wsApplicationName,
            applicationState: wsApplicationState,
            wasResourceName: wasResourceName,
            wasWaitTime: tTime
        ]

        def result = runProcedure(runParams)

        then: 'wait until job is completed:'
        waitUntil {
            try {
                jobCompleted(result)
            } catch (Exception e) {
                println e.getMessage()
            }
        }
        def outcome = getJobProperty('/myJob/outcome', result.jobId)
        def debugLog = getJobLogs(result.jobId)
        println "Procedure log:\n$debugLog\n"

        assert outcome.contains(expectedOutcome)

        where:
        wsConfigName            | wsAdminAbsolutePath               | wsApplicationName                             | wsApplicationState              | tTime      | expectedOutcome
        'specConfig-Incorrect'  | wsAdminAbsolutePathes.empty       | wsApplicationNames.notExistApplicationHW      | wsApplicationStates.exist       | '0'        | 'error'
        configName              | wsAdminAbsolutePathes.incorrect   | wsApplicationNames.notExistApplicationHW      | wsApplicationStates.exist       | '0'        | 'error'
        configName              | wsAdminAbsolutePathes.empty       | wsApplicationNames.notRunningApplicationHW    | wsApplicationStates.notExist    | '0'        | 'error'
        configName              | wsAdminAbsolutePathes.empty       | wsApplicationNames.notRunningApplicationHW    | wsApplicationStates.notReady    | '0'        | 'error'
        configName              | wsAdminAbsolutePathes.empty       | wsApplicationNames.runningApplicationHW       | wsApplicationStates.notRunning  | '0'        | 'error'
        configName              | wsAdminAbsolutePathes.correct     | wsApplicationNames.notRunningApplicationHW    | wsApplicationStates.running     | '0'        | 'error'
        configName              | wsAdminAbsolutePathes.empty       | wsApplicationNames.runningApplicationHW       | wsApplicationStates.running     | '-1'       | 'error'
        configName              | wsAdminAbsolutePathes.empty       | wsApplicationNames.runningApplicationHW       | wsApplicationStates.running     | 'abs'      | 'error'
   }

    def runProcedure(def parameters) {
        def code = """
                runProcedure(
                    projectName: '$testProjectName',
                    procedureName: '$testProcedureName',
                    actualParameter: [
                        configName:       '$parameters.configName',
                        applicationName:  '$parameters.applicationName',
                        applicationState: '$parameters.applicationState',
                        wasResourceName:  '$parameters.wasResourceName',
                        wasWaitTime:      '$parameters.wasWaitTime'
                    ]
                )
        """
        return dslWithTimeout(code)
    }

    def runProcedureDeployApp(def parameters){
        def code = """
            runProcedure(
                projectName: '$testProjectName',
                procedureName: '$preProcedureName',
                actualParameter: [
                    configNameDEA: '$parameters.configName',
                    wsadminAbsPathDEA: '$parameters.wsadminAbsPath',
                    appNameDEA: '$parameters.appName',
                    apppathDEA: '$parameters.apppath',
                    wasResourceName: '$parameters.wasResourceName',
                    additionalDeployParamsDEA: '$parameters.additionalDeployParams',
                    contextRootDEA: '$parameters.contextRoot',
                    serverListDEA: '$parameters.serverList',
                    syncActiveNodesDEA: '$parameters.syncActiveNodes',
                    distributeAppDEA: '$parameters.distributeApp',
                    startAppDEA: '$parameters.startApp'
                ]
            )
        """
        return dslWithTimeout(code)
    }

}
