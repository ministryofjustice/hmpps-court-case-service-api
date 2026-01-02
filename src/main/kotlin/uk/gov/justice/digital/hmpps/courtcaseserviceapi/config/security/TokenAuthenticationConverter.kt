package uk.gov.justice.digital.hmpps.courtcaseserviceapi.config.security

import org.slf4j.LoggerFactory
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import reactor.core.publisher.Mono

class TokenAuthenticationConverter : Converter<Jwt, Mono<AbstractAuthenticationToken>> {

  private val log = LoggerFactory.getLogger(TokenAuthenticationConverter::class.java)

  override fun convert(jwtToken: Jwt): Mono<AbstractAuthenticationToken> = Mono.just(jwtToken).map(this::doConversion)

  private fun doConversion(jwtToken: Jwt): AbstractAuthenticationToken {
    val clientId = jwtToken.claims["client_id"]
    val clientOnly = jwtToken.subject == clientId
    log.debug("Converting JWT with subject: ${jwtToken.subject}")
    return JwtTokenAuthenticationImpl(jwtToken, clientOnly, manuallyExtractAuthorities(jwtToken))
  }

  private fun manuallyExtractAuthorities(jwtToken: Jwt): Collection<GrantedAuthority> {
    val authClaim = jwtToken.claims["authorities"] ?: return emptyList()
    return when (authClaim) {
      is Collection<*> -> authClaim.mapNotNull { it?.toString() }
        .map { SimpleGrantedAuthority(it) }
      else -> emptyList()
    }
  }
}
