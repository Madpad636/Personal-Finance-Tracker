package com.financeapp.service;

import java.util.UUID;

// TODO: generate monthly/category/budget/savings reports and export as PDF/CSV
// (itext7 and opencsv are already on the classpath - see pom.xml).
public interface ReportService {
    byte[] exportMonthlyReportCsv(UUID userId, int month, int year);
    byte[] exportMonthlyReportPdf(UUID userId, int month, int year);
}
