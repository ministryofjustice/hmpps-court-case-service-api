package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.hearing

import java.time.OffsetDateTime
import java.util.UUID

data class HearingOutcome(
  val id: UUID?,
  val legacyId: Int? = null,
  val defendantId: UUID? = null,
  val type: String?,
  val outcomeDate: OffsetDateTime?,
  val state: String?,
  val assignedTo: String?,
  val assignedToUUID: UUID?,
  val resultedDate: OffsetDateTime?,
  val isLegacy: Boolean?,
  val createdAt: OffsetDateTime?,
  val createdBy: String?,
  val updatedAt: OffsetDateTime?,
  val updatedBy: String?,
  val isSoftDeleted: Boolean?,
  val version: Int?,
)

data class HearingOutcomeWrapper(
  var hearingOutcomes: List<HearingOutcome>? = null,
)
