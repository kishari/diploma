package hu.messaging.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;

public class MessagingDAO {
	 private DataSource dataSource = null;
	 public static final String DATA_SOURCE_NAME = "jdbc/messagingDataSource";
	 private static final String INSERT_MESSAGE = "insert into  messages(messageId, content) values (?, ?)";
	 
	 public MessagingDAO() {
		 init();
	 }
	 
	 public void init() {
		InitialContext ctx = null;
		try {
			ctx = new InitialContext();
		} catch (NamingException e) {
			System.out.println("Failed to get InitialContext");
			e.printStackTrace();
			throw new IllegalStateException();
		}
		try {
			dataSource = (DataSource) ctx.lookup(DATA_SOURCE_NAME);
		} catch (NamingException e) {
			System.out.println("Unable to find pool with name: "
			+ DATA_SOURCE_NAME);
			e.printStackTrace();
			throw new IllegalStateException();
		}

		if (dataSource == null) {
			System.out.println("Unable to find pool with name: "
			+ DATA_SOURCE_NAME);
			throw new IllegalStateException();
		}
	}
	 	    
	   /* public static boolean init() {
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
	   */
	 	public void insertMessage( String messageId, byte[] content ) {
	    	
	 		Connection conn = null;
	        PreparedStatement pstmt = null;
	        try {
	            conn = getConnection();
	            pstmt = conn.prepareStatement(INSERT_MESSAGE);
	            pstmt.setString(1, messageId);
	            pstmt.setBytes(2, content);
	            pstmt.executeUpdate();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        } finally {
	            if (pstmt != null)
	                try {
	                    pstmt.close();
	                } catch (SQLException ignore) { }

	            if (conn != null)
	                try {
	                    conn.close();
	                } catch (SQLException ignore) { }
	        }
	    }
	 
	    public String getMessages() {
	    	
	        PreparedStatement pstmt = null;
	        ResultSet rs = null;
	        
	        String selectCommand = "SELECT id, messageId, content FROM messagingdb.messages";
	        
	        try {
	            pstmt = getConnection().prepareStatement(selectCommand);
	            rs = pstmt.executeQuery();	        
	            
	            while (rs.next()) {
	            	System.out.println("id: " + rs.getInt(1));
	            	System.out.println("messageId: " + rs.getString(2));
	            	
	            	InputStream is = rs.getBlob(3).getBinaryStream();
	            	try {
						byte[] content = IOUtils.toByteArray(is);
						System.out.println(new String(content));
					} catch (IOException e) {
						e.printStackTrace();
					}
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        } finally {
	        	closeAll(rs, pstmt);
			}
	        
	        return null;
	    }
	
/*	    
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
*/	    
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
	    
	    private Connection getConnection() throws SQLException {
	        return dataSource.getConnection();
	    }
}
