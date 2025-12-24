package uk.gov.justice.digital.hmpps.courtcaseserviceapi.config.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken

class JwtTokenAuthenticationImpl(jwtToken: Jwt,
                                 clientOnly: Boolean,
                                 authorities: Collection<GrantedAuthority>): JwtAuthenticationToken(jwtToken, authorities) {

  private val subject = jwtToken.subject
  private val clientOnly = clientOnly
}