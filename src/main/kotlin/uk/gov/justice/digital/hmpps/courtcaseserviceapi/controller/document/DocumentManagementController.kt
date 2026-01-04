package uk.gov.justice.digital.hmpps.courtcaseserviceapi.controller.document

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.api.cases.CaseDocumentResponse
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.service.document.DocumentManagementService
import java.util.UUID

@RestController
class DocumentManagementController(private val documentManagementService: DocumentManagementService) {

  @PostMapping(
    value = ["/hearing/{hearingId}/defendant/{defendantId}/file"],
    consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
  )
  fun uploadCaseDocumentToDocumentManagementService(
    @PathVariable hearingId: UUID,
    @PathVariable defendantId: UUID,
    @RequestPart("file") file: FilePart,
  ): Mono<ResponseEntity<CaseDocumentResponse>> = documentManagementService.uploadFileToDocumentManagementService(hearingId, defendantId, file)
    .map { ResponseEntity.ok(it) }
    .defaultIfEmpty(ResponseEntity.noContent().build())
}
