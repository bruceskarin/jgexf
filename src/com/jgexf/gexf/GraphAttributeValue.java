/**
 * 	Author: Bruce Skarin
 *  Nov 25, 2014
 */
package com.jgexf.gexf;

import com.jgexf.gexf.util.GraphWriter;

/**
 * Object for storing the instance of a graph attribute.
 *  
 * @author bskarin
 *
 */

public class GraphAttributeValue extends GraphTime{
	private String id;
	private String value;
	
	private GraphAttribute graphAttribute; //The parent attribute for the value
	
	/**
	 * Basic Graph Attribute Value constructor
	 */
	public GraphAttributeValue() {
		setStart("");
		setEnd("");
	}
	/**
	 * 
	 * @param id
	 * @param value
	 */
	public GraphAttributeValue(String id, String value) {
		this();
		this.id = id;
		this.value = value;
	}
	/**
	 * 
	 * @param id
	 * @param value
	 * @param start
	 */
	public GraphAttributeValue(String id, String value, String start, String format) {
		this(id, value);
		setStart(start);
		setFormat(format);
	}
	/**
	 * 
	 * @param id
	 * @param value
	 * @param start
	 * @param end
	 */
	public GraphAttributeValue(String id, String value, String start, String end, String format) {
		this(id, value, start, format);
		setEnd(end);
	}
	
	@Override
	public String toString() {
		return "(id=" + id + ", title=" + graphAttribute.getTitle() + ", value=" + value + ")";
	}
	
	public String toXML(){
		StringBuilder buddy = new StringBuilder();
		buddy.append(new String("          <attvalue for=\""));
		buddy.append(id);
		buddy.append("\" value=\"");
		String cleanValue = value;
		if (graphAttribute.getType().equals("string")) {
			cleanValue = GraphWriter.cleanText(value);
		}
		buddy.append(cleanValue);
		buddy.append("\"");
		if(!getStart().isEmpty()){
			buddy.append(" start=\"");
			buddy.append(getStartXML());
			buddy.append("\"");
		}
		if(!getEnd().isEmpty()){
			buddy.append(" end=\"");
			buddy.append(getEndXML());
			buddy.append("\"");
		}
		buddy.append("></attvalue>\n");
		return buddy.toString();
	}
	
	
	public String toXML(int depth){
		
		String tab_spacing = "";
		for (int i=0;i<depth;i++) {
			tab_spacing +="\t";
		}
		
		StringBuilder buddy = new StringBuilder();
		buddy.append(new String(tab_spacing+"<attvalue for=\""));
		buddy.append(id);
		buddy.append("\" value=\"");
		String cleanValue = value;
		if (graphAttribute.getType().equals("string")) {
			cleanValue = GraphWriter.cleanText(value);
		}
		buddy.append(cleanValue);
		buddy.append("\"");
		if(!getStart().isEmpty()){
			buddy.append(" start=\"");
			buddy.append(getStartXML());
			buddy.append("\"");
		}
		if(!getEnd().isEmpty()){
			buddy.append(" end=\"");
			buddy.append(getEndXML());
			buddy.append("\"");
		}
		buddy.append("></attvalue>\n");
		return buddy.toString();
	}
	
	public String toJSON(){
		StringBuilder buddy = new StringBuilder();
		buddy.append(new String("\""));
		buddy.append(graphAttribute.getTitle());
		buddy.append("\":\"");
		String cleanValue = value;
		if (graphAttribute.getType().equals("string")) {
			cleanValue = GraphWriter.cleanText(value);
		} 
		buddy.append(cleanValue);
		buddy.append("\"");

		return buddy.toString();
	}
	
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * 
	 * @return the value as a double
	 */
	public double getValueAsDouble(){
		double val = 0;
		try{
			val = Double.parseDouble(value);
		}
		catch (NumberFormatException e){
			e.printStackTrace();
		}
		return val;
	}
	
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the graphAttribute
	 */
	public GraphAttribute getGraphAttribute() {
		return graphAttribute;
	}

	/**
	 * @param graphAttribute the graphAttribute to set
	 */
	public void setGraphAttribute(GraphAttribute graphAttribute) {
		this.graphAttribute = graphAttribute;
	}
}