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

public class CreateMailSessionTest {

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
    public void createMailSessionTest() throws Exception {

        String scope = "/Cell:" + props.getProperty(StringConstants.CELL_NAME) + "/Node:" + props.getProperty(StringConstants.NODE_NAME) + "/Server:" + props.getProperty(StringConstants.SERVER_NAME) + "/";
        long jobTimeoutMillis = 5 * 60 * 1000;
        JSONObject jo = new JSONObject();

        jo.put("projectName", "EC-WebSphere-" + StringConstants.PLUGIN_VERSION);
        jo.put("procedureName", "CreateMailSession");
        jo.put("resourceName", StringConstants.RESOURCE_NAME);

        JSONArray actualParameterArray = new JSONArray();

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "configName")
                .put("value", "WebCfg"));

        actualParameterArray.put(new JSONObject()
                .put("value", props.getProperty(StringConstants.WSADMIN_LOCATION))
                .put("actualParameterName", "wsadminAbsPath"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "scope")
                .put("value", scope));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "mailSessionName")
                .put("value", "automatedTest-testMailSession"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "mailSessionDesc")
                .put("value", "Mail session created during automated plugin testing."));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "mailSessionJNDIName")
                .put("value", "jndi/automatedTest-testMailSession"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "debug")
                .put("value", "1"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "mailTransportHost")
                .put("value", "smtp.host.com"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "mailTransportPort")
                .put("value", "25"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "mailTransportUser")
                .put("value","mail_credentials"));

        jo.put("actualParameter", actualParameterArray);

        JSONArray credentialArray = new JSONArray();

        credentialArray.put(new JSONObject()
                .put("credentialName", "mail_credentials")
                .put("userName", props.getProperty(StringConstants.WEBSPHERE_USER))
                .put("password", props.getProperty(StringConstants.WEBSPHERE_PASSWORD)));

        jo.put("credential", credentialArray);

        String jobId = TestUtils.callRunProcedure(jo);

        String response = TestUtils.waitForJob(jobId,jobTimeoutMillis);

        // Check job status
        assertEquals("Job completed with errors", "success", response);

    }

    @Test
    public void createMailSessionInvalidScopeTest() throws Exception {

        String invalidScope = "/Cell:" + props.getProperty(StringConstants.CELL_NAME) + "someRandomString" + "/Node:" + props.getProperty(StringConstants.NODE_NAME) + "/Server:" + props.getProperty(StringConstants.SERVER_NAME) + "anotherRandomString" + "/";
        long jobTimeoutMillis = 5 * 60 * 1000;
        JSONObject jo = new JSONObject();

        jo.put("projectName", "EC-WebSphere-" + StringConstants.PLUGIN_VERSION);
        jo.put("procedureName", "CreateMailSession");
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
                .put("actualParameterName", "scope")
                .put("value", invalidScope));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "mailSessionName")
                .put("value", "automatedTest-testMailSession"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "mailSessionDesc")
                .put("value", "Mail session created during automated plugin testing."));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "mailSessionJNDIName")
                .put("value", "jndi/automatedTest-testMailSession"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "debug")
                .put("value", "1"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "mailTransportHost")
                .put("value", "smtp.host.com"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "mailTransportPort")
                .put("value", "25"));

        actualParameterArray.put(new JSONObject()
                .put("actualParameterName", "mailTransportUser")
                .put("value","mail_credentials"));

        jo.put("actualParameter", actualParameterArray);

        JSONArray credentialArray = new JSONArray();

        credentialArray.put(new JSONObject()
                .put("credentialName", "mail_credentials")
                .put("userName", props.getProperty(StringConstants.WEBSPHERE_USER))
                .put("password", props.getProperty(StringConstants.WEBSPHERE_PASSWORD)));

        jo.put("credential", credentialArray);

        String jobId = TestUtils.callRunProcedure(jo);

        String response = TestUtils.waitForJob(jobId,jobTimeoutMillis);

        // Check job status
        assertEquals("Job completed without errors", "error", response);

    }

}