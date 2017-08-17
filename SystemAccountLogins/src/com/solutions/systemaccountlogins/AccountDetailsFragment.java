package com.solutions.systemaccountlogins;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import com.solutions.systemaccountlogins.constants.Constants;
import com.solutions.systemaccountlogins.constants.SearchQuery;
import com.solutions.systemaccountlogins.constants.SearchQuery.Column;
import com.solutions.systemaccountlogins.dao.SystemAccount;
import com.solutions.systemaccountlogins.manager.SystemAccountsManager;
import com.solutions.systemaccountlogins.utils.CreatePassword;
import com.solutions.utils.component.CommonTextWatcher;
import com.solutions.utils.component.CreateComponents;
import com.solutions.utils.component.DatePickerFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ShareActionProvider;
import android.widget.ShareActionProvider.OnShareTargetSelectedListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AccountDetailsFragment extends Fragment{
	
	private static final String TAG = "AccountDetailFragment";
	private static final String ARG_ACCOUNT_ID = "ACCOUNT_ID";
	private static final int REQUEST_DATE = 0;
	private static final String DIALOG_DATE = "date";
	private ShareActionProvider sharedActionProvider;
	private static final String SHARE_HISTORY_FILE_NAME = "accountdata";
	
	private CommonTextWatcher commonTextWatcher;
	
	
	
	private static AccountDetailsFragment accountDetailsFragment;
	private SystemAccountsManager systemAccountsManager;
	private SystemAccount systemAccount;
	private Button createDateButton ;
	private TextView createDateEditText;
	private TextView displayNotes;
	private LinearLayout toggleNotes;
	private Button saveButton;
	private Button cancelButton;
	
	private TextView userNameTextView;
	private EditText applicationNameEditText;
	private EditText serverNameEditText;
	private EditText serverLoginIdEditText;
	private EditText serverLoginPasswordEditText;
	private Spinner environmentSpinner;
	private TextView createDateTextView;
	private CheckBox expiredCheckBox;
	private EditText descriptionEditText;
	private EditText notesEditText;
	private ImageView passGeneratorImage;
	
	ArrayAdapter<String> environmentAdapter;
	
	MenuItem menuItem;
	
	
	
	
	public static Fragment createInstance(long accountId){
		
		Log.d(TAG,"Displaying AccountDetails Fragment for Account:"+accountId);
		
		Bundle args = new Bundle();
		args.putLong(ARG_ACCOUNT_ID, accountId);
		accountDetailsFragment = new AccountDetailsFragment();
		accountDetailsFragment.setArguments(args);
		
		return accountDetailsFragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setRetainInstance(true);
        systemAccountsManager = SystemAccountsManager.getInstance(getActivity());
        
        commonTextWatcher = new CommonTextWatcher(){
        	
        	@Override
        	public void setShareIntent(){
        		Log.d(TAG,"Setting shared");
        		Intent intent = setIntent("Login Account Details");
        		setCommonShareIntent(intent);
        	}

			
        };
		
        setHasOptionsMenu(true);
        
        
		Bundle args = getArguments();
		long accountId = (long) args.get(ARG_ACCOUNT_ID);
		if(accountId != -1){//-1 create a brand new form .
			Log.d(TAG,"onCreate() Account:"+accountId);
			
			systemAccount = systemAccountsManager.getSystemAccount(accountId);
			
			Log.d(TAG,"SystemAccount Server is:"+systemAccount.getServerName());
			
			
		}
		
		environmentAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,android.R.id.text1,Constants.Environment.getEnviromentNames());
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup parent,Bundle savedInstanceState){
		
		View accountDetailFragment = inflater.inflate(R.layout.account_detail_fragment, parent,false);
		
		userNameTextView = (TextView)accountDetailFragment.findViewById(R.id.user_name);
		userNameTextView.setText(SystemAccountsLoginApplication.getInstance().getUserName());
		
		applicationNameEditText = (EditText) accountDetailFragment.findViewById(R.id.application_name);
		applicationNameEditText.addTextChangedListener(commonTextWatcher);
		
		serverNameEditText = (EditText) accountDetailFragment.findViewById(R.id.server_name);
		serverNameEditText.addTextChangedListener(commonTextWatcher);
		
		serverLoginIdEditText = (EditText)accountDetailFragment.findViewById(R.id.server_login_id);
		serverLoginIdEditText.addTextChangedListener(commonTextWatcher);
		
		serverLoginPasswordEditText = (EditText)accountDetailFragment.findViewById(R.id.server_login_password);
		serverLoginPasswordEditText.addTextChangedListener(commonTextWatcher);

		passGeneratorImage = (ImageView) accountDetailFragment.findViewById(R.id.image_generator);
		
		environmentSpinner = (Spinner)accountDetailFragment.findViewById(R.id.environment);
		environmentSpinner.setAdapter(environmentAdapter);
		environmentSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View view,
					int arg2, long arg3) {
				
				Intent intent = setIntent("Login Account Details");
        		setCommonShareIntent(intent);

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		createDateTextView = (TextView)accountDetailFragment.findViewById(R.id.created_date);
		expiredCheckBox = (CheckBox)accountDetailFragment.findViewById(R.id.expired);
		descriptionEditText = (EditText)accountDetailFragment.findViewById(R.id.description);
		notesEditText = (EditText)accountDetailFragment.findViewById(R.id.notes);
		
		
		createDateEditText = (TextView) accountDetailFragment.findViewById(R.id.created_date);
		createDateEditText.setText( (new Date()).toString());
		
		
		createDateButton = (Button) accountDetailFragment.findViewById(R.id.created_date_button);
		
		displayNotes = (TextView) accountDetailFragment.findViewById(R.id.display_notes);
		
		toggleNotes =  (LinearLayout)accountDetailFragment.findViewById(R.id.toggle_notes);
		
		saveButton = (Button)accountDetailFragment.findViewById(R.id.save_button);
		cancelButton = (Button)accountDetailFragment.findViewById(R.id.cancel_button);
		
		
		passGeneratorImage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d(TAG,"Image clicked going to genereate Number");
				
				CreateComponents.createOkCancelDialog(getActivity(), R.string.ok_cancel_generate_dialog, R.string.alert_generate_password,
			            new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								
								Log.d(TAG,"OK Button clicked.....");
								
								//present dialog if you are sure you want to change your password.
								String password = CreatePassword.generate();
								if(password != null && password.length() > 0)
								   serverLoginPasswordEditText.setText(password);
								
								
							}
					
						}, 
			            new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Log.d(TAG,"Cancel Button clicked.....");
								return;
							}
					
						}
					
				).show();

			}
		});
		
		
		createDateButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d(TAG,"CreateDateButton clicked..");
				
				FragmentManager fragmentManager = getActivity().getFragmentManager();
				
				DatePickerFragment datePickerDialog = DatePickerFragment.newInstance(new Date());
				
				//Make AccountDetailsFragment the target Fragment of DatePickerFragment.Create a conversation between the two .
				//The FragmentManager keeps track of Target Fragment and request code
				//Request Code returned in onActivityResult.Check for this request code
				//Allows communication back here from the Dialog .x
				datePickerDialog.setTargetFragment(AccountDetailsFragment.this, REQUEST_DATE);
				
				
				//the DIALOG_DATE uniquely identifies the DialogFragment in the Fragment Manager
				//Display the DatPickerFragment and add it to the Fragmentmanager
				datePickerDialog.show(fragmentManager, DIALOG_DATE);
				
			}
		});
		
		displayNotes.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
				if(toggleNotes.isShown()){
					toggleNotes.setVisibility(View.GONE);
					displayNotes.setText(getResources().getString(R.string.display_notes));
				}else{
					toggleNotes.setVisibility(View.VISIBLE);
					displayNotes.setText(getResources().getString(R.string.hide_notes));
				}
				
			}
		});
		
		/**
		 * Save form to the database
		 */
		saveButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d(TAG,"Save Button Clicked");
				
				long recordId =0;
				boolean creatingNewRecord = false;
				
				if(!validateEntries()){
					 AlertDialog alertDialog = CreateComponents.createAlertDialog(getActivity(),R.string.missing_details);
					 alertDialog.show();
					 
					 return;  
				}
				
				
				if(systemAccount == null){
					creatingNewRecord = true;
					systemAccount = new SystemAccount();
				}else{
					creatingNewRecord = false;
				}
				
				systemAccount.setUserName(userNameTextView.getText().toString());
				systemAccount.setApplication(applicationNameEditText.getText().toString());
				systemAccount.setServerName(serverNameEditText.getText().toString());
				systemAccount.setLoginName(serverLoginIdEditText.getText().toString());
				systemAccount.setLoginPassword(serverLoginPasswordEditText.getText().toString());
				systemAccount.setEnvironment(Long.toString(environmentSpinner.getSelectedItemId()));
				systemAccount.setDateCreated(createDateTextView.getText().toString());
				systemAccount.setExpired(expiredCheckBox.isChecked() == true ? (short) 1:(short) 0);
				systemAccount.setDescription(descriptionEditText.getText().toString());
				systemAccount.setNotes(notesEditText.getText().toString());
				

				//check if the record exists in the Database
				if(creatingNewRecord)
    				recordId = systemAccountsManager.insertSystemAccount(systemAccount);
				else
				    recordId = systemAccountsManager.updateSystemRecord(systemAccount);
				
				setCommonShareIntent(setIntent("Login Account Details"));
				
				Toast.makeText(getActivity(), "Inserted Record:"+recordId +" Into the database", Toast.LENGTH_SHORT).show();
				
				
			}
		});
		
		/**
		 * Cancel return to Accounts List
		 */
		cancelButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d(TAG,"Cancel Button Clicked");
				
				Intent accountListIntent = new Intent(getActivity(),AccountsListActivity.class);
				startActivity(accountListIntent);
				
			}
		});
		
		if(systemAccount != null)
		   updateUI();
		
		return accountDetailFragment;
		
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu,MenuInflater inflater){
		
		inflater.inflate(R.menu.account_detail,menu);
		menuItem = (MenuItem) menu.findItem(R.id.action_share);
		sharedActionProvider = (ShareActionProvider) menuItem.getActionProvider();
		sharedActionProvider.setShareHistoryFileName(SHARE_HISTORY_FILE_NAME);
		sharedActionProvider.refreshVisibility();

		sharedActionProvider.setOnShareTargetSelectedListener(new OnShareTargetSelectedListener() {
			
			@Override
			public boolean onShareTargetSelected(ShareActionProvider source,
					Intent intent) {
				Log.d(TAG,"sharedActionProvider Item selected");
				
				//Will cause onCreateOptionsMenu to be called again.
                getActivity().invalidateOptionsMenu();
				
				return false;   
			}
		});
		sharedActionProvider.setShareIntent(setIntent("Login Account Details"));

	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		
		Log.d(TAG,"Menu Option is "+item.getItemId());
		
		switch(item.getItemId()){
		
		  
		
		case R.id.menu_item_search:
			Log.d(TAG,"Search Requested..............");
			getActivity().onSearchRequested();//Launch search opens search Dialog.
			return true;
		
		}
		return false;
		
	}

	/**
	 * populate the shareActionProvider with the created Explicit intent .
	 * @param text
	 */
	private Intent setIntent(String subjecText){

		Intent  intent = new Intent(Intent.ACTION_SEND);
		
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

		intent.setType("text/plain");
		
		String bodyContent = "Account:"+userNameTextView.getText().toString() +"\n Application:"+applicationNameEditText.getText().toString()
				                                                                                +"\nServer Name:"+serverNameEditText.getText().toString() 
				                                                                                +"\nLogin Id:"+serverLoginIdEditText.getText().toString()
                                                                            +"\nLogin Pass:"+serverLoginPasswordEditText.getText().toString();
                                                                           
		intent.putExtra(Intent.EXTRA_TEXT, bodyContent);

		String[] to = { SystemAccountsLoginApplication.getInstance().getEmailAddress() };
        String[] cc = { SystemAccountsLoginApplication.getInstance().getEmailAddress() };
        intent.putExtra(android.content.Intent.EXTRA_EMAIL, to);
		intent.putExtra(android.content.Intent.EXTRA_SUBJECT, subjecText);

		return intent;

	}
	
	/**
	 * called when a component value changes to update the sharedActionProvider .
	 * @param shareIntent
	 */
	private void setCommonShareIntent(Intent shareIntent) {
		  if (sharedActionProvider != null) {
			  sharedActionProvider.setShareIntent(shareIntent); 
		  } 
	}
	
	
	/**
	 * updates the UI 
	 */
	public void updateUI(){
		userNameTextView.setText(systemAccount.getUserName() == null || systemAccount.getUserName().length() == 0 ? SystemAccountsLoginApplication.getInstance().getUserName():systemAccount.getUserName() );
		
		applicationNameEditText.setText(systemAccount.getApplication());
		
		serverNameEditText.setText(systemAccount.getServerName());
		serverLoginIdEditText.setText(systemAccount.getLoginName());
		serverLoginPasswordEditText.setText(systemAccount.getLoginPassword());
		environmentSpinner.setSelection(Integer.parseInt(systemAccount.getEnvironment() == null ?"0":systemAccount.getEnvironment()));
		createDateTextView.setText(systemAccount.getDateCreated());
		expiredCheckBox.setChecked(systemAccount.getExpired() == 0?false:true);
		descriptionEditText.setText(systemAccount.getDescription());
		notesEditText.setText(systemAccount.getNotes());
		
	}
	
	/**
	 * Validate Entries .
	 * @return
	 */
	private boolean validateEntries(){
		Log.d(TAG,"Validating entries..........");
		
				
		if(applicationNameEditText.getText() == null || applicationNameEditText.getText().toString().length() == 0)
			return false;
		
		if(serverLoginIdEditText.getText() == null || serverLoginIdEditText.getText().toString().length() == 0)
			return false;
		
		if(serverLoginPasswordEditText.getText() == null || serverLoginPasswordEditText.getText().toString().length() == 0)
			return false;
		
		if(serverNameEditText.getText() == null || serverNameEditText.getText().toString().length() == 0)
			return false;
		
		
		return true;
		
	}
	
	@Override
	public void onActivityResult(int requestCode,int resultCode,Intent data){
		
		if(resultCode != Activity.RESULT_OK) return;
		
		if(requestCode == REQUEST_DATE){
			
			Date date = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
			createDateEditText.setText(date.toString());
			
			
		}
	}

}
