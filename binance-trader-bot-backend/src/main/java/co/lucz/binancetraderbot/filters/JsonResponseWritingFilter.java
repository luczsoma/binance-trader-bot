package co.lucz.binancetraderbot.filters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public abstract class JsonResponseWritingFilter extends OncePerRequestFilter {
    @Autowired
    private Jackson2ObjectMapperBuilder objectMapperBuilder;

    protected final void writeJsonResponse(HttpServletResponse response, ResponseEntity<Object> responseEntity)
        throws IOException
    {
        response.setStatus(responseEntity.getStatusCodeValue());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        responseEntity.getHeaders().forEach((headerName, headerValues) ->
            headerValues.forEach(headerValue ->
                response.addHeader(headerName, headerValue)
            )
        );

        Object responseBody = Optional.ofNullable(responseEntity.getBody()).orElse(new Object());
        objectMapperBuilder.build().writeValue(response.getWriter(), responseBody);
    }
}
