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

public class CoordinatorController{
    private User currentUser;

public CoordinatorController() {
    this.currentUser = AuthService.getCurrentUser();
}

// Get coordinator dashboard scene
public Scene getCoordinatorDashboardScene(Stage primaryStage) {
    BorderPane borderPane = new BorderPane();

    // Header with logout button
    HBox header = new HBox();
    header.setPadding(new Insets(15, 12, 15, 12));
    header.setSpacing(10);
    header.setStyle("-fx-background-color: #336699;");

    Text title = new Text("Coordinator Dashboard");
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

    // Assign advisors tab
    Tab assignTab = new Tab("Assign Advisors");
    assignTab.setClosable(false);
    assignTab.setContent(createAssignAdvisorsTab(primaryStage));

    // View reports tab
    Tab reportsTab = new Tab("View Reports");
    reportsTab.setClosable(false);
    reportsTab.setContent(createViewReportsTab(primaryStage));

    tabPane.getTabs().addAll(assignTab, reportsTab);
    borderPane.setCenter(tabPane);

    return new Scene(borderPane, 800, 600);
}

// Create assign advisors tab content
private VBox createAssignAdvisorsTab(Stage primaryStage) {
    VBox vbox = new VBox();
    vbox.setPadding(new Insets(10));
    vbox.setSpacing(10);

    // Title for the section
    Text sectionTitle = new Text("Assign Advisors to Projects");
    sectionTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 16));

    // Projects list
    Text projectsTitle = new Text("Approved Projects without Advisor");
    projectsTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));

    ListView<Project> projectsListView = new ListView<>();
    projectsListView.setCellFactory(param -> new ListCell<Project>() {
        @Override
        protected void updateItem(Project project, boolean empty) {
            super.updateItem(project, empty);

            if (empty || project == null) {
                setText(null);
            } else {
                User student = UserService.getUserById(project.getStudentId());
                String studentName = student != null ? student.getUsername() : "Unknown";
                setText(project.getTitle() + " (Student: " + studentName + ")");
            }
        }
    });

    // Advisor assignment section
    Text assignmentTitle = new Text("Assign Advisor");
    assignmentTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));

    GridPane assignmentGrid = new GridPane();
    assignmentGrid.setVgap(10);
    assignmentGrid.setHgap(10);
    assignmentGrid.setPadding(new Insets(10));

    Label projectLabel = new Label("Project:");
    assignmentGrid.add(projectLabel, 0, 0);

    Label projectValue = new Label("");
    assignmentGrid.add(projectValue, 1, 0);

    Label advisorLabel = new Label("Advisor:");
    assignmentGrid.add(advisorLabel, 0, 1);

    ComboBox<User> advisorComboBox = new ComboBox<>();
    advisorComboBox.setCellFactory(param -> new ListCell<User>() {
        @Override
        protected void updateItem(User user, boolean empty) {
            super.updateItem(user, empty);

            if (empty || user == null) {
                setText(null);
            } else {
                setText(user.getUsername() + " (" +
                       ProjectService.getProjectCountByAdvisorId(user.getId()) + " projects)");
            }
        }
    });
    advisorComboBox.setButtonCell(new ListCell<User>() {
        @Override
        protected void updateItem(User user, boolean empty) {
            super.updateItem(user, empty);

            if (empty || user == null) {
                setText(null);
            } else {
                setText(user.getUsername() + " (" +
                       ProjectService.getProjectCountByAdvisorId(user.getId()) + " projects)");
            }
        }
    });
    assignmentGrid.add(advisorComboBox, 1, 1);

    Button assignButton = new Button("Assign Advisor");
    assignmentGrid.add(assignButton, 1, 2);

    // Load projects
    refreshProjectsList(projectsListView);

    // Load advisors
    List<User> advisors = UserService.getAllAdvisors();
    advisorComboBox.getItems().addAll(advisors);

    // Show project details when a project is selected
    projectsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
        if (newSelection != null) {
            Project project = newSelection;
            User student = UserService.getUserById(project.getStudentId());
            String studentName = student != null ? student.getUsername() : "Unknown";

            projectValue.setText(project.getTitle() + " (Student: " + studentName + ")");
        }
    });

    // Assign advisor
    assignButton.setOnAction(e -> {
        Project selectedProject = projectsListView.getSelectionModel().getSelectedItem();
        User selectedAdvisor = advisorComboBox.getValue();

        if (selectedProject == null || selectedAdvisor == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Assignment Failed",
                      "Please select both project and advisor.");
            return;
        }

        int projectCount = ProjectService.getProjectCountByAdvisorId(selectedAdvisor.getId());
        if (projectCount >= 2) {
            showAlert(Alert.AlertType.ERROR, "Error", "Assignment Failed",
                      "Advisor already has maximum number of projects (2).");
            return;
        }

        boolean success = ProjectService.assignAdvisor(selectedProject.getId(), selectedAdvisor.getId());

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Advisor Assigned",
                      "Advisor has been assigned to the project.");
            refreshProjectsList(projectsListView);
            refreshAdvisorsList(advisorComboBox);
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Assignment Failed",
                      "Failed to assign advisor to project.");
        }
    });

    vbox.getChildren().addAll(sectionTitle, projectsTitle, projectsListView,
                             assignmentTitle, assignmentGrid);

    return vbox;
}

// Create view reports tab content
private VBox createViewReportsTab(Stage primaryStage) {
    VBox vbox = new VBox();
    vbox.setPadding(new Insets(10));
    vbox.setSpacing(10);

    // Title for the section
    Text sectionTitle = new Text("View All Reports");
    sectionTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 16));

    // Projects list
    Text projectsTitle = new Text("Select Project");
    projectsTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));

    ListView<Project> projectsListView = new ListView<>();
    projectsListView.setCellFactory(param -> new ListCell<Project>() {
        @Override
        protected void updateItem(Project project, boolean empty) {
            super.updateItem(project, empty);

            if (empty || project == null) {
                setText(null);
            } else {
                User student = UserService.getUserById(project.getStudentId());
                String studentName = student != null ? student.getUsername() : "Unknown";

                User advisor = null;
                String advisorName = "None";
                if (project.getAdvisorId() != null) {
                    advisor = UserService.getUserById(project.getAdvisorId());
                    advisorName = advisor != null ? advisor.getUsername() : "Unknown";
                }

                setText(project.getTitle() + " (Student: " + studentName + ", Advisor: " + advisorName + ")");
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

    Label commentsLabel = new Label("Advisor Comments:");
    reportDetailsGrid.add(commentsLabel, 0, 1);

    TextArea commentsArea = new TextArea();
    commentsArea.setEditable(false);
    commentsArea.setPrefRowCount(3);
    reportDetailsGrid.add(commentsArea, 1, 1);

    // Load all projects
    List<Project> allProjects = ProjectService.getAllProjects();
    projectsListView.getItems().addAll(allProjects);

    // Show reports when a project is selected
    projectsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
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

    vbox.getChildren().addAll(sectionTitle, projectsTitle, projectsListView,
                             reportsTitle, reportsListView, reportDetailsTitle, reportDetailsGrid);

    return vbox;
}

// Refresh projects list
private void refreshProjectsList(ListView<Project> listView) {
    listView.getItems().clear();

    List<Project> approvedProjects = ProjectService.getApprovedProjects();
    // Filter out projects that already have an advisor
    approvedProjects.removeIf(p -> p.getAdvisorId() != null);

    listView.getItems().addAll(approvedProjects);
}

// Refresh advisors list
private void refreshAdvisorsList(ComboBox<User> comboBox) {
    comboBox.getItems().clear();

    List<User> advisors = UserService.getAllAdvisors();
    comboBox.getItems().addAll(advisors);
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