package org.es.uremote;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static org.es.uremote.utils.IntentKeys.EXTRA_SERVER_DATA;
import static org.es.uremote.utils.IntentKeys.EXTRA_SERVER_ID;
import static org.es.uremote.utils.IntentKeys.ACTION_ADD_SERVER;
import static org.es.uremote.utils.IntentKeys.ACTION_EDIT_SERVER;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import org.es.uremote.components.ServerAdapter;
import org.es.uremote.components.ServerXmlHandler;
import org.es.uremote.objects.ServerInfo;
import org.es.uremote.utils.IntentKeys;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

/**
 * @author Cyril Leroux
 */
public class ServerList extends ListActivity {

	private static final String TAG = "ServerList";
	private static final int RC_ADD_SERVER	= 0;
	private static final int RC_EDIT_SERVER	= 1;

	List<ServerInfo> mServers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.server_list);
		mServers = new ArrayList<ServerInfo>();

		FileInputStream fis = null;
		try {
			fis = openFileInput(ServerInfo.SAVE_FILE);
			(new AsyncServerLoader()).execute(fis);
		} catch (FileNotFoundException e) {
			if (BuildConfig.DEBUG) {
				Log.e(TAG, e.getMessage());
			}
		}
	}

	private void updateView(final List<ServerInfo> servers) {
		ServerAdapter adapter = new ServerAdapter(getApplicationContext(), servers);
		setListAdapter(adapter);

		ListView listView = getListView();
		listView.setOnItemClickListener( new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ServerInfo server = servers.get(position);
				Intent editIntent = new Intent(getApplicationContext(), ServerEdit.class);
				editIntent.putExtra(IntentKeys.EXTRA_SERVER_DATA, server);
				editIntent.setAction(ACTION_EDIT_SERVER);
				startActivityForResult(editIntent, RC_EDIT_SERVER);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu _menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.server_list, _menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			Intent intent = new Intent(getApplicationContext(), Computer.class);
			intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;

		case R.id.add_server:
			Intent addIntent = new Intent(getApplicationContext(), ServerEdit.class);
			addIntent.setAction(ACTION_ADD_SERVER);
			startActivityForResult(addIntent, RC_ADD_SERVER);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Load the servers from a FileInputStream.
	 * @author Cyril Leroux
	 *
	 */
	private class AsyncServerLoader extends AsyncTask<FileInputStream, Void, List<ServerInfo>> {

		@Override
		protected List<ServerInfo> doInBackground(FileInputStream... fileInputStreams) {
			return LoadFromXml(fileInputStreams[0]);
		}

		private List<ServerInfo> LoadFromXml(FileInputStream fis) {

			ServerXmlHandler serverXmlhandler = new ServerXmlHandler();
			try {
				SAXParserFactory parserFactory = SAXParserFactory.newInstance();
				XMLReader reader = parserFactory.newSAXParser().getXMLReader();
				reader.setContentHandler(serverXmlhandler);
				reader.parse(new InputSource(fis));

			} catch (Exception e) {
				if (BuildConfig.DEBUG) {
					Log.d(TAG, "Parsing Server exception : " + e);
				}
			}
			return serverXmlhandler.getServers();
		}

		@Override
		protected void onPostExecute(List<ServerInfo> servers) {
			updateView(servers);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode != RESULT_OK) {
			return;
		}

		if (requestCode == RC_ADD_SERVER) {
			// TODO Add to the server list
			ServerInfo server = data.getParcelableExtra(EXTRA_SERVER_DATA);
			mServers.add(server);
			boolean saved = ServerInfo.saveToXmlFile(getApplicationContext(), mServers);
			if (!saved) {
				if (BuildConfig.DEBUG) {
					Log.e(TAG, "Servers not saved.");
				}
				Toast.makeText(getApplicationContext(), R.string.server_not_saved, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(), R.string.server_saved, Toast.LENGTH_SHORT).show();
			}

			updateView(mServers);
			// load list
			// Add to list
			// save

		} else if (requestCode == RC_EDIT_SERVER) {
			//
			final int serverId = data.getIntExtra(EXTRA_SERVER_ID, -1);
			if (serverId == -1) {
				ServerInfo server = data.getParcelableExtra(EXTRA_SERVER_DATA);
				mServers.remove(serverId);
			}
		}
	}
}