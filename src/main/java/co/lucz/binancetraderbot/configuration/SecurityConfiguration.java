package co.lucz.binancetraderbot.configuration;

import co.lucz.binancetraderbot.filters.Headers;
import co.lucz.binancetraderbot.filters.Methods;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
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

    private static void configureApiHttpSecurity(HttpSecurity http) throws Exception {
        http.antMatcher("/api/**");

        configureBaselineHttpSecurity(http);

        http.headers().contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'none'; " +
                                                                                 "base-uri 'self'; " +
                                                                                 "form-action 'none'; " +
                                                                                 "frame-ancestors 'none';"));
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
    @Order(2)
    @Profile("development")
    public static class DevelopmentApiSecurityConfiguration extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            configureApiHttpSecurity(http);

            http.cors(cors -> {
                CorsConfigurationSource corsConfigurationSource = httpServletRequest -> {
                    CorsConfiguration corsConfiguration = new CorsConfiguration();
                    corsConfiguration.setAllowedOrigins(List.of("*"));
                    corsConfiguration.setAllowedMethods(List.of("*"));
                    corsConfiguration.setAllowedHeaders(List.of("*"));
                    return corsConfiguration;
                };

                cors.configurationSource(corsConfigurationSource);
            });
        }
    }

    @Configuration
    @Order(2)
    @Profile("production")
    public static class ProductionApiSecurityConfiguration extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            configureApiHttpSecurity(http);
        }
    }

    @Configuration
    public static class SpaSecurityConfiguration extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            configureBaselineHttpSecurity(http);

            http.headers().contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'none'; " +
                                                                                     "script-src 'self' 'unsafe-inline'; " +
                                                                                     "style-src 'self' 'unsafe-inline'; " +
                                                                                     "img-src 'self' data:; " +
                                                                                     "connect-src 'self'; " +
                                                                                     "base-uri 'self'; " +
                                                                                     "form-action 'none'; " +
                                                                                     "frame-ancestors 'none';"));
        }
    }
}
