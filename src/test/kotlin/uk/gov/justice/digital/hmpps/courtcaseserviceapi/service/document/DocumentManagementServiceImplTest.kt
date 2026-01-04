package uk.gov.justice.digital.hmpps.courtcaseserviceapi.service.document

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.uuid.Generators
import io.mockk.Called
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.util.MultiValueMap
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.config.OAuth2WebClientService
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.api.cases.DocumentUploadResponse
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.cases.ProsecutionCase
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.common.caseDocuments.CaseHearingDefendant
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.exceptions.HearingNotFoundException
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.exceptions.UnsupportedFileTypeException
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.repository.cases.ProsecutionCaseRepository
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.repository.common.CaseHearingDefendantRepository
import java.time.OffsetDateTime

class DocumentManagementServiceImplTest {

  private lateinit var caseHearingDefendantRepository: CaseHearingDefendantRepository
  private lateinit var prosecutionCaseRepository: ProsecutionCaseRepository
  private lateinit var oauth2WebClientService: OAuth2WebClientService
  private lateinit var filePart: FilePart
  private lateinit var headers: HttpHeaders
  private lateinit var service: DocumentManagementServiceImpl

  private val objectMapper = ObjectMapper()
  private lateinit var allowedExtensions: List<String>
  private val picDocumentType = "PIC_CASE_DOCUMENTS"
  private val documentManagementUploadUrl = "https://doc-management/upload"
  private val documentClientRegistrationId = "doc-client"

  @BeforeEach
  fun setUp() {
    allowedExtensions = listOf(
      "csv",
      "doc",
      "docx",
      "jpg",
      "jpeg",
      "xml",
      "ods",
      "odt",
      "pdf",
      "png",
      "ppt",
      "pptx",
      "rdf",
      "rtf",
      "txt",
      "xls",
      "xlsx",
      "zip",
    )

    caseHearingDefendantRepository = mockk()
    prosecutionCaseRepository = mockk()
    oauth2WebClientService = mockk()
    filePart = mockk()
    headers = HttpHeaders()
    headers.contentType = MediaType.APPLICATION_PDF
    headers.setContentLength(2048L)
    every { filePart.headers() } returns headers
    every { filePart.filename() } returns "evidence.pdf"
    every { filePart.content() } returns Flux.empty()

    service = DocumentManagementServiceImpl(
      caseHearingDefendantRepository,
      prosecutionCaseRepository,
      oauth2WebClientService,
      objectMapper,
      picDocumentType,
      documentManagementUploadUrl,
      documentClientRegistrationId,
      allowedExtensions,
    )
  }

  @AfterEach
  fun tearDown() {
    clearAllMocks()
  }

  @Test
  fun `uploadFileToDocumentManagementService throws when extension is unsupported`() {
    every { filePart.filename() } returns "evidence.exe"

    StepVerifier.create(
      service.uploadFileToDocumentManagementService(
        Generators.timeBasedEpochGenerator().generate(),
        Generators.timeBasedEpochGenerator().generate(),
        filePart,
      ),
    ).expectErrorMatches { error ->
      error is UnsupportedFileTypeException &&
        !error.supportedExtensions.contains("exe")
    }.verify()

    verify { caseHearingDefendantRepository wasNot Called }
    verify { prosecutionCaseRepository wasNot Called }
    verify { oauth2WebClientService wasNot Called }
  }

  @Test
  fun `uploadFileToDocumentManagementService propagates hearing not found error`() {
    val hearingId = Generators.timeBasedEpochGenerator().generate()
    val defendantId = Generators.timeBasedEpochGenerator().generate()
    every { caseHearingDefendantRepository.findByHearingIdAndDefendantId(hearingId, defendantId) } returns Mono.empty()

    StepVerifier.create(service.uploadFileToDocumentManagementService(hearingId, defendantId, filePart))
      .expectErrorMatches { error ->
        error is HearingNotFoundException && error.message?.contains(hearingId.toString()) == true
      }
      .verify()

    verify(exactly = 1) { caseHearingDefendantRepository.findByHearingIdAndDefendantId(hearingId, defendantId) }
    verify { prosecutionCaseRepository wasNot Called }
    verify { oauth2WebClientService wasNot Called }
  }

  @Test
  fun `uploadFileToDocumentManagementService returns response for happy path`() {
    val hearingId = Generators.timeBasedEpochGenerator().generate()
    val defendantId = Generators.timeBasedEpochGenerator().generate()
    val caseId = "CASE-123"
    val prosecutionCaseId = Generators.timeBasedEpochGenerator().generate()
    val row = mockk<CaseHearingDefendant>()

    val existingCase = ProsecutionCase(
      id = prosecutionCaseId,
      caseId = caseId,
      caseNumber = "12345",
      caseURN = emptyList(),
      sourceType = "SOURCE",
      caseMarker = emptyList(),
      caseDocument = emptyList(),
      createdAt = OffsetDateTime.now(),
      createdBy = "system",
      updatedAt = OffsetDateTime.now(),
      updatedBy = "system",
      isSoftDeleted = false,
      version = 1,
    )

    val uploadResponse = DocumentUploadResponse(
      documentUuid = Generators.timeBasedEpochGenerator().generate(),
      documentFilename = "document.pdf",
      filename = "source-file.pdf",
      fileExtension = "pdf",
      mimeType = MediaType.APPLICATION_PDF_VALUE,
    )

    val expectedUploadUrlPrefix = "$documentManagementUploadUrl/documents/$picDocumentType/"

    every { row.caseId } returns caseId
    every { row.caseURN } returns null
    every { row.prosecutionCaseId } returns prosecutionCaseId
    every { filePart.content() } returns Flux.empty()

    every {
      caseHearingDefendantRepository.findByHearingIdAndDefendantId(any(), any())
    } returns Mono.just(row)

    every {
      oauth2WebClientService.post(
        match { it.startsWith(expectedUploadUrlPrefix) },
        any<MultiValueMap<String, HttpEntity<*>?>>(),
        documentClientRegistrationId,
        DocumentUploadResponse::class.java,
      )
    } returns Mono.just(uploadResponse)

    every { prosecutionCaseRepository.findByCaseId(caseId) } returns Mono.just(existingCase)
    every { prosecutionCaseRepository.save(any()) } answers {
      val savedCase = it.invocation.args[0] as ProsecutionCase
      Mono.just(savedCase)
    }

    StepVerifier.create(service.uploadFileToDocumentManagementService(hearingId, defendantId, filePart))
      .assertNext { response ->
        assertThat(uploadResponse.filename).isEqualTo(response.file.name)
        assertThat(2048L).isEqualTo(response.file.size)
      }
      .verifyComplete()

    verify(exactly = 1) {
      oauth2WebClientService.post(
        match { it.startsWith(expectedUploadUrlPrefix) },
        any<MultiValueMap<String, HttpEntity<*>?>>(),
        documentClientRegistrationId,
        DocumentUploadResponse::class.java,
      )
    }
  }
}
