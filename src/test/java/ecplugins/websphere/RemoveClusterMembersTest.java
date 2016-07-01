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
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;


public class RemoveClusterMembersTest {

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
    public void removeClusterMembersTest() throws Exception {

        long jobTimeoutMillis = 5 * 60 * 1000;
        JSONObject jo = new JSONObject();
        String clusterMembers = props.getProperty(StringConstants.CLUSTER_MEMBER_NODE_NAME) + "=" + props.getProperty(StringConstants.CLUSTER_MEMBER_SERVER_NAME);

        jo.put("projectName", "EC-WebSphere-" + StringConstants.PLUGIN_VERSION);
        jo.put("procedureName", "RemoveClusterMembers");

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
                .put("actualParameterName", "clusterName")
                .put("value", props.getProperty(StringConstants.CLUSTER_NAME)));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "clusterMembers")
                .put("value", clusterMembers));

        jo.put("actualParameter", actualParameterArray);

        String jobId = TestUtils.callRunProcedure(jo);

        String response = TestUtils.waitForJob(jobId,jobTimeoutMillis);

        // Check job status
        assertEquals("Job completed with errors", "success", response);

    }


    @Test
    public void removeClusterMembersFromInvalidClusterTest() throws Exception {

        long jobTimeoutMillis = 5 * 60 * 1000;
        JSONObject jo = new JSONObject();
        String clusterMembers = props.getProperty(StringConstants.CLUSTER_MEMBER_NODE_NAME) + "=" + props.getProperty(StringConstants.CLUSTER_MEMBER_SERVER_NAME);

        jo.put("projectName", "EC-WebSphere-" + StringConstants.PLUGIN_VERSION);
        jo.put("procedureName", "RemoveClusterMembers");

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
                .put("actualParameterName", "clusterName")
                .put("value", props.getProperty(StringConstants.CLUSTER_NAME) + "someRandomClusterName"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "clusterMembers")
                .put("value", clusterMembers));

        jo.put("actualParameter", actualParameterArray);

        String jobId = TestUtils.callRunProcedure(jo);

        String response = TestUtils.waitForJob(jobId,jobTimeoutMillis);

        // Check job status
        assertEquals("Job completed without errors", "error", response);

    }

    @Test
    public void removeInvalidClusterMembersTest() throws Exception {

        long jobTimeoutMillis = 5 * 60 * 1000;
        JSONObject jo = new JSONObject();
        String clusterMembers = props.getProperty(StringConstants.CLUSTER_MEMBER_NODE_NAME) + "=" + props.getProperty(StringConstants.CLUSTER_MEMBER_SERVER_NAME);

        jo.put("projectName", "EC-WebSphere-" + StringConstants.PLUGIN_VERSION);
        jo.put("procedureName", "RemoveClusterMembers");

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
                .put("actualParameterName", "clusterName")
                .put("value", props.getProperty(StringConstants.CLUSTER_NAME)));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "clusterMembers")
                .put("value", clusterMembers + "someRandomeString"));

        jo.put("actualParameter", actualParameterArray);

        String jobId = TestUtils.callRunProcedure(jo);

        String response = TestUtils.waitForJob(jobId,jobTimeoutMillis);

        // Check job status
        assertEquals("Job completed without errors", "error", response);

    }
}