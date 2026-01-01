package uk.gov.justice.digital.hmpps.courtcaseserviceapi.config.security

import org.springframework.stereotype.Component
import java.security.Principal

private const val USER_UUID = "user_uuid"
private const val USER_ID = "user_id"
private const val USER_NAME = "user_name"
private const val AUTH_SOURCE = "auth_source"

@Component
class AuthenticationExtractor {

  fun extractAuthUserUuid(principal: Principal): String = extractAttribute(principal, USER_UUID)

  fun extractAuthUserId(principal: Principal): String = extractAttribute(principal, USER_ID)

  fun extractAuthUserName(principal: Principal): String = extractAttribute(principal, USER_NAME)

  fun extractAuthSource(principal: Principal): String = extractAttribute(principal, AUTH_SOURCE)

  private fun extractAttribute(principal: Principal, attributeKey: String): String = when (principal) {
    is JwtTokenAuthenticationImpl -> {
      principal.tokenAttributes[attributeKey]?.toString()
        ?: throw IllegalStateException("Attribute '$attributeKey' not found in token")
    }
    else -> throw IllegalArgumentException(
      "Unsupported principal type: ${principal.javaClass.simpleName}",
    )
  }
}
