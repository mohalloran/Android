package com.solutions.systemaccountlogins.manager;

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;

import com.solutions.systemaccountlogins.constants.SearchQuery;
import com.solutions.systemaccountlogins.dao.SystemAccount;
import com.solutions.systemaccountlogins.database.SystemAccountsDatabaseHelper;
import com.solutions.systemaccountlogins.database.SystemAccountsDatabaseHelper.SystemCursor;

public class SystemAccountsManager {
	
	private ArrayList<SystemAccount> systemAccountsList = new ArrayList<SystemAccount>();
	private SystemAccount systemAccounts;
	private Context context;
	private SystemAccountsDatabaseHelper systemAccountsDatabaseHelper;
	
	private static SystemAccountsManager systemAccountsManager;
	
	public SystemAccountsManager(Context context){
		this.context = context;
		
		systemAccountsDatabaseHelper = new SystemAccountsDatabaseHelper(context);
		
		
		
		//PopulateDummyData();
	}
	
	public static SystemAccountsManager getInstance(Context context){
		
		if(systemAccountsManager == null){
			systemAccountsManager = new SystemAccountsManager(context);
		}
		return systemAccountsManager;	
	}
	
	public ArrayList<SystemAccount> PopulateDummyData(){
		
		systemAccounts = new SystemAccount();
		
		systemAccounts.setId(1L);
		systemAccounts.setExpired((short)1);
		systemAccounts.setLoginName("mohallo");
		systemAccounts.setApplication("MEAT");
		systemAccounts.setServerName("suznd263");
		systemAccounts.setDescription("Old cvs server for OAS");
		systemAccountsList.add(systemAccounts);
		
		systemAccounts = new SystemAccount();
		
		systemAccounts.setId(2L);
		systemAccounts.setExpired((short)1);
		systemAccounts.setLoginName("mohallo");
		systemAccounts.setApplication("DOPT");
		systemAccounts.setServerName("doptltndev1");
		systemAccounts.setDescription("Old DOPT Application Dev Server.Still contains source code");
		systemAccountsList.add(systemAccounts);
		
		systemAccounts = new SystemAccount();
		
		systemAccounts.setId(3L);
		systemAccounts.setExpired((short)0);
		systemAccounts.setLoginName("mohallo");
		systemAccounts.setApplication("OAS FOM");
		systemAccounts.setServerName("ecomt122");
		systemAccounts.setDescription("Ecom Machine Oas Deployments");
		systemAccountsList.add(systemAccounts);
		
        return systemAccountsList;
		
	}
	
	/**
	 * Truncates the System Table
	 */
	public boolean truncateSystemTable(){
		return systemAccountsDatabaseHelper.truncateSystemTable();
		
	}
	
	/**
	 * SystemCursor is a wrapper supplies method getSystemAccount to parse the contents of the SystemCursor and return a SystemAccount Object.
	 * @param accountId
	 * @return
	 */
	public SystemAccount getSystemAccount(long accountId){
		
		SystemCursor systemAccountCursor = systemAccountsDatabaseHelper.getSystemAccount(accountId);
		systemAccountCursor.moveToFirst();//Move to the first row
		SystemAccount systemAccount = null;

        // if we got a row, get a systemAccount
        if (!systemAccountCursor.isAfterLast())
        	systemAccount = systemAccountCursor.getSystemAccount();
            
        systemAccountCursor.close();
        
        return systemAccount;

	}
	
	
	
	/**
	 * returns Cursor
	 * @return
	 */
	public Cursor getSystemAccounts(){
		
		return systemAccountsDatabaseHelper.getSystemAccounts();

	}
	
	/**
	 * returns Cursor
	 * @return
	 */
	public Cursor getSystemAccounts(SearchQuery searchQuery){
		
		return systemAccountsDatabaseHelper.getSystemAccounts(searchQuery);

	}
	
	/**
	 * Returns a list of SystemAccounts
	 * @return
	 */
	public ArrayList<SystemAccount> getSystemAccountsList(){
		
		ArrayList<SystemAccount> systemAccountList = new ArrayList<SystemAccount>();
		SystemCursor systemAccountsCursor = systemAccountsDatabaseHelper.getSystemAccountList();
		// if we got a row, got a systemAccount
        if (!systemAccountsCursor.isAfterLast())
		     systemAccountList = systemAccountsCursor.geSystemAccountsList();
        
        return systemAccountList;
	}
	
	public SystemAccount getSystemAccount(Long id){
		
		return systemAccountsList.get(id.intValue());
	}
	
	/**
	 * Insterts Record
	 * @param systemAccount
	 * @return
	 */
	public long insertSystemAccount(SystemAccount systemAccount){
		
		return systemAccountsDatabaseHelper.insertSystemRecord(systemAccount);
		
	}
	
	/**
	 * update the Database with record
	 * @param systemAccount
	 * @return
	 */
	public long updateSystemRecord(SystemAccount systemAccount){
		
		return systemAccountsDatabaseHelper.updateSystemRecord(systemAccount);
	}
	
	/*
	 * check if a record exists for supplied object .
	 */
	public boolean doesRecordExist(SystemAccount systemAccount){
		
		return systemAccountsDatabaseHelper.doesRecordExist(systemAccount);
	}
	
	public boolean doesRecordExist(long id){
		
		return systemAccountsDatabaseHelper.doesRecordExist(id);
	}
    
	
	public long deleteRecord(long id){
		
		return systemAccountsDatabaseHelper.deleteRecord(id);
	}

}
