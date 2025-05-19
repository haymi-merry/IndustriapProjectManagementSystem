import controllers.AuthController;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.DatabaseService;

public class Main extends Application {
    private static final String APP_TITLE = "Industrial Project Management System";
    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 600;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize in-memory data
            DatabaseService.initializeDatabase();

            // Load the login scene
            AuthController authController = new AuthController();
            Scene loginScene = authController.getLoginScene(primaryStage);

            // Set up the stage
            primaryStage.setTitle(APP_TITLE);
            primaryStage.setScene(loginScene);
            primaryStage.setWidth(DEFAULT_WIDTH);
            primaryStage.setHeight(DEFAULT_HEIGHT);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Startup Error");
            alert.setHeaderText("Application failed to start");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @Override
    public void stop() {
        DatabaseService.closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}