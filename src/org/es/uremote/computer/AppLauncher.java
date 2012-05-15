package org.es.uremote.computer;

import static org.es.uremote.utils.IntentKeys.APPLICATION_MESSAGE;
import static org.es.uremote.utils.ServerMessage.APP_GOM_PLAYER;
import static org.es.uremote.utils.ServerMessage.KILL_GOM_PLAYER;

import org.es.uremote.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class AppLauncher extends Activity implements OnClickListener {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.launcher_in, R.anim.launcher_out);
		setContentView(R.layout.server_app_launcher);

		//requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Click listener pour tous les boutons
		((ImageButton) findViewById(R.id.btnAppGomPlayer)).setOnClickListener(this);
		((ImageButton) findViewById(R.id.btnKillGomPlayer)).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btnAppGomPlayer:
			returnAppMessage(APP_GOM_PLAYER);
			break;

		case R.id.btnKillGomPlayer:
			returnAppMessage(KILL_GOM_PLAYER);
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
