package org.es.uremote.computer.dao;

import org.es.uremote.device.NetworkDevice;
import org.es.uremote.device.NetworkDevice.ConnectionType;
import org.es.utils.Log;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

import static org.es.uremote.computer.dao.NetworkDeviceDao.TAG_BROADCAST;
import static org.es.uremote.computer.dao.NetworkDeviceDao.TAG_CONNECTION_TIMEOUT;
import static org.es.uremote.computer.dao.NetworkDeviceDao.TAG_CONNECTION_TYPE;
import static org.es.uremote.computer.dao.NetworkDeviceDao.TAG_LOCAL_HOST;
import static org.es.uremote.computer.dao.NetworkDeviceDao.TAG_LOCAL_PORT;
import static org.es.uremote.computer.dao.NetworkDeviceDao.TAG_MAC_ADDRESS;
import static org.es.uremote.computer.dao.NetworkDeviceDao.TAG_NAME;
import static org.es.uremote.computer.dao.NetworkDeviceDao.TAG_READ_TIMEOUT;
import static org.es.uremote.computer.dao.NetworkDeviceDao.TAG_REMOTE_HOST;
import static org.es.uremote.computer.dao.NetworkDeviceDao.TAG_REMOTE_PORT;
import static org.es.uremote.computer.dao.NetworkDeviceDao.TAG_ROOT;
import static org.es.uremote.computer.dao.NetworkDeviceDao.TAG_SECURITY_TOKEN;
import static org.es.uremote.computer.dao.NetworkDeviceDao.TAG_SERVER;

/**
 * This component allow to parse a XML file that contains device connection information.
 *
 * @author Cyril Leroux
 *         Created on 22/05/13.
 */
public class NetworkDeviceXmlHandler extends DefaultHandler {

    private static final String TAG = NetworkDeviceXmlHandler.class.getSimpleName();

    private boolean mCurrentElement = false;
    private boolean mLoaded = false;
    private String mCurrentValue;

    private NetworkDevice.Builder mBuilder;
    private List<NetworkDevice> mDevices;

    /** Called tag opening (<tag>). */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        mCurrentElement = true;

        if (localName.equals(TAG_ROOT)) {
            mLoaded = false;
            mDevices = new ArrayList<>();

        } else if (localName.equals(TAG_SERVER)) {
            if (mBuilder == null) {
                mBuilder = NetworkDevice.newBuilder();
            } else {
                mBuilder.clear();
            }
        }
    }

    /** Called at tag closure (</tag>) to get the value of the parsed element. */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        mCurrentElement = false;

        if (localName.equals(TAG_NAME)) {
            mBuilder.setName(mCurrentValue);

        } else if (localName.equals(TAG_LOCAL_HOST)) {
            mBuilder.setLocalHost(mCurrentValue);

        } else if (localName.equals(TAG_LOCAL_PORT)) {
            mBuilder.setLocalPort(Integer.parseInt(mCurrentValue));

        } else if (localName.equals(TAG_BROADCAST)) {
            mBuilder.setBroadcast(mCurrentValue);

        } else if (localName.equals(TAG_REMOTE_HOST)) {
            mBuilder.setRemoteHost(mCurrentValue);

        } else if (localName.equals(TAG_REMOTE_PORT)) {
            mBuilder.setRemotePort(Integer.parseInt(mCurrentValue));

        } else if (localName.equals(TAG_MAC_ADDRESS)) {
            mBuilder.setMacAddress(mCurrentValue);

        } else if (localName.equals(TAG_CONNECTION_TIMEOUT)) {
            mBuilder.setConnectionTimeout(Integer.parseInt(mCurrentValue));

        } else if (localName.equals(TAG_READ_TIMEOUT)) {
            mBuilder.setReadTimeout(Integer.parseInt(mCurrentValue));

        } else if (localName.equals(TAG_SECURITY_TOKEN)) {
            mBuilder.setSecurityToken(mCurrentValue);

        } else if (localName.equals(TAG_CONNECTION_TYPE)) {
            mBuilder.setConnectionType(ConnectionType.valueOf(mCurrentValue));

        } else if (localName.equals(TAG_SERVER)) {
            try {
                mDevices.add(mBuilder.build());
            } catch (Exception e) {
                Log.error(TAG, "#endElement", e);
            }

        } else if (localName.equals(TAG_ROOT)) {
            mLoaded = true;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (!mCurrentElement) {
            return;
        }

        mCurrentValue = new String(ch, start, length);
        mCurrentElement = false;
    }

    /** @return A list of {@link org.es.uremote.device.NetworkDevice}. */
    public List<NetworkDevice> getDevices() {
        if (mLoaded) {
            return mDevices;
        }
        return new ArrayList<>();
    }
}
