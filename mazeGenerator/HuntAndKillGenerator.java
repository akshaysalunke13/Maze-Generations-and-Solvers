package mazeGenerator;

import java.util.ArrayList;
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
		int randomRow = r.nextInt(maze.sizeR);
		int randomCol = r.nextInt(maze.sizeC);
		
		Cell start = maze.map[randomRow][randomCol];
		this.m = maze;
		for(Cell[] c: maze.map) {
			for(Cell t: c) {
				visited.add(t);
			}
		}
		
		while(visited.size() > 0) {
			if(start != null)
				carve(start);
			else {
				break;
			}
		start = hunt(m);
		}
	}

	private void carve(Cell input) {
		// TODO Auto-generated method stub
		input.isVisited = true;
		visited.remove(input);
		int count = 0;
		for(int i = 0; i < input.neigh.length; i++) {
			if(input.neigh[i] != null && !input.neigh[i].isVisited) {
				//Count the total unvisited neighbours for this cell.
				count++;
				//start.neighbours.add(e)
			}
		}
		
		if(count > 0) {
			//If tthis cell has neighbours
		int neighbours[] = new int[count];
		//Generate a random number aka. random neighbour
		int randomNeighbour = r.nextInt(count);
		
		//Reset count for reuse
		count = 0;
		for(int i = 0; i < input.neigh.length; i++) {
			if(input.neigh[i] != null && !input.neigh[i].isVisited)
				//Add direction of unvisited neighbour to neighbour[] array. This can be improved...
			neighbours[count++] = i;
			
		}
		
		//If there are neighbours. carve wall to random neighbour selected above
		if(neighbours.length > 0) {
		carve(input, neighbours[randomNeighbour]);
		carve(m.map[input.r + Maze.deltaR[neighbours[randomNeighbour]]][input.c + Maze.deltaC[neighbours[randomNeighbour]]]);
		}
	} else {
		//hunt
		//return;
	}
	}

	private void carve(Cell c, int dir) {
		c.wall[dir].present = false;
	}
	private Cell hunt(Maze m) {
		for (int i = m.sizeR-1; i >= 0; i--) {
			for(int j = 0; j < m.sizeC; j++) {
			if (!m.map[i][j].isVisited && hasUnvisitedNeigh(m.map[i][j])) {
				return m.map[i][j];
			}
			}
		}
		return null;
	}

	private boolean hasUnvisitedNeigh(Cell cell) {
		// TODO Auto-generated method stub
		for (int i = 0; i < cell.neigh.length; i++) {
			if(cell.neigh[i] != null) {
				if(!cell.neigh[i].isVisited) {
					return true;
				}
			}
		}
		return false;
	}
} // end of class HuntAndKillGenerator
