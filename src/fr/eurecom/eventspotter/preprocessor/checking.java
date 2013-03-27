package fr.eurecom.eventspotter.preprocessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class checking {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		 String s = null;

	        try {
	            
		    // run the Unix "ps -ef" command
	            // using the Runtime exec method:
	            //Process p = Runtime.getRuntime().exec("pwd");
	        	//Process l = Runtime.getRuntime().exec("cd workspace/stanford-parser-2012-11-12/");
	        //	BufferedReader stdInput1 = new BufferedReader(new 
		   //              InputStreamReader(l.getInputStream()));
//
		   //         BufferedReader stdError1 = new BufferedReader(new 
		   //              InputStreamReader(l.getErrorStream()));
		            
	        	Process p = Runtime.getRuntime().exec("java -cp home/meghana/workspace/stanford-parser-2012-11-12/stanford-parser.jar " +
	        										  "home/meghana/workspace/stanford-parser-2012-11-12/stanford-parser-2.0.4-sources/edu.stanford.nlp.process.PTBTokenizer " +
	        		        			              "home/workspace/stanford-parser-2012-11-12/data/testsent.txt > home/meghana/workspace/stanford-parser-2012-11-12/data/output.txt");
	            BufferedReader stdInput2 = new BufferedReader(new 
	                 InputStreamReader(p.getInputStream()));

	            BufferedReader stdError2 = new BufferedReader(new 
	                 InputStreamReader(p.getErrorStream()));

	            // read the output from the command
	            System.out.println("Here is the standard output of the command:\n");
	            while ((s = stdInput2.readLine()) != null) {
	                System.out.println(s);
	            }
	            
	            // read any errors from the attempted command
	            System.out.println("Here is the standard error of the command (if any):\n");
	            while ((s = stdError2.readLine()) != null) {
	                System.out.println(s);
	            }
	            
	            System.exit(0);
	        }
	        catch (IOException e) {
	            System.out.println("exception happened - here's what I know: ");
	            e.printStackTrace();
	            System.exit(-1);
	        }


			}

}
