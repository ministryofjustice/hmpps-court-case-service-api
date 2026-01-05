package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.cases

import java.time.OffsetDateTime
import java.util.UUID

data class CaseMarker(
  val id: UUID?,
  val typeId: String?,
  val typeCode: String?,
  val typeDescription: String?,
  val createdAt: OffsetDateTime?,
  val createdBy: String?,
  val updatedAt: OffsetDateTime?,
  val updatedBy: String?,
  val isSoftDeleted: Boolean?,
  val version: Int?,
)
