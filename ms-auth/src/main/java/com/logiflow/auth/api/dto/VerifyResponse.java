package com.logiflow.auth.api.dto;

import java.util.List;

public record VerifyResponse(boolean valid, String username, List<String> roles) {}
