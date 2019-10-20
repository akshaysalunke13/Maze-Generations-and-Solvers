package mazeGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import maze.Cell;
import maze.Maze;

public class HuntAndKillGenerator implements MazeGenerator {

	ArrayList<Cell> visited = new ArrayList<Cell>();
	Random r = new Random();
	Maze m;

	@Override
	public void generateMaze(Maze maze) {
		// TODO Auto-generated method stub
		init(maze);
	} // end of generateMaze()

	private void init(Maze maze) {
		// TODO Auto-generated method stub
		Cell start = maze.entrance;
		visited.add(start);
		this.m = maze;

		while (visited.size() > 0) {
			carve(visited.get(0));
			// hunt(m);
		}
	}

	private void carve(Cell start) {
		// TODO Auto-generated method stub
		visited.remove(start);
		List<Integer> neighbourDirs = new ArrayList<Integer>();
		start.isVisited = true;
		for (int i = 0; i < start.neigh.length; i++) {
			if (start.neigh[i] != null && !start.neigh[i].isVisited) {
				// Count the total unvisited neighbours for this cell.
				neighbourDirs.add(i);
			}
		}
		if (start.tunnelTo != null) {
			//Cell is a tunnel
			if(!start.tunnelTo.isVisited) {
				visited.add(start.tunnelTo);
				carve(start.tunnelTo);
			}
		} 
		else if (neighbourDirs.size() > 0) {
			//Normal cell
		if (neighbourDirs.size() > 0) {
			for (int i = 0; i < start.neigh.length; i++) {
				if (start.neigh[i] != null && !start.neigh[i].isVisited) {
					if (m.map[start.r + Maze.deltaR[i]][start.c + Maze.deltaC[i]] != null)
						start.neighbours.put(i, m.map[start.r + Maze.deltaR[i]][start.c + Maze.deltaC[i]]);
				}
			}

			int totalNeigh = neighbourDirs.size();
			Collections.shuffle(neighbourDirs);

			// If there are neighbours. carve wall to random neighbour selected above
			for (int i = 0; i < totalNeigh; i++) {

				if (start.neighbours.get(neighbourDirs.get(i)) != null) {
					if (!start.neighbours.get(neighbourDirs.get(i)).isVisited) {
						start.wall[neighbourDirs.get(i)].present = false;
						carve(start.neighbours.get(neighbourDirs.get(i)));
						visited.add(start.neighbours.get(neighbourDirs.get(i)));
					}
				}
			}

		}
		}
	}

	@SuppressWarnings("unused")
	private void hunt(Maze m) {
		for (int i = m.sizeC - 1; i >= 0; i--) {
			for (int j = m.sizeC - 1; j >= 0; j--) {
				if (!m.map[i][j].isVisited) {
					carve(m.map[i][j]);
				}
			}
		}
	}
} // end of class HuntAndKillGenerator
