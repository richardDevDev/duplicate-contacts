package com.example.dupapi.controllers;

import com.example.dupapi.dto.DuplicateGroup;
import com.example.dupapi.dto.ImportSummary;
import com.example.dupapi.services.DuplicateService;
import com.example.dupapi.services.ExcelImportService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ContactController {

  private final ExcelImportService importer;
  private final DuplicateService duplicateService;

  public ContactController(ExcelImportService importer, DuplicateService duplicateService) {
    this.importer = importer;
    this.duplicateService = duplicateService;
  }

  @PostMapping(path = "/contacts/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ImportSummary importExcel(@RequestParam("file") MultipartFile file) throws Exception {
    return importer.importXlsx(file);
  }

  @GetMapping("/duplicates")
  public List<DuplicateGroup> duplicates(@RequestParam(defaultValue = "email") String by) {
    return duplicateService.findDuplicatesBy(by);
  }

  @GetMapping(value = "/duplicates/export", produces = "text/csv")
  public ResponseEntity<byte[]> export(@RequestParam(defaultValue = "email") String by) {
    List<DuplicateGroup> groups = duplicateService.findDuplicatesBy(by);
    String csv = toCsv(groups);
    return ResponseEntity.ok()
        .header("Content-Disposition", "attachment; filename=duplicates-" + by + ".csv")
        .body(csv.getBytes(StandardCharsets.UTF_8));
  }

  private static String toCsv(List<DuplicateGroup> groups) {
    String header = "key,id,contactId,name,name1,email,postalZip,address";
    return header + "\n" + groups.stream().flatMap(g ->
      g.rows().stream().map(r -> String.join(",",
        q(g.key()), s(r.id()), s(r.contactId()), q(r.name()), q(r.name1()),
        q(r.email()), q(r.postalZip()), q(r.address())
      ))
    ).collect(Collectors.joining("\n"));
  }

  private static String s(Object o) { return o == null ? "" : o.toString(); }
  private static String q(String s) { if (s == null) return ""; return "\"" + s.replace("\"","\"\"") + "\""; }
}
