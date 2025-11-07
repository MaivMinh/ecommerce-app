package com.minh.promotion_service.interceptor;

import org.axonframework.eventhandling.EventMessage;
import org.axonframework.messaging.InterceptorChain;
import org.axonframework.messaging.MessageHandlerInterceptor;
import org.axonframework.messaging.MetaData;
import org.axonframework.messaging.unitofwork.UnitOfWork;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class EventSecurityContextPropagationInterceptor implements MessageHandlerInterceptor<EventMessage<?>> {
    @Override
    public Object handle(UnitOfWork<? extends EventMessage<?>> unitOfWork, InterceptorChain interceptorChain) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            unitOfWork.transformMessage(message -> message.andMetaData(MetaData.with("authentication", authentication)));
        }
        return interceptorChain.proceed();
    }
}