package fr.eurecom.eventspotter;

import java.io.File;
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
        Boolean option = readOptions(options, args);

        // perform the extraction
        if(option) {
                //read file
            try {
                String document = FileUtils.readFileToString(new File(inFile));
                EventSpotter evSpotter = new EventSpotter();
                evSpotter.start_spotter(document);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
    private static Boolean readOptions(Options options, String[] args) 
    {
        CommandLineParser parser = new PosixParser();

        Boolean option = false;
        try {
            // parse the command line arguments
            CommandLine line = parser.parse( options, args );

            if(line.hasOption("in") &&
               line.hasOption("out") ) 
            {
                inFile =  line.getOptionValue( "in" );
                outFile = line.getOptionValue( "out" );
                option = true;
            }

            if (line.hasOption("help") || !option) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp( "eventspotter.jar --in <document_to_annotate> --out <annotated_document>", options );               
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
        return options;
    }

}
