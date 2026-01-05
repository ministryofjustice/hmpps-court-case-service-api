package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.cases

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime
import java.util.UUID

data class CaseURN(
  val id: UUID?,
  val caseURN: String?,
  val createdAt: OffsetDateTime?,
  val createdBy: String?,
  val updatedAt: OffsetDateTime?,
  val updatedBy: String?,
  val isSoftDeleted: Boolean?,
  val version: Int?,
)

data class CaseURNs(
  @JsonProperty("caseURNs")
  val caseURNs: List<CaseURN>,
)
