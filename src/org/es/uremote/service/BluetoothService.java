package org.es.uremote.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.es.utils.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import static org.es.uremote.utils.IntentKeys.DEVICE_NAME;
import static org.es.uremote.utils.IntentKeys.MESSAGE_DEVICE_NAME;
import static org.es.uremote.utils.IntentKeys.MESSAGE_READ;
import static org.es.uremote.utils.IntentKeys.MESSAGE_STATE_CHANGE;
import static org.es.uremote.utils.IntentKeys.MESSAGE_TOAST;
import static org.es.uremote.utils.IntentKeys.MESSAGE_WRITE;
import static org.es.uremote.utils.IntentKeys.TOAST;

/**
 * This class set up and manage Bluetooth connections.
 * A thread listens for incoming connections.
 * An other thread for connecting with a device.
 * The last thread performs data transmissions when connected.
 *
 * @author Cyril Leroux
 */
public class BluetoothService {

	/** This token is used to synchronize class methods. */
	private static final Object TOKEN = new Object();

	// Constants that indicate the current connection state
	/** Doing nothing. */
	public static final int STATE_NONE = 0;
	/** Doing nothing. */
	public static final String ACTION_NONE = "None";

	/** Listening for incoming connections. */
	public static final int STATE_LISTEN = 1;
	/** Listening for incoming connections. */
	public static final String ACTION_LISTEN = "Listening";

	/** Initiating an outgoing connection. */
	public static final int STATE_CONNECTING = 2;
	/** Initiating an outgoing connection. */
	public static final String ACTION_CONNECTING = "Connecting";

	/** Connected to remote device. */
	public static final int STATE_CONNECTED = 3;
	/** Connected to remote device. */
	public static final String ACTION_CONNECTED = "Connected";

	// Name for the SDP record when creating server socket
	private static final String TAG 			= "BluetoothService";
	private static final String NAME_SECURE		= "BluetoothConnectorSecure";
	private static final String NAME_INSECURE	= "BluetoothConnectorInsecure";
	private static final String SOCKET_TYPE_SECURE		= "Secure";
	private static final String SOCKET_TYPE_INSECURE	= "Insecure";

	// Unique UUID for this application
	private static final UUID MY_UUID_SECURE	= UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private static final UUID MY_UUID_INSECURE	= UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	// Member fields
	private final BluetoothAdapter mAdapter;
	private final Handler mHandler;
	private AcceptThread mSecureAcceptThread;
	private AcceptThread mInsecureAcceptThread;
	private ConnectThread mConnectThread;
	private ConnectedThread mConnectedThread;
	private int mState;

	/**
	 * @param context
	 * @param handler
	 */
	public BluetoothService(Context context, Handler handler) {
		mAdapter	= BluetoothAdapter.getDefaultAdapter();
		mState		= STATE_NONE;
		mHandler	= handler;
	}

	/**
	 * Set the current state of the chat connection
	 *
	 * @param state An integer defining the current connection state
	 */
	private synchronized void setState(int state) {
		Log.info(TAG, "Synchronized setState() " + getStateAsString(mState) + " -> " + getStateAsString(state));
		mState = state;

		// Give the new state to the Handler so the UI Activity can update
		mHandler.obtainMessage(MESSAGE_STATE_CHANGE, state, -1).sendToTarget();

	}

	/** @return The current connection state. */
	public synchronized int getState() {
		Log.info(TAG, "Synchronized getState(), value : " + getStateAsString(mState));
		return mState;
	}

	/**
	 * Start the chat service. Specifically start AcceptThread to begin a
	 * session in listening (server) mode. Called by the Activity onResume()
	 */
	public synchronized void start() {
		Log.info(TAG, "Synchronized start()");

		// Cancel any thread attempting to make a connection
		if (mConnectThread != null) {mConnectThread.closeSocket(); mConnectThread = null;}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {mConnectedThread.closeSocket(); mConnectedThread = null;}

		setState(STATE_LISTEN);

		// Start the thread to listen on a BluetoothServerSocket
		if (mSecureAcceptThread == null) {
			mSecureAcceptThread = new AcceptThread(true);
			mSecureAcceptThread.start();
		}
		if (mInsecureAcceptThread == null) {
			mInsecureAcceptThread = new AcceptThread(false);
			mInsecureAcceptThread.start();
		}
	}

	/**
	 * Start the ConnectThread to initiate a connection to a remote device.
	 *
	 * @param device The BluetoothDevice to connect
	 * @param secure Socket Security type - Secure (true) , Insecure (false)
	 */
	public synchronized void connect(BluetoothDevice device, boolean secure) {
		Log.info(TAG, "Synchronized connect to: " + device);

		// Cancel any thread attempting to make a connection
		if (mState == STATE_CONNECTING) {
			if (mConnectThread != null) {mConnectThread.closeSocket(); mConnectThread = null;}
		}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {mConnectedThread.closeSocket(); mConnectedThread = null;}

		// Start the thread to connect with the given device
		mConnectThread = new ConnectThread(device, secure);
		mConnectThread.start();
		setState(STATE_CONNECTING);
	}

	/**
	 * Start the ConnectedThread to begin managing a Bluetooth connection
	 *
	 * @param socket The BluetoothSocket on which the connection was made
	 * @param device The BluetoothDevice that has been connected
	 * @param socketType
	 */
	public synchronized void connected(BluetoothSocket socket, BluetoothDevice device, final String socketType) {
		Log.info(TAG, "Synchronized connected, Socket Type:" + socketType);

		// Cancel the thread that completed the connection
		if (mConnectThread != null) {mConnectThread.closeSocket(); mConnectThread = null;}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {mConnectedThread.closeSocket(); mConnectedThread = null;}

		// Cancel the accept thread because we only want to connect to one device
		if (mSecureAcceptThread != null) {
			mSecureAcceptThread.closeSocket();
			mSecureAcceptThread = null;
		}
		if (mInsecureAcceptThread != null) {
			mInsecureAcceptThread.closeSocket();
			mInsecureAcceptThread = null;
		}

		// Start the thread to manage the connection and perform transmissions
		mConnectedThread = new ConnectedThread(socket, socketType);
		mConnectedThread.start();

		// Send the name of the connected device back to the UI Activity
		Message msg = mHandler.obtainMessage(MESSAGE_DEVICE_NAME);
		Bundle bundle = new Bundle();
		bundle.putString(DEVICE_NAME, device.getName());
		msg.setData(bundle);
		mHandler.sendMessage(msg);

		setState(STATE_CONNECTED);
	}

	/** Stop all threads */
	public synchronized void stop() {
		Log.info(TAG, "Synchronized stop()");

		if (mConnectThread != null) {
			mConnectThread.closeSocket();
			mConnectThread = null;
		}

		if (mConnectedThread != null) {
			mConnectedThread.closeSocket();
			mConnectedThread = null;
		}

		if (mSecureAcceptThread != null) {
			mSecureAcceptThread.closeSocket();
			mSecureAcceptThread = null;
		}

		if (mInsecureAcceptThread != null) {
			mInsecureAcceptThread.closeSocket();
			mInsecureAcceptThread = null;
		}
		setState(STATE_NONE);
	}

	/**
	 * @param state
	 * @return The state as a string.
	 */
	public static String getStateAsString(int state) {
		switch (state) {

			case STATE_CONNECTED:
				return ACTION_CONNECTED;

			case STATE_CONNECTING:
				return ACTION_CONNECTING;

			case STATE_LISTEN:
				return ACTION_LISTEN;

			case STATE_NONE:
				return ACTION_NONE;
		}
		return ACTION_NONE;
	}

	/**
	 * Asynchronously write to the ConnectedThread
	 *
	 * @param out The bytes to write
	 * @see ConnectedThread#write(byte[])
	 */
	public void write(byte[] out) {
		// Create temporary object
		ConnectedThread r;
		// Synchronize a copy of the ConnectedThread
		synchronized (TOKEN) {
			if (mState != STATE_CONNECTED) {
				return;
			}
			r = mConnectedThread;
		}
		// Perform the write asynchronously
		r.write(out);
	}

	/** Indicate that the connection attempt failed and notify the UI Activity. */
	private void connectionFailed() {
		Log.info(TAG, "connectionFailed()");

		// Send a failure message back to the Activity
		Message msg = mHandler.obtainMessage(MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(TOAST, "Unable to connect device");
		msg.setData(bundle);
		mHandler.sendMessage(msg);

		// Start the service over to restart listening mode
		BluetoothService.this.start();
	}

	/** Indicate that the connection was lost and notify the UI Activity. */
	private void connectionLost() {
		Log.info(TAG, "connectionLost()");

		// Send a failure message back to the Activity
		Message msg = mHandler.obtainMessage(MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(TOAST, "Device connection was lost");
		msg.setData(bundle);
		mHandler.sendMessage(msg);

		// Start the service over to restart listening mode
		BluetoothService.this.start();
	}

	/**
	 * This thread runs while listening for incoming connections. It behaves
	 * like a server-side client. It runs until a connection is accepted
	 * (or until canceled).
	 */
	private class AcceptThread extends Thread {
		private final BluetoothServerSocket mmServerSocket;
		private final String mSocketType;

		public AcceptThread(boolean secure) {
			Log.debug(TAG, "AcceptThread constructor");

			BluetoothServerSocket tmp = null;
			mSocketType = secure ? SOCKET_TYPE_SECURE : SOCKET_TYPE_INSECURE;

			// Create a new listening server socket
			try {
				if (secure) {
					tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE, MY_UUID_SECURE);
				} else {
					tmp = mAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME_INSECURE, MY_UUID_INSECURE);
				}
			} catch (IOException e) {
				Log.error(TAG, "Socket Type: " + mSocketType + "listen() failed", e);
			}
			mmServerSocket = tmp;
		}

		@Override
		public void run() {
			Log.debug(TAG, "AcceptThread run() | SocketType:" + mSocketType);

			setName("ListeningThread" + mSocketType);

			BluetoothSocket socket = null;

			// Listen to the server socket if we're not connected
			while (mState != STATE_CONNECTED) {
				try {
					// This is a blocking call and will only return on a
					// successful connection or an exception
					socket = mmServerSocket.accept();

				} catch (IOException e) {
					Log.error(TAG, "Socket Type: " + mSocketType + "accept() failed", e);
					break;
				}

				// If a connection was accepted
				if (socket != null) {
					synchronized (BluetoothService.this) {
						switch (mState) {
							case STATE_LISTEN:
							case STATE_CONNECTING:
								// Situation normal. Start the connected thread.
								connected(socket, socket.getRemoteDevice(), mSocketType);
								break;

							case STATE_NONE:
							case STATE_CONNECTED:
								// Either not ready or already connected. Terminate new socket.
								try {
									socket.close();
								} catch (IOException e) {
									Log.error(TAG, "Could not close unwanted socket", e);
								}
								break;

						}
					}
				}
			}
			Log.debug(TAG, "END mAcceptThread, socket Type: " + mSocketType);

		}

		public void closeSocket() {
			Log.debug(TAG, "AcceptThread closeSocket() | SocketType:" + mSocketType);

			try {
				mmServerSocket.close();
			} catch (IOException e) {
				Log.error(TAG, "AcceptThread closeSocket() |Socket Type" + mSocketType + "close() of server failed", e);
			}
		}
	}

	/**
	 * This thread runs while attempting to make an outgoing connection
	 * with a device. It runs straight through; the connection either
	 * succeeds or fails.
	 */
	private class ConnectThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;
		private final String mSocketType;

		public ConnectThread(BluetoothDevice device, boolean secure) {
			Log.debug(TAG, "ConnectThread constructor");

			mmDevice = device;
			BluetoothSocket tmp = null;
			mSocketType = secure ? "Secure" : "Insecure";

			// Get a BluetoothSocket for a connection with the
			// given BluetoothDevice
			try {
				if (secure) {
					tmp = device.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
				} else {
					tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID_INSECURE);
				}
			} catch (IOException e) {
				Log.error(TAG, "Socket Type: " + mSocketType + "create() failed", e);
			}
			mmSocket = tmp;
		}

		@Override
		public void run() {
			Log.debug(TAG, "ConnectThread.run() | SocketType:" + mSocketType);
			setName("ConnectThread" + mSocketType);

			// Always cancel discovery because it will slow down a connection
			mAdapter.cancelDiscovery();

			// Make a connection to the BluetoothSocket
			try {
				// This is a blocking call and will only return on a
				// successful connection or an exception
				mmSocket.connect();

			} catch (IOException e) {
				Log.error(TAG, "ConnectThread.run " + e);
				// Close the socket
				closeSocket();
				connectionFailed();
				return;
			}

			// Reset the ConnectThread because we're done
			synchronized (BluetoothService.this) {
				mConnectThread = null;
			}

			// Start the connected thread
			connected(mmSocket, mmDevice, mSocketType);
		}

		public void closeSocket() {
			Log.debug(TAG, "ConnectThread closeSocket() | SocketType:" + mSocketType);

			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.error(TAG, "close() of connect " + mSocketType + " socket failed", e);
			}
		}
	}

	/**
	 * This thread runs during a connection with a remote device.
	 * It handles all incoming and outgoing transmissions.
	 */
	private class ConnectedThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;

		public ConnectedThread(BluetoothSocket socket, String socketType) {
			Log.debug(TAG, "ConnectedThread constructor");

			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			// Get the BluetoothSocket input and output streams
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
				Log.error(TAG, "temp sockets not created", e);
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		@Override
		public void run() {
			Log.debug(TAG, "ConnectedThread.run()");

			byte[] buffer = new byte[1024];
			int bytes;

			// Keep listening to the InputStream while connected
			while (true) {
				try {
					// Read from the InputStream
					bytes = mmInStream.read(buffer);

					// Send the obtained bytes to the UI Activity
					mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();

				} catch (IOException e) {
					Log.error(TAG, "disconnected", e);
					connectionLost();

					// Start the service over to restart listening mode
					BluetoothService.this.start();
					break;
				}
			}
		}

		/**
		 * Write to the connected OutStream.
		 *
		 * @param buffer The bytes to write
		 */
		public void write(byte[] buffer) {
			try {
				mmOutStream.write(buffer);

				// Share the sent message back to the UI Activity
				mHandler.obtainMessage(MESSAGE_WRITE, -1, -1, buffer).sendToTarget();

			} catch (IOException e) {
				Log.error(TAG, "Exception during write", e);
			}
		}

		public void closeSocket() {
			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.error(TAG, "close() of connect socket failed", e);
			}
		}
	}
}

