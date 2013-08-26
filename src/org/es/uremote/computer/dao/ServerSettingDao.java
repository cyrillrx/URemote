package org.es.uremote.computer.dao;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.preference.PreferenceManager;

import org.es.exception.AccessStorageOnMainThreadException;
import org.es.uremote.R;
import org.es.uremote.components.ServerXmlHandler;
import org.es.uremote.objects.ServerSetting;
import org.es.utils.Log;
import org.es.utils.XmlWriter;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

/** Created by Cyril Leroux on 10/08/13. */
public class ServerSettingDao {

	public static final String TAG_ROOT				= "servers";
	public static final String TAG_SERVER			= "server";
	public static final String TAG_NAME				= "name";
	public static final String TAG_LOCAL_HOST		= "local_ip_address";
	public static final String TAG_LOCAL_PORT		= "local_port";
	public static final String TAG_BROADCAST		= "broadcast_address";
	public static final String TAG_REMOTE_HOST		= "remote_ip_address";
	public static final String TAG_REMOTE_PORT		= "remote_port";
	public static final String TAG_MAC_ADDRESS		= "mac_address";
	public static final String TAG_CONNECTION_TIMEOUT = "connection_timeout";
	public static final String TAG_READ_TIMEOUT		= "read_timeout";
	public static final String TAG_CONNECTION_TYPE	= "connection_type";
	private static String TAG = "ServerSettingDao";

	/**
	 * Save the object attributes into a XML file.
	 *
	 * @param servers
	 * @param confFile
	 * @return true if save succeeded, false otherwise.
	 */
	public static boolean saveToFile(List<ServerSetting> servers, File confFile) {

		if (Looper.myLooper() == Looper.getMainLooper()) {
			throw new AccessStorageOnMainThreadException("ServerSettingDao #saveToFile");
		}

		if (servers == null || servers.isEmpty()) {
			return false;
		}

		try {
			Log.debug(TAG, confFile.getPath());

			FileOutputStream fos = new FileOutputStream(confFile);

			XmlWriter xmlWriter = new XmlWriter(fos, TAG_ROOT);

			for (ServerSetting server : servers) {
				xmlWriter.startTag(null, TAG_SERVER);
				xmlWriter.addChild(TAG_NAME, server.getName(), null);
				xmlWriter.addChild(TAG_LOCAL_HOST, server.getLocalHost(), null);
				xmlWriter.addChild(TAG_LOCAL_PORT, server.getLocalPort(), null);
				xmlWriter.addChild(TAG_BROADCAST, server.getBroadcast(), null);
				xmlWriter.addChild(TAG_REMOTE_HOST, server.getRemoteHost(), null);
				xmlWriter.addChild(TAG_REMOTE_PORT, server.getRemotePort(), null);

				xmlWriter.addChild(TAG_MAC_ADDRESS, server.getMacAddress(), null);
				xmlWriter.addChild(TAG_CONNECTION_TIMEOUT, server.getConnectionTimeout(), null);
				xmlWriter.addChild(TAG_READ_TIMEOUT, server.getReadTimeout(), null);
				xmlWriter.addChild(TAG_CONNECTION_TYPE, server.getConnectionType().toString(), null);
				xmlWriter.endTag(null, TAG_SERVER);
			}

			xmlWriter.closeAndSave();

		} catch (IOException e) {
			Log.error(TAG, "#saveToFile - Servers have not been saved.", e);
			return false;
		}
		return true;
	}

	/**
	 * Load the server settings from an XML file into the server list.
	 *
	 * @param configFile The file to read.
	 * @param servers The list to fill with loaded data.
	 * @return true if load succeeded, false otherwise.
	 */
	public static boolean loadFromFile(File configFile, List<ServerSetting> servers) {

		if (Looper.myLooper() == Looper.getMainLooper()) {
			throw new AccessStorageOnMainThreadException("ServerSettingDao #loadFromFile");
		}

		ServerXmlHandler serverXmlhandler = new ServerXmlHandler();
		try {
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			XMLReader reader = parserFactory.newSAXParser().getXMLReader();
			reader.setContentHandler(serverXmlhandler);
			reader.parse(new InputSource(new FileInputStream(configFile)));

		} catch (Exception e) {
			Log.error(TAG, "#loadFromFile - Error occurred while parsing Server settings (file " + configFile + ")", e);
			return false;
		}

		servers.addAll(serverXmlhandler.getServers());
		return true;
	}

	/**
	 * @param context The application context.
	 * @return The Server connection settings stored in User Preferences
	 */
	public static ServerSetting loadFromPreferences(Context context) {

		// Get Host and Port key
		final String keyLocalHost = context.getString(R.string.key_local_host);
		final String keyLocalPort = context.getString(R.string.key_local_port);
		final String keyBroadcast = context.getString(R.string.key_broadcast);
		final String keyRemoteHost = context.getString(R.string.key_remote_host);
		final String keyRemotePort = context.getString(R.string.key_remote_port);
		final String keyMacAddress = context.getString(R.string.key_mac_address);

		// Get key for other properties
		final String keyConnectionTimeout = context.getString(R.string.key_connection_timeout);
		final String keyReadTimeout = context.getString(R.string.key_read_timeout);

		// Get default values for Host and Port
		final String defaultLocalHost = context.getString(R.string.default_local_host);
		final String defaultLocalPort = context.getString(R.string.default_local_port);
		final String defaultBroadcast = context.getString(R.string.default_broadcast);
		final String defaultRemoteHost = context.getString(R.string.default_remote_host);
		final String defaultRemotePort = context.getString(R.string.default_remote_port);
		final String defaultMacAddress = context.getString(R.string.default_mac_address);

		// Get default values for other properties
		final String defaultConnectionTimeout = context.getString(R.string.default_connection_timeout);
		final String defaultReadTimeout = context.getString(R.string.default_read_timeout);

		// Get the properties values
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
		final String localHost = pref.getString(keyLocalHost, defaultLocalHost);
		final int localPort = Integer.parseInt(pref.getString(keyLocalPort, defaultLocalPort));
		final String broadcast = pref.getString(keyBroadcast, defaultBroadcast);
		final String remoteHost = pref.getString(keyRemoteHost, defaultRemoteHost);
		final int remotePort = Integer.parseInt(pref.getString(keyRemotePort, defaultRemotePort));
		final String macAddress = pref.getString(keyMacAddress, defaultMacAddress);
		final int connectionTimeout = Integer.parseInt(pref.getString(keyConnectionTimeout, defaultConnectionTimeout));
		final int readTimeout = Integer.parseInt(pref.getString(keyReadTimeout, defaultReadTimeout));

		return new ServerSetting("", localHost, localPort, broadcast, remoteHost, remotePort, macAddress, connectionTimeout, readTimeout, ServerSetting.ConnectionType.LOCAL);
	}
}
