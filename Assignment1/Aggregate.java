/*
 * Name: Parm Johal
 * ID: V00787710
 * Date: June 23, 2018
 * Filename: Aggregate.java
 * Details: \CSC 225\ Assignment 1
 */

/*For all asymptotic time complexity analysis in this code, take n to be the number of rows (since
  the width of a row is already constrained to 100000 characters).*/

//For reading files:
import java.io.BufferedReader;
import java.io.FileReader;
//For storing an unknown number of columns:
import java.util.ArrayList;
//For error handling:
import java.io.IOException;

public class Aggregate1{

	public static ArrayList<ArrayList<String>> readCSV(String inputFile) throws IOException{//Runs in O(n^2) time.
		//Used code from https://stackoverflow.com/questions/4716503/reading-a-plain-text-file-in-java as a template for my use of BufferedReader and FileReader.
		try{
			BufferedReader input = new BufferedReader(new FileReader(inputFile));
			String newLine;
			String[] newLineList;
			ArrayList<ArrayList<String>> table = new ArrayList<ArrayList<String>>();
			int lineCount = 0;
			int lastComma;
			int columnWidth = 0;//Initialized to prevent "might not have been initialized" error java keeps giving me...
			while ((newLine = input.readLine()) != null){//
				if (newLine.trim().equals("")){
					continue;
				}
				table.add(new ArrayList<String>());/*Takes n operations, since new space must be allocated when the
													 ArrayList has filled it's previously allocated space.*/
				lastComma = 0;
				newLineList = newLine.split(",");//Irrelevant for time complexity since newLine has a maximum length of 100000 characters.
				for (int i = 0; i < newLineList.length; i++){//Runs a max of 100001 times.
					table.get(lineCount).add(newLineList[i]);
				}
				if (lineCount == 0){
					columnWidth = table.get(lineCount).size();
				}
				else{
					if (table.get(lineCount).size() != columnWidth){
						throw new IOException("Table contains inconsistent number of columns.");
					}
				}
				lineCount++;
			}
			input.close();
			return table;
		} catch (IOException e) {
			throw new IOException("Can't find file" + inputFile);
		}
	}

	public static String[][] reformat(ArrayList<ArrayList<String>> inputData, String[] groups, String aggregation) throws IOException{//Runs in O(n) time.
		for (int i = 0; i < groups.length; i++){
			if (groups[i].equals(aggregation)){
				throw new IOException("Group column matches aggregation column.");
			}
		}
		int width = groups.length + 1;
		int height = inputData.size();
		String[][] outputData = new String[height][width];
		int[] columnIndices = new int[width];
		int cc = 0;
		boolean aggregationFound = false;
		for (int i = 0; i < inputData.get(0).size(); i++){//For all rows (except 0th row):
			for (int j = 0; j < groups.length; j++){//For all columns:
				if (inputData.get(0).get(i).equals(groups[j])){//If you find a group column entry, add it to the output data.
					outputData[0][cc] = groups[j];
					columnIndices[cc] = i;
					cc++;
					break;
				}
			}
			if (inputData.get(0).get(i).equals(aggregation)){//If you find the aggregation column's entry, add it to the last column of the output data.
				outputData[0][width-1] = aggregation;
				columnIndices[width-1] = i;
				aggregationFound = true;
			}
		}
		for (int i = 1; i < inputData.size(); i++){
			for (int j = 0; j < width; j++){
				outputData[i][j] = inputData.get(i).get(columnIndices[j]);
			}
		}
		if (cc < groups.length || !aggregationFound){
			throw new IOException("File does not contain requested columns.");
		}
		return outputData;
	}

	public static String[][] process(String[][] inputData, String operation) throws IOException{//Before calling a function, runs in O(1) time.
		ArrayList<String[]> groupEntries = new ArrayList<String[]>();
		groupEntries.add(inputData[0]);
		groupEntries.get(0)[inputData[0].length-1] = operation + "(" + inputData[0][inputData[0].length-1] + ")";
		if (operation.equals("count")){
			return count(inputData, groupEntries);
		}
		else if (operation.equals("sum")){
			return sum(inputData, groupEntries);
		}
		else if (operation.equals("avg")){
			return avg(inputData, groupEntries);
		}
		else if (operation.equals("count_distinct")){
			return count_distinct(inputData, groupEntries);
		}
		else{
			throw new IOException(operation + " is an invalid function.");
		}
	}

	public static String[][] count(String[][] inputData, ArrayList<String[]> groupEntries){//Runs in O(n^2) time.
		boolean present;
		for (int i = 1; i < inputData.length; i++){
			present = false;
			for (int j = 1; j < groupEntries.size(); j++){
				if (compareArrays(groupEntries.get(j), inputData[i], 0, inputData[i].length-1, 0, inputData[i].length-1)){
					groupEntries.get(j)[inputData[i].length-1] = String.valueOf(Integer.parseInt(groupEntries.get(j)[inputData[i].length-1]) + 1);
					present = true;
					break;
				}
			}
			if (!present){
				groupEntries.add(inputData[i]);
				groupEntries.get(groupEntries.size()-1)[inputData[i].length-1] = String.valueOf(1);
			}
		}
		String[][] outputData = new String[groupEntries.size()][groupEntries.get(0).length];
		for (int i = 0; i < outputData.length; i++){
			outputData[i] = groupEntries.get(i);
		}
		return outputData;
	}

	public static String[][] sum(String[][] inputData, ArrayList<String[]> groupEntries) throws IOException{//Runs in O(n^2) time.
		for (int i = 1; i < inputData.length; i++){
			try{
				Float.parseFloat(inputData[i][inputData[i].length-1]);
			} catch (Exception e) {
				throw new IOException("Cannot sum non-numerical data.");
			}
		}
		boolean present;
		String inputString;
		float inputValue;
		String outputString;
		float outputValue;
		for (int i = 1; i < inputData.length; i++){
			present = false;
			for (int j = 1; j < groupEntries.size(); j++){
				if (compareArrays(groupEntries.get(j), inputData[i], 0, inputData[i].length-1, 0, inputData[i].length-1)){
					inputString = inputData[i][inputData[i].length-1];
					inputValue = Float.parseFloat(inputString);
					outputString = groupEntries.get(j)[inputData[i].length-1];
					outputValue = Float.parseFloat(outputString);
					outputValue += inputValue;
					outputString = String.valueOf(outputValue);
					groupEntries.get(j)[inputData[i].length-1] = outputString;
					present = true;
					break;
				}
			}
			if (!present){
				groupEntries.add(inputData[i]);
			}
		}
		String[][] outputData = new String[groupEntries.size()][groupEntries.get(0).length];
		for (int i = 0; i < outputData.length; i++){
			outputData[i] = groupEntries.get(i);
		}
		return outputData;
	}

	public static String[][] avg(String[][] inputData, ArrayList<String[]> groupEntries) throws IOException{//Runs in O(n^2) time.
		for (int i = 1; i < inputData.length; i++){
			try{
				Float.parseFloat(inputData[i][inputData[i].length-1]);
			} catch (Exception e) {
				throw new IOException("Cannot average non-numerical data.");
			}
		}
		boolean present;
		String inputString;
		float inputValue;
		String outputString;
		float outputValue;
		ArrayList<Integer> count = new ArrayList<Integer>();
		for (int i = 1; i < inputData.length; i++){
			present = false;
			for (int j = 1; j < groupEntries.size(); j++){
				if (compareArrays(groupEntries.get(j), inputData[i], 0, inputData[i].length-1, 0, inputData[i].length-1)){
					inputString = inputData[i][inputData[i].length-1];
					inputValue = Float.parseFloat(inputString);
					outputString = groupEntries.get(j)[inputData[i].length-1];
					outputValue = Float.parseFloat(outputString);
					outputValue += inputValue;
					outputString = Float.toString(outputValue);
					groupEntries.get(j)[inputData[i].length-1] = outputString;
					count.set(j-1, count.get(j-1)+1);
					present = true;
					break;
				}
			}
			if (!present){
				groupEntries.add(inputData[i]);
				count.add(1);
			}
		}
		String[][] outputData = new String[groupEntries.size()][groupEntries.get(0).length];
		outputData[0] = groupEntries.get(0);
		for (int i = 1; i < outputData.length; i++){
			outputData[i] = groupEntries.get(i);
			outputData[i][groupEntries.get(0).length-1] = Float.toString(Float.parseFloat(outputData[i][groupEntries.get(0).length-1]) / count.get(i-1));
		}
		return outputData;
	}

	public static String[][] count_distinct(String[][] inputData, ArrayList<String[]> groupEntries){//Runs in O(n^2) time.
		for (int i = 1; i < inputData.length; i++){//Copy inputData into groupEntries.
			groupEntries.add(inputData[i]);
		}
		for (int i = 1; i < groupEntries.size(); i++){//Remove duplicates.
			for (int j = i+1; j < groupEntries.size(); j++){
				if (compareArrays(groupEntries.get(i), groupEntries.get(j), 0, groupEntries.get(i).length, 0, groupEntries.get(j).length)){
					groupEntries.remove(j);
					j--;
				}
			}
		}
		for (int i = 1; i < groupEntries.size(); i++){//Set all counts to 1.
			groupEntries.get(i)[groupEntries.get(i).length-1] = String.valueOf(1);
		}
		for (int i = 1; i < groupEntries.size(); i++){//If rows of the same group are found, remove the second one and increment the first one's count.
			for (int j = i+1; j < groupEntries.size(); j++){
				if (compareArrays(groupEntries.get(i), groupEntries.get(j), 0, groupEntries.get(i).length-1, 0, groupEntries.get(j).length-1)){
					groupEntries.get(i)[groupEntries.get(i).length-1] = String.valueOf(Integer.parseInt(groupEntries.get(i)[groupEntries.get(i).length-1])+1);
					groupEntries.remove(j);
					j--;
				}
			}
		}
		String[][] outputData = new String[groupEntries.size()][groupEntries.get(0).length];
		for (int i = 0; i < outputData.length; i++){
			outputData[i] = groupEntries.get(i);
		}
		return outputData;
	}

	public static boolean compareArrays(String[] array1, String[] array2, int l1, int r1, int l2, int r2){//Runs in O(1) time.
		if (r1-l1!=r2-l2){
			return false;
		}
		for (int i = l1, j = l2; i < r1; i++, j++){
			if (!array1[i].equals(array2[j])){
				return false;
			}
		}
		return true;
	}

	public static void outputCSV(String[][] outputData){
		String outputLine;
		for (int i = 0; i < outputData.length; i++){
			outputLine = new String();
			for (int j = 0; j < outputData[i].length; j++){
				outputLine += outputData[i][j] + ",";
			}
			if (outputLine.length() > 0){
				outputLine = outputLine.substring(0, outputLine.length()-1);
			}
			System.out.println(outputLine);
		}
	}

	public static void main(String[] args) throws IOException{//Total program runs in O(n^2) time.

		//Process arguments.
		String operation = args[0];

		String aggregation = args[1];

		String fileName = args[2];

		String[] groups = new String[args.length-3];
		for (int i = 3; i < args.length; i++){
			groups[i-3] = args[i];
		}

		ArrayList<ArrayList<String>> table1 = readCSV(fileName);

		String[][] table2 = reformat(table1, groups, aggregation);//Removes unnecessary columns.

		String[][] table3 = process(table2, operation);//Processes data based on specified function.

		outputCSV(table3);//Prints data in csv format to standard out.
	}
}
