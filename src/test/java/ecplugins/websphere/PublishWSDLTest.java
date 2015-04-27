package ecplugins.websphere;

import org.eclipse.jdt.internal.compiler.impl.StringConstant;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;


public class PublishWSDLTest {

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
    public void publishWSDLTest() throws Exception {


        long jobTimeoutMillis = 5 * 60 * 1000;
        JSONObject jo = new JSONObject();

        jo.put("projectName", "EC-WebSphere-" + StringConstants.PLUGIN_VERSION);
        jo.put("procedureName", "PublishWSDL");
        jo.put("resourceName", StringConstants.RESOURCE_NAME);

        JSONArray actualParameterArray = new JSONArray();

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "configname")
                .put("value", "WebCfg"));

        actualParameterArray.put(new JSONObject()
                .put("value", props.getProperty(StringConstants.WSADMIN_LOCATION))
                .put("actualParameterName", "wsadminabspath"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "connectiontype")
                .put("value", "soap"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "appname")
                .put("value", props.getProperty(StringConstants.WEBSERVICE_APP_NAME)));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "publish_location")
                .put("value", props.getProperty(StringConstants.WSDL_LOCATION)));

        jo.put("actualParameter", actualParameterArray);


        String jobId = TestUtils.callRunProcedure(jo);

        String response = TestUtils.waitForJob(jobId,jobTimeoutMillis);

        // Check job status
        assertEquals("Job completed with errors", "success", response);

    }

    @Test
    public void publishWSDLInvalidAppNameTest() throws Exception {

        long jobTimeoutMillis = 5 * 60 * 1000;
        JSONObject jo = new JSONObject();

        jo.put("projectName", "EC-WebSphere-" + StringConstants.PLUGIN_VERSION);
        jo.put("procedureName", "PublishWSDL");
        jo.put("resourceName", StringConstants.RESOURCE_NAME);

        JSONArray actualParameterArray = new JSONArray();

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "configname")
                .put("value", "WebCfg"));

        actualParameterArray.put(new JSONObject()
                .put("value", props.getProperty(StringConstants.WSADMIN_LOCATION))
                .put("actualParameterName", "wsadminabspath"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "connectiontype")
                .put("value", "soap"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "appname")
                .put("value", props.getProperty(StringConstants.WEBSERVICE_APP_NAME) + "987$sdf"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "publish_location")
                .put("value", props.getProperty(StringConstants.WSDL_LOCATION)));

        jo.put("actualParameter", actualParameterArray);


        String jobId = TestUtils.callRunProcedure(jo);

        String response = TestUtils.waitForJob(jobId,jobTimeoutMillis);

        // Check job status
        assertEquals("Job completed without errors", "error", response);

    }
}