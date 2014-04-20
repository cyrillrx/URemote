package org.es.uremote.computer.dao;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.preference.PreferenceManager;

import org.es.exception.AccessStorageOnMainThreadException;
import org.es.uremote.device.ServerSetting;
import org.es.uremote.utils.PrefKeys;
import org.es.utils.Log;
import org.es.utils.XmlWriter;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import static org.es.uremote.device.ServerSetting.FILENAME;

/**
 * @author Cyril Leroux
 *         Created on 10/08/13.
 */
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
    public static final String TAG_SECURITY_TOKEN   = "security_token";
    public static final String TAG_CONNECTION_TYPE	= "connection_type";

	private static final String TAG = "ServerSettingDao";

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

				xmlWriter.addChild(TAG_NAME,                server.getName(), null);
				xmlWriter.addChild(TAG_LOCAL_HOST,          server.getLocalHost(), null);
				xmlWriter.addChild(TAG_LOCAL_PORT,          server.getLocalPort(), null);
				xmlWriter.addChild(TAG_BROADCAST,           server.getBroadcast(), null);
				xmlWriter.addChild(TAG_REMOTE_HOST,         server.getRemoteHost(), null);
				xmlWriter.addChild(TAG_REMOTE_PORT,         server.getRemotePort(), null);
				xmlWriter.addChild(TAG_MAC_ADDRESS,         server.getMacAddress(), null);
				xmlWriter.addChild(TAG_CONNECTION_TIMEOUT,  server.getConnectionTimeout(), null);
				xmlWriter.addChild(TAG_READ_TIMEOUT,        server.getReadTimeout(), null);
				xmlWriter.addChild(TAG_CONNECTION_TYPE,     server.getConnectionType().toString(), null);
				xmlWriter.addChild(TAG_SECURITY_TOKEN,      server.getSecurityToken(), null);

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
     * Use this function to update an existing server list.
	 *
	 * @param configFile The file to read.
	 * @param servers The list to fill with loaded data.
	 * @return true if load succeeded, false otherwise.
	 */
	public static boolean loadFromFile(File configFile, List<ServerSetting> servers) {

//		if (Looper.myLooper() == Looper.getMainLooper()) {
//			throw new AccessStorageOnMainThreadException("ServerSettingDao #loadFromFile");
//		}

		if (!configFile.exists()) {
			Log.warning(TAG, "#loadFromFile - File is null or does not exist");
			return false;
		}

		ServerSettingXmlHandler xmlHandler = new ServerSettingXmlHandler();
		try {
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			XMLReader reader = parserFactory.newSAXParser().getXMLReader();
			reader.setContentHandler(xmlHandler);
			reader.parse(new InputSource(new FileInputStream(configFile)));

		} catch (Exception e) {
			Log.error(TAG, "#loadFromFile - Error occurred while parsing Server settings (file " + configFile + ")", e);
			return false;
		}

		return servers.addAll(xmlHandler.getServers());
	}

    /**
     * Load the server list from the default XML file and returns it.
     * Use this function to load the list of servers.
     *
     * @param context The application context.
     * @return The server list or null if an error occurred.
     */
    public static List<ServerSetting> loadList(Context context) {

        final File confFile = new File(context.getExternalFilesDir(null), FILENAME);
        final List<ServerSetting> servers = new ArrayList<>();
        if (loadFromFile(confFile, servers)) {
            return servers;
        }
        return null;
    }

    /**
     * Load the selected server.
     *
     * @param context The application context.
     * @return The selected server.
     */
    public static ServerSetting loadSelected(Context context) {

        final List<ServerSetting> servers = loadList(context);

        // Get the properties values
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        final int serverId = pref.getInt(PrefKeys.KEY_SERVER_ID, PrefKeys.DEFAULT_SERVER_ID);
        try {
            return servers.get(serverId);
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            return null;
        }
    }
}
