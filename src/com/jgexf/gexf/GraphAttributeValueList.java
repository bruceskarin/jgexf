/**
 * 	Author: Bruce Skarin
 *  Dec 1, 2014
 */
package com.jgexf.gexf;

import java.util.ArrayList;

/**
 * A list for storing the attribute values of a node or edge.
 * Ensures that attributes added do not produce any duplicate
 * instances for the same time period.
 * @author Bruce
 *
 */
public class GraphAttributeValueList extends ArrayList<GraphAttributeValue>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1251728200937571619L;
	private boolean verbose = false;
	
	public GraphAttributeValueList() {
		
	}
	/**
	 * Add {@link GraphAttributeValue} to list, aggregate or merge depending on value,
	 * and make note of duplicate value if detected.
	 * @param gav
	 */
	public void addGraphAttribute(GraphAttributeValue gav){
		//Check for merge, aggregate, and duplicates
		boolean noDuplicate = true;
		for(GraphAttributeValue gav2 : this){
			//Check for match
			if(gav.getId().equals(gav2.getId())){
				//Check for total, aggregate, or merge
				if(gav2.getGraphAttribute() != null){
					GraphAttribute ga = gav2.getGraphAttribute();
					//If the type starts with 'a' then append or aggregate the result
					String mode = ga.getDataMode();
					String type = ga.getType();
					if(mode.equalsIgnoreCase(GraphAttribute.TOTAL)){	
						aggregateValue(gav2, gav, type);
						noDuplicate = false;
						break;
					}
					else if(mode.equalsIgnoreCase(GraphAttribute.ADD) && gav.overlaps(gav2)){
						aggregateValue(gav2, gav, type);
						noDuplicate = false;
						break;
					}
					else if(mode.equalsIgnoreCase(GraphAttribute.MERGE)){
						noDuplicate = mergeValue(gav2, gav);
						//If merge action is completed, exit the loop, otherwise check other instances for a match
						if(!noDuplicate){
							break;
						}
					}
					//Still check duplicate if it is a standard or not specified
					else if(gav.getStart().equals(gav2.getStart())){
						if(verbose)
							System.out.println("Warning, attribute " + ga.getTitle() + " value: " + gav2.getValue() + " could not be added because it is at a duplicate time");
						noDuplicate = false;
						break;
					}
				}//End if attribute
				//Otherwise Check for duplicate
				else if(gav.getStart().equals(gav2.getStart())){
					if(verbose)
						System.out.println("Warning, attribute value: " + gav2.getValue() + " could not be added because it is at a duplicate time");
					noDuplicate = false;
					break;
				}
			}
		}
		if(noDuplicate){
			this.add(gav);
		}
	}
	
	/**
	 * Aggregate numeric values of {@link GraphAttribute}s.
	 * @param gav1
	 * @param gav2
	 * @param type
	 */
	public void aggregateValue(GraphAttributeValue gav1, GraphAttributeValue gav2, String type){
		String newValue = gav1.getValue();
		//if numeric, parse the value and add them
		if(type.startsWith("int") || type.equalsIgnoreCase("double") || type.equalsIgnoreCase("float")){
			double dValue = Double.parseDouble(gav1.getValue()) + Double.parseDouble(gav2.getValue());
			newValue = "" + dValue;
		}
		else{
			newValue += ", " + gav2.getValue();
		}
		gav1.setValue(newValue);
		//Update the time
		gav1.updateGraphTime(gav2);
	}
	
	/**
	 * Merge {@link GraphAttributeValue}s with equal values.
	 * @param gav1
	 * @param gav2
	 * @return if values are not duplicates
	 */
	public boolean mergeValue(GraphAttributeValue gav1, GraphAttributeValue gav2){
		//If the new value is na it can be ignored
		if(gav2.getValue().equals("na")){
			return false;
		}
		//Check for duplicate period
		else if(gav1.checkOverlap(gav2)){ 
			//Check for "na" value and replace it with the new value
			if(gav1.getValue().equals("na")){
				gav1.setValue(gav2.getValue());
				return false;
			}
			//If the value is unchanged then it can be merged and the period updated
			else if(gav1.getValue().equals(gav2.getValue())){
				gav1.updateGraphTime(gav2);
				return false;
			}
			else{
				aggregateValue(gav1, gav2, "string");
				System.out.println("Warning, attribute value: " + gav2.getValue() + " could not be merged because it is different at a duplicate time so it has been appended.");
				return false;
			}
		}
		else return true;
	}
	
	/**
	 * @param id of {@link GraphAttribute} whose value is to be retrieved
	 * @return 
	 * 			first {@link GraphAttributeValue} with matching id
	 */
	public GraphAttributeValue getGraphAttributeValueByID(String id){
		for(GraphAttributeValue gav : this){
			if(gav.getId().equals(id)){
				return gav;
			}
		}
		return null;
	}
		
	public String toXML(){
		StringBuilder buddy = new StringBuilder();
		buddy.append("        <attvalues>\n");
		for(GraphAttributeValue gav : this){
			buddy.append(gav.toXML());
		}
		buddy.append("        </attvalues>\n");
		return buddy.toString();
	}

	public String toXML(int depth){
		
		String tab_spacing = "";
		for (int i=0;i<depth;i++) {
			tab_spacing +="\t";
		}
		
		StringBuilder buddy = new StringBuilder();
		buddy.append(tab_spacing+"<attvalues>\n");
		for(GraphAttributeValue gav : this){
			buddy.append(gav.toXML(depth+1));
		}
		buddy.append(tab_spacing+"</attvalues>\n");
		return buddy.toString();
	}
	
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	public String toJSON() {

		
		StringBuilder buddy = new StringBuilder();
		buddy.append("\"attributes\":{");
		
		int attrCounter = 0;
		for(GraphAttributeValue gav : this){
			buddy.append(gav.toJSON());
			attrCounter+=1;
			if  (attrCounter<this.size()) {
				buddy.append(",");
			}
		}
		buddy.append("}");
		return buddy.toString();
	}
	
}
