package controllers;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.User;
import services.AuthService;

public class AuthController {
    // Get login scene
    public Scene getLoginScene(Stage primaryStage) {
        // Create layout
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        // Add components
        Text sceneTitle = new Text("Industrial Project Management System");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(sceneTitle, 0, 0, 2, 1);

        Label usernameLabel = new Label("Username:");
        grid.add(usernameLabel, 0, 1);

        TextField usernameField = new TextField();
        grid.add(usernameField, 1, 1);

        Label passwordLabel = new Label("Password:");
        grid.add(passwordLabel, 0, 2);

        PasswordField passwordField = new PasswordField();
        grid.add(passwordField, 1, 2);

        Button loginButton = new Button("Login");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(loginButton);
        grid.add(hbBtn, 1, 4);

        // Add login functionality
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Login Failed",
                        "Username and password cannot be empty.");
                return;
            }

            User user = AuthService.login(username, password);

            if (user != null) {
                // Login successful, redirect to appropriate dashboard
                switch (user.getRole()) {
                    case STUDENT:
                        StudentController studentController = new StudentController();
                        primaryStage.setScene(studentController.getStudentDashboardScene(primaryStage));
                        break;
                    case ADVISOR:
                        AdvisorController advisorController = new AdvisorController();
                        primaryStage.setScene(advisorController.getAdvisorDashboardScene(primaryStage));
                        break;
                    case COORDINATOR:
                        CoordinatorController coordinatorController = new CoordinatorController();
                        primaryStage.setScene(coordinatorController.getCoordinatorDashboardScene(primaryStage));
                        break;
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Login Failed",
                        "Invalid username or password.");
            }
        });

        // For demonstration, show sample login credentials
        Text demoCredentials = new Text("Demo Accounts:\n" +
                "Student: username=student, password=password\n" +
                "Advisor: username=advisor, password=password\n" +
                "Coordinator: username=coordinator, password=password");
        demoCredentials.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));
        grid.add(demoCredentials, 0, 6, 2, 1);

        return new Scene(grid, 800, 600);
    }

    // Show an alert dialog
    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}