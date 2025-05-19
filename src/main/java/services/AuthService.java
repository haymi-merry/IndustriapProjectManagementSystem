package services;

import models.User;
import java.util.List;

public class AuthService {
    private static User currentUser;
    
    public static User login(String username, String password) {
        List<User> users = DatabaseService.getTable("users", User.class);
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                currentUser = user;
                return user;
            }
        }
        return null;
    }
    
    public static User getCurrentUser() {
        return currentUser;
    }
    
    public static void logout() {
        currentUser = null;
    }
}