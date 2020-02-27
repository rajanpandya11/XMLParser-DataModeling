import java.util.*;
import java.io.*;

public class CreateDropClient {
	
	public static void main(String args[]){
		Scanner input = new Scanner(System.in);
		int choice = 0;
		System.out.println("-----CREATE/DROP DATABASE/TABLE CLIENT DEMO-----\n");
		
		while(choice != 5){
			Scanner _input = new Scanner(System.in);
			System.out.println(menu() + "Enter choice: ");
			choice = input.nextInt();
			
			switch(choice){
				case 1:
					System.out.println("Enter database name: ");
					String databaseName1 = _input.nextLine();
					
					createDatabase(databaseName1);
					break;
				case 2:
					System.out.print("Enter valid database name (final project will have access to...just for directory purposes): ");
					String databaseName2 = _input.nextLine();
					System.out.print("\nEnter table name: ");
					String tableName2 = _input.nextLine();
					//simulating (field-def[, field-def] ... )
					//NOTE: this portion depends how the information is being passed. TEMPORARY SOLUTION
					System.out.println("\nEnter all field definitions (Enter 'Done' to stop): ");
					ArrayList<String> fieldDefs = new ArrayList<String>();
					do{
						fieldDefs.add(input.nextLine());
					}while(fieldDefs.get(fieldDefs.size() - 1).equals("Done") == false);
					
					createTable(databaseName2, tableName2, fieldDefs);
					break;
				case 3:
					System.out.print("Enter valid database name (final project will have access to...just for directory purposes): ");
					String databaseName3 = _input.nextLine();
					System.out.print("\nEnter table name: ");
					String tableName3 = _input.nextLine();
					
					dropTable(databaseName3, tableName3);
					break;
				case 4:
					System.out.println("Enter database name: ");
					String databaseName4 = _input.nextLine();
					
					dropDatabase(databaseName4);
					break;
				case 5:
					System.out.println("-----Client quitting-----");
					input.close();
					_input.close();
					System.exit(0);
					break;
				default:
					System.out.println("Invalid input");
					break;
			}
		}
	}//end main()
//----------------------------------------------------------------------
	
	/* createDatabase
	 * Parameters: 1) databaseName - the user-defined name for the database
	 * Function: A) generates a new directory with the name specified by the user ('databaseName')
	 * 			 B) writes the new database name to the database dictionary file ('index.txt'). 'index.txt'
	 * 				already exists in the directory with all databases
	 * 			 C) creates a new 'index.txt' dictionary file for tables, and places it in the new database directory*/
	public static void createDatabase(String databaseName){
		String currentDir = System.getProperty("user.dir");					//get user's current directory
		File newDir = new File(currentDir + "/src/" + databaseName);		//set path for new directory
		File databaseIndexFile = new File(currentDir + "/src/index.txt");	//set path for database index file
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
	}//end createDatabase
//----------------------------------------------------------------------
	
	/* createTable
	 * Parameters: 1) databaseName - the current database the user is working in
	 * 			   2) tableName - the name the user wishes to name the new table
	 * 			   3) fieldDefs - an ArrayList of Strings that contains the attribute names for the table (will be written to the new table)
	 * Function: A) creates a new text file for a table
	 * 			 B) writes the new table name to the dictionary file ('index.txt')*/
	public static void createTable(String databaseName, String tableName, ArrayList<String> fieldDefs){
		String startingDir = System.getProperty("user.dir");				//get starting directory
		String currentDir = startingDir + "/src/" + databaseName;			//get user's current directory (in database)
		
		File newTable = new File(currentDir + "/" + tableName + ".txt");	//set path for new table file
		File tableIndexFile = new File(currentDir + "/index.txt");			//get path to table index file
		
		try{
			//catch if the table already exists
			if(newTable.exists() == true){
				System.out.println("Table: " + tableName + " already exists.");
				return;
			}
			else{
				//create new table file
				newTable.createNewFile();
				
				//populate the table file with field-defs (separated by '\t')
				PrintWriter tableWriter = new PrintWriter(new FileWriter(newTable, true));
				for(int i = 0; i < fieldDefs.size() - 1; i++){
					tableWriter.append(fieldDefs.get(i) + "\t");
				}
				tableWriter.append(System.lineSeparator());
				tableWriter.close();
				
				//add table name to index file
				PrintWriter indexWriter = new PrintWriter(new FileWriter(tableIndexFile, true));
				indexWriter.append(tableName + System.lineSeparator());
				indexWriter.close();
				
				System.out.println("Table created");
			}
		}
		catch(Exception ex){
			System.out.println(ex);
		}
	}//end createTable
//----------------------------------------------------------------------
	
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
	}//end dropDatabase
//----------------------------------------------------------------------
	
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
	}//end dropTable
//----------------------------------------------------------------------
	
	public static String menu(){
		return("1. Create Database\n"
			 + "2. Create Table\n"
			 + "3. Drop Table\n"
			 + "4. Drop Database\n"
			 + "5. EXIT\n");
	}//end menu()
}//end CreateDropClient class
