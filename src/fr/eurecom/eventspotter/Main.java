package fr.eurecom.eventspotter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

public class Main {
    
    private static String inFile = null;
    private static String outFile = null;
    
    public static void main(String[] args) 
    {
        // define list of options
        Options options = initOptions();

        // read options
        String option = readOptions(options, args);

        // perform the extraction
        if(option == "evaluate_no" || option == "evaluate_yes") 
        {
                //read file
            try 
            {
                String document = FileUtils.readFileToString(new File(inFile));
                EventSpotter evSpotter = new EventSpotter();
                String Annot_str = evSpotter.start_spotter(document);      
               
            //    StringBuilder new_document=new StringBuilder();
                if(option == "evaluate_yes") 
                {
                	File validate_file = new File("interim.txt");
                	FileWriter fweval = new FileWriter(validate_file.getAbsoluteFile(),true);
                	BufferedWriter bweval = new BufferedWriter(fweval);
                	bweval.write(Annot_str);
                	bweval.close();
                	/*tokenize now starts*/
           		 File output = new File("tokenized.txt");
           		 String[] command = { "/usr/lib/jvm/java-7-openjdk-amd64/bin/java", "-jar", "ptb.jar","interim.txt"};
           		 ProcessBuilder pb = new ProcessBuilder(command);
           		 pb.redirectOutput(output);
           		Process proc = pb.start();
           		proc.waitFor();
           		 //After tokenize label in conll format
           	  String line1= new String();
              String modified_line= new String();
              String inFile="tokenized.txt";
              String outFile="es.conll";
              int subevent=0;
              boolean flag = true;
              LineIterator valid_opt = FileUtils.lineIterator(new File(inFile));
              File file = new File(outFile);
     
              try{
                     // if file doesnt exist, then create it
                 	if (file.exists()) {
                 		file.delete();
                     }
                 	file.createNewFile();

                 } catch (IOException e) {
                     e.printStackTrace();
                 System.out.println(e.getMessage());
             } 
              FileWriter fwj = new FileWriter(file.getAbsoluteFile(),true);
              BufferedWriter bwj = new BufferedWriter(fwj);
          	  
             while(valid_opt.hasNext())
              {
            	 line1=valid_opt.nextLine();
              if(line1.contains("<EVENT>"))
              {
            	  ++subevent;
            	  continue;
              }
              if(line1.contains("</EVENT>"))
              {
            	  --subevent;
            	  continue;
              }

              if(subevent>0)
              {
            	modified_line= line1+ "\t" + "I-EVENT"+"\n";
        		bwj.write(modified_line);
              }  
              
              if(subevent==0)
              {
            	  bwj.write(line1+"\t"+"O\n");
              	 
              }
      /*    		if(line1.contains("<EVENT>"))
          		{
          	        line1=valid_opt.nextLine();

              	do{

              		if(line1.contains("<EVENT>"))
              		{
              			subevent++;
              			line1=valid_opt.nextLine();
              			continue;
              		}
              		if(subevent!=0 && line1.contains("</EVENT>"))
              		{
              			subevent--;
              			line1=valid_opt.nextLine();
              			continue;
              		}
              
              		modified_line= line1+ "\t" + "I-EVENT"+"\n";
              		bwj.write(modified_line);
          			line1=valid_opt.nextLine();
          			if(subevent==0 && line1.contains("</EVENT>"))
          				flag= false;
          			}while(flag);
              	continue;
              }
          		else
          		{
          			bwj.write(line1+"\t"+"O\n");
          		}
              	*/
              }
              bwj.close();
                       	
                }
            }
            catch (IOException e) 
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }
        
        //perform evaluation
            }
    
    private static String readOptions(Options options, String[] args)
    {
        CommandLineParser parser = new PosixParser();

        String option = "false";
        try {
            // parse the command line arguments
            CommandLine line = parser.parse( options, args );

            if(line.hasOption("in") &&
               line.hasOption("out") && !line.hasOption("eval") )
            {
                inFile =  line.getOptionValue( "in" );
                outFile = line.getOptionValue( "out" );
                option = "evaluate_no";
            }
            
            if(line.hasOption("in") &&
                    line.hasOption("out") && line.hasOption("eval") )
                 {
                     inFile =  line.getOptionValue( "in" );
                     outFile = line.getOptionValue( "out" );
                     option = "evaluate_yes";
                 }

            if (line.hasOption("help") || ((option!="evaluate_no")&&(option!="evaluate_yes")) )
            {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp( "eventspotter.jar --in=<document_to_annotate> --out=<annotated_document> --eval<for generating conll output>", options );              
            }
            
        }
        catch( ParseException exp ) {
            System.out.println( "Unexpected exception:" + exp.getMessage() );
        }

        return option;
    }
  


    private static Options initOptions()
    {
        // create the Options
        Options options = new Options();
        options.addOption( "I", "in", true, "input file" );
        options.addOption( "O", "out", true, "annotated outfile");
        options.addOption( "H", "help", false, "print the help" );
        options.addOption( "E", "eval", false, "evaluate the eventspotter" );
        return options;
    }

}
