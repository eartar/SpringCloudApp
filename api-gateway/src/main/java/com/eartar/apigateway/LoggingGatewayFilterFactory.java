package com.eartar.apigateway;

import io.jsonwebtoken.Jwts;
import org.apache.http.HttpHeaders;
import org.checkerframework.checker.units.qual.C;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;


public class LoggingGatewayFilterFactory extends
        AbstractGatewayFilterFactory<LoggingGatewayFilterFactory.Config> {

    public static class Config {
        private String msg;
        private boolean preLogger;
        private boolean postLogger;

        public Config(String msg, boolean preLogger, boolean postLogger) {
            this.msg = msg;
            this.preLogger = preLogger;
            this.postLogger = postLogger;
        }
    }

    Environment env;
    final Logger logger =
            LoggerFactory.getLogger(LoggingGatewayFilterFactory.class);


    public LoggingGatewayFilterFactory(Environment env) {
        this.env = env;
    }

    public LoggingGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return new OrderedGatewayFilter((exchange, chain) -> {
            // Pre-processing
            if (config.preLogger) {
                logger.info("Pre GatewayFilter logging: "
                        + config.msg);


                ServerHttpRequest request = exchange.getRequest();

                if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    return onError(exchange, "No auth header", HttpStatus.UNAUTHORIZED);
                }

                //read jwt from the request
                String authHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                String jwt = authHeader.replace("Bearer", "");

                if (!isJwtValid(jwt)){
                    return onError(exchange, "JWT token is invalid", HttpStatus.UNAUTHORIZED);
                }
                return chain.filter(exchange);



            }
            return chain.filter(exchange)
                    .then(Mono.fromRunnable(() -> {
                        // Post-processing
                        if (config.postLogger) {
                            logger.info("Post GatewayFilter logging: "
                                    + config.msg);
                        }
                    }));
        }, 1);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        return response.setComplete();
    }

    private boolean isJwtValid(String jwt) {
        boolean retVal = true;

        String subject = null;
        //fetch this from spring cloud config file later
        try {
            subject = Jwts.parser().setSigningKey(env.getProperty("token.secret")).parseClaimsJws(jwt)
                    .getBody().getSubject();
        }catch (Exception e){
            retVal = false;
        }

        if (subject == null || subject.isEmpty()){
            retVal = false;
        }

        return retVal;
    }


    @Bean
    public RouteLocator routes(
            RouteLocatorBuilder builder,
            LoggingGatewayFilterFactory loggingFactory) {
        return builder.routes()
                .route("service_route_java_config", r -> r.path("/users-ws/**")
                        .filters(f ->
                                f.rewritePath("/users-ws/users/(?<segment>.*)", "/users/e$\\{segment}")
                                        .filter(loggingFactory.apply(
                                                new Config("My Custom Message", true, true))))
                        .uri("lb://users-ws"))
                .build();
    }
}