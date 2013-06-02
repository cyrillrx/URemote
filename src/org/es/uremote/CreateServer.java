package org.es.uremote;

import org.es.uremote.objects.ServerBuilder;
import org.es.uremote.objects.ServerInfo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * @author Cyril Leroux
 *
 */
public class CreateServer extends Activity {

	private ServerBuilder mServerBuilder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_server);
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
			// TODO fill serverBuilder
			ServerInfo server = mServerBuilder.build();
			if (server == null) {
				return false;
			}

			if (server.saveToXmlFile()) {
				//TODO close
			} else {
				// TODO notify that an error occurred
			}

		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
