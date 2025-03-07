package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import utils.ChatHistory;
import utils.Message;
import utils.ChatSessionRequest;
import utils.Connection;
import utils.User;
import utils.UserObjectInputStream;
import utils.NewUser;

public class ClientHandler extends Thread implements Connection {
  
  public Socket socket;
  public String username;
  public String password;
  public String dob;
  public String email;
  public User user;
  public NewUser newuser;
  
  DbConnection db = new DbConnection();
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
//    this.ois = new ObjectInputStream(socket.getInputStream());
    this.oos = new ObjectOutputStream(socket.getOutputStream());
    
//    this.ois = new ObjectInputStream(socket.getInputStream());
    
//    this.uois = (UserObjectInputStream) new ObjectInputStream(socket.getInputStream());
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
      oos.writeObject("Account created successfully! Welcome, " + username + "!");
      System.out.println(username + " has registered successfully.");
    } else {
      
      oos.writeObject("Account create failed!");
      System.out.println(username + " registration failed (User already exists).");
    }

  }
  
  @Override
  public void authenticate(NewUser user) throws IOException, ClassNotFoundException {
    
     if (user.isNewUser()) {  // Use isNewUser() instead of instanceof
        System.out.println("New registration request received.");
        register((NewUser) user);
        return;
    }
    
    System.out.println("Authentication request received.");
    username = user.getUsername();
    password = user.getPassword();
//    System.out.println(username + password);
              
    isAuthenticated = db.check_login(username, password);
      
    if (isAuthenticated) oos.writeObject("Login successful!");
    
        else {
          
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
    return ((ObjectInputStream) uois).readObject();
  }
  
  public void run() {
    
    try {
      
      Object receivedObject;
      
      while (!isAuthenticated) {
        receivedObject = ((UserObjectInputStream) uois).readUserObject();
        authenticate((NewUser) receivedObject);
        cManager.addClient(username, this);
      }
      
      
      while (isAuthenticated) {
        
        receivedObject = receiveMessage(ois);
          
        if (receivedObject instanceof Message) {
            
          message = (Message) receivedObject;
            
          if (message.getReceiver() == "") cManager.broadcastMessages(message, this);
          else {
            cManager.sendPrivateMessage(message);
            db.save_message(message.getSender(), message.getReceiver(), message.getContent());
          }
            
          System.out.println(message.getSender() + " to " + message.getReceiver() + ": " + message.getContent());
          
        }
          
        if (receivedObject instanceof ChatSessionRequest) {
            
          openChatReq = (ChatSessionRequest) receivedObject;
          String sender = openChatReq.getSender();
          String receiver;
          if(openChatReq.getReceiver() == "") receiver="";
          else {
            receiver = openChatReq.getReceiver();
            
            List<Message> oldMessages = cManager.getOldMessages(sender, receiver);
            cHistory = new ChatHistory(oldMessages);
            
            sendMessage(cHistory, oos);
          }
  
          cManager.addClientChatSession(sender, receiver);
        }
      }
      
    } catch (IOException | ClassNotFoundException e) {
      // TODO Auto-generated catch block
      cManager.removeclient(username);
      System.out.println(username + " is disconnected");
      
    } finally {
      
      try {
        
        if (ois != null) ois.close();
              if (oos != null) oos.close();
              if (socket != null) socket.close();
        
      } catch (IOException e) {
        if (username != null) {
              cManager.removeclient(username);
              System.out.println(username + " is disconnected");
          } else {
              System.out.println("Client disconnected before authentication.");
          }
      }
      
    }
    
  }

}
