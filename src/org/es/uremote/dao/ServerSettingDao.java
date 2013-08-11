package org.es.uremote.dao;

import android.util.Log;

import org.es.uremote.BuildConfig;
import org.es.uremote.objects.ServerSetting;
import org.es.utils.XmlWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Cyril Leroux on 10/08/13.
 */
public class ServerSettingDao {

    private static String TAG = "ServerSettingDao";

    public static final String TAG_ROOT					= "servers";
    public static final String TAG_SERVER	            = "server";
    public static final String TAG_NAME			        = "name";
    public static final String TAG_LOCAL_HOST           = "local_ip_address";
    public static final String TAG_LOCAL_PORT           = "local_port";
    public static final String TAG_BROADCAST            = "broadcast_address";
    public static final String TAG_REMOTE_HOST          = "remote_ip_address";
    public static final String TAG_REMOTE_PORT          = "remote_port";
    public static final String TAG_MAC_ADDRESS	        = "mac_address";
    public static final String TAG_CONNECTION_TIMEOUT	= "connection_timeout";
    public static final String TAG_READ_TIMEOUT			= "read_timeout";
    public static final String TAG_CONNECTION_TYPE		= "connection_type";

    /**
     * Save the object attributes into a XML file.
     * @param confFile
     * @param servers
     * @return true if save succeeded false otherwise.
     */
    public static boolean saveToXmlFile(File confFile, List<ServerSetting> servers) {

        if (servers == null || servers.isEmpty()) {
            return false;
        }

        try {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, confFile.getPath());
            }
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
            return false;
        }
        return true;
    }
}
