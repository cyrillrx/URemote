package org.es.uremote.components;

import org.es.uremote.objects.ServerBuilder;
import org.es.uremote.objects.ServerSetting;
import org.es.uremote.objects.ServerSetting.ConnectionType;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

import static org.es.uremote.computer.dao.ServerSettingDao.TAG_BROADCAST;
import static org.es.uremote.computer.dao.ServerSettingDao.TAG_CONNECTION_TIMEOUT;
import static org.es.uremote.computer.dao.ServerSettingDao.TAG_CONNECTION_TYPE;
import static org.es.uremote.computer.dao.ServerSettingDao.TAG_LOCAL_HOST;
import static org.es.uremote.computer.dao.ServerSettingDao.TAG_LOCAL_PORT;
import static org.es.uremote.computer.dao.ServerSettingDao.TAG_MAC_ADDRESS;
import static org.es.uremote.computer.dao.ServerSettingDao.TAG_NAME;
import static org.es.uremote.computer.dao.ServerSettingDao.TAG_READ_TIMEOUT;
import static org.es.uremote.computer.dao.ServerSettingDao.TAG_REMOTE_HOST;
import static org.es.uremote.computer.dao.ServerSettingDao.TAG_REMOTE_PORT;
import static org.es.uremote.computer.dao.ServerSettingDao.TAG_ROOT;
import static org.es.uremote.computer.dao.ServerSettingDao.TAG_SERVER;

/**
 * This component allow to parse a XML file that contains server connection information.
 *
 * @author Cyril Leroux
 * Created on 22/05/13.
 */
public class ServerXmlHandler extends DefaultHandler {
	private boolean mCurrentElement	= false;
	private boolean mLoaded			= false;
	private String mCurrentValue;

	private ServerBuilder mBuilder;
	private List<ServerSetting> mServers;

	/** Called tag opening (<tag>). */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		mCurrentElement = true;

		if (localName.equals(TAG_ROOT)) {
			mLoaded = false;
			mServers = new ArrayList<>();

		} else if (localName.equals(TAG_SERVER)) {
			mBuilder = new ServerBuilder();
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

		} else if (localName.equals(TAG_CONNECTION_TYPE)) {
			mBuilder.setConnectionType(ConnectionType.valueOf(mCurrentValue));

		} else if (localName.equals(TAG_SERVER)) {
			try {
				ServerSetting server = mBuilder.build();
				mServers.add(server);
			} catch (Exception e) {

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

	/** @return A list of {@link org.es.uremote.objects.ServerSetting}. */
	public List<ServerSetting> getServers() {
		if (mLoaded) {
			return mServers;
		}
		return new ArrayList<>();
	}
}
