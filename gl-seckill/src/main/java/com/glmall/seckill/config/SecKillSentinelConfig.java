package com.glmall.seckill.config;

import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlBlockHandler;
import com.alibaba.csp.sentinel.adapter.servlet.callback.WebCallbackManager;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glmall.utils.CodeEnum;
import com.glmall.utils.R;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class SecKillSentinelConfig {

    public SecKillSentinelConfig() {
        WebCallbackManager.setUrlBlockHandler(new UrlBlockHandler() {
            @Override
            public void blocked(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BlockException e) throws IOException {
                R error = R.error(CodeEnum.FLOW_LIMIT.getCode(), CodeEnum.FLOW_LIMIT.getMsg());
                ObjectMapper objectMapper = new ObjectMapper();
                String s = objectMapper.writeValueAsString(error);
                httpServletResponse.getWriter().write(s);
            }
        });
    }
}
