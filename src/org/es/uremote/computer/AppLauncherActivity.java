package org.es.uremote.computer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import org.es.uremote.exchange.ExchangeMessages.Request.Code;
import org.es.uremote.exchange.ExchangeMessages.Request.Type;
import org.es.uremote.R;

import static org.es.uremote.utils.IntentKeys.REQUEST_CODE;
import static org.es.uremote.utils.IntentKeys.REQUEST_TYPE;

/**
 * This Activity displays a list of applications that you can launch on the remote server.
 *
 * @author Cyril Leroux
 * Created before first commit (08/04/12).
 */
public class AppLauncherActivity extends Activity implements OnClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.launcher_in, R.anim.launcher_out);
		setContentView(R.layout.server_app_launcher);

		// Click listener for all buttons
		((ImageButton) findViewById(R.id.btnAppGomPlayer)).setOnClickListener(this);
		((ImageButton) findViewById(R.id.btnKillGomPlayer)).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
			case R.id.btnAppGomPlayer:
				returnAppMessage(Type.APP, Code.GOM_PLAYER_RUN);
				break;

			case R.id.btnKillGomPlayer:
				returnAppMessage(Type.APP, Code.GOM_PLAYER_KILL);
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

	private void returnAppMessage(Type type, Code code) {
		Intent data = new Intent();
		data.putExtra(REQUEST_TYPE, type.getNumber());
		data.putExtra(REQUEST_CODE, code.getNumber());
		setResult(RESULT_OK, data);
		finish();
		overridePendingTransition(R.anim.launcher_in, R.anim.launcher_out);
	}
}