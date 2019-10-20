package mazeGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import maze.*;

public class KruskalGenerator implements MazeGenerator {
   
   // Arraylists to store cell nodes, and the edges connecting them.
   ArrayList<Edge> edges = new ArrayList<Edge>();
   ArrayList<Node> nodes = new ArrayList<Node>();
   ArrayList<NodeSet> connectedNodes = new ArrayList<NodeSet>();
   Random random = new Random();
   
   @Override
   public void generateMaze(Maze maze){
      
      //Create node for each maze cell
	      for(int i = 0; i < maze.sizeR; i++){	     
	         for(int j = 0; j < maze.sizeC; j++){
	        	 //Tunnel maze type
	            if(maze.type == Maze.TUNNEL)
	               nodes.add(new Node(maze.map[i][j], Maze.TUNNEL));
	            else // Normal maze
	               nodes.add(new Node(maze.map[i][j], Maze.NORMAL));
	         }
	      }
	   

	   //
	   for(Node node : nodes){
	      /* Creating new edge for each direction */
	      for(int i = 0; i < Maze.NUM_DIR; i++){ 
	         if(node.getCell().neigh[i] != null){
	            Cell tempCell = node.getCell().neigh[i];
	            Node tempNode = null;
	            for(Node sNode : nodes){
	               if(sNode.getCell() == tempCell){
	                  tempNode = sNode;
	                  break;
	               }
	            }
	            if(tempNode != null){
	               Edge tempEdges = new Edge(node, tempNode, i);
	               edges.add(tempEdges);
	               node.addEdge(tempEdges, i);
	            }
	         } 
	      }
	      if(maze.type == 1){ // Tunnel Maze
	         if(node.getCell().tunnelTo != null){
	            Node tunnelDest = null;
	            for(Node sNode : nodes){ // Search for tunnel end in all nodes
	               if(sNode.getCell() == node.getCell().tunnelTo){
	                  tunnelDest = sNode;
	                  break;
	               }
	            }
	            node.setTunnel(tunnelDest);
	            // Check if connection already exists between node and tunnelDest
               if(!checkNodeConnection(node, tunnelDest)){   
   	            NodeSet tunnelNodeSet = new NodeSet();
                  tunnelNodeSet.connectedNodes.put(node.getKey(), node);
                  tunnelNodeSet.connectedNodes.put(tunnelDest.getKey(), tunnelDest);
                  connectedNodes.add(tunnelNodeSet);
               }
	         }
	      }
      }

	   while(!edges.isEmpty()){	   
	      
	      int index = random.nextInt(edges.size());
         int direction = edges.get(index).getDir();  
         
         if(!checkNodeConnection(edges.get(index).getFrom(), edges.get(index).getTo())){
            
            edges.get(index).getFrom().addConnectedEdge(direction);
            
            int reverse = Maze.oppoDir[direction];
            edges.get(index).getTo().addConnectedEdge(reverse);
            edges.get(index).getFrom().getCell().wall[direction].drawn = false;
            edges.get(index).getFrom().getCell().wall[direction].present = false;
            
            NodeSet fromSet = null;
            NodeSet toSet = null;
            
            //If cells are connected
            for(NodeSet nodeSet : connectedNodes){ 
               if(nodeSet.searchNode(edges.get(index).getFrom()))
                  fromSet = nodeSet;
               if(nodeSet.searchNode(edges.get(index).getTo()))
                  toSet = nodeSet;
            }
            //  No connection found
            if(fromSet == null && toSet == null){
               NodeSet newNodeSet = new NodeSet();
               newNodeSet.connectedNodes.put(edges.get(index).getFrom().getKey(), edges.get(index).getFrom());
               newNodeSet.connectedNodes.put(edges.get(index).getTo().getKey(), edges.get(index).getTo());
               connectedNodes.add(newNodeSet);
            }
            //One connection found
            else if( fromSet == null)
               toSet.connectedNodes.put(edges.get(index).getFrom().getKey(), edges.get(index).getFrom());
            else
               fromSet.connectedNodes.put(edges.get(index).getTo().getKey(), edges.get(index).getTo());
         }
         
         else{
        	 //Check for loop
            if(!isLoop(edges.get(index).getFrom(), edges.get(index).getTo())){
               edges.get(index).getFrom().addConnectedEdge(direction);
               int rev = Maze.oppoDir[direction];
               edges.get(index).getTo().addConnectedEdge(rev);
               
               edges.get(index).getFrom().getCell().wall[direction].drawn = false;
               edges.get(index).getFrom().getCell().wall[direction].present = false;

               NodeSet fromSet = null;
               NodeSet toSet = null;
               for(NodeSet nodeSet : connectedNodes){
                 
                  if(nodeSet.searchNode(edges.get(index).getFrom()))
                     fromSet = nodeSet;
                  if(nodeSet.searchNode(edges.get(index).getTo()))
                     toSet = nodeSet;
               }                 
               //Join 2 disjoint trees.
               NodeSet newNodeSet = new NodeSet();
               newNodeSet.connectedNodes.putAll(fromSet.connectedNodes);
               newNodeSet.connectedNodes.putAll(toSet.connectedNodes);
               connectedNodes.remove(fromSet);
               connectedNodes.remove(toSet);
               connectedNodes.add(newNodeSet);
            }
         }
         edges.remove(index);  
	   }
	} // end of generateMaze()
	
   // Method to check if 2 nodes are connected
   public boolean checkNodeConnection(Node fromNode, Node toNode){
      NodeSet fromSet = null;
      NodeSet toSet = null;
      if(connectedNodes.size() == 0)
         return false;
      for(NodeSet nodeSet : connectedNodes){
         if(nodeSet.searchNode(fromNode))
            fromSet = nodeSet;
         if(nodeSet.searchNode(toNode))
            toSet = nodeSet;
      }
      if(fromSet == null || toSet == null)
         return false;
      else
         return true;
   }
   
 
   public boolean isLoop(Node from, Node to){
      NodeSet toSet = null;
      for(NodeSet nodeSet : connectedNodes){
         if(nodeSet.searchNode(to))
            toSet = nodeSet;
      }
      if(toSet.connectedNodes.containsValue(from))
         return true;
      return false;
     }
	  
   
	private class NodeSet{
      private HashMap<String, Node> connectedNodes = new HashMap<String, Node>();
      public boolean searchNode(Node node){
         if(connectedNodes.containsValue(node))
            return true;
         else
            return false;
      }
	}
	

	private class Node{
	   private String key;
	   private Cell cell;
	   private Edge[] edges = new Edge[6];
	   private Edge[] connectedEdges = new Edge[6];
	   
	   public Node(Cell cell, int type){
	      this.cell = cell;
	      key = "[" + cell.c + "," + cell.r + "]";
	   }
	   public String getKey(){
	      return key;
	   }
	   public void addEdge(Edge edge, int dir){
	      edges[dir] = edge;
	   }
	   public void addConnectedEdge(int dir){
	      connectedEdges[dir] = edges[dir];
	   }
	   public void setTunnel(Node node){
	   }
	   public Cell getCell(){
	      return cell;
	   }
	}
	
	
	private class Edge{
	   private Node from;
	   private Node to;
	   private int direction;
	   
	   public Edge(Node from, Node to, int direction){
	      this.from = from;
	      this.to = to;
	      this.direction = direction;
	   }	   
	   public int getDir(){
	      return direction;
      }
	   public Node getFrom(){
         return from;
      }
	   public Node getTo(){
         return to;
      }
	}
} // end of class KruskalGenerator