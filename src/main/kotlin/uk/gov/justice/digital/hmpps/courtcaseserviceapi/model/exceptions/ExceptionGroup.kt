package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.exceptions

import java.util.UUID

open class ApiException(message: String, cause: Throwable) : RuntimeException(message, cause)
class ClientException(message: String, cause: Throwable) : ApiException(message, cause)
class ServerException(message: String, cause: Throwable) : ApiException(message, cause)
class HearingCaseNoteDraftNotFoundException(message: String, vararg args: Any) : RuntimeException(String.format(message, *args))

class HearingNotFoundException(id: UUID) : RuntimeException(String.format(MESSAGE, id)) {
  companion object {
    private const val MESSAGE = "Hearing [id=%d] not found"
  }
}
