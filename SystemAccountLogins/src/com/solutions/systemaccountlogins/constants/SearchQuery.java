package com.solutions.systemaccountlogins.constants;

public class SearchQuery {
	
	public Column column;
	public String value;
	
	public enum Column{
		ALL("All"),APPLICATIONNAME("Application Name"),SERVERNAME("Server Name");
		
		private String descriptiveName = "";
		
		private Column(String descriptiveName){
			this.descriptiveName = descriptiveName;
		}
		
		public String getDescriptiveName(){
			return descriptiveName;
		}
	};
	
	
	public SearchQuery(Column column,String value){
		
		this.column = column;
		this.value = value;
	}
	
	public void setValue(String value){
		this.value = value.toUpperCase();
	}
	
	
	public String queryDBString(){
		
		switch(column){
		 
		case ALL :return "";
		case APPLICATIONNAME:return "where application_name = '"+value+"'";
		case SERVERNAME:return "where UPPER(server_name) = '"+value+"'";
		
		default:return "";
		}
	}
	
	
	public static void main(String args[]){
		
		SearchQuery qry = new SearchQuery(Column.APPLICATIONNAME,"MEAT");
		System.out.println("Qry with Application Name:"+qry.queryDBString());
	}
	

}
