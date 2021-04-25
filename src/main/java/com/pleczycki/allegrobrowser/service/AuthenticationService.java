package com.pleczycki.allegrobrowser.service;

import com.pleczycki.allegrobrowser.dto.UserDto;
import com.pleczycki.allegrobrowser.model.RoleName;
import com.pleczycki.allegrobrowser.model.User;
import com.pleczycki.allegrobrowser.repository.RoleRepository;
import com.pleczycki.allegrobrowser.repository.UserRepository;
import com.pleczycki.allegrobrowser.security.JwtAuthenticationResponse;
import com.pleczycki.allegrobrowser.security.JwtTokenProvider;
import com.pleczycki.allegrobrowser.utils.ApiResponse;
import com.pleczycki.allegrobrowser.utils.CustomModelMapper;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.*;

@Slf4j
@Service("AuthenticationService")
public class AuthenticationService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private CustomModelMapper modelMapper;

    @Transactional
    public ResponseEntity<ApiResponse> register(UserDto userDto) {

        if (userRepository.existsByEmail(userDto.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, "Email is already used!"));
        }

        User user = modelMapper.map(userDto, User.class);
        user.setCreatedAt(new Date());
        user.setUsername("");
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRoles(Collections.singleton(roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(
                () -> new RuntimeException("User Role not set.")
        )));
        user.setEnabled(false);
        user.setRegistrationToken(RandomStringUtils.randomAlphanumeric(30));
        userRepository.save(user);

        return ResponseEntity.ok(new ApiResponse(true, "User registered successfully"));
    }

    public ResponseEntity<JwtAuthenticationResponse> login(@Valid @RequestBody UserDto userDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userDto.getEmail(),
                        userDto.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);

        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    public ResponseEntity<ApiResponse> confirmAccount(Map<String, String> confirmationData) {
        Long id = Long.valueOf(confirmationData.get("userId"));
        String token = confirmationData.get("registrationToken");
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, "E-mail not found"));
        }

        User user = optionalUser.get();

        if (user.getRegistrationToken().equals(token)) {
            user.setRegistrationToken(null);
            user.setEnabled(true);
            userRepository.save(user);
            return ResponseEntity.ok(new ApiResponse(true, "Account activated successfully"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, "Invalid confirmation token"));
    }

    public ResponseEntity<ApiResponse> resendConfirmationLink(Map<String, String> resendConfirmationLinkData) {
        String email = resendConfirmationLinkData.get("email");
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, "E-mail not found"));
        }

        User user = optionalUser.get();

        if (!user.isEnabled()) {
            user.setRegistrationToken(RandomStringUtils.randomAlphanumeric(30));
            userRepository.save(user);
            return ResponseEntity.ok(new ApiResponse(true, "Confirmation link resent successfully"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, "User is already activated"));
    }

    @Transactional
    public ResponseEntity<ApiResponse> retrievePassword(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, "E-mail not found"));
        }

        User user = optionalUser.get();
        user.setPassRecoveryToken(RandomStringUtils.randomAlphanumeric(30));
        return ResponseEntity.ok(new ApiResponse(true, "Message with password retrieve link sent successfully"));
    }

    @Transactional
    public ResponseEntity<ApiResponse> retrievePassword(Map<String, String> passwordRetrievalData) {

        Optional<User> optionalUser = userRepository.findById(Long.valueOf(passwordRetrievalData.get("userId")));
        String token = passwordRetrievalData.get("lostPasswordToken");

        if(optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "Invalid password recovery token - user not found."));
        }

        User user = optionalUser.get();

        if (user.getPassRecoveryToken().equals(token)) {
            user.setPassword(passwordEncoder.encode(passwordRetrievalData.get("password")));
            user.setPassRecoveryToken(null);
            userRepository.save(user);
            return ResponseEntity.ok(new ApiResponse(true, "Password changed successfully"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, "Invalid password recovery token"));
    }

    public ResponseEntity<ApiResponse> changePassword(Map<String, String> changePasswordData) {
        Long userId = Long.valueOf(changePasswordData.get("userId"));
        String currentPassword = changePasswordData.get("currentPassword");
        String newPassword = changePasswordData.get("newPassword");

        Optional<User> optionalUser = userRepository.findById(userId);

        if(optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "User not found."));
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, "Current password is incorrect"));
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return ResponseEntity.ok(new ApiResponse(true, "Password changed successfully"));
    }
}
