import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import PluginTestHelper

@Stepwise
class DeleteConnectionFactorySuite extends PluginTestHelper {

    /**
     * Environments Variables
     */

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

    /**
     * Dsl Parameters
     */

    /**
     * Dsl Parameters
     */

    @Shared
    def testProjectName =           "EC-WebSphere-SystemTests"
    @Shared
    def testProcedureName =         "DeleteActivationSpec"
    @Shared
    def preparationProcedureName1 = "CreateorUpdateSIBConnectionFactory"
    @Shared
    def preparationProcedureName2 = "CreateorUpdateWMQConnectionFactory"

    /**
     * Common Maps: General Maps fpr different fields
     */

    @Shared
    def checkBoxValues = [
        unchecked:  "0",
        checked:    "1",
    ]

    /**
     * Parameters for Test Setup
     */

}
