package com.cyrillrx.uremote.common.device;

/**
 * Simple object that hold the bluetooth device data.
 *
 * @author Cyril Leroux
 *         Created on 23/10/12.
 */
public class BluetoothDevice extends ConnectedDevice {

    /** Device paired */
    public static final String TYPE_PAIRED = "paired";
    /** Device discovered */
    public static final String TYPE_DISCOVERED = "discovered";

    private final String address;
    private final String type;

    /**
     * Constructor
     *
     * @param device The {@link android.bluetooth.BluetoothDevice} used to build the {@link BluetoothDevice} object.
     * @param type   The device state.
     */
    public BluetoothDevice(android.bluetooth.BluetoothDevice device, String type) {
        name = device.getName();
        address = device.getAddress();
        this.type = type;
    }

    /** @return the address */
    public String getAddress() { return address; }

    /** @return the type */
    public String getType() { return type; }
}
