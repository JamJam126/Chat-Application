package client;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import utils.User;
import utils.EnvLoader;
import utils.InvalidCredentialException;
import utils.NewUser;

public class ChatClient extends Application {

	static String SERVER_ADDRESS;
	static int SERVER_PORT;
	static boolean isAuthenticated = false;

	@Override
	public void start(Stage primaryStage) throws IOException, ClassNotFoundException {
		Map<String, String> env = EnvLoader.load();
	    SERVER_ADDRESS = env.get("SERVER_ADDRESS");
	    SERVER_PORT = Integer.parseInt(env.get("SERVER_PORT"));
		
		ClientConnection cc = new ClientConnection(SERVER_ADDRESS, SERVER_PORT, primaryStage);
		
		Label welcomeLabel = new Label("Welcome! Please login or register");
		welcomeLabel.setStyle("-fx-font-size: 24px");

        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");

		loginButton.setStyle("-fx-background-color: #008CBA; -fx-text-fill: white; -fx-font-size: 14px;");
        registerButton.setStyle("-fx-background-color: #008CBA; -fx-text-fill: white; -fx-font-size: 14px;");

		loginButton.setOnAction(e -> login_page(primaryStage, cc));
		registerButton.setOnAction(e -> Register_page(primaryStage, cc));

		loginButton.setPrefWidth(150);
        registerButton.setPrefWidth(150);
		loginButton.setPrefHeight(50);
		registerButton.setPrefHeight(50);

		VBox loginPanel = new VBox(30);
        loginPanel.getChildren().addAll(welcomeLabel, loginButton, registerButton);
		loginPanel.setAlignment(Pos.CENTER);
		loginPanel.setStyle("-fx-background-color: #ffffff");

		Scene scene = new Scene(loginPanel, primaryStage.getWidth(), primaryStage.getHeight());
        primaryStage.setTitle("Chat Application");  

        primaryStage.setScene(scene);
        primaryStage.show();
		primaryStage.setMaximized(true);

	}
	
	private void login_page(Stage primaryStage, ClientConnection cc) {
		
        primaryStage.setTitle("Login");

		Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #008CBA; -fx-text-fill: white; -fx-font-size: 14px;");
        
        backButton.setOnAction(e -> {
        	
			try {
			
				start(primaryStage);
			
			} catch (IOException | ClassNotFoundException ex) {
			
				ex.printStackTrace();
			}
		});
        
		TextField usernameField = new TextField();
        usernameField.setMaxWidth(300);
		usernameField.setPromptText("Enter your username");
        PasswordField passwordField = new PasswordField();
        passwordField.setMaxWidth(300);
		passwordField.setPromptText("Enter your password");
        Button loginSubmitButton = new Button("Sign In");
		loginSubmitButton.setStyle("-fx-background-color: #008CBA; -fx-text-fill: white; -fx-font-size: 14px;");

		Label statusLabel = new Label();

		loginSubmitButton.setOnAction(e -> {
			String username = usernameField.getText();
			String password = passwordField.getText();
			try {
//				validate(username, password);
				User user = new User(username, password);
//				 User user = new User("Jam", "12345678");
				cc.authenticate(user);
				if(!isAuthenticated) {
					statusLabel.setText("Login failed. Try again.");
					statusLabel.setStyle("-fx-text-fill: red;");
				}
				
//			} catch (InvalidCredentialException ex) {
//				statusLabel.setText(ex.getMessage());
//				statusLabel.setStyle("-fx-text-fill: red;");
				
			} catch (Exception ex) {
				statusLabel.setText("An error occurred during authentication.");
				statusLabel.setStyle("-fx-text-fill: red;");
				ex.printStackTrace();
			}	
		});

		HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.TOP_LEFT);
        topBar.getChildren().add(backButton);

		VBox dialogLayout = new VBox(10);
        dialogLayout.setAlignment(Pos.CENTER);
        dialogLayout.getChildren().addAll(
                new Label("Username:"), usernameField,
                new Label("Password:"), passwordField,
                loginSubmitButton,
				statusLabel
        );
		dialogLayout.setMaxWidth(400);
		
		VBox LoginLayout = new VBox(100);
		LoginLayout.setAlignment(Pos.CENTER);
		LoginLayout.getChildren().addAll(topBar,dialogLayout);
		
		Scene dialogScene = new Scene(LoginLayout,primaryStage.getWidth(), primaryStage.getHeight());
        primaryStage.setScene(dialogScene);
		primaryStage.setMaximized(true); 
        primaryStage.show();
	}
	
	private void Register_page(Stage primaryStage,ClientConnection cc) {
        primaryStage.setTitle("Register");

		Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #008CBA; -fx-text-fill: white; -fx-font-size: 14px;");
        backButton.setOnAction(e -> {
			try {
				start(primaryStage);
			
			} catch (IOException | ClassNotFoundException ex) {
				
				ex.printStackTrace();
			}
		});
        
		TextField usernameField = new TextField();
		usernameField.setPrefWidth(300);
        usernameField.setMaxWidth(300);
		usernameField.setPromptText("Enter your username");

        PasswordField passwordField = new PasswordField();
		passwordField.setPrefWidth(300);
        passwordField.setMaxWidth(300);
		passwordField.setPromptText("Enter your password");

		TextField emailField = new TextField();
    	emailField.setMaxWidth(300);
    	emailField.setPromptText("Enter your email");

		DatePicker dobPicker = new DatePicker();
    	dobPicker.setMaxWidth(300);
        Button loginSubmitButton = new Button("Sign Up");
		loginSubmitButton.setStyle("-fx-background-color: #008CBA; -fx-text-fill: white; -fx-font-size: 14px;");

		HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.TOP_LEFT);
        topBar.getChildren().add(backButton);

		Label statusLabel = new Label();

		loginSubmitButton.setOnAction(e -> {
			String username = usernameField.getText();
			String email = emailField.getText();
			LocalDate dob = dobPicker.getValue() ;
			String password = passwordField.getText();
			
			try {
				
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        		String formattedDob = dob.format(formatter);
				validate(username, password, formattedDob, email);
				NewUser user = new NewUser(username, password,formattedDob,email);
				cc.register(user);
				
				if(!isAuthenticated){
					statusLabel.setText("Register failed. Try again.");
					statusLabel.setStyle("-fx-text-fill: red;");
				}
				
			} catch (InvalidCredentialException ex) {
				statusLabel.setText(ex.getMessage());
				statusLabel.setStyle("-fx-text-fill: red;");
			}
			
			catch (Exception ex) {
				statusLabel.setText("An error occurred during authentication.");
				statusLabel.setStyle("-fx-text-fill: red;");
				ex.printStackTrace();
			}
		});

		VBox dialogLayout = new VBox(10);
        dialogLayout.setAlignment(Pos.CENTER);
        dialogLayout.getChildren().addAll(
                new Label("Username:"), usernameField,
				new Label("Email:"), emailField, 
            	new Label("Date of Birth:"), dobPicker,
                new Label("Password:"), passwordField,
                loginSubmitButton,
				statusLabel
        );
		dialogLayout.setMaxWidth(400);
		
		VBox LoginLayout = new VBox(100);
		LoginLayout.setAlignment(Pos.CENTER);
		LoginLayout.getChildren().addAll(topBar,dialogLayout);
		
		Scene dialogScene = new Scene(LoginLayout,primaryStage.getWidth(), primaryStage.getHeight());
        primaryStage.setScene(dialogScene);
		primaryStage.setMaximized(true); 
        primaryStage.show();
	}

	public static void validate(String username, String password,String dob,String email) throws InvalidCredentialException {
    	int MIN_PASSWORD_LENGTH = 8;
        if (password.isBlank() || username.isBlank() || dob.isBlank() || email.isBlank()) {
        	throw new InvalidCredentialException("User credential musn't be empty");
        }
        
        else if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new InvalidCredentialException("Password must be at least " + MIN_PASSWORD_LENGTH + " characters long.");
        }
      
    }

	public static void validate(String username, String password) throws InvalidCredentialException {
    	int MIN_PASSWORD_LENGTH = 8;
        if (password.isBlank() || username.isBlank()) {
        	throw new InvalidCredentialException("User credential musn't be empty");
        }
        
        else if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new InvalidCredentialException("Password must be at least " + MIN_PASSWORD_LENGTH + " characters long.");
        }
      
    }

	public static void main(String[] args) {
        launch(args);
    }
	
}
