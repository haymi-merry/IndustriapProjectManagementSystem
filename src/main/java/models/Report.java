package models;

public class Report{
    private int id;
private String content;
private int week;
private int projectId;
private String comments;

public Report() {}

public Report(int id, String content, int week, int projectId, String comments) {
    this.id = id;
    this.content = content;
    this.week = week;
    this.projectId = projectId;
    this.comments = comments;
}

// Getters and setters
public int getId() {
    return id;
}

public void setId(int id) {
    this.id = id;
}

public String getContent() {
    return content;
}

public void setContent(String content) {
    this.content = content;
}

public int getWeek() {
    return week;
}

public void setWeek(int week) {
    this.week = week;
}

public int getProjectId() {
    return projectId;
}

public void setProjectId(int projectId) {
    this.projectId = projectId;
}

public String getComments() {
    return comments;
}

public void setComments(String comments) {
    this.comments = comments;
}

@Override
public String toString() {
    return "Week " + week + " Report";
}

}