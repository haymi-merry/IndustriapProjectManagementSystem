package services;

import models.Project;
import models.Report;
import models.User;
import models.User.Role;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseService {
    private static final Map<String, List<?>> memoryDb = new HashMap<>();
    
    static {
        initializeDatabase();
    }
    
    public static void initializeDatabase() {
        // Users
        List<User> users = new ArrayList<>();
        users.add(new User(1, "student", "password", Role.STUDENT));
        users.add(new User(2, "advisor", "password", Role.ADVISOR));
        users.add(new User(3, "coordinator", "password", Role.COORDINATOR));
        memoryDb.put("users", users);
        
        // Projects
        List<Project> projects = new ArrayList<>();
        projects.add(new Project(1, "E-Commerce Platform", "Online shopping system", 
            Project.Status.APPROVED, 1, 2));
        projects.add(new Project(2, "Inventory System", "Track products and stock", 
            Project.Status.PENDING, 1, null));
        memoryDb.put("projects", projects);
        
        // Reports
        List<Report> reports = new ArrayList<>();
        reports.add(new Report(1, "Completed requirements analysis", 1, 1, "Good start"));
        memoryDb.put("reports", reports);
    }
    
    @SuppressWarnings("unchecked")
    public static <T> List<T> getTable(String tableName, Class<T> type) {
        return (List<T>) memoryDb.get(tableName);
    }
    
    public static void closeConnection() {
        // No-op for in-memory database
    }
}