package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.offender

import java.sql.Timestamp

data class OffenderMatchGroup(
  val id: Int,
  val defendantId: Int?,
  val prosecutionCaseId: Int?,
  val createdAt: Timestamp?,
  val createdBy: String?,
  val updatedAt: Timestamp?,
  val updatedBy: String?,
  val isDeleted: Boolean?,
  val version: Int?,
)
