package com.tf4.delivery.common.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;
import java.util.UUID;

@Component("auditorAware")
public class AuditAwareImpl implements AuditorAware<UUID> {

    @Override
    public Optional<UUID> getCurrentAuditor() {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();

        // check type & casting
        if (requestAttributes instanceof ServletRequestAttributes) {
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
            HttpServletRequest request = servletRequestAttributes.getRequest();
            String userId = request.getHeader("X-User-Id");

            if (userId != null && !userId.isEmpty()) {
                return Optional.of(UUID.fromString(userId));
            }
        }

        return Optional.of(UUID.fromString("00000000-0000-0000-0000-000000000000"));

    }
}