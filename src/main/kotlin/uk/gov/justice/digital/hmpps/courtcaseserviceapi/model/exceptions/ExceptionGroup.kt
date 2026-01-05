package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.exceptions

open class ApiException(message: String, cause: Throwable) : RuntimeException(message, cause)
class ClientException(message: String, cause: Throwable) : ApiException(message, cause)
class ServerException(message: String, cause: Throwable) : ApiException(message, cause)
class HearingCaseNoteDraftNotFoundException(message: String, vararg args: Any) : RuntimeException(String.format(message, *args))

class HearingNotFoundException(hearingId: String) : RuntimeException(String.format(MESSAGE, hearingId)) {
  companion object {
    private const val MESSAGE = "Hearing [hearingId=%s] not found"
  }
}
class HearingCaseNoteNotFoundException : RuntimeException {
  companion object {
    private const val MESSAGE = "Note [noteId=%s] not found for Hearing [hearingId=%s] and Defendant [defendantId=%s] for this user [userUUID=%s]"
  }

  constructor(noteId: String, defendantId: String, hearingId: String, userUUID: String) : super(
    String.format(MESSAGE, noteId, defendantId, hearingId, userUUID),
  )
}
