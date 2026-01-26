package com.hmall.gateaway.filter;

import cn.hutool.core.text.AntPathMatcher;
import com.hmall.gateaway.config.AuthProperties;
import com.hmall.gateaway.utils.JwtTool;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static io.prometheus.client.Counter.build;

@RequiredArgsConstructor
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {
    private final AuthProperties authProperties;
    private final AntPathMatcher antPathMatcher=new AntPathMatcher();
    private final JwtTool jwtTool;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        //1.不是登录的地址就放行
        if(isExclude(request.getPath().toString())){
            return chain.filter(exchange);
        }
        //2.如果要登录，获取请求头的属性
        String token = request.getHeaders().getFirst("authorization");

        //3、校验令牌；获得用户信息
        try {
            Long userId = jwtTool.parseToken(token);

            //4、将用户信息传递到后端微服务
            //
            //将用户id设置到请求头：改写request对象
            exchange.mutate().request(builder -> {
                builder.header( "user-info", userId.toString()).build();
            });
            //校验不通过则返回，没有授权，401
        } catch (Exception e) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }


        return chain.filter(exchange);
    }

    private boolean isExclude(String path) {
        for(String excludepath:authProperties.getExcludePaths()){
            if(antPathMatcher.match(excludepath,path)){
                return true;
            }

        }
        return false;

    }

    @Override
    public int getOrder() {
        return 0;
    }
}
