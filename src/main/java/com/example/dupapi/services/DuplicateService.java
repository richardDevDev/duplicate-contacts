package com.example.dupapi.services;

import com.example.dupapi.dto.ContactView;
import com.example.dupapi.dto.DuplicateGroup;
import com.example.dupapi.entities.ContactEntity;
import com.example.dupapi.repositories.ContactRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DuplicateService {

  private final ContactRepository repo;

  public DuplicateService(ContactRepository repo) {
    this.repo = repo;
  }

  // by: "email" | "address" | "name" (name + name1)
  public List<DuplicateGroup> findDuplicatesBy(String by) {
    List<ContactEntity> all = repo.findAll();

    java.util.function.Function<ContactEntity, String> keyFn =
        switch (by == null ? "email" : by.toLowerCase(Locale.ROOT)) {
          case "email"   -> c -> norm(c.getEmail());
          case "address" -> c -> norm(c.getAddress());
          case "name"    -> c -> (norm(c.getName()) + "|" + norm(c.getName1()));
          default -> throw new IllegalArgumentException("by must be one of: email, address, name");
        };

    Map<String, List<ContactEntity>> grouped = all.stream()
        .filter(c -> {
          String k = keyFn.apply(c);
          return k != null && !k.isEmpty();
        })
        .collect(Collectors.groupingBy(keyFn));

    return grouped.entrySet().stream()
        .filter(e -> e.getValue().size() > 1)
        .sorted(Map.Entry.comparingByKey())
        .map(e -> new DuplicateGroup(
            e.getKey(),
            e.getValue().stream()
                .map(c -> new ContactView(
                    c.getId(), c.getContactId(), c.getName(), c.getName1(),
                    c.getEmail(), c.getPostalZip(), c.getAddress()))
                .toList()
        ))
        .toList();
  }

  private static String norm(String s) {
    return s == null ? null : s.trim().toLowerCase(Locale.ROOT);
  }
}
