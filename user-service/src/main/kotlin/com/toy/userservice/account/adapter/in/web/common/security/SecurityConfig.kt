package com.toy.userservice.account.adapter.`in`.web.common.security

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(JwtProperties::class)
class SecurityConfig {
}
