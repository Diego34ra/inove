package br.edu.ifgoiano.inove.security;

import br.edu.ifgoiano.inove.controller.dto.response.login.LoginResponseDTO;
import br.edu.ifgoiano.inove.controller.dto.request.auth.RefreshTokenDTO;
import br.edu.ifgoiano.inove.controller.exceptions.ResourceBadRequestException;
import br.edu.ifgoiano.inove.domain.model.User;
import br.edu.ifgoiano.inove.domain.service.UserService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;


@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${api.security.token.expiration}")
    private Integer hourExpirationToken;

    @Value("${api.security.refresh-token.expiration}")
    private Integer hourExpirationRefreshToken;

    @Autowired
    private UserService userService;

    public LoginResponseDTO getAuthentication(User user){
        var token  = generateToken(user,hourExpirationToken);
        var refreshToken  = generateToken(user,hourExpirationRefreshToken);
        Long userId = user.getId();
        return new LoginResponseDTO(token,refreshToken,userId);
    }

    public LoginResponseDTO getRefreshToken(RefreshTokenDTO refreshTokenDTO){
        var login = validateToken(refreshTokenDTO.refreshToken());
        UserDetails user = userService.findByEmail(login);

        if(user == null)
            throw new ResourceBadRequestException("Refresh token inválido.");

        var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return getAuthentication((User) authentication.getPrincipal());
    }

    public String generateToken(User user, Integer hourExpirationToken){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("auth-api")
                    .withSubject(user.getEmail())
                    .withClaim("id",user.getId())
                    .withClaim("role",user.getRole().toString())
                    .withExpiresAt(genExpirationDate(hourExpirationToken))
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro enquanto gerava o token ", exception);
        }
    }

    public String validateToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm).withIssuer("auth-api").build().verify(token).getSubject();
        } catch (JWTVerificationException e){
            return null;
        }
    }

    private Instant genExpirationDate(Integer hour){
        return Instant.now().plus(hour, ChronoUnit.HOURS);
    }
}
