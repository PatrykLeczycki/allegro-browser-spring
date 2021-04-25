package com.pleczycki.allegrobrowser.controller;

import com.pleczycki.allegrobrowser.dto.UserDto;
import com.pleczycki.allegrobrowser.security.JwtAuthenticationResponse;
import com.pleczycki.allegrobrowser.service.AuthenticationService;
import com.pleczycki.allegrobrowser.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.Map;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody UserDto userDto) {
        return authenticationService.register(userDto);
    }

    @PostMapping("/confirmAccount")
    public ResponseEntity<ApiResponse> confirmAccount(@RequestBody Map<String, String> accountConfirmationData) {
        return authenticationService.confirmAccount(accountConfirmationData);
    }

    @PostMapping("/resendConfirmationLink")
    public ResponseEntity<ApiResponse> resendConfirmationLink(@RequestBody Map<String, String> resendConfirmationLinkData) {
        return authenticationService.resendConfirmationLink(resendConfirmationLinkData);
    }

    @PostMapping("/retrievePasswordFirst")
    public ResponseEntity<ApiResponse> retrievePasswordFirstStage(@RequestBody Map<String, String> emailData) {
        return authenticationService.retrievePassword(emailData.get("email"));
    }

    @PostMapping("/retrievePasswordSecond")
    public ResponseEntity<ApiResponse> retrievePasswordSecondStage(@RequestBody Map<String, String> passwordRetrievalData) {
        return authenticationService.retrievePassword(passwordRetrievalData);
    }

    @PostMapping("/changePassword")
    public ResponseEntity<ApiResponse> changePassword(@RequestBody Map<String, String> changePasswordData) {
        return authenticationService.changePassword(changePasswordData);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> login(@Valid @RequestBody UserDto userDto) {
        return authenticationService.login(userDto);
    }
}
