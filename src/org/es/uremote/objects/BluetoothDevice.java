package org.es.uremote.objects;

/**
 * Simple object that hold the bluetooth device data.
 *
 * @author Cyril Leroux
 * Created on 23/10/12.
 */
public class BluetoothDevice {

	/** Device paired */
	public static final String TYPE_PAIRED = "paired";
	/** Device discovered */
	public static final String TYPE_DISCOVERED = "discovered";

	private final String mName;
	private final String mAddress;
	private final String mType;

	/**
	 * Constructor
	 *
	 * @param device The {@link android.bluetooth.BluetoothDevice} used to build the {@link BluetoothDevice} object.
	 * @param type The device state.
	 */
	public BluetoothDevice(android.bluetooth.BluetoothDevice device, String type) {
		mName		= device.getName();
		mAddress	= device.getAddress();
		mType		= type;
	}

	/** @return the name of the device */
	public String getName() {
		return mName;
	}

	/** @return the address */
	public String getAddress() {
		return mAddress;
	}

	/** @return the type */
	public String getType() {
		return mType;
	}
}
