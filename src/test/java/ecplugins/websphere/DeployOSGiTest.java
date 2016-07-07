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

import java.util.Properties;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DeployOSGiTest {

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
    public void deployOSGiTest() throws Exception {

        String appName = "automatedTest-sampleOsgiApp";
        String deployUnit = "";
        long jobTimeoutMillis = 5 * 60 * 1000;

        if(props.containsKey(StringConstants.NODE_NAME)){
                // if Node name is provided
            deployUnit = "node=" + props.getProperty(StringConstants.NODE_NAME) + ",";
        }
        deployUnit += "server=" + props.getProperty(StringConstants.SERVER_NAME);

        JSONObject jo = new JSONObject();

        jo.put("projectName", "EC-WebSphere-" + StringConstants.PLUGIN_VERSION);
        jo.put("procedureName", "DeployOSGi");
        jo.put("resourceName", StringConstants.RESOURCE_NAME);

        JSONArray actualParameterArray = new JSONArray();

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "configName")
                .put("value", "WebCfg"));

        actualParameterArray.put(new JSONObject()
                .put("value", props.getProperty(StringConstants.WSADMIN_LOCATION))
                .put("actualParameterName", "wsadminAbsPath"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "externalRepoName")
                .put("value", "automatedTest-testExtRepo"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "externalRepoURL")
                .put("value", "http://felix.apache.org/obr/releases.xml"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "localRepoBundleList")
                .put("value", props.getProperty(StringConstants.LOCAL_REPO_BUNDLE_LIST)));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "ebaPath")
                .put("value", props.getProperty(StringConstants.EBA_FILE_LOCATION)));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "appName")
                .put("value", appName));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "deployUnit")
                .put("value", deployUnit));

        jo.put("actualParameter", actualParameterArray);


        String jobId = TestUtils.callRunProcedure(jo);

        String response = TestUtils.waitForJob(jobId,jobTimeoutMillis);

        // Check job status
        assertEquals("Job completed with errors", "success", response);

    }


    @Test
    public void deployOSGiInvalidEBAFilePathTest() throws Exception {

        String appName = "automatedTest-sampleOsgiApp";
        String deployUnit = "";
        long jobTimeoutMillis = 5 * 60 * 1000;

        if(props.containsKey(StringConstants.NODE_NAME)){
            // if Node name is provided
            deployUnit = "node=" + props.getProperty(StringConstants.NODE_NAME) + ",";
        }
        deployUnit += "server=" + props.getProperty(StringConstants.SERVER_NAME);

        JSONObject jo = new JSONObject();

        jo.put("projectName", "EC-WebSphere-" + StringConstants.PLUGIN_VERSION);
        jo.put("procedureName", "DeployOSGi");
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
                .put("actualParameterName", "externalRepoName")
                .put("value", "automatedTest-testExtRepo"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "externalRepoURL")
                .put("value", "http://felix.apache.org/obr/releases.xml"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "localRepoBundleList")
                .put("value", props.getProperty(StringConstants.LOCAL_REPO_BUNDLE_LIST)));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "ebaPath")
                .put("value", "SOMERandomeFilePath"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "appName")
                .put("value", appName));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "deployUnit")
                .put("value", deployUnit));

        jo.put("actualParameter", actualParameterArray);


        String jobId = TestUtils.callRunProcedure(jo);

        String response = TestUtils.waitForJob(jobId,jobTimeoutMillis);

        // Check job status
        assertEquals("Job completed without errors", "error", response);

    }
}