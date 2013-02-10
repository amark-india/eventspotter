package fr.eurecom.eventspotter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import fr.eurecom.eventspotter.caslight.FeatureStructure;
import fr.eurecom.eventspotter.worker.EventSpotterLight;


public class EventSpotter
{
    private String titlesFilePath;
    private int maxTitleLength;
    // private Set<String> SUPPORTED_MIMETYPES = Collections.unmodifiableSet(new HashSet<String>(
    //     Arrays.asList("text/plain")));
    // public static final Integer defaultOrder = ServiceProperties.ORDERING_PRE_PROCESSING;
    private EventSpotterLight eventspotter;
    //  final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());
    private String dbPath;
    private String dbDriver;
    private String dbUser;
    private String dbPassword;


    public void start_spotter(String document) throws IOException
    {
        this.titlesFilePath = "/opt/event-titles.list";
        this.dbPath = "jdbc:mysql://localhost/eventspotter";
        this.dbDriver = "com.mysql.jdbc.Driver";
        this.dbUser = "root";
        this.dbPassword = "root";
        this.maxTitleLength = 10;
        this.eventspotter = new EventSpotterLight(titlesFilePath, dbDriver, dbPath, dbUser, dbPassword, maxTitleLength);
        //logger.info("EventSpotter initiated with titles file:" + titlesFilePath);

        //logger.info("Running eventSpotter...");
        List<FeatureStructure> spottedevents = eventspotter.spotevents(document);
        //logger.info("Processing spotted events(" + spottedevents.size() + ")");
        File file = new File("/home/amark/project/output.txt");
        try{
            // if file doesnt exist, then create it
            if (!file.exists()) {
                file.createNewFile();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        for (FeatureStructure fs : spottedevents) {
            //   logger.info("FOUND Event:" + fs.toString());

            System.out.println("FOUND Event:");

            String label= fs.getFeature("title").getValueAsString();
            String startChar = fs.getFeature("begin").getValueAsString();
            String endChar = fs.getFeature("end").getValueAsString();
            String type = fs.getFeature("type").getValueAsString();
            String uri = fs.getFeature("eventId").getValueAsString();
            String confidence =fs.getFeature("confidence").getValueAsString();
            String surrounding =fs.getFeature("Surrounding").getValueAsString();

            System.out.println("{");
            System.out.println("label:"+label);
            System.out.println("startChar:"+startChar);
            System.out.println("endChar:"+endChar);
            System.out.println("type:"+type);
            System.out.println("uri:"+uri);
            System.out.println("confidence:"+confidence);
            System.out.println("Surrounding:"+surrounding);

            System.out.println("},");

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

            /*` MGraph metadata = ci.getMetadata();
            String uriRefStr = "eventspotter.sztaki.hu:eventOccurrence";

            metadata.add(new TripleImpl(textAnnotation, DC_TYPE, new UriRef(uriRefStr)));

            if (fs.getFeature("begin") != null) {
                metadata.add(new TripleImpl(textAnnotation, ENHANCER_START,
                        literalFactory.createTypedLiteral(fs.getFeature("begin").getValueAsInteger())));
            }
            if (fs.getFeature("end") != null) {
                metadata.add(new TripleImpl(textAnnotation, ENHANCER_END,
                        literalFactory.createTypedLiteral(fs.getFeature("end").getValueAsInteger())));
            }
            if (fs.getCoveredText() != null && !fs.getCoveredText().isEmpty()) {
                metadata.add(new TripleImpl(textAnnotation, ENHANCER_SELECTED_TEXT, new PlainLiteralImpl(fs.getCoveredText())));
            }

            UriRef entityAnnotation = EnhancementEngineHelper.createEntityEnhancement(ci, this);

            //type
            metadata.add(new TripleImpl(entityAnnotation, ENHANCER_ENTITY_TYPE, new UriRef("http://dbpedia.org/ontology/event")));
            metadata.add(new TripleImpl(entityAnnotation, ENHANCER_ENTITY_TYPE, new UriRef("http://purl.org/ontology/bibo/event")));
            //label
            String author = "";
            if (fs.getFeature("author") != null) {
                author = fs.getFeature("author").getValueAsString();
            }
            String title = "";
            if (fs.getFeature("title") != null) {
                title = fs.getFeature("title").getValueAsString();
            }

            if (fs.getFeature("confidence") != null) {
                double confidence = (Double)fs.getFeature("confidence").getValue();
                metadata.add(new TripleImpl(entityAnnotation, ENHANCER_CONFIDENCE, literalFactory.createTypedLiteral(confidence)));
            }

            metadata.add(new TripleImpl(entityAnnotation, ENHANCER_ENTITY_LABEL, new PlainLiteralImpl(author + " : " + title)));
            //reference
            if (fs.getFeature("workId") != null) {

                String workId = fs.getFeature("workId").getValueAsString();
                if (workId.startsWith("/works")) {
                    workId = "http://openlibrary.org"+workId;
                }
                metadata.add(new TripleImpl(entityAnnotation, ENHANCER_ENTITY_REFERENCE, new UriRef(workId)));
            }
            //link to the mention

           metadata.add(new TripleImpl(entityAnnotation, DC_RELATION, textAnnotation));
             */
        }
    }
}
