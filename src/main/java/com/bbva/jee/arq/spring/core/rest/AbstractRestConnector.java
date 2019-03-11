package com.bbva.jee.arq.spring.core.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import com.bbva.jee.arq.spring.core.properties.MisPropiedades;
import com.bbva.jee.arq.spring.core.rest.utils.RestConnectorUtils;

public abstract class AbstractRestConnector implements IRestConnector {

	private static final Log LOG = LogFactory
			.getLog(AbstractRestConnector.class);

	// Resquest / Response charset
	protected static final String CHARSET = "UTF-8";

	public int reintentos = 0;

	// Property for configuration additional headers by backend (backend must be
	// concatenated: servicing.connector.rest.addheaders.ejemplo)
	private static final String PROPERTY_ADDHEADERS = "servicing.connector.rest.addheaders.";

	HashMap<String, String> infoGT = new HashMap<String, String>();

	@Autowired
	protected RestConnectorUtils restConnectorUtils;

	@Autowired
	MisPropiedades propiedades;

	protected RestConnectorType type;

	protected String backend;

	public RestConnectorType getType() {
		return type;
	}

	@Override
	public String getBackend() {
		return backend;
	}

	@Override
	public RestConnectorResponse doGet(String url,
			HashMap<String, String> params, HashMap<String, String> headers) {
		return doGet(url, params, headers, Collections.<String> emptyList());
	}

	@Override
	public RestConnectorResponse doPost(String url,
			HashMap<String, String> params, HashMap<String, String> headers,
			String payload) {
		return doPost(url, params, headers, payload,
				Collections.<String> emptyList());
	}

	@Override
	public RestConnectorResponse doPut(String url,
			HashMap<String, String> params, HashMap<String, String> headers,
			String payload) {
		return doPut(url, params, headers, payload,
				Collections.<String> emptyList());
	}

	@Override
	public RestConnectorResponse doPatch(String url,
			HashMap<String, String> params, HashMap<String, String> headers,
			String payload) {
		return doPatch(url, params, headers, payload,
				Collections.<String> emptyList());
	}

	@Override
	public RestConnectorResponse doDelete(String url,
			HashMap<String, String> params, HashMap<String, String> headers) {
		return doDelete(url, params, headers, Collections.<String> emptyList());
	}

	@Override
	public RestConnectorResponse doDelete(String url,
			HashMap<String, String> params, HashMap<String, String> headers,
			String payload) {
		throw new UnsupportedOperationException(
				"ArqSpringAbstractRestConnector does not support this operation");
	}

	/**
	 * Execute specified request
	 * 
	 * @param url
	 *            URI to invoke
	 * @param headers
	 *            headers to include in request
	 * @param request
	 *            request to execute
	 * @param backend
	 *            backend name which have associated config
	 * @param forceSync
	 *            if true, execution must be synchronous
	 * @param useProxy
	 *            if true, proxy will be used
	 * @param clientBuilder
	 *            builder of client which execute request
	 * @return RestConnectorResponse
	 * @throws Exception
	 */
	protected RestConnectorResponse execute(String url,
			HashMap<String, String> headers, HttpRequestBase request,
			String backend, boolean forceSync, boolean useProxy,
			HttpClientBuilder clientBuilder) throws Exception {

		LOG.info(new StringBuilder().append("HTTP request - [Method: ")
				.append(request.getMethod()).append("] ").append("[Url: ")
				.append(url).append("]").toString());
		CloseableHttpClient client;
		HttpResponse response = null;
		boolean reintento = true;

		boolean async = restConnectorUtils.isAsync(headers) && !forceSync;
		LOG.info("[Rest Connector] Execute request ASYNC " + async);

		try {
			Map<String, String> requestHeaders = null;
			// Si reintento es true se lanza el servicio
			while (reintento) {

				// timeouts
				RequestConfig.Builder configBuilder = setConnectionParams(
						async, backend);
				// authentication
				CredentialsProvider credentialsProvider = setAuthConfig(
						configBuilder, useProxy);
				client = clientBuilder
						.setDefaultRequestConfig(configBuilder.build())
						.setDefaultCredentialsProvider(credentialsProvider)
						.build();

				// Informar tsec en las headers
				informarTsecHeader(headers, client);

				// Limpiamos las cabeceras informadas para que en el reintento
				// de ejecuciï¿½n del servicio, no se dupliquen la claves
				if (requestHeaders != null && requestHeaders.size() > 0) {
					requestHeaders.clear();
					// Se eliminan las cabeceras almacenadas en la request, para
					// que no dupliquen
					HeaderIterator heads = request.headerIterator();
					if (heads != null) {
						while (heads.hasNext()) {
							org.apache.http.Header hd = heads.nextHeader();
							request.removeHeader(hd);
							heads = request.headerIterator();
						}
					}
				}

				requestHeaders = fillHttpHeaders(headers, request.getMethod(),
						backend);
				for (String key : requestHeaders.keySet()) {
					String value = requestHeaders.get(key);
					request.addHeader(key, value);
					LOG.debug("RestConnector.fillHttpHeaders key [" + key
							+ "] - Value [" + value + "]");
				}

				response = client.execute(request);

				// Comprobar si se debe realizar un reintento
				reintento = requiresFailover(response);
			}

			return processResponse(response);

		} catch (SocketTimeoutException e) {

			LOG.error("[Rest Connector] SocketTimeoutException: " + e);
			if (async) {
				LOG.info("[Rest Connector] Async Request - generating 202-Accepted response");
				return processAcceptedResponse();
			}
			throw e;

		} catch (Exception e) {
			LOG.error("[Rest Connector] Exception: " + e);
			throw e;
		}
	}

	private boolean requiresFailover(HttpResponse response) {

		boolean encontrado = false;
		// Reintentamos si no supera el maximo de reintentos
		// definido
		int maxRetries = Integer.parseInt(propiedades.getMaxretries());

		if (response != null) {
			String respondeCode = "";
			respondeCode = Integer.toString(response.getStatusLine()
					.getStatusCode());

			String cod_error_tsec = "";

			cod_error_tsec = propiedades.getCodErrorTsec();

			// Si el responseCode corresponde con la configuracion obtenida
			// de
			// los https que deben reintar, iniciamos logica de reintento.
			if (cod_error_tsec != null && cod_error_tsec.trim().length() != 0
					&& respondeCode != null) {
				StringTokenizer stToken = new StringTokenizer(cod_error_tsec,
						",");
				while (stToken.hasMoreTokens()) {
					String parametro = stToken.nextToken();
					if (parametro.indexOf(":") != -1)
						parametro = parametro.substring(0,
								parametro.indexOf(":"));
					if (parametro.equals(respondeCode)) {
						encontrado = true;
						break;
					}
				}
				if (encontrado) {
					if (response != null)
						respondeCode = Integer.toString(response
								.getStatusLine().getStatusCode());

					if (reintentos < maxRetries) {
						reintentos++;
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Lanza el servicio de GT, para recuperar un tsec e informarlo en la
	 * cabecera de la petición
	 * 
	 * @param headers
	 */
	private void informarTsecHeader(HashMap<String, String> headers,
			CloseableHttpClient client) {

		HashMap<String, String> params = new HashMap<String, String>();
		Map<String, String> requestHeaders = null;

		HttpResponse response = null;

		String urlGT = "";
		String payloadGT = "";

		// Recuperamos del parámetro headers, la información para ejecutar el
		// Servicio GT
		if (headers != null && headers.size() > 0 && infoGT != null
				&& infoGT.size() <= 0) {
			urlGT = headers.get("urlGT");
			headers.remove("urlGT");
			payloadGT = headers.get("payloadGT");
			headers.remove("payloadGT");

			// Almacenamos la información por si hay que realizar un
			// reintento en la ejecución del Servicio GT
			infoGT.put("urlGT", urlGT);
			infoGT.put("payloadGT", payloadGT);

		} else {
			urlGT = infoGT.get("urlGT");
			payloadGT = infoGT.get("payloadGT");

		}

		if (urlGT != null && !urlGT.equals("") && payloadGT != null
				&& !payloadGT.equals("")) {

			// Se crea el payload de la petición del Servicio GT
			String fullUrl = buildUrl(urlGT, params);
			HttpPost request = new HttpPost(fullUrl);
			request.setEntity(new StringEntity(payloadGT, CHARSET));

			try {

				requestHeaders = fillHttpHeaders(headers, request.getMethod(),
						backend);

				for (String key : requestHeaders.keySet()) {
					String value = requestHeaders.get(key);
					request.addHeader(key, value);
					LOG.debug("RestConnector.fillHttpHeaders key [" + key
							+ "] - Value [" + value + "]");
				}

				response = client.execute(request);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		Header[] resulGT = response.getAllHeaders();
		// Tratamos la respuesta del servicio GT, para recuperar el tsec
		if (resulGT.length > 0) {
			for (int i = 0; i <= resulGT.length; i++) {
				String clave = resulGT[i].getName();
				if (clave.equals("tsec")) {
					String tsec = resulGT[i].getValue();
					headers.remove("tsec");
					headers.put("tsec", tsec);
					break;
				}
			}

		}

	}

	/**
	 * Sets the connection timeouts
	 * 
	 * @param async
	 *            El parametro async indica si la peticiÃ³n es o no asÃ­ncrona
	 * @param backend
	 *            El parametro backend es el nombre del end point
	 * @return RequestConfig.Builder with timeouts
	 */
	private RequestConfig.Builder setConnectionParams(boolean async,
			String backend) {

		LOG.info("[Rest Connector] setConnectionParams - async [" + async
				+ "], backend [" + backend + "]");

		RequestConfig.Builder configBuilder = RequestConfig.custom();
		Double connectTimeout = restConnectorUtils.getConnectTimeout(backend);
		Double readTimeout = restConnectorUtils.getReadTimeout(async, backend);
		if (connectTimeout != null) {
			configBuilder.setConnectTimeout(connectTimeout.intValue());
		}
		if (readTimeout != null) {
			configBuilder.setSocketTimeout(readTimeout.intValue());
		}
		return configBuilder;
	}

	/**
	 * Seteamos propiedades necesarias para la autenticacion: - Datos de proxy
	 * para salida a internet - Ruta de certificados para comunicacion con urls
	 * seguras (En los servidores el almacen de certs (cacerts) estandar se ha
	 * capado y hay que apuntarlo a nuestra ruta de certs de ArqServicios
	 */
	private CredentialsProvider setAuthConfig(RequestConfig.Builder builder,
			boolean useProxy) {

		LOG.info("[Rest Connector] Use Proxy: " + useProxy);
		String host = "";
		Integer port = 0;
		String user = "";
		String pwd = "";

		CredentialsProvider credentialsProvider = null;
		if (useProxy) {
			try {
				// Obtenemos la configuracion
				// String[] conf = ;
				String conf[] = new String[2];
				boolean hayCredenciales = conf.length == 4;

				host = conf[0];
				port = Integer.valueOf(conf[1]);
				if (hayCredenciales) {
					user = conf[2];
					pwd = conf[3];
				}

				LOG.info("[Rest Connector] Proxy authentication data - Host: "
						+ host + " and port: " + port);

				if (host != null && port != null) {

					HttpHost proxy = new HttpHost(host, port);
					builder.setProxy(proxy);

					// Add proxy user and password to default authenticator
					if (user != null && pwd != null) {
						credentialsProvider = new BasicCredentialsProvider();
						credentialsProvider.setCredentials(new AuthScope(host,
								port), new UsernamePasswordCredentials(user,
								pwd));
						LOG.info("[Rest Connector] Proxy authentication data - User and password ");
					}
					LOG.info("[Rest Connector] Proxy data set");
				}
			} catch (Exception e) {

			}

		}

		LOG.info("[Rest Connector Credentials]");
		LOG.info(System.getProperty("javax.net.ssl.trustStore"));

		return credentialsProvider;
	}

	/**
	 * Returns optional and backend configured headers
	 * 
	 * @param headers
	 * @param method
	 * @param backend
	 * @throws Exception
	 */
	protected Map<String, String> fillHttpHeaders(
			HashMap<String, String> headers, String method, String backend)
			throws Exception {

		Map<String, String> requestHeaders = new HashMap<String, String>();
		if (headers != null) {
			requestHeaders.putAll(headers);
		}

		// Accept and Content-Type default values
		if (headers == null || !headers.containsKey(HttpHeaders.ACCEPT)) {
			requestHeaders.put(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
			LOG.debug("Accept header set to default value "
					+ MediaType.APPLICATION_JSON
					+ " because of it's not informed");
		}
		if (!method.equals(HttpMethod.GET)
				&& (headers == null || !headers
						.containsKey(HttpHeaders.CONTENT_TYPE))) { // only if it
																	// is not
																	// GET
																	// method
			requestHeaders.put(
					HttpHeaders.CONTENT_TYPE,
					MediaType.APPLICATION_JSON.concat(";charset=").concat(
							CHARSET));
			LOG.debug("Content-Type header set to default value "
					+ MediaType.APPLICATION_JSON.concat(";charset=").concat(
							CHARSET) + " because of it's not informed");
		}

		/*-
		 * Se aÃ±aden a las cabeceras los datos de contexto configurados para el backend que llegue como param
		 *
		 * - aap. Consumidor del servicio.
		 * - ServiceID. Identificador del servicio.
		 * - RequestID. Identificador de la peticiÃ³n de servicio.
		 * - ContactID. Identificador de la sesiÃ³n del canal.
		 * - User.Usuario del canal.
		 */
		if (backend != null && !"".equals(backend)) {

			String[] propAddHeadersToRequest = new String[3];
			String header = "";
			try {
				header = propiedades.getHeaders();

				if (header != null && !header.equals("")) {
					propAddHeadersToRequest = header.split(",");
				}

				LOG.debug("[Rest Connector] Property addHeaders to request in REST Connector for backend ["
						+ backend + "]:" + propAddHeadersToRequest);
			} catch (Exception e) {
				LOG.debug("[Rest Connector] Error getting property for add context headers to backend key ["
						+ backend + "]:" + e.getMessage());
			}

			if (propAddHeadersToRequest != null
					&& propAddHeadersToRequest.length > 0) {

				for (int i = 0; i <= propAddHeadersToRequest.length; i++) {
					try {

						header = propAddHeadersToRequest[i];

						switch (RestConnectorHeadersEnum.valueOf(header)) {

						case AAP:
							String aap = null;
							aap = propiedades.getAap();

							if (aap != null) {
								requestHeaders
										.put(RestConnectorHeadersEnum.AAP
												.getValue(), aap);
								LOG.debug("[Rest Connector] Request header 'aap' is set for backend ["
										+ backend + "]");
							}
							break;

						case SERVICE_ID:
							String serviceID = null;
							serviceID = propiedades.getServiceID();

							if (serviceID != null) {
								requestHeaders.put(
										RestConnectorHeadersEnum.SERVICE_ID
												.getValue(), serviceID);
								LOG.debug("[Rest Connector] Request header 'serviceID' is set for backend ["
										+ backend + "]");
							}
							break;

						case REQUEST_ID:
							String requestID = null;

							requestID = propiedades.getRequestID();

							if (requestID != null) {
								requestHeaders.put(
										RestConnectorHeadersEnum.REQUEST_ID
												.getValue(), requestID);
								LOG.debug("[Rest Connector] Request header 'requestID' is set for backend ["
										+ backend + "]");
							}
							break;

						case CONTACT_ID:
							String contactID = null;
							contactID = propiedades.getContactID();

							if (contactID != null) {
								requestHeaders.put(
										RestConnectorHeadersEnum.CONTACT_ID
												.getValue(), contactID);
								LOG.debug("[Rest Connector] Request header 'contactID' is set for backend ["
										+ backend + "]");
							}
							break;

						case USER:
							String user = null;
							user = propiedades.getUser();

							if (user != null) {
								requestHeaders.put(
										RestConnectorHeadersEnum.USER
												.getValue(), user);
								LOG.debug("[Rest Connector] Request header 'user' is set for backend ["
										+ backend + "]");
							}
							break;

						default:
							LOG.debug("[Rest Connector] WARN - Request header "
									+ header + " NOT SET for backend ["
									+ backend + "]");
							break;
						}

					} catch (Exception e) {
						LOG.warn("[Rest Connector] Error getting servicing contextData to fill rest headers: "
								+ e.getMessage());
					}
				}
			}
		}
		LOG.debug("[Rest Connector] RestConnector.fillHttpHeaders length "
				+ requestHeaders.size());
		return requestHeaders;
	}

	/**
	 * Procesa la respuesta de una peticion REST rellenando un DTO de respuesta
	 * que sera lo que se devuelva al usuario
	 * 
	 * @param httpResponse
	 * @return DTO de respuesta con datos de la response: @see
	 *         com.bbva.jee.arq.spring.core.rest.RestConnectorResponse
	 * @throws Exception
	 */
	private RestConnectorResponse processResponse(HttpResponse httpResponse)
			throws Exception {

		RestConnectorResponse restServiceResponse = null;
		try {
			if (httpResponse != null) {
				restServiceResponse = new RestConnectorResponse();

				// Recuperamos cabeceras
				if (httpResponse.getAllHeaders() != null) {
					restServiceResponse.fillHeaders(httpResponse
							.getAllHeaders());
				}

				// Recuperamos cuerpo de la respuesta
				if (httpResponse.getEntity() != null
						&& httpResponse.getEntity().getContent() != null) {
					byte[] data = toByteArray(httpResponse.getEntity()
							.getContent());
					restServiceResponse.setContentBytes(data);
				} else {
					restServiceResponse.setContentBytes(null);
				}

				String charset;
				if (httpResponse.getEntity() != null
						&& httpResponse.getEntity().getContentEncoding() != null) {
					charset = httpResponse.getEntity().getContentEncoding()
							.getValue();
				} else {
					charset = CHARSET;
				}
				restServiceResponse.setCharset(charset);
				restServiceResponse.setStatusCode(httpResponse.getStatusLine()
						.getStatusCode());

				if (httpResponse.getEntity() != null
						&& httpResponse.getEntity().getContentType() != null) {
					restServiceResponse.setContentType(httpResponse.getEntity()
							.getContentType().getValue());
				} else {
					restServiceResponse.setContentType(null);
				}

				restServiceResponse.generateResponseBody();
			}
		} catch (UnsupportedEncodingException e) {
			LOG.error("[Rest Connector] UnsupportedEncodingException in processResponse "
					+ e);
			throw e;
		} catch (IOException ioe) {
			LOG.error("[Rest Connector] IOException in processResponse " + ioe);
			throw ioe;
		} catch (Exception ex) {
			LOG.error("[Rest Connector] Exception in processResponse " + ex);
			throw ex;
		}

		LOG.info("[Rest Connector] processResponse: "
				+ restServiceResponse.toString());
		return restServiceResponse;
	}

	/**
	 * Returns a 202 Accepted RestConnectorResponse
	 * 
	 * @return
	 */
	private RestConnectorResponse processAcceptedResponse() {
		RestConnectorResponse restServiceAcceptedResponse = new RestConnectorResponse();
		restServiceAcceptedResponse.setStatusCode(HttpStatus.SC_ACCEPTED);
		return restServiceAcceptedResponse;
	}

	/**
	 * Build full url adding query params
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	protected String buildUrl(String url, HashMap<String, String> params) {
		String queryParams = RestConnectorUtils.buildQueryParams(params);
		StringBuilder sbUrl = new StringBuilder(url);
		if (queryParams != null && queryParams.length() > 1) {
			sbUrl.append("?").append(queryParams);
		}
		return sbUrl.toString();
	}

	/**
	 * Read bytes from inputStream and writes to OutputStream, later converts
	 * OutputStream to byte array in Java.
	 * 
	 * @param is
	 * @return byte[] byteArray del inputStream recibido
	 * @throws IOException
	 */
	public static byte[] toByteArray(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		int reads = is.read();
		while (reads != -1) {
			baos.write(reads);
			reads = is.read();
		}
		is.close();
		return baos.toByteArray();
	}
}
