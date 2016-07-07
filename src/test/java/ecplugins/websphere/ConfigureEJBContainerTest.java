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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class ConfigureEJBContainerTest {

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
    public void EJBContainerTestWithoutReplicationDomain() throws Exception {

        long jobTimeoutMillis = 5 * 60 * 1000;

        JSONObject jo = new JSONObject();
        jo.put("projectName", "EC-WebSphere-" + StringConstants.PLUGIN_VERSION);
        jo.put("procedureName", "ConfigEJBContainer");
        jo.put("resourceName", StringConstants.RESOURCE_NAME);

        JSONArray actualParameterArray = new JSONArray();

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "configName")
                .put("value", "WebCfg"));

        actualParameterArray.put(new JSONObject()
                .put("value", props.getProperty(StringConstants.WSADMIN_LOCATION))
                .put("actualParameterName", "wsadminAbsPath"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "cellName")
                .put("value", props.getProperty(StringConstants.CELL_NAME)));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "nodeName")
                .put("value", props.getProperty(StringConstants.NODE_NAME)));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "serverName")
                .put("value", props.getProperty(StringConstants.SERVER_NAME)));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "passivationDirectory")
                .put("value", "C:/Program Files"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "cacheSize")
                .put("value", "30000"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "cleanupInterval")
                .put("value", "5000"));

        jo.put("actualParameter", actualParameterArray);


        String jobId = TestUtils.callRunProcedure(jo);

        String response = TestUtils.waitForJob(jobId, jobTimeoutMillis);

        // Check job status
        assertEquals("Job completed with errors", "success", response);

    }

    @Test
    public void EJBContainerTestWithReplicationDomain() throws Exception {

        long jobTimeoutMillis = 5 * 60 * 1000;
        JSONObject jo = new JSONObject();

        jo.put("projectName", "EC-WebSphere-" + StringConstants.PLUGIN_VERSION);
        jo.put("procedureName", "ConfigEJBContainer");
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
                .put("actualParameterName", "cellName")
                .put("value", props.getProperty(StringConstants.CELL_NAME)));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "nodeName")
                .put("value", props.getProperty(StringConstants.NODE_NAME)));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "serverName")
                .put("value", props.getProperty(StringConstants.SERVER_NAME)));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "passivationDirectory")
                .put("value", "C:/Program Files"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "cacheSize")
                .put("value", "30000"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "cleanupInterval")
                .put("value", "5000"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "enableSFSBFailover")
                .put("value", "true"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "messageBrokerDomainName")
                .put("value", "xyz"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "dataReplicationMode")
                .put("value", "both"));

        jo.put("actualParameter", actualParameterArray);


        String jobId = TestUtils.callRunProcedure(jo);

        String response = TestUtils.waitForJob(jobId,jobTimeoutMillis);

        // Check job status
        assertEquals("Job completed with errors", "success", response);

    }

    @Test
    public void EJBContainerTestInvalideServer() throws Exception {

        long jobTimeoutMillis = 5 * 60 * 1000;
        String randomString ="ad7daf81@&*";
        JSONObject jo = new JSONObject();

        jo.put("projectName", "EC-WebSphere-" + StringConstants.PLUGIN_VERSION);
        jo.put("procedureName", "ConfigEJBContainer");
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
                .put("actualParameterName", "cellName")
                .put("value", props.getProperty(StringConstants.CELL_NAME)));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "nodeName")
                .put("value", props.getProperty(StringConstants.NODE_NAME)));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "serverName")
                .put("value", props.getProperty(StringConstants.SERVER_NAME) + randomString));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "passivationDirectory")
                .put("value", "C:/Program Files"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "cacheSize")
                .put("value", "30000"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "cleanupInterval")
                .put("value", "5000"));


        jo.put("actualParameter", actualParameterArray);


        String jobId = TestUtils.callRunProcedure(jo);

        String response = TestUtils.waitForJob(jobId,jobTimeoutMillis);

        // Invalid server name supplied.Test is expected to fail
        // Check job status
        assertEquals("Job completed without errors", "error", response);

    }

}