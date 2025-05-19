package services;

import models.User;
import models.User.Role;
import java.util.List;
import java.util.stream.Collectors;

public class UserService {
    public static User getUserById(int userId) {
        return DatabaseService.getTable("users", User.class).stream()
            .filter(u -> u.getId() == userId)
            .findFirst()
            .orElse(null);
    }
    
    public static List<User> getAllAdvisors() {
        return getUsersByRole(Role.ADVISOR);
    }
    
    public static List<User> getAllStudents() {
        return getUsersByRole(Role.STUDENT);
    }
    
    private static List<User> getUsersByRole(Role role) {
        return DatabaseService.getTable("users", User.class).stream()
            .filter(u -> u.getRole() == role)
            .collect(Collectors.toList());
    }
}