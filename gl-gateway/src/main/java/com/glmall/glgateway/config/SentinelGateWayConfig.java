package com.glmall.glgateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glmall.utils.CodeEnum;
import com.glmall.utils.R;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class SentinelGateWayConfig {
    public SentinelGateWayConfig() {
        GatewayCallbackManager.setBlockHandler(new BlockRequestHandler() {
            @SneakyThrows
            @Override
            public Mono<ServerResponse> handleRequest(ServerWebExchange serverWebExchange, Throwable throwable) {
                R error = R.error(CodeEnum.FLOW_LIMIT.getCode(), CodeEnum.FLOW_LIMIT.getMsg());
                ObjectMapper objectMapper = new ObjectMapper();
                String s = objectMapper.writeValueAsString(error);
                Mono<ServerResponse> body = ServerResponse.ok().body(Mono.just(s), String.class);
                return body;
            }
        });
    }
}
