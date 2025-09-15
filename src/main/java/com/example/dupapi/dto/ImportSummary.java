package com.example.dupapi.dto;

import java.util.List;

public record ImportSummary(long total, long inserted, long updated, long skipped) {}
public record DuplicateGroup(String key, List<ContactView> rows) {}
public record ContactView(Long id, Long contactId, String name, String name1, String email, String postalZip, String address) {}
