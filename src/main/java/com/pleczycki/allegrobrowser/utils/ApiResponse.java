package com.pleczycki.allegrobrowser.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ApiResponse {
    private Boolean success;
    private String message;
}
