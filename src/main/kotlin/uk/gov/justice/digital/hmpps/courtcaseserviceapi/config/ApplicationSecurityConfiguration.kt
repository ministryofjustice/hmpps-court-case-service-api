package uk.gov.justice.digital.hmpps.courtcaseserviceapi.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher

@Configuration
@EnableWebFluxSecurity
class ApplicationSecurityConfiguration {

  @Bean
  fun apiHttpSecurity(http: ServerHttpSecurity): SecurityWebFilterChain {
    return http.securityMatcher(PathPatternParserServerWebExchangeMatcher("/**"))
      .authorizeExchange { auth ->
        auth.pathMatchers(
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
      }.oauth2Login(withDefaults())
      .oauth2Client(withDefaults())
      .oauth2ResourceServer { oauth2 ->
        oauth2.jwt { jwt -> jwt.jwtAuthenticationConverter(TokenAuthenticationConverter()) }
      }.build()
  }
}