package com.cyrillrx.uremote.ui.computer.dao;

import com.cyrillrx.logger.Logger;
import com.cyrillrx.uremote.common.device.NetworkDevice;
import com.cyrillrx.uremote.common.device.NetworkDevice.ConnectionType;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

import static com.cyrillrx.uremote.ui.computer.dao.NetworkDeviceDao.TAG_BROADCAST;
import static com.cyrillrx.uremote.ui.computer.dao.NetworkDeviceDao.TAG_CONNECTION_TIMEOUT;
import static com.cyrillrx.uremote.ui.computer.dao.NetworkDeviceDao.TAG_CONNECTION_TYPE;
import static com.cyrillrx.uremote.ui.computer.dao.NetworkDeviceDao.TAG_LOCAL_HOST;
import static com.cyrillrx.uremote.ui.computer.dao.NetworkDeviceDao.TAG_LOCAL_PORT;
import static com.cyrillrx.uremote.ui.computer.dao.NetworkDeviceDao.TAG_MAC_ADDRESS;
import static com.cyrillrx.uremote.ui.computer.dao.NetworkDeviceDao.TAG_NAME;
import static com.cyrillrx.uremote.ui.computer.dao.NetworkDeviceDao.TAG_READ_TIMEOUT;
import static com.cyrillrx.uremote.ui.computer.dao.NetworkDeviceDao.TAG_REMOTE_HOST;
import static com.cyrillrx.uremote.ui.computer.dao.NetworkDeviceDao.TAG_REMOTE_PORT;
import static com.cyrillrx.uremote.ui.computer.dao.NetworkDeviceDao.TAG_ROOT;
import static com.cyrillrx.uremote.ui.computer.dao.NetworkDeviceDao.TAG_SECURITY_TOKEN;
import static com.cyrillrx.uremote.ui.computer.dao.NetworkDeviceDao.TAG_SERVER;

/**
 * This component allow to parse a XML file that contains device connection information.
 *
 * @author Cyril Leroux
 *         Created on 22/05/13.
 */
public class NetworkDeviceXmlHandler extends DefaultHandler {

    private static final String TAG = NetworkDeviceXmlHandler.class.getSimpleName();

    private boolean currentElement = false;
    private boolean loaded = false;
    private String currentValue;

    private NetworkDevice.Builder builder;
    private List<NetworkDevice> devices;

    /** Called tag opening (<tag>). */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        currentElement = true;

        if (localName.equals(TAG_ROOT)) {
            loaded = false;
            devices = new ArrayList<>();

        } else if (localName.equals(TAG_SERVER)) {
            if (builder == null) {
                builder = NetworkDevice.newBuilder();
            } else {
                builder.clear();
            }
        }
    }

    /** Called at tag closure (</tag>) to get the value of the parsed element. */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        currentElement = false;

        if (localName.equals(TAG_NAME)) {
            builder.setName(currentValue);

        } else if (localName.equals(TAG_LOCAL_HOST)) {
            builder.setLocalHost(currentValue);

        } else if (localName.equals(TAG_LOCAL_PORT)) {
            builder.setLocalPort(Integer.parseInt(currentValue));

        } else if (localName.equals(TAG_BROADCAST)) {
            builder.setBroadcast(currentValue);

        } else if (localName.equals(TAG_REMOTE_HOST)) {
            builder.setRemoteHost(currentValue);

        } else if (localName.equals(TAG_REMOTE_PORT)) {
            builder.setRemotePort(Integer.parseInt(currentValue));

        } else if (localName.equals(TAG_MAC_ADDRESS)) {
            builder.setMacAddress(currentValue);

        } else if (localName.equals(TAG_CONNECTION_TIMEOUT)) {
            builder.setConnectionTimeout(Integer.parseInt(currentValue));

        } else if (localName.equals(TAG_READ_TIMEOUT)) {
            builder.setReadTimeout(Integer.parseInt(currentValue));

        } else if (localName.equals(TAG_SECURITY_TOKEN)) {
            builder.setSecurityToken(currentValue);

        } else if (localName.equals(TAG_CONNECTION_TYPE)) {
            builder.setConnectionType(ConnectionType.valueOf(currentValue));

        } else if (localName.equals(TAG_SERVER)) {
            try {
                devices.add(builder.build());
            } catch (Exception e) {
                Logger.error(TAG, "#endElement", e);
            }

        } else if (localName.equals(TAG_ROOT)) {
            loaded = true;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (!currentElement) {
            return;
        }

        currentValue = new String(ch, start, length);
        currentElement = false;
    }

    /** @return A list of {@link com.cyrillrx.uremote.common.device.NetworkDevice}. */
    public List<NetworkDevice> getDevices() {
        if (loaded) {
            return devices;
        }
        return new ArrayList<>();
    }
}
