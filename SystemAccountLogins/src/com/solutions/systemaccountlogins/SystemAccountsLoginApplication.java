package com.solutions.systemaccountlogins;

import java.util.HashMap;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

public class SystemAccountsLoginApplication extends Application {
	
	private static final String TAG = "CustomActionBarApplication";
	
	private String userName;
	private String password;
	private String emailAddress;
	
	
			
	private HashMap<String,String> fragmentValue;
	private static SystemAccountsLoginApplication singleton = null;
	
	public static SystemAccountsLoginApplication getInstance(){
		
		return singleton;
		
	
	}
	
	@Override 
	public void onConfigurationChanged(Configuration newConfig) { 
			super.onConfigurationChanged(newConfig); 
	} 
		 
	@Override 
	public void onCreate() { 
		super.onCreate(); 
		Log.d(TAG,"Creating instance of Application:");
		singleton = this;
	} 
	 
	 
	@Override 
	public void onLowMemory() { 
	 		super.onLowMemory(); 
	} 
	 
	 
	@Override 
	public void onTerminate() { 
		super.onTerminate(); 
	} 
	 
	 

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	
	

}
