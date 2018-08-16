import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import PluginTestHelper

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
        systemTest1: [
            name: 'C363382',
            description: 'Export Server, Export Server - file already exists '],
    ]

	@Shared
    def procName = 'ImportApplicationServer'


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

}