package org.es.uremote.computer;

import static org.es.uremote.utils.IntentKeys.APPLICATION_MESSAGE;

import org.es.network.ExchangeProtos.Request;
import org.es.uremote.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

/**
 * This Activity displays a list of applications that you can launch on the remote server.
 * 
 * @author Cyril Leroux
 *
 */
public class AppLauncher extends Activity implements OnClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.launcher_in, R.anim.launcher_out);
		setContentView(R.layout.server_app_launcher);

		//requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Click listener for all buttons
		((ImageButton) findViewById(R.id.btnAppGomPlayer)).setOnClickListener(this);
		((ImageButton) findViewById(R.id.btnKillGomPlayer)).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btnAppGomPlayer:
			returnAppMessage(Request.Code.GOM_PLAYER_RUN.name());
			break;

		case R.id.btnKillGomPlayer:
			returnAppMessage(Request.Code.GOM_PLAYER_KILL.name());
			break;

		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.launcher_in, R.anim.launcher_out);
	}

	private void returnAppMessage(String _appMessage) {
		Intent data = new Intent();
		data.putExtra(APPLICATION_MESSAGE, _appMessage);
		setResult(RESULT_OK, data);
		finish();
		overridePendingTransition(R.anim.launcher_in, R.anim.launcher_out);
	}


}
