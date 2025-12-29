package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.offence

import java.time.OffsetDateTime
import java.util.UUID

data class Verdict(
  val id: UUID,
  val verdictDate: OffsetDateTime?,
  val typeDescription: String?,
  val createdAt: OffsetDateTime?,
  val createdBy: String?,
  val updatedAt: OffsetDateTime?,
  val updatedBy: String?,
  val isSoftDeleted: Boolean?,
  val version: Int?,
)
