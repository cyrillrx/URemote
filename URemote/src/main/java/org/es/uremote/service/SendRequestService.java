package org.es.uremote.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * @author Cyril Leroux
 * Created on 15/05/13.
 */
public class SendRequestService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}
}
