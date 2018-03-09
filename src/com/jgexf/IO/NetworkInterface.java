package com.jgexf.IO;

import com.jgexf.gexf.GraphAttributeList;
import com.jgexf.gexf.GraphData;
import com.jgexf.gexf.GraphEdge;
import com.jgexf.gexf.GraphNode;
import com.jgexf.gexf.util.GraphParser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * The Network Interface serves to provide the network compilers
 * with a means for generating a {@link GraphData} object.
 * @author bskarin
 * 
 * --------------------------
 * Created 3/1/2018
 * ---------------------------
 *
 */
public final class NetworkInterface {

	/**
	 * The graph for the system
	 * 
	 */
	private GraphData graphData;


	
	/**
	 * This is a stop-gap due to measure the earliest message in the system
	 * This should be done at the graphData level
	 */
	private Date earliestDate;
	
	/**
	 * The date format required by Gephi for parsing GEXF graph files.
	 */
	public static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * Initializes a new graph
	 * @param graphID the look-up key for the graph
	 * @param paramFile the inputStream for the GEFX parameter file defining the graph
	 */
	public void initGraph(String paramFile){

		graphData = GraphParser.readGEXFParams(paramFile);
		graphData.setTimeFormat("datetime");
		
		updateGraphStartTime(Calendar.getInstance().getTime());

	
	}
	
	/**
	 * Getter for the  {@link GraphData} object
	 * @return A reference to the graph
	 */
	public GraphData getGraph() {
		 
		 return graphData;
	 }
	
	/**
	 * Updates the graph's start time to the given time
	 * @param newStart the new time the graph should startS
	 */
	private void updateGraphStartTime(Date newStart) {
		earliestDate = newStart;
		String startup_time = dateFormat.format(earliestDate);
		graphData.setStartTime(startup_time);
	}
	
	/**
	 * Add a node with a unique id to the graph
	 * If a node already exists, update its values when applicable
	 * @param graph
	 * @return true if the graph could be added, false if the graph is at the maximum size limit
	 */
	public boolean updateNode(GraphNode node) {
		
		//Check if the graph start time needs to be updated
		String nodeTimeString = node.getStart();
		Date nodeTime = null;
		try {
			nodeTime=dateFormat.parse(nodeTimeString);
			if (nodeTime!=null && nodeTime.before(earliestDate)) {
				
				updateGraphStartTime(nodeTime);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		graphData.addOrAppendElement(node);
		return true;
	}
	
	/**
	 * Getter for an existing node in the graph
	 * @param nodeID the unique id for the node requested
	 * @return requested GraphNode if it exists, otherwise NULL
	 */
	public GraphNode getNode(String nodeID) {
		
		return graphData.getNode(nodeID);
	}	

	/**
	 * Getter for an existing edge in the graph
	 * If an edge already exists, update its values when applicable
	 * @param edgeID the unique id for the node requested
	 * @return requested GraphEdge if it exists, otherwise NULL
	 */
	public GraphEdge getEdge(String edgeID) {
		
		return graphData.getEdge(edgeID);
	}	
	
	/**
	 * Add a node with a unique id to the graph
	 * If a node already exists, update its values when applicable
	 * @param graph
	 * @return requested GraphNode if it exists, otherwise NULL
	 */
	public GraphAttributeList requestAttributesByClass(String attributeClass) {
		
		return graphData.getGraphAttributeListByClass(attributeClass);
	}	
	
	/**
	 * Getter for the graph's string time format
	 * @return the timeFormat as a string
	 */
	public String getTimeFormat() {
		
		return graphData.getTimeFormat();
	}
	
	/**
	 * Add an edge between two nodes
	 * @param graph
	 * @return
	 */
	public boolean addEdge(GraphEdge edge) {	

		
		graphData.addOrAppendElement(edge);
		return true;
	}
	
}
