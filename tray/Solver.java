import java.util.*;
import java.io.*;

public class Solver {
	
	public static void main (String[] args) {
		//initialize tray
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
		Tray myTray = new Tray(initFile, goalFile, true);

		if (myTray.isDone()) {
			System.exit(0);
		}

		//prepare for DFS
		HashSet<Tray> pastGamestates = new HashSet<Tray>();
		Stack<Tray> fringe = new Stack<Tray>();
		pastGamestates.add(myTray);
		fringe.push(myTray);

		//DFS until winning state is found, or entire gamestate graph is traversed
		Tray end = null;
		while (!fringe.empty()) {
			Tray currConfig = fringe.pop();
			for (Tray.Block b : currConfig.myCurrConfig) {
				System.out.println(b.toString());
			}
			System.out.println();
			HashSet<Tray> children = currConfig.getChildrenConfigs();
			for (Tray config : children) {
				if (config.isDone()) {
					end = config;
					break;
				}
				if (pastGamestates.contains(config)) {
					continue;
				}
				pastGamestates.add(config);
				fringe.push(config);	
			}
		}
		System.out.println(end);
		if (end == null) {
			System.exit(1);
		}

		//Print moves
		Stack<String> moves = new Stack<String>();
		findPathHelper(myTray, end, pastGamestates, moves);
		while (!moves.empty()) {
			System.out.println(moves.pop());
		}

		System.exit(0);
	}

	public static void findPathHelper(Tray begConfig, Tray endConfig, 
								HashSet<Tray> traversed, Stack<String> pathSoFar) {
		Tray parentConfig = null;
		for (Tray config : traversed) {
			if (config.getChildrenConfigs().contains(endConfig)) {
				parentConfig = config;
				break;
			}
		}
		pathSoFar.push(Tray.findMove(parentConfig, endConfig));
		if (parentConfig.equals(begConfig)) {
			return;
		}
		traversed.remove(endConfig);
		findPathHelper(begConfig, parentConfig, traversed, pathSoFar);
	}

}