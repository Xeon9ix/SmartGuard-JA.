package com.scamguardja.config;

import com.google.api.services.gmail.Gmail;
import com.scamguardja.service.GmailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class GmailConfig {

    @Bean
    @Lazy
    public Gmail gmail(GmailService gmailService) throws Exception {
        return gmailService.buildGmailClient("default-user");
    }
}