package org.es.uremote.network;

import static org.es.uremote.utils.Constants.DEBUG;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.os.AsyncTask;
import android.util.Log;

public class WakeOnLan extends AsyncTask<String, int[], String> {
	private static final String TAG = "WakeOnLan";    
	private static final int PORT = 9;    

	private String wake(String _ip, String _mac) {
		
		if (_ip.isEmpty() || _mac.isEmpty()) {
//			Log.i(TAG, "Usage: java WakeOnLan <broadcast-ip> <mac-address>");
//			Log.i(TAG, "Example: java WakeOnLan 192.168.0.255 00:0D:61:08:22:4A");
//			Log.i(TAG, "Example: java WakeOnLan 192.168.0.255 00-0D-61-08-22-4A");
			return "_ip.isEmpty() || _mac.isEmpty()";
		}
		
		if (DEBUG)
			Log.i(TAG, "ip : " + _ip + ", mac : " + _mac);
		String result = "ip : " + _ip + ", mac : " + _mac + "\r\n";
		
		String ipStr	= _ip; //args[0];
		String macStr	= _mac; //args[1];

		try {
			byte[] macBytes = getMacBytes(macStr);
			byte[] bytes = new byte[6 + 16 * macBytes.length];
			
			for (int i = 0; i < 6; i++) {
				bytes[i] = (byte) 0xff;
			}
			
			for (int i = 6; i < bytes.length; i += macBytes.length) {
				System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
			}

			InetAddress address = InetAddress.getByName(ipStr);
			DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, PORT);
			DatagramSocket socket = new DatagramSocket();
			socket.send(packet);
			socket.close();
			
			if (DEBUG)
				Log.d(TAG, "Wake-on-LAN packet sent.");

			result += "Wake-on-LAN packet sent.";
			
		} catch (Exception e) {
			if (DEBUG)
				Log.e(TAG, "Failed to send Wake-on-LAN packet: " + e);

			result += "Failed to send Wake-on-LAN packet: " + e;
		}
		return result;
	}

	private byte[] getMacBytes(String macStr) throws IllegalArgumentException {
		byte[] bytes = new byte[6];
		String[] hex = macStr.split("(\\:|\\-)");
		if (hex.length != 6) {
			throw new IllegalArgumentException("Invalid MAC address.");
		}
		
		try {
			for (int i = 0; i < 6; i++) {
				bytes[i] = (byte) Integer.parseInt(hex[i], 16);
			}
		}
		
		catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid hex digit in MAC address.");
		}
		
		return bytes;
	}

	@Override
	protected String doInBackground(String... params) {
		return wake(params[0], params[1]);
	}
}
