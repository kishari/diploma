package hu.messaging.dao;

import hu.messaging.Recipient;
import hu.messaging.msrp.CompleteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;

public class MessagingDAO {
	 private DataSource dataSource = null;
	 public static final String DATA_SOURCE_NAME = "jdbc/messagingDataSource";
	 private static final String INSERT_MESSAGE = "INSERT INTO " + 
	 											  "messagingdb.messages(message_id, content, content_size) " + 
	 											  "VALUES (?, ?, ?)";
	 private static final String UPDATE_MESSAGE = "UPDATE messagingdb.messages " + "" +
	 											  "SET sender_name = ?, sender_sip_uri = ?, mime_type = ? " +
	 											  "WHERE message_id = ?";
	 private static final String INSERT_RECIPIENT = "INSERT INTO messagingdb.recipients(message_id, name, sip_uri) values (?, ?, ?)";
	 private static final String SELECT_CONTENTS_TO_SIPURI = "SELECT message_id, content FROM messagingdb.messages " + 
	 															"WHERE message_id IN(" + 
	 																	"SELECT message_id " +
	 																	"FROM messagingdb.recipients " + 
	 																	"WHERE sip_uri = ?)";
	
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
	        	closeAll(null, pstmt, null, conn);
	        }
	    }
	 
	 	public void updateMessage( String messageId, String mimeType, String senderName, String senderSIPUri ) {
	    	
	 		Connection conn = null;
	        PreparedStatement pstmt = null;
	        try {
	            conn = getConnection();
	            pstmt = conn.prepareStatement(UPDATE_MESSAGE);
	            pstmt.setString(1, senderName);
	            pstmt.setString(2, senderSIPUri);
	            pstmt.setString(3, mimeType);
	            pstmt.setString(4, messageId);
	            pstmt.executeUpdate();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        } finally {
	        	closeAll(null, pstmt, null, conn);
	        }
	    }
	 
	 	public void insertMessage( CompleteMessage message ) {
	    	
	 		Connection conn = null;
	        PreparedStatement pstmt = null;
	        try {
	            conn = getConnection();
	            pstmt = conn.prepareStatement(INSERT_MESSAGE);	            
	            pstmt.setString(1, message.getMessageId());
	            pstmt.setBytes(2, message.getContent());
	            pstmt.setInt(3, message.getContent().length);
	            pstmt.executeUpdate();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        } finally {
	        	closeAll(null, pstmt, null, conn);
	        }
	    }
	 
	 	public CompleteMessage getMessageToMessageId(String messageId) {	    	

	 		String sql = "SELECT messageId, content, sender, extension FROM messagingdb.messages " + 
			 			 "WHERE messageId = '" + StringEscapeUtils.escapeSql(messageId) + "'";
	 		
	 		System.out.println(sql);
	    	Connection conn = null;
	        Statement stmt = null;
	        ResultSet rs = null;
	        
	        List<CompleteMessage> result = new ArrayList<CompleteMessage>();
	        	        
	        try {
	        	conn = getConnection();
	        	stmt = conn.createStatement();
		        rs = stmt.executeQuery(sql);
		            while (rs.next()) {  
		            	String msgId = rs.getString(1);
		            	String extension = rs.getString(4);
		            	InputStream is = rs.getBlob(2).getBinaryStream();
	            	
		            	try {
		            		byte[] content = IOUtils.toByteArray(is);
		            		CompleteMessage msg = new CompleteMessage(msgId, content, extension);
		            		result.add(msg);
		            	} catch (IOException e) {
		            		e.printStackTrace();
		            	}
		            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        } finally {
	        	closeAll(rs, null, stmt, conn);
			}
	        
	        return result.get(0);
	    }
	

	    public List<byte[]> getMessagesToSipURI( String sipURI ) {
	    	
	    	Connection conn = null;
	        PreparedStatement pstmt = null;
	        ResultSet rs = null;
	        
	        List<byte[]> result = new ArrayList<byte[]>();
	        	        
	        try {
	        	conn = getConnection();
	            pstmt = conn.prepareStatement(SELECT_CONTENTS_TO_SIPURI);
	            pstmt.setString(1, sipURI);
	            rs = pstmt.executeQuery();	        
	            
	            while (rs.next()) {            	
	            	//InputStream is = rs.getBlob(3).getBinaryStream();
	            	InputStream is = rs.getBlob(1).getBinaryStream();
	            	
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
	        	closeAll(rs, pstmt, null, conn);
			}
	        
	        return result;
	    }
	
	    private void closeAll(ResultSet rs, PreparedStatement pstmt, Statement stmt, Connection conn) {	    	
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
	       
	       if (stmt != null) {
	            try {
	                stmt.close();
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
