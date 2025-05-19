package services;

import models.Project;
import models.Project.Status;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectService {
    public static List<Project> getProjectsByStudentId(int studentId) {
        return DatabaseService.getTable("projects", Project.class).stream()
            .filter(p -> p.getStudentId() == studentId)
            .collect(Collectors.toList());
    }
    
    public static List<Project> getProjectsByAdvisorId(int advisorId) {
        return DatabaseService.getTable("projects", Project.class).stream()
            .filter(p -> p.getAdvisorId() != null && p.getAdvisorId() == advisorId)
            .collect(Collectors.toList());
    }
    
    public static boolean createProject(String title, String description, int studentId) {
        List<Project> projects = DatabaseService.getTable("projects", Project.class);
        int newId = projects.size() + 1;
        projects.add(new Project(newId, title, description, Status.PENDING, studentId, null));
        return true;
    }
    
    public static boolean updateProjectStatus(int projectId, Status status) {
        return DatabaseService.getTable("projects", Project.class).stream()
            .filter(p -> p.getId() == projectId)
            .findFirst()
            .map(p -> {
                p.setStatus(status);
                return true;
            })
            .orElse(false);
    }
    
    public static boolean assignAdvisor(int projectId, int advisorId) {
        if (getProjectCountByAdvisorId(advisorId) >= 2) {
            return false;
        }
        
        return DatabaseService.getTable("projects", Project.class).stream()
            .filter(p -> p.getId() == projectId)
            .findFirst()
            .map(p -> {
                p.setAdvisorId(advisorId);
                return true;
            })
            .orElse(false);
    }
    
    public static int getProjectCountByAdvisorId(int advisorId) {
        return (int) DatabaseService.getTable("projects", Project.class).stream()
            .filter(p -> p.getAdvisorId() != null && p.getAdvisorId() == advisorId)
            .count();
    }
    
    public static List<Project> getAllProjects() {
        return new ArrayList<>(DatabaseService.getTable("projects", Project.class));
    }
    
    public static List<Project> getApprovedProjects() {
        return DatabaseService.getTable("projects", Project.class).stream()
            .filter(p -> p.getStatus() == Status.APPROVED)
            .collect(Collectors.toList());
    }
    
    public static List<Project> getPendingProjects() {
        return DatabaseService.getTable("projects", Project.class).stream()
            .filter(p -> p.getStatus() == Status.PENDING)
            .collect(Collectors.toList());
    }
}