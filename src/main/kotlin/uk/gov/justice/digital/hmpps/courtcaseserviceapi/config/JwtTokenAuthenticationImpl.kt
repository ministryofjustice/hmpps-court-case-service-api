package uk.gov.justice.digital.hmpps.courtcaseserviceapi.config

import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken

class JwtTokenAuthenticationImpl(jwtToken: Jwt,
                                 private val clientOnly: Boolean,
                                 authorities: Collection<GrantedAuthority>): JwtAuthenticationToken(jwtToken, authorities) {

  private val subject: String = jwtToken.subject
}