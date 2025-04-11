package server;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Base64;
import java.util.List;

import utils.ChatHistory;
import utils.Message;
import utils.ChatSessionRequest;
import utils.Connection;
import utils.LoginSuccessResponse;
import utils.User;
import utils.UserObjectInputStream;
import utils.NewUser;
import utils.Contact;
import utils.ContactList;
import utils.ImageMessage;

// HOURS WASTED: 7

public class ClientHandler extends Thread implements Connection {
  
	public Socket socket;
	public String username;
	public String password;
	public String dob;
	public String email;
	public User user;
	public NewUser newUser;
	public Contact contact;
  
	DbConnection db = new DbConnection(this);
	boolean isAuthenticated = false;
  
	ClientManager cManager;
	Message message;
	ObjectOutputStream oos;
	ObjectInputStream ois;
	ChatSessionRequest openChatReq;
	ChatHistory cHistory;
  
	UserObjectInputStream uois;
  
	ClientHandler(Socket socket, ClientManager cManager) throws IOException {
    
		this.socket = socket;
		this.cManager = cManager;
//		this.ois = new ObjectInputStream(socket.getInputStream());
		this.oos = new ObjectOutputStream(socket.getOutputStream());
    
//		this.ois = new ObjectInputStream(socket.getInputStream());
    
//		this.uois = (UserObjectInputStream) new ObjectInputStream(socket.getInputStream());
		this.uois = new UserObjectInputStream(socket.getInputStream());
		
    
	}
  
	@Override 
	public void register(NewUser newuser) throws IOException, ClassNotFoundException {
    
		username = newuser.getUsername();
		email = newuser.getEmail();
		dob = newuser.getDob();
		password = newuser.getPassword();
    
		boolean userExists = db.check_user(email, username);
    
		if (!userExists) { 
			db.save_user(dob, email, username, password);
			this.user = new User(username, password);
	
			authenticate(this.user);  // Call authentication
			if (isAuthenticated) {  // Check authentication status before sending response
				oos.writeObject(new LoginSuccessResponse(this.user, "Account created successfully! Welcome, " + username + "!"));
				cManager.addClient(this.user.getUserId(), this);
			} else {
				oos.writeObject("Account created, but login failed.");
			}
	
			System.out.println(username + " has registered successfully.");
		} else {
      
			oos.writeObject("Account create failed!");
			System.out.println(username + " registration failed (User already exists).");
		}

	}
	
  
	@Override
	public void authenticate(User user) throws IOException, ClassNotFoundException {
	    
	    System.out.println("Authentication request received.");
	    username = user.getUsername();
	    password = user.getPassword();
	    db.check_login(username, password);
	      
	    if (isAuthenticated) 
	    {	
	    	oos.writeObject(new LoginSuccessResponse(user, "Login Successful"));
	    	cManager.addClient(this.user.getUserId(), this);
	    }
    
        else 
        {
            oos.writeObject("login failed");
            System.out.println(username + " login failed.");  
        }
    
	}  
  
	@Override
  	public void sendMessage(Object message, ObjectOutputStream oos) throws IOException {
	  	// TODO Auto-generated method stub
        oos.writeObject(message);
        oos.flush();
  	}


	@Override
  	public Object receiveMessage(ObjectInputStream ois) throws IOException, ClassNotFoundException {
	  	// TODO Auto-generated method stub
	  	return ois.readObject();
  	}
  
	public void run() {

		Object receivedObject;
		

		try {
			while(!isAuthenticated) {		
				receivedObject = ((UserObjectInputStream) uois).readUserObject();
				
				if (receivedObject instanceof User) 
				{
					user = (User) receivedObject;
					authenticate(user);
					cManager.addClient(user.getUserId(), this);
				}
					
				else if (receivedObject instanceof NewUser) 
				{
					newUser = (NewUser) receivedObject;
			        System.out.println("New registration request received.");
			        register(newUser);
			    }	
			}	
			
		} catch (ClassNotFoundException | IOException e) {
			System.out.println("User failed to connect to the server: " + e.getMessage());
		}	
		
      	
		try {
			int receiverId = 0;


			List<Contact> getContactList = cManager.getContactAlls(user.getUserId());
			ContactList contactList = new ContactList(getContactList);

            sendMessage(contactList, oos);
			

			while (isAuthenticated) {
				receivedObject = receiveMessage((ObjectInputStream) uois);
					
				if (receivedObject instanceof Message) {
	            
					message = (Message) receivedObject;
					
					boolean hasContent = message.getContent() != null;
				    boolean hasImage = message.getImgMsg() != null;
				    boolean hasSender = message.getSender() != null;
				    boolean hasReceiver = message.getReceiver() != null;

				    if ((hasContent || hasImage) && hasSender && hasReceiver) 
				        cManager.sendPrivateMessage(message, this.user.getUserId(), receiverId);
				    
				    else if ((hasContent || hasImage) && !hasSender && !hasReceiver) 
				        System.out.println("Update message");
				    
				    else if ((!hasContent || !hasImage) && !hasSender && !hasReceiver) 
				        System.out.println("Delete message");
				    
					System.out.println(message.getSender() + " to " + message.getReceiver() + ": " + message.getContent());
	          
				}
				
				if (receivedObject instanceof Contact) {
				
					contact = (Contact) receivedObject;
					Contact newContact;
					boolean isUserExist = cManager.isReceiverExist(contact.getContact());
					if (isUserExist) newContact = new Contact(contact.getContact(), "");
					else newContact = new Contact("", "");
					
					sendMessage(newContact, oos);
				}
				
				if (receivedObject instanceof User) {
				
					System.out.println("recieved change");
					User Cuser = (User) receivedObject;
					db.change(Cuser);
				}
	          
				if (receivedObject instanceof ChatSessionRequest) {
	            
					openChatReq = (ChatSessionRequest) receivedObject;
					
					boolean isUserExist = cManager.isReceiverExist(openChatReq.getReceiver());
					System.out.println(isUserExist);
					
					if (isUserExist) {
						

						receiverId = cManager.getReceiverId(openChatReq.getReceiver());
						System.out.println("Chat Session Opened For " + 
											user.getUsername() + ", ID: " + user.getUserId() + " to " + 
											openChatReq.getReceiver() + ", ID: " + receiverId);
									
						List<Message> oldMessages = cManager.getOldMessages(openChatReq.getUser().getUserId(), receiverId);
						cHistory = new ChatHistory(oldMessages);
		            
						sendMessage(cHistory, oos);
							
						cManager.addClientChatSession(openChatReq.getUser().getUserId(), receiverId);
					}
				}
				
			}

		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			if (user != null) {
				cManager.removeclient(user.getUserId());
				System.out.println(user.getUsername() + " is disconnected");
			}
	      
		} finally {
		      
			try {
		       
				if (ois != null) ois.close();
				if (oos != null) oos.close();
				if (socket != null) socket.close();
		        
			} catch (IOException e) {
				if (user != null) {
					cManager.removeclient(user.getUserId());
					System.out.println(user.getUsername() + " is disconnected");
				}
		    		
				else System.out.println("Client disconnected before authentication.");
			}
		}			
	}
}
