package org.es.uremote.exchange;

import android.test.InstrumentationTestCase;

import junit.framework.Assert;

import org.es.uremote.device.NetworkDevice;
import org.es.uremote.network.AsyncMessageMgr;
import org.es.utils.Log;

import java.util.concurrent.ExecutionException;

/**
 * Created by cyril.leroux on 15/05/2014.
 */
public class TestRequestSender extends InstrumentationTestCase {

    private static final String TAG = "TestRequestSender";
    private static final String SECURITY_TOKEN = "123";

    NetworkDevice mDevice;

//    @Before
    @Override
    protected void setUp() throws Exception {

        mDevice = NetworkDevice.newBuilder()
                .setName("TestDevice")
                .setLocalHost("localhost")
                .setLocalPort(10000)
                .setBroadcast("192.168.0.255")
                .setRemoteHost("localhost")
                .setRemotePort(20000)
                .setMacAddress("00-FF-50-15-86-07")
                .setConnectionTimeout(500)
                .setReadTimeout(500)
                .setSecurityToken(SECURITY_TOKEN)
                .setConnectionType(NetworkDevice.ConnectionType.LOCAL)
                .build();
    }

//    @Test
    public void testRequest() {
        sendRequest(getKeyboardRequest(Message.Request.Code.DEFINE, Message.Request.Code.NONE, "A"));
    }

    private Message.Request getKeyboardRequest(Message.Request.Code primaryCode, Message.Request.Code extraCode, String extraString) {
        return MessageUtils.buildRequest(SECURITY_TOKEN, Message.Request.Type.KEYBOARD, primaryCode, extraCode, extraString);
    }

    /**
     * Initializes the async task manager then send a request with it.
     *
     * @param request The request to send.
     */
    public void sendRequest(Message.Request request) {

        if (AsyncMessageMgr.availablePermits() > 0) {
            AsyncMessageMgr task = new AsyncMessageMgr(mDevice, null);
            task.execute(request);
            Log.info(TAG, "#sendRequest - Command sent.");

            try {
                checkResponse(task.get());
            } catch (InterruptedException e) {
                Assert.fail("#sendRequest - InterruptedException : " + e.getMessage());
            } catch (ExecutionException e) {
                Assert.fail("#sendRequest - ExecutionException : " + e.getMessage());
            }

        } else {
            Assert.fail("#sendRequest - No more permit available!");
        }
    }

    /**
     * Checks the response validity.
     * @param response
     */
    private static void checkResponse(Message.Response response) {

        if (Message.Response.ReturnCode.RC_ERROR.equals(response.getReturnCode())) {
            Log.error(TAG, "#onPostExecute - response : " + response);
        } else {
            Log.info(TAG, "#onPostExecute - response : " + response);
        }

        Assert.assertEquals(
                "Request failed : " + response.getMessage(),
                Message.Response.ReturnCode.RC_SUCCESS,
                response.getReturnCode());

    }
}
