package utils;

import java.io.Serializable;
import java.sql.Timestamp;

public class Contact implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    private String contactName;
    private int sender_id; // Dak joal lg sin
    private String lastMessage;
    private boolean isSender;
    private Timestamp sendTime;
    
    public Contact(String contactName, String lastMessage) {
    	
        this.contactName = contactName;
        this.lastMessage = lastMessage;
//        this.setSendTime(sendTime);
    }
    
    public Contact(String contactName, String lastMessage, boolean isSender, Timestamp sendTime) {
    	
        this.contactName = contactName;
        this.lastMessage = lastMessage;
        this.isSender = isSender;
        this.setSendTime(sendTime);
    }
    
    public String getContact() {
    
    	return contactName;
    }
    
    public void setContact(String contactName) {
    	
    	this.contactName = contactName;
    }
    
    public String getMessage() {
        
    	return lastMessage;
    }
    
    public void setLastMessage(String lastMessage) {
    	
    	this.lastMessage = lastMessage;
    }
    
    public boolean isSender() {
        
    	return isSender;
    }

	public Timestamp getSendTime() {
		return sendTime;
	}

	public void setSendTime(Timestamp sendTime) {
		this.sendTime = sendTime;
	}


}
