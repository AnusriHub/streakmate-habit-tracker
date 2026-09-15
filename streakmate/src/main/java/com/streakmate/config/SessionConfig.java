package com.streakmate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SessionConfig implements WebMvcConfigurer {
    public static final String SESSION_USER_ID = "sessionUserId";
    public static final String SESSION_USERNAME = "sessionUsername";
}
