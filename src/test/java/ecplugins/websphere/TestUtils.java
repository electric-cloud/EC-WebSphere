/**
 *  Copyright 2015 Electric Cloud, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package ecplugins.websphere;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
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

    private static Properties props;
    private static final long jobStatusPollIntervalMillis = 15000;
    private static boolean isCommanderWorkspaceCreatedSuccessfully = false;
    private static boolean isCommanderResourceCreatedSuccessfully = false;
    private static boolean isConfigDeletedSuccessfully = false;
    private static boolean isConfigCreatedSuccessfully = false;
    private static boolean isResourceSetSuccessfully = false;

    public static Properties getProperties() throws Exception {

        if(props == null){
            props = new Properties();
            InputStream is = null;
            is = new FileInputStream("ecplugin.properties");
            props.load(is);
            is.close();
        }

        return props;
    }

    public static void setDefaultResourceAndWorkspace() throws Exception {

        if(!isResourceSetSuccessfully){

            HttpClient httpClient = new DefaultHttpClient();
            JSONObject jo = new JSONObject();

            jo.put("projectName", "EC-WebSphere-" + StringConstants.PLUGIN_VERSION);
            jo.put("resourceName", StringConstants.RESOURCE_NAME);
            jo.put("workspaceName", StringConstants.WORKSPACE_NAME);

            HttpPut httpPutRequest = new HttpPut("http://" + props.getProperty(StringConstants.COMMANDER_USER)
                    + ":" + props.getProperty(StringConstants.COMMANDER_PASSWORD) + "@" + StringConstants.COMMANDER_SERVER
                    + ":8000/rest/v1.0/projects/" + "EC-WebSphere-" + StringConstants.PLUGIN_VERSION);

            StringEntity input = new StringEntity(jo.toString());

            input.setContentType("application/json");
            httpPutRequest.setEntity(input);
            HttpResponse httpResponse = httpClient.execute(httpPutRequest);

            if (httpResponse.getStatusLine().getStatusCode() >= 400) {
                throw new RuntimeException("Failed to set default resource  " +
                        StringConstants.RESOURCE_NAME + " to project " +
                        "EC-WebSphere-" + StringConstants.PLUGIN_VERSION);
            }
            System.out.println("Set the default resource as " + StringConstants.RESOURCE_NAME + " and default workspace as " + StringConstants.WORKSPACE_NAME + " successfully.");
            isResourceSetSuccessfully = true;
        }
    }
    /**
     * callRunProcedure
     *
     * @param jo
     * @return the jobId of the job launched by runProcedure
     */
    public static String callRunProcedure(JSONObject jo) throws Exception {

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
            return result.getString("jobId");

        } finally {
            httpClient.getConnectionManager().shutdown();
        }

    }
    /**
     * waitForJob: Waits for job to be completed and reports outcome
     *
     * @param jobId
     * @return outcome of job
     */
     static String waitForJob(String jobId, long jobTimeOutMillis) throws Exception {

        long timeTaken = 0;

        String url = "http://" + props.getProperty(StringConstants.COMMANDER_USER) + ":" + props.getProperty(StringConstants.COMMANDER_PASSWORD) +
                "@" + StringConstants.COMMANDER_SERVER + ":8000/rest/v1.0/jobs/" +
                jobId + "?request=getJobStatus";
        JSONObject jsonObject = performHTTPGet(url);


        while (!jsonObject.getString("status").equalsIgnoreCase("completed")) {
            Thread.sleep(jobStatusPollIntervalMillis);
            jsonObject = performHTTPGet(url);
            timeTaken += jobStatusPollIntervalMillis;
            if(timeTaken > jobTimeOutMillis){
                throw new Exception("Job did not completed within time.");
            }
        }

        return jsonObject.getString("outcome");

    }

    /**
     *  Creates a new workspace. If the workspace already exists,It continues.
     *
     */
    static void createCommanderWorkspace() throws Exception {

        if( !isCommanderWorkspaceCreatedSuccessfully ) {

            HttpClient httpClient = new DefaultHttpClient();
            JSONObject jo = new JSONObject();


            try {
                HttpPost httpPostRequest = new HttpPost("http://" + props.getProperty(StringConstants.COMMANDER_USER)
                        + ":" + props.getProperty(StringConstants.COMMANDER_PASSWORD) + "@" + StringConstants.COMMANDER_SERVER
                        + ":8000/rest/v1.0/workspaces/");

                jo.put("workspaceName", StringConstants.WORKSPACE_NAME);
                jo.put("description", "testAutomationWorkspace");
                jo.put("agentDrivePath", "C:/Program Files/Electric Cloud/ElectricCommander");
                jo.put("agentUncPath", "C:/Program Files/Electric Cloud/ElectricCommander");
                jo.put("agentUnixPath", "/opt/electriccloud/electriccommander");
                jo.put("local", true);

                StringEntity input = new StringEntity(jo.toString());

                input.setContentType("application/json");
                httpPostRequest.setEntity(input);
                HttpResponse httpResponse = httpClient.execute(httpPostRequest);

                if (httpResponse.getStatusLine().getStatusCode() == 409) {
                    System.out.println("Commander workspace already exists.Continuing....");
                } else if (httpResponse.getStatusLine().getStatusCode() >= 400) {
                    throw new RuntimeException("Failed to create commander workspace " +
                            httpResponse.getStatusLine().getStatusCode() + "-" +
                            httpResponse.getStatusLine().getReasonPhrase());
                }
                // Indicate successful creating of workspace
                isCommanderWorkspaceCreatedSuccessfully = true;

            } finally {
                httpClient.getConnectionManager().shutdown();
            }
        }

    }

    /**
     *
     * @return
     */
    static void createCommanderResource() throws Exception {

        if (!isCommanderResourceCreatedSuccessfully) {    // If CommanderResource not created before

            HttpClient httpClient = new DefaultHttpClient();
            JSONObject jo = new JSONObject();

            try {
                HttpPost httpPostRequest = new HttpPost("http://" + props.getProperty(StringConstants.COMMANDER_USER)
                        + ":" + props.getProperty(StringConstants.COMMANDER_PASSWORD) + "@" + StringConstants.COMMANDER_SERVER
                        + ":8000/rest/v1.0/resources/");

                jo.put("resourceName", StringConstants.RESOURCE_NAME);
                jo.put("description", "Resource created for test automation");
                jo.put("hostName", props.getProperty(StringConstants.WEBSPHERE_AGENT_IP));
                jo.put("port", props.getProperty(StringConstants.WEBSPHERE_AGENT_PORT));
                jo.put("workspaceName", StringConstants.WORKSPACE_NAME);
                jo.put("pools", "default");
                jo.put("local", true);

                StringEntity input = new StringEntity(jo.toString());

                input.setContentType("application/json");
                httpPostRequest.setEntity(input);
                HttpResponse httpResponse = httpClient.execute(httpPostRequest);

                if (httpResponse.getStatusLine().getStatusCode() == 409) {
                    System.out.println("Commander resource already exists.Continuing....");

                } else if (httpResponse.getStatusLine().getStatusCode() >= 400) {
                    throw new RuntimeException("Failed to create commander workspace " +
                            httpResponse.getStatusLine().getStatusCode() + "-" +
                            httpResponse.getStatusLine().getReasonPhrase());
                }
                // Indicate successful creation commander resource.
                isCommanderResourceCreatedSuccessfully = true;
            } finally {
                httpClient.getConnectionManager().shutdown();
            }
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
     static void createConfiguration() throws Exception {

         long jobTimeoutMillis = 3 * 60 * 1000;
         if(isConfigCreatedSuccessfully == false) {

             String response = "";
             JSONObject parentJSONObject = new JSONObject();
             JSONArray actualParameterArray = new JSONArray();

             parentJSONObject.put("projectName", "EC-WebSphere-" + StringConstants.PLUGIN_VERSION);
             parentJSONObject.put("procedureName", "CreateConfiguration");

             actualParameterArray.put(new JSONObject()
                     .put("value", "WebCfg")
                     .put("actualParameterName", "config"));

             actualParameterArray.put(new JSONObject()
                     .put("actualParameterName", "conntype")
                     .put("value", "SOAP"));

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

             response = waitForJob(jobId,jobTimeoutMillis);

             // Check job status
             assertEquals("Job completed without errors", "success", response);

             isConfigCreatedSuccessfully = true;
         }
    }

    /**
     * Delete the WEBSPHERE configuration used for this test suite (clear previous runs)
     */
     static void deleteConfiguration() throws Exception {

         long jobTimeoutMillis = 3 * 60 * 1000;
         if (isConfigDeletedSuccessfully == false) {

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
             waitForJob(jobId,jobTimeoutMillis);
             // Do not check job status. Delete will error if it does not exist
             // which is OK since that is the expected state.

            isConfigDeletedSuccessfully = true;
         }

     }
}
