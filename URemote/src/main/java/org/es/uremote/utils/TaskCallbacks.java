package org.es.uremote.utils;

import org.es.uremote.exchange.Message.Response;

/**
 * Callback interface through which the fragment will report the
 * task's progress and results back to the Activity.
 *
 * Created by Cyril Leroux on 19/08/13.
 */
public interface TaskCallbacks {

	void onPreExecute();

	void onProgressUpdate(int percent);

	void onCancelled();

	void onPostExecute(Response response);
}
