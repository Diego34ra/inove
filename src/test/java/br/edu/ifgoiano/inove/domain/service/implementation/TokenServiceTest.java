package br.edu.ifgoiano.inove.security;

import br.edu.ifgoiano.inove.controller.dto.request.auth.RefreshTokenDTO;
import br.edu.ifgoiano.inove.controller.exceptions.ResourceBadRequestException;
import br.edu.ifgoiano.inove.domain.model.User;
import br.edu.ifgoiano.inove.domain.model.UserRole;
import br.edu.ifgoiano.inove.domain.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    @Mock
    private UserService userService;

    private User mockUser;
    private static final String SECRET = "test-secret-key-12345";
    private static final Integer TOKEN_EXPIRATION = 24;
    private static final Integer REFRESH_TOKEN_EXPIRATION = 168;

    @BeforeEach
    void setUp() {
        // Setup test configuration
        ReflectionTestUtils.setField(tokenService, "secret", SECRET);
        ReflectionTestUtils.setField(tokenService, "hourExpirationToken", TOKEN_EXPIRATION);
        ReflectionTestUtils.setField(tokenService, "hourExpirationRefreshToken", REFRESH_TOKEN_EXPIRATION);

        // Create mock user
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
        mockUser.setPassword("password");
        mockUser.setRole(UserRole.ADMINISTRATOR);
    }

    @Test
    void getAuthentication_ShouldGenerateValidTokens() {
        // Act
        var result = tokenService.getAuthentication(mockUser);

        // Assert
        assertNotNull(result);
        assertNotNull(result.token());
        assertNotNull(result.refreshToken());

        // Validate both tokens
        String validatedToken = tokenService.validateToken(result.token());
        String validatedRefreshToken = tokenService.validateToken(result.refreshToken());

        assertEquals(mockUser.getEmail(), validatedToken);
        assertEquals(mockUser.getEmail(), validatedRefreshToken);
    }

    @Test
    void generateToken_ShouldCreateValidToken() {
        // Act
        String token = tokenService.generateToken(mockUser, TOKEN_EXPIRATION);

        // Assert
        assertNotNull(token);
        String validatedEmail = tokenService.validateToken(token);
        assertEquals(mockUser.getEmail(), validatedEmail);
    }

    @Test
    void validateToken_WithInvalidToken_ShouldReturnEmptyString() {
        // Act
        String result = tokenService.validateToken("invalid.token.string");

        // Assert
        assertEquals("", result);
    }

    @Test
    void getRefreshToken_WithValidToken_ShouldReturnNewTokens() {
        // Arrange
        String refreshToken = tokenService.generateToken(mockUser, REFRESH_TOKEN_EXPIRATION);
        RefreshTokenDTO refreshTokenDTO = new RefreshTokenDTO(refreshToken);
        when(userService.findByEmail(anyString())).thenReturn(mockUser);

        // Act
        var result = tokenService.getRefreshToken(refreshTokenDTO);

        // Assert
        assertNotNull(result);
        assertNotNull(result.token());
        assertNotNull(result.refreshToken());

        // Validate new tokens
        String validatedToken = tokenService.validateToken(result.token());
        String validatedRefreshToken = tokenService.validateToken(result.refreshToken());

        assertEquals(mockUser.getEmail(), validatedToken);
        assertEquals(mockUser.getEmail(), validatedRefreshToken);
    }

    @Test
    void getRefreshToken_WithInvalidUser_ShouldThrowException() {
        // Arrange
        String refreshToken = tokenService.generateToken(mockUser, REFRESH_TOKEN_EXPIRATION);
        RefreshTokenDTO refreshTokenDTO = new RefreshTokenDTO(refreshToken);
        when(userService.findByEmail(anyString())).thenReturn(null);

        // Act & Assert
        assertThrows(ResourceBadRequestException.class, () -> {
            tokenService.getRefreshToken(refreshTokenDTO);
        });
    }

    @Test
    void getRefreshToken_WithInvalidToken_ShouldThrowException() {
        // Arrange
        RefreshTokenDTO refreshTokenDTO = new RefreshTokenDTO("invalid.token.string");

        // Act & Assert
        assertThrows(ResourceBadRequestException.class, () -> {
            tokenService.getRefreshToken(refreshTokenDTO);
        });
    }

    @Test
    void generateToken_WithNullUser_ShouldThrowException() {
        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            tokenService.generateToken(null, TOKEN_EXPIRATION);
        });
    }
}