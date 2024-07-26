package dev.luisjohann.ofxgateway.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
public class HomeController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(HomeController.class);

    @GetMapping(value = "/token2")
    public Mono<String> getHome2() {
        LOGGER.info("Entrou getHome endpoint");
        return Mono.just("authorizedClient.getAccessToken().getTokenValue()");
    }

    @GetMapping(value = "/unauthenticated")
    public Mono<String> getHomeNoAuth() {
        LOGGER.info("Entrou getHome endpoint");
        return Mono.just("Entrou getHome endpoint");
    }

}