import java.awt.Point;
import java.util.ArrayList;

public class Board {

    // Our representation of the board, where myBoard[0][0] represents 
    // the bottom left dot.
    private Dot[][] myBoard;
    // Total number of moves allowed for a single game session.
    private static int movesAllowed = 5;
    private int mySize;
    private int currentScore; 
    private int movesLeft = movesAllowed;
    private boolean [][] removeBoard;
    private ArrayList<Point> selectedDots;

    // DO NOT MODIFY
    public static final int MINSIZE = 4;
    public static final int MAXSIZE = 10;

    /**
     * Sets up the board's data and starts up the GUI. N is side length of the
     * board. (Number of dots per side) N will default to 0 if a non-integer is
     * entered as an argument. If there are no arguments, N will default to 10;
     */
    public static void main(String[] args) {
        int n = 0;
        try {
            n = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            n = 0;
        } catch (IndexOutOfBoundsException e) {
            // This line is run if no command line arguments are given.
            // If you wish to modify this line to test, remember to change it back to 
            // n = 10;
            n = 10;
        }
        GUI.initGUI(n);
    }

    /**
     * When the New Game button is clicked, a new randomized board is constructed.
     * Sets up the board with input SIZE, such that the board's side length is SIZE.
     * Note: The Board is always a square, so SIZE is both the length and the width.
     * Generate a random board such that each entry in board is a random color. 
     * (represented by an int). Should print and error and System.exit if the size 
     * is not within the MINSIZE and MAXSIZE. This constructor will only be called 
     * once per game session. Initialize any variables if needed here.
     */
    public Board(int size) {
    	// YOUR CODE HERE
    	if (size < MINSIZE || size > MAXSIZE) {
    		System.err.println(size + " is not a valid size.");
    		throw new IllegalArgumentException();
    	}
    	mySize = size;
    	myBoard = new Dot[mySize][mySize];
    	for (int row = 0; row < mySize; row++) {
    		for (int col = 0; col < mySize; col++) {
    			myBoard[row][col] = new Dot();
    		}
    	}
    	selectedDots = new ArrayList<Point>();
    	removeBoard = new boolean[mySize][mySize];
    	currentScore = 0;
    }
    
    /**
     * This constructor takes in a 2D int array of colors and generates a preset board
     * with each dot matching the color of the corresponding entry in the int[][] 
     * arguement. This constructor can be used for predetermined tests.
     * You may assume that the input is valid (between MINSIZE and MAXSIZE etc.) 
     * since this is for your own testing.
     */
    public Board(int[][] preset) {
    	// YOUR CODE HERE
    	int size = preset.length;
    	myBoard = new Dot[size][size];
    	for (int row = 0; row < size; row++) {
    		for (int col = 0; col < size; col++) {
    			myBoard[row][col] = new Dot(preset[row][col]);
    		}
    	}
    	selectedDots = new ArrayList<Point>();
    	removeBoard = new boolean[size][size];
    	currentScore = 0;
    	mySize = size;
    }
    
    /**
     * Returns the array representation of the board. (Data is used by GUI).
     */
    public Dot[][] getBoard() {
    	return myBoard;
    }

    /**
     * Returns the number of moves allowed per game. This value should not
     * change during a game session.
     */
    public static int getMovesAllowed() {
        // YOUR CODE HERE
        return movesAllowed;
    }

    /**
     * Change the number of moves allowed per game. This method can be used for 
     * testing.
     */
    public static void setMovesAllowed(int n) {
        // YOUR CODE HERE
    	movesAllowed = n;
    }

    /** Returns the number of moves left. */
    public int getMovesLeft() {
        // YOUR CODE HERE
    	return movesLeft;
    }

    /**
     * Return whether or not it is possible to make a Move. (ie, there exists
     * two adjacent dots of the same color.) If false, the GUI will report a
     * game over.
     */
    public boolean canMakeMove() {
    	// YOUR CODE HERE
    	int size = myBoard.length;
    	for (int row = 0; row < size; row++) {
    		for (int col = 0; col < size; col++) {
    			if (row == size - 1) {
    				if (col == size - 1) {
    					return false;
    				} else if (myBoard[row][col].isSameColor(myBoard[row][col+1])) {
    					return true;
    				}
    			} else if (col == size - 1) {
    				if (myBoard[row][col].isSameColor(myBoard[row+1][col])) {
    					return true;
    				}
    			} else if (myBoard[row][col].isSameColor(myBoard[row][col+1]) || myBoard[row][col].isSameColor(myBoard[row+1][col])) {
    				return true;
    			}
    		}
    	}
        return false;
    }

    /**
     * Returns if the board is in a state of game over. The game is over if there
     * are no possible moves left or if the player has used up the maximum
     * allowed moves.
     */
    public boolean isGameOver() {
    	// YOUR CODE HERE
        return !canMakeMove() || getMovesLeft() == 0;
    }

    /**
     * Returns whether or not you are allowed to select a dot at X, Y at the
     * moment. Remember, if the game is over, you cannot select any dots.
     */
    public boolean canSelect(int x, int y) {
        // YOUR CODE HERE
    	for (Point p : selectedDots) {
    		if (p.equals(new Point(x, y))) {
    			return false;
    		}
    	}
    	if (!isGameOver()) {
    		if (selectedDots.size() == 0) {
    			return true;
    		} else {
    			Point previous = selectedDots.get(selectedDots.size() - 1);
    			if (previous.getX() == x) {
    				if (previous.getY() - 1 == y || previous.getY() + 1 == y) {
    					if (myBoard[x][y].isSameColor(myBoard[(int) previous.getX()][(int) previous.getY()])) {
        					return true;
    					}				
    				} 
    			} else if (previous.getY() == y) {
    				if (previous.getX() - 1 == x || previous.getX() + 1 == x) {
    					if (myBoard[x][y].isSameColor(myBoard[(int) previous.getX()][(int) previous.getY()])) {
        					return true;
    					}				
    				}
    			}
    		}
    	}
        return false;
    }
     
    /**
     * Is called when a dot located at myBoard[X][Y] is selected on the GUI.
     */
    public void selectDot(int x, int y) {
        // YOUR CODE HERE
    	selectedDots.add(new Point(x, y));
    }

    /**
     * Checks if you are allowed to deselect the chosen point.
     * Assumes at least one point has been selected already.
     * You can only deselect the most recent point you have selected.
     * (You can select 3 dots and deselect them in reverse order.)
     */
    public boolean canDeselect(int x, int y) {
    	Point previous = selectedDots.get(selectedDots.size() - 1);
    	return previous.getX() == x && previous.getY() == y;
    }

    /**Is called when a dot located at myBoard[X][Y] is deselected on the GUI. */
    public void deselectDot(int x, int y) {
        // YOUR CODE HERE
    	selectedDots.remove(selectedDots.size() - 1);
    }

    /**Returns the number of of currently selected dots */
    public int numberSelected() {
    	// YOUR CODE HERE
    	return selectedDots.size();
    } 

    /**
     * Is called when the "Remove" button is clicked. Puts all selected dots in
     * a "removed" state. If no dots should be removed, throw a CantRemoveException. 
     * You must also create your own Exception Class named CantRemoveException.
     * If selected dots form a closed shape, remove all dots on the board that have
     * the same color as the selected dots.
     */
    public void removeSelectedDots() throws CantRemoveException {
    	// YOUR CODE HERE
    	if (selectedDots.size() < 2){
    		throw new CantRemoveException();
    	}
    	else{
    		for (Point z : selectedDots){
    			removeBoard[(int) z.getX()][(int) z.getY()] = true;
    		}
    		if (!isClosedShape()) {
    			currentScore += selectedDots.size();
    		}
    		else if (isClosedShape()) {
    			removeSameColor();
    		}
    		selectedDots.clear();
    		movesLeft--;
    	}
    }

    /**
     * Puts the dot at X, Y in a removed state. Later all dots above a
     * removed dot will drop.
     */
    public void removeSingleDot(int x, int y) {
        // OPTIONAL
    	removeBoard[x][y] = true;
    }

    /**
     * Return whether or not the selected dots form a closed shape. Refer to
     * diagram for what a closed shape looks like.
     */
    public boolean isClosedShape() {
    	// YOUR CODE HERE
    	int size = selectedDots.size();
    	for (int i = 0; i < size - 3; i++) {
    		if (selectedDots.get(i).getX() == selectedDots.get(size-1).getX()) {
    			if (selectedDots.get(i).getY() - 1 == selectedDots.get(size-1).getY() || 
    					selectedDots.get(i).getY() + 1 == selectedDots.get(size-1).getY()) {
					return true;			
				} 
    		} else if (selectedDots.get(i).getY() == selectedDots.get(size-1).getY()) {
    			if (selectedDots.get(i).getX() - 1 == selectedDots.get(size-1).getX() || 
    					selectedDots.get(i).getX() + 1 == selectedDots.get(size-1).getX()) {
					return true;			
				} 
    		}
    	}
    	return false;
    }

    /**
     * Removes all dots of the same color of the dots on the currently selected
     * dots. Assume it is confirmed that a closed shape has been formed from the
     * selected dots.
     */
    public void removeSameColor() {
    	// OPTIONAL
    	Point currentColorPoint = selectedDots.get(selectedDots.size() - 1);
    	Dot currentColorDot = myBoard[(int)currentColorPoint.getX()][(int)currentColorPoint.getY()];
    	for (int row = 0; row < mySize; row++) {
    		for (int col = 0; col < mySize; col++) {
    			if (myBoard[row][col].isSameColor(currentColorDot)) {
    				removeSingleDot(row,col);
    				currentScore++;
    			}
    		}
    	}
    }

    /**
     * Once the dots are removed. Rearrange the board to simulate the dropping of
     * all of the dots above the removed dots. Refer to diagram in the spec for clarity.
     * After dropping the dots, there should exist some "bad" dots at the top. 
     * (These are the blank dots on the 4-stage diagram.)
     * fillRemovedDots will be called immediately after this by the GUI so that random 
     * dots replace these bad dots with new ones that have a randomly generated color.
     */
    public void dropRemainingDots() {
    	// YOUR CODE HERE
    	int count = 0;
    	Dot [][] fillIn = new Dot[mySize][mySize];
    	for (int row = 0; row < mySize; row++){
    		count = 0;
    		for (int col = 0; col < mySize; col ++){
    			if (removeBoard[row][col] == false){
    				fillIn[row][count] = myBoard[row][col];
    				count ++;
    			}
    		}
    	}
    	myBoard = fillIn;
    	removeBoard = new boolean[mySize][mySize];
    }

    /**
     * After removing all dots that were meant to be removed, replace any
     * removed dot with a new dot of a random color.
     */
    public void fillRemovedDots() {
        // YOUR CODE HERE
    	for (int x = 0; x < myBoard.length; x++) {
    		for (int y = 0; y < myBoard.length; y++) {
    			if (myBoard[x][y] == null) {
    				myBoard[x][y] = new Dot();
    			}		
    		}
    	}
    }

    /**
     * Return the current score, which is called by the GUI when it needs to
     * update the display of the score. Remember to update the score in your 
     * other methods.
     */
    public int getScore() {
    	// YOUR CODE HERE
        return currentScore;
    }

    /**
     * Search the board for a sequence of 4 consecutive points which form a
     * square such that out of all possible 2x2 squares, selecting this one 
     * yields the most points.
     */
    public ArrayList<Point> findBestSquare() {
        // YOUR CODE HERE
    	int maxPoints = 0;
		Point dot1 = new Point();
		Point dot2 = new Point();
		Point dot3 = new Point();
		Point dot4 = new Point();
		int currentPoints;
		ArrayList<Point> squareList = new ArrayList<Point>();
		
		for (int col = 0; col < mySize - 1; col ++){
			for (int row = 0; row < mySize - 1; row ++){
				if (myBoard[row][col].isSameColor(myBoard[row + 1][col]) &&
				myBoard[row][col].isSameColor(myBoard[row][col + 1]) &&
				myBoard[row][col].isSameColor(myBoard[row + 1][col + 1])){
					currentPoints = calculatePoints(myBoard[row][col]);
					if ((currentPoints) > maxPoints){
						dot1 = new Point (row, col);
						dot2 = new Point (row + 1, col);
						dot3 = new Point (row + 1, col + 1);
						dot4 = new Point (row, col + 1);
						maxPoints = currentPoints;
					}				
				}
			}
		}
		squareList.add(dot1);
		squareList.add(dot2);
		squareList.add(dot3);
		squareList.add(dot4);
		return squareList;
    }
    
    public int calculatePoints(Dot colorDot) {
    	int count = 0;
		for (int col = 0; col < mySize; col ++){
			for (int row = 0; row < mySize; row ++){
				if (myBoard[row][col].isSameColor(colorDot)){
					count ++;
				}
			}
		}
		return count;
    }

    /**Prints the the board any way you like for testing purposes. */
    public void printBoard() {
        // OPTIONAL
    }

    public void printBoard(String msg) {
    	System.out.println(msg);
    	printBoard();
    }
    
    public static class CantRemoveException extends Exception {
    	public CantRemoveException() {
    		super();
    	}
    	
    	public CantRemoveException(String message) {
    		super(message);
    	}
    }
}
