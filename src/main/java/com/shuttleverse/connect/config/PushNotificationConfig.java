package com.shuttleverse.connect.config;

import java.security.GeneralSecurityException;
import java.security.Security;
import lombok.Getter;
import lombok.Setter;
import nl.martijndwars.webpush.PushService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "push.notifications.vapid")
@Getter
@Setter
public class PushNotificationConfig {

    private String publicKey;
    private String privateKey;
    private String subject;

    @Bean
    public PushService pushService() throws GeneralSecurityException {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        if (publicKey == null || privateKey == null || subject == null) {
            throw new IllegalStateException(
                    "VAPID keys must be configured");
        }
        return new PushService(publicKey, privateKey, subject);
    }
}
