package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.api.hearing

import java.time.OffsetDateTime
import java.util.UUID

data class HearingCaseNoteResponse(
  val noteId: UUID?,
  val note: String?,
  val createdAt: OffsetDateTime?,
  val author: String?,
  val createdByUuid: UUID?,
  val isDraft: Boolean?,
  val isLegacy: Boolean?,
)
