package com.solutions.systemaccountlogins.dao;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

public class SystemAccount implements Serializable,Comparable<SystemAccount>{
	
	private Long id;
	private String userName;
	private String application;
	private String serverName;
	private String dateCreated;
	private String loginName;
	private String loginPassword;
	private String Description;
	private String Notes;
	private String Environment = "0";
	private short expired = 0;
	
	private static final String JSON_ACCOUNT_ID = "_id";
	private static final String JSON_USER_NAME = "USER_NAME";
    private static final String JSON_SERVER_NAME = "SERVER_NAME";
    private static final String JSON_APPLICATION_NAME = "APPLICATION_NAME";
    private static final String JSON_DATE_CREATED = "DATE_CREATED";
    private static final String JSON_LOGIN_NAME = "LOGIN_NAME";
    private static final String JSON_LOGIN_PASSWORD = "LOGIN_PASSWORD";
    
    private static final String JSON_DESCRIPTION = "DESCRIPTION";
    private static final String JSON_ENVIRONMENT = "ENVIRONMENT";
    private static final String JSON_NOTES = "NOTES";
    private static final String JSON_EXPIRED = "EXPIRED";
    
    public SystemAccount(){}
    /**
     * Pass in a JSONObject and extract the content to instance variables
     * @param json
     * @throws JSONException
     */
    public SystemAccount(JSONObject json) throws JSONException {
		
		id = json.getLong(JSON_ACCOUNT_ID);
		
		userName = json.getString(JSON_USER_NAME == null?JSON_LOGIN_NAME:JSON_USER_NAME);
		serverName = json.getString(JSON_SERVER_NAME);
		application = json.getString(JSON_APPLICATION_NAME);
		dateCreated = json.getString(JSON_DATE_CREATED);
		loginName = json.getString(JSON_LOGIN_NAME == null?JSON_USER_NAME:JSON_LOGIN_NAME );
		loginPassword = json.getString(JSON_LOGIN_PASSWORD);
		Description = json.getString(JSON_DESCRIPTION);
		Environment = json.getString(JSON_ENVIRONMENT);
		Notes = json.getString(JSON_NOTES);
		expired = (short)json.getInt(JSON_EXPIRED);

	}
    
    public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(JSON_ACCOUNT_ID,id.toString());
		json.put(JSON_USER_NAME, userName);
		json.put(JSON_SERVER_NAME, serverName);
		json.put(JSON_APPLICATION_NAME, application);
		json.put(JSON_DATE_CREATED,dateCreated);
		
		json.put(JSON_LOGIN_NAME,loginName);
		json.put(JSON_LOGIN_PASSWORD, loginPassword);
		json.put(JSON_DESCRIPTION, Description);
		json.put(JSON_ENVIRONMENT, Environment);
		json.put(JSON_NOTES,Notes);
		json.put(JSON_EXPIRED,expired);
		
		return json;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public String getLoginPassword() {
		return loginPassword;
	}
	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}
	public String getDescription() {
		return Description;
	}
	public void setDescription(String description) {
		Description = description;
	}
	public String getNotes() {
		return Notes;
	}
	public void setNotes(String notes) {
		Notes = notes;
	}
	public String getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}
	
	public short getExpired() {
		return expired;
	}
	public void setExpired(short expired) {
		this.expired = expired;
	}
	public String getApplication() {
		return application;
	}
	public void setApplication(String application) {
		this.application = application;
	}
	public String getEnvironment() {
		return Environment;
	}
	public void setEnvironment(String environment) {
		Environment = environment;
	}
	
	
	@Override
	public int compareTo(SystemAccount systemAccount) {
		// TODO Auto-generated method stub
		
		if( this.getApplication().compareToIgnoreCase(systemAccount.getApplication()) < 0 
				|| this.getServerName().compareToIgnoreCase(systemAccount.getServerName()) < 0
				|| this.getLoginName().compareToIgnoreCase(systemAccount.getLoginName()) < 0){
		  return -1;
		}else if( this.getApplication().compareToIgnoreCase(systemAccount.getApplication()) > 0 
				|| this.getServerName().compareToIgnoreCase(systemAccount.getServerName()) > 0
				|| this.getLoginName().compareToIgnoreCase(systemAccount.getLoginName()) > 0){
		  return 1;
		}else{
			return 0;
		}
	}
	
	
	
	
	
	
	
}
