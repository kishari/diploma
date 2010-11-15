package hu.messaging.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUtil {
	 private static Connection connection = null;
	    
	    public static boolean init() {
	    	try {
				connection = getConnection();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
	    	if (connection != null) {
	    		
	        	return true;
	    	}
	    	return false;
	    }
	    
	    public static String getMessages() {
	    	
	        PreparedStatement pstmt = null;
	        ResultSet rs = null;
	        
	        String selectCommand = "SELECT id, message FROM hibernatedb.messages";
	        
	        try {
	            pstmt = connection.prepareStatement(selectCommand);
	            rs = pstmt.executeQuery();	        
	            
	            while (rs.next()) {
	            	System.out.println("id: " + rs.getInt(1));
	            	System.out.println("message: " + rs.getString(2));
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        } finally {
	        	closeAll(rs, pstmt);
			}
	        
	        return null;
	    }
	    
	    private static Connection getConnection() throws SQLException {
	    	Connection conn = null;
	    	
	    	String user = "root";
	        String pass = "root";
	        String db = "hibernatedb";
	        String hostport = "localhost:3306";
	        String url = "jdbc:mysql://" + hostport + "/";
	        url = url + db;
	        String driver = "com.mysql.jdbc.Driver";
	        
	        try {
	            Class.forName(driver).newInstance();
	            conn = DriverManager.getConnection(url, user, pass);

	        } catch (Exception e) { }
	        
	        return conn;
	    }
	    
	    private static void closeAll(ResultSet rs, PreparedStatement pstmt) {
	    	
	    	if (rs != null) {
	            try {
	                rs.close();
	            } catch (SQLException ignore) {

	            }
	       }
	    	
	       if (pstmt != null) {
	            try {
	                pstmt.close();
	            } catch (SQLException ignore) {
	            	
	            }
	       }
	    }
}
