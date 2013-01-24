package fr.eurecom.eventspotter.worker.helpers;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.linear.OpenMapRealVector;
import org.apache.commons.math3.linear.RealVectorFormat;
import org.apache.commons.math3.linear.SparseRealVector;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class CosineSimilarity {
    public String[] s=new String[2];
    
  public static void main(String[] args) {
        CosineSimilarity cs = new CosineSimilarity();
        try {
        	String doc1=" Cervantes’ Masterpiece and KGNU Present Take It To The Bridge Festival ft. The Motet, Black Uhuru, See-I (Featuring 6 Members of Thievery Corporation) and That 1 Guy w/ Euforquestra, Dubskin, Nicki Bluhm and the Gramblers, Mikey Thunder, Bedrockk, Atomga, Jaden Carlson Trio, Rally Round The Family (Rage Against The Machine Tribute), She Said String Band, Jonah And The Whales and Your Babies Daddy <br> Saturday, July, 21 at <a href=\"\"http://www.cervantesmasterpiece.com/venue/detail/state-bridge-lodge\"\" rel=\"\"nofollow\"\">State Bridge Lodge</a> <br> 2:30 PM (2:00 PM doors) / 21+ w/ Valid ID ";
            String doc2=" Cervantes’ Masterpiece and KGNU Present Take It To The Bridge Festival ft. The Motet, Black Uhuru, See-I (Featuring 6 Members of Thievery Corporation) and That 1 Guy w/ Euforquestra, Dubskin, Nicki Bluhm and the Gramblers, Mikey Thunder, Bedrockk, Atomga, Jaden Carlson Trio, Rally Round The Family (Rage Against The Machine Tribute), She Said String Band, Jonah And The Whales and Your Babies Daddy <br> Saturday, July, 21 at <a href=\"\"http://www.cervantesmasterpiece.com/venue/detail/state-bridge-lodge\"\" rel=\"\"nofollow\"\">State Bridge Lodge</a> <br> 2:30 PM (2:00 PM doors) / 21+ w/ Valid ID ";
            double x= cs.run(doc1,doc2);
           System.out.print(x);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public double run(String doc1,String doc2) throws IOException 
    {
        // index strings
    	s[0]=doc1;
    	s[1]=doc2;
    	System.out.print(s[0]+"\n"+s[1]+"\n");
        Directory index = new RAMDirectory();
        StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36, analyzer);
        IndexWriter writer = new IndexWriter(index, config);        
        for (String si : s) {
            Document doc = new Document();
            doc.add(new Field("content", si, Field.Store.YES, Field.Index.ANALYZED,TermVector.WITH_POSITIONS_OFFSETS));
            writer.addDocument(doc);
        }
        writer.close();
        
        // read the index
        IndexReader reader = IndexReader.open(index);
        
        // calculate tf/idf
        Map<String,Integer> terms = new HashMap<String,Integer>();
        TermEnum termEnum = reader.terms(new Term("content"));
        int pos = 0;
        while (termEnum.next()) {
            Term term = termEnum.term();
            if (! "content".equals(term.field())) break;
                terms.put(term.text(), pos++);
        }
        
//        for (int i=0; i<reader.maxDoc(); i++) {
//            if (reader.isDeleted(i))
//                continue;
//
//            Document doc = reader.document(i);
//            System.out.println(doc);
//            TermFreqVector tfvs = reader.getTermFreqVector(i,"content");
//            System.out.println(tfvs);
//        }
//        
        // apply cosine similarity
        DocVector[] docs = new DocVector[s.length];
        for (int i=0; i<s.length; i++) {
            TermFreqVector[] tfvs = reader.getTermFreqVectors(i);
            //String strip_str=tfvs.toString();
            //strip_str.replaceAll("null", "");
            
            docs[i] = new DocVector(terms); 
            System.out.print(tfvs);
        //}
            
           for (TermFreqVector tfv : tfvs) {
                String[] termTexts = tfv.getTerms();
                int[] termFreqs = tfv.getTermFrequencies();
                for (int j = 0; j < termTexts.length; j++) {
                docs[i].setEntry(termTexts[j], termFreqs[j]);
              }
            }
            docs[i].normalize();
           
          }
        
        // now get similarity between doc[0] and doc[1]
        double cosim01 = getCosineSimilarity(docs[0], docs[1]);
        //System.out.println("cosim(0,1)=" + cosim01);
        // between doc[0] and doc[2]
       // double cosim02 = getCosineSimilarity(docs[0], docs[3]);
        //System.out.println("cosim(0,2)=" + cosim02);
        // between doc[1] and doc[3]
        //double cosim03 = getCosineSimilarity(docs[1], docs[2]);
        //System.out.println("cosim(1,2)=" + cosim03);
       
       // }
        //double cosim01=10.0;
        reader.close();
        return cosim01;
    }

    private double getCosineSimilarity(DocVector d1, DocVector d2) {
        return (d1.vector.dotProduct(d2.vector)) /
          (d1.vector.getNorm() * d2.vector.getNorm());
      }

    
    class DocVector {
        public Map<String,Integer> terms;
        @SuppressWarnings("deprecation")
		public SparseRealVector vector;
        
        @SuppressWarnings("deprecation")
		public DocVector(Map<String,Integer> terms) {
          this.terms = terms;
          this.vector = new OpenMapRealVector(terms.size());
        }
        
        public void setEntry(String term, int freq) {
          if (terms.containsKey(term)) {
            int pos = terms.get(term);
            vector.setEntry(pos, (double) freq);
          }
        }
        
        @SuppressWarnings("deprecation")
		public void normalize() {
          double sum = vector.getL1Norm();
          vector = (SparseRealVector) vector.mapDivide(sum);
        }
        
        public String toString() {
          RealVectorFormat formatter = new RealVectorFormat();
          return formatter.format(vector);
        }
      }
}
