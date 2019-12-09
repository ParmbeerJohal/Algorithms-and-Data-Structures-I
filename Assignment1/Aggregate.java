/* Aggregate_Retry.java
   CSC 225 - Summer 2018
   Parm Johal
   V00787710
   06/23/2018
*/


import java.io.*;
import java.util.*;

public class Aggregate_Retry{
	//Starter code given by Bill Bird - 04/30/2018
	public static void showUsage(){
		System.err.printf("Usage: java Aggregate <function> <aggregation column> <csv file> <group column 1> <group column 2> ...\n");
		System.err.printf("Where <function> is one of \"count\", \"count_distinct\", \"sum\", \"avg\"\n");
	}     
	
	public static void main(String[] args){

		//At least four arguments are needed
		if (args.length < 4){
			showUsage();
			return;
		}
		String agg_function = args[0];
		String agg_column = args[1];
		String csv_filename = args[2];
		String[] group_columns = new String[args.length - 3];
		for(int i = 3; i < args.length; i++)
			group_columns[i-3] = args[i];

		if (!agg_function.equals("count") && !agg_function.equals("count_distinct") && !agg_function.equals("sum") && !agg_function.equals("avg")){
			showUsage();
			return;
		}

		BufferedReader br = null;

		try{
			br = new BufferedReader(new FileReader(csv_filename));
		}catch( IOException e ){
			System.err.printf("Error: Unable to open file %s\n",csv_filename);
			return;
		}

		String header_line;
		try{
			header_line = br.readLine(); //The readLine method returns either the next line of the file or null (if the end of the file has been reached)
		} catch (IOException e){
			System.err.printf("Error reading file\n", csv_filename);
			return;
		}
		if (header_line == null){
			System.err.printf("Error: CSV file %s has no header row\n", csv_filename);
			return;
		}

		//Split the header_line string into an array of string values using a comma
		//as the separator.
		String[] column_names = header_line.split(",");

		//... Your code here ...
		
		//System.out.println(Arrays.toString(column_names));
		
		ArrayList<String[]> table = new ArrayList<String[]>();
		table.add(column_names);
		try{
			while(br.ready()) {
				String[] lineToRead = br.readLine().split(",");
				table.add(lineToRead);
			}
		} catch(IOException e){
			System.err.printf("Error reading file\n", csv_filename);
			return;
		}
		for(String[] row : table) {
			System.out.println(Arrays.toString(row));
		}
		
		//FINISHED READING IN TABLE OF GRADES***
		
		//For this tablecut array
		//Row size is equal to number of rows + header row
		//Column size is equal to the length of group column array
		//plus the aggregation column
		String[][] tableCut = new String[table.size()][group_columns.length + 1];
		
		//ADD IN GROUP_COLUMN HEADERS TO NEW TABLE
		for(int count = 0; count < group_columns.length; count++) {
			tableCut[0][count] = group_columns[count];
		}
		//Add in agg column
		tableCut[0][group_columns.length] = agg_column;
		
		//add in relevant data for each column header
		int i;
		//This loop searches for which column place the agg column is in initially
		for(i = 0; i < column_names.length; i++) {
			if(agg_column.equals(column_names[i]))
				break;
		}
		for(int num = 1; num < table.size(); num++) {
			tableCut[num][group_columns.length] = table.get(num)[i];
		}
		//System.out.println(Arrays.deepToString(tableCut));
		
		int groupCount = 0;
		while(groupCount != group_columns.length) {
			
			int index = 0;
			for(index = 0; index < table.get(0).length; index++) {
				if(group_columns[groupCount].equals(table.get(0)[index])){break;}
				
			}
			//System.out.println("index variable: " + index);
			for(int x = 1; x < table.size(); x++) {
				tableCut[x][groupCount] = table.get(x)[index];
			}
			
			
			groupCount++;
		}
		//System.out.println(Arrays.deepToString(tableCut));
		for(String[] row : tableCut) {
			System.out.println(Arrays.toString(row));
		}
		
		ArrayList<String[]> finalTable;
		if(agg_function.equals("count")) {
			finalTable = count(tableCut);
		} else if(agg_function.equals("sum")) {
			finalTable = sum(tableCut);
		} else if(agg_function.equals("avg")) {
			finalTable = avg(tableCut);
		} else {
			finalTable = count_distinct(tableCut);
		}
		System.out.println("Final table to output:");
		for(String[] row : finalTable) {
			System.out.println(Arrays.toString(row));
		}
	}
	//WORKING CODE UP UNTIL THIS POINT***
	
	//FINISHED COUNT METHOD***
	public static ArrayList<String[]> count(String[][] table) {
		int count = 0;
		ArrayList<String[]> aggTable = new ArrayList<String[]>();
		aggTable.add(table[0]);
		
		System.out.println("aggTable input:");
		for(String[] row : aggTable) {
			System.out.println(Arrays.toString(row));
		}
		
		//Iteration to go through pruned table
		for(int i = 1; i < table.length-1; i++){
			if(table[i][0] == "") continue; // If 
			String[] row = new String[table[0].length];
			row[0] = table[i][0];
			count = 1;
			for(int j = i+1; j < table.length; j++) {
				//if(table[j][0].equals(table[i][0])) { // Add new condition in if statement which checks if all group columns are the same (METHOD)
				if(compareRows(table, i, j)) {	
					count++;
					table[j][0] = "";
					continue;
				}
			}
			//System.out.println("after nested for loop\nrow index accessed is:" + table[0].length);
			row[table[0].length - 1] = Integer.toString(count);
			aggTable.add(row);
		}
		
		System.out.println("aggTable to return:");
		for(String[] row : aggTable) {
			System.out.println(Arrays.toString(row));
		}
		
		return aggTable;
	}
	
	public static ArrayList<String[]> sum(String[][] table){
		System.out.println("Into the sum method...");
		return null;
	}
	
	public static ArrayList<String[]> avg(String[][] table){
		System.out.println("Into the avg method...");
		return null;
	}
	
	public static ArrayList<String[]> count_distinct(String[][] table){
		System.out.println("Into the count_distinct method...");
		return null;
	}
	
	//This method checks to see if 2 rows are the same for all columns
	//except the agg column.
	public static boolean compareRows(String[][] table, int row1, int row2) {
		if(table[row1].length != table[row2].length) {
			return false;
		}
		for(int i = 0; i < table[row1].length - 1; i++) {
			if(table[row1][i] != table[row2][i]) {
				return false;
			}
		}
		return true;
	}
	
}