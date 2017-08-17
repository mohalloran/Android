package com.solutions.utils.component;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.solutions.systemaccountlogins.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;

public class DatePickerFragment extends DialogFragment{
	
	public static final String TAG = "DatePickerFragment";
	public static final String EXTRA_DATE = "com.solutions.utils.component.date";
	
	
	private Date mDate;
	
	
	public static DatePickerFragment newInstance(Date date){
		
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_DATE, date);
		
		DatePickerFragment datePickerFragment = new DatePickerFragment();
		datePickerFragment.setArguments(args);
		
		return datePickerFragment;
		
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		
		Log.d(TAG,"Creating Dialog.........");
		
		mDate = (Date)this.getArguments().getSerializable(EXTRA_DATE);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(mDate);
		int year = calendar.get(Calendar.YEAR);
		final int month = calendar.get(Calendar.MONTH);
		final int day = calendar.get(Calendar.DAY_OF_MONTH);
		
		//inflate the view
		View dialogDateView = getActivity().getLayoutInflater().inflate(R.layout.dialog_date,null);
		DatePicker datePicker = (DatePicker) dialogDateView.findViewById(R.id.dialog_date_datePicker);
		datePicker.init(year, month, day ,new OnDateChangedListener(){

			@Override
			public void onDateChanged(DatePicker datePickerView, int year, int month,
					int day) {
				
                Log.d(TAG,"Date has changed");
				
				mDate = new GregorianCalendar(year,month,day).getTime();
				
				//getArguments().putSerializable(EXTRA_DATE,mDate);
			}
			
		});
		
		return new AlertDialog.Builder(getActivity())
        	.setView(dialogDateView)
        	.setTitle(R.string.created_date_Text)
        	.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {
			
        		@Override
        		public void onClick(DialogInterface dialog, int which) {
				
        			Log.d(TAG,"DatePickerFragment Dialog Button clicked");
				
        			sendResult(Activity.RESULT_OK);
				
        		}
		})
        .create();
   
	}
	
	private void sendResult(int resultCode){
		
		if(getTargetFragment() == null){
			return;
		}
		
		Intent intent = new Intent();
		intent.putExtra(EXTRA_DATE,mDate);
		
		getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
		
	}
	

}
