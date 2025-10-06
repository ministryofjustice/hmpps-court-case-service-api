package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.defendant

import java.sql.Timestamp

data class DefendantHearing(
  val id: Int,
  val defendantId: Int?,
  val hearingId: Int?,
  val createdAt: Timestamp?,
  val createdBy: String?,
  val updatedAt: Timestamp?,
  val updatedBy: String?,
  val isDeleted: Boolean?,
  val version: Int?,
)
