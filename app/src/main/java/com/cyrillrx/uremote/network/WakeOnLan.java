package com.cyrillrx.uremote.network;

import android.os.AsyncTask;

import com.cyrillrx.logger.Logger;
import com.cyrillrx.notifier.Toaster;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * This class allow the application to send a Magic Packet on the network to wake a PC up.
 *
 * @author Cyril Leroux
 *         Created on 08/05/12.
 */
public class WakeOnLan extends AsyncTask<String, int[], String> {

    private static final String TAG = WakeOnLan.class.getSimpleName();

    private static final int PORT = 9;

    @Override
    protected String doInBackground(String... params) { return wake(params[0], params[1]); }

    @Override
    protected void onPostExecute(String result) { Toaster.toast(result); }

    /**
     * Create and send a magic packet to the specified address to wake the PC.
     *
     * @param broadcastIp The ip address.
     * @param macAddress  The mac address.
     * @return The return message.
     */
    private static String wake(String broadcastIp, String macAddress) {

        if (broadcastIp.isEmpty() || macAddress.isEmpty()) {
            return "ip.isEmpty() || mac.isEmpty()";
        }

        Logger.info(TAG, "ip : " + broadcastIp + ", mac : " + macAddress);

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

            Logger.debug(TAG, "Wake-on-LAN packet sent.");

            result += "Wake-on-LAN packet sent.";

        } catch (Exception e) {
            Logger.error(TAG, "Failed to send Wake-on-LAN packet: " + e);
            result += "Failed to send Wake-on-LAN packet: " + e;
        }
        return result;
    }

    private static byte[] getMacBytes(String macStr) throws IllegalArgumentException {
        byte[] bytes = new byte[6];
        String[] hex = macStr.split("(\\:|\\-)");
        if (hex.length != 6) {
            throw new IllegalArgumentException("Invalid MAC address.");
        }

        try {
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) Integer.parseInt(hex[i], 16);
            }

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hex digit in MAC address.");
        }

        return bytes;
    }
}
