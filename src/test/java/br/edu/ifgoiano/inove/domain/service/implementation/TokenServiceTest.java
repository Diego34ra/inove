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
        ReflectionTestUtils.setField(tokenService, "secret", SECRET);
        ReflectionTestUtils.setField(tokenService, "hourExpirationToken", TOKEN_EXPIRATION);
        ReflectionTestUtils.setField(tokenService, "hourExpirationRefreshToken", REFRESH_TOKEN_EXPIRATION);

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
        mockUser.setPassword("password");
        mockUser.setRole(UserRole.ADMINISTRATOR);
    }

    @Test
    void getAuthentication_ShouldGenerateValidTokens() {
        var result = tokenService.getAuthentication(mockUser);

        assertNotNull(result);
        assertNotNull(result.token());
        assertNotNull(result.refreshToken());

        String validatedToken = tokenService.validateToken(result.token());
        String validatedRefreshToken = tokenService.validateToken(result.refreshToken());

        assertEquals(mockUser.getEmail(), validatedToken);
        assertEquals(mockUser.getEmail(), validatedRefreshToken);
    }

    @Test
    void generateToken_ShouldCreateValidToken() {
        String token = tokenService.generateToken(mockUser, TOKEN_EXPIRATION);

        assertNotNull(token);
        String validatedEmail = tokenService.validateToken(token);
        assertEquals(mockUser.getEmail(), validatedEmail);
    }

    @Test
    void validateToken_WithInvalidToken_ShouldReturnEmptyString() {
        String result = tokenService.validateToken("invalid.token.string");

        assertEquals("", result);
    }

    @Test
    void getRefreshToken_WithValidToken_ShouldReturnNewTokens() {
        String refreshToken = tokenService.generateToken(mockUser, REFRESH_TOKEN_EXPIRATION);
        RefreshTokenDTO refreshTokenDTO = new RefreshTokenDTO(refreshToken);
        when(userService.findByEmail(anyString())).thenReturn(mockUser);

        var result = tokenService.getRefreshToken(refreshTokenDTO);

        assertNotNull(result);
        assertNotNull(result.token());
        assertNotNull(result.refreshToken());

        String validatedToken = tokenService.validateToken(result.token());
        String validatedRefreshToken = tokenService.validateToken(result.refreshToken());

        assertEquals(mockUser.getEmail(), validatedToken);
        assertEquals(mockUser.getEmail(), validatedRefreshToken);
    }

    @Test
    void getRefreshToken_WithInvalidUser_ShouldThrowException() {
        String refreshToken = tokenService.generateToken(mockUser, REFRESH_TOKEN_EXPIRATION);
        RefreshTokenDTO refreshTokenDTO = new RefreshTokenDTO(refreshToken);
        when(userService.findByEmail(anyString())).thenReturn(null);

        assertThrows(ResourceBadRequestException.class, () -> {
            tokenService.getRefreshToken(refreshTokenDTO);
        });
    }

    @Test
    void getRefreshToken_WithInvalidToken_ShouldThrowException() {
        RefreshTokenDTO refreshTokenDTO = new RefreshTokenDTO("invalid.token.string");

        assertThrows(ResourceBadRequestException.class, () -> {
            tokenService.getRefreshToken(refreshTokenDTO);
        });
    }

    @Test
    void generateToken_WithNullUser_ShouldThrowException() {
        assertThrows(RuntimeException.class, () -> {
            tokenService.generateToken(null, TOKEN_EXPIRATION);
        });
    }
}