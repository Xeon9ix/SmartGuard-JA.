package com.scamguardja.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ReportService {

    private final Map<String, Map<String, Object>> reports = new ConcurrentHashMap<>();
    private final Map<String, Integer> communityCounts = new ConcurrentHashMap<>();

    public Map<String, Object> reportEmail(String emailId, String sender, String subject,
                                           String preview, int riskScore, List<String> reasons,
                                           boolean autoReported) {

        String caseId = "SG-" + ThreadLocalRandom.current().nextInt(10000, 99999);

        Map<String, Object> report = new HashMap<>();
        report.put("caseId", caseId);
        report.put("emailId", emailId);
        report.put("sender", sender);
        report.put("subject", subject);
        report.put("preview", preview);
        report.put("riskScore", riskScore);
        report.put("reasons", reasons);
        report.put("autoReported", autoReported);
        report.put("reportedAt", LocalDateTime.now().toString());
        report.put("status", "Submitted to ScamGuard Security");

        reports.put(emailId, report);
        communityCounts.put(emailId, communityCounts.getOrDefault(emailId, 0) + 1);

        return report;
    }

    public int getCommunityCount(String emailId) {
        return communityCounts.getOrDefault(emailId, 0);
    }

    public boolean hasReport(String emailId) {
        return reports.containsKey(emailId);
    }

    public boolean wasAutoReported(String emailId) {
        Map<String, Object> report = reports.get(emailId);

        if (report == null) {
            return false;
        }

        Object value = report.get("autoReported");
        return value instanceof Boolean && (Boolean) value;
    }

    public Collection<Map<String, Object>> getAllReports() {
        return reports.values();
    }

    public long getAutoReportCount() {
        return reports.values()
                .stream()
                .filter(r -> Boolean.TRUE.equals(r.get("autoReported")))
                .count();
    }
}