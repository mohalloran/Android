package com.solutions.utils.component;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;

public class CommonTextWatcher implements TextWatcher{

	@Override
	public void afterTextChanged(Editable arg0) {
		setShareIntent();
		
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}
	
	public void setShareIntent(){}

}
