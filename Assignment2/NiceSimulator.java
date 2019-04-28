/*
 * Name: Parm Johal
 * ID: V00787710
 * Date: July 9, 2018
 * Filename: NiceSimulator.java
 * Details: \CSC 225\ Assignment 2
 */


import java.io.*;
import java.util.*;

public class NiceSimulator{

	public static final int SIMULATE_IDLE = -2;
	public static final int SIMULATE_NONE_FINISHED = -1;
	public Task[] taskHolder;
	public Task[] heap;
	public int[] heapPosition;
	public int size;
	
	
	/* Constructor(maxTasks)
	   Instantiate the data structure with the provided maximum 
	   number of tasks. No more than maxTasks different tasks will
	   be simultaneously added to the simulator, and additionally
	   you may assume that all task IDs will be in the range
	     0, 1, ..., maxTasks - 1
	*/
	public NiceSimulator(int maxTasks){
		taskHolder = new Task[maxTasks];
		heap = new Task[maxTasks + 1];
		size = 0;
		heapPosition = new int[maxTasks];
	}
	
	/* taskValid(taskID)
	   Given a task ID, return true if the ID is currently
	   in use by a valid task (i.e. a task with at least 1
	   unit of time remaining) and false otherwise.
	   
	   Note that you should include logic to check whether 
	   the ID is outside the valid range 0, 1, ..., maxTasks - 1
	   of task indices.
	
	*/
	public boolean taskValid(int taskID){ // runtime is O(1) as pulling from an array index is constant time
		if(taskID >= 0 && taskID <= taskHolder.length-1 && taskHolder[taskID] != null) {
			return taskHolder[taskID].timeReq > 0;
		}
		return false;
	}
	
	
	/* getPriority(taskID)
	   Return the current priority value for the provided
	   task ID. You may assume that the task ID provided
	   is valid.
	
	*/
	public int getPriority(int taskID){// runtime is O(1) as pulling from an array index is constant time
		return taskHolder[taskID].niceLevel;
	}
	
	
	/* getRemaining(taskID)
	   Given a task ID, return the number of timesteps
	   remaining before the task completes. You may assume
	   that the task ID provided is valid.
	
	*/
	public int getRemaining(int taskID){ // runtime is O(1) as pulling from an array index is constant time
		return taskHolder[taskID].timeReq;
	}
	
	
	/* add(taskID, time_required)
	   Add a task with the provided task ID and time requirement
	   to the system. You may assume that the provided task ID is in
	   the correct range and is not a currently-active task.
	   The new task will be assigned nice level 0.
	*/
	public void add(int taskID, int time_required){       // runtime is O(logn) time as the add method places the task at the end of the array
		Task add_task = new Task(taskID, time_required);  // and bubbles up to the parent until in place.

		//adding to the taskHolder array
		taskHolder[taskID] = add_task;
		
		//adding to the heap
		if(size == heap.length - 1) {
			System.out.println("task holder is full.");
		} else {
			heap[size] = add_task;
			bubbleUp(size);

		} // end if
		size++;
	} // end add
	
	
	/* kill(taskID)
	   Delete the task with the provided task ID from the system.
	   You may assume that the provided task ID is in the correct
	   range and is a currently-active task.
	*/
	public void kill(int taskID){  				//runtime is O(logn) time as the method switches the item to kill and the last
		int bubbleTask = heapPosition[taskID];  //element in the array and bubbles down the switched item with the correct child.
		//kill item the taskHolder array
		Task findTask = taskHolder[taskID];
		taskHolder[taskID] = null;
		size--;
		
		//kill item in the heap
		swap(heap, heapPosition[taskID], size);

		heap[size] = null; //use heapPosition array to find the correct position of task.

		int index = bubbleTask;
		Task t = heap[index];
		bubbleUp(index);
		if (t == heap[index]){
			bubbleDown(index);
		}
	}
	
	
	/* renice(taskID, new_priority)
	   Change the priority of the the provided task ID to the new priority
       value provided. The change must take effect at the next simulate() step.
	   You may assume that the provided task ID is in the correct
	   range and is a currently-active task.
	
	*/
	public void renice(int taskID, int new_priority){ //runtime is O(logn) as the method sets a new priority to the task and stores it in another
													  //variable, while using kill() to restructure it and bubble it up by parent.
		taskHolder[taskID].niceLevel = new_priority;
		bubbleUp(heapPosition[taskID]);
		bubbleDown(heapPosition[taskID]);
		
	}
	
	
	/* simulate()
	   Run one step of the simulation:
		 - If no tasks are left in the system, the CPU is idle, so return
		   the value SIMULATE_IDLE.
		 - Identify the next task to run based on the criteria given in the
		   specification (tasks with the lowest priority value are ranked first,
		   and if multiple tasks have the lowest priority value, choose the 
		   task with the lowest task ID).
		 - Subtract one from the chosen task's time requirement (since it is
		   being run for one step). If the task now requires 0 units of time,
		   it has finished, so remove it from the system and return its task ID.
		 - If the task did not finish, return SIMULATE_NONE_FINISHED.
	*/
	public int simulate(){ // runs in O(logn) time worst case as it uses the kill method to return the id after a task has finished up.
		if(size == 0) {
			return SIMULATE_IDLE;
		}
		Task t = heap[0];
		t.timeReq--;
		if(t.timeReq == 0) {
			int id = t.task_ID;
			kill(id);
			return id;
			
		} else {
			return SIMULATE_NONE_FINISHED;
		}
		
	}
	
	public class Task { // task class to store task data
		public int task_ID;
		public int timeReq;
		public int niceLevel;
		
		public Task(int task_ID, int timeReq) {
			this.task_ID = task_ID;
			this.timeReq = timeReq;
			niceLevel = 0;
		}
		public int compareTo(Task t2){
			if(t2.niceLevel < this.niceLevel) {
				return 1;
			}
			else if(t2.niceLevel > this.niceLevel) {
				return -1;
			}
			else if(t2.niceLevel == this.niceLevel && t2.niceLevel < this.niceLevel) {
				return 1;
			}
			else if(t2.niceLevel == this.niceLevel && t2.niceLevel > this.niceLevel) {
				return -1;
			}
			else return 0;
		}
		public String toString() {
			return task_ID + " " + niceLevel;
		}
	}
	
	//Navigation helpers
	public int parent(int index) {
		return (index+1)/2-1;
	}
	public int leftChild(int index) {
		return (index+1)*2-1;
	}
	public int rightChild(int index) {
		return (index+1)*2;
	}
	public boolean hasParent(int index) {
		return index > 1;
	}
	public boolean hasLeftChild(int index) {
		return leftChild(index) < size;
	}
	public boolean hasRightChild(int index) {
		return rightChild(index) < size;
	}
	public void swap(Task[] array, int index1, int index2) { // used to swap 2 tasks, used for parent/child swaps in the heap array.
		Task temp1 = array[index1];
		array[index1] = array[index2];
		array[index2] = temp1;
		int id1 = heap[index1].task_ID;
		int id2 = heap[index2].task_ID;
		heapPosition[id1] = index1;
		heapPosition[id2] = index2;
	}
	
	public void bubbleUp(int index) {
		if(index == 0) {return;}
		if(heap[index].compareTo(heap[parent(index)]) < 0) {
			swap(heap, parent(index), index);
			bubbleUp(parent(index));
		}
	}
	
	public void bubbleDown(int index) {
		if(!hasLeftChild(index)) {return;}
		if(hasLeftChild(index) && !hasRightChild(index)) {
			if(heap[leftChild(index)].compareTo(heap[index]) < 0) {
				swap(heap, index, leftChild(index));
				bubbleDown(leftChild(index));
			} else{return;}
		} else {
			if(heap[leftChild(index)].compareTo(heap[rightChild(index)]) < 0) {
				if(heap[index].compareTo(heap[leftChild(index)]) > 0) {
					swap(heap, index, leftChild(index));
					bubbleDown(leftChild(index));
				} else {return;}
			}
			else {
				if(heap[index].compareTo(heap[rightChild(index)]) > 0) {
				swap(heap, index, rightChild(index));
				bubbleDown(rightChild(index));
				} else {return;}
			}
		}
	}
}
	
	
