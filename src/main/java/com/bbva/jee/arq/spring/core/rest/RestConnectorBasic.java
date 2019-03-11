package com.bbva.jee.arq.spring.core.rest;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.interceptor.Fault;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
public class RestConnectorBasic extends AbstractRestConnector implements IProxyRestConnector {
	
	private static final Log LOG = LogFactory.getLog(RestConnectorBasic.class);
	
	private static final boolean DEFAULT_USE_PROXY = false;
	private static final boolean DEFAULT_FORCE_SYNC = false;
	
	public RestConnectorBasic () {
		type = RestConnectorType.BASIC;
		
		this.backend = backend;
	}
	
	//@Autowired
	/*public RestConnectorBasic (String backend) {
		this();
		this.backend = backend;
	}*/
	
	@Override
	public RestConnectorResponse doGet (String url, HashMap<String, String> params, HashMap<String, String> headers, Collection<String> obfuscationMask) {
		return doGet(url, params, headers, DEFAULT_USE_PROXY, obfuscationMask);
	}
	
	@Override
	public RestConnectorResponse doPost (String url, HashMap<String, String> params, HashMap<String, String> headers, String payload, Collection<String> obfuscationMask) {
		return doPost(url, params, headers, payload, DEFAULT_USE_PROXY, obfuscationMask);
	}
	
	@Override
	public RestConnectorResponse doPut (String url, HashMap<String, String> params, HashMap<String, String> headers, String payload, Collection<String> obfuscationMask) {
		return doPut(url, params, headers, payload, DEFAULT_USE_PROXY, obfuscationMask);
	}
	
	@Override
	public RestConnectorResponse doPatch (String url, HashMap<String, String> params, HashMap<String, String> headers, String payload, Collection<String> obfuscationMask) {
		return doPatch(url, params, headers, payload, DEFAULT_USE_PROXY, obfuscationMask);
	}
	
	@Override
	public RestConnectorResponse doDelete (String url, HashMap<String, String> params, HashMap<String, String> headers, Collection<String> obfuscationMask) {
		return doDelete(url, params, headers, DEFAULT_USE_PROXY, obfuscationMask);
	}
	
	@Override
	public RestConnectorResponse doGet (String url, HashMap<String, String> params, HashMap<String, String> headers, boolean useProxy) {
		return doGet(url, params, headers, useProxy, Collections.<String>emptyList());
	}
	
	@Override
	public RestConnectorResponse doGet (String url, HashMap<String, String> params, HashMap<String, String> headers, boolean useProxy, Collection<String> obfuscationMask) {
		
		if (url == null || url.isEmpty()) {
			try {
				throw new Exception("errorRestConnector");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			String fullUrl = buildUrl(url, params);
			HttpGet request = new HttpGet(fullUrl);
			return execute(fullUrl, headers, request, backend, DEFAULT_FORCE_SYNC, useProxy, HttpClientBuilder.create());
			
		} catch (Exception e) {
			LOG.error("[Rest Connector] Exception in Rest connector - GET method. Error: " + e);
			try {
				throw new Exception("errorRestConnector");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return null;
	}
	
	@Override
	public RestConnectorResponse doPost (String url, HashMap<String, String> params, HashMap<String, String> headers, String payload, boolean useProxy) {
		return doPost(url, params, headers, payload, useProxy, Collections.<String>emptyList());
	}
	
	@Override
	public RestConnectorResponse doPost (String url, HashMap<String, String> params, HashMap<String, String> headers, String payload, boolean useProxy, Collection<String> obfuscationMask) {
		
		if (url == null || url.isEmpty()) {
			try {
				throw new Exception("errorRestConnector");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			String fullUrl = buildUrl(url, params);
			HttpPost request = new HttpPost(fullUrl);
			request.setEntity(new StringEntity(payload, CHARSET));
			return execute(fullUrl, headers, request, backend, DEFAULT_FORCE_SYNC, useProxy, HttpClientBuilder.create());
			
		} catch (Exception e) {
			LOG.error("[Rest Connector] Exception in Rest connector - POST method. Error: " + e);
			try {
				throw new Exception("errorRestConnector");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return null;
	}
	
	@Override
	public RestConnectorResponse doPut (String url, HashMap<String, String> params, HashMap<String, String> headers, String payload, boolean useProxy) {
		return doPut(url, params, headers, payload, useProxy, Collections.<String>emptyList());
	}
	
	@Override
	public RestConnectorResponse doPut (String url, HashMap<String, String> params, HashMap<String, String> headers, String payload, boolean useProxy, Collection<String> obfuscationMask) {
		
		if (url == null || url.isEmpty()) {
			try {
				throw new Exception("errorRestConnector");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			String fullUrl = buildUrl(url, params);
			HttpPut request = new HttpPut(fullUrl);
			request.setEntity(new StringEntity(payload, CHARSET));
			return execute(fullUrl, headers, request, backend, DEFAULT_FORCE_SYNC, useProxy, HttpClientBuilder.create());
			
		} catch (Exception e) {
			LOG.error("[Rest Connector] Exception in Rest connector - PUT method. Error: " + e);
			try {
				throw new Exception("errorRestConnector");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return null;
	}
	
	@Override
	public RestConnectorResponse doPatch (String url, HashMap<String, String> params, HashMap<String, String> headers, String payload, boolean useProxy) {
		return doPatch(url, params, headers, payload, useProxy, Collections.<String>emptyList());
	}
	
	@Override
	public RestConnectorResponse doPatch (String url, HashMap<String, String> params, HashMap<String, String> headers, String payload, boolean useProxy, Collection<String> obfuscationMask) {
		
		if (url == null || url.isEmpty()) {
			try {
				throw new Exception("errorRestConnector");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			String fullUrl = buildUrl(url, params);
			HttpPatch request = new HttpPatch(fullUrl);
			request.setEntity(new StringEntity(payload, CHARSET));
			return execute(fullUrl, headers, request, backend, DEFAULT_FORCE_SYNC, useProxy, HttpClientBuilder.create());
			
		} catch (Exception e) {
			LOG.error("[Rest Connector] Exception in Rest connector - PATCH method. Error: " + e);
			try {
				throw new Exception("errorRestConnector");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return null;
	}
	
	@Override
	public RestConnectorResponse doDelete (String url, HashMap<String, String> params, HashMap<String, String> headers, boolean useProxy) {
		return doDelete(url, params, headers, useProxy, Collections.<String>emptyList());
	}
	
	@Override
	public RestConnectorResponse doDelete (String url, HashMap<String, String> params, HashMap<String, String> headers, boolean useProxy, Collection<String> obfuscationMask) {
		
		if (url == null || url.isEmpty()) {
			try {
				throw new Exception("errorRestConnector");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			String fullUrl = buildUrl(url, params);
			HttpDelete request = new HttpDelete(fullUrl);
			return execute(fullUrl, headers, request, backend, DEFAULT_FORCE_SYNC, useProxy, HttpClientBuilder.create());
			
		} catch (Exception e) {
			LOG.error("[Rest Connector] Exception in Rest connector - DELETE method. Error: " + e);
			try {
				throw new Exception("errorRestConnector");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return null;
	}
				
}
