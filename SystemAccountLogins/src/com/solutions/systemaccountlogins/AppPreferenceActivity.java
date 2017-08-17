package com.solutions.systemaccountlogins;

import java.util.HashMap;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;


public class AppPreferenceActivity extends PreferenceActivity {
	
	private static final String TAG = "AppPreferenceActivity";
	private static SharedPreferences appPreferences;
	public static final String PREFS_CREATED = "prefscreated";
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		getFragmentManager().beginTransaction().replace(android.R.id.content, new AppPreferenceFragment()).commit();

    }

	@Override
	public void onResume(){
		super.onResume();

	}
	
	@Override
	public void onPause(){
		super.onPause();
		
		Log.d(TAG,"onPause() we should be finished....");
		appPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		displayPreferences();

	}
	
	@Override
	public void onBackPressed(){
		super.finish();
		Log.d(TAG,"Finishing up .Dont know when this is called but lets see");
		
		NavigateBack();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:

			NavigateBack();
			
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Navigates back to the previous screen whether it is the back buttor or the Up pressed.
	 */
	public void NavigateBack(){
		
		boolean preferencesCreated = checkForPreferences();
		
		Intent data = new Intent();
		data.putExtra(PREFS_CREATED, preferencesCreated);
		setResult(RESULT_OK,data);
		finish();
		
	}
	public static class AppPreferenceFragment extends PreferenceFragment
	{
		
		@Override
		public void onCreate(final Bundle savedInstanceState){
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.settings);
			
			Log.d(TAG,"onCreate() AppPreferenceFragment.....");
		}
		
		@Override
		public void onPause(){
			super.onPause();
			
		}
		
		@Override
		public void onStop(){
			super.onStop();
			Log.d(TAG,"onStop() we should be out of foucus....");
			
		}
		
		@Override
		public void onDestroy(){
			super.onDestroy();
			Log.d(TAG,"onDestroy() we should be cleaning up....");
			
		}
		
	}
	
	/**
	 * Displays the preferences
	 */
	public static void displayPreferences(){
		
        HashMap<String,String> preferenceMap = (HashMap) appPreferences.getAll();
        
        Log.d(TAG,"Displaying preferences in AppPreferenceActivity");
        //For Testing purposes .
        for(Entry<String,String> entry:preferenceMap.entrySet()){
        	
        	Log.d(TAG,entry.getKey()+","+entry.getValue());

        }
	}
	
	/**
	 * Check the main preferences exist .Must include username,password,email .
	 * @return
	 */
	public boolean checkForPreferences(){
		
		boolean userNameExists = false;
		boolean passwordExists = false;
		boolean emailExists = false;
		
		appPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		HashMap<String,String> preferenceMap = (HashMap) (appPreferences != null ?appPreferences.getAll():null);
		
		Log.d(TAG,"Checking for preferenences..");
		
		if(preferenceMap == null)
           return false;
		
        for(Entry<String,String> entry:preferenceMap.entrySet()){
        	
        	Log.d(TAG,entry.getKey()+","+entry.getValue());
        	
        	switch(entry.getKey()){
        	case "username":
        		     if(entry.getValue() == null || entry.getValue().length() == 0)
        		    	 userNameExists = false;
        		     else
        		    	 userNameExists = true;
        		     
        		continue;
        		
        	case "password":
        			if(entry.getValue() == null || entry.getValue().length() == 0)
        				passwordExists = false;
        			else
        				passwordExists = true;
        			
        		continue;
        		
        	case "email":
        			if(entry.getValue() == null || entry.getValue().length() == 0)
        				emailExists = false;
        			else
        				emailExists = true;
        			
        		continue;
        	}

        }
        if(userNameExists && passwordExists && emailExists)
        	return true;
        else
        	return false;
	}
}
