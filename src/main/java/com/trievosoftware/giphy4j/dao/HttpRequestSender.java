/*
 * The MIT License
 *
 * Copyright (c) 2019 Trievo, LLC. https://trievosoftware.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 *
 */

package com.trievosoftware.giphy4j.dao;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.trievosoftware.giphy4j.Giphy4JConstants;
import com.trievosoftware.giphy4j.http.Request;
import com.trievosoftware.giphy4j.http.Response;
import org.apache.commons.io.IOUtils;

/**
 * This class provides methods to send request to the web.
 * 
 * @author Mark Tripoli
 */
public class HttpRequestSender implements RequestSender {

    @Override
    public Response sendRequest(Request request) throws IOException {

	HttpURLConnection connection = createConnection(request);

		return send(connection);
    }

    private HttpURLConnection createConnection(Request request) throws IOException {
	URL url = new URL(request.getUrl());
	HttpURLConnection connection = (HttpURLConnection) url.openConnection();

	connection.setConnectTimeout(Giphy4JConstants.TIMEOUT);
	connection.setReadTimeout(Giphy4JConstants.TIMEOUT);

	connection.setRequestMethod(Giphy4JConstants.SEND_METHOD);

	return connection;
    }

    private Response send(HttpURLConnection connection) throws IOException {
	connection.connect();

	if (connection.getResponseCode() != 200) {
	    throw new IOException("Bad response! Code: " + connection.getResponseCode());
	}

	Map<String, String> headers = new HashMap<>();
	for (String key : connection.getHeaderFields().keySet()) {
	    headers.put(key, connection.getHeaderFields().get(key).get(0));
	}

	String body;

		try (InputStream inputStream = connection.getInputStream()) {

			String encoding = connection.getContentEncoding();
			encoding = encoding == null ? Giphy4JConstants.ENCODING : encoding;

			body = IOUtils.toString(inputStream, encoding);
		} catch (IOException e) {
			throw new IOException(e);
		}

	if (body == null) {
	    throw new IOException("Unparseable response body! \n {" + body + "}");
	}

		return new Response(headers, body);
    }

}
