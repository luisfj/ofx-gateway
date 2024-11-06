package dev.luisjohann.ofxgateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";
    public static final String TOKEN = "token";
    public static final String HEADER_ACCESS_CONTROL_ALLOW_HEADERS_VALUE = "x-requested-with, authorization, Content-Type, Content-Length, Authorization, credential, X-XSRF-TOKEN";
    public static final String HEADER_ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    public static final String HEADER_ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";
    public static final String HEADER_ACCESS_CONTROL_MAX_AGE_VALUE = "7200";
    public static final String HEADER_ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    public static final String HEADER_ACCESS_CONTROL_ALLOW_METHODS_VALUE = "GET, PUT, POST, DELETE, OPTIONS, PATCH";
    public static final String HEADER_ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    @Value("${conf.access_control_allow_origin}")
    String HEADER_ACCESS_CONTROL_ALLOW_ORIGIN_VALUE;

    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity serverHttpSecurity) {
        return serverHttpSecurity
                .addFilterBefore(corsAndUrlTokenFilter(), SecurityWebFiltersOrder.CORS)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/eureka/**",
                                "/api/*/permission-checker/public/**",
                                "/admin/**")
                        .permitAll()
                        .anyExchange().authenticated())
                .oauth2ResourceServer((oauth) -> oauth
                        .jwt(Customizer.withDefaults()))
                .build();
    }

    @Bean
    WebFilter corsAndUrlTokenFilter() {
        return (ServerWebExchange ctx, WebFilterChain chain) -> {
            ServerHttpRequest request = ctx.getRequest();

            urlTokenFilter(request);

            if (CorsUtils.isCorsRequest(request)) {
                ServerHttpResponse response = ctx.getResponse();
                HttpHeaders headers = response.getHeaders();
                headers.add(HEADER_ACCESS_CONTROL_ALLOW_ORIGIN, HEADER_ACCESS_CONTROL_ALLOW_ORIGIN_VALUE);
                headers.add(HEADER_ACCESS_CONTROL_ALLOW_METHODS, HEADER_ACCESS_CONTROL_ALLOW_METHODS_VALUE);
                headers.add(HEADER_ACCESS_CONTROL_MAX_AGE, HEADER_ACCESS_CONTROL_MAX_AGE_VALUE);
                headers.add(HEADER_ACCESS_CONTROL_ALLOW_HEADERS, HEADER_ACCESS_CONTROL_ALLOW_HEADERS_VALUE);

                if (request.getMethod() == HttpMethod.OPTIONS) {
                    response.setStatusCode(HttpStatus.OK);
                    return Mono.empty();
                }
            }
            return chain.filter(ctx);
        };
    }

    private static void urlTokenFilter(ServerHttpRequest request) {
        if (request.getQueryParams().containsKey(TOKEN)) {
            var token = request.getQueryParams().get(TOKEN);
            request.mutate()
                    .header(AUTHORIZATION, BEARER + token.getFirst())
                    .build();
        }
    }

}