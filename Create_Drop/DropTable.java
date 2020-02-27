import java.util.*;
import java.io.*;

public class DropTable {
	
	public static void main(String args[]){
		Scanner input = new Scanner(System.in);
		System.out.print("Enter valid database name (final project will have access to...just for directory purposes): ");
		String databaseName = input.nextLine();
		System.out.print("\nEnter table name: ");
		String tableName = input.nextLine();
		
		dropTable(databaseName, tableName);
		input.close();
	}
	
	public static void dropTable(String databaseName, String tableName){
		String startingDir = System.getProperty("user.dir");				//get starting directory
		String currentDir = startingDir + "/src/" + databaseName;			//get user's current directory (in database)
		
		File tableFile = new File(currentDir + "/" + tableName + ".txt");	//get path to table file
		File tableIndexFile = new File(currentDir + "/index.txt");			//get path to table index file
		File tempIndexFile = new File(currentDir + "/indexTemp.txt");		//set path for temporary file to overwrite 'index.txt'

		try{
			//catch if the table does not exist
			if(tableFile.exists() == false){
				System.out.println("Table: " + tableName + " does not exist.");
				return;
			}
			else	//remove tableName from index file
			{
				BufferedReader reader = new BufferedReader(new FileReader(tableIndexFile));		//reads the table index file
				BufferedWriter writer = new BufferedWriter(new FileWriter(tempIndexFile));		//writes to a temporary table index file
				
				String currentLine;
				
				//read the table index file line by line --> find the table name to delete
				while((currentLine = reader.readLine()) != null){
					String trimmedLine = currentLine.trim();
					if(trimmedLine.equals(tableName))		//found --> skip writing to the temp file
						continue;
					writer.write(currentLine + System.lineSeparator());	//not found --> write line to the temp file
				}
				writer.close();
				reader.close();
				
				tempIndexFile.renameTo(tableIndexFile);		//rename the temp file to 'index.txt' (overwrite 'index.txt')
								
				//delete table file
				tableFile.delete();
				System.out.println("Table dropped.");
			}
		}
		catch(Exception ex){
			System.out.println(ex);
		}
	}
}
