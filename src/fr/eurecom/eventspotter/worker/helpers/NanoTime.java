

package fr.eurecom.eventspotter.worker.helpers;

import java.text.DecimalFormat;

/**
 * Helper class to measure elapsed time.
 * 
 */
public class NanoTime {

    /**
     * Returns the elapsed time between two time instances. 
     * @param start - nanotime 1
     * @param end - nanotime 2
     * @return the differece in seconds.
     */
     public static String getTimeElapsed(long start, long end) {
        long d = end-start;
        double dd= (double)d / (double)1000000000;
        DecimalFormat df = new DecimalFormat("#.###");
        return Double.valueOf(df.format(dd)).toString();
    }


}
