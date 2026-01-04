package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.document

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Hmpps document management API metadata")
data class HmppsDocumentApiMetadata(
  val caseUrn: String?,
  val defendantId: String,
)
