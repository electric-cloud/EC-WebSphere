package ecplugins.websphere;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.Properties;
import static org.junit.Assert.assertEquals;


public class DeployAppTest {

    private static Properties props;

    @BeforeClass
    public static void setup() throws Exception {

        props = TestUtils.getProperties();

        TestUtils.createCommanderWorkspace();
        TestUtils.createCommanderResource();
        TestUtils.deleteConfiguration();
        TestUtils.createConfiguration();
        TestUtils.setDefaultResourceAndWorkspace();
    }

    @Test
    public void deployAppTest() throws Exception {

        String apppath = props.getProperty(StringConstants.SAMPLE_WAR_LOCATION);
        long jobTimeoutMillis = 5 * 60 * 1000;
        JSONObject jo = new JSONObject();

        jo.put("projectName", "EC-WebSphere-" + StringConstants.PLUGIN_VERSION);
        jo.put("procedureName", "DeployApp");
        jo.put("resourceName", "testAutomationResource");

        JSONArray actualParameterArray = new JSONArray();
        actualParameterArray.put(new JSONObject()
                .put("value", props.getProperty(StringConstants.WSADMIN_LOCATION))
                .put("actualParameterName", "wsadminabspath"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "appname")
                .put("value", props.getProperty(StringConstants.APP_NAME)));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "apppath")
                .put("value", apppath));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "configname")
                .put("value", "WebCfg"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "connectiontype")
                .put("value", "soap"));

        jo.put("actualParameter", actualParameterArray);

        String jobId = TestUtils.callRunProcedure(jo);

        String response = TestUtils.waitForJob(jobId,jobTimeoutMillis);

        // Check job status
        assertEquals("Job completed with errors", "success", response);

    }


    @Test
    public void deployAppInvalidWsadminPathTest() throws Exception {

        String apppath = props.getProperty(StringConstants.SAMPLE_WAR_LOCATION);
        long jobTimeoutMillis = 5 * 60 * 1000;
        JSONObject jo = new JSONObject();

        jo.put("projectName", "EC-WebSphere-" + StringConstants.PLUGIN_VERSION);
        jo.put("procedureName", "DeployApp");
        jo.put("resourceName", "testAutomationResource");

        JSONArray actualParameterArray = new JSONArray();
        actualParameterArray.put(new JSONObject()
                .put("value", props.getProperty(StringConstants.WSADMIN_LOCATION) + "someRandomString878*")
                .put("actualParameterName", "wsadminabspath"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "appname")
                .put("value", props.getProperty(StringConstants.APP_NAME)));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "apppath")
                .put("value", apppath));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "configname")
                .put("value", "WebCfg"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "connectiontype")
                .put("value", "soap"));

        jo.put("actualParameter", actualParameterArray);

        String jobId = TestUtils.callRunProcedure(jo);

        String response = TestUtils.waitForJob(jobId,jobTimeoutMillis);

        // Check job status
        assertEquals("Job completed without errors", "error", response);

    }
}