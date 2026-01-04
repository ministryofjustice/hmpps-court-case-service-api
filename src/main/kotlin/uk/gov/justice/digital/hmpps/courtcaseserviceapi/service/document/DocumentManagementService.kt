package uk.gov.justice.digital.hmpps.courtcaseserviceapi.service.document

import org.springframework.http.codec.multipart.FilePart
import reactor.core.publisher.Mono
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.api.cases.CaseDocumentResponse
import java.util.UUID

interface DocumentManagementService {
  fun uploadFileToDocumentManagementService(
    hearingId: UUID,
    defendantId: UUID,
    file: FilePart,
  ): Mono<CaseDocumentResponse>
}
