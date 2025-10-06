package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.hearing

import java.sql.Timestamp

data class Hearing(
  val id: Int,
  val type: String?,
  val eventType: String?,
  val listNumber: String?,
  val hearingOutcome: String?,
  val hearingCaseNote: String?,
  val createdAt: Timestamp?,
  val createdBy: String?,
  val updatedAt: Timestamp?,
  val updatedBy: String?,
  val isDeleted: Boolean?,
  val version: Int?,
)
