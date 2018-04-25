package com.cyrillrx.uremote.common.device;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.cyrillrx.logger.Logger;

/**
 * Parcelable class that holds network device data (ip, mac address, etc.).
 *
 * @author Cyril Leroux
 *         Created on 19/05/13.
 */
public class NetworkDevice extends ConnectedDevice implements Parcelable {

    private static final String TAG = NetworkDevice.class.getSimpleName();

    public static final String FILENAME = "serverConfig.xml";

    /** CREATOR is a required attribute to create an instance of a class that implements Parcelable */
    public static final Parcelable.Creator<NetworkDevice> CREATOR = new Parcelable.Creator<NetworkDevice>() {
        @Override
        public NetworkDevice createFromParcel(Parcel src) { return new NetworkDevice(src); }

        @Override
        public NetworkDevice[] newArray(int size) { return new NetworkDevice[size]; }
    };

    private String localHost;
    private int localPort;
    private String broadcast;
    private String remoteHost;
    private int remotePort;
    private String macAddress;
    private ConnectionType connectionType;

    private NetworkDevice() { }

    /** @param src  */
    public NetworkDevice(final Parcel src) {
        name = src.readString();
        localHost = src.readString();
        localPort = src.readInt();
        broadcast = src.readString();
        remoteHost = src.readString();
        remotePort = src.readInt();
        macAddress = src.readString();
        connectionTimeout = src.readInt();
        readTimeout = src.readInt();
        securityToken = src.readString();
        connectionType = ConnectionType.valueOf(src.readString());
    }

    /**
     * Update the server with the object passed.
     *
     * @param server The server updated data.
     */
    public void update(final NetworkDevice server) {
        name = server.getName();
        localHost = server.getLocalHost();
        localPort = server.getLocalPort();
        broadcast = server.getBroadcast();
        remoteHost = server.getRemoteHost();
        remotePort = server.getRemotePort();
        macAddress = server.getMacAddress();
        connectionTimeout = server.getConnectionTimeout();
        readTimeout = server.getReadTimeout();
        securityToken = server.getSecurityToken();
        connectionType = server.getConnectionType();
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel destination, int flags) {
        destination.writeString(name);
        destination.writeString(localHost);
        destination.writeInt(localPort);
        destination.writeString(broadcast);
        destination.writeString(remoteHost);
        destination.writeInt(remotePort);
        destination.writeString(macAddress);
        destination.writeInt(connectionTimeout);
        destination.writeInt(readTimeout);
        destination.writeString(securityToken);
        destination.writeString(connectionType.toString());
    }

    /**
     * @param context
     * @return True is the server is in the same network than the device.
     */
    public boolean isLocal(Context context) {
        // TODO define when local and remote => User defined
        final WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiMgr.isWifiEnabled();
    }

    @Override
    public String toString() {
        return (connectionType == ConnectionType.LOCAL) ? toStringLocal() : toStringRemote();
    }

    /** @return Concatenation of host and port of the local server. */
    public String toStringLocal() { return localHost + ":" + localPort; }

    /** @return Concatenation of host and port of the remote server. */
    public String toStringRemote() { return remoteHost + ":" + remotePort; }

    /** @return The ip address of the local server. */
    public String getLocalHost() { return localHost; }

    public void setLocalHost(String localHost) { this.localHost = localHost; }

    /** @return The port of the local server. */
    public int getLocalPort() { return localPort; }

    /** @return The broadcast address. */
    public String getBroadcast() { return broadcast; }

    /** @return The ip address of the remote server. */
    public String getRemoteHost() { return remoteHost; }

    /** @return The port of the remote server. */
    public int getRemotePort() { return remotePort; }

    /** @return The mac address of the server. */
    public String getMacAddress() { return macAddress; }

    /** @return The type of connection (remote or local). */
    public ConnectionType getConnectionType() { return connectionType; }

    /**
     * The type of connection.
     */
    public enum ConnectionType {
        /** The server IS in the same network than the device. It must be accessed locally. */
        LOCAL,
        /** The server IS NOT in the same network than the device. It must be accessed remotely. */
        REMOTE
    }

    /** @return An instance of ServerSetting.Builder. */
    public static Builder newBuilder() { return new Builder(); }

    /**
     * Class that holds server connection data.
     *
     * @author Cyril Leroux
     *         Created on 03/06/13.
     */
    // TODO Move a part of the builder in ConnectedDevice class
    public static class Builder {

        private static String DEFAULT_NAME = "";
        private static String DEFAULT_LOCAL_HOST = "";
        private static int DEFAULT_PORT = 0;
        private static String DEFAULT_BROADCAST = "";
        private static String DEFAULT_REMOTE_HOST = "";
        private static String DEFAULT_MAC_ADDRESS = "";
        private static ConnectionType DEFAULT_CONNECTION = ConnectionType.LOCAL;
        private static int DEFAULT_CONNECTION_TIMEOUT = 500;
        private static int DEFAULT_READ_TIMEOUT = 500;
        private static String DEFAULT_SECURITY_TOKEN = "";

        NetworkDevice device;

        private Builder() {
            device = new NetworkDevice();
            clear();
        }

        /** Reset builder */
        public void clear() {
            device.name = DEFAULT_NAME;
            device.localHost = DEFAULT_LOCAL_HOST;
            device.localPort = DEFAULT_PORT;
            device.broadcast = DEFAULT_BROADCAST;
            device.remoteHost = DEFAULT_REMOTE_HOST;
            device.remotePort = DEFAULT_PORT;
            device.macAddress = DEFAULT_MAC_ADDRESS;
            device.connectionType = DEFAULT_CONNECTION;
            device.connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
            device.readTimeout = DEFAULT_READ_TIMEOUT;
            device.securityToken = DEFAULT_SECURITY_TOKEN;
        }

        /**
         * @return A fully loaded {@link NetworkDevice} object.
         * @throws Exception if the builder has not all the data to build the object.
         */
        public NetworkDevice build() throws Exception {

            boolean error = false;
            boolean warning = false;

            StringBuilder sb = new StringBuilder();

            if (TextUtils.isEmpty(device.name)) {
                error = true;
                sb.append("- Name is null or empty.\n");
            }

            if (TextUtils.isEmpty(device.localHost)) {
                error = true;
                sb.append("- Localhost is empty.\n");
            }

            if (device.localPort == 0) {
                error = true;
                sb.append("- Local port is 0.\n");
            }

            if (TextUtils.isEmpty(device.broadcast)) {
                warning = true;
                sb.append("- Broadcast is empty.\n");
            }

            if (TextUtils.isEmpty(device.remoteHost)) {
                warning = true;
                sb.append("- Remote host is empty.\n");
            }

            if (device.remotePort == 0) {
                warning = true;
                sb.append("- Remote port is 0.\n");
            }

            if (TextUtils.isEmpty(device.macAddress)) {
                warning = true;
                sb.append("- Mac address is empty.\n");
            }

            if (error) {
                throw new Exception("Server creation has failed:\n" + sb.toString());
            }

            if (warning) {
                Logger.warning(TAG, "Server creation succeeded with warnings:\n" + sb.toString());
            } else {
                Logger.warning(TAG, "Server creation succeeded.");
            }

            return device;
        }

        public Builder setConnectionType(ConnectionType type) {
            device.connectionType = type;
            return this;
        }

        public Builder setName(final String name) {
            device.name = name;
            return this;
        }

        public Builder setLocalHost(final String ipAddress) {
            device.localHost = ipAddress;
            return this;
        }

        public Builder setLocalPort(final int port) {
            device.localPort = port;
            return this;
        }

        public Builder setBroadcast(final String broadcastAddress) {
            device.broadcast = broadcastAddress;
            return this;
        }

        public Builder setRemoteHost(final String ipAddress) {
            device.remoteHost = ipAddress;
            return this;
        }

        public Builder setRemotePort(final int port) {
            device.remotePort = port;
            return this;
        }

        public Builder setMacAddress(final String macAddress) {
            device.macAddress = macAddress;
            return this;
        }

        /**
         * If the connection with the remote server is not established
         * within this timeout, it is dismissed.
         */
        public Builder setConnectionTimeout(final int timeout) {
            device.connectionTimeout = timeout;
            return this;
        }

        public Builder setReadTimeout(final int timeout) {
            device.readTimeout = timeout;
            return this;
        }

        public Builder setSecurityToken(final String securityToken) {
            device.securityToken = securityToken;
            return this;
        }
    }
}