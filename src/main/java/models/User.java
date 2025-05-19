package models;

public class User {
public enum Role {
    STUDENT, ADVISOR, COORDINATOR
}

private int id;
private String username;
private String password;
private Role role;

public User() {}

public User(int id, String username, String password, Role role) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.role = role;
}

// Getters and setters
public int getId() {
    return id;
}

public void setId(int id) {
    this.id = id;
}

public String getUsername() {
    return username;
}

public void setUsername(String username) {
    this.username = username;
}

public String getPassword() {
    return password;
}

public void setPassword(String password) {
    this.password = password;
}

public Role getRole() {
    return role;
}

public void setRole(Role role) {
    this.role = role;
}

@Override
public String toString() {
    return username + " (" + role + ")";
}

}
