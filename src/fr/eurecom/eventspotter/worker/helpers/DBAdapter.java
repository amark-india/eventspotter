package fr.eurecom.eventspotter.worker.helpers;

import com.mysql.jdbc.Driver;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



/**
 * This class provides access to the sql database where  eventId,eventTitle,eventDescription,agents are stored.
 * 
 * 
 */
public class DBAdapter {

   // final Logger logger = LoggerFactory.getLogger(this.getClass());
    Connection conn;
    String dbPath;
    String dbUser;
    String dbPassword;
    String dbDriver;
    Driver driver;
    
    /**
     * Instantiates the class
     * 
     * @param dbPath - path to database, e.g. jdbc:mysql://localhost/bookspotter
     * @param dbUser - username for the database
     * @param dbPassword - passwor for the database
     * @param dbDriver - driver name for the database, e.g. com.mysql.jdbc.Driver
     */
    
    public DBAdapter(String dbPath, String dbUser, String dbPassword, String dbDriver) {
        this.dbPath = dbPath;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.dbDriver = dbDriver;
    }

    private Connection getConnection() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        // thread-unsafe connection handling!
        if (conn != null && conn.isValid(0)) {
            return conn;
        } else {
            Class.forName(dbDriver).newInstance();
            conn = DriverManager.getConnection(dbPath, dbUser, dbPassword);
            return conn;
        }
    }

    /**
     * Returns author names for work ids.
     * @param workId the id of the work in question
     * @return the names of the authors.
     */
    public List<String> getAuthors(String eventId) {
        try {
            Connection c = getConnection();
            PreparedStatement pstmt = c.prepareStatement("SELECT agent FROM eventMedia WHERE eventId='"+eventId+"'");
            ResultSet rs = pstmt.executeQuery();
            List<String> ret = new ArrayList<String>();
            while (rs.next()) {
                ret.add(rs.getString(1));
            }
            return ret;
        } catch (Exception ex) {
        //    logger.error("SELECT agent FROM eventMedia WHERE eventId='"+eventId+"'", ex);
            return null;
        }
    }
    
    /**
     * Returns the title of a work
     * @param workId - the id of the work in question
     * @return The title of the work.
     */
    public String getTitle(String eventId) {
        try {
            Connection c = getConnection();
            PreparedStatement pstmt = c.prepareStatement("SELECT eventTitle FROM `eventMedia` WHERE eventId=?");
            pstmt.setString(1, eventId);
            ResultSet rs = pstmt.executeQuery();
            
            List<String> ret = new ArrayList<String>();
            while (rs.next()) {
                return rs.getString("eventTitle");
            }
            return "";
        } catch (Exception ex) {
          //  logger.error("SELECT eventTitle FROM `eventMedia` WHERE eventId='"+eventId+"'", ex);
            return "";
        }
    }
    public String getType(String eventId) {
        try {
            Connection c = getConnection();
            PreparedStatement pstmt = c.prepareStatement("SELECT category FROM `eventMedia` WHERE eventId='"+eventId+"'");
            ResultSet rs = pstmt.executeQuery();
            List<String> ret = new ArrayList<String>();
            while (rs.next()) {
                return rs.getString(1);
            }
            return "";
        } catch (Exception ex) {
         //   logger.error("SELECT eventTitle FROM `eventMedia` WHERE eventId='"+eventId+"'", ex);
            return "";
        }
    }
    public String getDesc(String eventId) {
        try {
            Connection c = getConnection();
            PreparedStatement pstmt = c.prepareStatement("SELECT eventDiscription FROM `eventMedia` WHERE eventId=?");
            pstmt.setString(1,eventId);
            ResultSet rs = pstmt.executeQuery();
            List<String> ret = new ArrayList<String>();
            System.out.println(eventId);
            while (rs.next()) {
                return rs.getString("eventDiscription");
            }
            
        } catch (Exception ex) {
        		ex.printStackTrace();
         //   logger.error("SELECT eventTitle FROM `eventMedia` WHERE eventId='"+eventId+"'", ex);
            //return "";
        }
        return "";
    }
    
}
