/* Ladder.java
   CSC 225 - Summer 2018
   
   Parm Johal
   Student id : V00787710
   August 5, 2018
*/


import java.io.*;
import java.util.*;

	// WordNode class used to keep track of word, parent, neighbours and whether it's been visited.
	class WordNode {
		public String word;
		public WordNode parent;
		public boolean visited;
		public ArrayList<WordNode> neighbours;
		
		public WordNode(String newWord) {
			word = newWord;
			parent = null;
			visited = false;
			neighbours = new ArrayList<WordNode>();
		}
		
		public void setArraySize(int size) {
			neighbours = new ArrayList<WordNode>(size);
		}
	}

public class Ladder {

	
	public static void showUsage(){
		System.err.printf("Usage: java Ladder <word list file> <start word> <end word>\n");
	}
	

	public static void main(String[] args){
		
		//At least four arguments are needed
		if (args.length < 3){
			showUsage();
			return;
		}
		String wordListFile = args[0];
		String startWord = args[1].trim();
		String endWord = args[2].trim();
		
		
		//Read the contents of the word list file into a LinkedList (requires O(nk) time for
		//a list of n words whose maximum length is k).
		//(Feel free to use a different data structure)
		BufferedReader br = null;
		ArrayList<WordNode> words = new ArrayList<WordNode>();
		
		try{
			br = new BufferedReader(new FileReader(wordListFile));
		}catch( IOException e ){
			System.err.printf("Error: Unable to open file %s\n",wordListFile);
			return;
		}
		
		try{
			for (String nextLine = br.readLine(); nextLine != null; nextLine = br.readLine()){
				nextLine = nextLine.trim();
				if (nextLine.equals(""))
					continue; //Ignore blank lines
				//Verify that the line contains only lowercase letters
				for(int ci = 0; ci < nextLine.length(); ci++){
					//The test for lowercase values below is not really a good idea, but
					//unfortunately the provided Character.isLowerCase() method is not
					//strict enough about what is considered a lowercase letter.
					if ( nextLine.charAt(ci) < 'a' || nextLine.charAt(ci) > 'z' ){
						System.err.printf("Error: Word \"%s\" is invalid.\n", nextLine);
						return;
					}
				}
				WordNode s = new WordNode(nextLine);
				words.add(s);
			}
		} catch (IOException e){
			System.err.printf("Error reading file\n");
			return;
		}

		/* Find a word ladder between the two specified words. Ensure that the output format matches the assignment exactly. */
		
		for(WordNode n : words) { // bounds the ArrayList size.
			n.setArraySize(words.size()-1);
		}
		
		//checks to see if startWord and endword are in the list.
		boolean sInList = false;
		for(WordNode n : words) {
			if(startWord.equals(n.word)) {sInList = true;}
		}
		boolean eInList = false;
		for(WordNode n : words) {
			if(endWord.equals(n.word)) {eInList = true;}
		}
		if(sInList == false || eInList == false) {
			System.out.println("no word ladder found.");
			return;
		}
		
		// checks if startWord is the same length as endWord.
		if(startWord.length() != endWord.length()) {
			System.out.println("No word ladder found.");
			return;
			
		} else if(startWord.equals(endWord)) { // if startWord is the same as endWord
			System.out.println(startWord);
			
		} else { //continue with the list structure and the start/end words.
		
		ArrayList<WordNode> adjList = makeList(words); //method where words that are 1 letter different are adjacent to each other.
		
		for (WordNode n : adjList) {
			System.out.println("printing word:");
			System.out.println(n.word);
			System.out.println("printing neighbours");
		}
		
		LinkedList<WordNode> shortestPath = BFS(startWord, endWord, adjList); // uses a BFS traversal with a Queue to create the shortes ladder.
		
		
		stringThePath(shortestPath); // Prints the words creating the shortest ladder from startWord to endWord.
		
		} // end if
	} // end main
	
	
	/* This method creates a graph representation of an adjacency list
	 * using nested for loops and adding neighbours to each WordNode's
	 * ArrayList data. Runtime is O(n^2).
	*/
	public static ArrayList<WordNode> makeList(ArrayList<WordNode> list) {
		for(int i = 0; i < list.size(); i++) {
			for(int j = 0; j < list.size(); j++) {
				
				if(isAdjacent(list.get(i).word, list.get(j).word)) {
					list.get(i).neighbours.add(list.get(j)); // adds jth vertex in the correct index
				} // end if
			} // end for
		} // end for
		
		return list;
				
	} // end makeGraph
	
	//This method has constant runtime since it compares 2 strings and returns true if they differ by one letter.
	public static boolean isAdjacent(String a, String b) { // This method compares 2 words. This method runs in constant time according to the specifications given.
		if(a.length() != b.length()) {return false;} // end if
		int count = 0;
		for(int i = 0; i < a.length(); i++) {
			if(a.charAt(i) != b.charAt(i)) {
				count++;
			} // end if
		} // end for
		if(count == 1) {
			return true;
		} // end if
		return false;
	} // end isAdjacent
			
	
	// This method traverses the adjacency list using BFS and keeps track of
	// The current node's parent after each visit with a queue.
	// Then it traverses backwards from end word to start word using the parent references.
	// runtime is O(n^2).
	public static LinkedList<WordNode> BFS(String start, String end, ArrayList<WordNode> list) {
		
		Queue<WordNode> qWords = new LinkedList<WordNode>(); //create queue of type WordNode.
		
		for(int i = 0; i < list.size(); i++) { // iterates through the list to find and queue up the start word node
			if(start.equals(list.get(i).word)) {
				qWords.add(list.get(i));
				list.get(i).visited = true; // // mark start word as visited
				list.get(i).parent = list.get(i); // list start word as its own parent
			} // end if
		} // end for
		
		while(!qWords.isEmpty()) {//while queue is non-empty do
			WordNode v = qWords.poll(); //v = dequeue Q
			for(int i = 0; i < v.neighbours.size(); i++) {
				if(v.neighbours.get(i).visited == false) {
					v.neighbours.get(i).visited = true;
					v.neighbours.get(i).parent = v;
					qWords.add(v.neighbours.get(i));
				} // end if
			} // end for
		} // end while
		
		LinkedList<WordNode> shortList = new LinkedList<>(); // create a list to put follow and place the words in from end to start.
		
		WordNode qNode1 = null;
		for(int i = 0; i < list.size(); i++) {
			if(end.equals(list.get(i).word)) {
				qNode1 = list.get(i);
				break;
			} // end if
		} // end for
		
		while(!qNode1.word.equals(start)) {
			shortList.addFirst(qNode1);
			qNode1 = qNode1.parent;
		} // end while
		
		for(int i = 0; i < list.size(); i++) {
			if(start.equals(list.get(i).word)) {
				shortList.addFirst(list.get(i));
				break;
			} // end if
		} // end for
		
		return shortList;
		
	} // end BFS
	
	//Prints the shortest path to the console.
	public static void stringThePath(LinkedList<WordNode> path) {
		System.out.println("Printing Ladder:");
		for(int i = 0; i < path.size(); i++) {
			System.out.println(path.get(i).word);
		} // end for
	} // end stringThePath
} // end Ladder