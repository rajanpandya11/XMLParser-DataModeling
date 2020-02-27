import java.util.*;
import java.io.*;

public class CreateTable {
	
	public static void main(String args[]){
		Scanner input = new Scanner(System.in);
		System.out.print("Enter valid database name (final project will have access to...just for directory purposes): ");
		String databaseName = input.nextLine();
		System.out.print("\nEnter table name: ");
		String tableName = input.nextLine();
		//simulating (field-def[, field-def] ... )
		//NOTE: this portion depends how the information is being passed. TEMPORARY SOLUTION
		System.out.println("\nEnter all field definitions (Enter 'Done' to stop): ");
		ArrayList<String> fieldDefs = new ArrayList<String>();
		do{
			fieldDefs.add(input.nextLine());
		}while(fieldDefs.get(fieldDefs.size() - 1).equals("Done") == false);
		
		createTable(databaseName, tableName, fieldDefs);
		input.close();
	}
	
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
	}
}