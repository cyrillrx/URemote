package org.es.uremote;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import org.es.uremote.components.ServerAdapter;
import org.es.uremote.components.ServerXmlHandler;
import org.es.uremote.dao.ServerSettingDao;
import org.es.uremote.objects.ServerSetting;
import org.es.utils.Log;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static org.es.uremote.objects.ServerSetting.SAVE_FILE;
import static org.es.uremote.utils.IntentKeys.ACTION_ADD_SERVER;
import static org.es.uremote.utils.IntentKeys.ACTION_EDIT_SERVER;
import static org.es.uremote.utils.IntentKeys.EXTRA_SERVER_DATA;
import static org.es.uremote.utils.IntentKeys.EXTRA_SERVER_ID;

/**
 * @author Cyril Leroux
 */
public class ServerList extends ListActivity {

	private static final String TAG = "ServerList";
	private static final int RC_ADD_SERVER	= 0;
	private static final int RC_EDIT_SERVER	= 1;

	List<ServerSetting> mServers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.server_list);
		mServers = new ArrayList<>();
		loadServerList();

	}

	private File getConfFile() {
		return new File(getApplicationContext().getExternalFilesDir(null), SAVE_FILE);
	}

	private void loadServerList() {
		(new AsyncServerLoader()).execute(getConfFile());
	}

	private void saveServers(List<ServerSetting> servers) {
		boolean saved = ServerSettingDao.saveToXmlFile(getConfFile(), servers);

		if (!saved) {
			Log.error(TAG, "Servers not saved.");
			Toast.makeText(getApplicationContext(), R.string.server_not_saved, Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getApplicationContext(), R.string.server_saved, Toast.LENGTH_SHORT).show();
		}
	}

	private void updateView(final List<ServerSetting> servers) {
		ServerAdapter adapter = new ServerAdapter(getApplicationContext(), servers);
		setListAdapter(adapter);

		ListView listView = getListView();
		listView.setOnItemClickListener( new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ServerSetting server = servers.get(position);
				Intent editIntent = new Intent(getApplicationContext(), ServerEdit.class);
				editIntent.putExtra(EXTRA_SERVER_DATA, server);
				editIntent.putExtra(EXTRA_SERVER_ID, position);
				editIntent.setAction(ACTION_EDIT_SERVER);
				startActivityForResult(editIntent, RC_EDIT_SERVER);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.server_list, menu);
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
	private class AsyncServerLoader extends AsyncTask<File, Void, List<ServerSetting>> {

		@Override
		protected List<ServerSetting> doInBackground(File... files) {
			mServers = LoadFromXml(files[0]);
			return mServers;
		}

		private List<ServerSetting> LoadFromXml(File configFile) {

			ServerXmlHandler serverXmlhandler = new ServerXmlHandler();
			try {
				SAXParserFactory parserFactory = SAXParserFactory.newInstance();
				XMLReader reader = parserFactory.newSAXParser().getXMLReader();
				reader.setContentHandler(serverXmlhandler);
				reader.parse(new InputSource(new FileInputStream(configFile)));

			} catch (Exception e) {
                Log.debug(TAG, "Parsing Server exception : " + e);
			}
			return serverXmlhandler.getServers();
		}

		@Override
		protected void onPostExecute(List<ServerSetting> servers) {
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
			ServerSetting server = data.getParcelableExtra(EXTRA_SERVER_DATA);
			mServers.add(server);
			saveServers(mServers);
			updateView(mServers);

		} else if (requestCode == RC_EDIT_SERVER) {
			//
			final int serverId = data.getIntExtra(EXTRA_SERVER_ID, -1);
			if (serverId != -1) {
				ServerSetting server = data.getParcelableExtra(EXTRA_SERVER_DATA);
				mServers.get(serverId).copy(server);
				saveServers(mServers);
				updateView(mServers);
			}
		}
	}
}