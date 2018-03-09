package com.jgexf.gexf;

/**
 * Representation of {@link GraphNode} and {@link GraphEdge} to 
 * unify the handling of {@link GraphAttribute}s and {@link GraphSpell}s.
 * @author bskarin
 *
 */
public class GraphElement extends GraphTime {

	private GraphAttributeValueList attributes;
	private GraphSpellList spells;
	private String id;
	
	public static void main(String args[]) {
		GraphNode testNode = new GraphNode("A","A","2.0","3.0","double");
		GraphEdge testEdge = new GraphEdge("e1", "source", "target");
		System.out.println("Test node: " + testNode.getId());
		System.out.println(" Test Edge: " + testEdge.getId());
	}
	
	public GraphElement() {
		super();
		init();
	}
	
	/**
	 * Basic dynamic {@link GraphElement} constructor.
	 * @param start start time
	 * @param end end time
	 * @param format time format (date/double)
	 */
	public GraphElement(String start, String end, String format) {
		super(start, end, format);
		init();
	}
	
	/**
	 * Initialize default (empty) element variables.
	 */
	private void init() {
		attributes = new GraphAttributeValueList();
		spells = new GraphSpellList();
		id = "";
	}
	
	/**
	 * Dynamic {@link GraphElement} constructor with id.
	 * @param id element ID
	 * @param start start time
	 * @param end end time
	 * @param format time format (date/double)
	 */
	public GraphElement(String id, String start, String end, String format) {
		this(start, end, format);
		setId(id);
	}
	
	/**
	 * @return element ID
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Set element ID.
	 * @param id element ID
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Copies and returns element's {@link GraphAttributeValueList}.
	 * @return the attributes (copy)
	 */
	public GraphAttributeValueList getAttributes() {
		GraphAttributeValueList temp = new GraphAttributeValueList();
		temp.addAll(attributes);
		return temp;
	}
	/**
	 * Sets element's {@link GraphAttributeValueList}.
	 * @param attributes the element attributes to set
	 */
	public void setAttributes(GraphAttributeValueList attributes) {
		this.attributes = attributes;
	}

	/**
	 * Adds {@link GraphAttributeValue} to element's list of values.
	 * @param gav
	 */
	public void addAttribute(GraphAttributeValue gav){
		attributes.addGraphAttribute(gav);
	}

	/**
	 * Adds {@link GraphSpell} to element's list of spells, checking for overlap and modifying prior spells if necessary.
	 * @param spell to be added
	 */
	public void addSpell(GraphSpell spell) {
		//Check to see if a spell indeed needs to be added
		if(spells.isEmpty()){
			//If it overlaps, just update the base period
			if(overlaps(spell)){
				updateGraphTime(spell);
			}
			else{
				//Otherwise add the base period spell and then the new spell
				GraphSpell gs = new GraphSpell(getStart(), getEnd(), spell.getFormat());
				spells.add(gs);
				spells.add(spell);
			}
		}
		else{
			spells.addOrAppendSpell(spell);
		}
		//Make sure base period is covered
		updateGraphTime(spell);
	}
	
	/**
	 * @return the spells
	 */
	public GraphSpellList getSpells() {
		GraphSpellList temp = new GraphSpellList();
		temp.addAll(spells);
		return temp;
	}

	/**
	 * Sets element's {@link GraphSpellList}.
	 * @param spells the spells to set
	 */
	public void setSpells(GraphSpellList spells) {
		this.spells =  (GraphSpellList) spells.clone();
	}
	
	
	
	/**
	 * Returns hash based on element's String id.
	 */
	public int hashCode() {
		return id.hashCode();
	}

}
