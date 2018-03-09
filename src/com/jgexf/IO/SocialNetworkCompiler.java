package com.jgexf.IO;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.Map;

import com.jgexf.gexf.GraphEdge;
import com.jgexf.gexf.GraphAttributeList;
import com.jgexf.gexf.GraphData;

/**
 * A class for working with a variety of social network sources.
 * Classes that subclass the social network compiler must be able to read, parse, and convert 
 * data from a specified social network into the internal graph format.
 * 
 * @author bskarin
 * 
 * --------------------------
 * Created 3/1/2018
 * ---------------------------
 *
 *
 */
public abstract class SocialNetworkCompiler {
	
	
	/**
	 * The interface through which to access the graph
	 */
	protected NetworkInterface networkInterface;
	
	/**
	 * The file for writing the graph to
	 */
	public String outputFileName;
	
	/**
	 * The attributes for the node of the graph
	 * 
	 */
	protected GraphAttributeList nodeAttributes;
	
	/**
	 * The attributes for the edge of the graph
	 * 
	 */
	protected GraphAttributeList edgeAttributes;
	
	/**
	 * Calls the social network specific initializer
	 */
	public abstract void init();
	
	/**
	 * @return the outputFileName
	 */
	public String getOutputFileName() {
		return outputFileName;
	}

	/**
	 * @param outputFileName the outputFileName to set
	 */
	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}

	/**
	 * The constructor will initialize a network interface instance, then initialize itself
	 */
	public SocialNetworkCompiler(){
		
		networkInterface = new NetworkInterface();
		init();
	}
	
	/**
	 * Build the internal network by from a text file matching a specified template
	 * @param filePath The path to the social network file
	 */
	public abstract void buildGraphFromFile(String filePath);
	
	
	/**
	 * Retrieves a social network specific parameter file
	 * @return The input stream for the parameter file
	 */
	public abstract String getParamFile();
	
	/**
	 * Will construct and initialize a new graph via a network's local parameter file
	 */
	public void buildGraph() {
		
	
		networkInterface.initGraph(getParamFile());

		nodeAttributes = networkInterface.requestAttributesByClass(GraphAttributeList.NODE);

		edgeAttributes = networkInterface.requestAttributesByClass(GraphAttributeList.EDGE);
	}
	
	/**
	 * Checks for any self-looping edges and username duplicates,
	 * then writes to file.
	 */
	public void processAndWriteGraph(boolean json, String outputFile) 
	{
		setOutputFileName(outputFile);
		GraphData graph = networkInterface.getGraph();
		Map<String, GraphEdge> graphEdges = graph.getEdges();
		
		Iterator<Map.Entry<String, GraphEdge>> edgeIter = graphEdges.entrySet().iterator();
		while(edgeIter.hasNext()) 
		{
			Map.Entry<String, GraphEdge> pair = edgeIter.next();
			GraphEdge graphEdge = pair.getValue();
			String source = graphEdge.getSource(); String target = graphEdge.getTarget();
			if(source.equals(target)) 
			{
				edgeIter.remove();
			}
		}
		graph.setEdges(graphEdges);
				
		writeGraph(graph, json, outputFileName);
		
		System.out.println("Finished writing changes");
	}
	
	
	/**
	 * Write {@link GraphData} to file
	 */
	public void writeGraph(GraphData graph, boolean writeJson, String outGraph)
	{
		setOutputFileName(outGraph);
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(
					new OutputStreamWriter(
							new FileOutputStream(outputFileName + ".gexf"),"UTF8"));
			graph.toXML(out);
			out.close();

			if(writeJson){
				BufferedWriter outJson = new BufferedWriter(
						new OutputStreamWriter(
								new FileOutputStream(outputFileName + ".json"),"UTF8"));
			
				graph.toJSON(outJson);

				outJson.close();
			}
				
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(out != null)
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	

	
	
}
