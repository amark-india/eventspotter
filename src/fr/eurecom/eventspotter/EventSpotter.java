package fr.eurecom.eventspotter;

import java.io.BufferedWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.collections.list.SetUniqueList;
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
   


    public String[] start_spotter(String document) throws IOException
    {
    	StringBuilder new_document =new StringBuilder();
        this.titlesFilePath = "/opt/event-titles.list";
        this.dbPath = "jdbc:mysql://localhost/eventspotter";
        this.dbDriver = "com.mysql.jdbc.Driver";
        this.dbUser = "root";
        this.dbPassword = "root";
        this.maxTitleLength = 10;
        this.eventspotter = new EventSpotterLight(titlesFilePath, dbDriver, dbPath, dbUser, dbPassword, maxTitleLength);
        //logger.info("EventSpotter initiated with titles file:" + titlesFilePath);

        //logger.info("Running eventSpotter...");
        Set<FeatureStructure> spottedevents = eventspotter.spotevents(document);
        //logger.info("Processing spotted events(" + spottedevents.size() + ")");
        File file = new File("output.txt");
        File eval_file = new File("validate.txt");
        try{
            // if file doesnt exist, then create it
        	if (file.exists()) {
        		file.delete();
            }
        	file.createNewFile();

        } catch (IOException e) {
            e.printStackTrace();
        }
        //String output=document;
        Set<FeatureStructure> s = new HashSet<FeatureStructure>(spottedevents);
        for (FeatureStructure fs : s) 
        {
            //   logger.info("FOUND Event:" + fs.toString());
        	
            
        	
            String label= fs.getFeature("title").getValueAsString();
            String new_label= label.replace(" ", "/EVENT ");
            new_label=new_label+"/EVENT";
            if(!document.contains(new_label))
            	document=document.replaceAll(label,new_label);
            document=document.replaceAll("/EVENT ","/EVENT");
            document=document.replaceAll("/EVENT","/EVENT ");
            
            int startChar = fs.getFeature("begin").getValueAsInteger();
            int endChar = fs.getFeature("end").getValueAsInteger();
            
            String replaceevent = document.substring(startChar, endChar);
            
            String type = fs.getFeature("type").getValueAsString();
            String uri = fs.getFeature("eventId").getValueAsString();
            String confidence =fs.getFeature("confidence").getValueAsString();
            String surrounding =fs.getFeature("Surrounding").getValueAsString();
            //document.replaceAll("[^new_label]", "/0");

 /*           System.out.println("{");
            System.out.println("label:"+label);
            System.out.println("startChar:"+startChar);
            System.out.println("endChar:"+endChar);
            System.out.println("type:"+type);
            System.out.println("uri:"+uri);
            System.out.println("confidence:"+confidence);
            System.out.println("Surrounding:"+surrounding);

            System.out.println("},");
*/
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
                bw.write("type:\""+type+"\"");
                bw.newLine();
                bw.write("uri:\""+uri+"\"");
                bw.newLine();
                bw.write("confidence:"+confidence);
                bw.newLine();
                bw.write("surrounding:"+surrounding);
                bw.newLine();
                bw.write("},");
                bw.newLine();
                bw.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
           
        }
        String [] tex=new String[20];
        
        tex=document.split("[\\s|,|.]");

      System.out.println("\n"+document);
      String output = FileUtils.readFileToString(file);
      System.out.println("FOUND Events:");
      System.out.println(output);

    	return tex;
    }
    
}
