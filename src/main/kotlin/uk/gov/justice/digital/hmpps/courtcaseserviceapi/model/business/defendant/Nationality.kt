package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.defendant

import java.time.OffsetDateTime
import java.util.UUID

data class Nationality(
  val id: UUID?,
  val nationalityId: String?,
  val nationalityCode: String?,
  val description: String?,
  val additionalNationalityId: String?,
  val additionalNationalityCode: String?,
  val additionalNationalityDescription: String?,
  val createdAt: OffsetDateTime?,
  val createdBy: String?,
  val updatedAt: OffsetDateTime?,
  val updatedBy: String?,
  val isSoftDeleted: Boolean?,
  val version: Int?,
)
