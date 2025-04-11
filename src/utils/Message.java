package utils;

import java.io.Serializable;
import java.sql.Timestamp;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public int tempId;
    private int id;
    private String sender;
    private String receiver;
    private String content;
    private Timestamp sentTime;
    private ImageMessage imgMsg;
    
    public Message(String sender, String receiver, String content, Timestamp timestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.sentTime = timestamp;
        this.imgMsg = null;
        this.id = 0;
        this.tempId = 0;
    }
    
    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getContent() {
        return content;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setContent(String content) {
        this.content = content;
    }

	public Timestamp getSentTime() {
		return sentTime;
	}

	public void setSentTime(Timestamp sentTime) {
		this.sentTime = sentTime;
	}

	public ImageMessage getImgMsg() {
		return imgMsg;
	}

	public void setImgMsg(ImageMessage imgMsg) {
		this.imgMsg = imgMsg;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	

}
