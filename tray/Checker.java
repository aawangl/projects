import java.io.*;
import java.util.*;

public class Checker {

	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Exactly two arguments required");
			System.exit(2);
		}
		File initFile = new File(args[0]);
		File goalFile = new File(args[1]);
		if (!initFile.exists()) {
			System.out.println("File " + args[0] + " does not exist");
			System.exit(3);
		}
		if (!goalFile.exists()) {
			System.out.println("File " + args[1] + " does not exist");
			System.exit(3);
		}
		Tray myTray = new Tray(initFile, goalFile);
		Scanner myScanner = new Scanner(System.in);
		String nextLine;
		try {
			while (true) {
				String[] stdin = myScanner.nextLine().split(" ");
				if (stdin.length != 4) {
					System.out.println("Moves must consist of 4 arguments separated by single spaces");
					System.exit(4);
				} 
				int prevY = Integer.parseInt(stdin[0]);
				int prevX = Integer.parseInt(stdin[1]);
				int newY = Integer.parseInt(stdin[2]);
				int newX = Integer.parseInt(stdin[3]);
				myTray.makeMove(prevY, prevX, newY, newX);
			}			 
		} catch (NumberFormatException e) {
			System.out.println("Moves must consist of integers");
			System.exit(4);
		} catch (NoSuchElementException e) {
			if (myTray.isDone()) {
				System.out.println("Puzzle solved!");
				System.exit(0);
			}
			System.out.println("Reached end of input without winning");
			System.exit(1);
		}
	}

}

