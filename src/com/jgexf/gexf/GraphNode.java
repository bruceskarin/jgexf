package com.jgexf.gexf;

import java.awt.Color;

import com.jgexf.gexf.util.GraphWriter;

/**
 * Represents graph node within {@link GraphData}.
 * @author bskarin
 * 
 */
public class GraphNode extends GraphElement {

	private String label;
	private double xPos;
	private double yPos;
	private double size;
	private Color color;
	
	public GraphNode(){
		super();
	}
	
	public GraphNode(String id, String label) {
		this();
		setId(id);
		this.label = label;
	}
	
	/**
	 * Constructor to build {@link GraphNode} with id, label, and {@link GraphTime} stuff.
	 * @param id node ID
	 * @param label node label
	 * @param start start time
	 * @param end end time
	 * @param format time format (date or double)
	 */
	public GraphNode(String id, String label, String start, String end, String format){
		super(id, start, end, format);
		this.label = label;
	}
	
	@Override
	public String toString() {
		return "(id=" + getId() + ", label=" + getLabel() + ")";
	}
	
	/**
	 * Convert {@link GraphNode} to XML representation.
	 * @return XML String
	 */
	public String toXML(){
		StringBuilder buddy = new StringBuilder();
		buddy.append("      <node id=\"");
		buddy.append(GraphWriter.cleanText(getId()));
		buddy.append("\" label=\"");
		buddy.append(GraphWriter.cleanText(label));
		buddy.append("\"");
		buddy.append(super.toXML()); // GraphTime attributes
		buddy.append(">\n");
		if(!getSpells().isEmpty()){
			buddy.append(getSpells().toXML());
		}
		if(!getAttributes().isEmpty()){
			buddy.append(getAttributes().toXML());
		}
		buddy.append("      </node>\n");
		return buddy.toString();
	}

	/**
	 * Convert {@link GraphNode} to XML representation.
	 * @param depth how nested the node is
	 * @return @return XML String
	 */
	public String toXML(int depth){
		
		String tab_spacing = "";
		for (int i=0;i<depth;i++) {
			tab_spacing +="\t";
		}
		
		StringBuilder buddy = new StringBuilder();
		buddy.append(tab_spacing+"<node id=\"");
		buddy.append(getId());
		buddy.append("\" label=\"");
		buddy.append(label);
		buddy.append("\"");
		buddy.append(super.toXML()); // GraphTime attributes
		buddy.append(">\n");
		if(!getSpells().isEmpty()){
			buddy.append(getSpells().toXML(depth+1));
		}
		if(!getAttributes().isEmpty()){
			buddy.append(getAttributes().toXML(depth+1));
		}
		buddy.append(tab_spacing+"</node>\n");
		return buddy.toString();
	}
	
	public String toJSON() {

		//TODO: Compute size based on degrees (in/out)
		if (getSize()==0) {
			setSize(10);
		}
		
		StringBuilder buddy = new StringBuilder();
		buddy.append("{\"id\":\"");
		buddy.append(getId());
		buddy.append("\", \"label\":\"");
		buddy.append(label);
		buddy.append("\",");
		buddy.append("\"size\":");
		buddy.append(getSize()+",");
		//date time may not be supported
		buddy.append(super.toJSON()); // GraphTime attributes
		buddy.append(",");
		
		//Spells likely not supported?
		/*if(!getSpells().isEmpty()){
			buddy.append(getSpells().toJSON());
		}*/

		if(!getAttributes().isEmpty()){
			buddy.append(getAttributes().toJSON());
		}
		
		buddy.append("}");
		return buddy.toString();
	}
	
	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
	/**
	 * @param label the node label (usually identical to id)
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	
	/**
	 * @return the xPos
	 */
	public double getxPos() {
		return xPos;
	}

	/**
	 * @param xPos the xPos to set
	 */
	public void setxPos(double xPos) {
		this.xPos = xPos;
	}

	/**
	 * @return the yPos
	 */
	public double getyPos() {
		return yPos;
	}

	/**
	 * @param yPos the yPos to set
	 */
	public void setyPos(double yPos) {
		this.yPos = yPos;
	}

	/**
	 * @return the size
	 */
	public double getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(double size) {
		this.size = size;
	}

	/**
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(Color color) {
		this.color = color;
	}
	
	public boolean equals(Object n2) {
		return hashCode() == n2.hashCode();
	}


	
}
