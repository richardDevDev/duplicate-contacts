package com.example.dupapi.dto;

public record ContactView(
    Long id, Long contactId, String name, String name1,
    String email, String postalZip, String address
) {}
