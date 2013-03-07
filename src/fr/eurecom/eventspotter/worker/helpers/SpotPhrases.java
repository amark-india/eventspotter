package fr.eurecom.eventspotter.worker.helpers;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import fr.eurecom.eventspotter.caslight.Feature;
import fr.eurecom.eventspotter.caslight.FeatureStructure;


/**
 * Spots pre-loaded phrase occurrences in longer texts.
 *
 */
public class SpotPhrases {

   // final Logger logger = LoggerFactory.getLogger(this.getClass());
    public HashMap<String, String> entries = new HashMap<String, String>();
    private int maxWordLength = 3;
    private String type;

    
    /**
     * Loads the phares to spot form a file.
     * The format of the file: the first token (boundaries:whitespaces) is an identifier, the rest are the phrase to identify,
     * 
     * @param dataFile - the file with the data to pre-load
     * @param maxWordLength - the max word lenght in tokens to handle
     * @param type - a type identifier to associate with the found occurrences.
     */
    public SpotPhrases(File dataFile, int maxWordLength, String type) {
        this.maxWordLength = maxWordLength;
        this.type = type;

        FileInputStream fstream = null;
        try {
            fstream = new FileInputStream(dataFile);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            int i = 0;
            long nanoStart = System.nanoTime();
            long nanoLast = nanoStart;
            while ((strLine = br.readLine()) != null) {
                List<FeatureStructure> lineparts = Tokenizer.tokenize(strLine, false);
                if (lineparts.size() < 2) {
               //     logger.info("Line skipped (only one token):" + strLine);
                    continue;
                } else if (lineparts.size() == 2) {
                    entries.put(lineparts.get(1).getCoveredText().toLowerCase(), lineparts.get(0).getCoveredText());
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append(lineparts.get(1).getCoveredText().toLowerCase());
                    for (int x = 2; x < lineparts.size(); x++) {
                        sb.append(' ');
                        sb.append(lineparts.get(x).getCoveredText().toLowerCase());
                    }
                    entries.put(sb.toString(), lineparts.get(0).getCoveredText());
                }

                if ((i % 100000) == 0) {
                    long nanoNow = System.nanoTime();
                  //  logger.info("Inserting entry " + i + ". The processed line:" 
                         //   + strLine + ". This etap took " 
                           // + NanoTime.getTimeElapsed(nanoLast, nanoNow) + " s."
                            //+ " Total elapsed: "+NanoTime.getTimeElapsed(nanoStart, nanoNow)+" s.");
                    nanoLast = nanoNow;
                }
                i++;
            }
            long nanoEnd = System.nanoTime();
      //      logger.info("Lines loaded in: " + NanoTime.getTimeElapsed(nanoStart, nanoEnd) + "s");
            in.close();
            fstream.close();
        } catch (Exception ex) {
     //       logger.error("Exception during MemCatalog load", ex);
        }

    }

    
    private List<FeatureStructure> test(String sofa, List<FeatureStructure> fsList, int position, HashMap<String, String> entries) {
        List<FeatureStructure> matches = new ArrayList<FeatureStructure>();
        StringBuilder sb = new StringBuilder();

        FeatureStructure fsF = fsList.get(position);
        int bf = fsF.getFeature("begin").getValueAsInteger();
        int ef;

        int i = 0;
        boolean more = true;
        while (more && i < maxWordLength) {
            if (position + i < fsList.size()) {
                FeatureStructure fs = fsList.get(position + i);
                int end = (Integer) fs.getFeature("end").getValue();
                String token = fs.getCoveredText();
                if (i > 0) {
                    sb.append(" ");
                }
                sb.append(token);
                String toCheck;
                toCheck = sb.toString().toLowerCase();
           //     logger.debug("Checking:" + toCheck);
             //   logger.info("Checking:"+toCheck);
               

                if (entries.containsKey(toCheck)) {
          //      	logger.info("inside if loop");
                    ef = end;
                    String id = entries.get(toCheck);
                    FeatureStructure m = new FeatureStructure(UUID.randomUUID().toString(), type);
                    m.addFeature(new Feature<String>("eventId", id));
                    m.addFeature(new Feature<String>("title", toCheck));
                    m.addFeature(new Feature<Integer>("begin", bf));
                    m.addFeature(new Feature<Integer>("end", ef));
                    m.setCoveredText(m.getSofaChunk(sofa));

                    matches.add(m);
            //        logger.info("Match!:" + toCheck);
                }
            }
            i++;
        }
        if (matches.size() > 0) {
            return matches;
        } else {
            return null;
        }
    }

    /**
     * Returns the list of occurrences on a given input token list. 
     * 
     * @param input The input string
     * @param tokens A list of input tokens
     * @param entries A list of entry set to use. Typically this.entries
     * @return the list of found occurrences.
     */
    
    public List<FeatureStructure> doSpotting(String input, List<FeatureStructure> tokens, HashMap<String, String> entries) {
        List<FeatureStructure> fsRes = new ArrayList<FeatureStructure>();

       //logger.info("Looking for phrases on the " + tokens.size() + " tokens of the text");
        for (int i = 0; i < tokens.size(); i++) {
            List<FeatureStructure> fsAtPos = test(input, tokens, i, entries);
            if (fsAtPos != null) {
                fsRes.addAll(fsAtPos);
            }
        }
        //logger.debug("Spotter returning " + fsRes.size() + " hits");
        return fsRes;
    }
    
    public String getSurrounding(List<FeatureStructure> sentences,FeatureStructure fs)
    {

       // boolean enoughAgents = false;

        String Prevline=new String();
        String Curline=new String();
        String Nextline=new String();
        int curpos=0;
        StringBuffer Surround = new StringBuffer();
         for(FeatureStructure sen: sentences)
         { 
        	 int sent_beg= sen.getFeature("begin").getValueAsInteger();
        	 int event_beg= fs.getFeature("begin").getValueAsInteger();
        	 int sent_end= sen.getFeature("end").getValueAsInteger();
        	 if((sent_beg<event_beg)&&(event_beg<sent_end))
        	 {
        		curpos=sen.getFeature("number").getValueAsInteger();
        		
      
             }
         }
         int last_sent=sentences.size();
         for(FeatureStructure sen: sentences)
         {
        	
        	 int sent_count=sen.getFeature("number").getValueAsInteger();
        	 if(curpos!=1 && curpos!=last_sent)                	
        	 {
        		 if(sent_count== (curpos-1))
        		 {
        			 Prevline=sen.getCoveredText();
        			 Surround.append(Prevline);
        		 }
        		 if(sent_count==curpos)
        		 {
        			 Curline=sen.getCoveredText();
        			 Surround.append(Curline);
        		 }
        	 
        		 if(sent_count==(curpos+1))
        		 {
        			 Nextline=sen.getCoveredText();
        			 Surround.append(Nextline);
        		 }
        	 }
        	 
             if(curpos==1)
             {
            	 if(sent_count==curpos)
            	 {
            		 Curline=sen.getCoveredText();
            		 Surround.append(Curline);
            	 }
            	 
            	 if(sent_count==(curpos+1))
            	 {
            		 Nextline=sen.getCoveredText();
            		 Surround.append(Nextline);
            	 }
            	 
             }
             if(curpos==last_sent)
             {
             	
            		 if(sent_count== (curpos-1))
                	 {
                		 Prevline=sen.getCoveredText();
                		 Surround.append(Prevline);
                	 }
                	 if(sent_count==curpos)
                	 {
                		 Curline=sen.getCoveredText();
                		 Surround.append(Curline);
                	 }
             }

        	
        }
         System.out.println("here:"+Surround.toString());
         return Surround.toString();
    }
    
    
    }

