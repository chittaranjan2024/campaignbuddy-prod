package com.crg.mailservice.controller;

import com.crg.mailservice.model.User;
import com.crg.mailservice.repository.UserRepository;
import com.crg.mailservice.jwt.JwtUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    // ✅ Sign up a new user (check both email and username)
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Username already taken"));
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Email already registered"));
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }

    // ✅ Log in with username OR email
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody User loginRequest) {
        try {
            // Try email or username as identifier
            String identifier = loginRequest.getUsername(); // field used for email or username
            String password = loginRequest.getPassword();

            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(identifier, password)
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(identifier);
            String token = jwtUtil.generateToken(userDetails);
            long userid=userRepository.findByUsername(identifier).get().getId();
            String email=userRepository.findByUsername(identifier).get().getEmail();
            String name=userRepository.findByUsername(identifier).get().getName();
            return ResponseEntity.ok(Map.of("token", token, "userId",userid,"name",name,"email",email));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid username/email or password"));
        }
    }
}
