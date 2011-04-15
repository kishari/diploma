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
	 private static final String INSERT_MESSAGE = "insert into  messages(messageId, content, sender, extension) values (?, ?, ?, ?)";
	 private static final String UPDATE_MESSAGE = "update messagingdb.messages set extension=?, sender=? " +
	 														 "where messageId=?";
	 private static final String INSERT_RECIPIENT = "insert into  messagingdb.recipients(messageId, name, address) values (?, ?, ?)";
	 private static final String SELECT_MESSAGES_TO_SIPURI = "SELECT messageId, content FROM messagingdb.messages " + 
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
	        	closeAll(null, pstmt, null, conn);
	        }
	    }
	 
	 	public void updateMessage( String messageId, String extension, String sender ) {
	    	
	 		Connection conn = null;
	        PreparedStatement pstmt = null;
	        try {
	            conn = getConnection();
	            pstmt = conn.prepareStatement(UPDATE_MESSAGE);
	            pstmt.setString(1, extension);
	            pstmt.setString(2, sender);
	            pstmt.setString(3, messageId);
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
	            pstmt.setString(3, message.getSender());
	            pstmt.setString(4, message.getExtension());
	            pstmt.executeUpdate();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        } finally {
	        	closeAll(null, pstmt, null, conn);
	        }
	    }
	 
	 	public List<CompleteMessage> getMessagesToMessageIds( String[] messageIds ) {	    	

	 		String sql = "SELECT messageId, content, sender, extension FROM messagingdb.messages " + 
			 "WHERE messageId IN (";
	 		System.out.println("MessagingDao getMessagesToMessageIds");
	 		for (int i = 0; i < messageIds.length - 1; i++) {
	 			sql += "'" + StringEscapeUtils.escapeSql(messageIds[i]) + "',";
	 		}
	 		
	 		sql += "'" + StringEscapeUtils.escapeSql(messageIds[messageIds.length - 1]) + "')";
	 		
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
		            	String messageId = rs.getString(1);
		            	String extension = rs.getString(4);
		            	InputStream is = rs.getBlob(2).getBinaryStream();
	            	
		            	try {
		            		byte[] content = IOUtils.toByteArray(is);
		            		CompleteMessage msg = new CompleteMessage(messageId, content, extension);
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
	        
	        return result;
	    }
	

	    public List<byte[]> getMessagesToSipURI( String sipURI ) {
	    	
	    	Connection conn = null;
	        PreparedStatement pstmt = null;
	        ResultSet rs = null;
	        
	        List<byte[]> result = new ArrayList<byte[]>();
	        	        
	        try {
	        	conn = getConnection();
	            pstmt = conn.prepareStatement(SELECT_MESSAGES_TO_SIPURI);
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
