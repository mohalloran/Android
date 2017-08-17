package com.solutions.systemaccountlogins;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;



public abstract class SingleFragmentActivity extends Activity {
	
    protected abstract Fragment createFragment();
   
    
    @Override
    public void onCreate(Bundle savedInstanceState){
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_fragment);

    	FragmentManager manager = getFragmentManager();
    	Fragment fragment = manager.findFragmentById(R.id.fragmentContainer);
    	
    	if(fragment == null){
    		fragment = createFragment();//Create a fragment implementation in extended class.
    		manager.beginTransaction().add(R.id.fragmentContainer, fragment).commit();//Add it to the container R.id.fragmentContainer.
    	}
    	
    	
    	
    	
    }
}

