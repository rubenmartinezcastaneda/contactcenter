package com.bbva.jee.arq.spring.core.rest;

import com.google.api.client.http.HttpHeaders;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;

import javax.ws.rs.core.MediaType;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RestConnectorResponse {
	
	private static final Log LOG = LogFactory.getLog(RestConnectorResponse.class);
	
	private Map<String, String> headers = null;
	private byte[] contentBytes = null;
	private String contentType = MediaType.APPLICATION_JSON + ";charset=UTF-8";
	private String charset = "UTF-8";
	private int statusCode = 200;
	private String responseBody = null;
	
	
	public RestConnectorResponse () {
		this.headers = new HashMap<String, String>();
	}
	
	
	public String getResponseBody () {
		return responseBody;
	}
	
	public void setResponseBody (String responseBody) {
		this.responseBody = responseBody;
	}
	
	public Map<String, String> getHeaders () {
		return this.headers;
	}
	
	public void setHeaders (Map<String, String> headers) {
		this.headers = headers;
	}
	
	public byte[] getContentBytes () {
		return this.contentBytes;
	}
	
	public void setContentBytes (byte[] contentBytes) {
		this.contentBytes = contentBytes;
	}
	
	public String getContentType () {
		return this.contentType;
	}
	
	public void setContentType (String contentType) {
		this.contentType = contentType;
	}
	
	public String getCharset () {
		return this.charset;
	}
	
	public void setCharset (String charset) {
		this.charset = charset;
	}
	
	public int getStatusCode () {
		return this.statusCode;
	}
	
	public void setStatusCode (int statusCode) {
		this.statusCode = statusCode;
	}
	
	public void setHeaderValue (String key, String value) {
		this.headers.put(key, value);
	}
	
	public String getHeaderValue (String key) {
		return (String) this.headers.get(key);
	}
	
	public void generateResponseBody () {
		if (this.contentBytes != null && this.contentBytes.length > 0 && this.charset != null) {
			try {
				this.responseBody = new String(this.contentBytes, this.charset);
			} catch (UnsupportedEncodingException e) {
				LOG.error("Error while getting response body: " + e);
			}
		}
		LOG.debug("Response body: " + responseBody);
	}
	
	@Override
	public String toString () {
		return "RestConnectorResponse [headers=" + headers + ", contentType=" + contentType + ", charset=" + charset + ", statusCode=" + statusCode + ", responseBody=" + responseBody + "]";
	}
	
	/**
	 * Fill RestConnectorResponse headers from specified apache headers array
	 *
	 * @param headers
	 */
	public synchronized void fillHeaders (Header[] headers) {
		try {
			for (Header header : headers) {
				this.headers.put(String.valueOf(header.getName()), String.valueOf(header.getValue()));
			}
			LOG.debug("Response headers: " + this.headers);
		} catch (Exception e) {
			LOG.error("Error while getting response headers: " + e);
		}
	}
	
	/**
	 * Fill RestConnectorResponse headers from specified google headers
	 *
	 * @param headers
	 */
	public synchronized void fillHeaders (HttpHeaders headers) {
		try {
			Set<Map.Entry<String, Object>> responseHeaders = headers.entrySet();
			for (Map.Entry header : responseHeaders) {
				this.headers.put(String.valueOf(header.getKey()), ((ArrayList) header.getValue()).get(0).toString());
			}
			LOG.debug("Response headers: " + this.headers);
		} catch (Exception e) {
			LOG.error("Error al obtener las cabeceras de la respuesta: " + e);
		}
	}
}