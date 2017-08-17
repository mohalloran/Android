package com.solutions.systemaccountlogins;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class AccountDetailActivity extends SingleFragmentActivity {
	
	private static final String TAG = "AccountDetailActivity";
	private long accountId;

	@Override
	protected Fragment createFragment() {
		
		accountId = getIntent().getLongExtra(AccountsListFragment.ACCOUNT_ID, -1);
		
		return AccountDetailsFragment.createInstance(accountId);
	}

	
	
}
