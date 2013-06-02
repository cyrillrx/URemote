package org.es.uremote.components;

import java.util.ArrayList;
import java.util.List;

import org.es.uremote.objects.ServerBuilder;
import org.es.uremote.objects.ServerInfo;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This component allow to parse a XML file that contains server connection information.
 * @author Cyril Leroux
 */
public class ServerXmlHandler extends DefaultHandler {
	private boolean mCurrentElement	= false;
	private boolean mLoaded			= false;
	private String mCurrentValue;

	private static final String TAG_ROOT		= "servers";
	private static final String TAG_SERVER		= "server";
	private static final String TAG_LOCAL_IP	= "local_ip_address";
	private static final String TAG_LOCAL_PORT	= "local_port";
	private static final String TAG_REMOTE_IP	= "remote_ip_address";
	private static final String TAG_REMOTE_PORT	= "remote_port";
	private static final String TAG_MAC_ADDRESS	= "mac_address";
	private static final String TAG_CONNECTION_TIMEOUT	= "connection_timeout";
	private static final String TAG_READ_TIMEOUT		= "read_timeout";

	private ServerBuilder mBuilder;
	private List<ServerInfo> mServers;

	/**
	 * Called tag opening (<tag>).
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		mCurrentElement = true;

		if (localName.equals(TAG_ROOT)) {
			mLoaded = false;
			mServers = new ArrayList<ServerInfo>();

		}
		if (localName.equals(TAG_SERVER)) {
			mBuilder = new ServerBuilder();
		}
	}

	/**
	 * Called at tag closure (</tag>) to get the value of the parsed element.
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		mCurrentElement = false;

		if (localName.equals(TAG_LOCAL_IP)) {
			mBuilder.setLocalHost(mCurrentValue);

		} else if (localName.equals(TAG_LOCAL_PORT)) {
			mBuilder.setLocalPort(Integer.parseInt(mCurrentValue));

		} else if (localName.equals(TAG_REMOTE_IP)) {
			mBuilder.setRemoteHost(mCurrentValue);

		} else if (localName.equals(TAG_REMOTE_PORT)) {
			mBuilder.setRemotePort(Integer.parseInt(mCurrentValue));

		} else if (localName.equals(TAG_MAC_ADDRESS)) {
			mBuilder.setMacAddress(mCurrentValue);

		} else if (localName.equals(TAG_CONNECTION_TIMEOUT)) {
			mBuilder.setConnectionTimeout(Integer.parseInt(mCurrentValue));

		} else if (localName.equals(TAG_READ_TIMEOUT)) {
			mBuilder.setReadTimout(Integer.parseInt(mCurrentValue));

		} else if (localName.equals(TAG_SERVER)) {
			ServerInfo server = mBuilder.build();
			if (server != null) {
				mServers.add(server);
			}

		} else if (localName.equals(TAG_ROOT)) {
			mLoaded = true;
		}
	}

	@Override
	public void characters(char[] _ch, int _start, int _length) throws SAXException {
		if (!mCurrentElement) {
			return;
		}

		mCurrentValue = new String(_ch, _start, _length);
		mCurrentElement = false;
	}

	/**
	 * @return A list of {@link ServerInfo}.
	 */
	public List<ServerInfo> getServers() {
		if (mLoaded) {
			return mServers;
		}
		return null;
	}
}
