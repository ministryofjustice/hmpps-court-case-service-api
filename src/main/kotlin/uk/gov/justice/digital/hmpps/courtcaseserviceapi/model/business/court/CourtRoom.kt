package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.court

import java.time.OffsetDateTime
import java.util.UUID

data class CourtRoom(
  val id: UUID?,
  val roomId: UUID?,
  val roomName: String?,
  val createdAt: OffsetDateTime?,
  val createdBy: String?,
  val updatedAt: OffsetDateTime?,
  val updatedBy: String?,
  val isSoftDeleted: Boolean?,
  val version: Int?,
)
