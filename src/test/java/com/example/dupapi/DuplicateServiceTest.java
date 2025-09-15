package com.example.dupapi;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.dupapi.entities.ContactEntity;
import com.example.dupapi.repositories.ContactRepository;
import com.example.dupapi.services.DuplicateService;

@ExtendWith(MockitoExtension.class)
class DuplicateServiceTest {

  @Mock ContactRepository repo;
  @InjectMocks DuplicateService service;

  private ContactEntity c(Long id, Long contactId, String name, String name1, String email, String zip, String addr) {
    var e = new ContactEntity();
    e.setId(id);
    e.setContactId(contactId);
    e.setName(name);
    e.setName1(name1);
    e.setEmail(email);
    e.setPostalZip(zip);
    e.setAddress(addr);
    return e;
  }

  @BeforeEach
  void seed() {
    when(repo.findAll()).thenReturn(List.of(
        c(1L, 1L, "Ana", "Lopez", "dup@x.com", "01000", "Calle Uno"),
        c(2L, 2L, "ANA", "LOPEZ", "dup@x.com", "02000", "Calle Dos"),
        c(3L, 3L, "Juan", "Perez", "jp@x.com", "03000", "Calle Tres"),
        c(4L, 4L, "Mario", "Rossi", null, "04000", "Calle Uno")
    ));
  }

  @Test
  void byEmail_groupsCaseInsensitive() {
    var groups = service.findDuplicatesBy("email");
    assertThat(groups).hasSize(1);
    var g = groups.get(0);
    assertThat(g.key()).isEqualTo("dup@x.com");
    assertThat(g.rows()).hasSize(2);
    assertThat(g.rows()).extracting("id").containsExactlyInAnyOrder(1L, 2L);
  }

  @Test
  void byAddress_groupsOnSameAddress() {
    var groups = service.findDuplicatesBy("address");
    assertThat(groups).hasSize(1);
    var g = groups.get(0);
    assertThat(g.key()).isEqualTo("calle uno"); // normalizado
    assertThat(g.rows()).extracting("id").containsExactlyInAnyOrder(1L, 4L);
  }

  @Test
  void byName_groupsByNameAndName1() {
    var groups = service.findDuplicatesBy("name");
    assertThat(groups).hasSize(1);
    var g = groups.get(0);
    assertThat(g.key()).isEqualTo("ana|lopez");
    assertThat(g.rows()).hasSize(2);
  }
}
