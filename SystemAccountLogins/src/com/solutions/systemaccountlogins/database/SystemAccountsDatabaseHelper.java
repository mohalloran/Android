package com.solutions.systemaccountlogins.database;

import java.util.ArrayList;
import java.util.Date;

import com.solutions.systemaccountlogins.constants.SearchQuery;
import com.solutions.systemaccountlogins.dao.SystemAccount;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SystemAccountsDatabaseHelper extends SQLiteOpenHelper{
	
	private static final String TAG = "SystemAccountsDatabaseHelper";
	
	private static final int DB_VERSION = 6;
	private static final String DB_NAME = "systemaccounts";
	private static final String TABLE_SYSTEM = "system";
	
	private static final String COLUMN_ACCOUNT_ID = "_id";
	private static final String COLUMN_USER_NAME = "USER_NAME";
    private static final String COLUMN_SERVER_NAME = "SERVER_NAME";
    private static final String COLUMN_APPLICATION_NAME = "APPLICATION_NAME";
    private static final String COLUMN_DATE_CREATED = "DATE_CREATED";
    private static final String COLUMN_LOGIN_NAME = "LOGIN_NAME";
    private static final String COLUMN_LOGIN_PASSWORD = "LOGIN_PASSWORD";
    
    private static final String COLUMN_DESCRIPTION = "DESCRIPTION";
    private static final String COLUMN_ENVIRONMENT = "ENVIRONMENT";
    private static final String COLUMN_NOTES = "NOTES";
    private static final String COLUMN_EXPIRED = "EXPIRED";
    
	
	private SystemAccount systemAccounts;

	public SystemAccountsDatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		
	}

	//Called when the Database does not exist.
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		Log.d(TAG,"onCreate()...Creating the database..");
		String createSystemSql = "CREATE TABLE SYSTEM(_id INTEGER PRIMARY KEY AUTOINCREMENT, "+ 
				"USER_NAME TEXT, "+
				"SERVER_NAME TEXT,"+
				"APPLICATION_NAME," +
				"DATE_CREATED DATETIME,"+
				"LOGIN_NAME TEXT,"+
				"LOGIN_PASSWORD TEXT,"+
				"DESCRIPTION TEXT,"+
				"ENVIRONMENT,"+
				"NOTES TEXT,"+
				"EXPIRED NUMERIC);";

		db.execSQL(createSystemSql);

	}

	/**
	 * Called when there is a version upgrade when the DB_VERSION is greater than current version of the db
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG,"onUpgrade()...Updating the database..");
		
		if(DB_VERSION <= 6){
		  Log.d(TAG,"onUpgrade()...Updating the database to Version 2..");
		  db.execSQL("DROP TABLE IF EXISTS " + SystemAccountsDatabaseHelper.TABLE_SYSTEM);
          onCreate(db);
	    }
	}
	
	public long insertSystemRecord(SystemAccount sa) {

		ContentValues contentValues = new ContentValues();
		SQLiteDatabase db = null;

		contentValues.put("USER_NAME", sa.getUserName());
		contentValues.put("EXPIRED", sa.getExpired());
		contentValues.put("LOGIN_NAME", sa.getLoginName());
		contentValues.put("APPLICATION_NAME", sa.getApplication());
		contentValues.put("LOGIN_PASSWORD", sa.getLoginPassword());
		contentValues.put("DATE_CREATED", sa.getDateCreated());
		contentValues.put("SERVER_NAME", sa.getServerName());
		contentValues.put("ENVIRONMENT", sa.getEnvironment());
		contentValues.put("DESCRIPTION", sa.getDescription());
		contentValues.put("NOTES", sa.getNotes());

		try{
		   db = getWritableDatabase();
		   
		   return db.insertOrThrow(TABLE_SYSTEM, null, contentValues);
		   
		}catch(SQLiteConstraintException e){
			Log.e(TAG,"SQLiteConstraintException:" + e.getMessage());
			return 0;
		}catch (SQLiteException e) {
			Log.e(TAG, "SQLiteException:" + e.getMessage());
			return 0;
		}catch (Exception e){
			Log.e(TAG, "Exception:" + e.getMessage());
			return 0;
		}
   

	}
	
	public long updateSystemRecord(SystemAccount sa) {

		ContentValues contentValues = new ContentValues();
        String id = sa.getId().toString();
        
        contentValues.put("USER_NAME", sa.getUserName());
		contentValues.put("EXPIRED", sa.getExpired());
		contentValues.put("LOGIN_NAME", sa.getLoginName());
		contentValues.put("APPLICATION_NAME", sa.getApplication());
		contentValues.put("LOGIN_PASSWORD", sa.getLoginPassword());
		contentValues.put("DATE_CREATED", sa.getDateCreated());
		contentValues.put("SERVER_NAME", sa.getServerName());
		contentValues.put("ENVIRONMENT", sa.getEnvironment());
		contentValues.put("DESCRIPTION", sa.getDescription());
		contentValues.put("NOTES", sa.getNotes());

		return getWritableDatabase().update(TABLE_SYSTEM,contentValues,"_id=?",new String[] {id});

	}    
	
	/**
	 * Does the record exist.
	 * @param sa
	 * @return
	 */
	public boolean doesRecordExist(SystemAccount sa){  

		
		Cursor cursor = null;

		try{
			cursor = getWritableDatabase().query(TABLE_SYSTEM, new String[]{"_id"}, "login_name=? and server_name =?",new String[] {sa.getLoginName(),sa.getServerName()},null,null,null);

			if(cursor.getCount() > 0){
				cursor.moveToFirst();
				sa.setId(cursor.getLong(0));
				return true;
			}else{
				return false;
			}

		}catch (SQLiteException e) {
			Log.e(TAG, "SQLiteException:" + e.getMessage());
			return false;
		}catch (Exception e){
			Log.e(TAG, "Exception:" + e.getMessage());
			return false;
		}finally{
			cursor.close();
		}
	}
	
	/**
	 * Does the record exist .
	 * @param id
	 * @return
	 */
	public boolean doesRecordExist(long id){

		Cursor cursor = null;

		try{
			cursor = getWritableDatabase().query(TABLE_SYSTEM, new String[]{"count(*)"}, "_id=?",new String[] {String.valueOf(id)},null,null,null);

			if(cursor.getCount() > 0){
				cursor.moveToFirst();
				if (cursor.getInt(0) > 0) { 
					return true;
				}else{
					return false;
				}
			}else{
				return false;
			}

		}catch (SQLiteException e) {
			Log.e(TAG, "SQLiteException:" + e.getMessage());
			return false;
		}catch (Exception e){
			Log.e(TAG, "Exception:" + e.getMessage());
			return false;
		}finally{
			cursor.close();
		}
	}
	
	public Cursor getAllRecords(){
		
		ArrayList<SystemAccount> allRecords = new ArrayList<SystemAccount>();
		
		String[] allColumns = {"*"};
		
		Cursor allRecordsCursor = getWritableDatabase().query(TABLE_SYSTEM, allColumns, null, null, null, null, null);
		
		return allRecordsCursor;
	}
	/**
	 * Returns all the System table records .
	 * @return
	 */
	public Cursor getSystemAccounts(){
		
		String[] allColumns = {"*"};
		
		Cursor allRecordsCursor = getWritableDatabase().query(TABLE_SYSTEM, allColumns, null, null, null, null, null);
		
		return allRecordsCursor;
    }
	
	/**
	 * Returns all the System table records .
	 * @return
	 */
	public Cursor getSystemAccounts(SearchQuery searchQuery){
		
		Cursor allRecordsCursor = null;
		try{
		
			String[] allColumns = {"*"};
		
			String whereString = searchQuery.queryDBString();
		
		
			String qry = "select * from "+TABLE_SYSTEM + " "+whereString ;
			allRecordsCursor = getWritableDatabase().rawQuery(qry,null);
			
			return allRecordsCursor;
			//Cursor allRecordsCursor = getWritableDatabase().query(TABLE_SYSTEM, allColumns, null, null, null, null, null);
		}catch(Exception ex){
			Log.d(TAG,"Caught Exception in  getSystemAccounts.."+ex.getMessage());
			return null;
		}
		
    }
	
	/**
	 * Returns a list of SystemAccounts Records wrapped in a SystemCursor
	 * Next step is to retrive from the cursor with convenience method geSystemAccountsList which return an ArrayList of SystemAccount
	 * @return
	 */
    public SystemCursor getSystemAccountList(){
		
		String[] allColumns = {"*"};
		
		Cursor allRecordsCursor = getWritableDatabase().query(TABLE_SYSTEM, allColumns, null, null, null, null, null);
		
		return new SystemCursor(allRecordsCursor);
    }
    
	/** Returns the System Account for the supplied account Id */
	public SystemCursor getSystemAccount(long accountId){
		
		Cursor allRecordsCursor = null;
		String[] allColumns = {"*"};
		String[] whereArgs = {String.valueOf(accountId)};

		allRecordsCursor = getWritableDatabase().query(TABLE_SYSTEM, allColumns, "_id = ?", whereArgs, null, null, null);

		return new SystemCursor(allRecordsCursor);
	}
	
	
	public int getSystemRecordCount(){
		
		String[] columns = {"count(*) records"};
		String count = "0";
		Cursor cursor = null;
		try{
			cursor = getWritableDatabase().query(TABLE_SYSTEM, columns, null, null, null, null, null,null);
		
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
             
				count = cursor.getString(0);
             
			}
			
		}catch (SQLiteException e) {
			Log.e(TAG, "SQLiteException:" + e.getMessage());
			return 0;
		}catch (Exception e){
			Log.e(TAG, "Exception:" + e.getMessage());
			return 0;
		}finally{
			cursor.close();
		}
		 return Integer.parseInt(count);
	}

	public void PopulateDummyData(){
		
	
	    ArrayList<SystemAccount> systemAccountsList = new ArrayList<SystemAccount>();
	    
		systemAccounts = new SystemAccount();
		
		
		systemAccounts.setExpired((short)1);
		systemAccounts.setLoginName("mohallo");
		systemAccounts.setUserName("mohallo");
		systemAccounts.setServerName("suznd263");
		systemAccounts.setApplication("MEAT");
		systemAccounts.setDescription("Old cvs server for OAS");
		systemAccounts.setLoginPassword("12345");
		systemAccounts.setDateCreated("2007-01-01 10:00:00");
		systemAccounts.setNotes("Old cvs server for OAS.This has expired");
		systemAccountsList.add(systemAccounts);
		
		systemAccounts = new SystemAccount();
		
		
		systemAccounts.setExpired((short)1);
		systemAccounts.setLoginName("mohallo");
		systemAccounts.setUserName("mohallo");
		systemAccounts.setServerName("doptltndev1");
		systemAccounts.setApplication("DOPT");
		systemAccounts.setLoginPassword("12345");
		systemAccounts.setDescription("Old DOPT Application Dev Server.Still contains source code");
		systemAccounts.setDateCreated("2014-01-01 10:00:00");
		systemAccounts.setNotes("Old doptltndev1 server for DOPT.This has expired");
		systemAccountsList.add(systemAccounts);
		
		systemAccounts = new SystemAccount();
		
		
		systemAccounts.setExpired((short)0);
		systemAccounts.setLoginName("mohallo");
		systemAccounts.setUserName("mohallo");
		systemAccounts.setServerName("ecomt122");
		systemAccounts.setApplication("OAS FOM");
		systemAccounts.setLoginPassword("12345");
		systemAccounts.setNotes("Ecom Machine Oas Deployments");
		systemAccounts.setDateCreated("2007-01-01 10:00:00");
		systemAccounts.setDescription("Ecom Machine Oas Deployments");
		systemAccountsList.add(systemAccounts);
		
        //return systemAccountsList;
		ContentValues contentValues;
		for(int index = 0;index < systemAccountsList.size();index++){
						
			SystemAccount sa = systemAccountsList.get(index);
			insertSystemRecord(sa);
		}
		
	}
	
	/**
	 * cleans the table of Records .
	 */
	public boolean truncateSystemTable(){
		Log.d(TAG,"truncateSystemTable()...Deleteing DB Table SYSTEM..");
		
		try{
		  getWritableDatabase().delete(TABLE_SYSTEM, null, null);
		}catch(Exception ex){
			Log.d(TAG,"Error Deleting contents of Database Table:"+TABLE_SYSTEM);
			return false;
		}
		
		return true;
		
	}
	
	/**
	 * Delete a single record .
	 * @param id
	 * @return
	 */
	public long deleteRecord(long id){
		
		String[] whereArgs = new String[] {String.valueOf(id)};
		long recordId = 0L;
		
		try{
			recordId = getWritableDatabase().delete(TABLE_SYSTEM, "_id=?", whereArgs);
			return recordId;
			
		}catch(Exception ex){
			Log.d(TAG,"Error Deleting Records from the Database Table:"+TABLE_SYSTEM);
			return 0L;
		}
	}
	
	/**
	 * Wrapper Classs 
	 * @author mohallo
	 *
	 */
    public static class SystemCursor extends CursorWrapper {
        
        public SystemCursor(Cursor c) {
            super(c);
        }
        
        /**
         * Returns a System Account object configured for the current row, or null if the current row is invalid.
         */
        public SystemAccount getSystemAccount() {
            if (isBeforeFirst() || isAfterLast())
                return null;
            
            SystemAccount systemAccount = new SystemAccount();
            
            systemAccount.setId(getLong(getColumnIndex(COLUMN_ACCOUNT_ID)));
            systemAccount.setUserName(getString(getColumnIndex(COLUMN_USER_NAME)));
            systemAccount.setServerName(getString(getColumnIndex(COLUMN_SERVER_NAME)));
            systemAccount.setApplication(getString(getColumnIndex(COLUMN_APPLICATION_NAME)));
            systemAccount.setDateCreated(getString(getColumnIndex(COLUMN_DATE_CREATED)));
            systemAccount.setLoginName(getString(getColumnIndex(COLUMN_LOGIN_NAME)));
            systemAccount.setLoginPassword(getString(getColumnIndex(COLUMN_LOGIN_PASSWORD)));
            systemAccount.setDescription(getString(getColumnIndex(COLUMN_DESCRIPTION)));
            systemAccount.setEnvironment(getString(getColumnIndex(COLUMN_ENVIRONMENT)));
            systemAccount.setNotes(getString(getColumnIndex(COLUMN_NOTES)));
            systemAccount.setExpired(getShort(getColumnIndex(COLUMN_EXPIRED)));

            return systemAccount;
            
        }
        
        //Returns a list of SystemAccounts 
        public ArrayList<SystemAccount> geSystemAccountsList(){
        	
        	 //if (isBeforeFirst() || isAfterLast())
             //    return null;
        	 
        	 ArrayList<SystemAccount> systemAccountsList = new ArrayList<SystemAccount>();
        	 
        	 SystemAccount systemAccount;
        	 
        	 moveToFirst();
        	 do{
        		 systemAccount = new SystemAccount();
        		 
        		 systemAccount.setId(getLong(getColumnIndex(COLUMN_ACCOUNT_ID)));
                 systemAccount.setUserName(getString(getColumnIndex(COLUMN_USER_NAME)));
                 systemAccount.setServerName(getString(getColumnIndex(COLUMN_SERVER_NAME)));
                 systemAccount.setApplication(getString(getColumnIndex(COLUMN_APPLICATION_NAME)));
                 systemAccount.setDateCreated(getString(getColumnIndex(COLUMN_DATE_CREATED)));
                 systemAccount.setLoginName(getString(getColumnIndex(COLUMN_LOGIN_NAME)));
                 systemAccount.setLoginPassword(getString(getColumnIndex(COLUMN_LOGIN_PASSWORD)));
                 systemAccount.setDescription(getString(getColumnIndex(COLUMN_DESCRIPTION)));
                 systemAccount.setEnvironment(getString(getColumnIndex(COLUMN_ENVIRONMENT)));
                 systemAccount.setNotes(getString(getColumnIndex(COLUMN_NOTES)));
                 systemAccount.setExpired(getShort(getColumnIndex(COLUMN_EXPIRED)));
                 
                 systemAccountsList.add(systemAccount);
                 
        	 }while(moveToNext());
        	
			 return systemAccountsList;
        	
        	
        }
    }
	
	

}
