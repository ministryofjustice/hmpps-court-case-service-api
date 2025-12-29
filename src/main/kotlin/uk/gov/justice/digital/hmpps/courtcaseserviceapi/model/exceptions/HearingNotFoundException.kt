package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.exceptions

import java.util.UUID

class HearingNotFoundException(id: UUID) : RuntimeException(String.format(MESSAGE, id)) {
  companion object {
    private const val MESSAGE = "Hearing [id=%d] not found"
  }
}
