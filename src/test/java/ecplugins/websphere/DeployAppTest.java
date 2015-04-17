package ecplugins.websphere;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DeployAppTest {

    @BeforeClass
    public static void setup() throws JSONException, IOException {

        TestUtils.deleteConfiguration();
        TestUtils.createConfiguration();
    }

    @Test
    public void deployAppTest() throws JSONException, IOException {

        String appName = "automatedTest-testApp";
        String apppath = Properties.SAMPLE_WAR_LOCATION;

        JSONObject jo = new JSONObject();

        jo.put("projectName", "EC-WebSphere-" + Properties.PLUGIN_VERSION);
        jo.put("procedureName", "DeployApp");


        JSONArray actualParameterArray = new JSONArray();
        actualParameterArray.put(new JSONObject()
                .put("value", "C:/Program Files (x86)/IBM/WebSphere/AppServer/bin/wsadmin.bat")
                .put("actualParameterName", "wsadminabspath"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "appname")
                .put("value", appName));
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

        String response = TestUtils.waitForJob(jobId);

        // Check job status
        assertEquals("Job completed with errors", "success", response);

    }
}