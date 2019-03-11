package com.bbva.jee.arq.spring.core.rest;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.bbva.jee.arq.spring.core.properties.MisPropiedades;

@Component
public class RestConnectorFactory {
	
	private static final Log LOG = LogFactory.getLog(RestConnectorFactory.class);
	
	private static final String KEY_SEPARATOR = "#";
	
	@Autowired
	private ApplicationContext appContext;
	
	@Autowired
	MisPropiedades propiedades;
	
	//@Autowired
	//ConfigurationManager configurationManager;
	
	private Map<String, IRestConnector> mapRestConnector;
	
	/**
	 * Returns a RestConnector of specified type
	 *
	 * @param type
	 * @return
	 */
	public IRestConnector getRestConnector (RestConnectorType type) {
		return getRestConnector(type, null);
	}
	
	/**
	 * Returns a RestConnector of specified type with backend config
	 *
	 * @param type
	 * @param backend
	 * @return
	 */
	public IRestConnector getRestConnector (RestConnectorType type, String backend) {
		return getRestConnector(type, backend, null);
	}
	
	/**
	 * Returns a RestConnector of specified type with backend and appId config
	 *
	 * @param type
	 * @param backend
	 * @param appId
	 * @return
	 */
	public IRestConnector getRestConnector (RestConnectorType type, String backend, String appId) {
		
		if (!validateRestConnector(type, backend, appId)) {
			LOG.error("[RestConnectorFactory] Requested RestConnector invalid [" + type + ", " + backend + ", " + appId + "]");
			//throw new BusinessServiceException("errorRestConnectorFactory", "] Requested RestConnector invalid");
			try {
				throw new Exception("errorRestConnectorFactory");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		IRestConnector restConnector = null;
		appId = !StringUtils.isEmpty(appId) ? appId : "default";
		String key = generateKey(type, backend, appId);
		
		if (mapRestConnector == null || !mapRestConnector.containsKey(key)) {
			switch (type) {
				case BASIC:
					restConnector = (IRestConnector) appContext.getBean("restConnectorBasic");
					break;
				case OAUTH:
					restConnector = (IRestConnector) appContext.getBean("restConnectorOAuth", backend, appId);
					break;
				case CERTIFICATE:
					restConnector = (IRestConnector) appContext.getBean("restConnectorCertificate", backend, appId);
					break;
				case SALESFORCE:
					restConnector = (IRestConnector) appContext.getBean("restConnectorSalesforce");
					break;
				case GOOGLE:
					restConnector = (IRestConnector) appContext.getBean("restConnectorGoogle");
					break;
				default:
					LOG.error("[RestConnectorFactory] Unknown RestConnector type.");
				try {
					throw new Exception("errorRestConnectorFactory");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (mapRestConnector == null) {
				mapRestConnector = new HashMap<String, IRestConnector>();
			}
			mapRestConnector.put(key, restConnector);
		}
		
		restConnector = mapRestConnector.get(key);
		LOG.info("[RestConnectorFactory] Returns " + restConnector + " (key = " + key + ")");
		return restConnector;
	}
	
	/**
	 * Checks if RestConnector type is agree with provided configuration
	 *
	 * @param type
	 * @param backend
	 * @param appId
	 * @return
	 */
	private boolean validateRestConnector (RestConnectorType type, String backend, String appId) {
		boolean result = true;
		switch (type) {
			case BASIC:
				result = appId == null;
				break;
			case OAUTH:
			case CERTIFICATE:
				result = backend != null;
				break;
			case SALESFORCE:
			case GOOGLE:
				result = backend == null && appId == null;
				break;
		}
		return result;
	}
	
	/**
	 * Returns key of RestConnector map depends on type, backend and appId
	 *
	 * @param type
	 * @param backend
	 * @param appId
	 * @return
	 */
	private String generateKey (RestConnectorType type, String backend, String appId) {
		String key = null;
		switch (type) {
			case BASIC:
				// BASIC#backend or BASIC
				key = RestConnectorType.BASIC.name() + (!StringUtils.isEmpty(backend) ? KEY_SEPARATOR + backend : "");
				break;
		//Comentamos el resto de tipos ya que actualmente no se van a utilizar
			case OAUTH:
			case CERTIFICATE:
				// type#backend.appId or type#backend.default
				key = type.name() + KEY_SEPARATOR + backend + (!StringUtils.isEmpty(appId) ? "." + appId : ".default");
				break;
			case SALESFORCE:
				// SALESFORCE
				key = RestConnectorType.SALESFORCE.name();
				break;
			case GOOGLE:
				// GOOGLE#accountId
				
					String accountId = propiedades.getAccountId();
					key = RestConnectorType.GOOGLE.name() + KEY_SEPARATOR + accountId;
					break;
				
		
		}
		return key;
	}
}
