package uk.gov.justice.digital.hmpps.courtcaseserviceapi.config.security

import org.springframework.stereotype.Component
import java.security.Principal

private const val USER_UUID = "user_uuid"
private const val USER_ID = "user_id"
private const val USER_NAME = "user_name"
private const val AUTH_SOURCE = "auth_source"

@Component
class AuthenticationExtractor {

  fun extractAuthUserUuid(principal: Principal): String = (principal as JwtTokenAuthenticationImpl).tokenAttributes[USER_UUID].toString()
  fun extractAuthUserId(principal: Principal): String = (principal as JwtTokenAuthenticationImpl).tokenAttributes[USER_ID].toString()
  fun extractAuthUserName(principal: Principal): String = (principal as JwtTokenAuthenticationImpl).tokenAttributes[USER_NAME].toString()
  fun extractAuthSource(principal: Principal): String = (principal as JwtTokenAuthenticationImpl).tokenAttributes[AUTH_SOURCE].toString()
}
