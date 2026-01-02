package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.offence

import java.time.OffsetDateTime
import java.util.UUID

data class JudicialResult(
  val id: UUID?,
  val isConvictedResult: Boolean?,
  val label: String?,
  val resultTypeId: String?,
  val resultText: String?,
  val isJudicialResultDeleted: Boolean?,
  val createdAt: OffsetDateTime?,
  val createdBy: String?,
  val updatedAt: OffsetDateTime?,
  val updatedBy: String?,
  val isSoftDeleted: Boolean?,
  val version: Int?,
)

data class JudicialResultWrapper(
  var judicialResults: List<JudicialResult>?,
)
