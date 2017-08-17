package com.solutions.systemaccountlogins;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.solutions.utils.component.CreateComponents;
import com.solutions.utils.component.MailSender;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	
	private static String TAG = "MainActivity";
	private static final String PREFS_FILE = "settings";
	private static final String PREFS_KEY = "username";
	
	private Button submitButton;
	//private Button registerButton;
	private EditText userNameEditText;
	
	private EditText userPasswordEditText;
	private String userNameValue;
	private String userPasswordValue;
	
	
	private String prefUserName; 
	private String prefPassword;
	private String prefEmail = "";
	private SystemAccountsLoginApplication systemAccountsLoginApplication;
	private SharedPreferences appPreferences;
	
	
	private AlertDialog.Builder dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		systemAccountsLoginApplication = SystemAccountsLoginApplication.getInstance();
		
		Log.d(TAG,"onCreate().....");
		
	    //this.getActionBar().setDisplayShowTitleEnabled(false);
		
		//Remove Preference entries for Testing only .
		appPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		displayPreferences();
		
		
        //Clear the Prefereneces
        //PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().clear().commit();
        
		
		submitButton = (Button) findViewById(R.id.submit_button);
		//registerButton = (Button) findViewById(R.id.register_button);
		userNameEditText = (EditText) findViewById(R.id.user_name);
		userPasswordEditText = (EditText) findViewById(R.id.user_password);
		
		if(savedInstanceState != null){
			userNameEditText.setText(savedInstanceState.getString("username").toString());
		}
		
		userNameEditText.setOnKeyListener(new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER){
					Log.d(TAG,"Tab Key pressed");
					userPasswordEditText.requestFocus();
					return true;
				}else{
					return false;	
					
				}
				
			}
		});
		
		submitButton.setOnClickListener(new View.OnClickListener(){
			
			@Override
			public void onClick(View v) {
				Log.d(TAG,"Submit Button Clicked");
				
				appPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
				prefUserName = appPreferences.getString("username", "");
				prefPassword = appPreferences.getString("password", "");
				prefEmail = appPreferences.getString("email", "");
				
				//Toast.makeText(MainActivity.this, "PrefUserName is:"+prefUserName, Toast.LENGTH_LONG).show();
				//Toast.makeText(MainActivity.this, "PrefPassword is:"+prefPassword, Toast.LENGTH_LONG).show();
				
				userNameValue = userNameEditText.getText().toString().trim();
				userPasswordValue = userPasswordEditText.getText().toString();
				
				systemAccountsLoginApplication = SystemAccountsLoginApplication.getInstance();
				
				if( (userNameValue != null && userNameValue.length() > 0) && userPasswordValue != null && userPasswordValue.length() > 0 ){
				  if( (prefUserName != null && prefUserName.length() > 0) && (prefPassword != null && prefPassword.length() > 0 ) && (prefEmail != null && prefEmail.length() > 0)) {
					
				    //Validate the user and password entered are correct .
				    if(userNameValue.equalsIgnoreCase(prefUserName) && userPasswordValue.equalsIgnoreCase(prefPassword)){
					 if(systemAccountsLoginApplication != null){
						systemAccountsLoginApplication.setUserName(prefUserName);
						systemAccountsLoginApplication.setPassword(prefPassword);
						systemAccountsLoginApplication.setEmailAddress(prefEmail);
						
						//Go to AccountListActivity displays a list of Accounts
						Intent accountListIntent = new Intent(MainActivity.this,AccountsListActivity.class);
						startActivity(accountListIntent);
					 }
				    }else{
				    	AlertDialog alertDialog = CreateComponents.createAlertDialog(MainActivity.this,R.string.username_password_dontmatch);
						alertDialog.show();
				    }
				 }else{
					 //No Preferences created .
					 AlertDialog alertDialog = CreateComponents.createAlertDialog(MainActivity.this,R.string.no_preferences_created);
					 alertDialog.show();

				 }
				}else{
					//Dialog Alert must enter values .
					AlertDialog alertDialog = CreateComponents.createAlertDialog(MainActivity.this,R.string.invalid_username_password);
					alertDialog.show();
				}
			}
		});
		
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		appPreferences = PreferenceManager.getDefaultSharedPreferences(this);  
		String userName = appPreferences.getString(PREFS_KEY, "");
		
		/**
		if(userName != null && userName.length() > 0){
			
			MenuItem item = menu.findItem(R.id.action_register);
			item.setTitle(R.string.registered);
			item.setEnabled(false);
		}
		*/
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()){
			case R.id.action_register:
				Log.d(TAG,"Register Button Clicked");
                Intent registerIntent = new Intent(MainActivity.this,AppPreferenceActivity.class);
                startActivityForResult(registerIntent,0);
				
                return true;
            default:return super.onOptionsItemSelected(item);
		}
		
		
	}
	
	@Override
	protected void onActivityResult(int requestCode,int resultCode,Intent intentData){
		
		Log.d(TAG,"Got a Result.................");  
		boolean preferencesCreated = false;
		preferencesCreated = intentData.getBooleanExtra(AppPreferenceActivity.PREFS_CREATED, false);
		appPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		if(preferencesCreated){
			
			Log.d(TAG,"Got a Result of True:"+preferencesCreated);
			
			Thread th = new Thread(new Runnable(){
			
				public void run(){
					
					String userName = appPreferences.getString("username","");
					String password = appPreferences.getString("password","");
					String email = appPreferences.getString("email","");
					String mailHost = appPreferences.getString("outgoingmailserverhost","");
					
					String emailPassword = appPreferences.getString("emailpassword",""); 
					String subject = getResources().getString(R.string.email_subject_text);

					
					
					//Create Email and send .
					try{
						MailSender sender = new MailSender(email, emailPassword,mailHost);
						sender.sendMail(subject,   
							"User:"+userName+"\n"+"Password:"+password ,   
							email,   //sender
							email);  //recipient
					}catch(Exception ex){
						AlertDialog alertDialog = CreateComponents.createAlertDialog(MainActivity.this,R.string.username_password_dontmatch);
						alertDialog.show();
				
						Log.d(TAG,"Error Sending Mail to User:"+ex.getMessage());
					}
				}
			});
			th.start();
			
			
			//Send Notification 
			sendNotification();
			
		}else{
			AlertDialog alertDialog = CreateComponents.createAlertDialog(MainActivity.this,R.string.missing_preferences);
			alertDialog.show();
		}
	}
	
	public void displayPreferences(){
		
        HashMap<String,String> preferenceMap = (HashMap) appPreferences.getAll();
        
        //For Testing purposes .
        for(Entry<String,String> entry:preferenceMap.entrySet()){
        	
        	Log.d(TAG,entry.getKey()+","+entry.getValue());
        
        	
        }
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedBundle){
		super.onSaveInstanceState(savedBundle);
		
		savedBundle.putString("username",userNameEditText.getText().toString());
		
		Log.d(TAG,"onSaveInstanceState");
	}
	
	@Override
	public void onRestart(){
		super.onRestart();
		Log.d(TAG,"onRestart()");
	}
		
	@Override
	public void onStop(){
		super.onStop();
		Log.d(TAG,"onStop()..");
	}
	
	@Override
	public void onPause(){
		super.onPause();
		Log.d(TAG,"onPause()....");
	}
	
	public void onResume(){
		super.onResume();
		Log.d(TAG,"onResume()....");
		
		if(systemAccountsLoginApplication != null)
			userNameEditText.setText(systemAccountsLoginApplication.getUserName());
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		Log.d(TAG,"onDestroy()...");
		
	}
	
	public void sendNotification(){
		
		Intent intentResultActivity = new Intent(this, ResultActivity.class);
		//Intent intent = new Intent(Intent.ACTION_VIEW);
		//Intent intent = new Intent(Intent.CATEGORY_APP_EMAIL);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		//intent.addCategory(Intent.CATEGORY_APP_EMAIL);

		PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
		PendingIntent pIntentResultActivity = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intentResultActivity, 0);
		
		Notification n  = new Notification.Builder(this)
        .setContentTitle("New Registration mail sent to  " + "mohallousa@yahoo.com")
        .setContentText("Registration Info")
        .setSmallIcon(R.drawable.ic_action_location_found)
        .setContentIntent(pIntent)
        .setAutoCancel(true)
        .addAction(R.drawable.ic_action_location_found, "Call", pIntent)
        .addAction(R.drawable.ic_action_location_found, "More .Result Activity", pIntentResultActivity)
        .addAction(R.drawable.ic_action_location_found, "And more", pIntent).build();
		
		NotificationManager notificationManger =
				(NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		notificationManger.notify(0, n);
		
	}
}
