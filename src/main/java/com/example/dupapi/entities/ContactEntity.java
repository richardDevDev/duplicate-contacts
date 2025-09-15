package com.example.dupapi.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "contacts")
public class ContactEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true)
  private Long contactId;

  @NotBlank
  @Column(length = 100, nullable = false)
  private String name;

  @NotBlank
  @Column(length = 100, nullable = false)
  private String name1;

  @Email
  @Column(length = 200)
  private String email;

  @Column(length = 20)
  private String postalZip;

  @Column(length = 255)
  private String address;

  public ContactEntity() {}

  // Getters/Setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public Long getContactId() { return contactId; }
  public void setContactId(Long contactId) { this.contactId = contactId; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getName1() { return name1; }
  public void setName1(String name1) { this.name1 = name1; }

  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }

  public String getPostalZip() { return postalZip; }
  public void setPostalZip(String postalZip) { this.postalZip = postalZip; }

  public String getAddress() { return address; }
  public void setAddress(String address) { this.address = address; }
}
