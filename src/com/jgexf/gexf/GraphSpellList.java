/**
 * 	Author: Bruce Skarin
 *  Dec 10, 2014
 */
package com.jgexf.gexf;

import java.util.ArrayList;

/**
 * The list object for storing  {@link GraphSpell} objects and ensuring there are no duplicates 
 * @author bskarin
 *
 */
public class GraphSpellList extends ArrayList<GraphSpell> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4356412485151704929L;
	
	/**
	 * Basic graph spell list constructor
	 */
	GraphSpellList(){
		
	}
	
	/**
	 * Add spell to list or update overlapping spells.
	 * @param gs2 spell to be added
	 */
	public void addOrAppendSpell(GraphSpell gs2){
		boolean checkForMerge = false;
		for(GraphSpell gs1 : this){
			if(gs1.overlaps(gs2)){
				gs1.updateGraphTime(gs2);
				checkForMerge = true;
				break;
			}
		}//End for spell
		if(checkForMerge){
			checkForMerges();
		}
		else{
			this.add(gs2);
		}
	}
	
	/**
	 * Check to see if any {@link GraphSpell}s in list are overlapping and thus need to be merged.
	 * @return false if no merge is needed
	 */
	public boolean checkForMerges(){
		if(this.size() < 2){
			return false;
		}
		boolean needMerge = false;
		GraphSpell gs1 = this.get(0);
		GraphSpell gs2 = this.get(1);
		//Loop through triangle and check for merges
		for(int i=0; i < this.size() && !needMerge; i++){
			for(int j=0; j < i && !needMerge; j++){
				if(i != j){
					gs1 = this.get(i);
					gs2 = this.get(j);
					if(gs1.overlaps(gs2)){
						needMerge=true;
					}
				}
			}
		}//End check triangle
		if(needMerge){
			gs1.updateGraphTime(gs2);
			this.remove(gs2);
		}
		return needMerge;
	}
	
	/**
	 * Convert {@link GraphSpellList} to XML representation.
	 * @return XML representation
	 */
	public String toXML() {
		StringBuilder buddy = new StringBuilder();
		buddy.append("        <spells>\n");
		for(GraphSpell gs : this){
			buddy.append(gs.toXML());
		}
		buddy.append("        </spells>\n");
		return buddy.toString();
	}
	
	/**
	 * Convert {@link GraphSpellList} to XML representation.
	 * @return XML representation
	 */
	public String toXML(int depth) {
		
		String tab_spacing = "";
		for (int i=0;i<depth;i++) {
			tab_spacing +="\t";
		}
		
		StringBuilder buddy = new StringBuilder();
		buddy.append(tab_spacing+"<spells>\n");
		for(GraphSpell gs : this){
			buddy.append(gs.toXML(depth+1));
		}
		buddy.append(tab_spacing+"</spells>\n");
		return buddy.toString();
	}
}
