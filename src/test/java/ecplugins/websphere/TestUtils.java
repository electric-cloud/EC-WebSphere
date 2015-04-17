package ecplugins.websphere;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import static org.junit.Assert.*;


public class TestUtils {

    /**
     * callRunProcedure
     *
     * @param jo
     * @return the jobId of the job launched by runProcedure
     */
    public static String callRunProcedure(JSONObject jo) {

        HttpClient httpClient = new DefaultHttpClient();
        JSONObject result = null;
        try {
            HttpPost httpPostRequest = new HttpPost("http://" + Properties.COMMANDER_USER
                    + ":" + Properties.COMMANDER_PASSWORD + "@" + Properties.COMMANDER_SERVER
                    + ":8000/rest/v1.0/jobs?request=runProcedure");
            StringEntity input = new StringEntity(jo.toString());

            input.setContentType("application/json");
            httpPostRequest.setEntity(input);
            HttpResponse httpResponse = httpClient.execute(httpPostRequest);

            result = new JSONObject(EntityUtils.toString(httpResponse.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }

        if (result != null) {
            try {
                return result.getString("jobId");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return "";

    }
    /**
     * waitForJob: Waits for job to be completed and reports outcome
     *
     * @param jobId
     * @return outcome of job
     */
     static String waitForJob(String jobId) throws IOException, JSONException {

        String url = "http://" + Properties.COMMANDER_USER + ":" + Properties.COMMANDER_PASSWORD +
                "@" + Properties.COMMANDER_SERVER + ":8000/rest/v1.0/jobs/" +
                jobId + "?request=getJobStatus";
        JSONObject jsonObject = performHTTPGet(url);

        try {
            while (!jsonObject.getString("status").equalsIgnoreCase("completed")) {
                jsonObject = performHTTPGet(url);
            }

            return jsonObject.getString("outcome");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";

    }

    /**
     * Wrapper around a HTTP GET to a REST service
     *
     * @param url
     * @return JSONObject
     */
     static JSONObject performHTTPGet(String url) throws IOException, JSONException {

        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpGet httpGetRequest = new HttpGet(url);

            HttpResponse httpResponse = httpClient.execute(httpGetRequest);
            if (httpResponse.getStatusLine().getStatusCode() >= 400) {
                throw new RuntimeException("HTTP GET failed with " +
                        httpResponse.getStatusLine().getStatusCode() + "-" +
                        httpResponse.getStatusLine().getReasonPhrase());
            }
            return new JSONObject(EntityUtils.toString(httpResponse.getEntity()));

        } finally {
            httpClient.getConnectionManager().shutdown();
        }

    }

    /**
     * Create the openstack configuration used for this test suite
     */
     static void createConfiguration() throws JSONException, IOException {

        String response = "";
        JSONObject parentJSONObject = new JSONObject();
        JSONArray actualParameterArray = new JSONArray();

        parentJSONObject.put("projectName", "EC-WebSphere-" + Properties.PLUGIN_VERSION);
        parentJSONObject.put("procedureName", "CreateConfiguration");

        actualParameterArray.put(new JSONObject()
                .put("value", "WebCfg")
                .put("actualParameterName", "config"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "websphere_url")
                .put("value", Properties.WEBSPHERE_URL));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "websphere_port")
                .put("value", Properties.WEBSPHERE_PORT));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "credential")
                .put("value", "web_credentials"));

        parentJSONObject.put("actualParameter", actualParameterArray);

        JSONArray credentialArray = new JSONArray();

        credentialArray.put(new JSONObject()
                .put("credentialName", "web_credentials")
                .put("userName", Properties.WEBSPHERE_USER)
                .put("password", Properties.WEBSPHERE_PASSWORD));

        parentJSONObject.put("credential", credentialArray);

        String jobId = callRunProcedure(parentJSONObject);

        response = waitForJob(jobId);

        // Check job status
        assertEquals("Job completed without errors", "success", response);
    }

    /**
     * Delete the WEBSPHERE configuration used for this test suite (clear previous runs)
     */
     static void deleteConfiguration() throws JSONException, IOException {
        String jobId = "";
        JSONObject param1 = new JSONObject();
        JSONObject jo = new JSONObject();
        jo.put("projectName", "EC-WebSphere-" + Properties.PLUGIN_VERSION);
        jo.put("procedureName", "DeleteConfiguration");

        JSONArray actualParameterArray = new JSONArray();
        actualParameterArray.put(new JSONObject()
                .put("value", "WebCfg")
                .put("actualParameterName", "config"));

        jo.put("actualParameter", actualParameterArray);

         JSONArray credentialArray = new JSONArray();

         credentialArray.put(new JSONObject()
                 .put("credentialName", "web_credentials")
                 .put("userName", Properties.WEBSPHERE_USER)
                 .put("password", Properties.WEBSPHERE_PASSWORD));

         jo.put("credential", credentialArray);

         jobId = callRunProcedure(jo);

        // Block on job completion
        waitForJob(jobId);
        // Do not check job status. Delete will error if it does not exist
        // which is OK since that is the expected state.
    }
}
