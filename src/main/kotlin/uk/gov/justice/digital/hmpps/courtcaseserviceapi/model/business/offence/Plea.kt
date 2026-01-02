package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.offence

import java.time.OffsetDateTime
import java.util.UUID

data class Plea(
  val id: UUID?,
  val pleaDate: OffsetDateTime?,
  val value: String?,
  val createdAt: OffsetDateTime?,
  val createdBy: String?,
  val updatedAt: OffsetDateTime?,
  val updatedBy: String?,
  val isSoftDeleted: Boolean?,
  val version: Int?,
)

data class PleaWrapper(
  var pleas: List<Plea>? = null,
)
