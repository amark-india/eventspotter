package fr.eurecom.eventspotter.preprocessor;

import java.util.List;
import fr.eurecom.eventspotter.preprocessor.ColumnPositionMappingStrategy;
import fr.eurecom.eventspotter.preprocessor.CsvToBean;
import fr.eurecom.eventspotter.preprocessor.Event;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;



public class CSVFormatter 
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
    		//System.out.println(next);
    		if(next != null)
    		{
    			CSV=CSV+next;
    			CSV=CSV+"\n";
    			count++;	 
    		System.out.println(count);
    		//if(count==2000)break;
    		}
    		else
    			break;
    	}
    	bufRdr.close();  
    	//System.out.println(CSV);
	    CsvToBean<Event> bean = new CsvToBean<Event>();
	    System.out.println("here1");
    
    //Define strategy
    ColumnPositionMappingStrategy<Event> strategy = 
    new ColumnPositionMappingStrategy<Event>();
    strategy.setType(Event.class);
    strategy.setColumnMapping(new String [] { "eventId", "eventTitle", "publisher", "date" , "location" , "category" , "agent" , "eventDescription" });

    //Parse the CSV
   // List<Event> Events = bean.parse(strategy,new StringReader(CSV));
    
    List<Event> Events = bean.myparse(CSV);
    System.out.println("here2");
    //Append agent and category for rows with same eventId
    Event prev = null;
    /*
    for(Event every : Events)
    {
    	System.out.println(every.geteventTitle());
    }
    */
    for(int i = 0, n = Events.size(); i < n; i++) 
    {
    	Event every = Events.get(i);
        System.out.println("here2.5");
        /*
        if(every.geteventId() == " ")
        	every.seteventId("nil");
        
        if(every.geteventTitle() == " ")
        	every.seteventTitle("nil");
        
        if(every.getpublisher() == " ") 
        	every.setpublisher("nil");
        
        if(every.getdate() == " ")
        	every.setdate("nil");
        
        if(every.getlocation() == " ")
        	every.setlocation("nil");
        
        if(every.getcategory() == " ") 
        	every.setcategory("nil");
        
        if(every.getagent() == null)
        	every.setagent("nil");
        
        if(every.geteventDescription() == " ")	
        	every.seteventDescription("nil");
      	*/

      	//System.out.println(every.getagent());
      	//System.out.println(every.geteventDescription());
    	if(i == 0)
    	{
    	    System.out.println("here2.55");
    		prev = every;
    	}
    	else
    	{
    	    System.out.println("here2.60");
    		if(prev.geteventId().equals(every.geteventId()))
    		{
    		    System.out.println("here2.61");
    		    if(prev.getagent()=="nil"||every.getagent()=="nil")
    		   {
    		    	System.out.println("agent field is blank!\n");
    		    }
    		  else
    		    {
    		    	
    		    	
    		    boolean agentcheck = prev.getagent().equals(every.getagent());  		   
    		    
       			if(!agentcheck)
    		    {
       		        System.out.println("here2.62");
    		    //System.out.println(every.getagent());
    				every.setagent(every.getagent() + ";" + prev.getagent());
    			}
    		    }
       			boolean catcheck = prev.getcategory().equals(every.getcategory());
       			
       			
       			if(!catcheck)
    			{
       			//    System.out.println("here2.63");
    				every.setcategory(every.getcategory() + ";" + prev.getcategory());
    			}
       			//if(every.getagent()!="nil")
    			  prev = every;
    			  System.out.println("here2.62");
    		}
    		else
    		{	
    		    System.out.println("here2.64");
    			prev = every;
    		}
    	}
    }
    //Remove rows with duplicate eventIds
    for(int i = 0, n = Events.size(); i < n-1; i++) 
    {
        System.out.println("here2.75");
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
    System.out.println("here3");

    
    for(Event every : Events)
    {/*
    	if(every.geteventTitle()==null)
    		every.seteventTitle("nil");
    	if(every.getpublisher()==null)
    		every.setpublisher("nil");
    	if(every.getdate()==null)
    		every.setdate("nil");
    	if(every.getlocation()==null)
    		every.setlocation("nil");
    	if(every.getcategory()==null)
    		every.setcategory("nil");
    	if(every.getagent()==null)
    		every.setagent("nil");
    	if(every.geteventDescription()==null)
    		every.seteventDescription("nil");
    	//System.out.println(every.getagent()); */
    	output = output + every.geteventId() + "\t" + every.geteventTitle() + "\t" + every.getpublisher() + "\t " + every.getdate() + "\t " +
    			  every.getlocation() + "\t " + every.getcategory() + "\t" + every.getagent() + "\t " + every.geteventDescription() + "\n";   	
    }
    
 //   System.out.println(output);
    System.out.println("here4");
   // System.out.println(check);  
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
        System.out.println("hereinxepttion");
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