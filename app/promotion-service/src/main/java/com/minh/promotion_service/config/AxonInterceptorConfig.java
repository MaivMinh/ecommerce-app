package com.minh.promotion_service.config;

import com.minh.promotion_service.interceptor.SecurityDispatchInterceptor;
import com.minh.promotion_service.interceptor.SecurityHandlerInterceptor;
import org.axonframework.commandhandling.CommandBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AxonInterceptorConfig {

    @Autowired
    public void configureInterceptors(CommandBus commandBus,
                                      SecurityDispatchInterceptor dispatchInterceptor,
                                      SecurityHandlerInterceptor handlerInterceptor) {
        commandBus.registerDispatchInterceptor(dispatchInterceptor);
        commandBus.registerHandlerInterceptor(handlerInterceptor);
    }
}