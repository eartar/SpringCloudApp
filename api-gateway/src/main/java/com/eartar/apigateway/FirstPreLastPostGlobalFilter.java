package com.eartar.apigateway;

import io.jsonwebtoken.Jwts;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


public class FirstPreLastPostGlobalFilter
        implements GlobalFilter/*, Ordered*/ {


    private Environment env;
    final Logger logger =
            LoggerFactory.getLogger(FirstPreLastPostGlobalFilter.class);

    public FirstPreLastPostGlobalFilter(Environment env) {
        this.env = env;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {
        logger.info("First Pre Global Filter");


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

        /*
        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    logger.info("Last Post Global Filter");
                }));
         */
    }

    /*
    @Override
    public int getOrder() {
        return -1;
    }
     */

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
}