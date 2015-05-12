package com.cyrillrx.uremote.ui.computer.dao;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.preference.PreferenceManager;

import com.cyrillrx.android.toolbox.Logger;
import com.cyrillrx.android.utils.XmlWriter;
import com.cyrillrx.common.exception.AccessStorageOnMainThreadException;
import com.cyrillrx.uremote.common.device.NetworkDevice;
import com.cyrillrx.uremote.utils.PrefKeys;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import static com.cyrillrx.uremote.common.device.NetworkDevice.FILENAME;

/**
 * @author Cyril Leroux
 *         Created on 10/08/13.
 */
public class NetworkDeviceDao {

    public static final String TAG_ROOT = "servers";
    public static final String TAG_SERVER = "server";
    public static final String TAG_NAME = "name";
    public static final String TAG_LOCAL_HOST = "local_ip_address";
    public static final String TAG_LOCAL_PORT = "local_port";
    public static final String TAG_BROADCAST = "broadcast_address";
    public static final String TAG_REMOTE_HOST = "remote_ip_address";
    public static final String TAG_REMOTE_PORT = "remote_port";
    public static final String TAG_MAC_ADDRESS = "mac_address";
    public static final String TAG_CONNECTION_TIMEOUT = "connection_timeout";
    public static final String TAG_READ_TIMEOUT = "read_timeout";
    public static final String TAG_SECURITY_TOKEN = "security_token";
    public static final String TAG_CONNECTION_TYPE = "connection_type";

    private static final String TAG = NetworkDeviceDao.class.getSimpleName();

    /**
     * Save the object attributes into a XML file.
     *
     * @param devices
     * @param confFile
     * @return true if save succeeded, false otherwise.
     */
    public static boolean saveToFile(List<NetworkDevice> devices, File confFile) {

        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new AccessStorageOnMainThreadException("ServerSettingDao #saveToFile");
        }

        if (devices == null || devices.isEmpty()) {
            return false;
        }

        try {
            Logger.debug(TAG, confFile.getPath());

            FileOutputStream fos = new FileOutputStream(confFile);

            XmlWriter xmlWriter = new XmlWriter(fos, TAG_ROOT);

            for (NetworkDevice device : devices) {

                xmlWriter.startTag(null, TAG_SERVER);

                xmlWriter.addChild(TAG_NAME, device.getName(), null);
                xmlWriter.addChild(TAG_LOCAL_HOST, device.getLocalHost(), null);
                xmlWriter.addChild(TAG_LOCAL_PORT, device.getLocalPort(), null);
                xmlWriter.addChild(TAG_BROADCAST, device.getBroadcast(), null);
                xmlWriter.addChild(TAG_REMOTE_HOST, device.getRemoteHost(), null);
                xmlWriter.addChild(TAG_REMOTE_PORT, device.getRemotePort(), null);
                xmlWriter.addChild(TAG_MAC_ADDRESS, device.getMacAddress(), null);
                xmlWriter.addChild(TAG_CONNECTION_TIMEOUT, device.getConnectionTimeout(), null);
                xmlWriter.addChild(TAG_READ_TIMEOUT, device.getReadTimeout(), null);
                xmlWriter.addChild(TAG_CONNECTION_TYPE, device.getConnectionType().toString(), null);
                xmlWriter.addChild(TAG_SECURITY_TOKEN, device.getSecurityToken(), null);

                xmlWriter.endTag(null, TAG_SERVER);
            }

            xmlWriter.closeAndSave();

        } catch (IOException e) {
            Logger.error(TAG, "#saveToFile - Servers have not been saved.", e);
            return false;
        }
        return true;
    }

    /**
     * Load the device settings from an XML file into the device list.
     * Use this function to update an existing device list.
     *
     * @param configFile The file to read.
     * @param devices    The list to fill with loaded data.
     * @return true if load succeeded, false otherwise.
     */
    public static boolean loadFromFile(File configFile, List<NetworkDevice> devices) {

//		if (Looper.myLooper() == Looper.getMainLooper()) {
//			throw new AccessStorageOnMainThreadException("ServerSettingDao #loadFromFile");
//		}

        if (!configFile.exists()) {
            Logger.warning(TAG, "#loadFromFile - File is null or does not exist");
            return false;
        }

        NetworkDeviceXmlHandler xmlHandler = new NetworkDeviceXmlHandler();
        try {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            XMLReader reader = parserFactory.newSAXParser().getXMLReader();
            reader.setContentHandler(xmlHandler);
            reader.parse(new InputSource(new FileInputStream(configFile)));

        } catch (Exception e) {
            Logger.error(TAG, "#loadFromFile - Error occurred while parsing Server settings (file " + configFile + ")", e);
            return false;
        }

        return devices.addAll(xmlHandler.getDevices());
    }

    /**
     * Load the device list from the default XML file and returns it.
     * Use this function to load the list of devices.
     *
     * @param context The application context.
     * @return The device list or null if an error occurred.
     */
    public static List<NetworkDevice> loadList(Context context) {

        final File confFile = new File(context.getExternalFilesDir(null), FILENAME);
        final List<NetworkDevice> devices = new ArrayList<>();
        if (loadFromFile(confFile, devices)) {
            return devices;
        }
        return null;
    }

    /**
     * Load the selected device.
     *
     * @param context The application context.
     * @return The selected device.
     */
    public static NetworkDevice loadDevice(Context context, int deviceId) {

        final List<NetworkDevice> devices = loadList(context);

        try {
            return devices.get(deviceId);
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            return null;
        }
    }

    /**
     * Load the selected device.
     *
     * @param context The application context.
     * @return The selected device.
     */
    public static NetworkDevice loadSelected(Context context) {

        final List<NetworkDevice> devices = loadList(context);

        // Get the properties values
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        final int deviceId = pref.getInt(PrefKeys.KEY_SERVER_ID, PrefKeys.DEFAULT_SERVER_ID);
        try {
            return devices.get(deviceId);
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            return null;
        }
    }
}
