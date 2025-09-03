package com.crg.mailservice.service;

import lombok.RequiredArgsConstructor;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import com.crg.mailservice.repository.UserRepository;
import com.crg.mailservice.model.User; // Make sure this import is present

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

	 @Autowired
	    private UserRepository userRepository;

	    // Login with email or username
	    @Override
	    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
	        User user = userRepository.findByEmail(identifier)
	                .or(() -> userRepository.findByUsername(identifier)) // Java 8 Optional chaining
	                .orElseThrow(() -> new UsernameNotFoundException("User not found with email or username: " + identifier));

	        return new org.springframework.security.core.userdetails.User(
	                user.getEmail(), // or user.getUsername(), depends on your logic
	                user.getPassword(),
	                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
	        );
}
}
