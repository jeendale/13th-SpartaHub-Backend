package com.sparta.user.config;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class AuditAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            String usernameHeader = request.getHeader("X-Username");

            if (usernameHeader != null) {
                return Optional.of(usernameHeader);
            }
        }

        // 헤더가 없거나 요청이 잘못된 경우
        return Optional.empty();
    }
}
