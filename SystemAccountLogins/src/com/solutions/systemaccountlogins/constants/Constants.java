package com.solutions.systemaccountlogins.constants;

public class Constants {
	
	
	public static enum Environment{
		DEV(0),TEST1(1),TEST2(2),TEST3(3),E2E(4),PRODUCTION(5);
		
		public int value;
		
		private Environment(int value){
		
			this.value = value;
		}
		
		public static String getEnvironmentName(int value ){
			
			String environmentName = "";
			for(Environment environment:Environment.values()){
				
				if(environment.value == value){
					environmentName = environment.toString();
					break;
				}
			}
			return environmentName; 
		}
		
		public static String[] getEnviromentNames(){
			
			
			String environments[] = new String[Environment.values().length];
			
			for(int i=0;i < Environment.values().length;i++){
				environments[i] = (Environment.values()[i]).toString();
			}
			return environments;
		}
		
		
	};
	
	public static enum Confirmation{
		TRUE(1),FALSE(0);
		
		public int value;
		
		private Confirmation(int value){
			this.value = value;
		}
		
        public static String getConfirmationName(int value ){
			
			String confirmationName = "";
			for(Confirmation confirmation:Confirmation.values()){
				
				if(confirmation.value == value){
					confirmationName = confirmation.toString();
					break;
				}
			}
			return confirmationName;
		}
        
        public static String[] getConfirmationNames(){
        	
        	String[] confirmations = new String[Confirmation.values().length];
        	
        	for(int i = 0;i < Confirmation.values().length;i++){
        		
        		confirmations[i] = (Confirmation.values()[i]).toString();
        	}
        	
        	return confirmations;
        }
        
	};

}
