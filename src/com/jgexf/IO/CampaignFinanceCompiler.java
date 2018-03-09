/**
 * 	Author: Bruce Skarin
 *  Mar 5, 2018
 */
package com.jgexf.IO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import com.jgexf.gexf.GraphAttribute;
import com.jgexf.gexf.GraphAttributeValue;
import com.jgexf.gexf.GraphEdge;
import com.jgexf.gexf.GraphNode;

/**
 * @author bskarin
 *
 */
public class CampaignFinanceCompiler extends SocialNetworkCompiler {
	
	/**
	 * The location path of the GEFX parameter file
	 */
	private static String paramFile = "parameters.gexf";
	
	/* (non-Javadoc)
	 * @see com.jgexf.IO.SocialNetworkCompiler#init()
	 */
	@Override
	public void init() {
		//Set graph attributes
		buildGraph();
	}

	/* 
	 * 
	 */
	@Override
	public void buildGraphFromFile(String filePath) {
		FileInputStream inputFile = null;
		try 
		{
		//Open the file
		inputFile = new FileInputStream(new File(filePath));
		BufferedReader br = new BufferedReader(new InputStreamReader(inputFile));
		
		//Skip the header
		br.readLine();
				
		DateFormat formatter = NetworkInterface.dateFormat;
		Calendar c = Calendar.getInstance();
		String line = "";
		String format = "datetime";
		int lineNumber = 1;
		//For each line/record add the node and edge data
		while((line = br.readLine()) != null){
			lineNumber++;
			
			//Split record columns
			String[] splitLine = line.split("\\|");
			
			//Set candidate id
			String cid = splitLine[6].trim();
			String label = splitLine[7].trim();
			boolean selfFinance = false;
			//If ID is empty look for self financing
			if(cid.isEmpty()){
				String code = splitLine[13].trim();
				//check transaction code
				if(code.equals("15C")){
					selfFinance = true;
					cid = splitLine[8].trim();
					label = splitLine[18].trim();
					label = label.substring(0, label.length()-4);
				}
				else cid = "na";
			}
			cid = cid + "_" + label;
			String start = splitLine[10];
			int cycle = Integer.parseInt(splitLine[0]);
			//Only load records that are within the cycle
			String cycleStart = (cycle - 2) + "-01-01 00:00:00";
			String cycleEnd = (cycle + 1) + "-01-01 00:00:00";
			Date dateTime = null;
			Date dateStart = null;
			Date dateEnd = null;
			boolean dateOkay = true;
			try{
				dateTime = formatter.parse(start);
				dateStart = formatter.parse(cycleStart);
				dateEnd = formatter.parse(cycleEnd);
				c.setTime(dateTime);
			}
			catch(Exception e){
				dateOkay = false;
				e.printStackTrace();
			}
			//If parsing is fine check cutoff dates
			if(dateOkay){
				if(dateTime.before(dateStart) || dateTime.after(dateEnd)){
					dateOkay = false;
				}
			}
			
			//Check date cutoff
			if(dateOkay){
				c.add(Calendar.DATE, 30); //add 30 days to serve as the life of the contribution
				String end = formatter.format(c.getTime());
				
				//Do contributor node
				GraphNode gn = new GraphNode(cid, label, start, end, format);		
					
				//Now write attributes
				int col = 0;
				for(String val : splitLine){
					boolean isAttribute = false;
					//0-electioncycle,1-contributorstate,4-amount
					//11-zip,12-recip_code,16-gender,17-employer
					switch(col){
						case 0: case 1: case 4: case 11: case 16: case 17:
							isAttribute = true;
							break; 
					}
					if(col== 3 && selfFinance){
						isAttribute = true;
					}
					if(isAttribute){
						String aid = "n" + col;
						val = val.trim();
						if(val.isEmpty()){
							val = "na";
						}
						GraphAttributeValue gav = new GraphAttributeValue(aid, val, start, end, format);
						GraphAttribute ga = nodeAttributes.getAttributeByID(aid);
						gav.setGraphAttribute(ga);
						gn.addAttribute(gav);
					}
					col++;
				}
				//Add count attribute
				String aID = "total count";
				GraphAttributeValue gav = new GraphAttributeValue(aID, "1", start, end, format);
				GraphAttribute ga = nodeAttributes.getAttributeByID(aID);
				gav.setGraphAttribute(ga);
				gn.addAttribute(gav);
				
				networkInterface.updateNode(gn);
				
				//Do recipient node
				String rid = splitLine[18].trim();
				label = splitLine[18].trim();
				gn = new GraphNode(rid, label, start, end, format);		
					
				//write attributes
				col = 0;
				for(String val : splitLine){
					boolean isAttribute = false;
					switch(col){
					//0-electioncycle,2-recipientstate,3-recipientparty,4-amount
						case 0: case 2: case 3: case 4:
							isAttribute = true;
							break; 
					}
					if(isAttribute){
						String aid = "n" + col;
						val = val.trim();
						if(val.isEmpty()){
							val = "na";
						}
						gav = new GraphAttributeValue(aid, val, start, end, format);
						ga = nodeAttributes.getAttributeByID(aid);
						gav.setGraphAttribute(ga);
						gn.addAttribute(gav);
					}
					col++;
				}
				//Add count attribute
				aID = "total count";
				gav = new GraphAttributeValue(aID, "1", start, end, format);
				ga = nodeAttributes.getAttributeByID(aID);
				gav.setGraphAttribute(ga);
				gn.addAttribute(gav);
				
				networkInterface.updateNode(gn);
				
				//Do edges
				String source = cid;
				String target = rid;
				String id = source + "-" + target;
				
				GraphEdge ge = new GraphEdge(id, source, target, start, end, format);
				
				col = 0;
				for(String val : splitLine){
					boolean isAttribute = false;
					switch(col){
						//3-recipientparty, 4-contribution
						case 3: case 4:
							isAttribute = true;
							break; 
					}
					if(isAttribute){
						val = val.trim();
						if(val.isEmpty()){
							val = "na";
						}
						aID = "e" + col;
						gav = new GraphAttributeValue(aID, val, start, end, format);
						ga = edgeAttributes.getAttributeByID(aID);
						gav.setGraphAttribute(ga);
						ge.addAttribute(gav);
					}
					col++;
				}//End for columns
				
				//Add count attribute
				aID = "count";
				gav = new GraphAttributeValue(aID, "1", start, end, format);
				ga = edgeAttributes.getAttributeByID(aID);
				gav.setGraphAttribute(ga);
				ge.addAttribute(gav);
				
				//Set the weight
				ge.setWeight(splitLine[4]);
				
				networkInterface.addEdge(ge);
			}//End if date okay
			//Provide a warning if there is a problem with the date
			else{
				System.out.println("Warning, line number: " + lineNumber + " was not added because date: " + start + " was null or outside of the reporting cycle." );
			}		
			int mod = lineNumber % 10000;
			if(mod == 0){
				System.out.println("Completed line: " + lineNumber);
			}
		}//end while lines
		br.close();
		}//end try
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} 
		catch (OutOfMemoryError e)
		{
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if(inputFile != null)
				try {
					inputFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	/* (non-Javadoc)
	 * @see com.jgexf.IO.SocialNetworkCompiler#getParamFile()
	 */
	@Override
	public String getParamFile() {
		return paramFile;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String inputFile = new String("1990 relevant funders.txt");
		String outputFile = new String("1990");
		CampaignFinanceCompiler cpf = new CampaignFinanceCompiler();
		cpf.buildGraphFromFile(inputFile);
		cpf.processAndWriteGraph(false, outputFile);
	}

}
