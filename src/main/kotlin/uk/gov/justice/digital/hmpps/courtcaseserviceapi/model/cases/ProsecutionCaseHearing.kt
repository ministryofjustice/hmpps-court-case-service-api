package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.cases

import java.sql.Timestamp

data class ProsecutionCaseHearing(
  val id: Int,
  val hearingId: Int?,
  val prosecutionCaseId: Int?,
  val createdAt: Timestamp?,
  val createdBy: String?,
  val updatedAt: Timestamp?,
  val updatedBy: String?,
  val isDeleted: Boolean?,
  val version: Int?,
)
