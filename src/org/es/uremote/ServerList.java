package org.es.uremote;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import org.es.uremote.components.ServerAdapter;
import org.es.uremote.components.ServerXmlHandler;
import org.es.uremote.objects.ServerInfo;
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

/**
 * @author Cyril Leroux
 */
public class ServerList extends ListActivity {

	private static final String TAG			= "ServerList";
	private static final String FILENAME	= "serverConfig";
	List<ServerInfo> mServers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.server_list);
		mServers = new ArrayList<ServerInfo>();

		FileInputStream fis = null;
		try {
			fis = openFileInput(FILENAME);
			(new AsyncServerLoader()).execute(fis);
		} catch (FileNotFoundException e) {
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) { }
		}
	}

	private void updateView(List<ServerInfo> servers) {
		ServerAdapter adapter = new ServerAdapter(getApplicationContext(), servers);
		setListAdapter(adapter);
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
			startActivity(new Intent(getApplicationContext(), CreateServer.class));
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
}