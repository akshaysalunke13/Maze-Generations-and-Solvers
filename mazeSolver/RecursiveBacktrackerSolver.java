package mazeSolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import maze.Cell;
import maze.Maze;

/**
 * Recursive Backtracker to solve maze.
 */
public class RecursiveBacktrackerSolver implements MazeSolver {

	HashMap<String, Node> visitedNodes = new HashMap<String, Node>();
	int solutionLength = 0;

	@Override
	public void solveMaze(Maze maze) {

		// List to store neighbour nodes
		HashMap<String, Node> nodes = new HashMap<String, Node>();

		// Map all maze cells into nodes
		for (int i = 0; i < maze.sizeR; i++) {
			for (int j = 0; j < maze.sizeC; j++) {
				Node newNode = new Node(maze.map[i][j]);
				//Put newly created node in arraylist
				nodes.put(newNode.getKey(), newNode);
			}
		}

		//Iterate over all cells
		for (Node node : nodes.values()) {
			/* Creating new edge for each direction */
			for (int i = 0; i < Maze.NUM_DIR; i++) {
				// if neighbour exists in direction
				if (node.getCell().neigh[i] != null) {
					
					if (node.getCell().wall[i].present == false && node.getCell().wall[i].drawn == false) {
						//Get key of neighbour to current node
						String key = "[" + node.getCell().neigh[i].c + "," + node.getCell().neigh[i].r + "]";
						Node tempNode = nodes.get(key);
						if (tempNode != null)
							node.setNeighbour(tempNode, i);
					}
				}
			}
			// If maze is of type tunnel
			if (maze.type == Maze.TUNNEL) {
				//If this node is a tunnel
				if (node.getCell().tunnelTo != null) {
					Node tunnelEnd = nodes.get("[" + node.getCell().tunnelTo.c + "," + node.getCell().tunnelTo.r + "]");
					node.setNeighbour(tunnelEnd, 6);
				}
			}
		}

		// Initialise start and end for maze
		Node currentNode = nodes.get("[" + maze.entrance.c + "," + maze.entrance.r + "]");
		//
		Node endNode = nodes.get("[" + maze.exit.c + "," + maze.exit.r + "]");

		Random random = new Random();

		// loop until we reach the end
		while ( currentNode != endNode ) {
			
			maze.drawFtPrt(currentNode.getCell());

			// If this cell has a tunnel and is not visited
			if (currentNode.getNeighbour(6) != null && !visitedNodes.containsKey(currentNode.getNeighbour(6).getKey())) {
				//set tunnel ends previous as the current cell
				currentNode.getNeighbour(6).setPrevious(currentNode);
				visitedNodes.put(currentNode.getKey(), currentNode);
				
				//Move to next cell.
				currentNode = currentNode.getNeighbour(6);
			} else {
				// Check for unvisited neighbours
				boolean visited = true;
				for (int i = 0; i < 7; i++) {
					if (currentNode.getNeighbour(i) != null) {
						if (!visitedNodes.containsKey(currentNode.getNeighbour(i).getKey())) {
							visited = false;
						}
					}
				}
				// If all nodes are visited, recurse back
				if (visited) {
					//current node complete, put in list
					visitedNodes.put(currentNode.getKey(), currentNode);
					// move to previous node
					currentNode = currentNode.getPrevious();
				}
				// If not all not nodes are visited
				else {
					boolean b = false;
					while (!b) {
						//arraylist to store directions
						ArrayList<Integer> dirs = new ArrayList<Integer>();
						for (int i = 0; i < Maze.NUM_DIR; i++) {
							if (currentNode.neighbour[i] != null) {
								if (!visitedNodes.containsKey(currentNode.neighbour[i].getKey())) {
									dirs.add(i);
								}
							}
						}
						random = new Random();
						int randomDirection;
						randomDirection = random.nextInt(dirs.size());
						int dir = dirs.get(randomDirection);

						if (currentNode.getNeighbour(dir) != null) {
							if (!visitedNodes.containsKey(currentNode.getNeighbour(dir).getKey())) {
								
								Node newNode = nodes.get(currentNode.getNeighbour(dir).getKey());
								newNode.setPrevious(currentNode);
								visitedNodes.put(currentNode.getKey(), currentNode);
								currentNode = newNode;
								b = true;
							}
						}

					}
				}
			}
		}
		// Draw the last node.
		maze.drawFtPrt(currentNode.getCell());
		visitedNodes.put(currentNode.getKey(), currentNode);
		currentNode.getPathLength();
		//System.out.println("Path length of the solution is " + pathLength);
	} // end of solveMaze()

	@Override
	public boolean isSolved() {
		return true;
	}

	@Override
	public int cellsExplored() {
		// TODO Auto-generated method stub
		return visitedNodes.size();
	}


	//custom class to handle cells
	private class Node {
		private Cell cell;
		private String key;
	// Array of neighbouring nodes 6 direction for hexagonal maze + 1 tunnel (if any)
		private Node[] neighbour = new Node[7];
		private Node previous;

		public Node(Cell cell) {
			this.key = "[" + cell.c + "," + cell.r + "]";
			this.cell = cell;
		}

		public void getPathLength() {
			if (previous != null) {
				previous.getPathLength();
				solutionLength++;
			}
		}

		public Cell getCell() {
			return cell;
		}

		public String getKey() {
			return key;
		}

		public void setNeighbour(Node node, int dir) {
			neighbour[dir] = node;
		}

		public void setPrevious(Node node) {
			this.previous = node;
		}

		public Node getNeighbour(int dir) {
			return neighbour[dir];
		}

		public Node getPrevious() {
			return this.previous;
		}

	}
} // end of class RecursiveBackTrackerSolver