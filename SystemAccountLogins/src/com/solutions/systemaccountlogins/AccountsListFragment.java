package com.solutions.systemaccountlogins;

import java.util.ArrayList;


import java.util.HashMap;

import com.solutions.systemaccountlogins.constants.SearchQuery;
import com.solutions.systemaccountlogins.constants.SearchQuery.Column;
import com.solutions.systemaccountlogins.dao.SystemAccount;
import com.solutions.systemaccountlogins.database.SQLiteCursorLoader;
import com.solutions.systemaccountlogins.database.SystemAccountsDatabaseHelper;
import com.solutions.systemaccountlogins.manager.SystemAccountsManager;
import com.solutions.utils.component.CreateComponents;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ListFragment;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
//import android.graphics.Color;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
//import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
//import android.widget.Adapter;
//import android.widget.AdapterView;
import android.widget.AbsListView.MultiChoiceModeListener;
//import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
//import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Implements LoaderCallbacks interface which has three methods that must be implemented 
 * onCreateLoader,onLoadFinished,onLoaderReset.onCreateLoader calls SystemAccountsListCursorLoader which
 * implements SQLiteCursorLoader background Thread.The method loadCursor which is overridden is called and 
 * get the list Data and returns a Cursor which is returned to the method of the loader onLoadFinished which
 * creates the SystemAccountsCursorAdapter for the ListView .ListViews adapters is set to that Cursor .
 * onCreateView initiates the Loader from LoaderManager initLoader also the loader is reloaded when an item in the
 * listView is deleted getLoaderManager().restartLoader .
 * 
 * The listView also implements a long press on a list item .When this occurs a contextual action occurs giving the user
 * the ability to delete multiple items at once .The contextual action mode displays action items that affect the selected
 *  content in a bar at the top of the screen and allows the user to select multiple items.
 *  when the action mode is first created the method onCreateActionMode is called inflating an xml menu file which produces
 *  an image @android:drawable/ic_menu_delete .When clicked the event is captured in the method onActionItemClicked which
 *  removes the clicked items from the list .
 *  
 *  A normal click of an Item takes creates an Intent to the AccountDetailActivity Class .
 * 
 * @author mohallo
 *
 */
public class AccountsListFragment extends ListFragment implements LoaderCallbacks<Cursor>{
	
	private static final int RUN_LOADER_ID = 1;
	private static final int LOCATION_LOADER_ID = 2;
	private static final int ANOTHER_LOADER_ID = 3;
	
	public static final String ACCOUNT_ID = "account_id";
	
	private static final String TAG = "AccountsListFragment";
	private SystemAccountsCursorAdapter systemAccountsCursorAdapter;
	private SQLiteDatabase sqliteDatabase;
	private Cursor systemAccountsCursor;
	private SystemAccountsDatabaseHelper helper;
	private static SystemAccountsManager systemAccountsManager;
	private ListView listView;

	private LoaderCallbacks<Cursor> mCallbacks;
	
	private static SearchQuery searchQry = null;
	private AlertDialog alertDialog = null;
	private String searchQueryValue = null;
	
	
	public static Fragment createInstance(){
		
		return new AccountsListFragment();
	}

	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true);
		setRetainInstance(true);
		
		systemAccountsManager = SystemAccountsManager.getInstance(getActivity());
		
		mCallbacks = this;
		
		this.getActivity().getActionBar().setDisplayShowTitleEnabled(false);
		
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup parent,Bundle savedInstanceState){
		
		View v = super.onCreateView(inflater, parent, savedInstanceState);
		
		searchQry = new SearchQuery(Column.ALL,"");

		helper = new SystemAccountsDatabaseHelper(getActivity());
		sqliteDatabase = helper.getWritableDatabase();
		
		//parameters loader id,bundle ,callback interface .Calls onCreateLoader and then onLoaderFinished with the cursor which sets the listAdapter for the ListFragment
		getLoaderManager().initLoader(RUN_LOADER_ID, null, mCallbacks); 

		listView = (ListView) v.findViewById(android.R.id.list);
		listView.setItemsCanFocus(true);
		
		listView.setSelector(R.drawable.listview_selector);
		
		
		//Add Context Menu .Long press on List Item.Allow multiple selection
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {

			@Override
			public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {

				switch(menuItem.getItemId()){
				
				case R.id.menu_item_delete_system:
					//Log.d(TAG,"Deleting Account list item "+listView);
					systemAccountsCursorAdapter = (SystemAccountsCursorAdapter)getListAdapter();
					for (int i = systemAccountsCursorAdapter.getCount() - 1; i >= 0; i--) {
                        if (getListView().isItemChecked(i)) {
                        	Log.d(TAG,"Deleting Account list item "+systemAccountsCursorAdapter.getItemId(i));
                        	if(systemAccountsManager.doesRecordExist(systemAccountsCursorAdapter.getItemId(i))){
                        		
                        		long recordId = systemAccountsManager.deleteRecord(systemAccountsCursorAdapter.getItemId(i));
                        		
                        		if(recordId <= 0){
                        			Toast.makeText(getActivity(), getResources().getString(R.string.record_deletion_failure), Toast.LENGTH_LONG).show();
                        		}else{
                        			Toast.makeText(getActivity(),getResources().getString(R.string.record_deletion_success),Toast.LENGTH_LONG).show();
                        		}
                        	}
                        }
                    }
					 
					actionMode.finish(); 
					//parameters loader id,bundle ,callback interface .Reload the Data with the loader .
					getLoaderManager().restartLoader(RUN_LOADER_ID, null, mCallbacks);
					
                    return true;
                    
			    default:
			            return false;
				}
				
			}

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				MenuInflater inflater = mode.getMenuInflater();
				inflater.inflate(R.menu.system_list_context_menu,menu);

				return true;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
				Log.d(TAG,"Destroying Action Mode.......");

			}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onItemCheckedStateChanged(ActionMode actionMode, int position,
					long id, boolean checked) {
				
				Log.d(TAG,"Item clicked:"+position + " checked is  "+checked);  

				Log.d(TAG,"First visible position is "+listView.getFirstVisiblePosition()); 
				Log.d(TAG,"Last visible position is "+listView.getLastVisiblePosition());
				
				Log.d(TAG,"postion - firstVisible position is:"+(position - listView.getFirstVisiblePosition()));
				
				//listView.getChildAt(position - listView.getFirstVisiblePosition()).setBackgroundColor(getResources().getColor(R.color.sa_green));
				
				/**
				if(listView.getChildAt(position - listView.getFirstVisiblePosition()) != null){
				 if(checked){
					
					listView.getChildAt(position - listView.getFirstVisiblePosition()).setBackgroundColor(getResources().getColor(R.color.sa_green)); 
				 }else{
					listView.getChildAt(position - listView.getFirstVisiblePosition()).setBackgroundColor(getResources().getColor(R.color.lightGray));
					
				 }
				}
				*/
				
				//Register the clicked and unregister the unclicked in the selection hashmap.later these selected items backgrounds will be changed
				if(checked){
					systemAccountsCursorAdapter.setNewSelection(position, checked);
				}else{
					//listView.getChildAt(position - listView.getFirstVisiblePosition()).setBackgroundColor(getResources().getColor(R.color.lightGray));
					systemAccountsCursorAdapter.removeSelection(position);
				 }
				
				systemAccountsCursorAdapter.notifyDataSetChanged();
				
			}
			
		});
		
		
		

		return v;
	}
	
	
		
	@Override
	public void onListItemClick(ListView l,View v,int position,long id){
		
		//Toast.makeText(getActivity(), "Row clicked is "+id, Toast.LENGTH_SHORT).show();
		
			
		
		Intent accountDetailIntent = new Intent(getActivity(),AccountDetailActivity.class);

		accountDetailIntent.putExtra(ACCOUNT_ID, id);
		startActivity(accountDetailIntent);
		
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.accounts_list, menu);
		
		
		 // Enable Search SearchView. Get the SearchView and set the searchable configuration.
		 // config data comes from the searchable.xml file defined in the manifest for the Activity.
		 MenuItem searchItem = menu.findItem(R.id.menu_item_search);
         final SearchView searchView = (SearchView)searchItem.getActionView();

         
         SearchManager searchManager = (SearchManager)getActivity()
             .getSystemService(Context.SEARCH_SERVICE);
         ComponentName name = getActivity().getComponentName();
         //all the info about a search and the Activity that should receive the Intent and
         //every thing in your searchable.xml file is in the searchInfo.
         SearchableInfo searchInfo = searchManager.getSearchableInfo(name);

         searchView.setSearchableInfo(searchInfo);
         
         searchView.setOnSearchClickListener(new View.OnClickListener() {
 			
 			@Override
 			public void onClick(View v) {
 				
 				Log.d(TAG,"Search View clicked....");
 				
 				if(searchQry == null || searchQry.column.toString().equalsIgnoreCase("ALL")){
 				  searchView.onActionViewCollapsed();
 				  alertDialog = CreateComponents.createAlertDialog(getActivity(),R.string.select_search_criteria);
				  alertDialog.show();
 				}else{
 					Log.d(TAG,searchQry.column.toString());
 					searchView.setQueryHint(searchQry.column.getDescriptiveName());
 				}
 				
 			}
 		});
		
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()){
		
		case R.id.menu_item_search:
			Log.d(TAG,"Search Requested..............");
			getActivity().onSearchRequested();//Launch search opens search Dialog.
			return true;
		
		case R.id.menu_item_populate_system:
			Log.d(TAG,"Register Button Clicked"); 
			new InsertSystemTask().execute(1);
            return true;
		case R.id.menu_item_new_account:
			Log.d(TAG,"Creating new Account");
			
			//Create empty Detail form 
			Intent accountDetailIntent = new Intent(getActivity(),AccountDetailActivity.class);
			startActivity(accountDetailIntent);
			
			return true;
			
		case R.id.application_name:
			
			Log.d(TAG,"Searching by Application Name");
			searchQry = new SearchQuery(Column.APPLICATIONNAME,"");
			return true;
		case R.id.server_name:
			Log.d(TAG,"Searching by Server Name");
			searchQry = new SearchQuery(Column.SERVERNAME,"");
			return true;
			
		case R.id.menu_item_storage:
			Log.d(TAG,"Showing Storage Screen");
			Intent storageIntent = new Intent(getActivity(),StorageActivity.class);
			startActivity(storageIntent);
			
			return true;
		case R.id.menu_item_refresh:
			Log.d(TAG,"Refreshing List view");
			searchQry = new SearchQuery(Column.ALL,"");
			getLoaderManager().restartLoader(RUN_LOADER_ID, null, mCallbacks);
            return true;
            
            default:return super.onOptionsItemSelected(item);
		}
	
		
	}
	/**
	 * Initially created an ArrayAdapter .Replaced with cursor Adapter as seen below .
	 * @author mohallo
	 *
	 */
	public class SystemAccountsAdapter extends ArrayAdapter<SystemAccount>{

		SystemAccount systemAccount;
		TextView textViewServerName;
		TextView textViewDescription;
		CheckBox checkBox;
		
		public SystemAccountsAdapter(ArrayList<SystemAccount> systemAccounts) {
			super(getActivity(), 0,systemAccounts);

		}
		
		@Override
		public View getView(int position,View convertView,ViewGroup parent){
			
			if(convertView == null){
			   convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_account, null);
			}
			systemAccount = getItem(position);
			
			textViewServerName = (TextView) convertView.findViewById(R.id.system_account_servername_textview);
			textViewServerName.setText(systemAccount.getServerName());
			
			textViewDescription = (TextView) convertView.findViewById(R.id.system_account_description_textview);
			textViewDescription.setText(systemAccount.getDescription());
			
			checkBox = (CheckBox) convertView.findViewById(R.id.system_account_expired_checkbox);
			checkBox.setChecked(systemAccount.getExpired() == 0?false:true);
			
			
			
			return convertView;
			
		}

	}
	
	/**
     * Loader which interfaces with the RunManager and Database .New Thread created for Long running tasks
     * @author mohallo
     *
     */
    private static class SystemAccountsListCursorLoader extends SQLiteCursorLoader {
 
    	public SystemAccountsListCursorLoader (Context context){
    		super(context);
    		
    	}
    	
    	
    	/**
    	 * LoadInBackground of SQLiteCursorLoader runs this overriding method .
    	 */
    	@Override
    	protected Cursor loadCursor(){
    		
    		return systemAccountsManager.getSystemAccounts(searchQry);
    		
    		
    	}
    }
    
    
	
	/**
	 * Replaces ArrayAdapter as shown above .Accepts a CursorAdapter which was created from call to the Database .
	 * @author mohallo
	 *
	 */

	private class SystemAccountsCursorAdapter extends CursorAdapter {
		
		TextView textViewServerName;
		TextView textViewDescription;
		TextView textViewApplicationName;
		CheckBox checkBox;
		
		private HashMap<Integer, Boolean> selection = new HashMap<Integer, Boolean>();
		
		public SystemAccountsCursorAdapter(Context context, Cursor cursor, int flags) {
			super(context, cursor, flags);
			
		}
		
		/**
		 * Sets selected values for Context Menu on long click when context menu is enabled
		 * @param position
		 * @param value
		 */
		public void setNewSelection(int position, boolean value) {
            selection.put(position, value);
            notifyDataSetChanged();
        }
		
		/**
		 * Removes values to be deleted when context menu in enabled
		 * @param position
		 */
		public void removeSelection(int position) {
                        
            selection.remove(position);
            notifyDataSetChanged();
        }
		
		
		@Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);//let the adapter handle setting up the row views
            
            v.setBackground(getResources().getDrawable(R.drawable.account_list_item));

            //HashMap kept to remember the selections made when scrolling up and down in the ListView
            if (selection.get(position) != null) {
                v.setBackgroundColor(getResources().getColor(R.color.sa_green));// this is a selected position so make it green
            }
            
            return v;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			
			textViewApplicationName = (TextView) view.findViewById(R.id.system_account_application_name_textview);
			textViewApplicationName.setText(cursor.getString(cursor.getColumnIndexOrThrow("APPLICATION_NAME"))); 
			
			textViewServerName = (TextView) view.findViewById(R.id.system_account_servername_textview);
			textViewServerName.setText(cursor.getString(cursor.getColumnIndexOrThrow("SERVER_NAME"))); 
			
			textViewDescription = (TextView) view.findViewById(R.id.system_account_description_textview);
			textViewDescription.setText(cursor.getString(cursor.getColumnIndexOrThrow("DESCRIPTION")));
			
			checkBox = (CheckBox) view.findViewById(R.id.system_account_expired_checkbox);
			//checkBox.setChecked(systemAccount.getExpired() == 0?false:true);
			Short expired = cursor.getShort(cursor.getColumnIndexOrThrow("EXPIRED"));
			checkBox.setChecked(expired == 0?false:true);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
						
			View convertView = LayoutInflater.from(context).inflate(R.layout.list_item_account, parent,false);
			
			convertView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					Log.d(TAG,"Focussing on Item.............");
					Toast.makeText(getActivity(), "Hovering over value",Toast.LENGTH_SHORT).show();
					
				}
			});
				

			return convertView;

		}
		
		
	}
	
	/**
	 * Runs Thread in Background to insert Test Data .Called by the Link on the Taskbar 'insert system' which will be removed.
	 * @author mohallo
	 *
	 */
	private class InsertSystemTask extends AsyncTask<Integer,Void,Boolean>{

		@Override
		protected Boolean doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			
			SystemAccountsDatabaseHelper systemAccountsDataBaseHelper;
			try{
				systemAccountsDataBaseHelper = new SystemAccountsDatabaseHelper(getActivity());
				systemAccountsDataBaseHelper.PopulateDummyData();
			}catch(Exception ex){
				ex.printStackTrace();
				return false;
			}finally{
			
			}
			
			return true;
		}
		
		@Override
		protected void onPreExecute(){
			
		}
		
		protected void onPostExecute(Boolean success){
			
			if(!success){
				Toast toast = Toast.makeText(getActivity(), "Database Unavailable", Toast.LENGTH_SHORT);
				toast.show();
			}else{
				
				// Switch to new cursor and update contents of ListView
				systemAccountsCursor = sqliteDatabase.rawQuery("SELECT * from SYSTEM",null);
				systemAccountsCursorAdapter.changeCursor(systemAccountsCursor);
				
				Toast toast = Toast.makeText(getActivity(), "Database Available Insert ok", Toast.LENGTH_SHORT);
				toast.show();
			}
			
		}
		
	}
	
	/**
	 * Called from AccountListActivity .Will update the CursorAdapter with results from new Query parameter.
	 */
	public void updateList(String searchQueryValue){
		
		//pupulate the searchQry Enum Object with the Search value .Search column has been populated from the onOptionsItemSelected .
		this.searchQueryValue = searchQueryValue;
		if(searchQueryValue == null || searchQueryValue.length() == 0)
			searchQry = new SearchQuery(Column.ALL,"");
		 
		searchQry.setValue(searchQueryValue.toUpperCase());
		
		String query = PreferenceManager.getDefaultSharedPreferences(getActivity())
	                .getString(AccountsListActivity.PREF_SEARCH_QUERY, null);
	            if (query != null) {
	            	Log.d(TAG,"Query is in updateList() "+query);  
	                //return new FlickrFetchr().search(query);
	            } else {
	            	Log.d(TAG,"Query is in updateList() "+query);
	                //return new FlickrFetchr().fetchItems();
	            }
	 
	            
	    getLoaderManager().restartLoader(RUN_LOADER_ID, null, mCallbacks);
	}
	
	
	/**
	 * These three methods have to do with the Loader of the Database List Data .Running query asynchronously .
	 */

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		switch(id){
		case RUN_LOADER_ID:
    	    return new SystemAccountsListCursorLoader(getActivity());//Creates Loader and asynhronously creates a cursor
		case LOCATION_LOADER_ID:
			return null;
		default:
			return null;
		}
	}


    /**
     * Runs after onCreateLoader fetches the data and passes a cursor .The SetListAdapter is passed the new Cursor SystemAccountsCursorAdapter.
     */
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor systemAccountsCursor) {
		// TODO Auto-generated method stub
		
		switch (loader.getId()) {
		case RUN_LOADER_ID:
			// The asynchronous load is complete and the data
			// is now available for use. Only now can we associate
			// the queried Cursor with the SimpleCursorAdapter.
			
			systemAccountsCursorAdapter = new SystemAccountsCursorAdapter(getActivity(), systemAccountsCursor, 0);
			if(systemAccountsCursorAdapter.getCount() > 0){
				 Log.d(TAG,"Results in the Cursor");
			     setListAdapter(systemAccountsCursorAdapter);
			}else{
				Log.d(TAG,"No Results in the Cursor");
			}
			break;
		case LOCATION_LOADER_ID: 
			break;
		default:
			break;
  }
	}



	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		setListAdapter(null);
		
	}
	
	

}
