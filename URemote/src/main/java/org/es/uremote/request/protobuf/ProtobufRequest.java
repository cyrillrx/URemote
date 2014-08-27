package org.es.uremote.request.protobuf;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;

/**
 * @author Cyril Leroux
 *         Created 22/08/2014.
 */
public class ProtobufRequest<Data> extends Request<Data> {

    public ProtobufRequest(int method, String url, Response.ErrorListener listener) {
        super(method, url, listener);
    }

    @Override
    protected Response<Data> parseNetworkResponse(NetworkResponse response) {
        return null;
    }

    @Override
    protected void deliverResponse(Data response) {

    }
}
