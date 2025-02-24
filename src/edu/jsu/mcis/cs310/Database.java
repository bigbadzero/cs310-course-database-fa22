package edu.jsu.mcis.cs310;

import java.sql.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class Database {
    
    private final Connection connection;
    
    private final int TERMID_SP22 = 1;
    ResultSet resultset = null;
    ResultSetMetaData metadata;
    int columnCount;
    PreparedStatement pstSelect = null, pstUpdate = null;
    boolean hasresults;
    
    /* CONSTRUCTOR */

    public Database(String username, String password, String address) {
        
        this.connection = openConnection(username, password, address);
        
    }
    
    /* PUBLIC METHODS */

    public String getSectionsAsJSON(int termid, String subjectid, String num) {
        
        String result = null;
        
        // INSERT YOUR CODE HERE
        String query = "SELECT * FROM section WHERE termid = ? AND subjectid = ? AND num = ?";
        try{
            pstSelect = connection.prepareStatement(query);
            pstSelect.setInt(1, termid);
            pstSelect.setString(2, subjectid);
            pstSelect.setString(3, num);

            resultset = pstSelect.executeQuery();
            result = getResultSetAsJSON(resultset);

            resultset.close();
        }
        catch (Exception e) { e.printStackTrace(); }

        return result;
        
    }
    
    public int register(int studentid, int termid, int crn) {
        
        int result = 0;
        String query = "INSERT into registration (studentid, termid,crn) values(?,?,?)";
        try{
            pstSelect = connection.prepareStatement(query);
            pstSelect.setInt(1,studentid);
            pstSelect.setInt(2,termid);
            pstSelect.setInt(3,crn);

            result = pstSelect.executeUpdate();
            resultset.close();
        }
        catch (Exception e) { e.printStackTrace(); }
        return result;
        
    }

    public int drop(int studentid, int termid, int crn) {
        
        int result = 0;
        String query = "DELETE from registration WHERE studentid = ? and termid = ? and crn = ?";
        try{
            pstSelect = connection.prepareStatement(query);
            pstSelect.setInt(1,studentid);
            pstSelect.setInt(2,termid);
            pstSelect.setInt(3,crn);

            result = pstSelect.executeUpdate();
            resultset.close();
        }
        catch (Exception e) { e.printStackTrace(); }

        return result;
        
    }
    
    public int withdraw(int studentid, int termid) {
        
        int result = 0;

        String query = "DELETE from registration WHERE studentid = ? and termid = ?";
        try{
            pstSelect = connection.prepareStatement(query);
            pstSelect.setInt(1,studentid);
            pstSelect.setInt(2,termid);

            result = pstSelect.executeUpdate();
            resultset.close();
        }
        catch (Exception e) { e.printStackTrace(); }

        return result;
        
    }
    
    public String getScheduleAsJSON(int studentid, int termid) {
        
        String result = null;

        String query = "select * from registration r join section s on s.crn = r.crn where r.studentid = ? and r.termid = ?";
        try{
            pstSelect = connection.prepareStatement(query);
            pstSelect.setInt(1, studentid);
            pstSelect.setInt(2, termid);

            resultset = pstSelect.executeQuery();
            result = getResultSetAsJSON(resultset);

            resultset.close();
        }
        catch (Exception e) { e.printStackTrace(); }
        return result;
    }
    
    public int getStudentId(String username) {
        
        int id = 0;
        
        try {
        
            String query = "SELECT * FROM student WHERE username = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, username);
            
            boolean hasresults = pstmt.execute();
            
            if ( hasresults ) {
                
                ResultSet resultset = pstmt.getResultSet();
                
                if (resultset.next())
                    
                    id = resultset.getInt("id");
                
            }
            
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return id;
        
    }
    
    public boolean isConnected() {

        boolean result = false;
        
        try {
            
            if ( !(connection == null) )
                
                result = !(connection.isClosed());
            
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return result;
        
    }
    
    /* PRIVATE METHODS */

    private Connection openConnection(String u, String p, String a) {
        
        Connection c = null;
        
        if (a.equals("") || u.equals("") || p.equals(""))
            
            System.err.println("*** ERROR: MUST SPECIFY ADDRESS/USERNAME/PASSWORD BEFORE OPENING DATABASE CONNECTION ***");
        
        else {
        
            try {

                String url = "jdbc:mysql://" + a + "/jsu_sp22_v1?autoReconnect=true&useSSL=false&zeroDateTimeBehavior=CONVERT_TO_NULL&serverTimezone=America/Chicago";
                // System.err.println("Connecting to " + url + " ...");

                c = DriverManager.getConnection(url, u, p);

            }
            catch (Exception e) { e.printStackTrace(); }
        
        }
        
        return c;
        
    }

    private String getResultSetAsJSON(ResultSet resultset) {

        String result;

        /* Create JSON Containers */

        JSONArray json = new JSONArray();
        JSONArray keys = new JSONArray();

        try {

            /* Get Metadata */

            ResultSetMetaData metadata = resultset.getMetaData();
            int columnCount = metadata.getColumnCount();

            // INSERT YOUR CODE HERE
            for (int i = 1; i <= columnCount; ++i) {

                keys.add(metadata.getColumnLabel(i));

            }

            /* Get ResultSet Data */

            while(resultset.next()) {

                /* Create JSON Container for New Row */

                JSONObject row = new JSONObject();

                /* Get Row Data */

                for (int i = 1; i <= columnCount; ++i) {

                    /* Get Value; Pair with Key */

                    Object value = resultset.getObject(i);
                    row.put(keys.get(i - 1), String.valueOf(value));

                }

                /* Add Row Data to Collection */

                json.add(row);

            }

        }
        catch (Exception e) { e.printStackTrace(); }

        /* Encode JSON Data and Return */

        result = JSONValue.toJSONString(json);
        return result;

    }
    
}