package fr.eurecom.eventspotter.caslight;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * This class hold multiple list of Feature Structures
 * 
 */
public class FeatureStructureListHolder {
    
    private HashMap<String,List<FeatureStructure>> holder = new HashMap<String,List<FeatureStructure>>();
    
    /**
     * Adds a new FeatureStructure List to this holder.
     * @param source Name of the list
     * @param list The list
     */
    public void addFeatureStructureList(String source, List<FeatureStructure> list) {
        this.holder.put(source, list);
    }
    
    /**
     * Returns a FeatureStructure list by its name.
     * @param source the name of the list
     * @return 
     */
    public List<FeatureStructure> getFeatureStructureList(String source) {
        return this.holder.get(source);
    }
    
    /**
     * Returns all the names of the lists in this holder.
     * @return 
     */
    public Set<String> getListNames() {
        return this.holder.keySet();
    }
    
}
