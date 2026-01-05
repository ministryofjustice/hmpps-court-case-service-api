package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.api

import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.defendant.DefendantProbationStatus
import java.time.LocalDate

data class ProbationStatusDetails(
  val status: String,
  val previouslyKnownTerminationDate: LocalDate?,
  val isInBreach: Boolean?,
  val isPreSentenceActivity: Boolean,
  val isAwaitingPsr: Boolean?,
) {
  companion object {
    val NO_RECORD_STATUS = ProbationStatusDetails(
      status = DefendantProbationStatus.UNCONFIRMED_NO_RECORD.name,
      previouslyKnownTerminationDate = null,
      isInBreach = null,
      isPreSentenceActivity = false,
      isAwaitingPsr = null,
    )
  }
}
