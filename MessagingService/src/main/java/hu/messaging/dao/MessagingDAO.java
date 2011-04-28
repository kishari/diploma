package hu.messaging.dao;

import hu.messaging.model.CompleteMessage;
import hu.messaging.model.UserInfo;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;

public class MessagingDAO {
	 private DataSource dataSource = null;
	 public static final String DATA_SOURCE_NAME = "jdbc/messagingDataSource";
	 private static final String INSERT_MESSAGE = "INSERT INTO " + 
	 											  "messagingdb.messages(message_id, content, content_size) " + 
	 											  "VALUES (?, ?, ?)";
	 private static final String UPDATE_MESSAGE = "UPDATE messagingdb.messages " + "" +
	 											  "SET sender_name = ?, sender_sip_uri = ?, mime_type = ?, subject= ?, sent_at = ? " +
	 											  "WHERE message_id = ?";
	 private static final String INSERT_RECIPIENT = "INSERT INTO messagingdb.recipients(message_id, name, sip_uri, delivery_status) values (?, ?, ?, ?)";
	 private static final String UPDATE_DELIVERY_STATUS_TO_RECIPIENT = "UPDATE messagingdb.recipients " +
	 											    "SET delivery_status = ? " +
	 											    "WHERE sip_uri = ? AND message_id = ? ";
	 private static final String SELECT_CONTENTS_TO_SIPURI = "SELECT message_id, content FROM messagingdb.messages " + 
	 															"WHERE message_id IN(" + 
	 																	"SELECT message_id " +
	 																	"FROM messagingdb.recipients " + 
	 																	"WHERE sip_uri = ?)";
	 
	 private static final String SELECT_CONTENT_TO_MESSAGE_ID = "SELECT message_id, content, subject, sender_name, sender_sip_uri, mime_type, sent_at " +
	 															"FROM messagingdb.messages " + 
	 															"WHERE message_id = ?";
	
	 private static final String SELECT_NEW_MESSAGE_IDS_TO_RECIPIENT = "SELECT message_id " +
																	"FROM messagingdb.recipients " + 
																	"WHERE sip_uri = ? AND delivery_status = 'NEW'";

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
	 	 
	 public void insertRecipients( String messageId, List<UserInfo> recipients ) {
	    	
	 		Connection conn = null;
	        PreparedStatement pstmt = null;
	        try {
	            conn = getConnection();
	            pstmt = conn.prepareStatement(INSERT_RECIPIENT);
	            
	            for (UserInfo r : recipients) {		            
		            pstmt.setString(1, messageId);
		            pstmt.setString(2, r.getName());
		            pstmt.setString(3, r.getSipUri());
		            pstmt.setString(4, "NEW");
		            pstmt.executeUpdate();
	            }
	            
	        } catch (SQLException e) {
	            e.printStackTrace();
	        } finally {
	        	closeAll(null, pstmt, null, conn);
	        }
	    }
	 
	 public void updateDeliveryStatus(List<String> messageIds, String recipientSipUri, String deliveryStatus ) {
	    	
	 		Connection conn = null;
	        PreparedStatement pstmt = null;
	        try {
	            conn = getConnection();
	            pstmt = conn.prepareStatement(UPDATE_DELIVERY_STATUS_TO_RECIPIENT);
	            		    
	            for (String mId : messageIds) {
	            	pstmt.setString(1, deliveryStatus);
	            	pstmt.setString(2, recipientSipUri); 
	            	pstmt.setString(3, mId);
	            	pstmt.executeUpdate();
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        } finally {
	        	closeAll(null, pstmt, null, conn);
	        }
	    }
	 
	 	public Date updateMessage( String messageId, String mimeType, String senderName, String senderSIPUri, String subject) {
	    	
	 		Connection conn = null;
	        PreparedStatement pstmt = null;
	        java.sql.Timestamp sentAt = new java.sql.Timestamp(new Date().getTime());
	        try {
	            conn = getConnection();
	            pstmt = conn.prepareStatement(UPDATE_MESSAGE);
	            pstmt.setString(1, senderName);
	            pstmt.setString(2, senderSIPUri);
	            pstmt.setString(3, mimeType);
	            pstmt.setString(4, subject);
	            
	            System.out.println("sentAt: " + sentAt.toString());
	            pstmt.setTimestamp(5, sentAt);
	            pstmt.setString(6, messageId);
	            pstmt.executeUpdate();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        } finally {
	        	closeAll(null, pstmt, null, conn);
	        }
	        
	        return new Date(sentAt.getTime());
	    }
	 
	 	public void insertMessage( CompleteMessage message) {
	    	
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
	 			 		
	    	Connection conn = null;
	    	PreparedStatement pstmt = null;
	        ResultSet rs = null;
	        
	        List<CompleteMessage> result = new ArrayList<CompleteMessage>();
	        	        
	        try {
	        	conn = getConnection();
	        	pstmt = conn.prepareStatement(SELECT_CONTENT_TO_MESSAGE_ID);
	        	pstmt.setString(1, messageId);
		        rs = pstmt.executeQuery();
		            while (rs.next()) {  
		            	String msgId = rs.getString(1);
		            	String subject = rs.getString(3);
		            	String senderName = rs.getString(4);
		            	String senderSipUri = rs.getString(5);
		            	String mimeType = rs.getString(6);
		            	Timestamp sentAt = rs.getTimestamp(7);
		            	InputStream is = rs.getBlob(2).getBinaryStream();
	            	
		            	try {
		            		byte[] content = IOUtils.toByteArray(is);
		            		
		            		if (content != null)
		            			System.out.println("MessagingDao content size: " + content.length);
		            		
		            		CompleteMessage msg = new CompleteMessage(msgId, content, mimeType, senderName, senderSipUri,  subject);
		            		msg.setSentAt(sentAt);
		            		result.add(msg);
		            	} catch (IOException e) {
		            		e.printStackTrace();
		            	}
		            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        } finally {
	        	closeAll(rs, null, pstmt, conn);
			}
	        
	        return result.get(0);
	    }
	
	 	public List<CompleteMessage> getNewMessagesToSipUri(String sipURI) {
	 		List<CompleteMessage> result = new ArrayList<CompleteMessage>();
	 		
	 		List<String> newMessagesMessageId = new ArrayList<String>();
	 		
	 		Connection conn = null;
	    	PreparedStatement pstmt = null;
	        ResultSet rs = null;
	        
	        try {
	        	conn = getConnection();
	        	pstmt = conn.prepareStatement(SELECT_NEW_MESSAGE_IDS_TO_RECIPIENT);
	        	pstmt.setString(1, sipURI);
		        rs = pstmt.executeQuery();
		            while (rs.next()) {  
		            	String msgId = rs.getString(1);
		            	newMessagesMessageId.add(msgId);		            
		            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        } finally {
	        	closeAll(rs, null, pstmt, conn);
			}
	 		
	        for (String id : newMessagesMessageId) {
	        	CompleteMessage m = getMessageToMessageId(id);
	        	result.add(m);
	        }
	        
	 		return result;
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
