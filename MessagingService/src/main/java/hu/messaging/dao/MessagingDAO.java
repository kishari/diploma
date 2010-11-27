package hu.messaging.dao;

import hu.messaging.Recipient;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;

public class MessagingDAO {
	 private DataSource dataSource = null;
	 public static final String DATA_SOURCE_NAME = "jdbc/messagingDataSource";
	 private static final String INSERT_MESSAGE = "insert into  messages(messageId, content) values (?, ?)";
	 private static final String INSERT_RECIPIENT = "insert into  recipients(messageId, name, address) values (?, ?, ?)";
	 private static final String SELECT_MESSAGE = "SELECT messageId, content FROM messagingdb.messages " + 
	 															"WHERE messageId IN(" + 
	 																	"SELECT messageId " +
	 																	"FROM messagingdb.recipients " + 
	 																	"WHERE address = ?)";
	 
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
	 	 
	 public void insertRecipients( String messageId, List<Recipient> recipients ) {
	    	
	 		Connection conn = null;
	        PreparedStatement pstmt = null;
	        try {
	            conn = getConnection();
	            pstmt = conn.prepareStatement(INSERT_RECIPIENT);
	            
	            for (Recipient r : recipients) {		            
		            pstmt.setString(1, messageId);
		            pstmt.setString(2, r.getName());
		            pstmt.setString(3, r.getSipURI());
		            pstmt.executeUpdate();
	            }
	            
	        } catch (SQLException e) {
	            e.printStackTrace();
	        } finally {
	        	closeAll(null, pstmt, conn);
	        }
	    }
	 
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
	        	closeAll(null, pstmt, conn);
	        }
	    }
	 
	    public List<byte[]> getMessagesToSipURI( String sipURI ) {
	    	
	    	Connection conn = null;
	        PreparedStatement pstmt = null;
	        ResultSet rs = null;
	        
	        List<byte[]> result = new ArrayList<byte[]>();
	        	        
	        try {
	        	conn = getConnection();
	            pstmt = conn.prepareStatement(SELECT_MESSAGE);
	            pstmt.setString(1, sipURI);
	            rs = pstmt.executeQuery();	        
	            
	            while (rs.next()) {            	
	            	InputStream is = rs.getBlob(3).getBinaryStream();
	            	
	            	try {
						byte[] content = IOUtils.toByteArray(is);
						result.add(content);
					} catch (IOException e) {
						e.printStackTrace();
					}
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        } finally {
	        	closeAll(rs, pstmt, conn);
			}
	        
	        return result;
	    }
	
	    private void closeAll(ResultSet rs, PreparedStatement pstmt, Connection conn) {	    	
	    	if (rs != null) {
	            try {
	                rs.close();
	            } catch (SQLException ignore) {
	            	ignore.printStackTrace();
	            }
	       }	    	
	       if (pstmt != null) {
	            try {
	                pstmt.close();
	            } catch (SQLException ignore) {
	            	ignore.printStackTrace();
	            }
	       }
	       
	       if (conn != null) {
	    		   try {
					conn.close();
				} catch (SQLException e) { 
					e.printStackTrace();
				}
	       }
	    }
	    
	    private Connection getConnection() throws SQLException {
	        return dataSource.getConnection();
	    }
}
