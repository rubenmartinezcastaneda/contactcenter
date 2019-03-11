package com.bbva.jee.arq.spring.core.rest;

public enum RestConnectorHeadersEnum {

	
	AAP ("aap"),
	SERVICE_ID ("serviceID"),
	CONTACT_ID ("contactID"),
	REQUEST_ID ("requestID"),
	USER ("user");

	private final String value;
	
	RestConnectorHeadersEnum(String _value){
		this.value = _value;
	}
	
	 public String getValue(){
		 return this.value;
	 }
	 
}
