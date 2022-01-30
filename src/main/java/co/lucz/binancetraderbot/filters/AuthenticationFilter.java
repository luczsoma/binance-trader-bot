package co.lucz.binancetraderbot.filters;

import co.lucz.binancetraderbot.exceptions.GlobalExceptionHandler;
import co.lucz.binancetraderbot.exceptions.internal.BadRequestException;
import co.lucz.binancetraderbot.exceptions.internal.UnauthorizedException;
import co.lucz.binancetraderbot.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthenticationFilter extends JsonResponseWritingFilter {
    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    @Override
    public void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        try {
            attemptAuthentication(request);
        } catch (UnauthorizedException ex) {
            ResponseEntity<Object> responseEntity = globalExceptionHandler.handleUnauthorizedException(
                    ex,
                    new ServletWebRequest(request)
            );
            writeJsonResponse(response, responseEntity);
            return;
        } catch (Exception ex) {
            ResponseEntity<Object> responseEntity = globalExceptionHandler.handleUnknownException(
                    ex,
                    new ServletWebRequest(request)
            );
            writeJsonResponse(response, responseEntity);
            return;
        }

        chain.doFilter(request, response);
    }

    private void attemptAuthentication(HttpServletRequest request)
            throws AuthenticationException {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new BadRequestException("Authorization header must not be null or blank.");
        }

        try {
            authenticationService.verifyApiKey(authorizationHeader);
        } catch (Exception ex) {
            throw new UnauthorizedException("Authentication credentials could not be verified.", ex);
        }
    }
}
