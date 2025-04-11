package gui;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import utils.Contact;

public class ContactListCell extends ListCell<Contact> {
	
	@Override
	protected void updateItem(Contact contact, boolean empty) {
		super.updateItem(contact, empty);
        
        if (empty || contact == null) {
            setText(null);
            setStyle("-fx-background-color: #FEFEFE");
            
        } else {
            setStyle("-fx-background-color: #FEFEFE; -fx-text-fill: #212121");
        	Timestamp timestamp;
        	
        	if (contact.getSendTime() != null) 
        		timestamp = contact.getSendTime(); 
        	
        	else timestamp = new Timestamp(System.currentTimeMillis());
        	
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String formattedTime = sdf.format(timestamp);

            
            Label nameLabel = new Label(contact.getContact());
            Label lastMessageLabel;
            Label sentTime = new Label(formattedTime);
            
            sentTime.setAlignment(Pos.TOP_RIGHT);
            sentTime.setTextFill(Color.web("#9E9FA3"));
            
            nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #212121");
            
            if (isSelected()) setStyle("-fx-background-color: #EFEFEF");

            else setStyle("-fx-background-color: #FEFEFE; ");
            
            if (contact.isSender()) {
            	
                lastMessageLabel = new Label("You: " + contact.getMessage());
            } else {
            	
                lastMessageLabel = new Label(contact.getContact() + ": " + contact.getMessage());
            }
            
            // TODO: Get actual last message
            lastMessageLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: normal; -fx-text-fill: gray;");
            
            Circle circle = new Circle(20, Color.web("#ECECEE"));
             
            circle.setStrokeWidth(1);                    

            GridPane contactBox = new GridPane(); 
            contactBox.setHgap(10);
            
            contactBox.add(circle, 0, 0);
            GridPane.setRowSpan(circle, 2);
            contactBox.add(nameLabel, 1, 0);
            contactBox.add(lastMessageLabel, 1, 1);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            HBox test = new HBox(contactBox, spacer, sentTime);
            test.setPadding(new Insets(8, 0, 8, 6));

            setGraphic(test);
            
            setOnMouseEntered(event -> {
            	if (!isSelected()) setStyle("-fx-background-color: #FAFAFA; -fx-text-fill: #212121;");
            });

            setOnMouseExited(event -> {
                if (!isSelected()) setStyle("-fx-background-color: #FEFEFE; -fx-text-fill: #212121;");
            });
        }
	}
}
