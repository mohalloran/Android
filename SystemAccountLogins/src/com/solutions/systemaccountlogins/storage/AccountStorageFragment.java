package com.solutions.systemaccountlogins.storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONArray;

import org.json.JSONTokener;

import com.solutions.systemaccountlogins.R;
import com.solutions.systemaccountlogins.constants.Constants;
import com.solutions.systemaccountlogins.dao.SystemAccount;
import com.solutions.systemaccountlogins.manager.SystemAccountsManager;
import com.solutions.utils.component.CreateComponents;

import android.app.AlertDialog;
import android.app.Fragment;

import android.content.DialogInterface;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;

import android.widget.TextView;


public class AccountStorageFragment extends Fragment{
	
	private static final int MESSAGE_STATUS = 0;
	
	private static final String TAG = "AccountStorageFragment";
	
	private static final String DOCUMENTSFOLDER = "Documents";
	private static final String TEMPLATEFILE = "sad.xls";
	
	private static AccountStorageFragment accountStorageFragment;
	private Button saveButton;
	private Button updateDatabaseButton;
	private TextView storageStatus;
	private TextView fileLocationTextView;
	//private String fileDirectory;
	//private String filePath;
	private String[] fileLocation;
	private File systemAccountsFile;
	private Handler processHandler;
	private Message msg;
	private String fileAbsolutePath;
	private SystemAccountsManager systemAccountsManager;
	private ArrayList<SystemAccount> systemAccountsList;
	private AlertDialog alertDialog;
	private RadioButton storageTruncateInsertRadio;
	private RadioButton storageUpdateRadio;
	private File root;
	private File dir;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		processHandler = new Handler(){
			
			@Override
			public void handleMessage(Message msg){
				// process incoming messages here
                switch (msg.what) {
                    case MESSAGE_STATUS:
                    	storageStatus.setText((String)msg.obj);
                    	break;
                    	
                    case 1:
                }
				
			}
			
		};
	}
	
	public static Fragment createInstance(){
		
		accountStorageFragment = new AccountStorageFragment();
		return accountStorageFragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup parent,Bundle savedInstanceState){
		
		View accountStorageView = inflater.inflate(R.layout.account_storage_fragment, parent,false);
		
		storageStatus = (TextView) accountStorageView.findViewById(R.id.storage_status);
		
		saveButton = (Button) accountStorageView.findViewById(R.id.save_storage_button);
		
		updateDatabaseButton = (Button) accountStorageView.findViewById(R.id.update_database_button);
		
		fileLocationTextView = (TextView) accountStorageView.findViewById(R.id.storage_location);
		
		storageTruncateInsertRadio = (RadioButton) accountStorageView.findViewById(R.id.radio_storage_truncate_insert);
		storageUpdateRadio = (RadioButton) accountStorageView.findViewById(R.id.radio_storage_update);
		
	
		saveButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d(TAG,"Save StorageButton clicked.....");
				
				
				
				if(!validateFileLocation())
					return;

				
				if(isExternalStorageWritable() ){
					
					systemAccountsManager = SystemAccountsManager.getInstance(getActivity());
					
					
					Log.d(TAG,"ExternalStorageWritable is true");
					
					Thread storageThread = new Thread(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							try{
								
							  //Get List of Acccounts .
							  systemAccountsList = systemAccountsManager.getSystemAccountsList();
								
							  if(writeAccountDataListToExcellFile(systemAccountsList,fileLocation) ){

					              createMessage(0,"Successfully wrote Contents to output file:"+fileAbsolutePath);
							  }else{
								  createMessage(0,"UnSuccessfully wrote Contents to output file:"+fileAbsolutePath);
							  }
							  
							}catch(Exception ex){
								Log.d(TAG,ex.getMessage());
								createMessage(0,"Big bad Exception thrown .Was not Successfull in Writing Accounts Data to the Documents Directory");
							     
							}
							/**
							processHandler.post(new Runnable(){

								@Override
								public void run() {
									
									storageStatus.setText("Successfully wrote Contents to output file:"+systemAccountsFile.getAbsolutePath());
								}
								
							});
							*/
						}
						
					});
					storageThread.start();
		
				}else{
					Log.d(TAG,"ExternalStorageWritable is false");
				}
				
			}
		});
		
		updateDatabaseButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(!validateFileLocation())
					return;
				
                if(isExternalStorageWritable() ){
                	
                 systemAccountsManager = SystemAccountsManager.getInstance(getActivity());
                 
                 if(storageTruncateInsertRadio.isChecked() ){

					CreateComponents.createOkCancelDialog(getActivity(), R.string.ok_cancel_dialog, R.string.alert_delete_storage,
				            new DialogInterface.OnClickListener(){

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									
									Log.d(TAG,"OK Button clicked.....");
									
									new Thread(new Runnable(){

										@Override
										public void run() {
											
											if(storageTruncateInsertRadio.isChecked() ){
												
												Log.d(TAG,"Continueing.............");

												//extractFileContents();
												
												if(!extractExcellFileContents() ){
													//createMessage(0,"Failed to Extract File contents from the file:"+(fileAbsolutePath == null?"":fileAbsolutePath.toString()));
													return;
												}else{
													//check if we have contents of parsed file in List.
													if(systemAccountsList != null && systemAccountsList.size() > 0){
													  if(truncateAndSaveToDB() ){
														createMessage(0,"Successfully Truncated and Populated the System Table");
													  }else{
														  createMessage(0,"Not all Records were saved."); 
													  }
													}
												}
												
											   
											}else{
												Log.d(TAG,"Radion Button Update is checked......");
											}
										}
									
									
									
									
									
									}).start();
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
					
					
					
										
                }else{
                	
                	if(!extractExcellFileContents() ){
						createMessage(0,"Failed to Extract File contents from the file:"+(fileAbsolutePath == null?"":fileAbsolutePath.toString()));
						return;
					}else{
						//check if we have contents of parsed file in List.
						if(systemAccountsList != null && systemAccountsList.size() > 0){
						  if(updateDB() ){
							createMessage(0,"Successfully Updated the System Table");
						  }else{
							  createMessage(0,"Not all Records were saved."); 
						  }
						}
					}
                }
					
            }	
				
				
			
			}
		});
		
		//Create xls template file if it does not alread exist.
		if(createTemplate(new ArrayList<SystemAccount>(),new String[] {DOCUMENTSFOLDER,TEMPLATEFILE} ) ){
		   Log.d(TAG,"Creating Template File");
		   alertDialog = CreateComponents.createAlertDialog(getActivity(),R.string.xlstemplatecreated);
		   alertDialog.show();
		}
		
		
		
		return accountStorageView;
		
	}
	
	private boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false; 
	}
	
    private boolean writeAccountDataListToFile(ArrayList<SystemAccount> systemAccounts,String[] fileLocation){
    	
    	JSONArray array;  
    	
    	FileOutputStream fileOutputStream = null;
    	PrintWriter pw = null;
    	    			
		root = android.os.Environment.getExternalStorageDirectory();
		Log.d(TAG,"External file system root: "+root);
		
		
		//File dir = new File (root.getAbsolutePath() + "/Documents");
		//fileAbsolutePath = dir.getAbsolutePath() + "/SystemAccountsData.txt";
		
		dir = new File (root.getAbsolutePath() + "/"+ fileLocation[0]);
		fileAbsolutePath = dir.getAbsolutePath() + "/"+ fileLocation[1];
		
        if(dir.mkdirs() || dir.exists()){
        	systemAccountsFile = new File(dir, fileLocation[1]);

            try {  
            	     fileOutputStream = new FileOutputStream(systemAccountsFile);
                     pw = new PrintWriter(fileOutputStream);
                     
                     for(SystemAccount systemAccount:systemAccounts){
                    	 
                    	array = new JSONArray();
                    	array.put(systemAccount.toJSON());
                    	
                        pw.println(array.toString()+"\n");
                       
                     }
                     
                       
            } catch (FileNotFoundException e) {
                     e.printStackTrace();
                     Log.i(TAG, "File not found. Did you" +
                     " add a WRITE_EXTERNAL_STORAGE permission to the manifest?");
                     
                     createMessage(0,"File not found. Did you add a WRITE_EXTERNAL_STORAGE permission to the manifest? ");
                     
                     return false;
			         
            } catch (IOException e) {
                     e.printStackTrace();
                     
                     createMessage(0,"Error writing to the File "+fileAbsolutePath == null?"":fileAbsolutePath);
                     
                     return false;
			         
            } catch (Exception e) {
                e.printStackTrace();
                createMessage(0,"Error writing to the File ");
                
                return false;
            }finally{
            	try{
            	  fileOutputStream.close();
            	  
            	  pw.flush();
                  pw.close();
            	}catch(Exception ex){}
            }

        }
        
        return true;
	}
    
    /**
     * creates a Document Template.
     * @param systemAccounts
     * @param fileLocation
     * @return
     */
    private boolean createTemplate(ArrayList<SystemAccount> systemAccounts,String[] fileLocation){
    	
    	root = android.os.Environment.getExternalStorageDirectory();
		Log.d(TAG,"External file system root: "+root);
		
    	dir = new File (root.getAbsolutePath() + "/"+ fileLocation[0]);
		fileAbsolutePath = dir.getAbsolutePath() + "/"+ fileLocation[1];
		

		File file = new File(fileAbsolutePath);
		if(file.exists() )
			return true;
		
    	if(isExternalStorageWritable() ){
    	   return writeAccountDataListToExcellFile(systemAccounts,fileLocation);
    	}else{
    	   return false;
    	}
    }
    
    /**
     * Writes output to excell file .
     * @param systemAccounts
     * @param fileLocation
     * @return
     */
    
    private boolean writeAccountDataListToExcellFile(ArrayList<SystemAccount> systemAccounts,String[] fileLocation){
    	
    	JSONArray array;  
    	
    	FileOutputStream fileOutputStream = null;
    	    			
		root = android.os.Environment.getExternalStorageDirectory();
		Log.d(TAG,"External file system root: "+root);

		dir = new File (root.getAbsolutePath() + "/"+ fileLocation[0]);
		fileAbsolutePath = dir.getAbsolutePath() + "/"+ fileLocation[1];
		
        if(dir.mkdirs() || dir.exists()){
        	systemAccountsFile = new File(dir, fileLocation[1].trim());

            try {  
                     fileOutputStream = new FileOutputStream(systemAccountsFile);

                     Workbook wb = createExcellWorkSheet(systemAccounts,fileLocation);
                     wb.write(fileOutputStream);
  
                     fileOutputStream.close();
   
            } catch (FileNotFoundException e) {
                     e.printStackTrace();
                     Log.i(TAG, "File not found. Did you" +
                     " add a WRITE_EXTERNAL_STORAGE permission to the manifest?");
                     
                     createMessage(0,"File not found. Did you add a WRITE_EXTERNAL_STORAGE permission to the manifest? ");
                     
                     return false;
			         
            } catch (IOException e) {
                     e.printStackTrace();
                     
                     createMessage(0,"Error writing to the File "+fileAbsolutePath == null?"":fileAbsolutePath);
                     
                     return false;
			         
            } catch (Exception e) {
                e.printStackTrace();
                createMessage(0,"Error writing to the File ");
                
                return false;
            }	finally{
            	try{
            	  fileOutputStream.close();
            	}catch (Exception ex){}
            }

        }
        
        return true;
	}
    
    /**
     * Creates Excell Worksheet 
     * @return
     */
    private Workbook createExcellWorkSheet(ArrayList<SystemAccount>systemAccounts,String[] fileLocation){
    	
    
       int rowCount = 1;
       int cellCount = 0;
       
       boolean success = false;
		 //New Workbook
       Workbook wb = new HSSFWorkbook();

       Cell cell = null;
       
       //Cell style for header row
       CellStyle cs = wb.createCellStyle();
       cs.setFillForegroundColor(HSSFColor.LIME.index);
       cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
       
       //New Sheet
       Sheet sheet1 = null;
       sheet1 = wb.createSheet("applicationAccount");
       
       // Generate column headings
       Row row = sheet1.createRow(0);
       
       cell = row.createCell(0);
       cell.setCellValue("APPLICATION NAME");
       cell.setCellStyle(cs);

       cell = row.createCell(1);
       cell.setCellValue("SERVER NAME");
       cell.setCellStyle(cs);

       cell = row.createCell(2);
       cell.setCellValue("LOGIN NAME");
       cell.setCellStyle(cs);
       
       cell = row.createCell(3);
       cell.setCellValue("LOGIN PASSWORD");
       cell.setCellStyle(cs);
       
       cell = row.createCell(4);
       cell.setCellValue("DATE CREATED");
       cell.setCellStyle(cs);
       
       cell = row.createCell(5);
       cell.setCellValue("USER NAME");
       cell.setCellStyle(cs);
       
       cell = row.createCell(6);
       cell.setCellValue("ENVIRONMENT");
       cell.setCellStyle(cs);
       
       cell = row.createCell(7);
       cell.setCellValue("DESCRIPTION");
       cell.setCellStyle(cs);
       
       cell = row.createCell(8);
       cell.setCellValue("NOTES");
       cell.setCellStyle(cs);
       
       cell = row.createCell(9);
       cell.setCellValue("EXPIRED");
       cell.setCellStyle(cs);
       
       //Data
       for(SystemAccount systemAccount:systemAccounts){
    	   row = sheet1.createRow(rowCount);
    	   cell = row.createCell(0);
    	   cell.setCellValue(systemAccount.getApplication());


    	   cell = row.createCell(1);
    	   cell.setCellValue(systemAccount.getServerName());
    	   
    	   cell = row.createCell(2);
    	   cell.setCellValue(systemAccount.getLoginName());
    	 
    	   cell = row.createCell(3);
    	   cell.setCellValue(systemAccount.getLoginPassword());
    	   
    	   cell = row.createCell(4);
    	   cell.setCellValue(systemAccount.getDateCreated());
    	   
    	   cell = row.createCell(5);
    	   cell.setCellValue(systemAccount.getUserName());
    	   
    	   cell = row.createCell(6);
    	   cell.setCellValue(Constants.Environment.getEnvironmentName(Integer.parseInt(systemAccount.getEnvironment())));
    	   
    	   cell = row.createCell(7);
    	   cell.setCellValue(systemAccount.getDescription());
    	   
    	   cell = row.createCell(8);
    	   cell.setCellValue(systemAccount.getNotes());
    	   
    	   cell = row.createCell(9);
    	   cell.setCellValue(systemAccount.getExpired() == 1 ?Constants.Confirmation.TRUE.toString():Constants.Confirmation.FALSE.toString());


    	   sheet1.setColumnWidth(0, (15 * 500));
    	   sheet1.setColumnWidth(1, (15 * 500));
    	   sheet1.setColumnWidth(2, (15 * 500));
    	   
    	   rowCount++;
       }
    	
    	return wb;
    }

	private boolean writeAccountDataToFile(ArrayList<SystemAccount> systemAccounts){
		
		FileOutputStream f = null;
		PrintWriter pw = null;
		
		File root = android.os.Environment.getExternalStorageDirectory();
		Log.d(TAG,"External file system root: "+root);
		
		File dir = new File (root.getAbsolutePath() + "/Documents");
		fileAbsolutePath = dir.getAbsolutePath() + "/SystemAccountsData.txt";
		
        if(dir.mkdirs() || dir.exists()){
        	systemAccountsFile = new File(dir, "SystemAccountsData.txt");
        	
        	
            try {
                     f = new FileOutputStream(systemAccountsFile);
                     pw = new PrintWriter(f);
                     pw.println("Howdy do to you.");
                     pw.println("Here is a second line.");
                     
                     
            } catch (FileNotFoundException e) {
                     e.printStackTrace();
                     Log.i(TAG, "File not found. Did you" +
                     " add a WRITE_EXTERNAL_STORAGE permission to the manifest?");
                     
                     createMessage(0,getResources().getString(R.string.file_not_found));
                     
                     return false;
			         
            } catch (IOException e) {
                     e.printStackTrace();
                     
                     createMessage(0,getResources().getString(R.string.file_write_error)+":"+fileAbsolutePath == null?"":fileAbsolutePath);
                     
                     return false;
			         
            } catch (Exception e) {
                e.printStackTrace();
                createMessage(0,"Error writing to the File ");
                
                return false;
            }finally{
            	try{
            	  f.close();
            	  pw.close();
            	}catch(Exception ex){}
            }

        }
        
        return true;
	}
	
	/**
	 * Puts message in the queue handler.
	 * @param messageIdentifier
	 * @param messageContent
	 */
	private void createMessage(int messageIdentifier,String messageContent){
		
		msg = new Message();
        msg.what = messageIdentifier;
        msg.obj = messageContent;
        
        
        processHandler.sendMessage(msg);
        
	}
	
	/**
	 * validates the Location of the text file.
	 * @return
	 */
	private boolean validateFileLocation(){
		
		if(fileLocationTextView.getText() != null && fileLocationTextView.getText().toString().length() > 0){
			
		    fileLocation = fileLocationTextView.getText().toString().split("/");
		    
		    if(fileLocation.length != 2){
		    	//dialog alert .please enter directory name/filename 
		    	alertDialog = CreateComponents.createAlertDialog(getActivity(),R.string.invalid_location);
				alertDialog.show();
				 
				return false;  
		    }
		
		}else{
			//dialog message must enter a value
			alertDialog = CreateComponents.createAlertDialog(getActivity(),R.string.invalid_location);
			alertDialog.show();
			 
			return false; 
		}
		
		return true;
		
	}
	
	
	/*
	 * Extracts the contents of the File from the file system location.
	 * @return successfully in parsing the file 
	 */
	
	public boolean extractFileContents(){
		
		File root = android.os.Environment.getExternalStorageDirectory();
		dir = new File (root.getAbsolutePath() + "/"+ fileLocation[0]);
		fileAbsolutePath = dir.getAbsolutePath() + "/"+ fileLocation[1];
		
		FileInputStream fin = null;
		BufferedReader reader = null;
		StringBuilder jsonString = new StringBuilder();
		String line = null;
		boolean extractionStatus = true; 
		
		if(dir.exists()){
        	systemAccountsFile = new File(dir, fileLocation[1].trim());
        	systemAccountsList = new ArrayList<SystemAccount>();
        	
        	try {
        		fin = new FileInputStream(systemAccountsFile);
        		reader = new BufferedReader(new InputStreamReader(fin));
        		
        		
        		while((line = reader.readLine()) != null){
        			if(line.length() == 0 || !(line.indexOf("{") > 0))
        				continue;
        			jsonString.append(line).append("\n");
        			
        			
        			JSONArray jsonArray = ( (JSONArray) new JSONTokener(line.toString()).nextValue() );
        			systemAccountsList.add(new SystemAccount(jsonArray.getJSONObject(0)));
        			
        			
        		}
 	
        	}catch(FileNotFoundException fnfe){
        		Log.d(TAG,"File to Read was not found:"+fnfe.getMessage());
        		createMessage(0,getResources().getString(R.string.file_not_found)+":"+(fileAbsolutePath == null?"":fileAbsolutePath.toString()));
        		extractionStatus = false;
        	}catch(Exception ex){
        		Log.d(TAG,"Exception thrown when parsing and reading the file:"+ex.getMessage());
        		createMessage(0,getResources().getString(R.string.file_parsing_error)+":"+(fileAbsolutePath == null?"":fileAbsolutePath.toString()));
        		extractionStatus = false;
        	}finally{
        		if(reader != null)
					try {
						
						fin.close();
						reader.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						
					}
        	}
        	
        	

		}
		Log.d(TAG,"Parsed Results in List size is: "+systemAccountsList.size());
		return extractionStatus;
	}
	
	/*
	 * Extracts the contents of the File from the file system location.
	 * @return successfully in parsing the file 
	 */
	
	public boolean extractExcellFileContents(){
		
		File root = android.os.Environment.getExternalStorageDirectory();
		dir = new File (root.getAbsolutePath() + "/"+ fileLocation[0]);
		fileAbsolutePath = dir.getAbsolutePath() + "/"+ fileLocation[1];
		
		FileInputStream fin = null;
		BufferedReader reader = null;
		StringBuilder jsonString = new StringBuilder();
		
		SystemAccount systemAccount = null;
		
		String line = null;
		boolean extractionStatus = true;
		
		if(dir.exists()){
        	systemAccountsFile = new File(dir, fileLocation[1].trim());
        	systemAccountsList = new ArrayList<SystemAccount>();
        	
        	try {
        		fin = new FileInputStream(systemAccountsFile);
        		reader = new BufferedReader(new InputStreamReader(fin));
        		
        		// Create a POIFSFileSystem object 
                POIFSFileSystem fileSystem = new POIFSFileSystem(fin);
     
                // Create a workbook using the File System 
                HSSFWorkbook myWorkBook = new HSSFWorkbook(fileSystem);
                
                // Get the first sheet from workbook 
                HSSFSheet mySheet = myWorkBook.getSheetAt(0);
     
                /** iterate through the cells.**/
                Iterator rowIter = mySheet.rowIterator();
                
                while(rowIter.hasNext()){
                    HSSFRow myRow = (HSSFRow) rowIter.next();
                    
                    if(myRow.getRowNum() == 0)//skip headers
                    	continue;
                    
                    Iterator cellIter = myRow.cellIterator();
                    systemAccount = new SystemAccount();
                    while(cellIter.hasNext()){
                    	
                        HSSFCell myCell = (HSSFCell) cellIter.next();
                        switch(myCell.getColumnIndex()){
                        
                        	case 0:systemAccount.setApplication(myCell.getStringCellValue());
                               break;
                        	case 1:systemAccount.setServerName(myCell.getStringCellValue());
                               break;
                        	case 2:systemAccount.setLoginName(myCell.getStringCellValue());
                        	   break;
                        	case 3:systemAccount.setLoginPassword(myCell.getStringCellValue());
                        	   break;
                        	case 4:systemAccount.setDateCreated(myCell.getStringCellValue());
                        	   break;
                        	case 5:systemAccount.setUserName(myCell.getStringCellValue());
                        	   break;
                        	case 6:systemAccount.setEnvironment( String.valueOf( (Constants.Environment.valueOf(myCell.getStringCellValue())).value) );
                        	   break;
                        	case 7:systemAccount.setDescription(myCell.getStringCellValue());
                        	   break;
                        	case 8:systemAccount.setNotes(myCell.getStringCellValue());
                        	   break;
                        	case 9:systemAccount.setExpired( (short)(Constants.Confirmation.valueOf(myCell.getStringCellValue())).value);
                        	   break;
                        
                        }
                        
                    }
                    systemAccountsList.add(systemAccount);
                }
        		
        		        		
        		
        		
        	}catch(FileNotFoundException fnfe){
        		Log.d(TAG,"File to Read was not found:"+fnfe.getMessage());
        		createMessage(0,getResources().getString(R.string.file_not_found)+":"+(fileAbsolutePath == null?"":fileAbsolutePath.toString()));
        		extractionStatus = false;
        	}catch(Exception ex){
        		Log.d(TAG,"Exception thrown when parsing and reading the file:"+ex.getMessage());
        		createMessage(0,getResources().getString(R.string.file_parsing_error)+":"+(fileAbsolutePath == null?"":fileAbsolutePath.toString()));
        		extractionStatus = false;
        	}finally{
        		if(reader != null)
					try {
						
						fin.close();
						reader.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						
					}
        	}
        	
        	

		}
		Log.d(TAG,"Parsed Results in List size is: "+systemAccountsList.size());
		return extractionStatus;
	}
	
	
	/**
	 * Truncate the Database table
	 * save contents of the file to the Database .
	 * @return
	 */
	private boolean truncateAndSaveToDB(){
		
		int recordsInserted = 0;
		
				
		if(systemAccountsManager.truncateSystemTable() ){
		 
			for(SystemAccount systemAccount:systemAccountsList){
			    if(systemAccountsManager.insertSystemAccount(systemAccount) != -1)
				   recordsInserted++;
			}	   
			if(recordsInserted != systemAccountsList.size())
				   return false;
			else
				   return true;
			
			
			
		}else{
			return false;
		}
	}
	
	/**
	 * Update the Database Records 
	 */
	private boolean updateDB() {
		int updatedRecords = 0;

		ArrayList<SystemAccount> dbRecordList = systemAccountsManager.getSystemAccountsList();

		for (SystemAccount systemAccount : systemAccountsList) {

			
					// Does the record exist in the DB
					if (systemAccountsManager.doesRecordExist(systemAccount)) {
						systemAccountsManager.updateSystemRecord(systemAccount);
					} else {
						systemAccountsManager
								.insertSystemAccount(systemAccount);
					}  
			

		}

		return true;
	}
	


}
