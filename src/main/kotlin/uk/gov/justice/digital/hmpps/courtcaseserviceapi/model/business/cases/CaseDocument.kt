package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.cases

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime
import java.util.UUID

data class CaseDocument(
  @JsonProperty("id")
  val id: UUID? = null,
  @JsonProperty("defendantId")
  val defendantId: UUID? = null,
  @JsonProperty("documentId")
  val documentId: UUID? = null,
  @JsonProperty("documentName")
  val documentName: String? = null,
  @JsonProperty("createdAt")
  val createdAt: OffsetDateTime? = null,
  @JsonProperty("createdBy")
  val createdBy: String? = null,
  @JsonProperty("updatedAt")
  val updatedAt: OffsetDateTime? = null,
  @JsonProperty("updatedBy")
  val updatedBy: String? = null,
  @JsonProperty("isSoftDeleted")
  val isSoftDeleted: Boolean? = null,
  @JsonProperty("version")
  val version: Int? = null,
)

data class CaseDocumentWrapper(
  var caseDocuments: List<CaseDocument>? = null,
)
