package com.jgexf.gexf.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Calendar;
import java.util.Date;
import java.util.Stack;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.jgexf.gexf.GraphAttribute;
import com.jgexf.gexf.GraphAttributeList;
import com.jgexf.gexf.GraphAttributeValue;
import com.jgexf.gexf.GraphAttributeValueList;
import com.jgexf.gexf.GraphData;
import com.jgexf.gexf.GraphEdge;
import com.jgexf.gexf.GraphElement;
import com.jgexf.gexf.GraphNode;
import com.jgexf.gexf.GraphSpell;
import com.jgexf.gexf.GraphSpellList;

/**
 * Parse network stored as .GEXF file and convert to {@link GraphData}.
 * 
 * @author bskarin
 * 
 */
public class GraphParser {
	
	private static Date startDate;
	
	public GraphParser() {
		setStartDate(Calendar.getInstance().getTime());
	}
	
	/**
	 * @return the startDate
	 */
	public static Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public static void setStartDate(Date startDate) {
		GraphParser.startDate = startDate;
	}

	/**
	 * Extract network generation parameters from {@link GraphData} and write to parameters file.
	 * @param graphData
	 * @param filePrefix
	 */
	public static void writeParams(GraphData graphData, String filePrefix) {
		graphData = extractAttributes(graphData);
		String fileName = filePrefix + "_parameters.gexf";
		writeGraph(graphData, fileName);
	}
	
	/**
	 * Writes {@link GraphData} to specified filename. I only wrote this b/c of the {@link BufferedWriter}/{@link FileWriter} mess.
	 * @param graphData
	 * @param fileName
	 */
	public static void writeGraph(GraphData graphData, String fileName) {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(fileName));
			graphData.toXML(out);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Generate graph without nodes and edges to write as attribute file. 
	 * @param graphData
	 * @return newGraph
	 */
	public static GraphData extractAttributes(GraphData graphData) {
		GraphData newGraph = new GraphData();
		newGraph.setDefaultEdgeType(graphData.getDefaultEdgeType());
		newGraph.setMode(graphData.getMode());
		newGraph.setTimeFormat(graphData.getTimeFormat());
		newGraph.setAttributeLists(graphData.getAttributeLists());
		return newGraph;
	}
	
	
	
	/**
	 * Convert GEXF to {@link GraphData} with streaming reader to save memory/runtime.
	 * <p></p>
	 * Includes parameterization of {@link GraphAttribute}s.
	 * @param filename name of GEXF file
	 * @return network
	 */
	public static GraphData readGEXF(String filename) {
		FileInputStream fis = null;
		GraphData graphData = null;
		try 
		{
			fis = new FileInputStream(new File(filename));
			graphData = readGEXF(fis);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} 
		catch (OutOfMemoryError e)
		{
			e.printStackTrace();
		}
		finally {
			if(fis != null)
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		// finishing touch = update GraphAttribute ranges
		return graphData;
	}

	/**
	 * Convert GEXF to {@link GraphData} with streaming reader to save memory/runtime.
	 * <p>
	 * Include max node/edge parameters to avoid memory errors.
	 * </p>
	 * Includes parameterization of {@link GraphAttribute}s.
	 * @param filename full path to input file
	 * @param maxNodes maximum allowed number of nodes in {@link GraphData}
	 * @param maxEdges maximum allowed number of edges in {@link GraphData}
	 * @return network
	 */
	public static GraphData readGEXF(String filename, int maxNodes, int maxEdges) {
		GraphData graphData = null;
		Stack<GraphAttributeList> graphAttributeListStack = new Stack<GraphAttributeList>();
		Stack<GraphAttributeValueList> graphAttributeValueListStack = new Stack<GraphAttributeValueList>();
		Stack<GraphElement> elementStack = new Stack<GraphElement>();
		Stack<GraphSpell> spellStack = new Stack<GraphSpell>();
		boolean dynamicGraph = false;
		FileInputStream fis = null;
		int nodeCount = 0; 
		int edgeCount = 0;
		try {
			fis = new FileInputStream(new File(filename));
			XMLStreamReader reader = XMLInputFactory.newInstance().
					createXMLStreamReader(fis);
			int ctr = 1;
			String timeFormat = "";
			while(reader.hasNext() && nodeCount < maxNodes && edgeCount < maxEdges) {
				int event = reader.next();
				//String namespaceURI = reader.getNamespaceURI();
				switch(event) {
				case XMLStreamConstants.START_DOCUMENT:
					break;
				case XMLStreamConstants.START_ELEMENT:
					String elementName = reader.getLocalName();
					switch(elementName){
					case "graph":
						String edgeType = getAttr(reader,"defaultedgetype");
						timeFormat = getAttr(reader,"timeformat");
						String graphMode = getAttr(reader,"mode");
						String startTime = "";
						if(graphMode.equals("dynamic")) {
							dynamicGraph = true;
							startTime = getAttr(reader,"starttime");
						}
						graphData = new GraphData(edgeType, timeFormat, graphMode);
						graphData.setStartTime(startTime);
						break;
					case "attributes":
						String elementClass = getAttr(reader,"class");
						String elementMode = getAttr(reader,"mode");
						graphAttributeListStack.push(new GraphAttributeList(elementClass, elementMode));
						break;
					case "attribute":
						String id = getAttr(reader,"id");
						String title = getAttr(reader,"title");
						String type = getAttr(reader,"type");
						GraphAttribute graphAttribute = new GraphAttribute(id, title, type);
						graphAttributeListStack.peek().add(graphAttribute);
						break;
					case "nodes":
						break; 
					case "node":
						String nodeID = getAttr(reader,"id");
						String nodeLabel = getAttr(reader,"label");
						GraphNode graphNode = new GraphNode(nodeID, nodeLabel);
						if(dynamicGraph) {
							String start = dateHandler(reader,"start");
							String end = dateHandler(reader,"end");
							graphNode.setStart(start); graphNode.setEnd(end);
							graphNode.setFormat(timeFormat);
						}
						elementStack.push(graphNode);
						break;
					case "edges":
						break;
					case "edge":
						String source = getAttr(reader, "source");
						String target = getAttr(reader,"target");
						String weight = getAttr(reader,"weight");
						String edgeID = getAttr(reader,"id");
						if(edgeID == null) // all edges must have an ID of some kind; this quick fix doesn't allow multigraphs
							edgeID = source + "||" + target;
						GraphEdge graphEdge = new GraphEdge(edgeID, source, target);
						graphEdge.setWeight(weight);
						if(dynamicGraph) {
							String start = dateHandler(reader,"start");
							String end = dateHandler(reader,"end");
							graphEdge.setStart(start); graphEdge.setEnd(end);
							graphEdge.setFormat(timeFormat);
						}
						elementStack.push(graphEdge);
						break;
					case "attvalues":
						graphAttributeValueListStack.push(new GraphAttributeValueList());
						break;
					case "attvalue":
						String attID = getAttr(reader,"for");
						String attValue = getAttr(reader,"value");
						GraphAttributeValue gav = new GraphAttributeValue(attID, attValue);
						String attributeClass = (elementStack.peek() instanceof GraphNode ? "node" : "edge");
						GraphAttributeList gal = graphData.getGraphAttributeListByClass(attributeClass);
						GraphAttribute ga1 = gal.getAttributeByID(attID);
						gav.setGraphAttribute(ga1);
						if(dynamicGraph) {
							String start = dateHandler(reader,"start");
							String end = dateHandler(reader,"end");
							if(end == null)
								end = "";
							gav.setStart(start); gav.setEnd(end);
							gav.setFormat(timeFormat);
						}
						graphAttributeValueListStack.peek().add(gav);
						break;
					case "spells":
						break;
					case "spell":
						String spellStart = dateHandler(reader,"start");
						String spellEnd = dateHandler(reader,"end");
						GraphSpell spell = new GraphSpell(spellStart, spellEnd, graphData.getTimeFormat());
						spellStack.push(spell);
						break;
					default:
						break;
					}
					break;
				case XMLStreamConstants.END_ELEMENT:
					elementName = reader.getLocalName();
					switch(elementName){
					case "modelparameters":
						break;
					case "modelparameter":
						break;
					case "attributeparameters":
						break;
					case "attributeparameter":
						break;
					case "attributes": // dump attribute list to GraphData and get ready for next round of attributes
						graphData.getAttributeLists().add(graphAttributeListStack.pop());
						break;
					case "node": 
						graphData.addNode((GraphNode)elementStack.pop());
						nodeCount++;
						break;
					case "nodes":
						break;
					case "edge":
						graphData.addEdge((GraphEdge)elementStack.pop());
						edgeCount++;
						break;
					case "edges":
						break;
					case "attvalue":
						break;
					case "attvalues":
					GraphElement graphElement = elementStack.peek();
					GraphAttributeValueList currValues = graphAttributeValueListStack.pop();
					GraphAttributeValueList gavl = graphElement.getAttributes();
					// add one by one to handle time/value conflicts
					for(GraphAttributeValue currGav : currValues)
						gavl.addGraphAttribute(currGav);
					graphElement.setAttributes(gavl); 
						break;
					case "spell":
						GraphElement currElem = elementStack.peek();
						GraphSpellList spellList = currElem.getSpells(); // hack to allow correct spell adding
						spellList.addOrAppendSpell(spellStack.pop());
						currElem.setSpells(spellList);
						break;
					case "spells":
						break;
					default:
						break;
					}
					break;
				default:
					break;
				}
				if(ctr % 1000000 == 0)
					System.out.println(ctr + " events processed.");
				ctr++;
			}
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		finally {
			if(fis != null)
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return graphData;
	}

	/**
	 * Read {@link GraphData} network from an input stream.
	 * @param is input stream
	 * @return network
	 */
	public static GraphData readGEXF(InputStream is) 
	{
		GraphData graphData = null;
		try {
			XMLStreamReader reader = XMLInputFactory.newInstance().
					createXMLStreamReader(is);
			graphData = readGEXF(reader);
		} 
		catch (FactoryConfigurationError e) 
		{
			e.printStackTrace();
		} 
		catch (XMLStreamException e) 
		{
			e.printStackTrace();
		}
		return graphData;
	}

	/**
	 * Read {@link GraphData} network from a character reader.
	 * @param charReader character reader
	 * @return network
	 */
	public static GraphData readGEXF(Reader charReader) 
	{
		GraphData graphData = null;
		try 
		{
			XMLStreamReader reader = XMLInputFactory.newInstance().
					createXMLStreamReader(charReader);
			graphData = readGEXF(reader);
		} 
		catch (FactoryConfigurationError e) 
		{
			e.printStackTrace();
		} 
		catch (XMLStreamException e) 
		{
			e.printStackTrace();
		}
		return graphData;
	}
	
	/**
	 * Read network from an initialized XML reader.
	 * @param reader initialized XML reader.
	 * @return network
	 */
	private static GraphData readGEXF(XMLStreamReader reader) 
	{
		GraphData graphData = null;
		Stack<GraphAttributeList> graphAttributeListStack = 
				new Stack<GraphAttributeList>();
		Stack<GraphAttributeValueList> graphAttributeValueListStack = 
				new Stack<GraphAttributeValueList>();
		Stack<GraphElement> elementStack = new Stack<GraphElement>();
		Stack<GraphSpell> spellStack = new Stack<GraphSpell>();
		boolean dynamicGraph = false;
		int ctr = 1;
		String timeFormat = "";
		try {
			while(reader.hasNext()) 
			{
				int event = reader.next();
				//String namespaceURI = reader.getNamespaceURI();
				switch(event) 
				{
				case XMLStreamConstants.START_DOCUMENT:
					break;
				case XMLStreamConstants.START_ELEMENT:
					String elementName = reader.getLocalName();
					switch(elementName)
					{
					case "graph":
						String edgeType = getAttr(reader,"defaultedgetype");
						timeFormat = getAttr(reader,"timeformat");
						String graphMode = getAttr(reader,"mode");
						String startTime = "";
						if(graphMode.equals("dynamic")) 
						{
							dynamicGraph = true;
							startTime = getAttr(reader,"starttime");
						}
						graphData = new GraphData(edgeType, timeFormat, graphMode);
						graphData.setStartTime(startTime);
						break;
					case "attributes":
						String elementClass = getAttr(reader,"class");
						String elementMode = getAttr(reader,"mode");
						graphAttributeListStack.push(new GraphAttributeList(elementClass, elementMode));
						break;
					case "attribute":
						String id = getAttr(reader,"id");
						String title = getAttr(reader,"title");
						String type = getAttr(reader,"type");
						GraphAttribute graphAttribute = new GraphAttribute(id, title, type);
						graphAttributeListStack.peek().add(graphAttribute);
						break;
					case "nodes":
						break; 
					case "node":
						String nodeID = getAttr(reader,"id");
						String nodeLabel = getAttr(reader,"label");
						GraphNode graphNode = new GraphNode(nodeID, nodeLabel);
						if(dynamicGraph) {
							String start = dateHandler(reader,"start");
							String end = dateHandler(reader,"end");
							graphNode.setStart(start); graphNode.setEnd(end);
							graphNode.setFormat(timeFormat);
						}
						elementStack.push(graphNode);
						break;
					case "edges":
						break;
					case "edge":
						String source = getAttr(reader, "source");
						String target = getAttr(reader,"target");
						String weight = getAttr(reader,"weight");
						String edgeID = getAttr(reader,"id");
						if(edgeID == null) // all edges must have an ID of some kind; this quick fix doesn't allow multigraphs
							edgeID = source + "||" + target;
						GraphEdge graphEdge = new GraphEdge(edgeID, source, target);
						graphEdge.setWeight(weight);
						if(dynamicGraph) {
							String start = dateHandler(reader,"start");
							String end = dateHandler(reader,"end");
							graphEdge.setStart(start); graphEdge.setEnd(end);
							graphEdge.setFormat(timeFormat);
						}
						elementStack.push(graphEdge);
						break;
					case "attvalues":
						graphAttributeValueListStack.push(new GraphAttributeValueList());
						break;
					case "attvalue":
						String attID = getAttr(reader,"for");
						String attValue = getAttr(reader,"value");
						GraphAttributeValue gav = new GraphAttributeValue(attID, attValue);
						String attributeClass = (elementStack.peek() instanceof GraphNode ? "node" : "edge");
						GraphAttributeList gal = graphData.getGraphAttributeListByClass(attributeClass);
						GraphAttribute ga1 = gal.getAttributeByID(attID);
						gav.setGraphAttribute(ga1);
						if(dynamicGraph) {
							String start = dateHandler(reader,"start");
							String end = dateHandler(reader,"end");
							if(end == null)
								end = "";
							gav.setStart(start); gav.setEnd(end);
							gav.setFormat(timeFormat);
						}
						graphAttributeValueListStack.peek().add(gav);
						break;
					case "spells":
						break;
					case "spell":
						String spellStart = dateHandler(reader,"start");
						String spellEnd = dateHandler(reader,"end");
						GraphSpell spell = new GraphSpell(spellStart, spellEnd, graphData.getTimeFormat());
						spellStack.push(spell);
						break;
					default:
						break;
					}
					break;
				case XMLStreamConstants.END_ELEMENT:
					elementName = reader.getLocalName();
					switch(elementName){
					case "modelparameters":
						break;
					case "modelparameter":
						break;
					case "attributeparameters":
						break;
					case "attributeparameter":
						break;
					case "attributes": // dump attribute list to GraphData and get ready for next round of attributes
						graphData.getAttributeLists().add(graphAttributeListStack.pop());
						break;
					case "node": 
						graphData.addNode((GraphNode)elementStack.pop());
						break;
					case "nodes":
						break;
					case "edge":
						graphData.addEdge((GraphEdge)elementStack.pop());
						break;
					case "edges":
						break;
					case "attvalue":
						break;
					case "attvalues":
						GraphElement graphElement = elementStack.peek();
						GraphAttributeValueList currValues = graphAttributeValueListStack.pop();
						GraphAttributeValueList gavl = graphElement.getAttributes();
						// add one by one to handle time/value conflicts
						for(GraphAttributeValue currGav : currValues){
							gavl.addGraphAttribute(currGav);
						}
						graphElement.setAttributes(gavl); 
						break;
					case "spell":
						GraphElement currElem = elementStack.peek();
						GraphSpellList spellList = currElem.getSpells();
						spellList.addOrAppendSpell(spellStack.pop());
						currElem.setSpells(spellList);
						break;
					case "spells":
						break;
					default:
						break;
					}
					break;
				default:
					break;
				}
				if(ctr % 1000000 == 0)
				{
					System.out.println(ctr + " events processed.");
				}
				ctr++;
			}
		} 
		catch (XMLStreamException e) 
		{
			e.printStackTrace();
		}
		return graphData;
	}
	
	/**
	 * Generate {@link GraphData} with default parameters.gexf file.
	 * @return graphData
	 */
	public static GraphData readGEXF() {
		return readGEXF("parameters.gexf");
	}
	
	/**
	 * Generate parameters-only {@link GraphData} with default parameters.gexf file.
	 * @return network
	 */
	public static GraphData readGEXFParams() {
		return readGEXFParams("parameters.gexf");
	}
	
	/**
	 * Convert GEXF to {@link GraphData} containing only the 
	 * parameters for the model and for node/edge attributes.
	 * @param filename file name
	 * @return parameterized network
	 */
	public static GraphData readGEXFParams(String filename) {
		GraphData graphData = null;
		Stack<GraphAttributeList> graphAttributeListStack = new Stack<GraphAttributeList>();
		InputStream fis = null;
		try {
			fis = new FileInputStream(new File(filename));
			XMLStreamReader reader = XMLInputFactory.newInstance().
					createXMLStreamReader(fis);
			int ctr = 1;
			String timeformat = "";
			boolean finishedCollectingParams = false;
			while(reader.hasNext() && !finishedCollectingParams) {
				int event = reader.next();
				//String namespaceURI = reader.getNamespaceURI();
				switch(event) {
				case XMLStreamConstants.START_DOCUMENT:
					break;
				case XMLStreamConstants.START_ELEMENT:
					String elementName = reader.getLocalName();
					switch(elementName){
					case "graph":
						String edgeType = getAttr(reader,"defaultedgetype");
						timeformat = getAttr(reader,"timeformat");
						String graphMode = getAttr(reader,"mode");
						String startTime = "";
						if(graphMode.equals("dynamic")) {
							startTime = getAttr(reader,"starttime");
						}
						graphData = new GraphData(edgeType, timeformat, graphMode);
						graphData.setStartTime(startTime);
						break;
					case "attributes":
						String elementClass = getAttr(reader,"class");
						String elementMode = getAttr(reader,"mode");
						graphAttributeListStack.push(new GraphAttributeList(elementClass, elementMode));
						break;
					case "attribute":
						String id = getAttr(reader,"id");
						String title = getAttr(reader,"title");
						String type = getAttr(reader,"type");
						String datamode = getAttr(reader, "datamode");
						GraphAttribute graphAttribute = new GraphAttribute(id, title, type);
						graphAttribute.setDataMode(datamode);
						graphAttributeListStack.peek().add(graphAttribute);
						break;
					case "nodes":
						finishedCollectingParams = true; // short-circuit to finish reading file early
						break;
					}
					break;
				case XMLStreamConstants.END_ELEMENT:
					elementName = reader.getLocalName();
					switch(elementName){
					case "attributes": // dump attribute list to GraphData and get ready for next round of attributes
						graphData.getAttributeLists().add(graphAttributeListStack.pop());
						break;
					default:
						break;
					}
					break;
				default:
					break;
				}
				if(ctr % 1000000 == 0)
					System.out.println(ctr + " events processed.");
				ctr++;
			}
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}  finally {
			if(fis != null)
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return graphData;
	}
	
	/**
	 * Convert GEXF to {@link GraphData} containing only the structure (nodes and edges) to save memory.
	 * <p></p>
	 * Includes parameterization of {@link GraphAttribute}s.
	 * @param filename
	 * @return graphData
	 */
	public static GraphData readGEXFStructure(String filename) {
		GraphData graphData = null;
		Stack<GraphElement> elementStack = new Stack<GraphElement>();
		// prevents graphData.getMode().equals("dynamic")
		boolean dynamicGraph = false;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(new File(filename));
			XMLStreamReader reader = XMLInputFactory.newInstance().
					createXMLStreamReader(fis);
			int ctr = 1;
			String timeformat = "";
			while(reader.hasNext()) {
				int event = reader.next();
				//String namespaceURI = reader.getNamespaceURI();
				switch(event) {
				case XMLStreamConstants.START_DOCUMENT:
					break;
				case XMLStreamConstants.START_ELEMENT:
					String elementName = reader.getLocalName();
					switch(elementName){
					case "graph":
						String edgeType = getAttr(reader,"defaultedgetype");
						timeformat = getAttr(reader,"timeformat");
						String graphMode = getAttr(reader,"mode");
						if(graphMode.equals("dynamic"))
							dynamicGraph = true;
						graphData = new GraphData(edgeType, timeformat, graphMode);
						break;
					case "nodes":
						break; 
					case "node":
						String nodeID = getAttr(reader,"id");
						String nodeLabel = getAttr(reader,"label");
						GraphNode graphNode = new GraphNode(nodeID, nodeLabel);
						if(dynamicGraph) {
							String start = dateHandler(reader,"start");
							String end = dateHandler(reader,"end");
							graphNode.setStart(start); graphNode.setEnd(end);
							graphNode.setFormat(timeformat);
						}
						elementStack.push(graphNode);
						break;
					case "edges":
						break;
					case "edge":
						String source = getAttr(reader, "source");
						String target = getAttr(reader,"target");
						String weight = getAttr(reader,"weight");
						String edgeID = source + "||" + target;
						GraphEdge graphEdge = new GraphEdge(edgeID, source, target);
						graphEdge.setWeight(weight);
						if(dynamicGraph) {
							String start = dateHandler(reader,"start");
							String end = dateHandler(reader,"end");
							graphEdge.setStart(start); graphEdge.setEnd(end);
							graphEdge.setFormat(timeformat);
						}
						elementStack.push(graphEdge);
						break;
					default:
						break;
					}
					break;
				case XMLStreamConstants.END_ELEMENT:
					elementName = reader.getLocalName();
					switch(elementName){
					case "node": // dump current node
						graphData.addNode((GraphNode)elementStack.pop());
						break;
					case "nodes":
						break;
					case "edge":
						graphData.addEdge((GraphEdge)elementStack.pop());
						break;
					case "edges":
						break;
					default:
						break;
					}
					break;
				default:
					break;
				}
				if(ctr % 1000000 == 0)
					System.out.println(ctr + " events processed.");
				ctr++;
			}
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		finally {
			if(fis != null)
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return graphData;
	}
	
	/**
	 * Retrieve attribute value from current namespace.
	 * @param reader XML reader
	 * @param attributeName attribute name
	 * @return attribute value
	 */
	private static String getAttr(XMLStreamReader reader, String attributeName) {
		return reader.getAttributeValue(null, attributeName);
	}
	
	/**
	 * Generates map of attribute names to values, for all name/value pairs in current namespace.
	 * @param reader
	 * @return map
	 
	private static Map<String, String> getAttrMap(XMLStreamReader reader) {
		Map<String, String> map = new LinkedHashMap<String, String>();
		for(int i = 0; i < reader.getAttributeCount(); i++){
			String key = reader.getAttributeLocalName(i);
			if(!key.equalsIgnoreCase("name")){
				String value = reader.getAttributeValue(null, key);
				map.put(key, value);
			}
		}
		return map;
	}
	*///TODO check that it's not used and delete
	
	/**
	 * Looks for start/end values in current XML node, returns blank string if nonexistent.
	 * @param reader
	 * @param dateType
	 * @return date
	 */
	private static String dateHandler(XMLStreamReader reader, String dateType) {
		String date = getAttr(reader,dateType);
		if(date == null) {
			date = "";
		}
		return date;
	}
}