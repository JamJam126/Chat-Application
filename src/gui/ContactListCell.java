package gui;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
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
            lastMessageLabel.setMaxWidth(144);           // maximum widthd
            lastMessageLabel.setTextOverrun(OverrunStyle.ELLIPSIS);
            lastMessageLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: normal; -fx-text-fill: gray;");
//            
//            Circle circle = new Circle(20, Color.web("#ECECEE"));
//             
//            circle.setStrokeWidth(1);                    

            Circle background = new Circle(20, Color.web("#ECECEE"));
            
            SVGPath head = new SVGPath();
            head.setContent("M11 7c0 1.66-1.34 3-3 3S5 8.66 5 7s1.34-3 3-3s3 1.34 3 3");
            head.setFill(Color.web("#6B7280"));
            head.setScaleX(2.5);
            head.setScaleY(2.5);
            head.setTranslateY(-2);

            SVGPath body = new SVGPath();
            body.setContent("M16 8c0 4.42-3.58 8-8 8s-8-3.58-8-8s3.58-8 8-8s8 3.58 8 8M4 13.75C4.16 13.484 5.71 11 7.99 11c2.27 0 3.83 2.49 3.99 2.75A6.98 6.98 0 0 0 14.99 8c0-3.87-3.13-7-7-7s-7 3.13-7 7c0 2.38 1.19 4.49 3.01 5.75");
            body.setFill(Color.web("#6B7280"));
            body.setScaleX(2.5);
            body.setScaleY(2.5);

            StackPane avatar = new StackPane(background, body, head);
          
            GridPane contactBox = new GridPane(); 
            contactBox.setHgap(10);
            
            contactBox.add(avatar, 0, 0);
            GridPane.setRowSpan(avatar, 2);
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
