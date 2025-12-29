package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.common

import java.time.OffsetDateTime
import java.util.UUID

data class Address(
  val id: UUID,
  val address1: String?,
  val address2: String?,
  val address3: String?,
  val address4: String?,
  val address5: String?,
  val postcode: String?,
  val createdAt: OffsetDateTime?,
  val createdBy: String?,
  val updatedAt: OffsetDateTime?,
  val updatedBy: String?,
  val isSoftDeleted: Boolean?,
  val version: Int?,
)
