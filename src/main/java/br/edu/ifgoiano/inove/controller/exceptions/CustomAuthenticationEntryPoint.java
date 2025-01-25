package br.edu.ifgoiano.inove.controller.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    private HttpServletRequest request;

    @Override
    public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException authException) throws IOException, ServletException {
        res.setContentType("application/json; charset=UTF-8");
        res.setStatus(401);

        HttpStatus status = HttpStatus.UNAUTHORIZED;

        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"));
        ErrorDetails errorDetails = new ErrorDetails(new Date(),status.value(),"Acesso negado.",getRequestPath(req));
        res.getWriter().write(mapper.writeValueAsString(errorDetails));
    }

    private String getRequestPath(HttpServletRequest request) {
        String forwardUri = (String) request.getAttribute("jakarta.servlet.forward.request_uri");
        if (forwardUri != null) {
            return forwardUri;
        }
        return request.getRequestURI();
    }
}
