import spock.lang.*
import com.electriccloud.spec.SpockTestSupport
import PluginTestHelper


@Stepwise
class DeleteActivationSpecSuite extends PluginTestHelper {

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

    @Shared
    def testProjectName =           "EC-WebSphere-SystemTests"
    @Shared
    def testProcedureName =         "DeleteActivationSpec"
    @Shared
    def preparationProcedureName1 = "CreateorUpdateSIBActivationSpec"
    @Shared
    def preparationProcedureName2 = "CreateorUpdateWMQActivationSpec"

    /**
     * Common Maps: General Maps fpr different fields
     */

    @Shared
    def checkBoxValues = [
        unchecked: 	"0",
        checked: 	"1",
    ]

    /**
     * Parameters for Test Setup
     */

    @Shared //* Required Parameter (need incorrect and empty value)
    def specScopes = [
        empty:                          "",
        correctOneNode:                 "Node="+wasHost+"Node01",
        incorrect:                      "Node=incorrectScopeForDelete",
        correctOneNodeMessage:          "Node:"+wasHost+"Node01",
    ]

    @Shared //* Required Parameter (need incorrect and empty value)
    def specAdministrativeNames = [
        empty:                          "",
        correctQueue:                   "MySIBJMSAppSpecQueueForDelete",
        correctTopic:                   "MySIBJMSAppSpecTopicForDelete",
        incorrect:                      ":/:/: ForDelete",
    ]

    @Shared //* Required Parameter (need incorrect and empty value)
    def specJNDINames = [
        empty:                          "",
        correctSIBQueue:                "com.jndi.mySIBJMSAppSpecQueueForDelete",
        correctSIBTopic:                "com.jndi.mySIBJMSAppSpecTopicForDelete",
        correctWMQQueue:                "com.jndi.myWMQJMSAppSpecQueueForDelete",
        correctWMQTopic:                "com.jndi.myWMQJMSAppSpecTopicForDelete",
        incorrect:                      "incorrect Spec JNDI Name ForDelete",
    ]

    @Shared //* Required Parameter (need incorrect and empty value)
    def destinationJNDINames = [
        empty:                          "",
        correctSIBQueue:                "com.jndi.mySIBJMSDestSpecQueueForDelete",
        correctSIBTopic:                "com.jndi.mySIBJMSDestSpecTopicForDelete",
        correctWMQQueue:                "com.jndi.myWMQJMSDestSpecQueueForDelete",
        correctWMQTopic:                "com.jndi.myWMQJMSDestSpecTopicForDelete",
        incorrect:                      "incorrect destination JNDI Name ForDelete",
    ]

    @Shared // Required Parameter (need incorrect and empty value)
    def destinationTypes = [
        empty:                          "",
        correctSIBQueue:                "javax.jms.Queue",
        correctSIBTopic:                "javax.jms.Topic",
        correctWMQQueue:                "Queue",
        correctWMQTopic:                "Topic",
        incorrect:                      "Incorrect.destinationTypes",
    ]

}