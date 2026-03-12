package client;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.Map;
import java.io.IOException;
import java.util.HashMap;
import utils.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

class Profile {
    private ClientConnection clientConnection;
    private Stage primaryStage;
    private int userID;

    public Profile(ClientConnection clientConnection, Stage primaryStage, int Id) {
        this.clientConnection = clientConnection;
        this.primaryStage = primaryStage;
        this.userID = Id;
    }

    public void start() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // Back button at the top left
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.TOP_LEFT);
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-size: 14px;");
        backButton.setOnAction(e -> clientConnection.startChatUI());
        topBar.getChildren().add(backButton);
        root.setTop(topBar);
        
        // User details using a GridPane
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(15);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20));
        
        Label usernameLabel = new Label("Username:");
        Label usernameValue = new Label(clientConnection.user.getUsername());
        Button usernameChangeButton = new Button("Change");
        usernameChangeButton.setOnAction(e -> {
            try {
                changeUsername();
            } catch (IOException ex) {
                ex.printStackTrace(); // Handle the exception appropriately
            }
        });
        
        Label passwordLabel = new Label("Password:");
        Label passwordValue = new Label("");
        Button passwordChangeButton = new Button("Change");
        passwordChangeButton.setOnAction(e -> changePassword());
        
        Label gmailLabel = new Label("Gmail:");
        Label gmailValue = new Label(clientConnection.user.getEmail());
        Button gmailChangeButton = new Button("Change");
        gmailChangeButton.setOnAction(e -> changeGmail());
        
        Label dobLabel = new Label("Date of Birth:");
        Label dobValue = new Label(clientConnection.user.getDob());
        Button dobChangeButton = new Button("Change");
        dobChangeButton.setOnAction(e -> changeDob());
        
        // Styling labels
        usernameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        passwordLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        gmailLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        dobLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        usernameValue.setStyle("-fx-font-size: 16px;");
        passwordValue.setStyle("-fx-font-size: 16px;");
        gmailValue.setStyle("-fx-font-size: 16px;");
        dobValue.setStyle("-fx-font-size: 16px;");
        
        // Adding elements to GridPane
        gridPane.add(usernameLabel, 0, 0);
        gridPane.add(usernameValue, 1, 0);
        gridPane.add(usernameChangeButton, 2, 0);
        
        gridPane.add(passwordLabel, 0, 1);
        gridPane.add(passwordValue, 1, 1);
        gridPane.add(passwordChangeButton, 2, 1);
        
        gridPane.add(gmailLabel, 0, 2);
        gridPane.add(gmailValue, 1, 2);
        gridPane.add(gmailChangeButton, 2, 2);
        
        gridPane.add(dobLabel, 0, 3);
        gridPane.add(dobValue, 1, 3);
        gridPane.add(dobChangeButton, 2, 3);
        
        root.setCenter(gridPane);

        Scene scene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
        primaryStage.setTitle("Profile Page");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private void changeUsername() throws IOException {

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #008CBA; -fx-text-fill: white; -fx-font-size: 14px;");
        backButton.setOnAction(e -> start());


        VBox loginLayout = new VBox(20);
        loginLayout.setAlignment(Pos.CENTER);

        TextField changeField = new TextField();
        changeField.setMaxWidth(300);
        changeField.setPromptText("Enter your new username");

        PasswordField passwordField = new PasswordField();
        passwordField.setMaxWidth(300);
        passwordField.setPromptText("Enter your password");

        Button loginSubmitButton = new Button("Change");
        loginSubmitButton.setStyle("-fx-background-color: #008CBA; -fx-text-fill: white; -fx-font-size: 14px;");

        Label statusLabel = new Label();

        loginSubmitButton.setOnAction(e -> {
            String newUsername = changeField.getText().trim();
            String password = passwordField.getText().trim();

            if (newUsername.isEmpty() || password.isEmpty()) {
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("Both fields are required!");
                return;
            }

            if (!password.equals(clientConnection.user.getPassword())) {
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("Incorrect password!");
                return;
            }

            try {
                User user = new User(userID);
                user.addChange("username", newUsername);
                clientConnection.oos.writeObject(user);
                clientConnection.oos.flush();

                start();
                statusLabel.setText("Username change request sent.");
            } catch (IOException ex) {
                statusLabel.setText("Error sending request.");
                ex.printStackTrace();
            }
        });

        VBox dialogLayout = new VBox(10);
        dialogLayout.setAlignment(Pos.CENTER);
        dialogLayout.getChildren().addAll(
                new Label("New Username:"), changeField,
                new Label("Password:"), passwordField,
                loginSubmitButton,
                statusLabel
        );

        loginLayout.getChildren().addAll(backButton, dialogLayout);

        Scene dialogScene = new Scene(loginLayout, primaryStage.getWidth(), primaryStage.getHeight());
        primaryStage.setScene(dialogScene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private void changePassword() {

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #008CBA; -fx-text-fill: white; -fx-font-size: 14px;");
        backButton.setOnAction(e -> start());

        System.out.println("Change Password clicked");

        VBox passwordLayout = new VBox(20);
        passwordLayout.setAlignment(Pos.CENTER);

        PasswordField currentPasswordField = new PasswordField();
        currentPasswordField.setMaxWidth(300);
        currentPasswordField.setPromptText("Enter your current password");

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setMaxWidth(300);
        newPasswordField.setPromptText("Enter your new password");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setMaxWidth(300);
        confirmPasswordField.setPromptText("Confirm your new password");

        Button passwordSubmitButton = new Button("Change");
        passwordSubmitButton.setStyle("-fx-background-color: #008CBA; -fx-text-fill: white; -fx-font-size: 14px;");

        Label statusLabel = new Label();

        passwordSubmitButton.setOnAction(e -> {
            String currentPassword = currentPasswordField.getText().trim();
            String newPassword = newPasswordField.getText().trim();
            String confirmPassword = confirmPasswordField.getText().trim();

            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                statusLabel.setText("All fields are required!");
                return;
            }

            if (!currentPassword.equals(clientConnection.user.getPassword())) {
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("Incorrect current password!");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("Passwords do not match!");
                return;
            }

            try {
                User user = new User(userID);
                user.addChange("password", newPassword);
                clientConnection.oos.writeObject(user);
                clientConnection.oos.flush();

                start();
                statusLabel.setText("Password change request sent.");
            } catch (IOException ex) {
                statusLabel.setText("Error sending request.");
                ex.printStackTrace();
            }
        });

        VBox dialogLayout = new VBox(10);
        dialogLayout.setAlignment(Pos.CENTER);
        dialogLayout.getChildren().addAll(
                new Label("Current Password:"), currentPasswordField,
                new Label("New Password:"), newPasswordField,
                new Label("Confirm Password:"), confirmPasswordField,
                passwordSubmitButton,
                statusLabel
        );

        passwordLayout.getChildren().addAll(backButton, dialogLayout);

        Scene dialogScene = new Scene(passwordLayout, primaryStage.getWidth(), primaryStage.getHeight());
        primaryStage.setScene(dialogScene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private void changeGmail() {
        primaryStage.setTitle("Change Gmail");

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #008CBA; -fx-text-fill: white; -fx-font-size: 14px;");
        backButton.setOnAction(e -> start());

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);

        TextField changeField = new TextField();
        changeField.setMaxWidth(300);
        changeField.setPromptText("Enter your new Gmail address");

        PasswordField passwordField = new PasswordField();
        passwordField.setMaxWidth(300);
        passwordField.setPromptText("Enter your password");

        Button changeButton = new Button("Change");
        changeButton.setStyle("-fx-background-color: #008CBA; -fx-text-fill: white; -fx-font-size: 14px;");

        Label statusLabel = new Label();

        changeButton.setOnAction(e -> {
            String newGmail = changeField.getText().trim();
            String password = passwordField.getText().trim();

            if (newGmail.isEmpty() || password.isEmpty()) {
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("Both fields are required!");
                return;
            }

            if (!password.equals(clientConnection.user.getPassword())) {
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("Incorrect password!");
                return;
            }

            try {
                User user = new User(userID);
                user.addChange("gmail", newGmail);
                clientConnection.oos.writeObject(user);
                clientConnection.oos.flush();
                start();
                statusLabel.setText("Gmail change request sent.");
            } catch (IOException ex) {
                statusLabel.setText("Error sending request.");
                ex.printStackTrace();
            }
        });

        VBox dialogLayout = new VBox(10);
        dialogLayout.setAlignment(Pos.CENTER);
        dialogLayout.getChildren().addAll(
                new Label("New Gmail:"), changeField,
                new Label("Password:"), passwordField,
                changeButton,
                statusLabel
        );

        layout.getChildren().addAll(backButton, dialogLayout);

        Scene dialogScene = new Scene(layout, primaryStage.getWidth(), primaryStage.getHeight());
        primaryStage.setScene(dialogScene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private void changeDob() {
        primaryStage.setTitle("Change Date of Birth");

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #008CBA; -fx-text-fill: white; -fx-font-size: 14px;");
        backButton.setOnAction(e -> start());

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);

        DatePicker datePicker = new DatePicker();
        datePicker.setMaxWidth(300);

        PasswordField passwordField = new PasswordField();
        passwordField.setMaxWidth(300);
        passwordField.setPromptText("Enter your password");

        Button changeButton = new Button("Change");
        changeButton.setStyle("-fx-background-color: #008CBA; -fx-text-fill: white; -fx-font-size: 14px;");

        Label statusLabel = new Label();

        changeButton.setOnAction(e -> {
            // Get the selected date from the DatePicker
            LocalDate newDob = datePicker.getValue();
            String password = passwordField.getText().trim();

            // Check if date and password are entered
            if (newDob == null || password.isEmpty()) {
                statusLabel.setText("Both fields are required!");
                statusLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            // Check if the password matches
            if (!password.equals(clientConnection.user.getPassword())) {
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("Incorrect password!");
                return;
            }

            try {
                // Format the LocalDate as a String using DateTimeFormatter
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String formattedDob = newDob.format(formatter);

                // Create the user object and add the dob change
                User user = new User(userID);
                user.addChange("dob", formattedDob);
                clientConnection.user.setDob(formattedDob);
                // Send the request to the server
                clientConnection.oos.writeObject(user);
                clientConnection.oos.flush();

                // Update the UI
                start();
                statusLabel.setText("Date of Birth change request sent.");
            } catch (IOException ex) {
                statusLabel.setText("Error sending request.");
                ex.printStackTrace();
            }
        });

        VBox dialogLayout = new VBox(10);
        dialogLayout.setAlignment(Pos.CENTER);
        dialogLayout.getChildren().addAll(
                new Label("New Date of Birth:"), datePicker,
                new Label("Password:"), passwordField,
                changeButton,
                statusLabel
        );

        layout.getChildren().addAll(backButton, dialogLayout);

        Scene dialogScene = new Scene(layout, primaryStage.getWidth(), primaryStage.getHeight());
        primaryStage.setScene(dialogScene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }


}
