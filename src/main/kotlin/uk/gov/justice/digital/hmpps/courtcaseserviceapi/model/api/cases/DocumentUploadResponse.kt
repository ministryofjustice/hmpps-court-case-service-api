package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.api.cases

import java.util.UUID

data class DocumentUploadResponse(
  val documentUuid: UUID,
  val documentFilename: String,
  val filename: String,
  val fileExtension: String,
  val mimeType: String,
)
