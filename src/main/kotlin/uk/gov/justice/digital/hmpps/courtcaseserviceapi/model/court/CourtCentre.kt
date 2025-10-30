package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.court

import java.sql.Timestamp

data class CourtCentre(
  val id: Int,
  val code: String?,
  val name: String?,
  val roomId: Int?,
  val roomName: String?,
  val psaCode: String?,
  val region: String?,
  val address: String?,
  val createdAt: Timestamp?,
  val createdBy: String?,
  val updatedAt: Timestamp?,
  val updatedBy: String?,
  val isDeleted: Boolean?,
  val version: Int?,
)
