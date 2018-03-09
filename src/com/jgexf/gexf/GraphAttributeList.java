/**
 * 	Author: Bruce Skarin
 *  Nov 25, 2014
 */
package com.jgexf.gexf;

import java.util.ArrayList;

/**
 * 
 * The list object for node and edge attributes.
 * 
 * @author bskarin
 *
 */
public class GraphAttributeList extends ArrayList<GraphAttribute>{

	private static final long serialVersionUID = 7861489609367186110L;
	
	private String attributeClass; //node, edge
	public static final String NODE = "node";
	public static final String EDGE = "edge"; 
	
	private String mode; //static, dynamic
	public static String STATIC = "static";
	public static String DYNAMIC = "dynamic";
	
	public GraphAttributeList() {
		
	}
	public GraphAttributeList(String attributeClass, String mode){
		this.attributeClass = attributeClass;
		this.mode = mode;
	}
	
	public GraphAttribute getAttributeByID(String id){
		for(GraphAttribute ga : this){
			if(ga.getId().equals(id)){
				return ga;
			}
		}
		return null;
	}
	/**
	 * 
	 * @param title of attribute to retrieve
	 * @return the GraphAttribute with the matching title
	 */
	public GraphAttribute getAttributeByTitle(String title){
		for(GraphAttribute ga : this){
			if(ga.getTitle().equals(title)){
				return ga;
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @return the XML for the attribute list
	 */
	public String toXML(){
		StringBuilder buddy = new StringBuilder();
		buddy.append("    <attributes class=\"");
		buddy.append(attributeClass);
		buddy.append("\" mode=\"");
		buddy.append(mode);
		buddy.append("\">\n");
		for(GraphAttribute ga : this){
			buddy.append(ga.toXML());
		} 
		buddy.append("    </attributes>\n");
		return buddy.toString();
	}
	
	/**
	 * @return the attributeClass
	 */
	public String getAttributeClass() {
		return attributeClass;
	}
	/**
	 * @param attributeClass the attributeClass to set
	 */
	public void setAttributeClass(String attributeClass) {
		this.attributeClass = attributeClass;
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

}
