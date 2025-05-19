package controllers;

import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.Project;
import models.Report;
import models.User;
import services.AuthService;
import services.ProjectService;
import services.ReportService;
import services.UserService;

public class StudentController{
    private User currentUser;

public StudentController() {
    this.currentUser = AuthService.getCurrentUser();
}

// Get student dashboard scene
public Scene getStudentDashboardScene(Stage primaryStage) {
    BorderPane borderPane = new BorderPane();

    // Header with logout button
    HBox header = new HBox();
    header.setPadding(new Insets(15, 12, 15, 12));
    header.setSpacing(10);
    header.setStyle("-fx-background-color: #336699;");

    Text title = new Text("Student Dashboard");
    title.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
    title.setStyle("-fx-fill: white;");

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    Button logoutButton = new Button("Logout");
    logoutButton.setOnAction(e -> {
        AuthService.logout();
        AuthController authController = new AuthController();
        primaryStage.setScene(authController.getLoginScene(primaryStage));
    });

    header.getChildren().addAll(title, spacer, logoutButton);
    borderPane.setTop(header);

    // Tab pane for different functionalities
    TabPane tabPane = new TabPane();

    // Project submission tab
    Tab projectTab = new Tab("Submit Project");
    projectTab.setClosable(false);
    projectTab.setContent(createProjectSubmissionTab(primaryStage));

    // Weekly report tab
    Tab reportTab = new Tab("Weekly Reports");
    reportTab.setClosable(false);
    reportTab.setContent(createWeeklyReportTab(primaryStage));

    tabPane.getTabs().addAll(projectTab, reportTab);
    borderPane.setCenter(tabPane);

    return new Scene(borderPane, 800, 600);
}

// Create project submission tab content
private VBox createProjectSubmissionTab(Stage primaryStage) {
    VBox vbox = new VBox();
    vbox.setPadding(new Insets(10));
    vbox.setSpacing(10);

    // Title for the section
    Text sectionTitle = new Text("Submit New Project");
    sectionTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 16));

    // Form for project submission
    GridPane grid = new GridPane();
    grid.setVgap(10);
    grid.setHgap(10);
    grid.setPadding(new Insets(10));

    Label titleLabel = new Label("Project Title:");
    grid.add(titleLabel, 0, 0);

    TextField titleField = new TextField();
    grid.add(titleField, 1, 0);

    Label descriptionLabel = new Label("Description:");
    grid.add(descriptionLabel, 0, 1);

    TextArea descriptionArea = new TextArea();
    descriptionArea.setPrefRowCount(5);
    grid.add(descriptionArea, 1, 1);

    Button submitButton = new Button("Submit Project");
    grid.add(submitButton, 1, 2);

    submitButton.setOnAction(e -> {
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();

        if (title.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Submission Failed",
                      "Project title cannot be empty.");
            return;
        }

        boolean success = ProjectService.createProject(title, description, currentUser.getId());

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Project Submitted",
                      "Your project has been submitted for approval.");
            titleField.clear();
            descriptionArea.clear();
            refreshProjectList(vbox);
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Submission Failed",
                      "Failed to submit project. Please try again.");
        }
    });

    // List of submitted projects
    Text projectListTitle = new Text("My Projects");
    projectListTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 16));

    ListView<Project> projectListView = new ListView<>();
    projectListView.setCellFactory(param -> new ListCell<Project>() {
        @Override
        protected void updateItem(Project project, boolean empty) {
            super.updateItem(project, empty);

            if (empty || project == null) {
                setText(null);
            } else {
                setText(project.getTitle() + " (Status: " + project.getStatus() + ")");
            }
        }
    });

    // Load projects
    List<Project> projects = ProjectService.getProjectsByStudentId(currentUser.getId());
    projectListView.getItems().addAll(projects);

    vbox.getChildren().addAll(sectionTitle, grid, projectListTitle, projectListView);

    return vbox;
}

// Create weekly report tab content
private VBox createWeeklyReportTab(Stage primaryStage) {
    VBox vbox = new VBox();
    vbox.setPadding(new Insets(10));
    vbox.setSpacing(10);

    // Title for the section
    Text sectionTitle = new Text("Submit Weekly Report");
    sectionTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 16));

    // Form for report submission
    GridPane grid = new GridPane();
    grid.setVgap(10);
    grid.setHgap(10);
    grid.setPadding(new Insets(10));

    Label projectLabel = new Label("Project:");
    grid.add(projectLabel, 0, 0);

    // Project dropdown
    ComboBox<Project> projectComboBox = new ComboBox<>();
    projectComboBox.setCellFactory(param -> new ListCell<Project>() {
        @Override
        protected void updateItem(Project project, boolean empty) {
            super.updateItem(project, empty);

            if (empty || project == null) {
                setText(null);
            } else {
                setText(project.getTitle());
            }
        }
    });
    projectComboBox.setButtonCell(new ListCell<Project>() {
        @Override
        protected void updateItem(Project project, boolean empty) {
            super.updateItem(project, empty);

            if (empty || project == null) {
                setText(null);
            } else {
                setText(project.getTitle());
            }
        }
    });

    // Load approved projects
    List<Project> approvedProjects = ProjectService.getProjectsByStudentId(currentUser.getId());
    approvedProjects.removeIf(p -> p.getStatus() != Project.Status.APPROVED);
    projectComboBox.getItems().addAll(approvedProjects);

    grid.add(projectComboBox, 1, 0);

    Label weekLabel = new Label("Week:");
    grid.add(weekLabel, 0, 1);

    ComboBox<Integer> weekComboBox = new ComboBox<>();
    for (int i = 1; i <= 16; i++) {
        weekComboBox.getItems().add(i);
    }
    grid.add(weekComboBox, 1, 1);

    Label contentLabel = new Label("Report Content:");
    grid.add(contentLabel, 0, 2);

    TextArea contentArea = new TextArea();
    contentArea.setPrefRowCount(10);
    grid.add(contentArea, 1, 2);

    Button submitButton = new Button("Submit Report");
    grid.add(submitButton, 1, 3);

    submitButton.setOnAction(e -> {
        Project selectedProject = projectComboBox.getValue();
        Integer selectedWeek = weekComboBox.getValue();
        String content = contentArea.getText().trim();

        if (selectedProject == null || selectedWeek == null || content.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Submission Failed",
                      "All fields are required.");
            return;
        }

        boolean success = ReportService.submitReport(content, selectedWeek, selectedProject.getId());

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Report Submitted",
                      "Your weekly report has been submitted.");
            contentArea.clear();
            weekComboBox.setValue(null);
            refreshReportList(vbox, selectedProject);
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Submission Failed",
                      "Failed to submit report. You may have already submitted a report for this week.");
        }
    });

    // List of submitted reports
    Text reportListTitle = new Text("My Reports");
    reportListTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 16));

    ListView<Report> reportListView = new ListView<>();
    reportListView.setCellFactory(param -> new ListCell<Report>() {
        @Override
        protected void updateItem(Report report, boolean empty) {
            super.updateItem(report, empty);

            if (empty || report == null) {
                setText(null);
            } else {
                setText("Week " + report.getWeek() + " Report");
            }
        }
    });

    // Update reports when project selection changes
    projectComboBox.setOnAction(event -> {
        Project selectedProject = projectComboBox.getValue();
        if (selectedProject != null) {
            refreshReportList(vbox, selectedProject);
        }
    });

    vbox.getChildren().addAll(sectionTitle, grid, reportListTitle, reportListView);

    return vbox;
}

// Refresh project list
private void refreshProjectList(VBox vbox) {
    ListView<Project> projectListView = (ListView<Project>) vbox.getChildren().get(3);
    projectListView.getItems().clear();

    List<Project> projects = ProjectService.getProjectsByStudentId(currentUser.getId());
    projectListView.getItems().addAll(projects);
}

// Refresh report list
private void refreshReportList(VBox vbox, Project project) {
    ListView<Report> reportListView = (ListView<Report>) vbox.getChildren().get(3);
    reportListView.getItems().clear();

    List<Report> reports = ReportService.getReportsByProjectId(project.getId());
    reportListView.getItems().addAll(reports);
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