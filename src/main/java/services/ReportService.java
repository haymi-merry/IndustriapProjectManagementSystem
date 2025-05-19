package services;

import models.Report;
import java.util.List;
import java.util.stream.Collectors;

public class ReportService {
    public static List<Report> getReportsByProjectId(int projectId) {
        return DatabaseService.getTable("reports", Report.class).stream()
            .filter(r -> r.getProjectId() == projectId)
            .collect(Collectors.toList());
    }
    
    public static boolean submitReport(String content, int week, int projectId) {
        List<Report> reports = DatabaseService.getTable("reports", Report.class);
        int newId = reports.size() + 1;
        reports.add(new Report(newId, content, week, projectId, ""));
        return true;
    }
    
    public static boolean addComments(int reportId, String comments) {
        return DatabaseService.getTable("reports", Report.class).stream()
            .filter(r -> r.getId() == reportId)
            .findFirst()
            .map(r -> {
                r.setComments(comments);
                return true;
            })
            .orElse(false);
    }
}