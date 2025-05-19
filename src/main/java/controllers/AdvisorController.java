package controllers;

import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
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

public class AdvisorController{
    private User currentUser;

public AdvisorController() {
    this.currentUser = AuthService.getCurrentUser();
}

// Get advisor dashboard scene
public Scene getAdvisorDashboardScene(Stage primaryStage) {
    BorderPane borderPane = new BorderPane();

    // Header with logout button
    HBox header = new HBox();
    header.setPadding(new Insets(15, 12, 15, 12));
    header.setSpacing(10);
    header.setStyle("-fx-background-color: #336699;");

    Text title = new Text("Advisor Dashboard");
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

    // Project review tab
    Tab projectTab = new Tab("Review Projects");
    projectTab.setClosable(false);
    projectTab.setContent(createProjectReviewTab(primaryStage));

    // Report review tab
    Tab reportTab = new Tab("Review Reports");
    reportTab.setClosable(false);
    reportTab.setContent(createReportReviewTab(primaryStage));

    tabPane.getTabs().addAll(projectTab, reportTab);
    borderPane.setCenter(tabPane);

    return new Scene(borderPane, 800, 600);
}

// Create project review tab content
private VBox createProjectReviewTab(Stage primaryStage) {
    VBox vbox = new VBox();
    vbox.setPadding(new Insets(10));
    vbox.setSpacing(10);

    // Title for the section
    Text sectionTitle = new Text("Review Projects");
    sectionTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 16));

    // List of assigned projects
    Text assignedProjectsTitle = new Text("My Assigned Projects");
    assignedProjectsTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));

    ListView<Project> assignedProjectsListView = new ListView<>();
    assignedProjectsListView.setCellFactory(param -> new ListCell<Project>() {
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

    // Project details section
    Text projectDetailsTitle = new Text("Project Details");
    projectDetailsTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));

    GridPane projectDetailsGrid = new GridPane();
    projectDetailsGrid.setVgap(10);
    projectDetailsGrid.setHgap(10);
    projectDetailsGrid.setPadding(new Insets(10));

    Label projectTitleLabel = new Label("Title:");
    projectDetailsGrid.add(projectTitleLabel, 0, 0);

    Label projectTitleValue = new Label("");
    projectDetailsGrid.add(projectTitleValue, 1, 0);

    Label projectDescriptionLabel = new Label("Description:");
    projectDetailsGrid.add(projectDescriptionLabel, 0, 1);

    TextArea projectDescriptionValue = new TextArea();
    projectDescriptionValue.setEditable(false);
    projectDescriptionValue.setPrefRowCount(5);
    projectDetailsGrid.add(projectDescriptionValue, 1, 1);

    Label projectStudentLabel = new Label("Student:");
    projectDetailsGrid.add(projectStudentLabel, 0, 2);

    Label projectStudentValue = new Label("");
    projectDetailsGrid.add(projectStudentValue, 1, 2);

    // Approval buttons
    HBox approvalButtonsBox = new HBox();
    approvalButtonsBox.setSpacing(10);
    approvalButtonsBox.setPadding(new Insets(10, 0, 0, 0));

    Button approveButton = new Button("Approve");
    Button rejectButton = new Button("Reject");

    approvalButtonsBox.getChildren().addAll(approveButton, rejectButton);
    projectDetailsGrid.add(approvalButtonsBox, 1, 3);

    // Load assigned projects
    List<Project> assignedProjects = ProjectService.getProjectsByAdvisorId(currentUser.getId());
    assignedProjectsListView.getItems().addAll(assignedProjects);

    // Show project details when a project is selected
    assignedProjectsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
        if (newSelection != null) {
            Project project = newSelection;
            User student = UserService.getUserById(project.getStudentId());

            projectTitleValue.setText(project.getTitle());
            projectDescriptionValue.setText(project.getDescription());
            projectStudentValue.setText(student != null ? student.getUsername() : "Unknown");

            // Enable buttons based on project status
            if (project.getStatus() == Project.Status.PENDING) {
                approveButton.setDisable(false);
                rejectButton.setDisable(false);
            } else {
                approveButton.setDisable(true);
                rejectButton.setDisable(true);
            }
        }
    });

    // Approve project
    approveButton.setOnAction(e -> {
        Project selectedProject = assignedProjectsListView.getSelectionModel().getSelectedItem();
        if (selectedProject != null) {
            boolean success = ProjectService.updateProjectStatus(selectedProject.getId(), Project.Status.APPROVED);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Project Approved",
                          "The project has been approved.");
                refreshAssignedProjects(assignedProjectsListView);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Action Failed",
                          "Failed to approve project.");
            }
        }
    });

    // Reject project
    rejectButton.setOnAction(e -> {
        Project selectedProject = assignedProjectsListView.getSelectionModel().getSelectedItem();
        if (selectedProject != null) {
            boolean success = ProjectService.updateProjectStatus(selectedProject.getId(), Project.Status.REJECTED);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Project Rejected",
                          "The project has been rejected.");
                refreshAssignedProjects(assignedProjectsListView);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Action Failed",
                          "Failed to reject project.");
            }
        }
    });

    vbox.getChildren().addAll(sectionTitle, assignedProjectsTitle, assignedProjectsListView,
                             projectDetailsTitle, projectDetailsGrid);

    return vbox;
}

// Create report review tab content
private VBox createReportReviewTab(Stage primaryStage) {
    VBox vbox = new VBox();
    vbox.setPadding(new Insets(10));
    vbox.setSpacing(10);

    // Title for the section
    Text sectionTitle = new Text("Review Reports");
    sectionTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 16));

    // Project selection
    Text projectSelectionTitle = new Text("Select Project");
    projectSelectionTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));

    ListView<Project> projectListView = new ListView<>();
    projectListView.setCellFactory(param -> new ListCell<Project>() {
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

    // Reports list
    Text reportsTitle = new Text("Weekly Reports");
    reportsTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));

    ListView<Report> reportsListView = new ListView<>();
    reportsListView.setCellFactory(param -> new ListCell<Report>() {
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

    // Report details
    Text reportDetailsTitle = new Text("Report Details");
    reportDetailsTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));

    GridPane reportDetailsGrid = new GridPane();
    reportDetailsGrid.setVgap(10);
    reportDetailsGrid.setHgap(10);
    reportDetailsGrid.setPadding(new Insets(10));

    Label reportContentLabel = new Label("Content:");
    reportDetailsGrid.add(reportContentLabel, 0, 0);

    TextArea reportContentArea = new TextArea();
    reportContentArea.setEditable(false);
    reportContentArea.setPrefRowCount(5);
    reportDetailsGrid.add(reportContentArea, 1, 0);

    Label commentsLabel = new Label("Comments:");
    reportDetailsGrid.add(commentsLabel, 0, 1);

    TextArea commentsArea = new TextArea();
    commentsArea.setPrefRowCount(3);
    reportDetailsGrid.add(commentsArea, 1, 1);

    Button submitCommentsButton = new Button("Submit Comments");
    reportDetailsGrid.add(submitCommentsButton, 1, 2);

    // Load assigned projects
    List<Project> assignedProjects = ProjectService.getProjectsByAdvisorId(currentUser.getId());
    projectListView.getItems().addAll(assignedProjects);

    // Show reports when a project is selected
    projectListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
        if (newSelection != null) {
            Project project = newSelection;
            reportsListView.getItems().clear();

            List<Report> reports = ReportService.getReportsByProjectId(project.getId());
            reportsListView.getItems().addAll(reports);
        }
    });

    // Show report details when a report is selected
    reportsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
        if (newSelection != null) {
            Report report = newSelection;

            reportContentArea.setText(report.getContent());
            commentsArea.setText(report.getComments());
        }
    });

    // Submit comments
    submitCommentsButton.setOnAction(e -> {
        Report selectedReport = reportsListView.getSelectionModel().getSelectedItem();
        if (selectedReport != null) {
            String comments = commentsArea.getText();

            boolean success = ReportService.addComments(selectedReport.getId(), comments);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Comments Submitted",
                          "Your comments have been submitted.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Submission Failed",
                          "Failed to submit comments.");
            }
        }
    });

    vbox.getChildren().addAll(sectionTitle, projectSelectionTitle, projectListView,
                             reportsTitle, reportsListView, reportDetailsTitle, reportDetailsGrid);

    return vbox;
}

// Refresh assigned projects list
private void refreshAssignedProjects(ListView<Project> listView) {
    listView.getItems().clear();

    List<Project> assignedProjects = ProjectService.getProjectsByAdvisorId(currentUser.getId());
    listView.getItems().addAll(assignedProjects);
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