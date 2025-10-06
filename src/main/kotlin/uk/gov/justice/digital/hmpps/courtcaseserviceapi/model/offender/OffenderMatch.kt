package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.offender

import java.sql.Timestamp

data class OffenderMatch(
  val id: Int,
  val offenderId: Int?,
  val offenderMatchGroupId: Int?,
  val createdAt: Timestamp?,
  val createdBy: String?,
  val updatedAt: Timestamp?,
  val updatedBy: String?,
  val isDeleted: Boolean?,
  val version: Int?,
)
