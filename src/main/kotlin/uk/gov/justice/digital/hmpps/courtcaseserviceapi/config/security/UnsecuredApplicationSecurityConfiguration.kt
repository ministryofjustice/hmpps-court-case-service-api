package uk.gov.justice.digital.hmpps.courtcaseserviceapi.config.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableWebFluxSecurity
@Profile("unsecured")
class UnsecuredApplicationSecurityConfiguration {

  @Bean
  fun SecurityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain = http
    .sessionManagement { SessionCreationPolicy.STATELESS }
    .authorizeExchange { exchange ->
      exchange.anyExchange().permitAll()
    }
    .csrf { it.disable() }
    .build()
}
