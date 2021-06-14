/*******************************************************************************
 * MIT License
 *
 * Copyright (c) 2016 Valentin 'ThisIsMac' Marchaud
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
package fr.vmarchaud.mineweb.common;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import fr.vmarchaud.mineweb.common.interactor.requests.AskRequest;
import fr.vmarchaud.mineweb.common.interactor.requests.HandshakeRequest;
import fr.vmarchaud.mineweb.common.interactor.responses.AskResponse;
import fr.vmarchaud.mineweb.common.interactor.responses.HandshakeResponse;
import fr.vmarchaud.mineweb.utils.CryptoUtils;
import fr.vmarchaud.mineweb.utils.http.HttpResponseBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

public class RequestHandler {
	
	private ICore api;
	private SecretKeySpec key;
	private boolean	debug;
	
	/**
	 * Construct an instance of the RequestHandler that will validate and respond to inbound request
	 * @param api: instance of the API
	 */
	public RequestHandler(ICore api) {
		this.api = api;
		if (api.config().getSecretkey() != null)
			this.key = new SecretKeySpec(api.config().getSecretkey().getBytes(), "AES");
		
		// register handshake endpoint
		api.getHTTPRouter().post("/handshake", (request) -> this.handleHandshake(request.getRequest()));
		api.getHTTPRouter().post("/ask", (request) -> this.handle(request.getRequest()));
		debug = /*System.getenv("DEBUG") != null && System.getenv("DEBUG").equals("true")*/true;
	}
	
	/**
	 * Refresh the secretkey used to cipher and decipher request
	 * @param secretKey: String containing the 32 bytes key
	 */
	public void	refreshKey(String secretKey) {
		this.key = new SecretKeySpec(secretKey.getBytes(), "AES");
	}
	
	/**
	 * Handle special request from CMS (handshake)
	 * Retrieve secret-key -> save it in configuration -> send it back as succesfull handshake.
	 * 
	 * @param request: HttpRequest that contains data required for handshake
	 * @return httpResponse: Response object to send back to the client
	 * @throws Exception 
	 */
	public FullHttpResponse handleHandshake(FullHttpRequest httpRequest) {
		ByteBuf buf = httpRequest.content();
		String content = buf.toString(buf.readerIndex(), buf.readableBytes(), Charset.forName("UTF-8"));
		HandshakeRequest handshake = api.gson().fromJson(content, HandshakeRequest.class);
		
		api.logger().info(String.format("New Handshake id: %s (%s, %s)",
				handshake.getId(), handshake.getSecretKey(), handshake.getDomain()));
		
		if (!handshake.isValid()) {
			api.logger().info(String.format("Handshake failed id: %s (reason: invalid params)", handshake.getId()));
			return new HttpResponseBuilder().code(HttpResponseStatus.BAD_REQUEST).build();
		}
		
		if (api.config().getSecretkey() != null) {
			api.logger().info(String.format("Handshake failed id: %s (reason: already linked)", handshake.getId()));
			return new HttpResponseBuilder().code(HttpResponseStatus.FORBIDDEN).build();
		}
		try {
			HandshakeResponse response = new HandshakeResponse();
			
			// save all the stuff inside the configuration
			String secret = handshake.getSecretKey();
			api.config().setSecretkey(secret);
			api.config().setDomain(handshake.getDomain());
			api.config().save(api);
			response.setMsg("Successfully retrieved secret key, now ready !");
			response.setStatus(true);
			api.logger().info(String.format("Handshake request %s has been successfully valided (secret: %s)", handshake.getId(), secret));
			this.refreshKey(secret);
			
			return new HttpResponseBuilder().code(HttpResponseStatus.OK).json(api.gson().toJson(response)).build();
		} catch (Exception e) {
			if (debug) e.printStackTrace();
			return new HttpResponseBuilder().code(HttpResponseStatus.INTERNAL_SERVER_ERROR).json(api.gson().toJson(e.getMessage())).build();
		}
	}
	
	/**
	 * Handle general request from CMS
	 * 
	 * @param httpRequest: Request object that contains ciphered CMS's request
	 * @return httpResponse: Response object to send back to the client
	 */
	public FullHttpResponse handle(FullHttpRequest httpRequest) {
		if (api.config().getSecretkey() == null) {
			api.logger().severe("Secret key isnt defined, please setup like wrote in the mineweb documentation.");
			return new HttpResponseBuilder().code(HttpResponseStatus.NOT_IMPLEMENTED).build();
		}
		
		ByteBuf buf = httpRequest.content();
		String content = buf.toString(buf.readerIndex(), buf.readableBytes(), Charset.forName("UTF-8"));
		
		List<Command> requests;
		AskRequest request;
		JsonArray response = new JsonArray();
		Type token = new TypeToken<List<Command>>(){}.getType();
		
		try {
			// parse json to map
			request = api.gson().fromJson(content, AskRequest.class);
			// if in debug, request is done in plaintext
			String tmp = debug ? request.getSigned() : CryptoUtils.decryptAES(request.getSigned(), this.key, request.getIv());
			JsonReader reader = new JsonReader(new StringReader(tmp));
			reader.setLenient(true);
			requests = api.gson().fromJson(reader, token);
		} catch (Exception e) {
			api.logger().severe(String.format("Cant decipher/parse a request : %s", e.getMessage()));
			if (debug) e.printStackTrace();
			return HttpResponseBuilder.status(HttpResponseStatus.INTERNAL_SERVER_ERROR);
		}
		
		for(Command command : requests) {
			IMethod 	method = api.getMethods().get(command.getName());
			Object[] 	inputs = command.getArgs();
			
			// if we didnt found the method just continue
			if (method == null) {
				JsonObject res = new JsonObject();
				res.addProperty("name", command.getName());
				res.addProperty("response", "NOT_FOUND");
				response.add(res);
				continue ;
			}
			
			// verify if the params size are same as requested by the method handler
			MethodHandler annot = method.getClass().getDeclaredAnnotation(MethodHandler.class);
			if (annot == null) {
				JsonObject res = new JsonObject();
				res.addProperty("name", command.getName());
				res.addProperty("response", "INVALID_IMPLEMENTED_METHOD");
				response.add(res);
				continue ;
			}
			
			if (annot.inputs() != inputs.length) {
				JsonObject res = new JsonObject();
				res.addProperty("name", command.getName());
				res.addProperty("response", "BAD_REQUEST_ARGS_LENGTH");
				response.add(res);
				continue ;
			}
			
			// verify class type of input
			if (annot.inputs() > 0) {
				boolean valid = true;
				for(int i = 0; i < annot.types().length; i++) {
					if (debug) {
						api.logger().fine("Comparing input " + inputs[i] + " of class " 
								+ inputs[i].getClass().getName() + " to class " + annot.types()[i].getName());
					}
					if (!inputs[i].getClass().getName().equals(annot.types()[i].getName())) {
						valid = false;
						break ;
					}
				}
				if (!valid) {
					JsonObject res = new JsonObject();
					res.addProperty("name", command.getName());
					res.addProperty("response", "BAD_REQUEST_ARGS_TYPE");
					response.add(res);
					continue; 
				}
			}
			// execute the method and put the result into the response
			Object output = method.execute(api, inputs);
			JsonObject res = new JsonObject();
			res.addProperty("name", command.getName());
			res.add("response", api.gson().toJsonTree(output));
			response.add(res);
		}
		
		api.logger().fine(String.format("request %s : %s", httpRequest.hashCode(), api.gson().toJson(requests)));
		api.logger().fine(String.format("response %s : %s", httpRequest.hashCode() , api.gson().toJson(response)));
		
		try {
			// try to cipher the data and send it
			AskResponse askResponse = new AskResponse();
			String json = api.gson().toJson(response);
			// dont cipher in debug mode
			askResponse.setSigned(debug ? json : CryptoUtils.encryptAES(json, key, request.getIv()));
			askResponse.setIv(request.getIv());
			return new HttpResponseBuilder().json(api.gson().toJson(askResponse)).code(HttpResponseStatus.OK).build();
		} catch (Exception e) {
			api.logger().severe(String.format("Cant cipher/serialize a response : %s", e.getMessage()));
			return HttpResponseBuilder.status(HttpResponseStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
