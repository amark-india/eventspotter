package fr.eurecom.eventspotter.worker;

import fr.eurecom.eventspotter.caslight.Feature;
import fr.eurecom.eventspotter.caslight.FeatureStructure;
import fr.eurecom.eventspotter.worker.helpers.CosineSimilarity;
import fr.eurecom.eventspotter.worker.helpers.DBAdapter;
import fr.eurecom.eventspotter.worker.helpers.SpotPhrases;
import fr.eurecom.eventspotter.worker.helpers.Tokenizer;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * eventspotter. 
 * 
 * 
 * 
 */
public class EventSpotterLight {

    String dataFilePath;
    int maxWordLength;
    SpotPhrases spotPhrases;
    //final Logger logger = LoggerFactory.getLogger(this.getClass());
    DBAdapter dbAdapter;

    /**
     * This class spots event occurrences in String.
     * 
     * @param dataFilePath file path of titles to pre-load by SpotPhrases
     * @param dbDriver database driver name (database for author-title relations)
     * @param dbPath database jdbc path
     * @param dbUser database user
     * @param dbPasswd database password
     * @param maxWordLenght max word lenght in tokens
     */
    public EventSpotterLight(String dataFilePath, String dbDriver, String dbPath, String dbUser, String dbPasswd, int maxWordLenght) {
        this.dataFilePath = dataFilePath;
        this.maxWordLength = maxWordLenght;
        this.spotPhrases = new SpotPhrases(new File(dataFilePath), maxWordLength, "eventSpotter");
        this.dbAdapter = new DBAdapter(dbPath, dbUser, dbPasswd, dbDriver);
    }

    /**
     * This function spots event occurrences. 
     * 
     * First, it tokenizes the input string and looks for title occurrences in it.
     * After that, it looks up the corresponding authors and tries to identify them as well on the input. 
     * The more tokens of the auhors' string is identified, the bigger confidence is given. 
     * 
     * @param input - The input text as string
     * @return a list of event title matches in CasLight FeatureStructure
     */
    public List<FeatureStructure> spotevents(String input) {
        //logger.info("Running eventspotterlight...");
        List<FeatureStructure> tokens = Tokenizer.tokenize(input, false);
        List<FeatureStructure> sentences = Tokenizer.sentensize(input);
        List<FeatureStructure> ret = new ArrayList<FeatureStructure>();
        
        List<FeatureStructure> candidates = spotPhrases.doSpotting(input, tokens, spotPhrases.entries);
        for (FeatureStructure fs : candidates) {
            boolean add = false;
            String origSofaChunk = fs.getSofaChunk(input);
          //  logger.info("sofachunk: " + origSofaChunk);
            if (!Character.isUpperCase(origSofaChunk.charAt(0)) && !Character.isDigit(origSofaChunk.charAt(0))) {
                //logger.info("Not starting with upper case or digit !");
                continue;
            }
            if (fs.getFeature("eventId") == null) {
                continue;
            }

            String eventId = fs.getFeature("eventId").getValueAsString();
            if (eventId.isEmpty()) {
                continue;
            }

            HashMap<String, String> entries = new HashMap<String, String>();

            
            String hyphen = "-";  
		    StringBuffer str = new StringBuffer (eventId);  
		    str.insert (8, hyphen); 
		    str.insert (13, hyphen); 
		    str.insert (18, hyphen); 
		    str.insert (23, hyphen); 
		   
		    eventId = str.toString();
		    eventId = "http://data.linkedevents.org/event/" +eventId;
            
            fs.getFeature("eventId").setValue(eventId);

            if (fs.getFeature("title") != null) {
                fs.getFeature("title").setValue(dbAdapter.getTitle(eventId));
           //     logger.info("Re-Setting eventTitle:" + fs.toString());
            }
            Feature type = new Feature("type","null");
            fs.addFeature(type);
            
                fs.getFeature("type").setValue(dbAdapter.getType(eventId));
                
            


            List<String> agents = dbAdapter.getAgents(eventId);
            int agentTokenNum = 0;
            if (agents == null) {
                continue;
            }


            for (String agent : agents) {
                if (agent.isEmpty()) {
                    continue;
                }
                List<FeatureStructure> agentTokens = Tokenizer.tokenize(agent, false);
                StringBuilder agentString = new StringBuilder();
                if (agentTokens.size() > maxWordLength) {
                    continue;
                }
                for (FeatureStructure aFs : agentTokens) {
                    if (aFs.getCoveredText() == null) {
                        continue;
                    }
                    if (aFs.getCoveredText().length() >= 3 && Character.isUpperCase(aFs.getCoveredText().charAt(0))) {
                        entries.put(aFs.getCoveredText().toLowerCase(), agent);
                        agentTokenNum++;
                        if (agentString.length() > 0) {
                            agentString.append(' ');
                        }
                        agentString.append(aFs.getCoveredText().toLowerCase());
                    }
                }
                agentTokenNum++;
                entries.put(agentString.toString(), agent);
            }

            if (agentTokenNum > 0) {
                List<FeatureStructure> foundAgentToks = spotPhrases.doSpotting(input, tokens, entries);

                boolean enoughAgents = false;

                Set uniqueAgentToks = new HashSet<String>();
                Set uniqueUpperAgentToks = new HashSet<String>();


                for (FeatureStructure fsAT : foundAgentToks) {
                    if (Character.isUpperCase(fsAT.getSofaChunk(input).charAt(0))) {
                        uniqueUpperAgentToks.add(fsAT.getCoveredText());
                    }
                    uniqueAgentToks.add(fsAT.getCoveredText());
                }
                
                
                  String Surround = spotPhrases.getSurrounding(sentences,fs);
                  fs.addFeature(new Feature<String>("Surrounding",Surround));
                
                
                  try {
                 	 CosineSimilarity csi = new CosineSimilarity();
                 	 double confidence=0.0;
                 	 enoughAgents = true;
                 	 System.out.print(Surround);
                 	 String doc1=Surround;
                 	 if(doc1.isEmpty())
                 	 doc1=input;
                 	 String doc2=dbAdapter.getDesc(eventId);
                 	 if(doc2.isEmpty())
                 	 {
                 		 
                 		 fs.addFeature(new Feature<Double>("confidence",confidence ));
                 		 
                 	 } 
                 	 else
                 	 {
                 	  confidence=csi.run(doc1,doc2);
                 	 fs.addFeature(new Feature<Double>("confidence",confidence ));
                 	 }
                  } catch (IOException e) {
                      // TODO Auto-generated catch block
                      e.printStackTrace();
                  }

             /*  int uSize = uniqueAgentToks.size();
                int uUSize = uniqueUpperAgentToks.size();
               // logger.debug("agentTokenNum:" + agentTokenNum + " found unique AgentToks:" + uSize);

                if (uSize >= agentTokenNum) {
               //     logger.info(uSize + " of " + agentTokenNum + " upper case agent tokens found. Marking with high confidence.");
                    enoughAgents = true;
                    fs.addFeature(new Feature<Double>("confidence", 0.7));
                } else if (uUSize > 0) {
                   // logger.info(uSize + " of " + agentTokenNum + " upper case agent tokens found. Marking with confidence 0.1*<found tokens>");
                    enoughAgents = true;
                    fs.addFeature(new Feature<Double>("confidence", uUSize * 0.1));
                }
*/

                
                if (enoughAgents) {
                    StringBuilder allAgents = new StringBuilder();
                    for (String agent : agents) {
                        if (allAgents.length() > 0) {
                            allAgents.append(';');
                        }
                        allAgents.append(agent);
                    }
                    String agentStr = allAgents.toString();
                    fs.addFeature(new Feature<String>("agent", agentStr));
                    //logger.info("agents:"+agentStr+add);
                    //logger.info("Added to result:" + fs.toString());
                    if (agentStr.equalsIgnoreCase(origSofaChunk)) {
                       // continue;
                    //	logger.info("agents:"+agentStr);
                    }
                    // at least one agent found
                    add = true;
                }
            }

            if (add) {
                ret.add(fs);
            }
        }

        return ret;
    }
}
