package com.example.dupapi;

import java.io.ByteArrayOutputStream;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import com.example.dupapi.dto.ImportSummary;
import com.example.dupapi.entities.ContactEntity;
import com.example.dupapi.repositories.ContactRepository;
import com.example.dupapi.services.ExcelImportService;

@ExtendWith(MockitoExtension.class)
class ExcelImportServiceTest {

    @Mock
    ContactRepository repo;
    @InjectMocks
    ExcelImportService service;

    @Test
    void importXlsx_insertsValidRows_andSkipsInvalid() throws Exception {
        byte[] data;
        try (var wb = new XSSFWorkbook()) {
            Sheet sh = wb.createSheet();
            Row h = sh.createRow(0);
            h.createCell(0).setCellValue("contactID");
            h.createCell(1).setCellValue("name");
            h.createCell(2).setCellValue("name1");
            h.createCell(3).setCellValue("email");
            h.createCell(4).setCellValue("postalZip");
            h.createCell(5).setCellValue("address");

            Row r1 = sh.createRow(1);
            r1.createCell(0).setCellValue(1);
            r1.createCell(1).setCellValue("Ana");
            r1.createCell(2).setCellValue("López");
            r1.createCell(3).setCellValue("ana@example.com");
            r1.createCell(4).setCellValue("01000");
            r1.createCell(5).setCellValue("Calle 1");

            Row r2 = sh.createRow(2);
            r2.createCell(0).setCellValue("");
            r2.createCell(1).setCellValue("X");
            r2.createCell(2).setCellValue("Y");

            Row r3 = sh.createRow(3);
            r3.createCell(0).setCellValue(2);
            r3.createCell(1).setCellValue("  ");
            r3.createCell(2).setCellValue("Perez");
            r3.createCell(3).setCellValue("x@example.com");
            r3.createCell(4).setCellValue("02000");
            r3.createCell(5).setCellValue("Calle 2");

            try (var baos = new ByteArrayOutputStream()) {
                wb.write(baos);
                data = baos.toByteArray();
            }
        }

        var file = new MockMultipartFile(
                "file",
                "contacts.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                data);

        when(repo.save(any(ContactEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ImportSummary sum = service.importXlsx(file);

        ArgumentCaptor<ContactEntity> captor = ArgumentCaptor.forClass(ContactEntity.class);
        verify(repo, times(2)).save(captor.capture());

        assertThat(sum.total()).isEqualTo(3);
        assertThat(sum.inserted()).isEqualTo(2);
        assertThat(sum.skipped()).isEqualTo(1);

        var saved = captor.getAllValues();
        assertThat(saved).extracting(ContactEntity::getContactId).containsExactlyInAnyOrder(1L, 2L);
        var withId2 = saved.stream().filter(c -> c.getContactId().equals(2L)).findFirst().orElseThrow();
        assertThat(withId2.getName()).isEqualTo("N/A"); // normalización
    }
}
