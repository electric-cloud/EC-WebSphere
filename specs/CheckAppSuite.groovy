import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import PluginTestHelper

@Stepwise
class GetArtifactList extends PluginTestHelper {
    @Shared
    def testProjectName = 'EC-WebSphere-Specs-CheckApp'
    @Shared
    def testProcedureName = 'CheckApp'
    @Shared
    def configName = 'specConfig'
    // params for where section
    @Shared
    def tNumber

    // def doCleanupSpec() {
    //     dsl "deleteProject '$testProjectName'"
    // }
    def doSetupSpec() {
        def wasResourceName = System.getenv('WAS_HOST');
        createWorkspace(wasResourceName)
        createConfiguration(configName, [doNotRecreate: false])
        importProject(testProjectName, 'dsl/CheckApp/Procedure.dsl', [projectName: testProjectName, wasResourceName:wasResourceName])

        dsl 'setProperty(propertyName: "/plugins/EC-WebSphere/project/ec_debug_logToProperty", value: "/myJob/debug_logs")'
    }
    def "Running CheckApp suite, positive scenario - procedure"() {
        def wasResourceName=System.getenv('WAS_HOST');
        when: 'Procedure runs'
        def runParams = [
            configName: configName,
            applicationName: "HelloWorld",
            applicationState: "RUNNING",
            wasResourceName: wasResourceName,
            waitTime: '0'
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
                        waitTime:         '$parameters.propertyFormat'
                    ]
                )
        """
        return dsl(code)
        
    }
    def checkLogOutputByNumber(def log, def number, def context) {
        if (number == 1) {
            return log =~ 'Unable to find the credential'
        }
        else if (number == 2) {
            return log =~ "Jenkins job $context.jobName does not exist."
        }
        else if (number == 3) {
            return log =~ "Build #$context.buildNumber does not exist in existing Jenkins $context.jobName job."
        }
        else if (number == 4) {
            return log =~ "Build #$context.buildNumber does not exist in existing Jenkins $context.jobName job."
        }
        else if (number == 5) {
            return log =~ "Can't set property: Unrecognized path element in '$context.propertyPath"
        }
        else if (number == 6) {
            return log =~ "Can't set property: java.util.UnknownFormatConversionException: Conversion ="
        }
    }

}
