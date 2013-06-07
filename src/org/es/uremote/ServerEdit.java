package org.es.uremote;

import org.es.uremote.objects.ServerBuilder;
import org.es.uremote.objects.ServerInfo;
import org.es.uremote.utils.IntentKeys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author Cyril Leroux
 *
 */
public class ServerEdit extends Activity {

	private static final String TAG = "EditServer";
	private ServerBuilder mServerBuilder;

	private EditText mServerName;
	private EditText mLocalHost1;
	private EditText mLocalHost2;
	private EditText mLocalHost3;
	private EditText mLocalHost4;
	private EditText mLocalPort;

	private EditText mRemoteHost1;
	private EditText mRemoteHost2;
	private EditText mRemoteHost3;
	private EditText mRemoteHost4;
	private EditText mRemotePort;

	private EditText mMacAddress1;
	private EditText mMacAddress2;
	private EditText mMacAddress3;
	private EditText mMacAddress4;
	private EditText mMacAddress5;
	private EditText mMacAddress6;

	private EditText mConnectionTimeout;
	private EditText mReadTimeout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.server_edit);

		mServerName		= (EditText)findViewById(R.id.server_name);

		mLocalHost1	= (EditText)findViewById(R.id.local_host1);
		mLocalHost2	= (EditText)findViewById(R.id.local_host2);
		mLocalHost3	= (EditText)findViewById(R.id.local_host3);
		mLocalHost4	= (EditText)findViewById(R.id.local_host4);
		mLocalPort	= (EditText)findViewById(R.id.local_port);

		mRemoteHost1	= (EditText)findViewById(R.id.remote_host1);
		mRemoteHost2	= (EditText)findViewById(R.id.remote_host2);
		mRemoteHost3	= (EditText)findViewById(R.id.remote_host3);
		mRemoteHost4	= (EditText)findViewById(R.id.remote_host4);
		mRemotePort		= (EditText)findViewById(R.id.remote_port);

		mMacAddress1	= (EditText)findViewById(R.id.mac_address1);
		mMacAddress2	= (EditText)findViewById(R.id.mac_address2);
		mMacAddress3	= (EditText)findViewById(R.id.mac_address3);
		mMacAddress4	= (EditText)findViewById(R.id.mac_address4);
		mMacAddress5	= (EditText)findViewById(R.id.mac_address5);
		mMacAddress6	= (EditText)findViewById(R.id.mac_address6);

		mConnectionTimeout	= (EditText)findViewById(R.id.connection_timeout);
		mReadTimeout			= (EditText)findViewById(R.id.read_timeout);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.create_server, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.done:
			mServerBuilder = new ServerBuilder();

			mServerBuilder.setName(getName());
			mServerBuilder.setLocalHost(getLocalHost());
			mServerBuilder.setLocalPort(getLocalPort());
			mServerBuilder.setRemoteHost(getRemoteHost());
			mServerBuilder.setRemotePort(getRemotePort());
			mServerBuilder.setMacAddress(getMacAddress());
			mServerBuilder.setConnectionTimeout(getConnectionTimeout());
			mServerBuilder.setReadTimout(getReadTimeout());

			try {
				ServerInfo server = mServerBuilder.build();
				Intent data = new Intent();
				data.putExtra(IntentKeys.EXTRA_SERVER_DATA, server);
				setResult(RESULT_OK, data);
				finish();

			} catch (Exception e) {
				if (BuildConfig.DEBUG) {
					Log.e(TAG, e.getMessage());
				}
				Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
			}
			return true;


			//			case R.id.cancel:
			//				setResult(RESULT_CANCELED);
			//				finish();
			//				return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private String getName() {
		return mServerName.getEditableText().toString();
	}

	private String getLocalHost() {

		StringBuilder sb = new StringBuilder();
		sb.append(mLocalHost1.getEditableText().toString());
		sb.append(".");
		sb.append(mLocalHost2.getEditableText().toString());
		sb.append(".");
		sb.append(mLocalHost3.getEditableText().toString());
		sb.append(".");
		sb.append(mLocalHost4.getEditableText().toString());
		return sb.toString();
	}

	private int getLocalPort() {
		return Integer.valueOf(mLocalPort.getEditableText().toString());
	}

	private String getRemoteHost() {

		StringBuilder sb = new StringBuilder();
		sb.append(mRemoteHost1.getEditableText().toString());
		sb.append(".");
		sb.append(mRemoteHost2.getEditableText().toString());
		sb.append(".");
		sb.append(mRemoteHost3.getEditableText().toString());
		sb.append(".");
		sb.append(mRemoteHost4.getEditableText().toString());
		return sb.toString();
	}

	private int getRemotePort() {
		return Integer.valueOf(mRemotePort.getEditableText().toString());
	}

	private String getMacAddress() {

		StringBuilder sb = new StringBuilder();
		sb.append(mMacAddress1.getEditableText().toString());
		sb.append("-");
		sb.append(mMacAddress2.getEditableText().toString());
		sb.append("-");
		sb.append(mMacAddress3.getEditableText().toString());
		sb.append("-");
		sb.append(mMacAddress4.getEditableText().toString());
		sb.append("-");
		sb.append(mMacAddress5.getEditableText().toString());
		sb.append("-");
		sb.append(mMacAddress6.getEditableText().toString());
		return sb.toString();
	}

	private int getConnectionTimeout() {
		return Integer.valueOf(mConnectionTimeout.getEditableText().toString());
	}

	private int getReadTimeout() {
		return Integer.valueOf(mReadTimeout.getEditableText().toString());
	}
}
