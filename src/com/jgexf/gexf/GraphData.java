/**
 * 	Author: Bruce Skarin
 *  Nov 25, 2014
 */

package com.jgexf.gexf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The root object for constructing gexf network graphs from
 * a variety of data sources.
 * 
 * @author bskarin
 *
 */

public class GraphData {
	private String defaultEdgeType;
	private String timeFormat;
	private String mode;
	private String startTime;
	private List<GraphAttributeList> attributeLists;
	private Map<String,GraphNode> nodes;
	private Map<String,GraphEdge> edges;
	
	public GraphData() {
		this.nodes = new LinkedHashMap<String,GraphNode>();
		this.edges = new LinkedHashMap<String,GraphEdge>();
		this.attributeLists = new ArrayList<GraphAttributeList>();
	}

	public GraphData(String defaultEdgeType, String mode) {
		this();
		this.attributeLists = new ArrayList<GraphAttributeList>();
		this.defaultEdgeType = defaultEdgeType;
		this.mode = mode;
	}
	
	public GraphData(String defaultEdgeType, String timeFormat, String mode) {
		this(defaultEdgeType, mode);
		this.timeFormat = timeFormat;
	}
	
	public GraphData(String defaultEdgeType, String timeFormat, String mode, String startTime) {
		this(defaultEdgeType, mode, timeFormat);
		this.startTime = startTime;
	}
	
	public GraphAttributeList getGraphAttributeListByClass(String attributeClass){
		for(GraphAttributeList gal : attributeLists){
			if(gal.getAttributeClass().equals(attributeClass)){
				return gal;
			}
		}
		return null;
	}
	
	/**
	 * Write output to the given Writer 
	 * @param out
	 * @throws IOException 
	 */
	public void toXML(Writer out) throws IOException {
		out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		out.write("<gexf xmlns=\"http://www.gexf.net/1.3\" version=\"1.3\" xmlns:viz=\"http://www.gexf.net/1.3/viz\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.gexf.net/1.3 http://www.gexf.net/1.3/gexf.xsd\">\n");
		out.write("<creator>jgexf</creator>\n");
		out.write("  <graph defaultedgetype=\"");
		out.write(defaultEdgeType);
		out.write("\" mode=\"");
		out.write(mode);
		out.write("\"");
		if(mode.equalsIgnoreCase("dynamic")){
			out.write(" timeformat=\"");
			out.write(timeFormat);
			out.write("\"" + " timerepresentation=\"interval\"");
			out.write(" starttime=\"");
			out.write(startTime);
			out.write("\"");
		}
		out.write(">\n");

		//write attributes
		for(GraphAttributeList ga : attributeLists){
			out.write(ga.toXML());
		}
		
		//write nodes
		out.write("    <nodes count=\"" + nodes.size() + "\">\n"); // including the node/edge count saves on parser runtime
		for(GraphNode gn : nodes.values()){
			out.write(gn.toXML());
		}
		out.write("    </nodes>\n");
		
		//write edges
		out.write("    <edges count=\"" + edges.size() + "\">\n");
		for(GraphEdge ge : edges.values()){
			out.write(ge.toXML());
		}
		out.write("    </edges>\n");
		out.write("  </graph>\n");
		out.write("</gexf>");
	}
	
	/**
	 * Write output directly to given Writer 
	 * @param out
	 * @throws IOException 
	 */
	public void toJSON(Writer out) throws IOException {
		out.write("{\"edges\":[");

		
		int edgeCounter = 0;
		for(GraphEdge ge : edges.values()){
			out.write(ge.toJSON());

			edgeCounter++;
			if (edgeCounter<edges.size()) {
				out.write(",");
			}
		}
		
		out.write("],\"nodes\":[");
		
		int nodeCounter = 0;
	
		for(GraphNode gn : nodes.values()){
				out.write(gn.toJSON());
				nodeCounter++;
				if (nodeCounter<nodes.size()) {
					out.write(",");
				}
		} 

		out.write("]}");
	}
	
	public boolean verifyTimeIntervals(){
		return verifyAllTimeIntervals();
	}
	
	/**
	 * Verify time intervals over all {@link GraphElement}s, their {@link GraphAttributeValue}s, and their {@link GraphSpell}s.
	 * @return
	 */
	private boolean verifyAllTimeIntervals() {
		List<GraphElement> graphElements = new ArrayList<GraphElement>();
		graphElements.addAll(nodes.values());
		graphElements.addAll(edges.values());
		for(GraphElement elem : graphElements) {
			if(!elem.checkInterval()){
				return false;
			}
			for(GraphAttributeValue gav : elem.getAttributes()){
				if(!gav.checkInterval()){
					return false;
				}
			}
			for(GraphSpell gs : elem.getSpells()){
				if(!gs.checkInterval()){
					return false;
				}
			}
		}
		return true;
	}
	
	public String getNodeAttributeID(String name){
		for(GraphAttribute ga : getAttributeLists().get(0)){
			if(ga.getTitle().equals(name)){
				return ga.getId();
			}
		}

		return "NA";
	}
	
	public String getEdgeAttributeID(String name){
		for(GraphAttribute ga : getAttributeLists().get(1)){
			if(ga.getTitle().equals(name)){
				return ga.getId();
			}
		}
		return "NA";
	}
	
	/**
	 * @return the defaultEdgeType
	 */
	public String getDefaultEdgeType() {
		return defaultEdgeType;
	}
	/**
	 * @param defaultEdgeType the defaultEdgeType to set
	 */
	public void setDefaultEdgeType(String defaultEdgeType) {
		this.defaultEdgeType = defaultEdgeType;
	}
	/**
	 * @return the timeFormat
	 */
	public String getTimeFormat() {
		return timeFormat;
	}
	/**
	 * @param timeFormat the timeFormat to set
	 */
	public void setTimeFormat(String timeFormat) {
		this.timeFormat = timeFormat;
	}
	/**
	 * @return the mode
	 */
	public String getMode() {
		return mode;
	}
	/**
	 * @param mode the mode to set
	 */
	public void setMode(String mode) {
		this.mode = mode;
	}
	
	/**
	 * @return startTime
	 */
	public String getStartTime() {
		return startTime;
	}
	
	/**
	 * @param startTime
	 */
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	
	/**
	 * @return the attributeLists
	 */
	public List<GraphAttributeList> getAttributeLists() {
		return attributeLists;
	}

	/**
	 * @param attributeLists the attributeLists to set
	 */
	public void setAttributeLists(List<GraphAttributeList> attributeLists) {
		this.attributeLists = attributeLists;
	}

	/**
	 * Read node/edge {@link GraphAttribute}s from params file and initialize {@link GraphAttributeList}.
	 * @param paramFile
	 */
	public void initAttributeLists(String paramFile) { 
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(new File(paramFile)));
			String line = "";
			GraphAttributeList nodeAttributes = new GraphAttributeList();
			nodeAttributes.setAttributeClass("node"); nodeAttributes.setMode("dynamic");
			GraphAttributeList edgeAttributes = new GraphAttributeList();
			edgeAttributes.setAttributeClass("edge"); edgeAttributes.setMode("dynamic");
			attributeLists.add(nodeAttributes); attributeLists.add(edgeAttributes);
			while(line != null) {
				String[] lineVals = line.split(",");
				String graphElementType = lineVals[0];
				switch(graphElementType) {
				case("graph"):
					defaultEdgeType = lineVals[1];
					mode = lineVals[2];
					timeFormat = lineVals[3];
				break;
				case("node"): case("edge"):
					String id = lineVals[1]; String title = lineVals[1];
					String type = lineVals[2];
					GraphAttribute newAttribute = new GraphAttribute(id, title, type);
					switch(graphElementType){
					case("node"):
						nodeAttributes.add(newAttribute);
					break;
					case("edge"):
						edgeAttributes.add(newAttribute);
					break;
					default:
						break;
					}
					break;
				default:
					break;
				}
				line = reader.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * @return number of nodes in graph
	 */
	public int getNodeCount() {
		return nodes.size();
	}
	
	/**
	 * @return name-node mapping in graph
	 */
	public LinkedHashMap<String,GraphNode> getNodes() {
		LinkedHashMap<String,GraphNode> temp = new LinkedHashMap<String,GraphNode>();
		temp.putAll(nodes);
		return temp;
	}
	public List<GraphNode> getNodeList() {
		List<GraphNode> temp = new ArrayList<GraphNode>(nodes.values());
		return temp;
	}

	/**
	 * @param nodes the name-node mapping to set
	 */
	public void setNodes(LinkedHashMap<String,GraphNode> nodes) {
		this.nodes = new LinkedHashMap<String,GraphNode>(nodes);
	}
	
	/**
	 * Access {@link GraphNode} by node's ID.
	 * @param nodeID numeric ID
	 * @return graphNode
	 */
	public GraphNode getNode(String nodeID) {
		return nodes.get(nodeID);
	}
	
	/**
	 * Add {@link GraphNode} to list.
	 * @param graphNode
	 */
	public void addNode(GraphNode graphNode) {
		String name = graphNode.getId();
		if(!nodes.containsKey(name))
			nodes.put(name, graphNode);
	}
	
	/**
	 * @return number of edges in graph
	 */
	public int getEdgeCount() {
		return edges.size();
	}
	
	/**
	 * Access {@link GraphEdge} by edge ID.
	 * @param edgeID
	 * @return graphEdge
	 */
	public GraphEdge getEdge(String edgeID) {
		return edges.get(edgeID);
	}
	
	/**
	 * @return the edges
	 */
	public Map<String,GraphEdge> getEdges() {
		Map<String,GraphEdge> temp = new LinkedHashMap<String,GraphEdge>();
		temp.putAll(edges);
		return temp;
	}
	
	public List<GraphEdge> getEdgeList() {
		List<GraphEdge> temp = new ArrayList<GraphEdge>();
		temp.addAll(edges.values());
		return temp;
	}
		
	/**
	 * @param edges the edges to set
	 */
	public void setEdges(Map<String,GraphEdge> edges) {
		this.edges = edges;
	}
	
	/**
	 * Add {@link GraphEdge} to graph.
	 * @param graphEdge
	 */
	public void addEdge(GraphEdge graphEdge) {
		String edgeId = graphEdge.getId();
		if(!edges.containsKey(edgeId))
			edges.put(edgeId,graphEdge);
	}

	/**
	 * Update existing instance of {@link GraphElement} or add if it doesn't exist.
	 * @param newElement
	 */
	public void addOrAppendElement(GraphElement newElement) {
		Map<String, ? extends GraphElement> collection = (newElement instanceof GraphNode ? nodes : edges);
		String elemId = newElement.getId();
		if(collection.containsKey(elemId)) {
			GraphElement oldElement = collection.get(elemId);
			//Modify spell or add new spell and all other new attributes
			GraphSpell gs = new GraphSpell(newElement.getStart(), newElement.getEnd(), getTimeFormat());
			oldElement.addSpell(gs);
			for(GraphAttributeValue gav : newElement.getAttributes()) {
				oldElement.addAttribute(gav);
			}
			//If element is an edge, update the weight and messages
			if(newElement instanceof GraphEdge) {
				GraphEdge oldEdge = (GraphEdge)oldElement;
				GraphEdge newEdge = (GraphEdge)newElement;
				oldEdge.updateWeight(newEdge.getWeight());
			}
		}
		else {
			if(newElement instanceof GraphNode)
				nodes.put(elemId, (GraphNode)newElement);
			else
				edges.put(elemId, (GraphEdge)newElement);
		}
	}

	/**
	 * Retrieve list of {@link GraphElement}s given the element class (node/edge).
	 * @param elementClass
	 * @return graphElements
	 */
	public List<? extends GraphElement> getGraphElements(String elementClass) {
		switch(elementClass){
		case("node"):
			return getNodeList();
		case("edge"):
			return getEdgeList();
		default:
			return null;
		}
	}
	
	/**
	 * Clear {@link GraphElement}s from specified node/edge mapping and return the resulting list.
	 * @param elementClass node/edge
	 * @return list of {@link GraphElement}s
	 */
	public List<? extends GraphElement> clearGraphElements(String elementClass) {
		List<GraphElement> graphElements = new ArrayList<GraphElement>();
		switch(elementClass) {
		case("node"):
			Iterator<Map.Entry<String, GraphNode>> graphNodeIter = nodes.entrySet().iterator();
		while(graphNodeIter.hasNext()) {
			graphElements.add(graphNodeIter.next().getValue());
			graphNodeIter.remove();
		}
			break;
		case("edge"):
			Iterator<Map.Entry<String, GraphEdge>> graphEdgeIter = edges.entrySet().iterator();
		while(graphEdgeIter.hasNext()) {
			graphElements.add(graphEdgeIter.next().getValue());
			graphEdgeIter.remove();
		}
			break;
		default:
			return null;
		}
		return graphElements;
	}
}
