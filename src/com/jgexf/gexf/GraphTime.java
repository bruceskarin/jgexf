/**
 * 	Author: Bruce Skarin
 *  Dec 10, 2014
 *  
 */
package com.jgexf.gexf;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Base class for all other time dependent classes
 * @author bskarin
 *
 */
public class GraphTime {
	private String start;
	private String end;
	private String format;
	private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat XML_date_formatter= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
	
	/**
	 * Basic graph time constructor
	 */
	public GraphTime(){
		start = "";
		end = "";
		format = "double";
	}
	
	/**
	 * 
	 * @param start
	 * @param end
	 * @param format (datetime/double)
	 */
	GraphTime(String start, String end, String format){
		this.start = start;
		this.end = end;
		this.setFormat(format);
	}
	
	/**
	 * 
	 * @param time
	 * @return the Date object
	 */
	public static Date getAsDate(String time){		
		Date dt = new Date();
		
		if(!time.isEmpty()){
			try {
				dt = formatter.parse(time);
			} catch (ParseException e) {
				
				try {
					dt = XML_date_formatter.parse(time);
				} catch (ParseException e2) {
					e.printStackTrace();
				}
			}
		}
		else{
			System.out.println("Warning: datetime not set properly");
		}
		
		return dt;
	}
	
	
	/**
	 * 
	 * @param dt
	 * @return the String for the given date
	 */
	public static String getDateAsString(Date dt){ 
		String dateString = formatter.format(dt);
		return dateString;
	}

	
	/**
	 * 
	 * @param dt
	 * @return the String for the given date
	 */
	public static String getDateAsXMLString(Date dt){ 
		String dateString = XML_date_formatter.format(dt);
		return dateString;
	}
	
	/**
	 * 
	 * @param gt
	 * @return true if the time overlaps
	 */
	public boolean overlaps(GraphTime gt){
		if(start.isEmpty() || gt.getStart().isEmpty()){
			System.out.println("Warning: start time not set for:" + this);
			return false;
		}
		
		else if(getEnd().isEmpty() || gt.getEnd().isEmpty()){
			
			return checkStart(gt);
		}
		
		else return checkOverlap(gt);

	}
	/**
	 * 
	 * @param gt
	 * @return true if the start time is the same
	 */
	private boolean checkStart(GraphTime gt){
		
		if(getStart().equals(gt.getStart())) return true;
		
		else return false;
		
	}
	
	public boolean checkOverlap(GraphTime gt){
		
		if(gt.getStart().isEmpty() || start.isEmpty()){
			System.out.println("Warning: start time not set for:" + this);
			return false;
		}
		
		else if(format.equals("datetime")){
			Date s1 = getAsDate(start);
			Date s2 = getAsDate(gt.getStart());
			Date e1 = getAsDate(end);
			Date e2 = getAsDate(gt.getEnd());
			
			if(e1.before(s2) || e2.before(s1)){
				return false;
			}
			else return true;
		}
		else{
			Double s1 = Double.parseDouble(start);
			Double s2 = Double.parseDouble(gt.getStart());
			Double e1 = Double.parseDouble(end);
			Double e2 = Double.parseDouble(gt.getEnd());
			
			if(e1 < s2 || e2 < s1){
				return false;
			}
			else return true;
		}
	}
	
	/**
	 * Change start/end dates based on another {@link GraphTime}.
	 * @param gt
	 */
	public void updateGraphTime(GraphTime gt){
		if(gt.getStart().isEmpty() || start.isEmpty() || gt.getEnd().isEmpty() || end.isEmpty()){
			System.out.println("Warning: time not set for:" + this);
		}
		else {
		
			if(format.equals("datetime")){
				Date s1 = getAsDate(start);
				Date s2 = getAsDate(gt.getStart());
				Date e1 = getAsDate(end);
				Date e2 = getAsDate(gt.getEnd());
				
				if(s1.after(s2)){
					start = gt.getStart();
				}
				if(e1.before(e2)){
					end = gt.getEnd();
				}			
			} //End if date
			else{
				Double s1 = Double.parseDouble(start);
				Double s2 = Double.parseDouble(gt.getStart());
				Double e1 = Double.parseDouble(end);
				Double e2 = Double.parseDouble(gt.getEnd());
				
				if(s1 > s2){
					start = gt.getStart();
				}
	
				if(e1 < e2){
					end = gt.getEnd();
				}
			}
		}
	}
	/**
	 * 
	 * @return true if the interval is okay
	 */
	public boolean checkInterval(){
		
		if(!getEnd().isEmpty()){
		
			if(format.equals("datetime")){
				Date s1 = getAsDate(start);
				Date e1 = getAsDate(end);
				
				if(s1.after(e1)){
					return false;
				}
				else return true;		
			} //End if date
			else{
				Double s1 = Double.parseDouble(start);
				Double e1 = Double.parseDouble(end);
				
				if(s1 > e1){
					return false;
				}
				else return true;
			}
		}
		else return true;
	}
	
	/**
	 * @return the start
	 */
	public String getStart() {
		return start;
	}
	/**
	 * @param start the start date to set
	 */
	public void setStart(String start) {
		this.start = start;
	}
	
	
	/**
	 * @return the end
	 */
	public String getEnd() {
		return end;
	}
	/**
	 * @param end the end date to set
	 */
	public void setEnd(String end) {
		this.end = end;
	}

	/**
	 *Returns the start date in xml format (ISO 8601)
	 * @return the formatted start time
	 */
	public String getStartXML(){ 

		Date start_date = getAsDate(start);
		String dateString = XML_date_formatter.format(start_date);
		return dateString;
	}

	/**
	 * Returns the end date in xml format (ISO 8601)
	 * @return the formatted end time
	 */
	public String getEndXML(){ 

		Date end_date = getAsDate(end);
		String dateString = XML_date_formatter.format(end_date);
		return dateString;
	}
	
	/**
	 * @return the format type ({@link Date} or {@link Double})
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @param format the type of to set ({@link Date} "datetime" or {@link Double})
	 */
	public void setFormat(String format) {
		this.format = format;
	}
	
	/**
	 * Updated by aflowers on 05/24/17
	 * Convert {@link GraphTime} attributes to XML String.
	 * @return XML String
	 */
	public String toXML() {
		StringBuilder buddy = new StringBuilder();
		if(!getStart().isEmpty()){
			buddy.append(" start=\"");
			buddy.append( getStartXML());
			buddy.append("\"");
		}
		if(!getEnd().isEmpty()){
			buddy.append(" end=\"");
			buddy.append(getEndXML());
			buddy.append("\"");
		}
		return buddy.toString();
	}
	
	public String toJSON() {
		StringBuilder buddy = new StringBuilder();
		if(!getStart().isEmpty()){
			buddy.append(" \"start\":\"");
			buddy.append( getStartXML());
			buddy.append("\",");
		}
		if(!getEnd().isEmpty()){
			buddy.append(" \"end\":\"");
			buddy.append(getEndXML());
			buddy.append("\"");
		}
		return buddy.toString();
	}
}
