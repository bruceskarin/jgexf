/**
 * 	Author: Bruce Skarin
 *  Nov 25, 2014
 */
package com.jgexf.gexf;


/**
 * Instantiated from the parameters.gefx file, attributes define the id, title, type, and data mode
 * for the nodes and edges of a graph.
 * 
 * @author bskarin
 *
 */
public class GraphAttribute {
	private String id;
	private String title;
	private String type; // GEXF permitted data types {integer, float, double, boolean, string}
	
	private String dataMode; //The mode for adding, merging, or totaling attribute values
	public static final String STANDARD = "standard"; //Try to include an attribute value while preventing duplicate overlaps
	public static final String ADD = "add"; //Sum integer and double values for overlapping instances of the attribute, append strings
	public static final String MERGE = "merge"; //Merge all instances of the attribute while preventing duplicate overlaps
	public static final String TOTAL = "total"; //Sum integer and double values for all instances of the attribute, append strings

	public GraphAttribute() {
		type = "string";
		dataMode = STANDARD;
		}
	/**
	 * Constructor including id, title, and type.
	 * @param id
	 * @param title
	 * @param type
	 */
	public GraphAttribute(String id, String title, String type) {
		this();
		this.id = id;
		this.title = title;
		this.type = type;
	}

	@Override
	public String toString() {
		return "(id=" + id + ", title=" + title + ", type=" + type + ")";
	}
	
	/**
	 * Build XML representation of {@link GraphAttribute}.
	 * 
	 * @return XML string
	 */
	public String toXML() {
		StringBuilder buddy = new StringBuilder();
		buddy.append("      <attribute id=\"");
		buddy.append(id);
		buddy.append("\" title=\"");
		buddy.append(title);
		buddy.append("\" type=\"");
		buddy.append(type);
		buddy.append("\">");
		buddy.append("</attribute>\n");
		return buddy.toString();
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the type (e.g. string, integer, double)
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the mode
	 */
	public String getDataMode() {
		return dataMode;
	}
	/**
	 * @param mode the mode to set
	 */
	public void setDataMode(String mode) {
		this.dataMode = mode;
	}
}
