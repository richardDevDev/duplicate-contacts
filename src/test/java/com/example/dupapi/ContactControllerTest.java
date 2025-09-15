package com.example.dupapi;


import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.dupapi.controllers.ContactController;
import com.example.dupapi.dto.ContactView;
import com.example.dupapi.dto.DuplicateGroup;
import com.example.dupapi.dto.ImportSummary;
import com.example.dupapi.services.DuplicateService;
import com.example.dupapi.services.ExcelImportService;

@WebMvcTest(ContactController.class)
class ContactControllerTest {

  @Autowired MockMvc mvc;
  @MockBean ExcelImportService importer;
  @MockBean DuplicateService duplicateService;

  @Test
  void importEndpoint_returnsSummary() throws Exception {
    var file = new MockMultipartFile("file","c.xlsx",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[]{1,2,3});
    when(importer.importXlsx(any())).thenReturn(new ImportSummary(3,2,0,1));

    mvc.perform(multipart("/api/contacts/import").file(file))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.total").value(3))
        .andExpect(jsonPath("$.inserted").value(2))
        .andExpect(jsonPath("$.skipped").value(1));
  }

  @Test
  void duplicatesEndpoint_returnsGroups() throws Exception {
    var rows = List.of(new ContactView(1L, 1L, "Ana", "Lopez", "dup@x.com", "01000", "Calle Uno"),
                       new ContactView(2L, 2L, "ANA", "LOPEZ", "dup@x.com", "02000", "Calle Dos"));
    when(duplicateService.findDuplicatesBy("email"))
        .thenReturn(List.of(new DuplicateGroup("dup@x.com", rows)));

    mvc.perform(get("/api/duplicates").param("by", "email"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].key").value("dup@x.com"))
        .andExpect(jsonPath("$[0].rows", hasSize(2)))
        .andExpect(jsonPath("$[0].rows[0].email").value("dup@x.com"));
  }

  @Test
  void exportCsv_returnsTextCsv() throws Exception {
    when(duplicateService.findDuplicatesBy("email"))
        .thenReturn(List.of(new DuplicateGroup("dup@x.com",
            List.of(new ContactView(1L,1L,"Ana","Lopez","dup@x.com","01000","Calle Uno")))));

    mvc.perform(get("/api/duplicates/export").param("by", "email"))
        .andExpect(status().isOk())
        .andExpect(header().string("Content-Disposition", containsString("duplicates-email.csv")))
        .andExpect(content().contentType("text/csv"))
        .andExpect(content().string(containsString("dup@x.com")))
        .andExpect(content().string(containsString("contactId")));
  }
}
