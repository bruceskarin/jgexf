/**
 * 	Author: Bruce Skarin
 *  Dec 2, 2014
 */
package com.jgexf.gexf;

/**
 *Used by Gephi to support the filtering of graphs by time
 * @author Bruce
 *
 */
public class GraphSpell extends GraphTime{

	/**
	 * Basic Graph Spell constructor
	 */
	public GraphSpell() {
		
	}
	/**
	 * 
	 * @param start
	 * @param end
	 * @param format (datetime/double)
	 */
	public GraphSpell(String start, String end, String format) {
		super(start, end, format);
	}
	
	public String toXML(){
		StringBuilder buddy = new StringBuilder();
		buddy.append("          <spell");
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
		buddy.append("/>\n");
		return buddy.toString();
	}
	
	public String toXML(int depth){
		
		String tab_spacing = "";
		for (int i=0;i<depth;i++) {
			tab_spacing +="\t";
		}
		
		StringBuilder buddy = new StringBuilder();
		buddy.append(tab_spacing+"<spell");
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
		buddy.append("/>\n");
		return buddy.toString();
	}

}
