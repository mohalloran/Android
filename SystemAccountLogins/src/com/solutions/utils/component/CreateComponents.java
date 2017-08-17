package com.solutions.utils.component;

import com.solutions.systemaccountlogins.MainActivity;
import com.solutions.systemaccountlogins.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class CreateComponents {
	
	/**
	 * Creates AlertDialog
	 * @param context
	 * @param messageId
	 * @return
	 */
	public static AlertDialog createAlertDialog(Context context,int messageId){
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
	    alertDialogBuilder.setMessage(messageId);
	    alertDialogBuilder.setPositiveButton("Ok",
	        new DialogInterface.OnClickListener() {

	            @Override
	            public void onClick(DialogInterface arg0, int arg1) {
	        }
	    });
	    AlertDialog alertDialog = alertDialogBuilder.create();
		
	    return alertDialog;
		
	}
	
	public static AlertDialog createOkCancelDialog(Context context,int messageId){
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		
		alertDialogBuilder.setMessage(messageId);
	    alertDialogBuilder.setPositiveButton("Ok",
	        new DialogInterface.OnClickListener() {

	            @Override
	            public void onClick(DialogInterface dialog, int which) {}
	    }).
	    setNegativeButton("Cancel",
		        new DialogInterface.OnClickListener() {

		            @Override
		            public void onClick(DialogInterface dialog, int which) {
		            	
		            }
		    });
	    AlertDialog alertDialog = alertDialogBuilder.create();
		
	    return alertDialog;
		
		
	}
	
	public static AlertDialog createOkCancelDialog(Context context, int title, int messageId,
            DialogInterface.OnClickListener okListener, 
            DialogInterface.OnClickListener cancelListener) {
		
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
			alertDialogBuilder.setTitle(title);
			alertDialogBuilder.setMessage(messageId);
			alertDialogBuilder.setPositiveButton("OK", okListener);
			alertDialogBuilder.setNegativeButton("Cancel", cancelListener);
			AlertDialog alertDialog = alertDialogBuilder.create();
			return alertDialog;
	}

}
