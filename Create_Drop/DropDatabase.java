import java.util.*;
import java.io.*;

public class DropDatabase {
	
	public static void main(String args[]){
		Scanner input = new Scanner(System.in);
		System.out.println("Enter database name: ");
		String databaseName = input.nextLine();
		
		dropDatabase(databaseName);
		input.close();
	}
	
	public static void dropDatabase(String databaseName){
		String currentDir = System.getProperty("user.dir");					//get user's current directory
		File databaseDir = new File(currentDir + "/src/" + databaseName);	//get databaseName's path
		File databaseIndexFile = new File(currentDir + "/src/index.txt");	//set path for database index file
		File tempIndexFile = new File(currentDir + "/indexTemp.txt");		//set path for temporary file to overwrite 'index.txt'
		
		try{
			//catch if the database does not exist
			if(databaseDir.exists() == false){
				System.out.print("Database: " + databaseName + " does not exists.");
				return;
			}
			else	//remove databaseName from index file
			{
				BufferedReader reader = new BufferedReader(new FileReader(databaseIndexFile));		//reads the table index file
				BufferedWriter writer = new BufferedWriter(new FileWriter(tempIndexFile));			//writes to a temporary table index file
				
				String currentLine;
				
				//read the table index file line by line --> find the table name to delete
				while((currentLine = reader.readLine()) != null){
					String trimmedLine = currentLine.trim();
					if(trimmedLine.equals(databaseName))		//found --> skip writing to the temp file
						continue;
					writer.write(currentLine + System.lineSeparator());	//not found --> write line to the temp file
				}
				writer.close();
				reader.close();
				
				tempIndexFile.renameTo(databaseIndexFile);		//rename the temp file to 'index.txt' (overwrite 'index.txt')
				
				//delete database directory
				String entries[] = databaseDir.list();
				for(String s: entries){
					File currentFile = new File(databaseDir.getPath(), s);
					currentFile.delete();
				}
				databaseDir.delete();
				System.out.println("Database dropped.");
			}
		}
		catch(Exception ex){
			System.out.println(ex);
		}
	}
}
