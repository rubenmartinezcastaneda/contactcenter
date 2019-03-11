package com.bbva.jee.arq.spring.core.rest;

import java.util.Collection;
import java.util.HashMap;


public interface IProxyRestConnector extends IRestConnector {
	
	/**
	 * Do GET request to specified url and params adding headers. Proxy use can be specified to make the request.
	 *
	 * @param url
	 * @param params
	 * @param headers
	 * @param useProxy
	 * @return RestConnectorResponse
	 */
	public RestConnectorResponse doGet (String url, HashMap<String, String> params, HashMap<String, String> headers, boolean useProxy);
	
	/**
	 * Do GET request to specified url and params adding headers. Proxy use can be specified to make the request.
	 * Fields included in obfuscationMask will appear obfuscated in audit.
	 *
	 * @param url
	 * @param params
	 * @param headers
	 * @param useProxy
	 * @param obfuscationMask
	 * @return
	 */
	public RestConnectorResponse doGet (String url, HashMap<String, String> params, HashMap<String, String> headers, boolean useProxy, Collection<String> obfuscationMask);
	
	/**
	 * Do POST request to specified url and params adding headers. Proxy use can be specified to make the request.
	 *
	 * @param url
	 * @param params
	 * @param headers
	 * @param payload
	 * @param useProxy
	 * @return RestConnectorResponse
	 */
	public RestConnectorResponse doPost (String url, HashMap<String, String> params, HashMap<String, String> headers, String payload, boolean useProxy);
	
	/**
	 * Do POST request to specified url and params adding headers. Proxy use can be specified to make the request.
	 * Fields included in obfuscationMask will appear obfuscated in audit.
	 *
	 * @param url
	 * @param params
	 * @param headers
	 * @param payload
	 * @param useProxy
	 * @param obfuscationMask
	 * @return
	 */
	public RestConnectorResponse doPost (String url, HashMap<String, String> params, HashMap<String, String> headers, String payload, boolean useProxy, Collection<String> obfuscationMask);
	
	/**
	 * Do PUT request to specified url and params adding headers. Proxy use can be specified to make the request.
	 *
	 * @param url
	 * @param params
	 * @param headers
	 * @param payload
	 * @param useProxy
	 * @return RestConnectorResponse
	 */
	public RestConnectorResponse doPut (String url, HashMap<String, String> params, HashMap<String, String> headers, String payload, boolean useProxy);
	
	/**
	 * Do PUT request to specified url and params adding headers. Proxy use can be specified to make the request.
	 * Fields included in obfuscationMask will appear obfuscated in audit.
	 *
	 * @param url
	 * @param params
	 * @param headers
	 * @param payload
	 * @param useProxy
	 * @param obfuscationMask
	 * @return
	 */
	public RestConnectorResponse doPut (String url, HashMap<String, String> params, HashMap<String, String> headers, String payload, boolean useProxy, Collection<String> obfuscationMask);
	
	/**
	 * Do PATCH request to specified url and params adding headers. Proxy use can be specified to make the request.
	 *
	 * @param url
	 * @param params
	 * @param headers
	 * @param payload
	 * @param useProxy
	 * @return
	 */
	public RestConnectorResponse doPatch (String url, HashMap<String, String> params, HashMap<String, String> headers, String payload, boolean useProxy);
	
	/**
	 * Do PATCH request to specified url and params adding headers. Proxy use can be specified to make the request.
	 * Fields included in obfuscationMask will appear obfuscated in audit.
	 *
	 * @param url
	 * @param params
	 * @param headers
	 * @param payload
	 * @param useProxy
	 * @param obfuscationMask
	 * @return
	 */
	public RestConnectorResponse doPatch (String url, HashMap<String, String> params, HashMap<String, String> headers, String payload, boolean useProxy, Collection<String> obfuscationMask);
	
	/**
	 * Do DELETE request to specified url and params adding headers. Proxy use can be specified to make the request.
	 *
	 * @param url
	 * @param params
	 * @param headers
	 * @param useProxy
	 * @return RestConnectorResponse
	 */
	public RestConnectorResponse doDelete (String url, HashMap<String, String> params, HashMap<String, String> headers, boolean useProxy);
	
	/**
	 * Do DELETE request to specified url and params adding headers. Proxy use can be specified to make the request.
	 * Fields included in obfuscationMask will appear obfuscated in audit.
	 *
	 * @param url
	 * @param params
	 * @param headers
	 * @param useProxy
	 * @param obfuscationMask
	 * @return
	 */
	public RestConnectorResponse doDelete (String url, HashMap<String, String> params, HashMap<String, String> headers, boolean useProxy, Collection<String> obfuscationMask);
}
