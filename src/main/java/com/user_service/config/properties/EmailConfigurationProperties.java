package com.user_service.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "email-confirmation.token")
public record EmailConfigurationProperties(
        Short expirationDurability
) {
}
