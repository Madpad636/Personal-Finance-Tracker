package com.financeapp.service.impl;

import com.financeapp.entity.Expense;
import com.financeapp.entity.Income;
import com.financeapp.repository.ExpenseRepository;
import com.financeapp.repository.IncomeRepository;
import com.financeapp.service.ReportService;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;

    @Override
    public byte[] exportMonthlyReportCsv(UUID userId, int month, int year) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        List<Income> incomes = incomeRepository.findByUserIdAndDateBetween(userId, start, end);
        List<Expense> expenses = expenseRepository.findByUserIdAndDateBetween(userId, start, end);

        StringWriter stringWriter = new StringWriter();
        try (CSVWriter writer = new CSVWriter(stringWriter)) {
            writer.writeNext(new String[]{"Monthly Report", ym.toString()});
            writer.writeNext(new String[]{});

            writer.writeNext(new String[]{"Type", "Date", "Category/Source", "Description", "Amount"});
            for (Income i : incomes) {
                writer.writeNext(new String[]{"Income", i.getDate().toString(), i.getSource(),
                        nullToEmpty(i.getDescription()), i.getAmount().toString()});
            }
            for (Expense e : expenses) {
                writer.writeNext(new String[]{"Expense", e.getDate().toString(), e.getCategory().getName(),
                        nullToEmpty(e.getDescription()), e.getAmount().toString()});
            }

            writer.writeNext(new String[]{});
            BigDecimal totalIncome = sum(incomes.stream().map(Income::getAmount));
            BigDecimal totalExpenses = sum(expenses.stream().map(Expense::getAmount));
            writer.writeNext(new String[]{"Total Income", totalIncome.toString()});
            writer.writeNext(new String[]{"Total Expenses", totalExpenses.toString()});
            writer.writeNext(new String[]{"Net", totalIncome.subtract(totalExpenses).toString()});
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate CSV report", e);
        }

        return stringWriter.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    @Override
    public byte[] exportMonthlyReportPdf(UUID userId, int month, int year) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        List<Income> incomes = incomeRepository.findByUserIdAndDateBetween(userId, start, end);
        List<Expense> expenses = expenseRepository.findByUserIdAndDateBetween(userId, start, end);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (PdfWriter writer = new PdfWriter(outputStream);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc)) {

            document.add(new Paragraph("Monthly Financial Report - " + ym).setBold().setFontSize(16));
            document.add(new Paragraph(" "));

            Table table = new Table(UnitValue.createPercentArray(new float[]{15, 15, 25, 30, 15}))
                    .useAllAvailableWidth();
            for (String header : new String[]{"Type", "Date", "Category/Source", "Description", "Amount"}) {
                table.addHeaderCell(new Cell().add(new Paragraph(header).setBold()));
            }
            for (Income i : incomes) {
                table.addCell("Income");
                table.addCell(i.getDate().toString());
                table.addCell(i.getSource());
                table.addCell(nullToEmpty(i.getDescription()));
                table.addCell(i.getAmount().toString());
            }
            for (Expense e : expenses) {
                table.addCell("Expense");
                table.addCell(e.getDate().toString());
                table.addCell(e.getCategory().getName());
                table.addCell(nullToEmpty(e.getDescription()));
                table.addCell(e.getAmount().toString());
            }
            document.add(table);

            BigDecimal totalIncome = sum(incomes.stream().map(Income::getAmount));
            BigDecimal totalExpenses = sum(expenses.stream().map(Expense::getAmount));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Total Income: " + totalIncome));
            document.add(new Paragraph("Total Expenses: " + totalExpenses));
            document.add(new Paragraph("Net: " + totalIncome.subtract(totalExpenses)).setBold());
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate PDF report", e);
        }

        return outputStream.toByteArray();
    }

    private BigDecimal sum(java.util.stream.Stream<BigDecimal> amounts) {
        return amounts.reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
