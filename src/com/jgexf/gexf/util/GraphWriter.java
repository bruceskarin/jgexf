/**
 * 	Author: Bruce Skarin
 *  Dec 1, 2014
 */
package com.jgexf.gexf.util;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import com.jgexf.gexf.GraphData;

/**
 * @author bskarin
 *
 */
public class GraphWriter {

	/**
	 * Write {@link GraphData} to file.
	 * @param fileName
	 * @param gd
	 */
	public static void writeToFile(String fileName, GraphData gd){
		BufferedWriter out = null;
		try {
			FileOutputStream fstream = new FileOutputStream(fileName);
			out = new BufferedWriter(new OutputStreamWriter(fstream, "UTF-8"));
			gd.toXML(out);
			out.close();
			System.out.println("GEXF written to file: "+ fileName);
		} catch (Exception e){
			System.err.println("Error: " + e.getMessage());
		} finally {
			if(out != null)
				try { out.close(); } 
				catch (IOException e) { }
		}
	}

	/**
	 * Format text to be written in XML by escaping predefined entitites and converting to UTF-8.
	 * @param text text to be cleaned
	 * @return clean text
	 */
	public static String cleanText(String text) {
		String cleanText = text.replace("\n", "");
		cleanText = cleanText.replace("&", "&amp;");
		cleanText = cleanText.replace("\"","&quot;");
		cleanText = cleanText.replace("'","&apos;");
		cleanText = cleanText.replace("<","&lt;");
		cleanText = cleanText.replace(">","&gt;");
		// attempt to convert text to UTF-8
		try {
			byte[] utf8bytes = cleanText.getBytes("UTF8");
			cleanText = new String(utf8bytes, "UTF8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return cleanText;
	}
}
