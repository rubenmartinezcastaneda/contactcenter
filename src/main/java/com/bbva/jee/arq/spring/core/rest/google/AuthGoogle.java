package com.bbva.jee.arq.spring.core.rest.google;


import com.bbva.jee.arq.spring.core.properties.MisPropiedades;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivan.jimenez on 05/12/2017.
 */
@Component
@Scope ("prototype")
public class AuthGoogle {
	
	private static final Log LOG = LogFactory.getLog(AuthGoogle.class);
	
	public static final String GOOGLE_ACCOUNT_ID = "servicing.connector.rest.google.accountId";
	private static final String GOOGLE_ACCOUNT_USER = "servicing.connector.rest.google.accountUser";
	private static final String GOOGLE_CREDENTIAL_SCOPES = "servicing.connector.rest.google.scope";
	private static final String GOOGLE_PRIVATEKEY_FILEPATH = "servicing.connector.rest.google.privatekey.filepath";
	
	@Autowired
	MisPropiedades propiedades;
	
	@Autowired
	ApplicationContext applicationContext;
	
	private GoogleCredential googleCredential;
	
	/**
	 * Returns google credential
	 *
	 * @param httpTransport
	 * @return
	 * @throws Exception
	 */
	public GoogleCredential getGoogleCredential (HttpTransport httpTransport) throws Exception {
		
		if (googleCredential == null) {
			googleCredential = generateGoogleCredential(httpTransport);
		}
		try {
			// refresh token
			googleCredential.refreshToken();
		} catch (Exception e) {
			LOG.error("Exception in AuthGoogle. Error: " + e.getMessage());
			throw new Exception("Could not get accessToken from google", e);
		}
		LOG.debug("[Auth Google] Google AccessToken: " + googleCredential.getAccessToken());
		return googleCredential;
	}
	
	/**
	 * Generate google credential
	 *
	 * @param httpTransport
	 * @return
	 * @throws Exception
	 */
	private GoogleCredential generateGoogleCredential (HttpTransport httpTransport) throws Exception {
		
		GoogleCredential credential = null;
		try {
			String accountId = propiedades.getAccountId();
			String accountUser = propiedades.getAccountUser();
			if (accountId == null || accountUser == null) {
				LOG.error("Missing some parameters of google service [AccountId:" + accountId + "] [AccountUser:" + accountUser + "]");
				throw new Exception("Missing some parameters of google service [AccountId:" + accountId + "] [AccountUser:" + accountUser + "]");
			}
			LOG.debug("[Auth Google] Google credential - accountId: " + accountId + " - accountUser: " + accountUser);
			
			File certificate = getCertificate();
			String scopes = propiedades.getScope();
			
			GoogleCredential.Builder credentialBuilder = new GoogleCredential.Builder().setTransport(httpTransport).setJsonFactory(JacksonFactory.getDefaultInstance());
			credential = credentialBuilder.setServiceAccountId(accountId).setServiceAccountUser(accountUser).setServiceAccountPrivateKeyFromP12File(certificate).setServiceAccountScopes(scopes).build();
			
		} catch (GeneralSecurityException e) {
			LOG.error("GeneralSecurityException in AuthGoogle. Error: " + e.getMessage());
			throw new Exception("Could not get accessToken from google", e);
		} catch (Exception e) {
			LOG.error("Exception in AuthGoogle. Error: " + e.getMessage());
			throw new Exception("Could not get accessToken from google", e);
		}
		return credential;
	}
	
	/**
	 * Returns certificate for google service
	 *
	 * @return
	 * @throws Exception
	 */
	private File getCertificate () throws Exception {
		
		String pattern = propiedades.getFilepath();
		if (pattern == null || pattern.isEmpty()) {
			throw new Exception("Property pattern for certificate file could not be set");
		}
		LOG.debug("[Auth Google] Certificate filepath pattern " + pattern);
		
		List<File> fileResources = new ArrayList<File>();
		try {
			Resource[] resources = applicationContext.getResources(pattern);
			if (resources == null) {
				LOG.warn("No resources found with pattern " + pattern);
			} else {
				for (Resource resource : resources) {
					if (resource instanceof UrlResource) {
						try {
							File resourceFile = resource.getFile();
							fileResources.add(resourceFile);
						} catch (IOException e) {
							LOG.error("IOException found while looking for resources with pattern " + pattern, e);
						}
					}
				}
			}
		} catch (IOException e) {
			LOG.error("Errors found while looking for resources with pattern " + pattern, e);
		}
		
		if (CollectionUtils.isEmpty(fileResources)) {
			throw new Exception("Certificate could not be loaded. File not found in pattern " + pattern);
		}
		LOG.debug("[AuthGoogle] getResources - size result certificates " + fileResources.size());
		File certificate = fileResources.get(0);
		if (certificate == null) {
			LOG.error("Certificate p12 for google could not be loaded");
			throw new Exception("Certificate p12 for google could not be loaded");
		}
		return certificate;
	}
}
