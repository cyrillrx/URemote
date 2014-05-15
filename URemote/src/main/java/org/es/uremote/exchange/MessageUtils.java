package org.es.uremote.exchange;

import org.es.uremote.device.ServerSetting;
import org.es.uremote.exchange.Message.Request;
import org.es.uremote.exchange.Message.Request.Code;
import org.es.uremote.exchange.Message.Request.Type;
import org.es.uremote.exchange.Message.Response;
import org.es.utils.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

import static org.es.uremote.exchange.Message.Request.Code.NONE;

/**
 * Class that holds the utils to manage network messages
 *
 * @author Cyril Leroux
 *         Created on 15/05/13.
 */
public class MessageUtils {

    private static final String TAG = "MessageUtils";

    /**
     * Build a request with both integer and string parameters.
     *
     * @param securityToken The security token that identify the user.
     * @param type          The request type.
     * @param code          The request code.
     * @param extraCode     The request extra code.
     * @param intExtra      An integer parameter.
     * @param stringExtra   A string parameter.
     * @return The request if it had been initialized. Return null otherwise.
     */
    private static Request buildRequest(final String securityToken, final Type type, final Code code, final Code extraCode, final int intExtra, final String stringExtra) {

        Request request = Request.newBuilder()
                .setSecurityToken(securityToken)
                .setType(type)
                .setCode(code)
                .setExtraCode(extraCode)
                .setIntExtra(intExtra)
                .setStringExtra(stringExtra)
                .build();

        if (request.isInitialized()) {
            return request;
        }
        return null;
    }

    /**
     * Build a request with an integer parameter.
     *
     * @param securityToken The security token that identify the user.
     * @param type          The request type.
     * @param code          The request code.
     * @param intExtra      An integer parameter.
     * @return The request if it had been initialized. Return null otherwise.
     */
    public static Request buildRequest(final String securityToken, final Type type, final Code code, final int intExtra) {
        return buildRequest(securityToken, type, code, NONE, intExtra, "");
    }

    /**
     * Build a request with a string parameter.
     *
     * @param securityToken The security token that identify the user.
     * @param type          The request type.
     * @param code          The request code.
     * @param extraCode     The request extra code.
     * @param stringExtra   A string parameter.
     * @return The request if it had been initialized. Return null otherwise.
     */
    public static Request buildRequest(final String securityToken, final Type type, final Code code, final Code extraCode, final String stringExtra) {
        return buildRequest(securityToken, type, code, extraCode, 0, stringExtra);
    }

    /**
     * Build a request with a code and an extra code.
     *
     * @param securityToken The security token that identify the user.
     * @param type          The request type.
     * @param code          The request code.
     * @param extraCode     The request extra code.
     * @return The request if it had been initialized. Return null otherwise.
     */
    public static Request buildRequest(final String securityToken, final Type type, final Code code, final Code extraCode) {
        return buildRequest(securityToken, type, code, extraCode, 0, "");
    }

    /**
     * Build a request with a code.
     *
     * @param securityToken The security token that identify the user.
     * @param type          The request type.
     * @param code          The request code.
     * @return The request if it had been initialized. Return null otherwise.
     */
    public static Request buildRequest(final String securityToken, final Type type, final Code code) {
        return buildRequest(securityToken, type, code, NONE, 0, "");
    }

    /**
     * This function must be called from a background thread.
     * Send a request to a device and wait for a reply.
     *
     * @param request Client request.
     * @param device  The device towards which to send the request.
     * @return The server reply.
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
     * @param socket  The socket on which to send the message.
     * @param request Client request.
     * @return The server reply.
     * @throws IOException exception.
     */
    private static Response sendRequest(Socket socket, Request request) throws IOException {
        if (socket.isConnected()) {
            //create BAOS for protobuf
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            //mClientDetails is a protobuf message object, dump it to the BAOS
            request.writeDelimitedTo(baos);

            socket.getOutputStream().write(baos.toByteArray());
            socket.getOutputStream().flush();
            socket.shutdownOutput();

            return Response.parseDelimitedFrom(socket.getInputStream());
        }
        return null;
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
