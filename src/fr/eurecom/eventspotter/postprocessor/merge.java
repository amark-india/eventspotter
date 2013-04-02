/**
 * 
 */
package fr.eurecom.eventspotter.postprocessor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

public class merge {
    
    private static String inFile = null;
    private static String outFile = null;
    private static String anotFile = null;

    public static void main(String[] args) 
    {
        // define list of options
        Options options = initOptions();

        // read options
        Boolean option = readOptions(options, args);
        String line1 = new String();
        String line2 = new String();
        String [] linelist;

        // perform the extraction
        if(option)
        {
                //read file
            try 
            {
                LineIterator valid_opt = FileUtils.lineIterator(new File(inFile));
            
                LineIterator gs = FileUtils.lineIterator(new File(anotFile));
                
                File file = new File(outFile);
                 try{
                        // if file doesnt exist, then create it
                    	if (file.exists()) {
                    		file.delete();
                        }
                    	file.createNewFile();

                    } catch (IOException e) {
                        e.printStackTrace();
                    
                } 
                FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
                BufferedWriter bw = new BufferedWriter(fw);
                
                while(valid_opt.hasNext()&&gs.hasNext())
                {
                    line1=valid_opt.nextLine();
                    line2=gs.nextLine();
                    linelist=line1.split("\t");
                    line2=line2+" "+linelist[1]+"\n";
                    bw.write(line2);
                }
                bw.close();
                
            }
            
            catch (IOException e) 
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        //perform evaluation
            }
    
    private static Boolean readOptions(Options options, String[] args)
    {
        CommandLineParser parser = new PosixParser();

        Boolean option = false;
        try {
            // parse the command line arguments
            CommandLine line = parser.parse( options, args );

            if(line.hasOption("in") &&
               line.hasOption("out") && line.hasOption("anot") )
            {
                inFile =  line.getOptionValue( "in" );
                anotFile = line.getOptionValue( "anot" );
                outFile = line.getOptionValue( "out" );
                option = true;
                System.out.println("done!");
            }
            

            if (line.hasOption("help") || !option)
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
        options.addOption( "I", "in", true, "input file");
        options.addOption( "A", "anot", true, "manually annotated file");
        options.addOption( "O", "out", true, "output file");
        options.addOption( "H", "help", false, "print the help" );
        return options;
    }

}

