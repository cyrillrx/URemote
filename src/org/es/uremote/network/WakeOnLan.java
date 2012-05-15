package org.es.uremote.network;

import static org.es.uremote.utils.Constants.DEBUG;
import static org.es.uremote.utils.Constants.MESSAGE_WHAT_TOAST;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class WakeOnLan extends AsyncTask<String, int[], String> {
	// TODO Internationnaliser la classe
	private static final String TAG = "WakeOnLan";    
	private static final int PORT = 9;   
	private Handler mHandler;

	public WakeOnLan(Handler _handler) {
		mHandler = _handler;
	}

	@Override
	protected String doInBackground(String... _params) {
		return wake(_params[0], _params[1]);
	}

	@Override
	protected void onPostExecute(String _result) {

		Message msg = new Message();
		msg.what = MESSAGE_WHAT_TOAST;
		msg.obj = _result;
		mHandler.sendMessage(msg);

		super.onPostExecute(_result);
	}

	/**
	 * Crée et envoie un packet magique à l'adresse spécifiée pour reveiller le PC.
	 * @param _ip
	 * @param _mac
	 * @return Le message de retour
	 */
	private String wake(String _ip, String _mac) {

		if (_ip.isEmpty() || _mac.isEmpty()) {
			return "_ip.isEmpty() || _mac.isEmpty()";
		}

		// TODO Virer le log
		if (DEBUG)
			Log.i(TAG, "ip : " + _ip + ", mac : " + _mac);
		
		String result = "ip : " + _ip + ", mac : " + _mac + "\r\n";

		String ipStr	= _ip;
		String macStr	= _mac;

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
}
