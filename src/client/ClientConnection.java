package client;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import gui.ChatMessageCell;
import gui.ContactListCell;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import utils.*;

// HOUR WASTED: 1
public class ClientConnection implements Connection {

    private Socket socket;
    public ObjectInputStream ois;
    public ObjectOutputStream oos;
    public User user;
    private Stage primaryStage;
	public ObservableList<Message> messageList;
    public ListView<Message> chatListView;
    public ObservableList<Contact> contactsList;
    private ListView<Contact> contactsListView;
    String receiver = "";
    private boolean isReceiverRunning = false;
    String currentDate = "";
    public int messageSize = 0;

    public ClientConnection(String address, int port, Stage primaryStage) throws IOException {
        this.socket = new Socket(address, port);
        this.oos = new ObjectOutputStream(socket.getOutputStream());
        this.ois = new ObjectInputStream(socket.getInputStream());
        this.primaryStage = primaryStage;
        this.messageList = FXCollections.observableArrayList();
        this.contactsList = FXCollections.observableArrayList();
    }

    @Override
    public void register(NewUser newuser) throws IOException, ClassNotFoundException {
        oos.writeObject(newuser);
        oos.flush();
        Object response = ois.readObject();
        
        if (response instanceof LoginSuccessResponse) {
            this.user = ((LoginSuccessResponse) response).getUser();
            ChatClient.isAuthenticated = true;
            Platform.runLater(() -> startChatUI());
        } else {
            System.out.println("Account creation failed! Try again.");
        }
    }

    @Override
    public void authenticate(User user) throws IOException, ClassNotFoundException {
        oos.writeObject(user);
        oos.flush();
        Object response = ois.readObject();
        
        if (response instanceof LoginSuccessResponse) {
            this.user = ((LoginSuccessResponse) response).getUser();
            ChatClient.isAuthenticated = true;
            Platform.runLater(() -> startChatUI());
            
        } else {
            System.out.println("Login failed. Please try again.");
        }
    }

    public void startChatUI() {
        primaryStage.setTitle("Chat App - " + user.getUsername());

        HBox mainLayout = new HBox(0);
        VBox chatLayout = new VBox(0);
        
        Label receiverName = new Label(receiver);
        receiverName.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        contactsListView = new ListView<>(contactsList);
        contactsListView.setStyle("-fx-border-width: 0; -fx-background-color: transparent;");
        VBox.setVgrow(contactsListView, Priority.ALWAYS);
        contactsListView.setCellFactory(param -> new ContactListCell());
        contactsListView.setOnMouseClicked(event -> {
            Contact selectedContact = contactsListView.getSelectionModel().getSelectedItem();
            if (selectedContact != null) {
            	
            	if (this.receiver.isBlank() && mainLayout.getChildren().contains(chatLayout)) {
                	mainLayout.getChildren().remove(chatLayout);
                } else if (!this.receiver.isBlank() && !mainLayout.getChildren().contains(chatLayout)) 
                	mainLayout.getChildren().add(chatLayout);

            	if (!this.receiver.equals(selectedContact.getContact())) {
            		receiverName.setText(selectedContact.getContact());
                	openChatSession(selectedContact.getContact());
                }
            }
        });
        
        if (!isReceiverRunning) {
        	new Thread(new MessageReceiver(this.ois, this)).start();
        	isReceiverRunning = true;
        }
        
        Label contactError = new Label();
        contactError.setStyle("-fx-text-fill: red;");
        
        HBox findUser = new HBox(10);
        
        TextField inputUser = new TextField();
        inputUser.setPromptText("Type username");

        Button findBtn = new Button("Find");
        
        findBtn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5px 10px; -fx-background-radius: 10;");
        
        findUser.getChildren().addAll(inputUser, findBtn);
        
        Button viewProfileButton = new Button("View Profile");
        viewProfileButton.setStyle(
            "-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-size: 14px; " +
            "-fx-padding: 8px; -fx-background-radius: 100; -fx-min-width: 40px; " + 
            "-fx-min-height: 24px; -fx-margin: 16px;"
        );
        
        VBox.setMargin(viewProfileButton, new Insets(0, 0, 0, 15));
        
        viewProfileButton.setOnAction(e -> {
            Profile profilePage = new Profile(this, primaryStage, this.user.getUserId());
            profilePage.start();
        });
        
        Label findContactSectionHeader = new Label("Find New Contact");
        findContactSectionHeader.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        VBox findContactSection = new VBox(5, findContactSectionHeader, findUser);
        findContactSection.setPadding(new Insets(10, 0, 0, 15));
        
        findBtn.setOnAction(e -> {
        	contactError.setText("");
        	
        	Contact newContact = new Contact(inputUser.getText(), "");
        	boolean isContactExist = false;
        	
        	for (Contact contact: contactsList) {
        		if (contact.getContact().equals(newContact.getContact())) {
        			contactError.setText("You already had contact with " + newContact.getContact());
        			findContactSection.getChildren().add(contactError);
        			isContactExist = true;
        			break;
        		}
        	}
        	
        	if (isContactExist) System.out.println(newContact.getContact()); // Display Error
        	else {
        		try {
        			oos.writeObject(newContact);
        			oos.flush();
        		} catch (IOException e2) {
        			System.out.println("1" + e2.getMessage());
        		}
        	}
        	
        	// TODO Create interface for this message
//        	else System.out.println("Contact already existed.");
        	
        	
        });
        
        VBox sidebar = new VBox(5, viewProfileButton, findContactSection, contactsListView);
        sidebar.setPadding(new Insets(15, 0, 15, 0));
        sidebar.setBorder(new Border(new BorderStroke(
        		Color.web("#ebebeb"), 
                BorderStrokeStyle.SOLID, 
                null, 
                new BorderWidths(0, 1, 0, 0)
        )));

        // Chat Area
        chatListView = new ListView<>(messageList);
        chatListView.setStyle("-fx-border-width: 0; -fx-background-color: transparent;");
        VBox.setVgrow(chatListView, Priority.ALWAYS);
        chatListView.setCellFactory(param -> new ChatMessageCell(user, this));
	        
        SVGPath addImage = new SVGPath();
        addImage.setContent("M21 12v7a2 2 0 0 1-2 2h-3m5-9c-6.442 0-10.105 1.985-12.055 4.243M21 12v-1.5M3 16v3a2 2 0 0 0 2 2v0h11M3 16V5a2 2 0 0 1 2-2h8M3 16c1.403-.234 3.637-.293 5.945.243M16 21c-1.704-2.768-4.427-4.148-7.055-4.757M8.5 7C8 7 7 7.3 7 8.5S8 10 8.5 10S10 9.7 10 8.5S9 7 8.5 7M19 2v3m0 3V5m0 0h3m-3 0h-3");
        addImage.setStrokeWidth(1.8);
	    addImage.setStroke(Color.BLACK);
	    addImage.setFill(Color.TRANSPARENT);

	    Button addImageButton = new Button();
	    addImageButton.setGraphic(addImage);
	    addImageButton.setBorder(null);
	    addImageButton.setBackground(null);
	        
	    addImageButton.setOnAction(e -> {
	
	    	FileChooser fileChooser = new FileChooser();
	    	fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
	    	fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));            
	    	List<File> selectedFiles = fileChooser.showOpenMultipleDialog(primaryStage);
	    	if (selectedFiles != null) {
	    		selectedFiles.forEach(file -> {
	    			try {
	    				String name = file.getName();
	    				BufferedImage image = ImageIO.read(file);
	    				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    		        ImageIO.write(image, "jpg", byteArrayOutputStream);
	    		        
	    		        byte[] imageData = byteArrayOutputStream.toByteArray();
	    		        int size = imageData.length;

	    		        System.out.println(size);
	    		        ImageMessage imageMessage = new ImageMessage(name, size, imageData);

	    		        Message message = new Message(user.getUsername(), receiver, null, new Timestamp(System.currentTimeMillis()));
	    		        message.setImgMsg(imageMessage);
						sendMessage(message, oos);
						addMessage(message);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	    		});	
	    	}		            
	    });
	    addImageButton.setPadding(new Insets(0));
        
        TextField messageField = new TextField();
        messageField.setPromptText("Type a message...");
        messageField.setBorder(new Border(new BorderStroke(
        		Color.web("#e0e0e0"), 
                BorderStrokeStyle.SOLID, 
                new CornerRadii(6, false), 
                new BorderWidths(1, 1, 1, 1)
        )));
        messageField.setPadding(new Insets(9));
        messageField.setBackground(
        		new Background(new BackgroundFill(
        				Color.web("#F8FAFB"), 
        				new CornerRadii(6, false), 
        				Insets.EMPTY
        		))
        );
        HBox.setMargin(messageField, new Insets(0, 0, 0, 0));
        HBox.setHgrow(messageField, Priority.ALWAYS);
        
        SVGPath sendMessageIcon = new SVGPath();
        sendMessageIcon.setContent("m14 10l-3 3m9.288-9.969a.535.535 0 0 1 .68.681l-5.924 16.93a.535.535 0 0 1-.994.04l-3.219-7.242a.54.54 0 0 0-.271-.271l-7.242-3.22a.535.535 0 0 1 .04-.993z");
        sendMessageIcon.setStroke(Color.WHITE);
        sendMessageIcon.setFill(Color.TRANSPARENT);
        sendMessageIcon.setStrokeWidth(1.5);
        sendMessageIcon.setScaleX(0.8);
        sendMessageIcon.setScaleY(0.8);
        
        Button sendButton = new Button("Send");
        sendButton.setBackground(
        		new Background(new BackgroundFill(
        				Color.web("#14293D"), 
        				new CornerRadii(8, false), 
        				Insets.EMPTY
        		))
        );
        
        sendButton.setPadding(new Insets(8, 12, 8, 8));
        sendButton.setTextFill(Color.WHITE);
        sendButton.setFont(Font.font(sendButton.getFont().getFamily(), FontWeight.NORMAL, 14));
        sendButton.setGraphic(sendMessageIcon);
        sendButton.setAlignment(Pos.CENTER);
        sendButton.setGraphicTextGap(6);
        sendButton.setOnAction(e -> {
            String content = messageField.getText();
            if (!content.isEmpty()) {
                Message message = new Message(user.getUsername(), receiver, content, new Timestamp(System.currentTimeMillis()));
//                    sendMessage(message, oos);
            	try {
    				sendMessage(message, oos);
    			} catch (IOException ex) {	
    				ex.printStackTrace();
    			}
                addMessage(message);
	            messageField.clear();

            }
        });

        HBox messageBox = new HBox(12, addImageButton, messageField, sendButton);   
        messageBox.setPrefHeight(50);
        messageBox.setPadding(new Insets(16));
        messageBox.setAlignment(Pos.CENTER);
        
        HBox chatHeader = new HBox(receiverName);
        chatHeader.setPrefHeight(50);
        chatHeader.setPadding(new Insets(16));
        chatHeader.setAlignment(Pos.CENTER_LEFT); 
        chatHeader.setBorder(new Border(new BorderStroke(
        		Color.web("#ebebeb"), 
                BorderStrokeStyle.SOLID, 
                null, 
                new BorderWidths(0, 0, 1, 0)
        )));
        HBox.setHgrow(chatHeader, Priority.ALWAYS);
        
        chatLayout.getChildren().addAll(chatHeader, chatListView, messageBox);
        chatLayout.setAlignment(Pos.CENTER);
        HBox.setHgrow(chatLayout, Priority.ALWAYS);
        
        mainLayout.getChildren().add(sidebar);
        mainLayout.setStyle("-fx-background-color: white;");

        Scene chatScene = new Scene(mainLayout, primaryStage.getWidth() - 14, primaryStage.getHeight() - 40);
        primaryStage.setScene(chatScene);
        primaryStage.show();
		primaryStage.setMaximized(true);
    }

    private void openChatSession(String receiver) {
        try {
        	
        	this.receiver = receiver;
        	ChatSessionRequest chatSession = new ChatSessionRequest(this.user, receiver);
	        oos.writeObject(chatSession);
	        oos.flush();

        } catch (IOException e) {
            System.out.println("Error opening chat session: " + e.getMessage());
        }
    }

    public void addContact(Contact contact) {

    	System.out.println("Adding contact: " + contact.getContact());

        Platform.runLater(() -> contactsList.add(contact));

    }

    public void addMessage(Message message) {
        Platform.runLater(() -> {
        	this.messageList.add(message);
        	message.tempId = messageSize;
            messageSize++;
        });
    }
    
//    public void addMessageHistory(List<Message> chatHistory) {
//    	ObservableList<Message> observableNewMessages = FXCollections.observableArrayList(chatHistory);
//    	Platform.runLater(() -> {
//    	    messageList.setAll(observableNewMessages);
//    	});
//    }
    public void addMessageHistory(ObservableList<Message> chatHistory) {
    	Platform.runLater(() -> {
    	    messageList.setAll(chatHistory);
    	});
    }
    
    public void updateContactList(int index, Contact contact) {
    	Platform.runLater(() -> contactsList.set(index, contact));

    }

    @Override
    public void sendMessage(Object message, ObjectOutputStream oos) throws IOException {
    	
        oos.writeObject(message);
        oos.flush();
        
        if (message instanceof Message) {
//            addMessage((Message) message);
            String content = ((Message) message).getContent();
            if (content == null) content = "Image";
            
            for (int i = 0; i < contactsList.size(); i++) {
            	if (receiver.equals(contactsList.get(i).getContact())) 
            		updateContactList(i, new Contact(receiver, content, true, new Timestamp(System.currentTimeMillis())));
            }
        }
    }

    @Override
    public Object receiveMessage(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        return ois.readObject();
    }
    
    public void updateMessage(Message message) {
//    	message.setReceiver(null);
//    	message.setSender(null);
    	
    	try {
		
    		sendMessage(message, oos);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void deleteMessage(Message message) {
//    	message.setContent(null);
//    	message.setSender(null);
//    	message.setReceiver(null);
    	try {
			sendMessage(message, oos);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
