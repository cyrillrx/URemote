package org.es.uremote;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import org.es.uremote.components.ServerXmlHandler;
import org.es.uremote.objects.ServerInfo;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

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
			try { fis.close(); } catch (IOException e) { }
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
	}
}