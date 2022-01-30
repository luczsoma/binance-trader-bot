package co.lucz.binancetraderbot.configuration;

import co.lucz.binancetraderbot.filters.Headers;
import co.lucz.binancetraderbot.filters.Methods;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.ArrayList;
import java.util.List;

@EnableWebSecurity
public class SecurityConfiguration {
    private static void configureBaselineHttpSecurity(HttpSecurity http) throws Exception {
        http.headers(headers -> {
            headers.cacheControl();
            headers.frameOptions().deny();
            headers.httpStrictTransportSecurity()
                    .maxAgeInSeconds(63072000)
                    .includeSubDomains(true)
                    .preload(true);
            headers.xssProtection().block(true);
            headers.referrerPolicy().policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER);
            headers.featurePolicy(
                    "geolocation 'none'; " +
                            "midi 'none'; " +
                            "notifications 'none'; " +
                            "push 'none'; " +
                            "sync-xhr 'none'; " +
                            "microphone 'none'; " +
                            "camera 'none'; " +
                            "magnetometer 'none'; " +
                            "gyroscope 'none'; " +
                            "speaker 'none'; " +
                            "vibrate 'none'; " +
                            "fullscreen 'none'; " +
                            "payment 'none';"
            );
        });
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.anonymous().disable();
        http.csrf().disable();
        http.formLogin().disable();
        http.httpBasic().disable();
        http.logout().disable();
        http.rememberMe().disable();
        http.requestCache().disable();
    }

    @Configuration
    @Order(1)
    public static class H2ConsoleSecurityConfiguration extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/h2-console/**");

            configureBaselineHttpSecurity(http);

            http.exceptionHandling().disable();

            http.headers().frameOptions().disable();
        }
    }

    @Configuration
    public static class ApiSecurityConfiguration extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            configureBaselineHttpSecurity(http);

            http.headers(headers -> {
                headers.contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'none'; " +
                                                                                  "base-uri 'self'; " +
                                                                                  "form-action 'none'; " +
                                                                                  "frame-ancestors 'none';"));
            });

            http.cors(cors -> {
                CorsConfigurationSource defaultCorsConfigurationSource = httpServletRequest -> {
                    CorsConfiguration defaultCorsConfiguration = new CorsConfiguration();

                    defaultCorsConfiguration.setAllowedOrigins(List.of("https://trading.lucz.co"));
                    defaultCorsConfiguration.setAllowedMethods(new ArrayList<>(Methods.AllowedMethods));
                    defaultCorsConfiguration.setAllowedHeaders(new ArrayList<>(Headers.AllowedHeaders));
                    defaultCorsConfiguration.setAllowCredentials(false);
                    defaultCorsConfiguration.setMaxAge(300L);
                    return defaultCorsConfiguration;
                };

                cors.configurationSource(defaultCorsConfigurationSource);
            });
        }
    }
}
