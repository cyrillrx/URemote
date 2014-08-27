package org.es.uremote.utils;

import org.es.uremote.request.protobuf.RemoteCommand.Response;

/**
 * Callback interface through which the fragment will report the
 * task's progress and results back to the Activity.
 * <p/>
 * Created by Cyril Leroux on 19/08/13.
 */
public interface TaskCallbacks {

    void onPreExecute();

    void onProgressUpdate(int percent);

    void onPostExecute(Response response);

    void onCancelled(Response response);
}
