package org.es.uremote;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Activity to control the TV.
 *
 * @author Cyril Leroux
 *
 */
public class TvDialer extends Activity implements OnClickListener {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tv_dialer);
	}

	@Override
	public void onClick(View _v) {
		switch (_v.getId()) {
		// TODO code controls
		default:
			break;
		}
	}

}
