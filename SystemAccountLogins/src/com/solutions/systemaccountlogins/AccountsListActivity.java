package com.solutions.systemaccountlogins;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class AccountsListActivity extends SingleFragmentActivity {

	private static final String TAG = "AccountsListActivity";
	public static final String PREF_SEARCH_QUERY = "searchQuery";

		
	/**
	 * android:launchMode="singleTop" set in Manifest .Prevents multiple instances of the Searchable Activity from being created .There will
	 * just be one on the back stack .
	 */
	@Override
	protected void onNewIntent(Intent intent) { 
		
	    setIntent(intent);//SetIntent set so that if you say getIntent() in the future you will know it was a search .
	    handleIntent(intent);
	}
	
	private void handleIntent(Intent intent) {
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	    	
	      AccountsListFragment accountListFragment = (AccountsListFragment) this.getFragmentManager().findFragmentById(R.id.fragmentContainer);
	      
	      String queryValue = intent.getStringExtra(SearchManager.QUERY);
	      Log.d(TAG,"Search string is "+queryValue);
	      
	      //put value in Preferences will have for later on .
	      SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
	      preferences.edit().putString(PREF_SEARCH_QUERY, queryValue).commit();

	      
	      accountListFragment.updateList(queryValue);
	    }
	}
	
	@Override
	protected Fragment createFragment() {
		
		Log.d(TAG,"createFragment()");
		return AccountsListFragment.createInstance();
		
	}

	
}
