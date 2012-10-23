package org.es.uremote.objects;

import android.bluetooth.BluetoothDevice;

/**
 * Simple object that hold the bluetooth device informations.
 * @author Cyril Leroux
 *
 */
public class BTDevice {

	/** Device paired */
	public static final String TYPE_PAIRED = "paired";
	/** Device discovered */
	public static final String TYPE_DISCOVERED = "discovered";

	private final String mName;
	private final String mAddress;
	private final String mType;

	/**
	 * Constructor
	 * @param device
	 * @param type
	 */
	public BTDevice(BluetoothDevice device, String type) {
		mName		= device.getName();
		mAddress	= device.getAddress();
		mType		= type;
	}

	/**
	 * @return the name of the device
	 */
	public String getName() {
		return mName;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return mAddress;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return mType;
	}
}
