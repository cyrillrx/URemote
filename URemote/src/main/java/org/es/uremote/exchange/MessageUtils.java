package org.es.uremote.exchange;

import org.es.uremote.device.ServerSetting;
import org.es.uremote.exchange.Message.Request;
import org.es.uremote.exchange.Message.Response;
import org.es.utils.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

/**
 * Class that holds the utils to manage network messages
 *
 * @author Cyril Leroux
 *         Created on 15/05/13.
 */
public class MessageUtils {

    private static final String TAG = "MessageUtils";

    /**
     * This function must be called from a background thread.
     * Send a request to a device and wait for a reply.
     *
     * @param request Client request.
     * @param device The device towards which to send the request.
     * @return The server reply.
     *
     * @throws IOException exception.
     */
    public static Response sendRequest(final Request request, final ServerSetting device) {

        String errorMessage = "";

        if (device == null) {
            errorMessage = "Connected device is null. Nowhere to send data";

        } else {
            Socket socket = null;
            try {
                socket = connectToRemoteSocket(device);
                if (socket != null && socket.isConnected()) {
                    return MessageUtils.sendRequest(socket, request);
                }
                errorMessage = "Socket null or not connected";

            } catch (SocketTimeoutException e) {
                errorMessage = "Can't reach remote device (bad config or server down).";
                Log.warning(TAG, "Can't reach remote device (" + e.getMessage() + ")");

            } catch (Exception e) {
                errorMessage = "Exception - " + e.getMessage();

            } finally {
                closeSocket(socket);
            }
        }

        return Response.newBuilder()
                .setRequestType(request.getType())
                .setRequestCode(request.getCode())
                .setReturnCode(Response.ReturnCode.RC_ERROR)
                .setMessage(errorMessage)
                .build();
    }

    /**
     * Creates the socket, connects it to the server then returns it.
     *
     * @param server The object that holds server connection settings.
     * @return The socket on which to send the message.
     *
     * @throws IOException exception
     */
    private static Socket connectToRemoteSocket(ServerSetting server) throws IOException {

        final String host = server.getLocalHost();
        final int port = server.getLocalPort();
        final SocketAddress socketAddress = new InetSocketAddress(host, port);

        Socket socket = new Socket();
        socket.setSoTimeout(server.getReadTimeout());
        socket.connect(socketAddress, server.getConnectionTimeout());

        return socket;
    }

    /**
     * This function must be called from a background thread.
     * Send a message through a Socket to a server and get the reply.
     *
     * @param socket The socket on which to send the message.
     * @param request Client request.
     * @return The server reply.
     *
     * @throws IOException exception.
     */
    private static Response sendRequest(Socket socket, Request request) throws IOException {
        if (!socket.isConnected()) {
            return null;
        }

        //create BAOS for protobuf
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        //mClientDetails is a protobuf message object, dump it to the BAOS
        request.writeDelimitedTo(baos);

        socket.getOutputStream().write(baos.toByteArray());
        socket.getOutputStream().flush();
        socket.shutdownOutput();

        return Response.parseDelimitedFrom(socket.getInputStream());
    }

    /**
     * Closes socket IOs then close the socket.
     */
    private static void closeSocket(Socket socket) {
        if (socket == null) {
            Log.warning(TAG, "#closeSocketIO - Socket is null.");
            return;
        }

        try {
            socket.shutdownInput();
        } catch (IOException e) {
            Log.warning(TAG, "#closeSocketIO - On socket.shutdownInput() : " + e);
        }
        try {
            socket.shutdownOutput();
        } catch (IOException e) {
            Log.warning(TAG, "#closeSocketIO - On socket.shutdownOutput() : " + e);
        }
        try {
            socket.close();
        } catch (IOException e) {
            Log.warning(TAG, "#closeSocketIO - On socket.close() : " + e);
        }
    }
}
