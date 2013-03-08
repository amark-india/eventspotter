package fr.eurecom.eventspotter.worker.helpers;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
     StringBuffer Surround = new StringBuffer();
   	 int event_beg= fs.getFeature("begin").getValueAsInteger();
   	 int event_end= fs.getFeature("end").getValueAsInteger();
        
      /* int low=0; 
        int high =sentences.size()-1;
        int mid=0;
        boolean found= false;
         while(low<=high)
         {
        	 
        	 mid=(low+high)/2;
        	 int mid_pos=sentences.get(mid).getFeature("begin").getValueAsInteger();
        	 int mid_end=sentences.get(mid).getFeature("end").getValueAsInteger();
        	 
        	 if(event_beg<mid_pos)
        	 {
        		 high=mid;
        		 
        	 }
        	 if(mid_pos<=event_beg && event_end<=mid_end)
        	 {
        		found=true;
        		 break;
        	 }
        	 
        	 if(event_beg>mid_end)
        	 {
        		 low=mid;
        	 }
         }
         if(found)
         {
        	 if(mid>0 && mid<sentences.size()-1)
        	 {
        	 Surround.append(sentences.get(mid-1).getCoveredText());
        	 Surround.append(sentences.get(mid).getCoveredText());
        	 Surround.append(sentences.get(mid+1).getCoveredText());
        	 }
        	 if(mid==0)
        	 {
        		 Surround.append(sentences.get(mid).getCoveredText());
            	 Surround.append(sentences.get(mid+1).getCoveredText());
            	 Surround.append(sentences.get(mid+2).getCoveredText());
        	 }
        	 if(mid==sentences.size()-1)
        	 {
            	 Surround.append(sentences.get(mid-2).getCoveredText());
            	 Surround.append(sentences.get(mid-1).getCoveredText());
            	 Surround.append(sentences.get(mid).getCoveredText());
        	 }
         }
   	 */
   	 for(int i=0; i<sentences.size() ; i++)
   	 {
   		 //System.out.println("checkoooooooo:"+i);

   		int start_pos=sentences.get(i).getFeature("begin").getValueAsInteger();


   	 int end_pos=sentences.get(i).getFeature("end").getValueAsInteger();
		 System.out.println("start:"+start_pos+"end:"+end_pos);
   	 if(start_pos<=event_beg && event_end<=end_pos)
	 {
   		 //System.out.println("heeree:"+i);
   	 	 if(i==0)
    	 {
    		 Surround.append(sentences.get(i).getCoveredText());
        	 Surround.append(sentences.get(i+1).getCoveredText());
        	 Surround.append(sentences.get(i+2).getCoveredText());
    	 }
        	 if(i>0 && i<sentences.size()-1)
        	 {
        	 Surround.append(sentences.get(i-1).getCoveredText());
        	 Surround.append(sentences.get(i).getCoveredText());
        	 Surround.append(sentences.get(i+1).getCoveredText());
        	 }
       
        	 if(i==sentences.size()-1)
        	 {
        		 Surround.append(sentences.get(i-2).getCoveredText());
        		 Surround.append(sentences.get(i-1).getCoveredText());
            	 Surround.append(sentences.get(i).getCoveredText());
        	 }
         }
   	 }
         //System.out.println("here:"+Surround.toString());
         return Surround.toString();
    
    }
    }

