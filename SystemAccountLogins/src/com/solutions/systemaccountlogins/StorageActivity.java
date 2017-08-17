package com.solutions.systemaccountlogins;

import com.solutions.systemaccountlogins.storage.AccountStorageFragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class StorageActivity extends SingleFragmentActivity {

	
	@Override
	protected Fragment createFragment() {
		
		return AccountStorageFragment.createInstance();
		
		
	}

	
}
