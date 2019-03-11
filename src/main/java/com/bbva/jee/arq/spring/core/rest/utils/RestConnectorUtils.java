package com.bbva.jee.arq.spring.core.rest.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bbva.jee.arq.spring.core.properties.MisPropiedades;

@Component
public class RestConnectorUtils {
	
	private static final Log LOG = LogFactory.getLog(RestConnectorUtils.class);
	
	// async header and value
	private static final String PROPERTY_HEADER_PREFER = "prefer";
	private static final String VALUE_HEADER_PREFER_RESPOND_ASYNC = "respond-async";
	
	// Timeouts properties
	private static final Double CONNECT_TIMEOUT_DEFAULT = 5000d;
	private static final Double READ_TIMEOUT_DEFAULT = 5000d;
	private static final String PROPERTY_READ_TIMEOUT = "servicing.connector.rest.timeout.read";
	private static final String PROPERTY_CONNECT_TIMEOUT = "servicing.connector.rest.timeout.connect";
	private static final String PREFIX_PROPERTY_TIMEOUT_BACKEND = "servicing.connector.rest.";
	private static final String SUFIX_PROPERTY_READ_TIMEOUT_BACKEND = ".read.timeout";
	private static final String SUFIX_PROPERTY_READ_TIMEOUT_BACKEND_ASYNC = ".read.timeout.async";
	private static final String SUFIX_PROPERTY_CONNECT_TIMEOUT_BACKEND = ".connect.timeout";
	
	// Regular expressions to obfuscation
	private static final String FIELD_VALUE_SEPARATOR_REGEX = "\\s*:\\s*";
	private static final String SIMPLE_VALUE_REGEX = "\".+?\"";
	private static final String NUMBER_VALUE_REGEX = "\\d+";
	private static final String SIMPLE_ARRAY_REGEX = "\\[\"??.+?\"??\\]";
	private static final String OBFUSCATED_JSON_VALUE = "\"*****\"";
	
	// HttpMethod.PATCH exists as of jax-rs 2.1
	public static final String HTTP_METHOD_PATCH = "PATCH";
	
	@Autowired
	MisPropiedades propiedades;
	
	/**
	 * Returns true if headers contains "prefer" header with "respond-async" value
	 *
	 * @param headers
	 * @return
	 */
	public boolean isAsync (Map<String, String> headers) {
		return headers != null && headers.containsKey(PROPERTY_HEADER_PREFER) && headers.get(PROPERTY_HEADER_PREFER).equals(VALUE_HEADER_PREFER_RESPOND_ASYNC);
	}
	
	/**
	 * Gets connect timeout for specified backend
	 *
	 * @param backend
	 * @return
	 */
	public Double getConnectTimeout (String backend) {
		
		// get default connect timeout
		Double connectTimeout = CONNECT_TIMEOUT_DEFAULT;
		try {
			connectTimeout = Double.parseDouble(propiedades.getTimeoutConnect());
			LOG.info("[Rest Connector] default connectTimeout [" + connectTimeout + "]");
		} catch (Exception e) {
			LOG.warn("Error getting DEFAULT properties for connectTimeout " + e.getMessage());
		}
		return connectTimeout;
	}
	
	/**
	 * Gets read timeout for specified backend
	 *
	 * @param async
	 * @param backend
	 * @return
	 */
	public Double getReadTimeout (boolean async, String backend) {
		
		// get default read timeout
		Double readTimeout = READ_TIMEOUT_DEFAULT;
		try {
			readTimeout = Double.parseDouble(propiedades.getTimeoutRead());
			LOG.info("[Rest Connector] default readTimeout [" + readTimeout + "]");
		} catch (Exception e) {
			LOG.warn("Error getting DEFAULT properties for readTimeout " + e.getMessage());
		}
	
		return readTimeout;
	}
	
	/**
	 * Builds the query part of a URL
	 *
	 * @param params
	 * @return
	 */
	public static String buildQueryParams (HashMap<String, String> params) {
		
		String queryParams = "";
		if (params != null && !params.isEmpty()) {
			Set<String> keys = params.keySet();
			Iterator<String> it = keys.iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				String value = (String) params.get(key);
				if (value != null && !value.isEmpty()) {
					queryParams = new StringBuilder().append(queryParams).append(key).append("=").append(value).append("&").toString();
				}
			}
		}
		
		if (queryParams.length() > 1) {
			queryParams = queryParams.substring(0, queryParams.length() - 1);
		}
		return queryParams;
	}
	
	/**
	 * Obfuscates specified queryParams fields
	 *
	 * @param queryParams
	 * @param fieldsToObfuscate
	 * @return
	 */
	public static Map<String, String> obfuscateQueryParams (Map<String, String> queryParams, Collection<String> fieldsToObfuscate) {
		LOG.debug("QueryParams=" + queryParams + " FieldsToObfuscate=" + fieldsToObfuscate);
		for (String field : fieldsToObfuscate) {
			if (queryParams.containsKey(field)) {
				queryParams.put(field, "*****");
				LOG.debug("Replaced " + field + ". Result: " + queryParams.get(field));
			}
		}
		return queryParams;
	}
	
	/**
	 * Obfuscates specified payload fields
	 *
	 * @param payload
	 * @param fieldsToObfuscate
	 * @return
	 */
	public static String obfuscatePayload (String payload, Collection<String> fieldsToObfuscate) {
		LOG.debug("Payload=" + payload + " FieldsToObfuscate=" + fieldsToObfuscate);
		if (!StringUtils.isEmpty(payload)) {
			for (String field : fieldsToObfuscate) {
				String jsonField = "\"" + field + "\"";
				String fieldRegex = jsonField + FIELD_VALUE_SEPARATOR_REGEX + SIMPLE_VALUE_REGEX;
				String obfuscatedField = jsonField + ":" + OBFUSCATED_JSON_VALUE;
				payload = payload.replaceAll(fieldRegex, obfuscatedField);
				String numberRegex = jsonField + FIELD_VALUE_SEPARATOR_REGEX + NUMBER_VALUE_REGEX;
				String obfuscatedNumber = jsonField + ":" + OBFUSCATED_JSON_VALUE;
				payload = payload.replaceAll(numberRegex, obfuscatedNumber);
				String arrayFieldRegex = jsonField + FIELD_VALUE_SEPARATOR_REGEX + SIMPLE_ARRAY_REGEX;
				String obfuscatedArrayField = jsonField + ":\\[" + OBFUSCATED_JSON_VALUE + "\\]";
				payload = payload.replaceAll(arrayFieldRegex, obfuscatedArrayField);
				LOG.debug("Replaced " + fieldRegex + ", " + arrayFieldRegex + ". Result: " + payload);
			}
		}
		return payload;
	}
}
