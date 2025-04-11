package gui;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import client.ClientConnection;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import utils.Message;
import utils.User;

public class ChatMessageCell extends ListCell<Message> {

	private final User user;
	private final ClientConnection cConnection;
	public ChatMessageCell(User user, ClientConnection cConnection) {
		this.user = user;
		this.cConnection = cConnection;
	}
	
	@Override
    protected void updateItem(Message msg, boolean empty) {
        super.updateItem(msg, empty);

        setText(null);
        setStyle("-fx-background-color: #F4F6FA");
        setGraphic(null);

        if (empty || msg == null) return;

        Timestamp timestamp = msg.getSentTime() != null ? msg.getSentTime() : new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String formattedTime = sdf.format(timestamp);

        if (msg.getReceiver() == null && msg.getSender() == null && msg.getContent() == null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String date = dateFormat.format(timestamp);

            Label dateLabel = new Label(date);
            dateLabel.setTextFill(Color.web("#ABABAB"));
            dateLabel.setFont(Font.font(dateLabel.getFont().getFamily(), FontWeight.BOLD, dateLabel.getFont().getSize()));

            HBox dateSeparator = new HBox(dateLabel);
            dateSeparator.setAlignment(Pos.CENTER);
            dateSeparator.setPadding(new Insets(8));

            setGraphic(dateSeparator);
            return;
        }

        GridPane messageHeader = new GridPane();
        messageHeader.setHgap(5);
        VBox messageContainer = new VBox(10, messageHeader);
        HBox chatBox = new HBox(messageContainer);
        chatBox.setPadding(new Insets(8));

        Text timeStamp = new Text(formattedTime);
        Text sender = new Text(msg.getSender() != null && msg.getSender().equals(user.getUsername()) ? "You" : msg.getSender());
        sender.setFont(Font.font(sender.getFont().getFamily(), FontWeight.BOLD, sender.getFont().getSize()));

        boolean isUser = msg.getSender() != null && msg.getSender().equals(user.getUsername());

        if (msg.getImgMsg() != null) {
            Image image = new Image(new ByteArrayInputStream(msg.getImgMsg().getImageData()));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(500);
            imageView.setFitHeight(500);
            imageView.setPreserveRatio(true);

            Rectangle clip = new Rectangle(imageView.getFitWidth(), imageView.getFitHeight());
            clip.setArcWidth(32);
            clip.setArcHeight(32);
            imageView.setClip(clip);

            HBox imageBox = new HBox(imageView);
            messageContainer.getChildren().add(imageBox);
        }

        else {
            Text chatText = new Text(msg.getContent() + ", " + msg.getId());
            chatText.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
            if (chatText.getText().length() > 41) chatText.setWrappingWidth(400);

            VBox messageBox = new VBox(chatText);
            messageBox.setPadding(new Insets(16));
            messageContainer.getChildren().add(messageBox);

            chatText.setFill(isUser ? Color.WHITE : Color.BLACK);
            messageBox.setBackground(
                new Background(new BackgroundFill(
                    isUser ? Color.web("#132B41") : Color.WHITE,
                    isUser 
                        ? new CornerRadii(16, 0, 16, 16, false) 
                        : new CornerRadii(0, 16, 16, 16, false),
                    Insets.EMPTY
                ))
            );
        }


        if (isUser) {
            messageHeader.setAlignment(Pos.BASELINE_RIGHT);
            messageContainer.setAlignment(Pos.CENTER_RIGHT);
            chatBox.setAlignment(Pos.CENTER_RIGHT);
            messageHeader.add(sender, 1, 0);
            messageHeader.add(timeStamp, 0, 0);
        } else {
            messageHeader.setAlignment(Pos.BASELINE_LEFT);
            messageContainer.setAlignment(Pos.CENTER_LEFT);
            chatBox.setAlignment(Pos.CENTER_LEFT);
            messageHeader.add(sender, 0, 0);
            messageHeader.add(timeStamp, 1, 0);
        }
        
        chatBox.setOnMouseClicked(event -> {
        	if (event.getButton() == MouseButton.PRIMARY) System.out.println("Left Clicked");
        	else if (event.getButton() == MouseButton.SECONDARY)handleRightClick(chatBox, msg, event.getScreenX(), event.getSceneY());
        });

        setGraphic(chatBox);
	}
	
	private void handleRightClick(Node target, Message message, double x, double y) {
		System.out.println("Right Clicked at " + x + ", " + y);

		ContextMenu contextMenu = new ContextMenu();
		contextMenu.setStyle(null);

	    MenuItem copy = new MenuItem();
	    MenuItem delete = new MenuItem();
	    MenuItem update = new MenuItem();

	    copy.setGraphic(buildMenuItem("Copy"));
	    delete.setGraphic(buildMenuItem("Delete"));
	    update.setGraphic(buildMenuItem("Update"));

	    contextMenu.getItems().addAll(copy, delete, update);
	    if (!contextMenu.isShowing()) {
	        contextMenu.show(target, x, y);
	    }
	    
	    delete.setOnAction(e -> {
	    	
	    	Message deleteMessage = new Message(null, null, null, null);
	    	deleteMessage.setId(message.getId());
	    	try {
	    		
				cConnection.sendMessage(deleteMessage, cConnection.oos);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
	    });
	    
	    update.setOnAction(e -> {
	    	Message updateMessage = new Message(null, null, message.getContent(), null);
	    	updateMessage.setId(message.getId());
	    	try {
	    		
	    		cConnection.sendMessage(updateMessage, cConnection.oos);	    		
	    	} catch (IOException e1) {
	    		e1.printStackTrace();
	    	}
	    });
	    
	    
	    
	}
	
	private HBox buildMenuItem(String text) {
		Label label = new Label(text);
		

	    label.setMaxWidth(Double.MAX_VALUE);
	    label.setMaxHeight(Double.MAX_VALUE);
	    HBox test = new HBox(label);
	    test.setMaxWidth(Double.MAX_VALUE);
	    test.setMaxHeight(Double.MAX_VALUE);
	    HBox.setHgrow(test, Priority.ALWAYS);
	    
		label.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
		label.setBackground(new Background(new BackgroundFill(Color.BLUE, null, null)));
		return test;
	}
}
