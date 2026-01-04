package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.api.cases

import io.swagger.v3.oas.annotations.media.Schema
import java.time.OffsetDateTime

@Schema(description = "Case defendant documents object")
data class CaseDocumentResponse(
  val id: String,
  val datetime: OffsetDateTime,
  val file: FileResponse,
) {
  data class FileResponse(val name: String, val size: Number? = 0)
}
