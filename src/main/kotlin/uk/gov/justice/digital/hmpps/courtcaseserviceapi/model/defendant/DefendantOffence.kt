package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.defendant

import java.sql.Timestamp

data class DefendantOffence(
  val id: Int,
  val offenceId: Int?,
  val defendantId: Int?,
  val createdAt: Timestamp?,
  val createdBy: String?,
  val updatedAt: Timestamp?,
  val updatedBy: String?,
  val isDeleted: Boolean?,
  val version: Int?,
)
