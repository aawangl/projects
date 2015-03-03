import java.awt.Point;
import java.util.ArrayList;

import org.junit.Test;

import junit.framework.TestCase;

public class BoardTest extends TestCase {

	@Test
	public void testInitializeBoard() {
		Board.setMovesAllowed(5);

		try {
			Board testBoardLess = new Board(3);
		} catch (IllegalArgumentException e) {
			System.out.println("Bad board size. Size is less than MINSIZE: 4");
		}

		try {
			Board testBoardMore = new Board(11);
		} catch (IllegalArgumentException e) {
			System.out.println("Bad board size. Size is greater than MAXSIZE: 10");
		}

		try {
			Board testBoardNegative = new Board(-5);
		} catch (IllegalArgumentException e) {
			System.out.println("Bad board size. Size is less than MINSIZE and negative");

		}
	}

	@Test
	public void testMovesAllowed() {
		Board.setMovesAllowed(8);
		assertEquals(Board.getMovesAllowed(), 8);

		Board.setMovesAllowed(0);
		Board testBoard = new Board(5);
		assertEquals(Board.getMovesAllowed(), 0);
		assertTrue(testBoard.isGameOver());
	}
	
	@Test
	public void testCanMakeMove() {
		int[][] colors1 = { { 1, 2, 3, 4, 5 }, 
							{ 1, 2, 3, 4, 5 },
							{ 1, 2, 3, 4, 5 }, 
							{ 1, 2, 3, 4, 5 }, 
							{ 1, 2, 3, 4, 5 } };
		Board createBoard1 = new Board(colors1);
		assertTrue(createBoard1.canMakeMove());

		int[][] colors2 = { { 1, 2, 3, 4 }, 
							{ 2, 1, 4, 3 }, 
							{ 1, 2, 3, 4 },
							{ 2, 1, 4, 3 } };
		Board createBoard2 = new Board(colors2);
		assertFalse(createBoard2.canMakeMove());
		assertTrue(createBoard2.isGameOver());
	}
	
	@Test
	public void testcanSelect() {
		int[][] colors = { { 1, 2, 3, 4, 5 }, 
						   { 1, 2, 3, 4, 5 },
						   { 1, 2, 3, 4, 5 }, 
						   { 1, 2, 3, 4, 5 }, 
						   { 1, 2, 3, 4, 5 } };
		Board createBoard = new Board(colors);
		createBoard.selectDot(0, 0);
		assertTrue(createBoard.canSelect(1, 0));

		Board createBoard2 = new Board(colors);
		createBoard2.selectDot(0, 0);
		assertFalse(createBoard2.canSelect(0, 1));

		Board createBoard3 = new Board(colors);
		createBoard3.selectDot(2, 4);
		createBoard3.selectDot(2, 3);
		createBoard3.selectDot(2, 2);
		createBoard3.selectDot(2, 1);
		createBoard3.selectDot(3, 1);
		createBoard3.selectDot(4, 1);
		assertFalse(createBoard3.canSelect(2, 0));

		// edges
		// left-side
		Board createBoard4 = new Board(colors);
		createBoard4.selectDot(0, 0);
		assertTrue(createBoard4.canSelect(1, 0));
		createBoard4.selectDot(1, 0);
		assertTrue(createBoard4.canSelect(2, 0));
		createBoard4.selectDot(2, 0);
		assertTrue(createBoard4.canSelect(3, 0));
		createBoard4.selectDot(3, 0);
		assertTrue(createBoard4.canSelect(4, 0));
		createBoard4.selectDot(4, 0);
		assertFalse(createBoard4.canSelect(4, 1));
		try {
			assertFalse(createBoard4.canSelect(5, 0));
		} catch (IndexOutOfBoundsException e) {
			System.out.println("Index chose to select is out of bounds");
		}

		// right-side
		Board createBoard5 = new Board(colors);
		createBoard5.selectDot(0, 4);
		assertTrue(createBoard5.canSelect(1, 4));
		createBoard5.selectDot(1, 4);
		assertTrue(createBoard5.canSelect(2, 4));
		createBoard5.selectDot(2, 4);
		assertTrue(createBoard5.canSelect(3, 4));
		createBoard5.selectDot(3, 4);
		assertTrue(createBoard5.canSelect(4, 4));
		createBoard5.selectDot(4, 4);

		// top
		int[][] colors2 = { { 1, 1, 1, 1, 1 }, 
							{ 2, 2, 2, 2, 2 },
							{ 3, 3, 3, 3, 3 }, 
							{ 4, 4, 4, 4, 4 }, 
							{ 5, 5, 5, 5, 5 } };

		Board createBoard6 = new Board(colors2);
		createBoard6.selectDot(0, 0);
		assertTrue(createBoard6.canSelect(0, 1));
		createBoard6.selectDot(0, 1);
		assertTrue(createBoard6.canSelect(0, 2));
		createBoard6.selectDot(0, 2);
		assertTrue(createBoard6.canSelect(0, 3));
		createBoard6.selectDot(0, 3);
		assertTrue(createBoard6.canSelect(0, 4));
		createBoard6.selectDot(0, 4);

		// bottom
		Board createBoard7 = new Board(colors2);
		createBoard7.selectDot(4, 0);
		assertTrue(createBoard7.canSelect(4, 1));
		createBoard7.selectDot(4, 1);
		assertTrue(createBoard7.canSelect(4, 2));
		createBoard7.selectDot(4, 2);
		assertTrue(createBoard7.canSelect(4, 3));
		createBoard7.selectDot(4, 3);
		assertTrue(createBoard7.canSelect(4, 4));
		createBoard7.selectDot(4, 4);
	}

	@Test
	public void testCanDeselect() {
		int[][] colors = { { 1, 2, 3, 4, 5 }, 
						   { 1, 2, 3, 4, 5 },
						   { 1, 2, 3, 4, 5 }, 
						   { 1, 2, 3, 4, 5 }, 
						   { 1, 2, 3, 4, 5 } };
		Board createBoard1 = new Board(colors);
		createBoard1.selectDot(0, 0);
		createBoard1.selectDot(1, 0);
		createBoard1.selectDot(2, 0);
		createBoard1.selectDot(3, 0);
		createBoard1.deselectDot(3, 0);
		assertTrue(createBoard1.canDeselect(2, 0));
		assertFalse(createBoard1.canDeselect(1, 0));

		Board createBoard2 = new Board(colors);
		createBoard2.selectDot(0, 0);
		createBoard2.selectDot(1, 0);
		createBoard2.selectDot(2, 0);
		createBoard1.deselectDot(2, 0);
		createBoard1.deselectDot(1, 0);
		createBoard1.deselectDot(0, 0);
		assertFalse(createBoard2.canDeselect(0, 0));

		Board createBoard3 = new Board(colors);
		createBoard3.selectDot(0, 0);
		assertTrue(createBoard3.canDeselect(0, 0));
		createBoard3.deselectDot(0, 0);

		try {
			createBoard2.canDeselect(0, 5);
		} catch (IndexOutOfBoundsException e) {
			System.out.println("Index chose to select is out of bounds");
		}
	}

	@Test
	public void testNumberSelected() {
		int[][] colors = { { 1, 2, 3, 4, 5 }, 
						   { 1, 2, 3, 4, 5 },
						   { 1, 2, 3, 4, 5 }, 
						   { 1, 2, 3, 4, 5 }, 
						   { 1, 2, 3, 4, 5 } };
		Board createBoard = new Board(colors);
		createBoard.selectDot(0, 0);
		createBoard.selectDot(1, 0);
		createBoard.selectDot(2, 0);
		createBoard.selectDot(3, 0);
		assertEquals(createBoard.numberSelected(), 4);

		// none selected
		Board createBoardEmpty = new Board(colors);
		assertEquals(createBoardEmpty.numberSelected(), 0);

		// one selected
		Board createBoardOne = new Board(colors);
		createBoardOne.selectDot(0, 0);
		assertEquals(createBoardOne.numberSelected(), 1);
	}

	@Test
	public void testIsClosedShape() {

		int[][] colors1 = { { 1, 1, 3, 4 }, 
							{ 1, 1, 3, 4 }, 
							{ 1, 2, 3, 4 },
							{ 1, 2, 3, 4 } };

		// using 1's to make Shapes
		// regular square Shape
		Board createBoard1 = new Board(colors1);
		createBoard1.selectDot(0, 0);
		createBoard1.selectDot(0, 1);
		createBoard1.selectDot(1, 1);
		createBoard1.selectDot(1, 0);
		assertTrue(createBoard1.isClosedShape());

		int[][] colors2 = { { 1, 1, 1, 4 }, 
							{ 1, 3, 1, 3 }, 
							{ 1, 1, 1, 4 },
							{ 3, 2, 4, 1 } };

		// nonSquare Donut - closed Shape
		Board createBoard2 = new Board(colors2);
		createBoard2.selectDot(0, 0);
		createBoard2.selectDot(0, 1);
		createBoard2.selectDot(0, 2);
		createBoard2.selectDot(1, 2);
		createBoard2.selectDot(2, 2);
		createBoard2.selectDot(2, 1);
		createBoard2.selectDot(2, 0);
		createBoard2.selectDot(1, 0);
		assertTrue(createBoard2.isClosedShape());

		int[][] colors3 = { { 1, 1, 1, 4 }, 
							{ 1, 1, 2, 3 }, 
							{ 1, 1, 1, 4 },
							{ 3, 2, 4, 1 } };

		// nonSquare square + 1 extra Dot Shape
		Board createBoard3 = new Board(colors3);
		createBoard3.selectDot(0, 2);
		createBoard3.selectDot(0, 1);
		createBoard3.selectDot(0, 0);
		createBoard3.selectDot(1, 0);
		createBoard3.selectDot(1, 1);
		assertTrue(createBoard3.isClosedShape());

		// NOT CLOSED SHAPE nonSquare square + 1 extra Dot (different selection order)
		Board createBoard4 = new Board(colors3);
		createBoard4.selectDot(1, 1);
		createBoard4.selectDot(1, 0);
		createBoard4.selectDot(0, 0);
		createBoard4.selectDot(0, 1);
		createBoard4.selectDot(0, 2);
		assertFalse(createBoard4.isClosedShape());
	}

	@Test
	public void testRemoveSelectedDots() {
		int[][] colors = { { 1, 1, 3, 4 }, 
						   { 1, 1, 3, 4 }, 
						   { 1, 2, 1, 1 },
						   { 1, 2, 3, 1 } };
		Board createBoard = new Board(colors);
		createBoard.selectDot(0, 0);
		createBoard.selectDot(1, 0);
		createBoard.selectDot(2, 0);
		createBoard.selectDot(3, 0);
		try {
			createBoard.removeSelectedDots();
		} catch (Exception e) {
			
		}
		assertEquals(createBoard.getScore(), 4);
		
		Board createBoardZero = new Board(colors);
		assertEquals(createBoardZero.getScore(), 0);
		
//		check adding more points on and keeping track of same score
		createBoard.dropRemainingDots();
        createBoard.fillRemovedDots();
		createBoard.selectDot(3, 3);
		createBoard.selectDot(2, 3);
		try{
			createBoard.removeSelectedDots();
		} catch (Exception e){
			System.out.println("not removed");
		}
		assertEquals(createBoard.getScore(), 6);
		
		
	}

	@Test
	public void testRemoveSameColor() {
		int[][] colors1 = { { 1, 1, 3, 4 }, 
							{ 1, 1, 3, 4 }, 
							{ 1, 2, 1, 1 },
							{ 1, 2, 3, 1 } };
		Board createBoard = new Board(colors1);
		createBoard.selectDot(0, 0);
		createBoard.selectDot(0, 1);
		createBoard.selectDot(1, 1);
		createBoard.selectDot(1, 0);
		try {
			createBoard.removeSelectedDots();

		} catch (Exception e) {

		}
		assertEquals(createBoard.getScore(), 9);
		
	}

	@Test
	public void testFindBestSquare() {
		int[][] colors = { { 1, 1, 3, 4 }, 
						   { 1, 1, 3, 4 }, 
						   { 3, 2, 2, 2 },
						   { 3, 2, 2, 2 } };
		Board createBoard = new Board(colors);
		ArrayList<Point> select = createBoard.findBestSquare();
		for (int i = 0; i < select.size(); i++) {
			int xValue = (int) select.get(i).getX();
			int yValue = (int) select.get(i).getY();
			createBoard.selectDot(xValue, yValue);
		}
		try {
			createBoard.removeSelectedDots();
		} catch (Exception e) {

		}
		assertEquals(createBoard.getScore(), 6);
	}

}


