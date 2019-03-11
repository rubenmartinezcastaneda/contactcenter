package com.bbva.jee.arq.spring.core.properties;
 
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
/*
 * Cargamos las propiedades al contexto.
 */
@PropertySource("classpath:config.properties")

@Component
public class MisPropiedades {
    @Value("${servicing.connector.rest.addheaders}")
    private String headers;
    @Value("${servicing.connector.rest.headers.aap}")
    private String aap;
    @Value("${servicing.connector.rest.headers.serviceID}")
    private String serviceID;
    @Value("${servicing.connector.rest.headers.contactID}")
    private String contactID;
    @Value("${servicing.connector.rest.headers.requestID}")
    private String requestID;
    @Value("${servicing.connector.rest.headers.user}")
    private String user;
    @Value("${servicing.connector.rest.google.accountId}")
    private String accountId;
    @Value("${servicing.connector.rest.google.accountUser}")
    private String accountUser;
    @Value("${servicing.connector.rest.google.scope}")
    private String scope;
    @Value("${servicing.connector.rest.google.privatekey.filepath}")
    private String filepath;
    @Value("${servicing.connector.rest.timeout.connect}")
    private String timeoutConnect;
    @Value("${servicing.connector.rest.timeout.read}")
    private String timeoutRead;
    @Value("${arqspring.seguridad.reintentos.tsec.error}")
    private String codErrorTsec;
	@Value("${com.bbva.jee.arq.spring.core.servicios.tsecCaducado.maxretries}")
    private String maxretries;
    
    public String getMaxretries() {
		return maxretries;
	}
	public void setMaxretries(String maxretries) {
		this.maxretries = maxretries;
	}
	public String getHeaders() {
        return headers;
    }
    public void setHeaders(String headers) {
        this.headers = headers;
    }
  
    public String getAap() {
        return aap;
    }
    public void setAap(String aap) {
        this.aap = aap;
    }
    
    public String getServiceID() {
        return serviceID;
    }
    public void setServiceID(String serviceID) {
        this.serviceID = serviceID;
    }
    
    public String getContactID() {
        return contactID;
    }
    public void setContactID(String contactID) {
        this.contactID = contactID;
    }
    
    public String getRequestID() {
        return requestID;
    }
    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }
    
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    
    public String getAccountId() {
        return accountId;
    }
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
    
    public String getAccountUser() {
        return accountId;
    }
    public void setAccountUser(String accountUser) {
        this.accountUser = accountUser;
    }
    
    public String getScope() {
        return scope;
    }
    public void setScope(String scope) {
        this.scope = scope;
    }
    
    public String getFilepath() {
        return filepath;
    }
    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
    
    public String getTimeoutConnect() {
        return timeoutConnect;
    }
    public void setTimeoutConnect(String timeoutConnect) {
        this.timeoutConnect = timeoutConnect;
    }
    
    public String getTimeoutRead() {
        return timeoutRead;
    }
    public void setTimeoutRead(String timeoutRead) {
        this.timeoutRead = timeoutRead;
    }
 
    public String getCodErrorTsec() {
		return codErrorTsec;
	}
	public void setCodErrorTsec(String codErrorTsec) {
		this.codErrorTsec = codErrorTsec;
	}
}