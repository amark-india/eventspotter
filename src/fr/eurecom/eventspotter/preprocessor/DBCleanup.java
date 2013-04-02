package fr.eurecom.eventspotter.preprocessor;

import java.util.List;
import fr.eurecom.eventspotter.preprocessor.CsvParser;
import fr.eurecom.eventspotter.preprocessor.Event;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class DBCleanup 
{
	public static void main(String[] args) throws IOException 
	{  
		try
		{
			//Create a buffered reader that for eventmedia CSV file
			BufferedReader bufRdr = new BufferedReader(new FileReader("a.csv"));
    	
			String CSV = "";
			int count=1;
    	
			//Read all data from file and append into string CSV
			while(true)
			{
				String next = bufRdr.readLine();
				if(next != null)
				{
					CSV=CSV+next;
					CSV=CSV+"\n";
					count++;	 
					System.out.println(count);
				}
    		
				else
					break;
			}
    	bufRdr.close();  

	    CsvParser<Event> bean = new CsvParser<Event>();

	    List<Event> Events = bean.myparse(CSV);

	    //Append agent and category for rows with same eventId
	    Event prev = null;
	    for(int i = 0, n = Events.size(); i < n; i++) 
	    {
	    	Event every = Events.get(i);
        
	    	if(i == 0)
	    	{
	    		prev = every;
	    	}
	    	else
	    	{
	    		if(prev.geteventId().equals(every.geteventId()))
	    		{
	    			if(prev.getagent()=="nil"||every.getagent()=="nil")
	    			{
	    				System.out.println("agent field is blank!\n");
	    			}
	    			else
	    			{  		    	
	    				boolean agentcheck = prev.getagent().equals(every.getagent());  		   
    		    
	    				if(agentcheck==false)
	    				{
	    					System.out.println("here2.62");
	    					every.setagent(every.getagent() + ";" + prev.getagent());
	    				}
	    			}
       			
	    		 boolean catcheck = prev.getcategory().equals(every.getcategory());
       			
       			
       			if(catcheck==false)
    			{
    				every.setcategory(every.getcategory() + ";" + prev.getcategory());
    			}

    			 prev = every;
	    		}
	    		
	    		else
	    		{	
	    			prev = every;
	    		}
	    	}
	    }
	    
	    //Remove rows with duplicate eventIds
	    for(int i = 0, n = Events.size(); i < n-1; i++) 
	    {
	    	try
	    	{
	    		Event every = Events.get(i);
	    		if(every == null)break;
    		
	    		if(i == 0)
	    		{
	    			prev = every;
	    		}
	    		else
	    		{
	    			if(prev.geteventId().equals(every.geteventId()))
	    			{
	    				Events.remove(i-1);
	    				--i;
	    			}
	    			prev = every;
	    		}
	    	}
	    	catch (Exception e)
	    	{
	    		break;
	    	}
	    }
	
	    //Create output string 	   
	    String output = new String();
	    String temp=new String();
	    for(Event every : Events)
	    {
	    	temp=every.geteventDescription();
	    	
	    	String long_uri = "(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
	        String short_uri="(www)[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
	        String htm="<[^>]*>";
	       // IsMatch();
	        temp=temp.replaceAll(htm,"");
	        temp=temp.replaceAll(long_uri,"");
	        temp=temp.replaceAll(short_uri,"");
	    	
	    	output = output + every.geteventId() + "\t" + every.geteventTitle() + "\t" + every.getpublisher() + "\t " + every.getdate() + "\t " +
    			 every.getlocation() + "\t " + every.getcategory() + "\t" + every.getagent() + "\t " + temp + "\n";   	
	    }
    
	    // Open output csv file. If it doesn't exists, then create it
	    File file = new File("em.csv");
	    try
	    {     
	    	if (!file.exists()) 
	    	{
                file.createNewFile();
	    	} 
	    }
    
	    catch (IOException e)
	    {
	    	e.printStackTrace();
	    }

	    //Get FileWriter for output csv file
	    FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
	    BufferedWriter bw = new BufferedWriter(fw);
    
	    bw.write(output);
             
	    bw.close();
                               
	}
    catch(FileNotFoundException e)
    {
    	e.printStackTrace();
    }
  }
}