package org.es.uremote.components;

import static org.es.uremote.objects.ServerInfo.*;

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

		if (localName.equals(TAG_NAME)) {
			mBuilder.setName(mCurrentValue);

		} else if (localName.equals(TAG_LOCAL_HOST)) {
			mBuilder.setLocalHost(mCurrentValue);

		} else if (localName.equals(TAG_LOCAL_PORT)) {
			mBuilder.setLocalPort(Integer.parseInt(mCurrentValue));

		} else if (localName.equals(TAG_REMOTE_HOST)) {
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
			try {
				ServerInfo server = mBuilder.build();
				mServers.add(server);
			} catch (Exception e) {

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
