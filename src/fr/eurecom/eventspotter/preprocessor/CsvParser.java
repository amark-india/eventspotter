package fr.eurecom.eventspotter.preprocessor;

import java.util.ArrayList;
import java.util.List;


public class CsvParser<T> 
{
   public CsvParser() {}

    public List<Event> myparse(String CSV)
	{		
		List<Event> Events= new ArrayList<Event>();
		int count=0;
		String a=null,b=null,c=null,d=null,e=null,f=null,g=null,h =null;
		String[] lines = new String[12000];
		    
		lines = CSV.split("\n");

		for(String line : lines)
		{
			if(line == null)break;

			String[] parts = new String[10];
			parts = line.split(",");
			    
            count++;
            if(count==100)System.gc();
				
            int i = 1;
			for(String part : parts)
			{		
				switch(i)
				{
					case 1: a=part;  break;
					case 2: b=part;  break;
					case 3: c=part;  break;
					case 4: d=part;  break;
					case 5: e=part;  break;
					case 6: f=part;  break;
					case 7: g=part;  break;
					case 8: h=part;  break;				
				}
				i++;
			}
				
			Event obj = new Event(a,b,c,d,e,f,g,h);
				
			Events.add(obj);
				
		}
		return Events;		
	} 
}
