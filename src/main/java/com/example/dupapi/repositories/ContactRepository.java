package com.example.dupapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;


import com.example.dupapi.entities.ContactEntity;
public interface ContactRepository extends JpaRepository<ContactEntity, Long> { }