 
import java.util.*;
import java.io.*;

public class CreateDatabase {
	
	public static void main(String args[]){
		Scanner input = new Scanner(System.in);
		System.out.println("Enter database name: ");
		String databaseName = input.nextLine();
		
		createDatabase(databaseName);
		input.close();
	}
	
	/* createDatabase
	 * Parameters: 1) databaseName - the user-defined name for the database
	 * Function: A) generates a new directory with the name specified by the user ('databaseName')
	 * 			 B) writes the new database name to the database dictionary file ('index.txt'). 'index.txt'
	 * 				already exists in the directory with all databases
	 * 			 C) creates a new 'index.txt' dictionary file for tables, and places it in the new database directory*/
	public static void createDatabase(String databaseName){
		String currentDir = System.getProperty("user.dir");					//get user's current directory
		File newDir = new File(currentDir + "/" + databaseName);		//set path for new directory
		File databaseIndexFile = new File(currentDir + "/index.txt");	//set path for database index file
		File tableIndexFile = new File(newDir + "/index.txt");				//new index file to be created for the new database's tables
		
		try{
			//catch if the database already exists
			if(newDir.exists() == true){
				System.out.print("Database: " + databaseName + " already exists.");
				return;
			}
			else{
				//create database directory + new index file for the tables
				newDir.mkdir();
				tableIndexFile.createNewFile();
				//add database name to index file
				PrintWriter pw = new PrintWriter(new FileWriter(databaseIndexFile, true));
				pw.append(databaseName + System.lineSeparator());
				pw.close();
				
				System.out.println("Database created");
			}
		}
		catch(Exception ex){
			System.out.println(ex);
		}
	}
}
