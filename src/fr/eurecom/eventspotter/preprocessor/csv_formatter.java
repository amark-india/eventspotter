package fr.eurecom.eventspotter.preprocessor;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import fr.eurecom.eventspotter.preprocessor.ColumnPositionMappingStrategy;
import fr.eurecom.eventspotter.preprocessor.CsvToBean;
import fr.eurecom.eventspotter.preprocessor.event;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;


public class csv_formatter 
{
	

  public static void main(String[] args) throws IOException 
  {  
    try
    {
    	//Create a buffered reader that for eventmedia CSV file
    	BufferedReader bufRdr = new BufferedReader(new FileReader("eventmedia_2012.csv"));
		
    	String CSV = "";
    	int count=1;
    	
    	//Read all data from file and append into string CSV
    	while(true)
    	{
    		if(count==200)
    			break;
    		
    		CSV=CSV+bufRdr.readLine();
    		CSV=CSV+"\n";
    		count++;	 
    	}
    	System.out.println(CSV);
	    CsvToBean<event> bean = new CsvToBean<event>();
	
	
	
    
    //Define strategy
    ColumnPositionMappingStrategy<event> strategy = 
    new ColumnPositionMappingStrategy<event>();
    strategy.setType(event.class);
    strategy.setColumnMapping(new String [] { "eventId", "eventTitle", "publisher", "date" , "location" , "category" , "agent" , "eventDescription" });
    
    //Parse the CSV
    List<event> events = bean.parse(strategy,new StringReader(CSV));
    
    //Append agent and category for rows with same eventId
    event prev = null;
    System.out.println(events.size());
    for(int i = 0, n = events.size(); i < n; i++) 
    {
    	event every = events.get(i);

    	if(i == 0)
    	{
    		prev = every;
    	}
    	else
    	{
    		if(prev.geteventId().equals(every.geteventId()))
    		{
    			every.setagent(every.getagent() + ";" + prev.getagent());
    			every.setcategory(every.getcategory() + ";" + prev.getcategory());
    			prev = every;
    		}
    		else
    		{	
    			prev = every;
    		}
    	}
    }
    //Remove rows with duplicate eventIds
    for(int i = 0, n = events.size(); i < n-1; i++) 
    {
    	try
    	{
    	event every = events.get(i);
    	if(every == null)break;
    	
    	
    	if(i == 0)
    	{
    		prev = every;
    	}
    	else
    	{
    		if(prev.geteventId().equals(every.geteventId()))
    		{
    			events.remove(i-1);
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
    String output = "";
    for(event every : events)
    {
    	output = output + " " + every.geteventId() + "\t" + every.geteventTitle() + "\t" + every.getpublisher() + "\t " + every.getdate() + "\t " +
    			  every.getlocation() + "\t " + every.getcategory() + "\t" + every.getagent() + "\t " + every.geteventDescription() + "\n";   	
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
    bufRdr.close();                             

    }

    catch(FileNotFoundException e)
    {
    	e.printStackTrace();
    }
  }
}