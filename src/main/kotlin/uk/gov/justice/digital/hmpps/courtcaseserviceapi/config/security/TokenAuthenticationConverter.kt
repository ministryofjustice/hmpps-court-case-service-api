package uk.gov.justice.digital.hmpps.courtcaseserviceapi.config.security

import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import reactor.core.publisher.Mono

class TokenAuthenticationConverter: Converter<Jwt, Mono<AbstractAuthenticationToken>> {

  override fun convert(jwtToken: Jwt): Mono<AbstractAuthenticationToken> {
    return Mono.just(jwtToken).map(this::doConversion)
  }

  private fun doConversion(jwtToken: Jwt): AbstractAuthenticationToken {
    val clientId = jwtToken.claims["client_id"]
    val clientOnly = jwtToken.subject == clientId
    return JwtTokenAuthenticationImpl(jwtToken, clientOnly, manuallyExtractAuthorities(jwtToken))
  }

  private fun manuallyExtractAuthorities(jwtToken: Jwt): Collection<GrantedAuthority> {
    val authorities: Collection<*> = jwtToken.claims.getOrDefault("authorities", emptyList<Any>()) as Collection<*>
    return authorities
      .map { it.toString() }
      .map { SimpleGrantedAuthority(it) }
      .toList()
  }
}