package fr.eurecom.eventspotter.preprocessor;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import fr.eurecom.eventspotter.preprocessor.CsvParser;
import fr.eurecom.eventspotter.preprocessor.Event;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.Buffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;


public class GScreator
{
	

  public static void main(String[] args) throws IOException 
  {  
    try
    {
    	//Create a buffered reader that for eventmedia CSV file
    	BufferedReader bufRdr = new BufferedReader(new FileReader("a.csv"));
    	
    	String CSV = "";
    	int count=1;
    	
    	char a[];
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
    	
    		}
    		else
    			break;
    	}
    	bufRdr.close();  
    	//System.out.println(CSV);
	    CsvParser<Event> bean = new CsvParser<Event>();
	    System.out.println("here1");
    
    //Define strategy
   
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
    String temp=new String();
    int limit=1;
    for(Event every : Events)
    {
    	//if(limit>50)break;

    	temp=every.geteventDescription();
    	
    	String long_uri = "(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        String short_uri="(www)[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        String htm="<[^>]*>";
       // IsMatch();
        temp=temp.replaceAll(htm,"");
        temp=temp.replaceAll(long_uri,"");
        temp=temp.replaceAll(short_uri,"");
    	
    	
    	
       // String pattern = "(\\w)([\\.,?!:()<>\\+\\*/@#\\$%\\^\\|\\&;‘'█=])";
       // temp=temp.replaceAll(pattern, "$1 $2");
       // String pattern2 = "([\\.,?!:()<>\\+\\*/@#\\$%\\^\\|\\&;‘'█=])(\\w)";
       // temp=temp.replaceAll(pattern2, "$1 $2");
       // String pattern3 = "([\\.,?!:()<>\\+\\*/@#\\$%\\^\\|\\&;‘'█=])([\\.,?!:()<>\\+\\*/@#\\$%\\^\\|\\&;‘'])";
       // temp=temp.replaceAll(pattern3, "$1 $2");
       // String pattern4 = "([\\.,?!:()<>\\+\\*/@#\\$%\\^\\|\\&;‘'█=])(a-zA-Z0-9)";
        //temp=temp.replaceAll(pattern4, "$1 $2");
    	output = output + every.geteventId() + " ;" + temp + "\n";   	
    
        
        
    	
    	//limit++;
    }
    
    System.out.println("here4");
    File file = new File("GS.txt");
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