package models;

public class Project{
    public enum Status {
    PENDING, APPROVED, REJECTED
}

private int id;
private String title;
private String description;
private Status status;
private int studentId;
private Integer advisorId;  // Using Integer to allow null values

public Project() {
    this.status = Status.PENDING;
}

public Project(int id, String title, String description, Status status, int studentId, Integer advisorId) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.status = status;
    this.studentId = studentId;
    this.advisorId = advisorId;
}

// Getters and setters
public int getId() {
    return id;
}

public void setId(int id) {
    this.id = id;
}

public String getTitle() {
    return title;
}

public void setTitle(String title) {
    this.title = title;
}

public String getDescription() {
    return description;
}

public void setDescription(String description) {
    this.description = description;
}

public Status getStatus() {
    return status;
}

public void setStatus(Status status) {
    this.status = status;
}

public int getStudentId() {
    return studentId;
}

public void setStudentId(int studentId) {
    this.studentId = studentId;
}

public Integer getAdvisorId() {
    return advisorId;
}

public void setAdvisorId(Integer advisorId) {
    this.advisorId = advisorId;
}

@Override
public String toString() {
    return title + " (" + status + ")";
}

}