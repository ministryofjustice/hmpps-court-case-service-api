package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.cases

data class CaseDocument(
  val id: Int,
  val documentId: String?,
  val documentName: String?,
  val createdAt: String?,
  val createdBy: String?,
  val updatedAt: String?,
  val updatedBy: String?,
  val isDeleted: Boolean?,
  val version: Int?,
)
