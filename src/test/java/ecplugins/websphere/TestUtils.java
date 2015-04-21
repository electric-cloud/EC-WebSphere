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

import java.io.*;
import java.util.Properties;

import static org.junit.Assert.*;


public class TestUtils {

    static Properties props;

    static {

        // 1. Load propeties
        props = new Properties();
        InputStream is = null;

        try {
            is = new FileInputStream("ecplugin.properties");
            props.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //2. Create commander workspace
        createCommanderWorkspace();

        //3. Create commander resource
        createCommanderResource();

        //4. Delete any existing plugin configuration
        try {
            deleteConfiguration();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //5 . Create new pluing configuration
        try {
            createConfiguration();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


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
            HttpPost httpPostRequest = new HttpPost("http://" + props.getProperty(StringConstants.COMMANDER_USER)
                    + ":" + props.getProperty(StringConstants.COMMANDER_PASSWORD) + "@" + StringConstants.COMMANDER_SERVER
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

        String url = "http://" + props.getProperty(StringConstants.COMMANDER_USER) + ":" + props.getProperty(StringConstants.COMMANDER_PASSWORD) +
                "@" + StringConstants.COMMANDER_SERVER + ":8000/rest/v1.0/jobs/" +
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
     *  Creates a new workspace. If the workspace already exists,It continues.
     *
     */
    static void createCommanderWorkspace(){

        HttpClient httpClient = new DefaultHttpClient();
        JSONObject jo = new JSONObject();
        JSONObject result = null;

        try {
            HttpPost httpPostRequest = new HttpPost("http://" + props.getProperty(StringConstants.COMMANDER_USER)
                    + ":" + props.getProperty(StringConstants.COMMANDER_PASSWORD) + "@" + StringConstants.COMMANDER_SERVER
                    + ":8000/rest/v1.0/workspaces/");

            jo.put("workspaceName","testAutomationWorkspace");
            jo.put("description","testAutomationWorkspace");
            jo.put("agentDrivePath","C:/Program Files/Electric Cloud/ElectricCommander");
            jo.put("agentUncPath","C:/Program Files/Electric Cloud/ElectricCommander");
            jo.put("agentUnixPath","/opt/electriccloud/electriccommander");
            jo.put("local",true);

            StringEntity input = new StringEntity(jo.toString());

            input.setContentType("application/json");
            httpPostRequest.setEntity(input);
            HttpResponse httpResponse = httpClient.execute(httpPostRequest);

            if(httpResponse.getStatusLine().getStatusCode() == 409) {
                System.out.println("Commander workspace already exists.Continuing....");
                result = null;
            } else if (httpResponse.getStatusLine().getStatusCode() >= 400) {
                throw new RuntimeException("Failed to create commander workspace " +
                        httpResponse.getStatusLine().getStatusCode() + "-" +
                        httpResponse.getStatusLine().getReasonPhrase());
            }
            result = new JSONObject(EntityUtils.toString(httpResponse.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }

    /**
     *
     * @return
     */
    static void createCommanderResource(){

        HttpClient httpClient = new DefaultHttpClient();
        JSONObject jo = new JSONObject();
        JSONObject result = null;

        try {
            HttpPost httpPostRequest = new HttpPost("http://" + props.getProperty(StringConstants.COMMANDER_USER)
                    + ":" + props.getProperty(StringConstants.COMMANDER_PASSWORD) + "@" + StringConstants.COMMANDER_SERVER
                    + ":8000/rest/v1.0/resources/");

            jo.put("resourceName","testAutomationResource");
            jo.put("description","Resource created for test automation");
            jo.put("hostName",props.getProperty(StringConstants.WEBSPHERE_AGENT_IP));
            jo.put("port",props.getProperty(StringConstants.WEBSPHERE_AGENT_PORT));
            jo.put("workspaceName","testAutomationWorkspace");
            jo.put("pools","default");
            jo.put("local",true);

            StringEntity input = new StringEntity(jo.toString());

            input.setContentType("application/json");
            httpPostRequest.setEntity(input);
            HttpResponse httpResponse = httpClient.execute(httpPostRequest);

            if(httpResponse.getStatusLine().getStatusCode() == 409) {
                System.out.println("Commander resource already exists.Continuing....");
                result = null;
            } else if (httpResponse.getStatusLine().getStatusCode() >= 400) {
                throw new RuntimeException("Failed to create commander workspace " +
                        httpResponse.getStatusLine().getStatusCode() + "-" +
                        httpResponse.getStatusLine().getReasonPhrase());
            }
            result = new JSONObject(EntityUtils.toString(httpResponse.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }

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

        parentJSONObject.put("projectName", "EC-WebSphere-" + StringConstants.PLUGIN_VERSION);
        parentJSONObject.put("procedureName", "CreateConfiguration");

        actualParameterArray.put(new JSONObject()
                .put("value", "WebCfg")
                .put("actualParameterName", "config"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "websphere_url")
                .put("value", props.getProperty(StringConstants.WEBSPHERE_URL)));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "websphere_port")
                .put("value", props.getProperty(StringConstants.WEBSPHERE_PORT)));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "credential")
                .put("value", "web_credentials"));

        parentJSONObject.put("actualParameter", actualParameterArray);

        JSONArray credentialArray = new JSONArray();

        credentialArray.put(new JSONObject()
                .put("credentialName", "web_credentials")
                .put("userName", props.getProperty(StringConstants.WEBSPHERE_USER))
                .put("password", props.getProperty(StringConstants.WEBSPHERE_PASSWORD)));

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
        jo.put("projectName", "EC-WebSphere-" + StringConstants.PLUGIN_VERSION);
        jo.put("procedureName", "DeleteConfiguration");

        JSONArray actualParameterArray = new JSONArray();
        actualParameterArray.put(new JSONObject()
                .put("value", "WebCfg")
                .put("actualParameterName", "config"));

        jo.put("actualParameter", actualParameterArray);

         JSONArray credentialArray = new JSONArray();

         credentialArray.put(new JSONObject()
                 .put("credentialName", "web_credentials")
                 .put("userName", props.getProperty(StringConstants.WEBSPHERE_USER))
                 .put("password", props.getProperty(StringConstants.WEBSPHERE_PASSWORD)));

         jo.put("credential", credentialArray);

         jobId = callRunProcedure(jo);

        // Block on job completion
        waitForJob(jobId);
        // Do not check job status. Delete will error if it does not exist
        // which is OK since that is the expected state.
    }

}
