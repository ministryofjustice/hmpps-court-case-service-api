package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.defendant

import java.time.OffsetDateTime
import java.util.UUID

data class ContactInformation(
  val id: UUID,
  val home: String?,
  val work: String?,
  val mobile: String?,
  val primaryEmail: String?,
  val secondaryEmail: String?,
  val fax: String?,
  val createdAt: OffsetDateTime?,
  val createdBy: String?,
  val updatedAt: OffsetDateTime?,
  val updatedBy: String?,
  val isSoftDeleted: Boolean?,
  val version: Int?,
)
