package com.example.dupapi.dto;

import java.util.List;

public record DuplicateGroup(String key, List<ContactView> rows) {}
