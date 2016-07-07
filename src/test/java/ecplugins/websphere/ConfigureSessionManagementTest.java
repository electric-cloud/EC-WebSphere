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

public class ConfigureSessionManagementTest {

    private static Properties props;
    private long jobTimeoutMillis;

    @BeforeClass
    public void setup() throws Exception {

        props = TestUtils.getProperties();
        jobTimeoutMillis  = 3 * 60 * 1000;
        TestUtils.createCommanderWorkspace();
        TestUtils.createCommanderResource();
        TestUtils.deleteConfiguration();
        TestUtils.createConfiguration();
        TestUtils.setDefaultResourceAndWorkspace();
    }



    @Test
    public void ConfigureSessionManagementTest() throws Exception {

        JSONObject jo = new JSONObject();

        jo.put("projectName", "EC-WebSphere-" + StringConstants.PLUGIN_VERSION);
        jo.put("procedureName", "ConfigureSession");
        jo.put("resourceName", StringConstants.RESOURCE_NAME);

        JSONArray actualParameterArray = new JSONArray();
        actualParameterArray.put(new JSONObject()
                .put("value",  props.getProperty(StringConstants.WSADMIN_LOCATION))
                .put("actualParameterName", "wsadminAbsPath"));

            actualParameterArray.put(new JSONObject()
                    .put("actualParameterName", "configName")
                    .put("value", "WebCfg"));

            actualParameterArray.put(new JSONObject()
                    .put("actualParameterName", "appName")
                    .put("value", StringConstants.APP_NAME));

            actualParameterArray.put(new JSONObject()
                    .put("actualParameterName", "enableCookie")
                    .put("value", "1"));

            actualParameterArray.put(new JSONObject()
                    .put("actualParameterName", "maxInMemorySessionCount")
                    .put("value", "1000"));

            actualParameterArray.put(new JSONObject()
                    .put("actualParameterName", "allowOverflow")
                    .put("value", "1"));

            actualParameterArray.put(new JSONObject()
                        .put("actualParameterName", "enableSerializedSession")
                        .put("value", "1"));

            actualParameterArray.put(new JSONObject()
                    .put("actualParameterName", "accessSessionOnTimeout")
                    .put("value", "true"));

            actualParameterArray.put(new JSONObject()
                    .put("actualParameterName", "maxWaitTime")
                    .put("value", "10"));

            actualParameterArray.put(new JSONObject()
                    .put("actualParameterName", "invalidTimeout")
                    .put("value", "2"));

            actualParameterArray.put(new JSONObject()
                        .put("actualParameterName", "sessionPersistMode")
                        .put("value", "NONE"));


        jo.put("actualParameter", actualParameterArray);


        String jobId = TestUtils.callRunProcedure(jo);

        String response = TestUtils.waitForJob(jobId,jobTimeoutMillis);

        // Check job status
        assertEquals("Job completed with errors", "success", response);

    }

    @Test
    public void ConfigureSessionManagementForNonExistentAppTest() throws Exception {

        JSONObject jo = new JSONObject();

        jo.put("projectName", "EC-WebSphere-" + StringConstants.PLUGIN_VERSION);
        jo.put("procedureName", "ConfigureSession");
        jo.put("resourceName", StringConstants.RESOURCE_NAME);

        JSONArray actualParameterArray = new JSONArray();
        actualParameterArray.put(new JSONObject()
                .put("value", "C:/Program Files (x86)/IBM/WebSphere/AppServer/bin/wsadmin.bat")
                .put("actualParameterName", "wsadminAbsPath"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "configName")
                .put("value", "WebCfg"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "connectionType")
                .put("value", "soap"));

        // Append string to application name
        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "appName")
                .put("value", StringConstants.APP_NAME + "dummyValue"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "enableCookie")
                .put("value", "1"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "maxInMemorySessionCount")
                .put("value", "1000"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "allowOverflow")
                .put("value", "1"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "enableSerializedSession")
                .put("value", "1"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "accessSessionOnTimeout")
                .put("value", "true"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "maxWaitTime")
                .put("value", "10"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "invalidTimeout")
                .put("value", "2"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "sessionPersistMode")
                .put("value", "NONE"));


        jo.put("actualParameter", actualParameterArray);


        String jobId = TestUtils.callRunProcedure(jo);

        String response = TestUtils.waitForJob(jobId,jobTimeoutMillis);

        // Check job status
        assertEquals("Job completed without errors", "error", response);

    }


}