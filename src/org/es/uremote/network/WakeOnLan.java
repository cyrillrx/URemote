package org.es.uremote.network;

import static org.es.uremote.utils.Constants.MESSAGE_WHAT_TOAST;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.es.uremote.BuildConfig;
import org.es.utils.Log;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

/**
 * This class allow the application to send a Magic Packet on the network to wake a PC up.
 *
 * @author Cyril Leroux
 *
 */
public class WakeOnLan extends AsyncTask<String, int[], String> {

	private static final String TAG	= "WakeOnLan";
	private static final int PORT	= 9;
	private final Handler mHandler;

	/**
	 * @param handler
	 */
	public WakeOnLan(Handler handler) {
		mHandler = handler;
	}

	@Override
	protected String doInBackground(String... params) {
		return wake(params[0], params[1]);
	}

	@Override
	protected void onPostExecute(String result) {

		Message msg = new Message();
		msg.what = MESSAGE_WHAT_TOAST;
		msg.obj = result;
		mHandler.sendMessage(msg);

		super.onPostExecute(result);
	}

	/**
	 * Create and send a magic packet to the specified address to wake the PC.
	 * @param broadcastIp The ip address.
	 * @param macAddress The mac address.
	 * @return The return message.
	 */
	private String wake(String broadcastIp, String macAddress) {

		if (broadcastIp.isEmpty() || macAddress.isEmpty()) {
			return "ip.isEmpty() || mac.isEmpty()";
		}

		if (BuildConfig.DEBUG) {
			Log.info(TAG, "ip : " + broadcastIp + ", mac : " + macAddress);
		}

		String result = "ip : " + broadcastIp + ", mac : " + macAddress + "\r\n";

		try {
			byte[] macBytes = getMacBytes(macAddress);
			byte[] bytes = new byte[6 + 16 * macBytes.length];

			for (int i = 0; i < 6; i++) {
				bytes[i] = (byte) 0xff;
			}

			for (int i = 6; i < bytes.length; i += macBytes.length) {
				System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
			}

			InetAddress address = InetAddress.getByName(broadcastIp);
			DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, PORT);
			DatagramSocket socket = new DatagramSocket();
			socket.send(packet);
			socket.close();

			Log.debug(TAG, "Wake-on-LAN packet sent.");

			result += "Wake-on-LAN packet sent.";

		} catch (Exception e) {
			Log.error(TAG, "Failed to send Wake-on-LAN packet: " + e);
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
}
