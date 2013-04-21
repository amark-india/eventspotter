package fr.eurecom.eventspotter;

import java.io.BufferedWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import fr.eurecom.eventspotter.caslight.FeatureStructure;
import fr.eurecom.eventspotter.worker.EventSpotterLight;


public class EventSpotter
{
    private String titlesFilePath;
    private int maxTitleLength;
    // private Set<String> SUPPORTED_MIMETYPES = Collections.unmodifiableSet(new HashSet<String>(
    //     Arrays.asList("document/plain")));
    // public static final Integer defaultOrder = ServiceProperties.ORDERING_PRE_PROCESSING;
    private EventSpotterLight eventspotter;
    //  final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());
    private String dbPath;
    private String dbDriver;
    private String dbUser;
    private String dbPassword;
   


    public String start_spotter(String document) throws IOException
    {
        this.titlesFilePath = "/opt/event-titles.list";
        this.dbPath = "jdbc:mysql://localhost/trial";
        this.dbDriver = "com.mysql.jdbc.Driver";
        this.dbUser = "root";
        this.dbPassword = "root";
        this.maxTitleLength = 1000;
        this.eventspotter = new EventSpotterLight(titlesFilePath, dbDriver, dbPath, dbUser, dbPassword, maxTitleLength);
        //logger.info("EventSpotter initiated with titles file:" + titlesFilePath);

        //logger.info("Running eventSpotter...");
        Set<FeatureStructure> spottedevents = eventspotter.spotevents(document);
        //logger.info("Processing spotted events(" + spottedevents.size() + ")");
        File file = new File("output.txt");
        try{
            // if file doesnt exist, then create it
        	if (file.exists()) {
        		file.delete();
            }
        	file.createNewFile();

        } catch (IOException e) {
            e.printStackTrace();
        }
        Set<FeatureStructure> s = new HashSet<FeatureStructure>(spottedevents);
        for(FeatureStructure str : s)
        {
        	for(FeatureStructure ss : s)
        	{
        	if(!str.getFeature("title from input").getValueAsString().contentEquals(ss.getFeature("title from input").getValueAsString()))
        	{
        			if(str.getFeature("title from input").getValueAsString().contains(ss.getFeature("title from input").getValueAsString()))
        		        		{
        				if(ss.getFeature("title from input").getValueAsString()!="null")
        			ss.getFeature("title from input").setValue("null");
        		}
        		}
        	}
        }
        
        Iterator<FeatureStructure> iter = s.iterator();
        while (iter.hasNext()) {
            if (iter.next().getFeature("title from input").getValueAsString() == "null") iter.remove();
        }
        for (FeatureStructure fs : s) 
        {      	
            
        	String label= fs.getFeature("title from input").getValueAsString();
            String new_label=" <EVENT>"+label+"</EVENT> ";
            if(!document.contains(new_label))
            	document=document.replaceAll(" "+label+" ",new_label);
            int startChar = fs.getFeature("begin").getValueAsInteger();
            int endChar = fs.getFeature("end").getValueAsInteger();
            String agent = fs.getFeature("agent").getValueAsString();            
            String type = fs.getFeature("type").getValueAsString();
            String uri = fs.getFeature("eventId").getValueAsString();
            String confidence =fs.getFeature("confidence").getValueAsString();
            String surrounding =fs.getFeature("Surrounding").getValueAsString();
            String input_Text =fs.getFeature("title from input").getValueAsString();        
            try{
                FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write("{");
                bw.newLine();
                bw.write("label:\""+label+"\"");
                bw.newLine();
                bw.write("startChar:"+startChar);
                bw.newLine();
                bw.write("endChar:"+endChar);
                bw.newLine();
                bw.write("agent(s):\""+agent+"\"");
                bw.newLine();
                bw.write("type:\""+type+"\"");
                bw.newLine();
                bw.write("uri:\""+uri+"\"");
                bw.newLine();
                bw.write("confidence:"+confidence);
                bw.newLine();
                bw.write("atleast input"+input_Text);
                bw.newLine();
                bw.write("},");
                bw.newLine();
                bw.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
           
        }
      System.out.println("\n"+document);
      String output = FileUtils.readFileToString(file);
      System.out.println("FOUND Events:");
      System.out.println(output);

    	return document;
    }
    
}
