package com.cyrillrx.uremote.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.cyrillrx.logger.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import static com.cyrillrx.uremote.utils.IntentKeys.DEVICE_NAME;
import static com.cyrillrx.uremote.utils.IntentKeys.MESSAGE_DEVICE_NAME;
import static com.cyrillrx.uremote.utils.IntentKeys.MESSAGE_READ;
import static com.cyrillrx.uremote.utils.IntentKeys.MESSAGE_STATE_CHANGE;
import static com.cyrillrx.uremote.utils.IntentKeys.MESSAGE_TOAST;
import static com.cyrillrx.uremote.utils.IntentKeys.MESSAGE_WRITE;
import static com.cyrillrx.uremote.utils.IntentKeys.TOAST;

/**
 * This class set up and manage Bluetooth connections.
 * A thread listens for incoming connections.
 * An other thread for connecting with a device.
 * The last thread performs data transmissions when connected.
 *
 * @author Cyril Leroux
 *         Created on 23/10/12.
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
    private static final String TAG = BluetoothService.class.getSimpleName();
    private static final String NAME_SECURE = "BluetoothConnectorSecure";
    private static final String NAME_INSECURE = "BluetoothConnectorInsecure";
    private static final String SOCKET_TYPE_SECURE = "Secure";
    private static final String SOCKET_TYPE_INSECURE = "Insecure";

    // Unique UUID for this application
    private static final UUID MY_UUID_SECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final UUID MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Member fields
    private final BluetoothAdapter adapter;
    private final Handler handler;
    private AcceptThread secureAcceptThread;
    private AcceptThread insecureAcceptThread;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private int state;

    public BluetoothService() {
        this(null);
    }

    /**
     * @param handler
     */
    public BluetoothService(Handler handler) {
        adapter = BluetoothAdapter.getDefaultAdapter();
        state = STATE_NONE;
        this.handler = handler;
    }

    /**
     * Set the current state of the chat connection
     *
     * @param state An integer defining the current connection state
     */
    private synchronized void setState(int state) {
        Logger.info(TAG, "Synchronized setState() " + getStateAsString(this.state) + " -> " + getStateAsString(state));
        this.state = state;

        // Give the new state to the Handler so the UI Activity can update
        handler.obtainMessage(MESSAGE_STATE_CHANGE, state, -1).sendToTarget();

    }

    /** @return The current connection state. */
    public synchronized int getState() {
        Logger.info(TAG, "Synchronized getState(), value : " + getStateAsString(state));
        return state;
    }

    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume()
     */
    public synchronized void start() {
        Logger.info(TAG, "Synchronized start()");

        // Cancel any thread attempting to make a connection
        if (connectThread != null) {
            connectThread.closeSocket();
            connectThread = null;
        }

        // Cancel any thread currently running a connection
        if (connectedThread != null) {
            connectedThread.closeSocket();
            connectedThread = null;
        }

        setState(STATE_LISTEN);

        // Start the thread to listen on a BluetoothServerSocket
        if (secureAcceptThread == null) {
            secureAcceptThread = new AcceptThread(true);
            secureAcceptThread.start();
        }
        if (insecureAcceptThread == null) {
            insecureAcceptThread = new AcceptThread(false);
            insecureAcceptThread.start();
        }
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     *
     * @param device The BluetoothDevice to connect
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    public synchronized void connect(BluetoothDevice device, boolean secure) {
        Logger.info(TAG, "Synchronized connect to: " + device);

        // Cancel any thread attempting to make a connection
        if (state == STATE_CONNECTING) {
            if (connectThread != null) {
                connectThread.closeSocket();
                connectThread = null;
            }
        }

        // Cancel any thread currently running a connection
        if (connectedThread != null) {
            connectedThread.closeSocket();
            connectedThread = null;
        }

        // Start the thread to connect with the given device
        connectThread = new ConnectThread(device, secure);
        connectThread.start();
        setState(STATE_CONNECTING);
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     *
     * @param socket     The BluetoothSocket on which the connection was made
     * @param device     The BluetoothDevice that has been connected
     * @param socketType
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device, final String socketType) {
        Logger.info(TAG, "Synchronized connected, Socket Type:" + socketType);

        // Cancel the thread that completed the connection
        if (connectThread != null) {
            connectThread.closeSocket();
            connectThread = null;
        }

        // Cancel any thread currently running a connection
        if (connectedThread != null) {
            connectedThread.closeSocket();
            connectedThread = null;
        }

        // Cancel the accept thread because we only want to connect to one device
        if (secureAcceptThread != null) {
            secureAcceptThread.closeSocket();
            secureAcceptThread = null;
        }
        if (insecureAcceptThread != null) {
            insecureAcceptThread.closeSocket();
            insecureAcceptThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        connectedThread = new ConnectedThread(socket, socketType);
        connectedThread.start();

        // Send the name of the connected device back to the UI Activity
        Message msg = handler.obtainMessage(MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(DEVICE_NAME, device.getName());
        msg.setData(bundle);
        handler.sendMessage(msg);

        setState(STATE_CONNECTED);
    }

    /** Stop all threads */
    public synchronized void stop() {
        Logger.info(TAG, "Synchronized stop()");

        if (connectThread != null) {
            connectThread.closeSocket();
            connectThread = null;
        }

        if (connectedThread != null) {
            connectedThread.closeSocket();
            connectedThread = null;
        }

        if (secureAcceptThread != null) {
            secureAcceptThread.closeSocket();
            secureAcceptThread = null;
        }

        if (insecureAcceptThread != null) {
            insecureAcceptThread.closeSocket();
            insecureAcceptThread = null;
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
            if (state != STATE_CONNECTED) {
                return;
            }
            r = connectedThread;
        }
        // Perform the write asynchronously
        r.write(out);
    }

    /** Indicate that the connection attempt failed and notify the UI Activity. */
    private void connectionFailed() {
        Logger.info(TAG, "connectionFailed()");

        // Send a failure message back to the Activity
        Message msg = handler.obtainMessage(MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(TOAST, "Unable to connect device");
        msg.setData(bundle);
        handler.sendMessage(msg);

        // Start the service over to restart listening mode
        BluetoothService.this.start();
    }

    /** Indicate that the connection was lost and notify the UI Activity. */
    private void connectionLost() {
        Logger.info(TAG, "connectionLost()");

        // Send a failure message back to the Activity
        Message msg = handler.obtainMessage(MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(TOAST, "Device connection was lost");
        msg.setData(bundle);
        handler.sendMessage(msg);

        // Start the service over to restart listening mode
        BluetoothService.this.start();
    }

    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until canceled).
     */
    private class AcceptThread extends Thread {
        private final BluetoothServerSocket serverSocket;
        private final String socketType;

        public AcceptThread(boolean secure) {
            Logger.debug(TAG, "AcceptThread constructor");

            BluetoothServerSocket tmp = null;
            socketType = secure ? SOCKET_TYPE_SECURE : SOCKET_TYPE_INSECURE;

            // Create a new listening server socket
            try {
                if (secure) {
                    tmp = adapter.listenUsingRfcommWithServiceRecord(NAME_SECURE, MY_UUID_SECURE);
                } else {
                    tmp = adapter.listenUsingInsecureRfcommWithServiceRecord(NAME_INSECURE, MY_UUID_INSECURE);
                }
            } catch (IOException e) {
                Logger.error(TAG, "Socket Type: " + socketType + "listen() failed", e);
            }
            serverSocket = tmp;
        }

        @Override
        public void run() {
            Logger.debug(TAG, "AcceptThread run() | SocketType:" + socketType);

            setName("ListeningThread" + socketType);

            BluetoothSocket socket = null;

            // Listen to the server socket if we're not connected
            while (state != STATE_CONNECTED) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = serverSocket.accept();

                } catch (IOException e) {
                    Logger.error(TAG, "Socket Type: " + socketType + "accept() failed", e);
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (BluetoothService.this) {
                        switch (state) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                // Situation normal. Start the connected thread.
                                connected(socket, socket.getRemoteDevice(), socketType);
                                break;

                            case STATE_NONE:
                            case STATE_CONNECTED:
                                // Either not ready or already connected. Terminate new socket.
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Logger.error(TAG, "Could not close unwanted socket", e);
                                }
                                break;

                        }
                    }
                }
            }
            Logger.debug(TAG, "END mAcceptThread, socket Type: " + socketType);

        }

        public void closeSocket() {
            Logger.debug(TAG, "AcceptThread closeSocket() | SocketType:" + socketType);

            try {
                serverSocket.close();
            } catch (IOException e) {
                Logger.error(TAG, "AcceptThread closeSocket() |Socket Type" + socketType + "close() of server failed", e);
            }
        }
    }

    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket socket;
        private final BluetoothDevice device;
        private final String socketType;

        public ConnectThread(BluetoothDevice device, boolean secure) {
            Logger.debug(TAG, "ConnectThread constructor");

            this.device = device;
            BluetoothSocket tmp = null;
            socketType = secure ? "Secure" : "Insecure";

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                if (secure) {
                    tmp = device.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
                } else {
                    tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID_INSECURE);
                }
            } catch (IOException e) {
                Logger.error(TAG, "Socket Type: " + socketType + "create() failed", e);
            }
            socket = tmp;
        }

        @Override
        public void run() {
            Logger.debug(TAG, "ConnectThread.run() | SocketType:" + socketType);
            setName("ConnectThread" + socketType);

            // Always cancel discovery because it will slow down a connection
            adapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                socket.connect();

            } catch (IOException e) {
                Logger.error(TAG, "ConnectThread.run " + e);
                // Close the socket
                closeSocket();
                connectionFailed();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothService.this) {
                connectThread = null;
            }

            // Start the connected thread
            connected(socket, device, socketType);
        }

        public void closeSocket() {
            Logger.debug(TAG, "ConnectThread closeSocket() | SocketType:" + socketType);

            try {
                socket.close();
            } catch (IOException e) {
                Logger.error(TAG, "close() of connect " + socketType + " socket failed", e);
            }
        }
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket socket;
        private final InputStream inStream;
        private final OutputStream outStream;

        public ConnectedThread(BluetoothSocket socket, String socketType) {
            Logger.debug(TAG, "ConnectedThread constructor");

            this.socket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Logger.error(TAG, "temp sockets not created", e);
            }

            inStream = tmpIn;
            outStream = tmpOut;
        }

        @Override
        public void run() {
            Logger.debug(TAG, "ConnectedThread.run()");

            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = inStream.read(buffer);

                    // Send the obtained bytes to the UI Activity
                    handler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();

                } catch (IOException e) {
                    Logger.error(TAG, "disconnected", e);
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
                outStream.write(buffer);

                // Share the sent message back to the UI Activity
                handler.obtainMessage(MESSAGE_WRITE, -1, -1, buffer).sendToTarget();

            } catch (IOException e) {
                Logger.error(TAG, "Exception during write", e);
            }
        }

        public void closeSocket() {
            try {
                socket.close();
            } catch (IOException e) {
                Logger.error(TAG, "close() of connect socket failed", e);
            }
        }
    }
}

