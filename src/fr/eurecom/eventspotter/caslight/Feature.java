package fr.eurecom.eventspotter.caslight;

/**
 * This class represents a UIMA Feature. This class uses generics, so any kind of objects can be feature values. 
 * 
 * @author Mihály Héder <mihaly.heder@sztaki.hu>
 */
public class Feature<E> {


    E value;
    String name;
    

    /**
     * Constructs a Feature
     * @param name name of the feature
     * @param value value of the feature
     */
    public Feature(String name,E value) {
        this.name = name;
        this.value = value;
       
    }

    /**
     * Returns the name of this Feature.
     * @return The feature Name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this feature.
     * @param name The feature name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the feature value as String, by calling its toString method.
     * @return 
     */
    public String getValueAsString() {
        return value.toString();
    }

    /**
     * Returns the feature value as Integer, if applicable. 
     * Performs an Integer.parseInt call on the object stored as value.
     * @return 
     */
    public int getValueAsInteger() {
        return Integer.parseInt(value.toString());
    }

    /**
     * Sets the value of this Feature.
     * @param value 
     */
    public void setValue(E value) {
        this.value = value;
    }    

    /**
     * Returns the value of this feature.
     * @return 
     */
    public E getValue() {
        return value;
    }

    /**
     * A custom toString method which prints the name, class of the value, and value of the Feature.
     * @return 
     */
    @Override
    public String toString() {
        return "Feature name:"+name+" type:"+value.getClass().getCanonicalName()+" value:"+value;
    }


}
