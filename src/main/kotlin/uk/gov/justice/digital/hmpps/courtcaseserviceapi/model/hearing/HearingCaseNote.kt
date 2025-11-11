package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.hearing

data class HearingCaseNote(
  val id: Int,
  val defendantId: String?,
  val note: String?,
  val author: String?,
  val isDraft: Boolean?,
  val isLegacy: Boolean?,
  val createdByUUID: String?,
  val createdAt: String?,
  val createdBy: String?,
  val updatedAt: String?,
  val updatedBy: String?,
  val isDeleted: Boolean?,
  val version: Int?,
)
