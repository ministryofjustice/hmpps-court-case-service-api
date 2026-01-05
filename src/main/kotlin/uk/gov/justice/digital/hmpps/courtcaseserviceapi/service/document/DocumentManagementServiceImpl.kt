package uk.gov.justice.digital.hmpps.courtcaseserviceapi.service.document

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.uuid.Generators
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import org.springframework.util.MultiValueMap
import reactor.core.publisher.Mono
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.config.OAuth2WebClientService
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.api.cases.CaseDocumentResponse
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.api.cases.DocumentUploadResponse
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.cases.CaseDocument
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.document.HmppsDocumentApiMetadata
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.exceptions.CaseNotFoundException
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.exceptions.HearingNotFoundException
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.exceptions.UnsupportedFileTypeException
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.repository.cases.ProsecutionCaseRepository
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.repository.common.CaseHearingDefendantRepository
import java.time.OffsetDateTime
import java.util.UUID
import kotlin.jvm.java

@Service
class DocumentManagementServiceImpl(
  private val caseHearingDefendantRepository: CaseHearingDefendantRepository,
  private val prosecutionCaseRepository: ProsecutionCaseRepository,
  private val oauth2WebClientService: OAuth2WebClientService,
  private val objectMapper: ObjectMapper,
  @Value($$"${hmpps-document-management-api.document-type}") private val picDocumentType: String,
  @Value($$"${hmpps-document-management-api.create-document}") private val documentManagementUploadUrl: String,
  private val documentClientRegistrationId: String = "hmpps-document-management-api-client",
  @Value($$"${hmpps-document-management-api.allowed-file-extensions}") var allowedExtensions: List<String>,
) : DocumentManagementService {

  private val normalizedExtensions = allowedExtensions.map(String::lowercase).toSet()

  override fun uploadFileToDocumentManagementService(
    hearingId: UUID,
    defendantId: UUID,
    file: FilePart,
  ): Mono<CaseDocumentResponse> {
    return Mono.defer {
      val filename = file.filename().takeUnless { it.isBlank() }
        ?: return@defer Mono.error(IllegalArgumentException("Uploaded file must have a name"))

      val extension = filename.substringAfterLast('.', "").lowercase()
      if (extension.isBlank() || extension !in normalizedExtensions) {
        return@defer Mono.error(UnsupportedFileTypeException(extension, normalizedExtensions.sorted()))
      }

      val fileSize = file.headers().contentLength.takeIf { it >= 0 }

      caseHearingDefendantRepository.findByHearingIdAndDefendantId(hearingId, defendantId)
        .switchIfEmpty(Mono.error(HearingNotFoundException(hearingId.toString())))
        .flatMap { row ->
          val fileMetadata = HmppsDocumentApiMetadata(
            caseUrn = row.caseURN?.firstOrNull { it.caseURN?.isNotBlank() ?: false }?.caseURN
              ?: row.caseURN?.firstOrNull()?.caseURN,
            defendantId = defendantId.toString(),
          )
          val documentId = Generators.timeBasedEpochGenerator().generate()
          val multipartBody = buildUploadBody(documentId, fileMetadata, file)
          val uploadUrl = "$documentManagementUploadUrl/documents/$picDocumentType/$documentId"

          oauth2WebClientService.post(
            uploadUrl,
            multipartBody,
            documentClientRegistrationId,
            DocumentUploadResponse::class.java,
          ).flatMap { uploadResponse ->
            prosecutionCaseRepository.findByCaseId(row.caseId)
              .switchIfEmpty(Mono.error(CaseNotFoundException(row.prosecutionCaseId)))
              .flatMap { prosecutionCase ->
                val today = OffsetDateTime.now()
                val newCaseDocument = CaseDocument(
                  id = Generators.timeBasedEpochGenerator().generate(),
                  defendantId = defendantId,
                  documentId = documentId,
                  documentName = uploadResponse.documentFilename,
                  createdAt = today,
                  isSoftDeleted = false,
                  version = (prosecutionCase.caseDocument?.size ?: 0) + 1,
                )

                val updatedCase = prosecutionCase.copy(
                  caseDocument = (prosecutionCase.caseDocument ?: emptyList()) + newCaseDocument,
                )
                prosecutionCaseRepository.save(updatedCase).map {
                  CaseDocumentResponse(
                    id = documentId.toString(),
                    datetime = today,
                    file = CaseDocumentResponse.FileResponse(
                      name = uploadResponse.filename,
                      size = fileSize,
                    ),
                  )
                }
              }
          }
        }
    }
  }

  private fun buildUploadBody(
    documentId: UUID,
    metadata: HmppsDocumentApiMetadata,
    file: FilePart,
  ): MultiValueMap<String, HttpEntity<*>?> {
    val builder = MultipartBodyBuilder()
    builder.part("documentId", documentId.toString())

    builder.part("metadata", objectMapper.writeValueAsString(metadata))
      .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)

    builder.part("file", file)
      .header(HttpHeaders.CONTENT_DISPOSITION, "form-data; name=file; filename=${file.filename()}")
      .header(
        HttpHeaders.CONTENT_TYPE,
        file.headers().contentType?.toString() ?: MediaType.APPLICATION_OCTET_STREAM_VALUE,
      )

    return builder.build()
  }
}
