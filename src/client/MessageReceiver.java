package client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import utils.ChatHistory;
import utils.Message;
import utils.Contact;
import utils.ContactList;


public class MessageReceiver implements Runnable {

	ObjectInputStream ois;
	Message message;
	ChatHistory cHistory;
	ContactList cList;
	ClientConnection cConnection;
	Contact cContact;

	public MessageReceiver(ObjectInputStream ois, ClientConnection cConnection) {
//		this.socket = socket;
		this.ois = ois;
		this.cConnection = cConnection;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		Object receivedObject;
		
		try {
			
			String line; 
			while (true) {
				
				receivedObject = cConnection.receiveMessage(ois);
				
				if (receivedObject instanceof Message) {

					message = (Message) receivedObject;
					
					if (message.getSender().equals(cConnection.user.getUsername()) 
						&& message.getReceiver().equals(cConnection.receiver)) 
					{
						System.out.println(message.getContent());
						System.out.println(message.getId());
						System.out.println(message.tempId);
						
						System.out.println(cConnection.messageList.get(message.tempId).getContent());
					}
					
					else {
						boolean hasContact = false;
											
						for (int i = 0; i < cConnection.contactsList.size(); i++) 
						{
							if (message.getSender().equals(cConnection.contactsList.get(i).getContact())) 
							{
								hasContact = true;
								cConnection.updateContactList(i, new Contact(message.getSender(), message.getContent()));
							}
						}
						
						System.out.println("Sender: " + message.getSender() + ", Receiver: " + message.getReceiver() + ", Content: " + message.getContent());
						
						if (!hasContact) cConnection.addContact(new Contact(message.getSender(), message.getContent()));
						
						if (cConnection.receiver.equals(message.getSender())) cConnection.addMessage(message);
					}
				}

				if (receivedObject instanceof ContactList){
					cList = (ContactList) receivedObject;
					List<Contact> listContact = cList.getContacts();
					
					for (Contact contact: listContact) cConnection.contactsList.add(contact);
					
				}
				
				if (receivedObject instanceof Contact) {
					
					cContact = (Contact) receivedObject;
					if (cContact.getContact().isBlank()) System.out.println("User does not exist");
					else cConnection.addContact(cContact);
				}

				if (receivedObject instanceof ChatHistory) {
					
					cConnection.messageSize = 0;
					
					cHistory = (ChatHistory) receivedObject;
					List<Message> oldMessages = cHistory.getMessages();		
					ObservableList<Message> chatList = FXCollections.observableArrayList();
					String currentDate = "";
					
					for (Message msg: oldMessages) {
						Timestamp timestamp = msg.getSentTime();
	                	SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-MM-yyyy");
	                    String currentDateString = currentDateFormat.format(timestamp);
	                    
	                    if (!currentDateString.equals(currentDate)) {
	                    	
	                    	chatList.add(new Message(null, null, null, timestamp));
	                    	cConnection.messageSize ++;
	                    	currentDate = currentDateString;
	                    }
	                    
	                    chatList.add(msg);
	                    cConnection.messageSize ++;
					}
					
					cConnection.addMessageHistory(chatList);
				}
			
			}
			
		} catch (IOException e) {
			
//			System.out.println("Error receiving message from server");
		} catch (ClassNotFoundException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
