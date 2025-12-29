package uk.gov.justice.digital.hmpps.courtcaseserviceapi.config.security

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
@Profile("!unsecured")
class ApplicationSecurityConfiguration {

  @Bean
  fun apiHttpSecurity(http: ServerHttpSecurity): SecurityWebFilterChain = http.securityMatcher(PathPatternParserServerWebExchangeMatcher("/**"))
    .sessionManagement { SessionCreationPolicy.STATELESS }
    .csrf { it.disable() }
    .authorizeExchange {
      it.pathMatchers(
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
    }.oauth2Login(Customizer.withDefaults())
    .oauth2Client(Customizer.withDefaults())
    .oauth2ResourceServer { oauth2 ->
      oauth2.jwt { it.jwtAuthenticationConverter(TokenAuthenticationConverter()) }
    }.build()
}
