package org.es.network;

import static org.es.network.ExchangeProtos.Request.Code.NONE;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.es.network.ExchangeProtos.Request;
import org.es.network.ExchangeProtos.Request.Code;
import org.es.network.ExchangeProtos.Request.Type;
import org.es.network.ExchangeProtos.Response;

/**
 * Class that holds the utils to manage network messages
 * 
 * @author Cyril Leroux
 *
 */
public class MessageHelper {

	/**
	 * Build a request with both integer and string parameters.
	 * @param securityToken The security token that identify the user.
	 * @param type The request type.
	 * @param code The request code.
	 * @param extraCode The request extra code.
	 * @param intParam An integer parameter.
	 * @param stringParam A string parameter.
	 * @return The request if it had been initialized. Return null otherwise.
	 */
	private static Request buildRequest(final String securityToken, final Type type, final Code code, final Code extraCode, final int intParam, final String stringParam) {
		Request request = Request.newBuilder()
				.setSecurityToken(securityToken) // Add the security token
				.setType(type)
				.setCode(code)
				.setExtraCode(extraCode)
				.setIntParam(intParam)
				.setStringParam(stringParam)
				.build();

		if (request.isInitialized()) {
			return request;
		}
		return null;
	}

	/**
	 * Build a request with an integer parameter.
	 * @param securityToken The security token that identify the user.
	 * @param type The request type.
	 * @param code The request code.
	 * @param intParam An integer parameter.
	 * @return The request if it had been initialized. Return null otherwise.
	 */
	public static Request buildRequest(final String securityToken, final Type type, final Code code, final int intParam) {
		return buildRequest(securityToken, type, code, NONE, intParam, "");
	}

	/**
	 * Build a request with a string parameter.
	 * @param securityToken The security token that identify the user.
	 * @param type The request type.
	 * @param code The request code.
	 * @param extraCode The request extra code.
	 * @param stringParam A string parameter.
	 * @return The request if it had been initialized. Return null otherwise.
	 */
	public static Request buildRequest(final String securityToken, final Type type, final Code code, final Code extraCode, final String stringParam) {
		return buildRequest(securityToken, type, code, extraCode, 0, stringParam);
	}

	/**
	 * Build a request with a code and an extra code.
	 * @param securityToken The security token that identify the user.
	 * @param type The request type.
	 * @param code The request code.
	 * @param extraCode The request extra code.
	 * @return The request if it had been initialized. Return null otherwise.
	 */
	public static Request buildRequest(final String securityToken, final Type type, final Code code, final Code extraCode) {
		return buildRequest(securityToken, type, code, extraCode, 0, "");
	}

	/**
	 * Build a request with a code.
	 * @param securityToken The security token that identify the user.
	 * @param type The request type.
	 * @param code The request code.
	 * @return The request if it had been initialized. Return null otherwise.
	 */
	public static Request buildRequest(final String securityToken, final Type type, final Code code) {
		return buildRequest(securityToken, type, code, NONE, 0, "");
	}

	/**
	 * This function must be called from a background thread.
	 * Send a message through a Socket to a server and get the reply.
	 * 
	 * @param socket The socket on which to send the message.
	 * @param request Client request.
	 * @param soTimeOut socket's read timeout in milliseconds. Use 0 for no timeout.
	 * 
	 * @return The server reply.
	 * @throws IOException exception.
	 */
	public static Response sendRequest(Socket socket, Request request, final int soTimeOut) throws IOException {
		if (socket.isConnected()) {
			//create BAOS for protobuf
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			//mClientDetails is a protobuf message object, dump it to the BAOS
			request.writeDelimitedTo(baos);

			socket.setSoTimeout(soTimeOut);
			socket.getOutputStream().write(baos.toByteArray());
			socket.getOutputStream().flush();
			socket.shutdownOutput();

			return Response.parseDelimitedFrom(socket.getInputStream());
		}
		return null;
	}
}
