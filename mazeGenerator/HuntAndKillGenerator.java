package mazeGenerator;

import java.io.Console;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import maze.Cell;
import maze.Maze;

public class HuntAndKillGenerator implements MazeGenerator {

	ArrayList<Cell> visited;
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

		carve(start);
		hunt(m);
	}

	private void carve(Cell start) {
		// TODO Auto-generated method stub
		start.isVisited = true;
		int count = 0;
		for(int i = 0; i < start.neigh.length; i++) {
			if(start.neigh[i] != null && !start.neigh[i].isVisited) {
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
		for(int i = 0; i < start.neigh.length; i++) {
			if(start.neigh[i] != null && !start.neigh[i].isVisited)
				//Add direction of unvisited neighbour to neighbour[] array. This can be improved...
			neighbours[count++] = i;
			
		}
		
		//If there are neighbours. carve wall to random neighbour selected above
		if(neighbours.length > 0) {
			
		start.wall[neighbours[randomNeighbour]].present = false;
		//recursive call to carve the new neighbour
		carve(m.map[start.r + Maze.deltaR[neighbours[randomNeighbour]]][start.c + Maze.deltaC[neighbours[randomNeighbour]]]);
		}
	}
	}

	private void hunt(Maze m) {
		for (int i = m.sizeC; i >= 0; i--) {
			for(int j = m.sizeC; j >= 0; j--) {
			if (!m.map[i][j].isVisited) {
				carve(m.map[i][j]);
			}
			}
		}
	}
} // end of class HuntAndKillGenerator
