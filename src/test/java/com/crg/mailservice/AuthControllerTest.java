/*
 * package com.crg.mailservice;
 * 
 * 
 * 
 * import com.crg.mailservice.controller.AuthController; import
 * com.crg.mailservice.jwt.JwtUtil; import com.crg.mailservice.model.User;
 * import com.crg.mailservice.repository.UserRepository; import
 * org.junit.jupiter.api.BeforeEach; import org.junit.jupiter.api.Test; import
 * org.mockito.*; import org.springframework.http.ResponseEntity; import
 * org.springframework.security.authentication.*; import
 * org.springframework.security.core.userdetails.UserDetails; import
 * org.springframework.security.core.userdetails.UserDetailsService; import
 * org.springframework.security.crypto.password.PasswordEncoder;
 * 
 * import java.util.Map;
 * 
 * import static org.junit.jupiter.api.Assertions.*; import static
 * org.mockito.Mockito.*;
 * 
 * class AuthControllerTest {
 * 
 * @InjectMocks private AuthController authController;
 * 
 * @Mock private UserRepository userRepository;
 * 
 * @Mock private PasswordEncoder passwordEncoder;
 * 
 * @Mock private AuthenticationManager authenticationManager;
 * 
 * @Mock private UserDetailsService userDetailsService;
 * 
 * @Mock private JwtUtil jwtUtil;
 * 
 * @BeforeEach void setUp() { MockitoAnnotations.openMocks(this); }
 * 
 * // ✅ Test for signup
 * 
 * @Test void testRegisterUser_Success() { User user = new User();
 * user.setUsername("testuser"); user.setPassword("password");
 * 
 * when(userRepository.existsByUsername("testuser")).thenReturn(false);
 * when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
 * when(userRepository.save(any(User.class))).thenReturn(user);
 * 
 * ResponseEntity<?> response = authController.registerUser(user);
 * 
 * assertEquals(200, response.getStatusCodeValue());
 * assertEquals("User registered successfully", response.getBody()); }
 * 
 * @Test void testRegisterUser_UsernameConflict() { User user = new User();
 * user.setUsername("existingUser");
 * 
 * when(userRepository.existsByUsername("existingUser")).thenReturn(true);
 * 
 * ResponseEntity<?> response = authController.registerUser(user);
 * 
 * assertEquals(409, response.getStatusCodeValue());
 * assertEquals("Username already taken", response.getBody()); }
 * 
 * // ✅ Test for login
 * 
 * @Test void testAuthenticateUser_Success() { User loginRequest = new User();
 * loginRequest.setUsername("testuser"); loginRequest.setPassword("password");
 * 
 * UserDetails userDetails = mock(UserDetails.class);
 * when(userDetails.getUsername()).thenReturn("testuser");
 * 
 * when(userDetailsService.loadUserByUsername("testuser")).thenReturn(
 * userDetails);
 * when(jwtUtil.generateToken(userDetails)).thenReturn("dummy-jwt");
 * 
 * doNothing().when(authenticationManager).authenticate(any());
 * 
 * ResponseEntity<?> response = authController.authenticateUser(loginRequest);
 * 
 * assertEquals(200, response.getStatusCodeValue()); assertTrue(((Map<?, ?>)
 * response.getBody()).containsKey("token")); }
 * 
 * @Test void testAuthenticateUser_InvalidCredentials() { User loginRequest =
 * new User(); loginRequest.setUsername("testuser");
 * loginRequest.setPassword("wrongpassword");
 * 
 * doThrow(new BadCredentialsException("Bad credentials"))
 * .when(authenticationManager).authenticate(any());
 * 
 * ResponseEntity<?> response = authController.authenticateUser(loginRequest);
 * 
 * assertEquals(401, response.getStatusCodeValue());
 * assertEquals("Invalid username or password", response.getBody()); } }
 */