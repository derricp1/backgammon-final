//Patrick D'Errico
//CSC 380 - AI
//For Dr. Kim

//This function creates a backgammon playing object and runs it.

import java.text.*;
import java.util.*;
import java.lang.*;

import java.util.Scanner;

public class BackPlayer {

	public BackPlayer() {
		//The board is kept out of the object itself, out of preference.
	}
	
	public void play() {
	
		Board board = new Board(); //Creates a board object with the opening position
		
		StdDraw.setCanvasSize(870, 512);
		StdDraw.setXscale(0, 170);
		StdDraw.setYscale(0, 100);
		StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
		StdDraw.filledRectangle(85, 50, 85, 50); 
		//Sets up StdDraw
		
		int die1 = 1;
		int die2 = 1;
		Random rand = new Random(); //inits rand
		
		while (die1 == die2) { //Get who goes first (no doubles)
			die1 = rand.nextInt(5) + 1;
			die2 = rand.nextInt(5) + 1;
		}
		
		drawboard(board);
		drawdice(die1, die2);
		//Draws board
		
		int playerpos = 2;
		if (die1 > die2) { //Select first player
			playerpos = 1;
			System.out.println("You go first. You are red.");
		}
		else
			System.out.println("I go first. You are black");
		
		
		int curplayer = 1; //Starts the game with player 1
		boolean finished = false; //Is game over?
		int[] thisboard = board.getboard(); //Board to array
		double[] bup = new double[27];
		
		while (!finished) {
		
			drawdice(die1, die2);
		
			if (curplayer != playerpos) { //CPU Moves
			
				System.out.println("My Turn.");
				//Stopwatch sw = new Stopwatch();
			
				if (curplayer == 1) { //Calls mini or max depending on which player the computer is
					bup = minimaxmax(board, curplayer, -99999, 99999, 1, die1, die2);
				}
				else {
					bup = minimaxmini(board, curplayer, -99999, 99999, 1, die1, die2);
				}
				
				//System.out.println(sw.elapsedTime());
				
				for(int i=0; i<26; i++) { //Repopulates the board with the array from the minimax functions
					thisboard[i] = (int)bup[i];
				}
				board.setboard(thisboard); //Updates board

			}
			else { //Player moves
			
				System.out.println("Your Turn.");
				
				boolean playermoved = false;
				LinkedList<int[]> validmoves = board.getmoves(curplayer, die1, die2); //Gets all valid moves			
				
				while (!playermoved) { //Repeat until a vaild move is entered
						
					int movesleft = 2;	
					if (die1 == die2)
						movesleft = 4;
					
					int playermulti = 1;
					if (playerpos > 1)
						playermulti = -1;
					
					int[] tempboard = board.getboard(); //Makes a new board for this starting position
					Board iterator = new Board(tempboard);
					
					while (movesleft > 0) {
					
						int status = iterator.checkstate(curplayer, iterator.getboard());
						
						System.out.println("Moves left " + movesleft);
						System.out.println("Enter die you wish to use, or 0 to pass");
						Scanner scanIn = new Scanner(System.in);
						int testdie = -1;
						while (testdie < 0 || testdie > 6) {
							testdie = scanIn.nextInt(); //Read in an entry
						}
						
						if (testdie == 0) { //0 can be entered to skip
							movesleft--;
							continue;
						}
						if (testdie != die1 && testdie != die2) { //Check for validity
							System.out.println("You didn't roll that.");
							continue;
						}
						
						int testpos = -1;
						System.out.println("Enter column you want to move from, or 0 for bar");
						while (testpos < 0 || testpos > 24) { //Enter a column to move from
							testpos = scanIn.nextInt();
						}
						
						if ((iterator.getboard()[testpos] <= 0 && playermulti == 1) || (iterator.getboard()[testpos] >= 0 && playermulti == -1)) {
							System.out.println("You don't have pieces there.");
							continue;							
						}
						
						switch (status) {	//Depending on the state of the board, the appropriate function is picked - 0 for normal, 1 for removal, or 2 for captured.
											//If the move is valid, we're done, and we can update the board
							case 0:
								if (iterator.iszeromovevalid(curplayer, iterator.getboard(), testpos, testdie)) {
									tempboard = iterator.makezeromove(curplayer, iterator.getboard(), testpos, testdie);
									movesleft--;
									iterator = new Board(tempboard);
								}
								else
									System.out.println("Not a valid move.");
									
								break;
							case 1:
								if (iterator.isonemovevalid(curplayer, iterator.getboard(), testpos, testdie)) {
									tempboard = iterator.makeonemove(curplayer, iterator.getboard(), testpos, testdie);
									movesleft--;
									iterator = new Board(tempboard);
								}
								else
									System.out.println("Not a valid move.");
								
								break;
							case 2:
								if (testpos != 0)
									System.out.println("Need to move off the bar first.");									
								else {
									if ((curplayer == 1 && iterator.getboard()[25-testdie] >= -1) || (curplayer == 2 && iterator.getboard()[testdie] <= 1)) {
										tempboard = iterator.maketwomove(curplayer, iterator.getboard(), testdie);
										movesleft--;
										iterator = new Board(tempboard);
									}
									else
										System.out.println("Not a valid move.");								
								}
								break;
							
						}
						
					}
					
					int[] resultmove = iterator.getboard();
					for (int c=0; c<validmoves.size(); c++) { //Feed in the board, make sure it's a valid move given the constraints of backgammon rules
						int[] validops = validmoves.get(c);
						boolean success = true;
						for (int m=0; m<26; m++) {
							if (validops[m] != resultmove[m])
								success = false;
				
						}
						if (success) {
							playermoved = true;
							board = new Board(resultmove);
							break;
						}
					}
					
				}
		
			}
		
			drawboard(board);
			//Draw board - separate functions - flow is clear, draw frame, draw pieces
		
 			curplayer = curplayer + 1; //Change players
			if (curplayer == 3)
				curplayer = 1;
				
			if (board.checkstate(1) < 3 || board.checkstate(2) < 3) { //Roll dice, or end game on a win
				die1 = rand.nextInt(5) + 1;
				die2 = rand.nextInt(5) + 1;
			}
			else
				finished = true;
		
		}
		
	}
	
	public void drawboard(Board board) { //Helper function for drawing
	
		clearboard();
		drawspaces();
		drawpieces(board);
	
	}
	
	public void drawpieces(Board board) { //Draws the pieces on the board
	
		//Pieces are 7 by 7 circles - triangles center at 5s 
		//15 25 35 45 55 65, 85 95 105 115 125 135
		//1 is on bottom right
		
		//More than 5 pieces will not be drawn
		
		int[] thisboard = board.getboard();
		
		for (int i=0; i <= 25; i++) { //Iterates thtough each position
		
			if (thisboard[i] != 0) { //Only draws if men are there
				int men = Math.min(5,Math.abs(thisboard[i]));
				
				StdDraw.setPenColor(StdDraw.RED);
				if (thisboard[i] < 0)
					StdDraw.setPenColor(StdDraw.BLACK);
					
				//Pick the proper start point and draw in the pieces	
					
				double starth = 13.5;
				double startw;				
				if (i > 12) {
					starth = 86.5;
					if (i > 18)
						startw = 85 + (i-19)*10;
					else
						startw = 15 + (i-13)*10;
					
				}
				else {
					if (i > 6)
						startw = 65 - (i-7)*10;
					else
						startw = 135 - (i-1)*10;				
				
				}
				
				if (i == 25) {
					starth = 86.5;
					startw = 75;
				}
				if (i == 0) {
					starth = 13.5;
					startw = 75;
				}
				
				double currh = starth;
				for (int j=0; j < men; j++) {

					StdDraw.filledCircle(startw, currh, 3.5);
					
					currh = currh + 7;
					if (i > 12)
						currh = currh - 14;
						
				}
				
			}
			
			if (i >= 1 && i <= 24) {
				StdDraw.setPenColor(StdDraw.BLACK);
				int nh = 5;
				if (i > 12)
					nh = 95;
					
				int nw = 0;
				if (i <= 6)
					nw = 135 - (i-1)*10;
				if (i <= 12 && i > 6)
					nw = 65 - (i-7)*10;	
				if (i >= 19)
					nw = 85 + (i-19)*10;
				if (i <= 18 && i > 12)
					nw = 15 + (i-13)*10;
					
				String temp = Integer.toString(i);
				StdDraw.text(nw, nh, temp, 0);
			}
		
		}
		
		//BAR
		
	}
	
	public void clearboard() { //Clears out the board
	
		StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
		StdDraw.filledRectangle(85, 50, 85, 50);		
		
	}
	
	public void drawspaces() { //Draws in the board and its outline
	
		StdDraw.setPenColor(StdDraw.GRAY);
		StdDraw.filledRectangle(75, 50, 75, 50);
		StdDraw.setPenRadius(0.005);
		StdDraw.setPenColor(StdDraw.DARK_GRAY);
		StdDraw.rectangle(75, 50, 75, 50);
		
		StdDraw.filledRectangle(75, 50, 5, 50);
		StdDraw.filledRectangle(5, 50, 5, 50);
		StdDraw.filledRectangle(145, 50, 5, 50);
		StdDraw.filledRectangle(75, 5, 75, 5);
		StdDraw.filledRectangle(75, 95, 75, 5);
		
		drawtriangles(10, 10, 45); 
		drawtriangles(10, 90, 55);
		drawtriangles(80, 10, 45);
		drawtriangles(80, 90, 55);		
	
	}
	
	public void drawtriangles(int xstart, int ystart, int yend) { //Draws the triangles on the board

		for (int i=0; i<6; i++) {
			StdDraw.line((xstart+(10*i)), ystart, (xstart+5+(10*i)), yend);
			StdDraw.line((xstart+5+(10*i)), yend, (xstart+10+(10*i)), ystart);
		}
		
	}	
	
	public void drawdice(int d1, int d2) { //Helper to draw the dice
	
		drawdie(d1, 160, 60, true);
		drawdie(d2, 160, 40, false);
	
	}
	
	public void drawdie(int d, double x, double y, boolean white) {
	
		if (white) //Picks color
			StdDraw.setPenColor(StdDraw.WHITE);
		else
			StdDraw.setPenColor(StdDraw.BLACK);
		
		StdDraw.filledSquare(x, y, 8);
		
		if (white)
			StdDraw.setPenColor(StdDraw.BLACK);
		else
			StdDraw.setPenColor(StdDraw.WHITE);

		//Draws pips
		if (d >= 2) {
			StdDraw.filledCircle((x-2.5), (y-2.5), 1);
			StdDraw.filledCircle((x+2.5), (y+2.5), 1);
		}
		if (d >= 4) {
			StdDraw.filledCircle((x+2.5), (y-2.5), 1);
			StdDraw.filledCircle((x-2.5), (y+2.5), 1);			
		}
		if (d == 6) {
			StdDraw.filledCircle((x-2.5), y, 1);
			StdDraw.filledCircle((x+2.5), y, 1);
		}
		if (d == 1 || d == 3 || d == 5) {
			StdDraw.filledCircle(x, y, 1);
		}		
	
	
	}
	
	public double chance (Board inboard, int inplayer, double alpha, double beta, int depth, boolean isnextmax) { //Functionality for chance nodes
	
		int[] combosa = new int[21];
		int[] combosb = new int[21];
		double[] prob = new double[21];
	
		combosa[0] = 1;
		combosa[1] = 1;
		combosa[2] = 1;
		combosa[3] = 1;
		combosa[4] = 1;
		combosa[5] = 1;
		combosa[6] = 2;
		combosa[7] = 2;
		combosa[8] = 2;
		combosa[9] = 2;
		combosa[10] = 2;
		combosa[11] = 3;
		combosa[12] = 3;
		combosa[13] = 3;
		combosa[14] = 3;
		combosa[15] = 4;
		combosa[16] = 4;
		combosa[17] = 4;
		combosa[18] = 5;
		combosa[19] = 5;
		combosa[20] = 6;

		combosb[0] = 1;
		combosb[1] = 2;
		combosb[2] = 3;
		combosb[3] = 4;
		combosb[4] = 5;
		combosb[5] = 6;
		combosb[6] = 2;
		combosb[7] = 3;
		combosb[8] = 4;
		combosb[9] = 5;
		combosb[10] = 6;
		combosb[11] = 3;
		combosb[12] = 4;
		combosb[13] = 5;
		combosb[14] = 6;
		combosb[15] = 4;
		combosb[16] = 5;
		combosb[17] = 6;
		combosb[18] = 5;
		combosb[19] = 6;
		combosb[20] = 6;

		for (int i=0; i<21; i++) {
			if (i != 0 && i != 6 && i != 11 && i != 15 && i != 18 && i != 20)
				prob[i] = 0.0555;
			else
				prob[i] = 0.02777;
			
		}
		
		//Arrays have now been populated with all possible rolls and the chance of each

		double value = 0; //Cumulative value of this board
		
		int nextplayer = inplayer + 1; //Next player
		if (nextplayer > 2)
			nextplayer = 1;
		
		for (int i=0; i < 21; i++) { //Iteratively sums the expected values for each possible roll and sums them
			if (isnextmax) {
				double[] mmvals = minimaxmax(inboard, nextplayer, alpha, beta, (depth+1), combosa[i], combosb[i]);
				value = value + (prob[i]*mmvals[26]); //Need to extract the value from minimax
			}
			else {
				double[] mmvals = minimaxmini(inboard, nextplayer, alpha, beta, (depth+1), combosa[i], combosb[i]);
				value = value + (prob[i]*mmvals[26]); //Need to extract the value from minimax			
			}
		}

		return value;
	
	}
	
	
	public double[] minimaxmax (Board inboard, int inplayer, double alpha, double beta, int depth, int die1, int die2) { //Returns a board and the score for it
	
		int playerwinstate = inboard.checkstate(inplayer, inboard.getboard()); //Test for winner
		int maxdepth = 2;
		
		double[] result = new double[27];
		
		int nextplayer = 2; //Get next player
		if (inplayer == 2)
			nextplayer = 1;
		
		if (playerwinstate <3 && depth <= maxdepth) { //It's a heart
		
			LinkedList<int[]> allmoves = inboard.getmoves(inplayer, die1, die2); //Get all valid moves
			int loopsize = allmoves.size();
		
			if (loopsize > 0) {
				double[] moveoptions = new double[loopsize];
				for (int i=0; i < loopsize; i++) {
					int[] thismove = allmoves.get(i); //Extract a board state
					Board thisboard = new Board(thismove);
					
					//Call chance nodes
					moveoptions[i] = chance(thisboard, inplayer, alpha, beta, depth, false);
					
					//Check alphabeta
					if (moveoptions[i] >= beta) {
						double[] choice = new double[27];
						for (int j=0; j<25; j++)
							choice[j] = thismove[j];
						
						choice[26] = moveoptions[i];
						return choice;
					}
					else { //otherwise alpha is now the max of alpha and this column's score
						alpha = Math.max(alpha,moveoptions[i]);
					}
					
				}
				
				//Pick the best one (the max), return the board and the score for it
				int bestindex = 0;
				for (int i=0; i < loopsize; i++) {
					if (moveoptions[i] >= moveoptions[bestindex])
						bestindex = i;
						
				}				
				
				int[] newboard = allmoves.get(bestindex);
				
				for (int i=0; i < 26; i++) {
					result[i] = (double)(newboard[i]);
				}
				result[26] = moveoptions[bestindex]; //Generate new board and value
				
				return result;
				
			}
			else {
				//Handle no moves left - send back this board and its heuristic score
				int[] sameboard = inboard.getboard();
				for (int i=0; i < 26; i++) {
					result[i] = (double)(sameboard[i]);
				}
				result[26] = inboard.heuristic();
				
				return result;
			}
		
		}
		else {
			//Handle max depth (heuristic) and a win
			int[] sameboard = inboard.getboard();
			for (int i=0; i < 26; i++) {
				result[i] = (double)(sameboard[i]);
			}
			result[26] = inboard.heuristic(); //heuristic value for the board
			
			if (playerwinstate == 3) { //Game is won or lost
				if (inplayer == 1)
					result[26] = 99999;
				else
					result[26] = -99999;
				
			}
			
		}
		
		return result;
	
	}
	
	public double[] minimaxmini (Board inboard, int inplayer, double alpha, double beta, int depth, int die1, int die2) { //Returns a board and the score for it
	
		int playerwinstate = inboard.checkstate(inplayer, inboard.getboard()); //Test for winner
		int maxdepth = 2;
		
		double[] result = new double[27];
		
		int nextplayer = 2; //Get next player
		if (inplayer == 2)
			nextplayer = 1;
		
		if (playerwinstate <3 && depth <= maxdepth) { //It's a heart
		
			LinkedList<int[]> allmoves = inboard.getmoves(inplayer, die1, die2); //Get move list
			int loopsize = allmoves.size();
		
			if (loopsize > 0) {
				double[] moveoptions = new double[loopsize];
				for (int i=0; i < loopsize; i++) {
					int[] thismove = allmoves.get(i);
					Board thisboard = new Board(thismove); //Extract board
					
					//Call chance nodes
					moveoptions[i] = chance(thisboard, inplayer, alpha, beta, depth, true);
					
					//Check alphabeta
					if (moveoptions[i] <= alpha) {
						double[] choice = new double[27];
						for (int j=0; j<25; j++)
							choice[j] = thismove[j];
						
						choice[26] = moveoptions[i];
						return choice;
					}
					else { //otherwise alpha is now the max of alpha and this column's score
						beta = Math.min(beta,moveoptions[i]);
					}
					
				}
				
				//Pick the best one (the max), return the board and the score for it
				int bestindex = 0;
				for (int i=0; i < loopsize; i++) {
					if (moveoptions[i] <= moveoptions[bestindex])
						bestindex = i;
						
				}
				
				int[] newboard = allmoves.get(bestindex);
				
				for (int i=0; i < 26; i++) {
					result[i] = (double)(newboard[i]);
				}
				result[26] = moveoptions[bestindex];
					
				return result;
				
			}
			else {
				//Handle no moves left - send back this board and its heuristic score
				int[] sameboard = inboard.getboard();
				for (int i=0; i < 26; i++) {
					result[i] = (double)(sameboard[i]);
				}
				result[26] = inboard.heuristic();
				
				return result;
			}
		
		}
		else {
			//Handle max depth (heuristic) and a win
			int[] sameboard = inboard.getboard();
			for (int i=0; i < 26; i++) {
				result[i] = (double)(sameboard[i]);
			}
			result[26] = inboard.heuristic(); //heuristic value for the board
			
			if (playerwinstate == 3) { //Game is won or lost
				if (inplayer == 1)
					result[26] = 99999;
				else
					result[26] = -99999;
				
			}
			
		}
		
		return result;
	
	}

}

//Old idea list, left for reference

//History hash table in the minimax portions
//Other minimax varients too 
//Hash duplicates - NO
//Setting queues for last loop of generation?
//Only use movequeue, check how many moves there are at first and use a for for that many