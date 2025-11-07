package com.minh.order_service.config;

import com.minh.order_service.interceptor.SecurityDispatchInterceptor;
import com.minh.order_service.interceptor.SecurityHandlerInterceptor;
import org.axonframework.commandhandling.CommandBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class AxonInterceptorConfig {
    private CommandBus commandBus;

    @Autowired
    public void configureInterceptors(@Lazy CommandBus commandBus,
                                      SecurityDispatchInterceptor dispatchInterceptor,
                                      SecurityHandlerInterceptor handlerInterceptor) {
        this.commandBus = commandBus;
        commandBus.registerDispatchInterceptor(dispatchInterceptor);
        commandBus.registerHandlerInterceptor(handlerInterceptor);
    }
}