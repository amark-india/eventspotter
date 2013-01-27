package fr.eurecom.eventspotter.caslight;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;



/**
 * This class represents a UIMA Feature Structure. 
 * 
 */

public class FeatureStructure {
//	final Logger logger = LoggerFactory.getLogger(this.getClass());

    String id;
    String typeName;
    //Set<String> superTypes;
    Set<Feature> features;
    String coveredText;

    /**
     * Comparator for Features
     * @param <Feature> 
     */
    class FeatureComparator<Feature> implements Comparator<Feature> {

        public int compare(Feature t, Feature t1) {
            return t.toString().compareTo(t1.toString());
        }
    }
    
    /**
     * Creatues a new FeatureStructure
     * @param id Identifier
     * @param typeName Type name
     */
    public FeatureStructure(String id, String typeName) {
        this.id = id;
        this.typeName = typeName;
        this.features = new TreeSet<Feature>(new FeatureComparator<Feature>());
    }

    /**
     * Returns the set of the features. 
     * @return 
     */
    public Set<Feature> getFeatures() {
        return features;
    }

    /**
     * Returns the id of this feature structure
     * @return 
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id of this feature structure
     * @param id 
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Adds a feature to this Feature Structure
     * @param feature 
     */
    public void addFeature(Feature feature) {
        this.features.add(feature);
    }

    /**
     * Returns a feature by its name
     * @param key The feature name
     * @return 
     */
    public Feature getFeature(String key) {
        Iterator<Feature> it = features.iterator();
        while (it.hasNext()) {
            Feature f = it.next();
            if (f.getName().equals(key)) {
                return f;
            }
        }
        return null;
    }

    /**
     * Returns the Feature Structure's type name.
     * @return 
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * Sets the type name of this feature structure.
     * @param typeName 
     */
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    /**
     * A customized toString method that prints this Feature Structure and its Feature values.
     * @return 
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Feature id:");
        sb.append(id);
        sb.append(" typeName:");
        sb.append(typeName);
        sb.append("\n");
        for (Feature f : features) {
            sb.append(f.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
    
    
    /**
     * If this FS has begin and end features, this function calculates the corresponding 
     * covered text from the subject of annotation.
     * @param sofaString the subject of annotation string
     * @return the covered text or null
     */
    public String getSofaChunk(String sofaString) {
    //	logger.info("i'm in getsofachunk");
        if (this.getFeature("begin") != null && this.getFeature("end") != null) {
            String sofaChunk;
            try {
                sofaChunk = sofaString.substring(this.getFeature("begin").getValueAsInteger(),
                        this.getFeature("end").getValueAsInteger());
            } catch (StringIndexOutOfBoundsException e) {
                sofaChunk = "ERROR: string index out of sofa bounds. begin:"
                        + this.getFeature("begin").getValueAsString() + " end:"
                        + this.getFeature("end").getValueAsString();
            }
            return sofaChunk;
        } else {
            return null;
        }
    }

    /**
     * Returns the covered text.
     * @return 
     */
    public String getCoveredText() {
        return coveredText;
    }

    /**
     * Sets the covered Text.
     * @param coveredText 
     */
    public void setCoveredText(String coveredText) {
        this.coveredText = coveredText;
    }
    
    
    
}
