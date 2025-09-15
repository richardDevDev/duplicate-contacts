package com.example.dupapi.services;

import com.example.dupapi.dto.ImportSummary;
import com.example.dupapi.entities.ContactEntity;
import com.example.dupapi.repositories.ContactRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class ExcelImportService {

  private final ContactRepository repo;

  public ExcelImportService(ContactRepository repo) {
    this.repo = repo;
  }

  // Columnas esperadas (sheet 0):
  // contactID | name | name1 | email | postalZip | address
  public ImportSummary importXlsx(MultipartFile file) throws Exception {
    long total = 0, inserted = 0, skipped = 0;

    try (InputStream in = file.getInputStream(); Workbook wb = new XSSFWorkbook(in)) {
      Sheet sheet = wb.getSheetAt(0);
      if (sheet == null) throw new IllegalArgumentException("Sheet 0 not found");

      for (int i = 1; i <= sheet.getLastRowNum(); i++) {
        Row row = sheet.getRow(i);
        if (row == null) continue;
        total++;

        Long contactId = getLong(row.getCell(0));
        String name     = getString(row.getCell(1));
        String name1    = getString(row.getCell(2));
        String email    = getString(row.getCell(3));
        String zip      = getString(row.getCell(4));
        String address  = getString(row.getCell(5));

        if (contactId == null) { skipped++; continue; }

        ContactEntity c = new ContactEntity();
        c.setContactId(contactId);
        c.setName(emptyToNA(name));
        c.setName1(emptyToNA(name1));
        c.setEmail(safe(email));
        c.setPostalZip(safe(zip));
        c.setAddress(safe(address));

        repo.save(c);
        inserted++;
      }
    }
    return new ImportSummary(total, inserted, 0, skipped);
  }

  // helpers
  private static String getString(Cell cell) {
    if (cell == null) return null;
    return switch (cell.getCellType()) {
      case STRING -> cell.getStringCellValue();
      case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
      case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
      case FORMULA -> cell.getCellFormula();
      default -> null;
    };
  }

  private static Long getLong(Cell cell) {
    String s = getString(cell);
    if (s == null || s.isBlank()) return null;
    try { return Long.valueOf(s.trim()); } catch (NumberFormatException e) { return null; }
  }

  private static String safe(String s) { return s == null ? null : s.trim(); }
  private static String emptyToNA(String s) {
    s = safe(s); return (s == null || s.isEmpty()) ? "N/A" : s;
  }
}
