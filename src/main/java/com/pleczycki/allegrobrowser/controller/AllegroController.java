package com.pleczycki.allegrobrowser.controller;

import com.pleczycki.allegrobrowser.service.AllegroService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RequestMapping("/allegro")
@RestController
@RequiredArgsConstructor
public class AllegroController {

    private final RestTemplate restTemplate;
    private final AllegroService allegroService;

    @PostMapping("/auth")
    public ResponseEntity<String> getToken(@RequestBody Map<String, String> body) {

        String codeString = body.get("code");

        String url = "https://allegro.pl/auth/oauth/token" +
                "?grant_type=authorization_code&code=" + codeString +
                "&redirect_uri=http://localhost:4200/allegro/auth";
        ResponseEntity<String> responseEntity =
                restTemplate.exchange(url, HttpMethod.POST, allegroService.getAuthHeader(), String.class);
        String json = responseEntity.getBody();

        return ResponseEntity.ok(json);
    }
}