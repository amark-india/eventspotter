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
                String[] validate_array = evSpotter.start_spotter(document);            
                StringBuilder new_document=new StringBuilder();
                if(option == "evaluate_yes") 
                {
                	File validate_file = new File("validate.txt");
                	FileWriter fweval = new FileWriter(validate_file.getAbsoluteFile(),true);
                	BufferedWriter bweval = new BufferedWriter(fweval);
                	for(String s : validate_array)
                	{
                		if (s.contains("/EVENT"))
                		{	
                			s = s.replace("/","\t/");
                			s = s+" \n";
                			new_document=new_document.append(s);
                			
                			//System.out.println(new_document);
                				continue;        		
                		}
                		else
                		{
                			s = s + "\t/O\n";
                		new_document=new_document.append(s);
                		
                		}
                		
                	}
                	String validate_output=new_document.toString();
                	bweval.write(validate_output);
                	bweval.close();
                }
            }
            catch (IOException e) 
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
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
                formatter.printHelp( "eventspotter.jar --in=<document_to_annotate> --out=<annotated_document>", options );              
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
