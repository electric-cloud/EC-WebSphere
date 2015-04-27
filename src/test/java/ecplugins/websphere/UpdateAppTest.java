package ecplugins.websphere;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class UpdateAppTest {

    private static Properties props;

    @BeforeClass
    public static void setup() throws Exception {

        props = TestUtils.getProperties();

        TestUtils.createCommanderWorkspace();
        TestUtils.createCommanderResource();
        TestUtils.deleteConfiguration();
        TestUtils.createConfiguration();
    }

    // Before running this test ensure that the application to be updated is already deployed and running.2
    @Test
    public void UpdateAppTest() throws Exception {

        long jobTimeoutMillis = 5 * 60 * 1000;
        JSONObject jo = new JSONObject();

        jo.put("projectName", "EC-WebSphere-" + StringConstants.PLUGIN_VERSION);
        jo.put("procedureName", "UpdateApp");
        jo.put("resourceName", StringConstants.RESOURCE_NAME);

        JSONArray actualParameterArray = new JSONArray();

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "configName")
                .put("value", "WebCfg"));

        actualParameterArray.put(new JSONObject()
                .put("value", props.getProperty(StringConstants.WSADMIN_LOCATION))
                .put("actualParameterName", "wsadminAbsPath"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "connectionType")
                .put("value", "soap"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "appName")
                .put("value", props.getProperty(StringConstants.APP_NAME_TO_UPDATE)));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "contentType")
                .put("value", "file"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "operation")
                .put("value", "update"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "content")
                .put("value", props.getProperty(StringConstants.SRC_FILE_LOCATION)));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "contentURI")
                .put("value", props.getProperty(StringConstants.URI_OF_FILE_TO_REPLACE)));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "isAppOnCluster")
                .put("value", "0"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "serverName")
                .put("value", props.getProperty(StringConstants.SERVER_NAME)));

        jo.put("actualParameter", actualParameterArray);


        String jobId = TestUtils.callRunProcedure(jo);

        String response = TestUtils.waitForJob(jobId, jobTimeoutMillis);

        // Check job status
        assertEquals("Job completed with errors", "success", response);

    }

    @Test
    public void UpdateAppInvalidAppNameTest() throws Exception {

        long jobTimeoutMillis = 5 * 60 * 1000;
        JSONObject jo = new JSONObject();

        jo.put("projectName", "EC-WebSphere-" + StringConstants.PLUGIN_VERSION);
        jo.put("procedureName", "UpdateApp");
        jo.put("resourceName", StringConstants.RESOURCE_NAME);

        JSONArray actualParameterArray = new JSONArray();

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "configName")
                .put("value", "WebCfg"));

        actualParameterArray.put(new JSONObject()
                .put("value", props.getProperty(StringConstants.WSADMIN_LOCATION))
                .put("actualParameterName", "wsadminAbsPath"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "connectionType")
                .put("value", "soap"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "appName")
                .put("value", props.getProperty(StringConstants.APP_NAME_TO_UPDATE) + "somexysRandomString"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "contentType")
                .put("value", "file"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "operation")
                .put("value", "update"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "content")
                .put("value", props.getProperty(StringConstants.SRC_FILE_LOCATION)));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "contentURI")
                .put("value", props.getProperty(StringConstants.URI_OF_FILE_TO_REPLACE)));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "isAppOnCluster")
                .put("value", "0"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "serverName")
                .put("value", props.getProperty(StringConstants.SERVER_NAME)));

        jo.put("actualParameter", actualParameterArray);


        String jobId = TestUtils.callRunProcedure(jo);

        String response = TestUtils.waitForJob(jobId, jobTimeoutMillis);

        // Check job status
        assertEquals("Job completed with errors", "error", response);

    }

    @Test
    public void UpdateAppInvalidOperationContentTypeCombinationNameTest() throws Exception {

        long jobTimeoutMillis = 5 * 60 * 1000;
        JSONObject jo = new JSONObject();

        jo.put("projectName", "EC-WebSphere-" + StringConstants.PLUGIN_VERSION);
        jo.put("procedureName", "UpdateApp");
        jo.put("resourceName", StringConstants.RESOURCE_NAME);

        JSONArray actualParameterArray = new JSONArray();

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "configName")
                .put("value", "WebCfg"));

        actualParameterArray.put(new JSONObject()
                .put("value", props.getProperty(StringConstants.WSADMIN_LOCATION))
                .put("actualParameterName", "wsadminAbsPath"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "connectionType")
                .put("value", "soap"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "appName")
                .put("value", props.getProperty(StringConstants.APP_NAME_TO_UPDATE)));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "contentType")
                .put("value", "app"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "operation")
                .put("value", "delete"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "content")
                .put("value", props.getProperty(StringConstants.SRC_FILE_LOCATION)));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "contentURI")
                .put("value", props.getProperty(StringConstants.URI_OF_FILE_TO_REPLACE)));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "isAppOnCluster")
                .put("value", "0"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "serverName")
                .put("value", props.getProperty(StringConstants.SERVER_NAME)));

        jo.put("actualParameter", actualParameterArray);


        String jobId = TestUtils.callRunProcedure(jo);

        String response = TestUtils.waitForJob(jobId, jobTimeoutMillis);

        // For update application procedure, delete is invalid operation for entire application content type.
        // Hence error is expected.
        assertEquals("Job completed with errors", "error", response);

    }

    @Test
    public void UpdateAppInvalidDeploymentOptionTest() throws Exception {

        // If isAppDeployedOnCluster is selected but the cluster name is not provided
        // then its an error condition.
        long jobTimeoutMillis = 5 * 60 * 1000;
        JSONObject jo = new JSONObject();

        jo.put("projectName", "EC-WebSphere-" + StringConstants.PLUGIN_VERSION);
        jo.put("procedureName", "UpdateApp");
        jo.put("resourceName", StringConstants.RESOURCE_NAME);

        JSONArray actualParameterArray = new JSONArray();

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "configName")
                .put("value", "WebCfg"));

        actualParameterArray.put(new JSONObject()
                .put("value", props.getProperty(StringConstants.WSADMIN_LOCATION))
                .put("actualParameterName", "wsadminAbsPath"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "connectionType")
                .put("value", "soap"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "appName")
                .put("value", props.getProperty(StringConstants.APP_NAME_TO_UPDATE)));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "contentType")
                .put("value", "app"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "operation")
                .put("value", "delete"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "content")
                .put("value", props.getProperty(StringConstants.SRC_FILE_LOCATION)));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "contentURI")
                .put("value", props.getProperty(StringConstants.URI_OF_FILE_TO_REPLACE)));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "isAppOnCluster")
                .put("value", "1"));

        // Instead of cluster name, servername is supplied: hence error condition
        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "serverName")
                .put("value", props.getProperty(StringConstants.SERVER_NAME)));

        jo.put("actualParameter", actualParameterArray);


        String jobId = TestUtils.callRunProcedure(jo);

        String response = TestUtils.waitForJob(jobId, jobTimeoutMillis);

        // For update application procedure, delete is invalid operation for entire application content type.
        // Hence error is expected.
        assertEquals("Job completed with errors", "error", response);

    }
}