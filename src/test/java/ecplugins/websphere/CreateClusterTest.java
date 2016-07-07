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


public class CreateClusterTest {

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
    public void createClusterTest() throws Exception {

        String clusterMembers = props.getProperty(StringConstants.CLUSTER_MEMBER_NODE_NAME) + "=" + props.getProperty(StringConstants.CLUSTER_MEMBER_SERVER_NAME);
        long jobTimeoutMillis = 5 * 60 * 1000;
        JSONObject jo = new JSONObject();

        jo.put("projectName", "EC-WebSphere-" + StringConstants.PLUGIN_VERSION);
        jo.put("procedureName", "CreateCluster");
        jo.put("resourceName", StringConstants.RESOURCE_NAME);

        JSONArray actualParameterArray = new JSONArray();

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "configname")
                .put("value", "WebCfg"));

        actualParameterArray.put(new JSONObject()
                .put("value", props.getProperty(StringConstants.WSADMIN_LOCATION))
                .put("actualParameterName", "wsadminabspath"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "cellname")
                .put("value", props.getProperty(StringConstants.CLUSTER_CELL_NAME)));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "clusterName")
                .put("value", props.getProperty(StringConstants.CLUSTER_NAME)));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "clusterMembers")
                .put("value", clusterMembers));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "deployApp")
                .put("value", "1"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "appname")
                .put("value", props.getProperty(StringConstants.APP_NAME) + "onCluster"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "apppath")
                .put("value", props.getProperty(StringConstants.SAMPLE_WAR_LOCATION)));

        jo.put("actualParameter", actualParameterArray);


        String jobId = TestUtils.callRunProcedure(jo);

        String response = TestUtils.waitForJob(jobId,jobTimeoutMillis);

        // Check job status
        assertEquals("Job completed with errors", "success", response);

    }



}