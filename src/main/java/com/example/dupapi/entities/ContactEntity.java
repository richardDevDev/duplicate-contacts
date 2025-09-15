package com.example.dupapi.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true) 
  private Long contactId;

  @NotBlank
  @Column(length = 100)
  private String name;

  @NotBlank
  @Column(length = 100)
  private String name1; 

  @Email
  @Column(length = 200)
  private String email;

  @Column(length = 20)
  private String postalZip;

  @Column(length = 255)
  private String address;
}
