package com.bbva.jee.arq.spring.core.rest;

import java.util.Collection;
import java.util.HashMap;

public interface IRestConnector {
	
	/**
	 * Returns configured backend
	 *
	 * @return
	 */
	public String getBackend ();
	
	/**
	 * Do GET request to specified url and params adding headers.
	 *
	 * @param url
	 * @param params
	 * @param headers
	 * @return RestConnectorResponse
	 */
	public RestConnectorResponse doGet (String url, HashMap<String, String> params, HashMap<String, String> headers);
	
	/**
	 * Do GET request to specified url and params adding headers.
	 * Fields included in obfuscationMask will appear obfuscated in audit.
	 *
	 * @param url
	 * @param params
	 * @param headers
	 * @param obfuscationMask
	 * @return
	 */
	public RestConnectorResponse doGet (String url, HashMap<String, String> params, HashMap<String, String> headers, Collection<String> obfuscationMask);
	
	/**
	 * Do POST request to specified url and params adding headers.
	 *
	 * @param url
	 * @param params
	 * @param headers
	 * @param payload
	 * @return RestConnectorResponse
	 */
	public RestConnectorResponse doPost (String url, HashMap<String, String> params, HashMap<String, String> headers, String payload);
	
	/**
	 * Do POST request to specified url and params adding headers.
	 * Fields included in obfuscationMask will appear obfuscated in audit.
	 *
	 * @param url
	 * @param params
	 * @param headers
	 * @param payload
	 * @param obfuscationMask
	 * @return
	 */
	public RestConnectorResponse doPost (String url, HashMap<String, String> params, HashMap<String, String> headers, String payload, Collection<String> obfuscationMask);
	
	/**
	 * Do PUT request to specified url and params adding headers.
	 *
	 * @param url
	 * @param params
	 * @param headers
	 * @param payload
	 * @return RestConnectorResponse
	 */
	public RestConnectorResponse doPut (String url, HashMap<String, String> params, HashMap<String, String> headers, String payload);
	
	/**
	 * Do PUT request to specified url and params adding headers.
	 * Fields included in obfuscationMask will appear obfuscated in audit.
	 *
	 * @param url
	 * @param params
	 * @param headers
	 * @param payload
	 * @param obfuscationMask
	 * @return
	 */
	public RestConnectorResponse doPut (String url, HashMap<String, String> params, HashMap<String, String> headers, String payload, Collection<String> obfuscationMask);
	
	/**
	 * Do PATCH request to specified url and params adding headers.
	 *
	 * @param url
	 * @param params
	 * @param headers
	 * @param payload
	 * @return
	 */
	public RestConnectorResponse doPatch (String url, HashMap<String, String> params, HashMap<String, String> headers, String payload);
	
	/**
	 * Do PATCH request to specified url and params adding headers.
	 * Fields included in obfuscationMask will appear obfuscated in audit.
	 *
	 * @param url
	 * @param params
	 * @param headers
	 * @param payload
	 * @param obfuscationMask
	 * @return
	 */
	public RestConnectorResponse doPatch (String url, HashMap<String, String> params, HashMap<String, String> headers, String payload, Collection<String> obfuscationMask);
	
	/**
	 * Do DELETE request to specified url and params adding headers.
	 *
	 * @param url
	 * @param params
	 * @param headers
	 * @return RestConnectorResponse
	 */
	public RestConnectorResponse doDelete (String url, HashMap<String, String> params, HashMap<String, String> headers);
	
	/**
	 * Do DELETE request to specified url and params adding headers.
	 * Fields included in obfuscationMask will appear obfuscated in audit.
	 *
	 * @param url
	 * @param params
	 * @param headers
	 * @param obfuscationMask
	 * @return
	 */
	public RestConnectorResponse doDelete (String url, HashMap<String, String> params, HashMap<String, String> headers, Collection<String> obfuscationMask);
	
	/**
	 * Do DELETE request to specified url and params adding headers and payload.
	 *
	 * @param url
	 * @param params
	 * @param headers
	 * @param payload
	 * @return
	 */
	public RestConnectorResponse doDelete (String url, HashMap<String, String> params, HashMap<String, String> headers, String payload);
}
