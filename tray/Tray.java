import java.util.*;
import java.io.*;

public class Tray {
	
	int myHeight;
	int myWidth;
	HashSet<Block> myCurrConfig;
	HashSet<Block> myGoal;
	boolean [][] boardMap; //represents which tiles are currently covered by a block
	boolean solver; //different exit statuses are required for the same error,
    //depending on if we are using Solver or Checker
    
	public Tray (int height, int width, HashSet<Block> currConfig, HashSet<Block> goal, boolean [][] board) {
		myHeight = height;
		myWidth = width;
		myCurrConfig = (HashSet<Block>) currConfig.clone();
		myGoal = (HashSet<Block>) goal.clone();
		boardMap = new boolean[myWidth][myHeight];
		for (int y= 0; y < myHeight; y++){
			for (int x= 0; x < myWidth; x ++){
				boardMap[x][y] = board[x][y];
			}
		}
	}
    
	/*Initialize tray. Should throw error 5 here if needed*/
	public Tray (File init, File goal) {
		myCurrConfig = readFile(init, true, true);
		myGoal = readFile(goal, false, false);
	}
    
	public Tray (File init, File goal, boolean solve) {
		solver = solve;
		myCurrConfig = readFile(init, true, true);
		myGoal = readFile(goal, false, false);
	}
	
	private HashSet<Block> readFile(File f, boolean isInit, boolean checking) {
		HashSet<Block> blocks = new HashSet<Block>();
		int myTopLeftY;
		int myTopLeftX;
		int myBotRightY;
		int myBotRightX;
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line;
			boolean readSize = true;
			String [] dimensions;
			while ((line = br.readLine()) != null) {
				dimensions = line.split(" ");
				if (readSize && isInit) {
					myHeight = Integer.parseInt(dimensions[0]);
					myWidth = Integer.parseInt(dimensions[1]);
					readSize = false;
					if (checking){
						boardMap = new boolean [myWidth][myHeight];
					}
				} else {
					myTopLeftY = Integer.parseInt(dimensions[0]);
					myTopLeftX = Integer.parseInt(dimensions[1]);
					myBotRightY = Integer.parseInt(dimensions[2]);
					myBotRightX = Integer.parseInt(dimensions[3]);
					checkBlock(myTopLeftY, myTopLeftX, myBotRightY, myBotRightX, true);
					blocks.add(new Block(myTopLeftY, myTopLeftX, myBotRightY, myBotRightX));
					if (checking) {
						addBooleanBoard(myTopLeftY, myTopLeftX, myBotRightY, myBotRightX, true);
					}
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
			System.exit(3);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return blocks;
	}
    
	public void checkBlock (int topLeftY, int topLeftX, int botRightY, int botRightX, boolean init) {
		if (topLeftY < 0 || topLeftX < 0 || botRightY < 0 || botRightX < 0 ||
			topLeftY >= myHeight || botRightY >= myHeight ||
			topLeftX > myWidth || botRightY > myWidth ||
			topLeftX > botRightX || topLeftY < botRightY) {
			System.out.println("Invalid indices when attempting to place block "
                               + topLeftY + " " + topLeftX + " " + botRightY + " " + botRightX);
			if (init) {
				if (solver) {
					System.exit(4);
				}
				System.exit(5);
			}
			System.exit(6);
		}
	}
    
	/*Adds block. Should check validity, i.e. does not intersect with other blocks
     and has dimensions within board.*/
	public void addBlock(int topLeftY, int topLeftX, int botRightY, int botRightX, boolean init) {
		checkBlock(topLeftY, topLeftX, botRightY, botRightX, init);
		addBooleanBoard(topLeftY, topLeftX, botRightY, botRightX, init);
		myCurrConfig.add(new Block(topLeftY, topLeftX, botRightY, botRightX));
	}
    
	/*Checks if current configuration and goal configuration are valid, i.e. all
     blocks are within the dimensions of the tray and no blocks intersect.
     Should throw error 5 if not.*/
	public boolean isOK() {
		return true;
	}
	
	public void addBooleanBoard(int topLeftY, int topLeftX, int botRightY, int botRightX, boolean init){
		try {
			for (int myX = topLeftX; myX <= botRightX; myX++) {
				for (int myY = botRightY; myY <= topLeftY; myY++) {
					if (!boardMap[myX][myY]) {
						boardMap[myX][myY] = true;
					} else {
						System.out.println("Blocks overlap when attempting to place block "
                                           + topLeftY + " " + topLeftX + " " + botRightY + " " + botRightX);
						if (init) { //blocks being added during initialization
							if (solver) {
								System.exit(4);
							}
							System.exit(5);
						}
						System.exit(6); //block being added in a move
					}
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Attempted to place out of bounds block "
                               + topLeftY + " " + topLeftX + " " + botRightY + " " + botRightX);
			if (init) { //blocks being added during initialization
				if (solver) {
					System.exit(4);
				}
				System.exit(5);
			}
			System.exit(6); //block being added in a move
		}
	}
    
	public void removeBooleanBoard(int topLeftY, int topLeftX, int botRightY, int botRightX){
		for (int myX = topLeftX; myX <= botRightX; myX ++) {
			for (int myY = topLeftY; myY <= botRightY; myY ++) {
				if (boardMap[myX][myY]) {
					boardMap[myX][myY] = false;
				}
			}
		}
	}
    
	/*Checks if goal configuration is met*/
	public boolean isDone() {
		return myCurrConfig.containsAll(myGoal);
	}
    
	public void makeMove(int prevY, int prevX, int newY, int newX) {
		if (prevY != newY && prevX != newX) {
			System.out.println("Attempted diagonal move " + prevY + " " + prevX + " " + newY + " " + newX);
			System.exit(6);
		}
        
		// decompose long horizontal or vertical moves into single length moves
		if (prevY < newY && newY - prevY > 1) {
			for (int i = 0; i < newY - prevY; i++) {
				makeMove(prevY + i , prevX, prevY + i + 1, newX);
			}
		} else if (prevY > newY && prevY - newY > 1) {
			for (int i = 0; i < prevY - newY; i++) {
				makeMove(prevY - i , prevX, prevY - i - 1, newX);
			}
		} else if (prevX < newX && newX - prevX > 1) {
			for (int i = 0; i < newX - prevX; i++) {
				makeMove(prevY, prevX + i, prevY, prevX + i + 1);
			}
		} else if (prevX > newX && prevX - newX > 1) {
			for (int i = 0; i < prevX - newX; i++) {
				makeMove(prevY, prevX - i, prevY, prevX - i - 1);
			}
		} else {
			Block toRemove = null;
			for (Block b : myCurrConfig) {
				if (b.myTopLeftY == prevY && b.myTopLeftX == prevX) {
					toRemove = b;
					break;
				}
				//b will always exist here, since isValidMove returns true
			}
			if (toRemove == null) {
				System.out.println("No block at " + prevY + " " + prevX + " exists");
				System.exit(6);
			}
			myCurrConfig.remove(toRemove);
			removeBooleanBoard(toRemove.myTopLeftY, toRemove.myTopLeftX,
                               toRemove.myBotRightY, toRemove.myBotRightX);
			addBlock(newY, newX, newY + toRemove.myTopLeftY - toRemove.myBotRightY,
                     newX + toRemove.myBotRightX - toRemove.myTopLeftX, false);
		}
	}
	//helper methods for solver
    
	/*Returns set of all configurations possible after exactly 1 move*/
	public HashSet<Tray> getChildrenConfigs () {
		HashSet<Tray> toRtn = new HashSet<Tray>();
		Tray afterMove;
		for (Block block : this.myCurrConfig) {
		    if (canMove(block, "up")) {
				afterMove = new Tray(myHeight, myWidth, myCurrConfig, myGoal, boardMap);
				afterMove.makeMove(block.myTopLeftY, block.myTopLeftX,
					block.myTopLeftY + 1, block.myTopLeftX);
				toRtn.add(afterMove);
		    }
		    if (canMove(block, "down")) {
				afterMove = new Tray(myHeight, myWidth, myCurrConfig, myGoal, boardMap);
				afterMove.makeMove(block.myTopLeftY, block.myTopLeftX,
					block.myTopLeftY - 1, block.myTopLeftX);
				toRtn.add(afterMove);
		    }
		    if (canMove(block, "right")) {
				afterMove = new Tray(myHeight, myWidth, myCurrConfig, myGoal, boardMap);
				afterMove.makeMove(block.myTopLeftY, block.myTopLeftX,
					block.myTopLeftY, block.myTopLeftX + 1);
				toRtn.add(afterMove);
		    }
		    if (canMove(block, "left")) {
				afterMove = new Tray(myHeight, myWidth, myCurrConfig, myGoal, boardMap);
				afterMove.makeMove(block.myTopLeftY, block.myTopLeftX,
					block.myTopLeftY, block.myTopLeftX - 1);
				toRtn.add(afterMove);
		    }
		}
		return toRtn;
    }

    private boolean canMove(Block block, String direction) {
		try { 
			if (direction.equals("up")) {
				for (int i = 0; i < block.myWidth; i++) {
					if (boardMap[block.myTopLeftX + i][block.myTopLeftY - 1]) {
					    return false;
					}
				}	
			} else if (direction.equals("down")) {
				for (int i = 0; i < block.myWidth; i++) {
					if (boardMap[block.myTopLeftX + i][block.myBotRightY + 1]) {
					    return false;
					}
				}
			} else if (direction.equals("right")) {
				for (int i = 0; i < block.myHeight; i++) {
					if (boardMap[block.myBotRightX + 1][block.myBotRightY - i]) {
					    return false;
					}
				}	
			} else if (direction.equals("left")) {
				for (int i = 0; i < block.myHeight; i++) {
					if (boardMap[block.myTopLeftX - 1][block.myBotRightY - i]) {
					    return false;
					}
				}	
			} else {
				throw new IllegalArgumentException();
			}
			return true;
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
  	}

    
	/*Given that config2 is the result of 1 move applied to config1, returns that move
     as a string, e.g. "1 1 0 0"*/
	public static String findMove(Tray config1, Tray config2) {
		Block toMove = null;
		for (Block b : config1.myCurrConfig) {
			if (!config2.myCurrConfig.contains(b)) {
				toMove = b;
			}
		}
		Block newPos = null;
		for (Block b : config2.myCurrConfig) {
			if (!config1.myCurrConfig.contains(b)) {
				newPos = b;
			}
		}
		System.out.println(toMove.myTopLeftY + " " + toMove.myTopLeftX + " " + 
				newPos.myTopLeftY + " " + newPos.myTopLeftX);
		return toMove.myTopLeftY + " " + toMove.myTopLeftX + " " + 
				newPos.myTopLeftY + " " + newPos.myTopLeftX;
	}
	
	public boolean equals (Object obj) {
		try {
			Tray other = (Tray) obj;
			return this.myHeight == other.myHeight &&
            this.myWidth == other.myWidth &&
            this.myCurrConfig.equals(other.myCurrConfig) &&
            this.myGoal.equals(other.myGoal);
		} catch (ClassCastException e) {
			return false;
		}
	}
    
	public int hashCode() {
		return myWidth + myHeight + myCurrConfig.hashCode() + myGoal.hashCode();
	}
	
	public void printBoard(){
		for (int y = 0; y < myHeight; y ++){
			for (int x = 0; x < myWidth; x ++){
				if (x == myWidth - 1){
					if (boardMap[x][y] == true){
						System.out.println("T ");
					}else{
						System.out.println("F ");
					}
				}
				else{
					if (boardMap[x][y] == true){
						System.out.print("T ");
					}else{
						System.out.print("F ");
					}
				}
			}
		}
	}
	
	private boolean goalAchievable() {
		// key: area, value: number of blocks with that area (original config)
		HashMap<Integer, Integer> currConfigBlockSizes = new HashMap<Integer, Integer>();
		// same as above but for goal config
		HashMap<Integer, Integer> goalBlockSizes = new HashMap<Integer, Integer>();
		// find all the areas sizes in the original config of blocks
		// and their frequencies
		int area;
		Integer n;
		for (Block block : myCurrConfig) {
	    		area = block.calculateArea();
	    		n = currConfigBlockSizes.get(area);
	    		if (n == null) {
				currConfigBlockSizes.put(area, 1);
	    		} else {
				currConfigBlockSizes.put(area, n + 1);
	    		}
		}
		for (Block block : myGoal) {
			area = block.calculateArea();
	    		// if the original configuration of blocks doesn't contain this
	    		// area, return false
	    		if (currConfigBlockSizes.get(area) == null) {
				return false;
	    		} else {
				n = goalBlockSizes.get(area);
				if (n == null) {
		    		goalBlockSizes.put(area, 1);
				} else {
		    			// if the the number of blocks in the original configuration
		    			// with area x is equal to the number of blocks with area x
		    			// in the goal config and we're about to add one more of
		    			// area x, return false
		    			if (currConfigBlockSizes.get(area) == n) {
						return false;
		    			} else {
						goalBlockSizes.put(area, n + 1);
		    			}
				}
	    		}
		}
		// Check if the goal config contains less blocks of area x than the
		// original config
		for (Integer areaSize : currConfigBlockSizes.keySet()) {
	    		if (goalBlockSizes.get(areaSize) != currConfigBlockSizes.get(areaSize)) {
				return false;
	    		}
		}
		return true;
    	}
    
	public static class Block {
        
		int myTopLeftY;
		int myTopLeftX;
		int myBotRightY;
		int myBotRightX;
		int myWidth;
		int myHeight;
        
		public Block(int topLeftY, int topLeftX, int botRightY, int botRightX) {
			myTopLeftY = topLeftY;
			myTopLeftX = topLeftX;
			myBotRightY = botRightY;
			myBotRightX = botRightX;
			myWidth = myBotRightX - myTopLeftX + 1;
			myHeight = myBotRightY - myTopLeftY + 1;
		}
        
		public int hashCode() {
			return myTopLeftY * 1000 + myTopLeftX * 100 + myBotRightY * 10 + myBotRightX;
		}
        
		/*For hashing, and later use when we want to see if a two hashsets of
         blocks are equal*/
		public boolean equals(Object obj) {
			try {
				Block other = (Block) obj;
				return this.myTopLeftY == other.myTopLeftY &&
                this.myTopLeftX == other.myTopLeftX &&
                this.myBotRightY == other.myBotRightY &&
                this.myBotRightX == other.myBotRightX;
			} catch (ClassCastException e) {
				return false;
			}
		}
        
		/*Would be used for isValidMove. However, since the project calls for
         maximal time efficiency with no concern for space, maintaining a hashset of
         occupied tiles may be more optimal*/
		public boolean intersects (Block other) {
			return ((this.myTopLeftX <= other.myTopLeftX && 
                     other.myTopLeftX <= this.myBotRightX) || 
					(other.myTopLeftX <= this.myTopLeftX && 
                     this.myTopLeftX <= other.myBotRightX)) //x intersect
            &&
            ((this.myBotRightY <= other.myBotRightY && 
              other.myBotRightY <= this.myTopLeftY) || 
             (other.myBotRightY <= this.myBotRightY && 
              this.myBotRightY <= other.myBotRightY)); //y intersect
		}
        
		public String toString() {
			return myTopLeftY + " " + myTopLeftX + " " + myBotRightY + " " + myBotRightX;
		}
	}
		
		
		public static void main(String[] args) {
			File pos = new File("tree+180");
			File goal = new File("tree+180.goal");
			Tray z = new Tray(pos, goal);
			z.printBoard();
			HashSet<Tray> childrenConfig = z.getChildrenConfigs();
			//z.printBoard();
			System.out.println();
			for (Tray tray: childrenConfig){
				System.out.println();
				tray.printBoard();
			}
			
		}
}
