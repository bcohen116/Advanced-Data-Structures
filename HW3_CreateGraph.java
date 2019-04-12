// This is a library to be used to represent a Graph and various measurments for a Graph
//  and to perform optimization using Particle Swarm Optimization (PSO)
//    Copyright (C) 2008, 2015 
//       Patrick Olekas - polekas55@gmail.com
//       Ali Minai - minaiaa@gmail.com
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//   You should have received a copy of the GNU General Public License
//   along with this program.  If not, see <http://www.gnu.org/licenses/>.
package psograph;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;
import java.util.Vector;

import psograph.graph.*;
import psograph.measurements.PercentageInLargestCluster;
import psograph.util.Util;



public class CreateGraph 
{

	//This is the directory where the Seed is generated.  The Seed is the Node configuration
	//with no edges
	File m_SeedDirectory;  
	
	//This is where the graphs will reside TODO need more
	File m_GraphDirectory;
	
	Graph m_graphSeed;
	Graph canditate;
	
	double m_basisCost;
	NodeLocationCalculator m_nodeLoc;
	
	int seed = 0;
	int candidate = 0;
	
	public CreateGraph()
	{
		//m_SeedDirectory = new File("C:\\TestHeuristic3\\StandardStartingPoint");
                m_SeedDirectory = new File("C:\\Users\\Ben\\Downloads\\JavaPSOGraph3(1)\\JavaPSOGraph3\\StandardStartingPoint");
	}
	
	private void generateSeed() throws Exception
	{
		/* commenting out to use standardized seed
		//Generate a new graph for as the Seed
		m_graphSeed = new Graph(GraphConstants.NUM_NODES);

		//graphSeed.printWithLocationAndWeights();
		System.out.println("--------------------------------------");
		System.out.println("Saving Graph Seed "+seed);
		// stream out seed
		
		Util.streamOutSeed(m_SeedDirectory, m_graphSeed);   
			*/	
		File m_SeedDirectory2 = Util.CreateSeedDirectory();
		m_GraphDirectory = Util.CreateCalculatedGraphDirectory(m_SeedDirectory2);

		
		m_graphSeed = Util.streaminSeed(m_SeedDirectory);
		
		System.out.println("--------------------------------------");
		
		m_nodeLoc = new NodeLocationCalculator(m_graphSeed, false);
		m_nodeLoc.calculateResults();
		
		
		
	}
	
	private void calculateCostBasis() throws Exception
	{
		m_basisCost =0;
		//CostBasis costBasis = new CostBasis(m_graphSeed);
			
		//System.out.println("-----------exponentialCostBasis-----------------");
		//costBasis.generate(GraphConstants.MAX_CONNECTIONS+300);
		//System.out.println("Total edges "+exponentialCostBasis.getTotalEdges());			
		//Util.streamOutExponentialGraph(m_SeedDirectory, costBasis,1);
		
		//Used Standard Cost Basis
		File CostBasisFile = new File(m_SeedDirectory+"\\ExponentialCostBasis1.Graph");
		CostBasis costBasis = Util.streaminCostBasis(CostBasisFile);
		m_basisCost = costBasis.getCost();


		PercentageInLargestCluster expoLCC = new PercentageInLargestCluster(costBasis);
		double valueCostBasisLCC = expoLCC.Measure();
		if(Double.compare(valueCostBasisLCC,1.0) != 0)
			System.out.println("valueExpoLCC is not equal to 1.0 : "+valueCostBasisLCC + "differ of :" + (1.0 - valueCostBasisLCC));	
		
		//Do we want to perform some measurements on this guy
		
	}
	
	private void connnectCandidate() throws Exception
	{
		
		Random pickNode = new Random();

		NodeLocationCalculator workingNodeLoc = new NodeLocationCalculator(m_nodeLoc, false);
		
		//workingNodeLoc.printWithLocationAndWeights();
		
		canditate = new Graph(m_graphSeed);
		
		Vector<Node> v_Nodes = new Vector<Node>(m_graphSeed.getHeaderNodesMap().values());
		int jj;

		//
		// We should really be picking each node randomly.
		// 
		//  three different ideas on how to connect initially
		//  n = number of nodes
		//  1)Put in n initial connections.  Where each node
		//  will have a connection to another node
		//
		//  2) have n-1 connections.  This will gives us a chance to make a tree connecting
		// all nodes
		//
		// 3) Make a MST of the nodes
		//
		
		int num_of_nodes = v_Nodes.size();				
		
		for(jj=0; jj < num_of_nodes ; jj++)
		{
			int t_id = pickNode.nextInt(v_Nodes.size());
			
			Node n = workingNodeLoc.chooseCloseNode(v_Nodes.get(t_id));
			if(n == null)
			{
				throw new Exception("ERROR - null node returned for choose close node");
			}
			else
			{
				//System.out.println("a real node returned for choose close node");
				Edge ci = n.getConnectionInfo(v_Nodes.get(t_id));
				canditate.addConnection(v_Nodes.get(t_id).getID(), n.getID(), ci.getWeight());
				
				//Remove from working NodeLoc so we don't hit it in random phase
				workingNodeLoc.removeConnection(v_Nodes.get(t_id).getID(), n.getID());
				
				//System.out.println("v_Nodes.get(t_id).m_id is "+v_Nodes.get(t_id).m_id);
				//System.out.println("t_id is "+t_id);
				v_Nodes.remove(t_id);
				//System.out.println("n.m_id is "+n.m_id);
				//int t2 = v_Nodes.indexOf(n);
				//System.out.println("t2 is "+t2);							
			}
		}
		
	//	System.out.println("Total edges after first connect "+canditate.getTotalEdges());
						
		//System.out.println("Print out of candiate after initial connectiveness");
		//canditate.printWithLocationAndWeights();
		//printMFile(canditate);
		//System.out.println("End of Print out of candiate after initial connectiveness");
		
		
		v_Nodes = new Vector<Node>(m_graphSeed.getHeaderNodesMap().values());
		//int MAX_CONNECTIONS = (6 * v_Nodes.size());
		
		//System.out.println("MAX_CONNECTIONS "+MAX_CONNECTIONS);
		
		v_Nodes = new Vector<Node>(m_graphSeed.getHeaderNodesMap().values());
		for (jj=0; jj < GraphConstants.MAX_CONNECTIONS ; )
		{
			
			int t_id = pickNode.nextInt(v_Nodes.size());
			Node NodeToConnect = v_Nodes.get(t_id);

			Node n = workingNodeLoc.chooseNode(NodeToConnect);
                        
			if(n == null)
			{
				System.out.println("null node returned for choose random/close node");
			}
			else
			{
        
				Edge ci = n.getConnectionInfo(NodeToConnect);
				canditate.addConnection(NodeToConnect.getID(), n.getID(), ci.getWeight());
				workingNodeLoc.removeConnection(NodeToConnect.getID(), n.getID());
				jj=jj+2;
			}
		}
		
	//	System.out.println("Random :"+workingNodeLoc.m_random+" closest :"+workingNodeLoc.m_closest);
     
		if(canditate.getTotalEdges() != GraphConstants.MAX_CONNECTIONS)
			throw new Exception("Total edges "+canditate.getTotalEdges()+ " does not equal "+GraphConstants.MAX_CONNECTIONS);		

	}
        
        //This method is used for our HW3 MST code in replacement of connectCandidate
        private void connnectMSTCandidate() throws Exception
	{
		
		Random pickNode = new Random();

		NodeLocationCalculator workingNodeLoc = new NodeLocationCalculator(m_nodeLoc, false);
		
		canditate = new Graph(m_graphSeed);
		
		Vector<Node> v_Nodes = new Vector<Node>(m_graphSeed.getHeaderNodesMap().values());
		int jj;
                Vector<Node> v_Nodes2 = v_Nodes;//Since we delete things from v_Nodes, need to keep a copy of the original list

		//
		// We should really be picking each node randomly.
		// 
		//  three different ideas on how to connect initially
		//  n = number of nodes
		//  1)Put in n initial connections.  Where each node
		//  will have a connection to another node
		//
		//  2) have n-1 connections.  This will gives us a chance to make a tree connecting
		// all nodes
		//
		// 3) Make a MST of the nodes
		//
		
		int num_of_nodes = v_Nodes.size();
                ArrayList<Edge> usedEdges = new ArrayList();//keep track of what edges have already been used.
		
		for(jj=0; jj < num_of_nodes - 1; jj++)
		{
			int t_id = pickNode.nextInt(v_Nodes.size());
			
			//Node n = workingNodeLoc.chooseCloseNode(v_Nodes.get(t_id));
                        Node NodeToConnect = v_Nodes.get(v_Nodes.size() - 1);
                        

			Node n = workingNodeLoc.chooseNode(NodeToConnect);
                        TreeMap<Double,Vector<Integer>> neighbors2 = n.getNeighborDistribution();//Used for finding the nearest Node to n
			if(n == null)
			{
				throw new Exception("ERROR - null node returned for choose close node");
			}
			else
			{
                            //Get the neighbor edges to node n and add them to a list
                            TreeMap<Integer,Edge> neighbors = n.getNeighbors();
                            ArrayList<Edge> edgeList = new ArrayList();//make a list to store neighboring edges
                            for (int x = 0; x < neighbors.size();x++){
                                //Loop through all the neighbors connected to this node
                                edgeList.add(neighbors.get(x));//add to the list of edges connected
                                
                                
                            }
                            
                            //We now have the edges for node n
                            //Now let's find the minimum edge that is usable
                            Edge minimum = null;
                            Node newNode = null;
                            for (Edge edge : edgeList){
                                //Loop through the list of edges from earlier
                                n.getEdgeInfo(t_id);
                                if (minimum == null || (edge != null && minimum.getWeight() > edge.getWeight())){
                                    //keep track of the smallest edge so we can make a minimum spanning tree
                                    if (usedEdges.isEmpty()){
                                        //Set starting edge
                                        Vector<Integer> nodeId = neighbors2.get(edge.getWeight());
                                            
                                        for (int y = 0; y < v_Nodes2.size() - 1;y++){
                                            if (v_Nodes2.get(y).getID() == nodeId.lastElement()){
                                                //At this point we have gotten the node that the minimum edge is trying to connect to from node n
                                                newNode = v_Nodes2.get(y);
                                            }
                                        }
                                        minimum = edge;//set the new minimum
                                   
                                    }
                                    else{
                                        //The edge found was not the first in the list, so it will go here
                                        //We will check if the edge has been previously used or if it is the new minimum weight
                                        boolean wasEdgeUsed = false;
                                        for (Edge usedEdge: usedEdges){
                                            if (usedEdge == edge){
                                                //Loop through the list of used edges and check if current edge has been used
                                                wasEdgeUsed = true;
                                            }
                                        }   
                                        if (!wasEdgeUsed){
                                            //if edge was found to not be used, add it to the MST
                                            if (edge != null){
                                                Vector<Integer> nodeId = neighbors2.get(edge.getWeight());
                                            
                                                for (int y = 0; y < v_Nodes2.size() - 1;y++){
                                                    if (v_Nodes2.get(y).getID() == nodeId.lastElement()){
                                                        //At this point we have gotten the node that the minimum edge is trying to connect to from node n
                                                        newNode = v_Nodes2.get(y);
                                                    }
                                                }
                                                minimum = edge;//set the new minimum
                                            }
                                            
                                            
                                        }
                                    }
                                    
                                }
                            }
                            
                            //At this point we have both the original node n, and the newNode which is what the minimum edge is trying to connect to
                            boolean nIsConnected = false;
                            boolean newIsConnected = false;
                            if (null != newNode){
                                //Now lets check what other Nodes newNode connects to so we can try to find a "parent" Node
                                TreeMap<Integer,Edge> newNodeNeighbors = newNode.getNeighbors();
                                ArrayList<Edge> newNodeEdgeList = new ArrayList();//make a list to store neighboring edges
                                if (newNodeNeighbors != null){
                                    for (int x = 0; x < newNodeNeighbors.size();x++){
                                        //Loop through all the neighbors connected to this node
                                        newNodeEdgeList.add(newNodeNeighbors.get(x));//add to the list of edges connected
                                    }
                                }
        
                                ArrayList<Node> connectedNodes = new ArrayList();
                                
                                //at this point we have a list of edges connected to newNode
                                for (Edge edge : newNodeEdgeList){
                                    for (Edge usedEdge : usedEdges){
                                        if (edge == usedEdge){
                                            //Loop through all the previously used edges to try to find a parent Node
                                            Vector<Integer> nodeId = neighbors2.get(edge.getWeight());
                                            
                                            for (int y = 0; y < v_Nodes2.size() - 1;y++){
                                                if (v_Nodes2.get(y).getID() == nodeId.lastElement()){
                                                    //This is a parent node, save it for later
                                                    connectedNodes.add(v_Nodes2.get(y));
                                                }
                                            }
                                        }
                                    }
                                }
                                ArrayList<Node> connectedNodes2 = new ArrayList();
                                
                                //At this point we have a list of parent nodes connected to newNode
                                //Now we need to check if any of those parents are connected to the original node n
                                //If they are connected, we have a cycle, which is bad, so ignore the edge we are trying to connect
                                for (Node node : connectedNodes){
                                    node.getNeighbors();
                                    TreeMap<Integer,Edge> newNodeNeighbors2 = node.getNeighbors();
                                    ArrayList<Edge> newNodeEdgeList2 = new ArrayList();//make a list to store neighboring edges
                                    for (int x = 0; x < newNodeNeighbors2.size();x++){
                                        //Loop through all the neighbors connected to this node
                                        newNodeEdgeList2.add(newNodeNeighbors.get(x));//add to the list of edges connected
                                    }
                                    
                                    for (Edge edge : newNodeEdgeList2){
                                        for (Edge usedEdge : usedEdges){
                                            if (edge == usedEdge){
                                                //Loop through all the previously used edges to try to find a parent Node
                                                Vector<Integer> nodeId = neighbors2.get(edge.getWeight());

                                                for (int y = 0; y < v_Nodes2.size() - 1;y++){
                                                    if (v_Nodes2.get(y).getID() == nodeId.lastElement()){
                                                        //This node is connected to one of the parent nodes we found, store this for later
                                                        connectedNodes2.add(v_Nodes2.get(y));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                
                                
                                //At this point we have a list of all of the Nodes connected to newNode, AND all the nodes connected to those nodes
                                //Check that list and see if any of them are the same node an the original n
                                //If it is == then it's a cycle, so we ignore adding the edge to the tree
                                for (Node node : connectedNodes2){
                                    if (node.getID() == n.getID()){
                                        nIsConnected = true;
                                    }
                                    else if (node.getID() == newNode.getID()){
                                        newIsConnected = true;
                                    }
                                }
                            }
                            //All the work has been completed, finally, check if we have a valid edge to add to the tree
                            if (nIsConnected && newIsConnected){
                                //This is a cycle, don't add the edge to the tree
                                workingNodeLoc.removeConnection(v_Nodes.get(v_Nodes.size() - 1).getID(), n.getID());
			
				v_Nodes.remove(v_Nodes.size() - 1);
                                
                            }
                            else{
                                usedEdges.add(minimum);
                                canditate.addConnection(v_Nodes.get(v_Nodes.size() - 1).getID(), n.getID(), minimum.getWeight());
                                //Found the minimum edge to connect, add it to the MST

				
				//Remove from working NodeLoc so we don't hit it in random phase
				workingNodeLoc.removeConnection(v_Nodes.get(v_Nodes.size() - 1).getID(), n.getID());
			
				v_Nodes.remove(v_Nodes.size() - 1);
                            }
                            
										
			}
		}
		
		v_Nodes = new Vector<Node>(m_graphSeed.getHeaderNodesMap().values());
		
		v_Nodes = new Vector<Node>(m_graphSeed.getHeaderNodesMap().values());
		int MAX_MST_CONNECTIONS = GraphConstants.NUM_NODES - 1;//using this to create a MST
		
                if(canditate.getTotalEdges() != MAX_MST_CONNECTIONS)
			throw new Exception("Total edges "+canditate.getTotalEdges()+ " does not equal "+MAX_MST_CONNECTIONS);		

	}
	
	public void doWork() throws Exception
	{
		try
		{
			for( seed=0; seed < GraphConstants.MAX_SEEDS; seed++)
			{
				
				generateSeed();

				//Now to make some graphs to be used for normalizing the cost
				calculateCostBasis();

				//nodeLoc.printWithLocationAndWeights();

				for(candidate=0; candidate < 1; candidate++)
				{
					//canditate.printWithLocationAndWeights();
					
					//connnectCandidate();//Removed for HW3 MST
                                        connnectMSTCandidate();//new HW3 method
					measureAndOutputCandidate();					  
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
	}
	
	private void measureAndOutputCandidate() throws Exception
	{

		CalculatedGraph calculatedCanditate = new CalculatedGraph(canditate); 
		calculatedCanditate.setCostBasis(m_basisCost);
	//	calculatedCanditate.UpdateCalcuations();
		calculatedCanditate.UpdatePSOCalculations();
		
		System.out.println("------------------Begin Measurements-----------");
		System.out.println("Avg Robustness Measure for Random - Percentage in LCC "+calculatedCanditate.getRandomLCC());
		System.out.println("Avg Robustness Measure for Random - Diameter in LCC "+calculatedCanditate.getRandomDiameter());
		System.out.println("Avg Robustness Measure for Directed - Percentage in LCC "+calculatedCanditate.getDirectLCC());
		System.out.println("Avg Robustness Measure for Directed - Diameter in LCC "+calculatedCanditate.getDirectDiameter());
		System.out.println("Connectivity Measure - AISPL "+ calculatedCanditate.getAISPL());
		System.out.println("Cost Measure - summation weight costs "+ calculatedCanditate.getCost());	
		System.out.println("Cost Basis -                      "+ calculatedCanditate.getCostBasis());
	    double t = calculatedCanditate.getCost() / calculatedCanditate.getCostBasis();
		System.out.println("Cost Basis ratio - "+t );
		System.out.println("Fitness Value -  "+calculatedCanditate.getFitnessValue());
		System.out.println("Diameter Value -  "+calculatedCanditate.getDiameter());
		System.out.println("ClusteringCoefficient - "+calculatedCanditate.getClusteringCoefficient());
		System.out.println("Per LCC -  "+calculatedCanditate.getLCC());

		System.out.println("------------------End Measurements-------------");
		calculatedCanditate.printWithLocationAndWeights();

		Util.streamOutCalculatedGraph(m_GraphDirectory, candidate, calculatedCanditate);	

	}
	

	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{
		
		try
		{
			CreateGraph createGraph = new CreateGraph();
			createGraph.doWork();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
	}

}
