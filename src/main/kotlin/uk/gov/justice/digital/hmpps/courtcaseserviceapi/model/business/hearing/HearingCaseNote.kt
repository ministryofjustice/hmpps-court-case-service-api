package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.hearing

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime
import java.util.UUID

data class HearingCaseNote(
  @JsonProperty("id")
  val id: UUID?,
  @JsonProperty("legacy_id")
  val legacyId: Int?,
  @JsonProperty("defendant_id")
  val defendantId: UUID?,
  @JsonProperty("name")
  var note: String?,
  @JsonProperty("author")
  var author: String?,
  @JsonProperty("isDraft")
  var isDraft: Boolean?,
  @JsonProperty("isLegacy")
  val isLegacy: Boolean?,
  @JsonProperty("createdByUUID")
  var createdByUUID: UUID?,
  @JsonProperty("createdAt")
  val createdAt: OffsetDateTime?,
  @JsonProperty("createdBy")
  val createdBy: String?,
  @JsonProperty("updatedAt")
  var updatedAt: OffsetDateTime?,
  @JsonProperty("updatedBy")
  var updatedBy: String?,
  @JsonProperty("isSoftDeleted")
  var isSoftDeleted: Boolean?,
  @JsonProperty("version")
  var version: Int?,
)

data class HearingCaseNoteWrapper(
  var caseNotes: List<HearingCaseNote>? = null,
)
