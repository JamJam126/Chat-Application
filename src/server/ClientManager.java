package server;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import utils.Message;
import utils.Contact;

public class ClientManager {
	
	private Map<Integer, ClientHandler> clientSockets = new ConcurrentHashMap<>();
	private Map<Integer, Integer> chatSessions = new ConcurrentHashMap<>();
	
	DbConnection db = new DbConnection();

	public void addClient(int userId, ClientHandler cHandler) {
		
		clientSockets.put(userId, cHandler);
	}
		
	public void removeclient(int userId) {
		
		clientSockets.remove(userId);
	}
	
	public void addClientChatSession(int senderId, int receiverId) {
		
		chatSessions.put(senderId, receiverId);
	}
	
	public void broadcastMessages(Message message, ClientHandler cHandler) {
		
		for (Map.Entry<Integer, ClientHandler> 	entry: clientSockets.entrySet()) {
			
			try {
				
				entry.getValue().sendMessage(message, entry.getValue().oos);
			
			} catch (IOException e) {
				
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public List<Message> getOldMessages(int sender, int receiver) {
		
		List<Message> messageHistory = db.private_chat(sender, receiver);
		
		return messageHistory;
	}

	public List<Contact> getContactAlls(int senderId) {
		
        return db.getAllContact(senderId);

	}
	
	public void sendPrivateMessage(Message message, int senderId, int receiverId) {
		
		try {

			int messageId = db.save_message(senderId, receiverId, message);
			message.setId(messageId);
			clientSockets.get(senderId).sendMessage(message, clientSockets.get(senderId).oos);
			
			if (isReceiverLoggedIn(receiverId)) {
				
				message.tempId = 0;
				clientSockets.get(receiverId).sendMessage(message, clientSockets.get(receiverId).oos);
			}
			
			
			
		} catch (IOException | NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	
	
	public boolean isReceiverChatSessionOpen(int sender, int receiver) {
		
		Integer session = chatSessions.get(receiver);
	    return clientSockets.containsKey(receiver) && session != null && session == sender;
	}

	public boolean isReceiverLoggedIn(int receiver) {
		

	    return clientSockets.containsKey(receiver);
	}
	
	
	public boolean isReceiverExist(String receiver) {

		
		if (db.findReceiver(receiver)) System.out.println("User Exist: " + receiver);

		else System.out.println("User doesnot exist: " + receiver);
		
		return db.findReceiver(receiver);
	}

	public int getReceiverId(String receiver) {
		
		return db.findReceiverId(receiver);
	}
}
