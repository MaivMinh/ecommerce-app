package com.minh.order_service.interceptor;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.axonframework.queryhandling.QueryMessage;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

@Component
public class QuerySecurityDispatchInterceptor implements MessageDispatchInterceptor<QueryMessage<?, ?>> {
    @Override
    public BiFunction<Integer, QueryMessage<?, ?>, QueryMessage<?, ?>> handle(List<? extends QueryMessage<?, ?>> messages) {
        return (index, message) -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                Map<String, Object> meta = new HashMap<>();
                meta.put("username", auth.getName());
                return message.andMetaData(meta);
            }
            return message;
        };
    }
}