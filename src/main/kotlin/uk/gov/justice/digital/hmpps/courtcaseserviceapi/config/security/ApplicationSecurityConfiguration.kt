package uk.gov.justice.digital.hmpps.courtcaseserviceapi.config.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher

@Configuration
@EnableWebFluxSecurity
@Profile("secured")
class ApplicationSecurityConfiguration {

  @field:Value("\${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
  private lateinit var issuer: String

  @Bean
  fun apiHttpSecurity(http: ServerHttpSecurity): SecurityWebFilterChain = http.securityMatcher(PathPatternParserServerWebExchangeMatcher("/**"))
    .sessionManagement { SessionCreationPolicy.STATELESS }
    .csrf { csrf -> csrf.disable() }
    .oauth2Client(Customizer.withDefaults())
    .authorizeExchange { exchanges ->
      exchanges.pathMatchers(
        "/health/**",
        "/info",
        "/ping",
        "/swagger-ui.html",
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/queue-admin/retry-all-dlqs",
        "/process-un-resulted-cases",
        "/hearing/delete-duplicates",
      ).permitAll()
        .anyExchange().hasAnyRole("PREPARE_A_CASE", "SAR_DATA_ACCESS")
    }
    .oauth2ResourceServer { oauth2 ->
      oauth2.jwt { it.jwtAuthenticationConverter(TokenAuthenticationConverter()) }
    }.build()
}
