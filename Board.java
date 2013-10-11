//Patrick D'Errico
//CSC 380 - AI
//For Dr. Kim

//This function creates a board object, representing teach space on a backgammon board

import java.util.*;
//import java.lang.*;

public class Board {

	//location 0 is the P1 bar, and location 25 is the P2 bar
	private int[] spaces; //Player 1 moves up from 1 to 24 and is positive, P2 moves from 24 to 1 and is negative
	//State 0 - normal, 1 - endgame, 2 - bar, 3 - win
	
	public Board(int[] oldboard) {
		spaces = oldboard;
	}
	
	public Board() {
		spaces = new int[26];
		
		//Intialize the board to a fresh one
		spaces[1] = 2;
		spaces[6] = -5;
		spaces[8] = -3;
		spaces[12] = 5;
		spaces[13] = -5;
		spaces[17] = 3;
		spaces[19] = 5;
		spaces[24] = -2;
	}
	
	public int[] getboard() { //Returns the integer array of the board
		return spaces;
	}
	
	public void setboard(int[] board) { //Takes a board array and adds it to this object
		spaces = board;
	}
	
	public int checkstate(int player, int[] board) { //Checks the state of the board - State 0 - normal, 1 - endgame, 2 - bar, 3 - win
	
		for (int i=0; i<26; i++) { //Check for win
		
			if ((player == 1 && board[i] > 0) || (player == 2 && board[i] < 0)) //If pieces are on the board, not a win
				break;
		
			if (i == 25)
				return 3;
				
		}
		
		if ((player == 1 && board[0] > 0) || (player == 2 && board[25] < 0)) //Check for bar
			return 2;
			
		for (int i=0; i<26; i++) { //Check for endgame
	
			if ((player == 1 && i < 19 && board[i] > 0) || (player == 2 && i > 6 && board[i] < 0))
				return 0; //must be normal if a piece is outside the area
				
		}

		return 1; //else in endgame
	
	}
	
	public int checkstate(int player) {
	
		for (int i=0; i<26; i++) { //Check for win
		
			if ((player == 1 && spaces[i] > 0) || (player == 2 && spaces[i] < 0))
				break;
		
			if (i == 25)
				return 3;
				
		}
		
		if ((player == 1 && spaces[0] > 0) || (player == 2 && spaces[25] < 0)) //Check for bar
			return 2;
			
		for (int i=0; i<26; i++) { //Check for endgame
	
			if ((player == 1 && i < 19 && spaces[i] > 0) || (player == 2 && i > 6 && spaces[i] < 0))
				return 1; //must be normal if a piece is outside the area
				
		}

		return 2; //else in endgame
	
	}
	
	public LinkedList<int[]> getmoves(int player, int d1, int d2) {
		
		LinkedList<int[]> movequeue = new LinkedList<int[]>();
		LinkedList<boolean[]> movediequeue = new LinkedList<boolean[]>();	
		//Lists holding all moves in this and the next iteration of the board, and the state of the dice used for each state.
		//These are updated together to maintain order
		
		boolean[] falsestart = new boolean[4];
		falsestart[0] = false;
		falsestart[1] = false;
		falsestart[2] = false;
		falsestart[3] = false;
		//At the beginning each state has not used any dice

		movequeue.add(spaces);
		movediequeue.add(falsestart);

		LinkedList<int[]> dumpqueue = new LinkedList<int[]>();
		LinkedList<boolean[]> dumpdiequeue = new LinkedList<boolean[]>();
		//These dump lists hold all states visited in the tour of locations
		
		dumpqueue.add(spaces);
		dumpdiequeue.add(falsestart);

		int movesleft = 2;
		if (d1 == d2) {
			movesleft = 4;
		}
		
		int totalmoves = movesleft;
		
		int[] dice = new int[movesleft];
		dice[0] = d1;
		dice[1] = d2;
		if (movesleft > 2) {
			dice[2] = d1;
			dice[3] = d1;
		}
		
		//Set the dice array to each of the dice values, doubles include 4 of the same
			
		while (movesleft > 0) { //Only branch for the number of moves you have
		
			int links = movequeue.size(); //Get how many places you can branch from
		
			for (int linkcount=0; linkcount < links; linkcount++) { //For each, repeat
			
				int[] thisboard = movequeue.remove(); //Get the board's status
				boolean[] thisstate = movediequeue.remove();			
			
				if ((player == 1 && checkstate(1,thisboard) == 0) || (player == 2 && checkstate(2,thisboard) == 0)) { //State 0
					for (int pos = 1; pos <= 24; pos++) {
						if ((player == 1 && thisboard[pos] > 0) || (player == 2 && thisboard[pos] < 0)) { //For every position that the current player has pieces
						
							for (int curdie = 0; curdie < totalmoves; curdie++) { //For each die, see if you can move that piece that many spaces
								boolean isvalid = iszeromovevalid(player, thisboard, pos, dice[curdie]); //Check validity
								if (isvalid && thisstate[curdie] == false) {
									int[] tempboard = makezeromove(player, thisboard, pos, dice[curdie]); //Make move if die has not been used yet and move is legal						
									
									movequeue.add(tempboard);
									
									boolean[] tempstate = new boolean[4];
									for (int q=0; q<4; q++) {
										tempstate[q] = thisstate[q];
									}
									
									boolean done = false; //Update th dice state array for this new move
									
									if (!done && (dice[curdie]) == d1) {
										tempstate[0] = true;
										done = true;
									}
									else { 
										if (!done && (dice[curdie]) == d2) {
											tempstate[1] = true;
											done = true;
										}
										else {
											if (!done && (tempstate[0] && tempstate[1])) {
												tempstate[2] = true;
												done = true;
											}
											else {
												if (!done && (tempstate[0] && tempstate[1] && tempstate[2])) {
													tempstate[3] = true;
													done = true;
												}										
											}
										}
									}
									
									movediequeue.add(tempstate); //Add move data to movequeue
								}
								
							}
						}
					}				
				}
				
				//The same logic as for state 0 applies for state 1 and 2, so the comments are not repeated for the sake of the writer.
				//Aside for the differences in checking if a move is vaild, the same logic applies.
				
				if ((player == 1 && checkstate(1,thisboard) == 1) || (player == 2 && checkstate(2,thisboard) == 1)) { //State 1
					for (int pos = 1; pos <= 6; pos++) {
						if ((player == 1 && thisboard[18+pos] > 0) || (player == 2 && thisboard[pos] < 0)) {
							for (int curdie = 0; curdie < totalmoves; curdie++) {
								boolean isvalid = isonemovevalid(player, thisboard, pos, dice[curdie]);							
								if (isvalid && thisstate[curdie] == false) {
									int[] tempboard = makeonemove(player, thisboard, pos, dice[curdie]);
									movequeue.add(tempboard);
									
									boolean[] tempstate = new boolean[4];
									for (int q=0; q<4; q++) {
										tempstate[q] = thisstate[q];
									}
									
									boolean done = false;
									
									if (!done && (dice[curdie]) == d1) {
										tempstate[0] = true;
										done = true;
									}
									else { 
										if (!done && (dice[curdie]) == d2) {
											tempstate[1] = true;
											done = true;
										}
										else {
											if (!done && (tempstate[0] && tempstate[1])) {
												tempstate[2] = true;
												done = true;
											}
											else {
												if (!done && (tempstate[0] && tempstate[1] && tempstate[2])) {
													tempstate[3] = true;
													done = true;
												}										
											}
										}
									}
									
									movediequeue.add(tempstate);
									
								}
							}
						}
					}					
				}
				
				if ((player == 1 && checkstate(1,thisboard) == 2) || (player == 2 && checkstate(2,thisboard) == 2)) { //State 2
					for (int pos = 1; pos <= 6; pos++) {
						for (int curdie = 0; curdie < totalmoves; curdie++) {
							if (((player == 1 && thisboard[pos] >= -1 && pos == dice[curdie]) || (player == 2 && thisboard[25-pos] <= 1 && pos == dice[curdie])) && thisstate[curdie] == false) {
								int[] tempboard = maketwomove(player, thisboard, dice[curdie]);
								movequeue.add(tempboard);
								
								boolean[] tempstate = new boolean[4];
								for (int q=0; q<4; q++) {
									tempstate[q] = thisstate[q];
								}
								
								boolean done = false;
								
								if (!done && (dice[curdie]) == d1) {
									tempstate[0] = true;
									done = true;
								}
								else { 
									if (!done && (dice[curdie]) == d2) {
										tempstate[1] = true;
										done = true;
									}
									else {
										if (!done && (tempstate[0] && tempstate[1])) {
											tempstate[2] = true;
											done = true;
										}
										else {
											if (!done && (tempstate[0] && tempstate[1] && tempstate[2])) {
												tempstate[3] = true;
												done = true;
											}										
										}
									}
								}

								movediequeue.add(tempstate);
								
							}
						}
					}					
				}
				
			}
			
			//Move up the lists
			//if movequeue has a size of 0, no valid moves
			movesleft--;
			
			for (int i=0; i<movequeue.size(); i++) { //Add all moves from this round to the dump
				dumpqueue.add((movequeue.get(i)));
				dumpdiequeue.add((movediequeue.get(i)));
			}
			
		}

		//check the one die moving state
		LinkedList<int[]> finalqueue = new LinkedList<int[]>();
		
		boolean[] states = new boolean[4];
		
		if (d1 != d2) {
			states[0] = false; //Can a move with both be done?
			states[1] = false; //How about one with the first die and not the second?
			states[2] = false; //How about the other way around?
			states[3] = (d1 > d2); //Is the first die larger than the second?
			
			for (int i=0; i<dumpqueue.size(); i++) { //Find which of the 4 states hold
				boolean[] test = dumpdiequeue.get(i);
				states[0] = (states[0] || (test[0] == true && test[1] == true));
				states[1] = (states[1] || (test[0] == true && test[1] == false));
				states[2] = (states[2] || (test[0] == false && test[1] == true));
			}
			
			for (int i=0; i<dumpqueue.size(); i++) {
				boolean[] test = dumpdiequeue.get(i);
				int[] thisboard = dumpqueue.get(i);
				
				if (states[0] && (test[0] && test[1])) { //State 0 takes precidence if it is the case
					finalqueue.add(thisboard);
				}
					
				if (!states[0] && ((states[1] && states[3] && test[0] && !test[1]) || (states[2] && !states[3] && !test[0] && test[1]))) { //Otherwise make sure the one die is the highest
					finalqueue.add(thisboard);
				}
				
			}			
			
		}
		else { //For doubles, this process is even simpler - the highest state index takes precidence.
			states[0] = false; //d1
			states[1] = false; //d2
			states[2] = false; //d3
			states[3] = false; //d4
			
			for (int i=0; i<dumpqueue.size(); i++) {
				boolean[] test = dumpdiequeue.get(i);
				states[0] = (states[0] || test[0] == true);
				states[1] = (states[1] || test[1] == true);
				states[2] = (states[2] || test[2] == true);
				states[3] = (states[3] || test[3] == true);
			}

			for (int i=0; i<dumpqueue.size(); i++) {
				boolean[] test = dumpdiequeue.get(i);
				int[] thisboard = dumpqueue.get(i);
				
				if (states[3] && (test[0] && test[1] && test[2] && test[3])) {
					finalqueue.add(thisboard);
				}	

				if (!states[3] && states[2] && (test[0] && test[1] && test[2])) {
					finalqueue.add(thisboard);
				}
					
				if (!states[3] && !states[2] && states[1] && (test[0] && test[1])) {
					finalqueue.add(thisboard);
				}
					
				if (!states[3] && !states[2] && !states[1] && states[0] && test[0]) {
					finalqueue.add(thisboard);
				}
				
			}				

		}
		
		return finalqueue; //maybe not this one
			
	}	
	
	
	public boolean iszeromovevalid(int player, int[] curboard, int s, int m) { //Checks to see if a move in the zero state is valid
	
		boolean result = false;
	
		int playermulti = 1;
		if (player == 2)
			playermulti = -1;
		
		if (((s+(playermulti*m)) <= 24) && ((s+(playermulti*m)) >= 1)) { //assure we're not falling off the board
			
			//assure we don't move in an illegal spot
			if ((curboard[s+(playermulti*m)] <= -playermulti && player == 2) || (curboard[s+(playermulti*m)] >= -playermulti && player == 1))
				result = true;

		}

		return result;
	
	}
	
	public int[] makezeromove(int player, int[] curboard, int s, int m) { //Moves a zero state move, should check for validity first
	
		int[] result = new int[26];
		for (int i=0; i<25; i++)
			result[i] = curboard[i];
		
		int playermulti = 1;
		if (player == 2)
			playermulti = -1;

		result[s] = result[s] - playermulti;
		if (result[s+(playermulti*m)] != -playermulti) //Move piece
			result[s+(playermulti*m)] = result[s+(playermulti*m)] + playermulti;
		else { //Captures occur on blots
			result[s+(playermulti*m)] = playermulti;
			if (player == 1)
				result[25] = result[25] - 1;
			else
				result[0] = result[0] + 1;
			
		}	
			
		return result;
	
	}
	
	public boolean isonemovevalid(int player, int[] curboard, int s, int m) { //Checks to see if a move in the one state is valid
	
		int playermulti = 1;
		if (player == 2)
			playermulti = -1;
		
		if ((player == 1 && s+m > 24) || (player == 2 && s-m < 1)) { //moves off the board if not true
			if (player == 1) {
				for (int i = 19; i < s; i++) { //Player 1
					if (curboard[i] != 0 && s+m > 25) //trying to remove a spot from a higher roll when higher ones remain
						return false; //can't move off if an earlier piece remains
						
				}
			}
			else {
				for (int i = 6; i > s; i--) { //Player 2
					if (curboard[i] != 0 && s-m < 0) //trying to remove a spot from a higher roll when higher ones remain
						return false; //can't move off if an earlier piece remains
						
				}			
			}
		}
		else { //not removing a spot
			if ((curboard[s+(playermulti*m)] <= -playermulti && player == 2) || (curboard[s+(playermulti*m)] >= -playermulti && player == 1))
				return true;
			else
				return false;
					
		}
		
		return true;
	
	}
	
	public int[] makeonemove(int player, int[] curboard, int s, int m) { //Makes a state 1 move, check for validity first
	
		int[] result = new int[26];
		for (int i=0; i<25; i++)
			result[i] = curboard[i];
		
		int playermulti = 1;
		if (player == 2)
			playermulti = -1;
		
		if ((player == 1 && s+m > 24) || (player == 2 && s-m < 1)) { //moves off the board, double checks
			result[s] = result[s] - playermulti;
		}
		else { //exactly like a normal move
			result[s] = result[s] - playermulti;
			if (result[s+(playermulti*m)] != -playermulti) //Move piece
				result[s+(playermulti*m)] = result[s+(playermulti*m)] + playermulti;
			else { //Capture
				result[s+(playermulti*m)] = playermulti;
				if (player == 1)
					result[25] = result[25] - 1;
				else
					result[0] = result[0] + 1;
				
			}				
		}
			
		return result;
	
	}
	
	public int[] maketwomove(int player, int[] curboard, int m) { //Makes a move given the two state, validity is easy enough to check outside of another function
	
		int[] result = new int[26];
		for (int i=0; i<25; i++)
			result[i] = curboard[i];
		
		int playermulti = 1;
		if (player == 2)
			playermulti = -1;

		int epos = m; //Generate entry position
		if (player == 2)
			epos = 25-epos;		
		
		if (player == 2)
			result[0] = result[0] - 1;
		else
			result[25] = result[25] + 1;
		
		if (result[epos] != -playermulti) //Move piece
			result[epos] = playermulti;
		else { //Capture
			result[epos] = playermulti;
			if (player == 2)
				result[25] = result[25] - 1;
			else
				result[0] = result[0] + 1;
			
		}				
			
		return result;
	
	}
	
	public int heuristic() { //provides heuristic score
	
		int h = 0;
	
		//Add Player 1, subtract player 2
		for (int i=1; i<=24; i++) {
		
			if (spaces[i] > 1) {
				
				int ts = 50;
				if (i > 1 && spaces[i-1] > 1) //Consecutive points are even better
					ts = ts + 20;
				if (i < 24 && spaces[i+1] > 1)
					ts = ts + 20;
				if (i == 20)
					ts = ts + 30;
				if (i == 21 || i == 19)
					ts = ts + 25;
				if (i == 22)
					ts = ts + 20;
				
				ts = ts - 3*(spaces[i]-2); //Take off a bit for unneeded additions to a dupe
				
				h = h + ts;
					
			}
			if (spaces[i] == 1) //Blots are bad
				h = h - 2*i;
				
		}
		
		h = h - 200*spaces[0]; //Captured pieces are really bad
		h = h + 125*(15-menleft(1)); //Removing pieces is very good
		
		//Same logic applies for player 2 as above
		//However the points are subtracted - positive - better for Player 1, and vice versa
		
		for (int i=1; i<=24; i++) {
				
			if (spaces[i] < -1) {
				
				int ts = -50;
				if (i > 1 && spaces[i-1] < -1)
					ts = ts - 20;
				if (i < 24 && spaces[i+1] < -1)
					ts = ts - 20;
				if (i == 5)
					ts = ts - 30;
				if (i == 4 || i == 6)
					ts = ts - 25;
				if (i == 3)
					ts = ts - 20;
				
				ts = ts + 3*(spaces[i]+2);
				
				h = h + ts;
					
			}
			if (spaces[i] == -1)
				h = h + 2*(25-i);
				
		}
		
		h = h + 200*spaces[25];
		h = h - 125*(15-menleft(2));		
			
		return h;
	
	}
	
	public int menleft(int player) { //gets number of men a player has left
	
		int p = 0;
		
		int playermulti = 1;
		if (player == 2)
			playermulti = -1;
	
		for (int i=1; i<=24; i++) {
			if (playermulti * spaces[i] > 0)
				p = p + (playermulti * spaces[i]);
				
		}	
			
		return p;
	
	}
	
}