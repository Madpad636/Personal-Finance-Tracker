package com.financeapp.controller;

import com.financeapp.security.AuthenticatedUser;
import com.financeapp.service.ReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// TODO: implement ReportService (CSV via opencsv, PDF via itext7 - both already in pom.xml).
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Export monthly/category/budget/savings reports")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/monthly/csv")
    public ResponseEntity<byte[]> monthlyCsv(@AuthenticationPrincipal AuthenticatedUser user,
                                              @RequestParam int month, @RequestParam int year) {
        byte[] csv = reportService.exportMonthlyReportCsv(user.getId(), month, year);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report-" + year + "-" + month + ".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }

    @GetMapping("/monthly/pdf")
    public ResponseEntity<byte[]> monthlyPdf(@AuthenticationPrincipal AuthenticatedUser user,
                                              @RequestParam int month, @RequestParam int year) {
        byte[] pdf = reportService.exportMonthlyReportPdf(user.getId(), month, year);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report-" + year + "-" + month + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
