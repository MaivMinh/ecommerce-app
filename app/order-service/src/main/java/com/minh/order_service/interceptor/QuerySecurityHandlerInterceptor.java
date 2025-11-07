package com.minh.order_service.interceptor;

import org.axonframework.messaging.InterceptorChain;
import org.axonframework.messaging.Message;
import org.axonframework.messaging.MessageHandlerInterceptor;
import org.axonframework.messaging.unitofwork.UnitOfWork;
import org.axonframework.queryhandling.QueryMessage;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;

@Component
public class QuerySecurityHandlerInterceptor implements MessageHandlerInterceptor<QueryMessage<?, ?>> {

    @Override
    public Object handle(@Nonnull UnitOfWork<? extends QueryMessage<?, ?>> unitOfWork, @Nonnull InterceptorChain interceptorChain) throws Exception {
        Message<?> message = unitOfWork.getMessage();
        String username = (String) message.getMetaData().get("username");
        if (username != null) {
            var auth = new UsernamePasswordAuthenticationToken(username, null, List.of());
            var ctx = SecurityContextHolder.createEmptyContext();
            ctx.setAuthentication(auth);
            SecurityContextHolder.setContext(ctx);
        }
        return interceptorChain.proceed();
    }
}