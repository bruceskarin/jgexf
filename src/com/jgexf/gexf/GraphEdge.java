/**
 * 	Author: Bruce Skarin
 *  Nov 25, 2014
 */
package com.jgexf.gexf;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jgexf.gexf.util.GraphWriter;


/**
 * @author bskarin
 *
 */
public class GraphEdge extends GraphElement {
	
	private String source;
	private String target;
	
	private GraphElement sourceNode = null;
	private GraphElement targetNode = null;
	
	private String weight;
	
	/**
	 * Basic {@link GraphEdge} Constructor
	 */
	public GraphEdge(){
		super();
		setWeight("");
	}
	
	/**
	 * Basic {@link GraphEdge} Constructor with id.
	 * @param id
	 */
	public GraphEdge(String id) {
		this();
		setId(id);
	}
	
	/**
	 * Constructor to initialize id, source, and target of {@link GraphEdge}.
	 * @param id
	 * @param source
	 * @param target
	 */
	public GraphEdge(String id, String source, String target) {
		this();
		setId(id);
		init(source, target);
	}
	/**
	 * Constructor to initialize id, source, target, start date, and time format of {@link GraphEdge}.
	 * @param id
	 * @param source
	 * @param target
	 * @param start
	 */
	public GraphEdge(String id, String source, String target, String start, String format){
		this(id, source, target);
		setStart(start);
		setFormat(format);
	}
	
	/**
	 * Constructor to initialize id, source, target, start/end date, and time format of {@link GraphEdge}.
	 * @param id
	 * @param source
	 * @param target
	 * @param start
	 * @param end
	 */
	public GraphEdge(String id, String source, String target, String start, String end, String format){
		super(id, start, end, format);
		init(source, target);
		setWeight("");
	}
	
	/**
	 * Copy constructor
	 * @param edge the original edge
	 */
	public GraphEdge(GraphEdge edge){
		super(edge.getId(), edge.getStart(), edge.getEnd(), edge.getFormat());
		init(source, target);
		this.sourceNode = edge.getSourceNode();
		this.targetNode = edge.getTargetNode();
		setWeight(edge.getWeight());
	}
	
	/**
	 * Initialize {@link GraphEdge}'s source and target.
	 * @param source
	 * @param target
	 */
	private void init(String source, String target) {
		this.source = source;
		this.target = target;
	}
	
	@Override
	public String toString() {
		return "(id=" + getId() + ")";
	}
	
	/**
	 * Convert {@link GraphEdge} attributes to XML String.
	 */
	public String toXML(){
		StringBuilder buddy = new StringBuilder();
		buddy.append("      <edge id=\"");
		buddy.append(GraphWriter.cleanText(getId()));
		buddy.append("\" source=\"");
		buddy.append(GraphWriter.cleanText(source));
		buddy.append("\" target=\"");
		buddy.append(GraphWriter.cleanText(target));
		buddy.append("\"");
		if(!weight.isEmpty()){
			buddy.append(" weight=\"" + weight + "\"");
		}
		buddy.append(super.toXML()); // GraphTime attributes
		buddy.append(">\n");
		if(!getSpells().isEmpty()){
			buddy.append(getSpells().toXML());
		}
		if(!getAttributes().isEmpty()){
			buddy.append(getAttributes().toXML());
		}
		buddy.append("      </edge>\n");
		return buddy.toString();
	}

	public String toJSON(){
		StringBuilder buddy = new StringBuilder();
		buddy.append("{\"id\":\"");
		buddy.append(getId());
		buddy.append("\",\"source\":\"");
		buddy.append(source);
		buddy.append("\",\"target\":\"");
		buddy.append(target);
		buddy.append("\"");
		if(!weight.isEmpty()){
			buddy.append(" ,\"attributes\":{\"Weight\":\"" + weight + "\"}");
		}
		
		buddy.append("}");
		
		//Remove newlines from stringbuilder (tweets often add them)
		Pattern newlineFinder = Pattern.compile("\n|\r");
		
		Matcher newlineMatcher = newlineFinder.matcher(buddy.toString());
		
		String outString = newlineMatcher.replaceAll("");
		outString = outString.replaceAll("\\\\{1,2}",  Matcher.quoteReplacement("\\\\"));

		return outString;
	}
	
	/**
	 * @return the source of the edge
	 */
	public String getSource() {
		return source;
	}
	
	/**
	 * @param source the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}
	
	/**
	 * @return the target
	 */
	public String getTarget() {
		return target;
	}
	
	/**
	 * @param target the target to set
	 */
	public void setTarget(String target) {
		this.target = target;
	}

	/**
	 * @return the weight
	 */
	public String getWeight() {
		return weight;
	}

	/**
	 * @param weight the weight to set
	 */
	public void setWeight(String weight) {
		this.weight = weight;
	}
	
	public void updateWeight(String w2){
		if(weight.isEmpty() && !w2.isEmpty()){
			setWeight(w2);
		}
		else if(!weight.isEmpty() && !w2.isEmpty()){
			double total = Double.parseDouble(weight) + Double.parseDouble(w2);
			setWeight("" + total);
		}
	}

	public GraphElement getSourceNode() {
		return sourceNode;
	}

	public void setSourceNode(GraphElement sourceNode) {
		this.sourceNode = sourceNode;
	}

	public GraphElement getTargetNode() {
		return targetNode;
	}

	public void setTargetNode(GraphElement targetNode) {
		this.targetNode = targetNode;
	}

	/**
	 * Combine the weight and times of two edges with the same source and target location
	 * @param edge the edge to combine with this edge
	 * @return True if the merge was successful, false if the source and target are different
	 */
	public void merge(GraphEdge edge) {
				
		Double internalWeight = Double.parseDouble(this.getWeight());
		Double otherWeight = Double.parseDouble(edge.getWeight());
		
		if (internalWeight != null && otherWeight!=null) {
			
			this.updateGraphTime(edge);
			this.setWeight(String.valueOf(internalWeight+otherWeight));
			
		}
		
	}
}
