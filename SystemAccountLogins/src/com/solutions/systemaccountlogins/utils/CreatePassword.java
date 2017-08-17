package com.solutions.systemaccountlogins.utils;

import java.util.Random;



public class CreatePassword {
	
	public static String generate(){
		
		   String password = "";
		   
		   int randomIntNumber =generateRandom(33,46);
		   password += CharUtils.ASCIIToChar(randomIntNumber);
		   
		   randomIntNumber =generateRandom(48,57);
		   password += CharUtils.ASCIIToChar(randomIntNumber);
		   
		   randomIntNumber =generateRandom(65,90);
		   password += CharUtils.ASCIIToChar(randomIntNumber);
		   
		   randomIntNumber =generateRandom(97,122);
		   password += CharUtils.ASCIIToChar(randomIntNumber);
		   
		   randomIntNumber =generateRandom(33,46);
		   password += CharUtils.ASCIIToChar(randomIntNumber);
		   
		   randomIntNumber =generateRandom(48,57);
		   password += CharUtils.ASCIIToChar(randomIntNumber);
		   
		   randomIntNumber =generateRandom(65,90);
		   password += CharUtils.ASCIIToChar(randomIntNumber);
		   
		   randomIntNumber =generateRandom(97,122);
		   password += CharUtils.ASCIIToChar(randomIntNumber);
 
		   return password;
	}
	
	public static int generateRandom(int min ,int max){
		
		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;
		
		return randomNum;
	}
	
	public static class CharUtils{
		
		public static int charToASCII(char charValue){
			
			return (int)charValue;
			
		}
		
		public static char ASCIIToChar(int intValue){
			
			return (char)intValue;
		}
		
		
	}

}
