package uk.gov.justice.digital.hmpps.courtcaseserviceapi.controller.document

import com.fasterxml.uuid.Generators
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.ContentDisposition
import org.springframework.http.MediaType
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.web.reactive.function.BodyInserters
import reactor.core.publisher.Mono
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.config.security.ApplicationSecurityConfiguration
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.api.cases.CaseDocumentResponse
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.service.document.DocumentManagementService
import java.time.OffsetDateTime

@ActiveProfiles("test", "secured")
@WebFluxTest(DocumentManagementController::class)
@Import(ApplicationSecurityConfiguration::class)
class DocumentManagementControllerTest {

  @MockitoBean
  private lateinit var documentManagementService: DocumentManagementService

  @Autowired
  private lateinit var webTestClient: WebTestClient

  @Test
  fun `uploadCaseDocumentToDocumentManagementService returns ok when service emits response`() {
    val hearingId = Generators.timeBasedEpochRandomGenerator().generate()
    val defendantId = Generators.timeBasedEpochRandomGenerator().generate()
    val expectedResponse = CaseDocumentResponse(
      id = Generators.timeBasedEpochRandomGenerator().generate().toString(),
      datetime = OffsetDateTime.now(),
      file = CaseDocumentResponse.FileResponse(
        name = "document.pdf",
        size = 2048,
      ),
    )

    whenever(
      documentManagementService.uploadFileToDocumentManagementService(eq(hearingId), eq(defendantId), any()),
    ).thenReturn(Mono.just(expectedResponse))

    webTestClient
      .mutateWith(SecurityMockServerConfigurers.mockJwt().authorities(SimpleGrantedAuthority("ROLE_PREPARE_A_CASE")))
      .post()
      .uri("/hearing/$hearingId/defendant/$defendantId/file")
      .contentType(MediaType.MULTIPART_FORM_DATA)
      .body(BodyInserters.fromMultipartData(buildMultipartData()))
      .exchange()
      .expectStatus().isOk
      .expectBody<CaseDocumentResponse>()
      .value { body ->
        assertThat(body.id).isEqualTo(expectedResponse.id)
        assertThat(body.datetime).isEqualTo(expectedResponse.datetime)
        assertThat(body.file.name).isEqualTo(expectedResponse.file.name)
        assertThat(body.file.size).isEqualTo(expectedResponse.file.size)
      }

    verify(documentManagementService).uploadFileToDocumentManagementService(eq(hearingId), eq(defendantId), any())
  }

  @Test
  fun `uploadCaseDocumentToDocumentManagementService returns no content when service is empty`() {
    val hearingId = Generators.timeBasedEpochRandomGenerator().generate()
    val defendantId = Generators.timeBasedEpochRandomGenerator().generate()

    whenever(
      documentManagementService.uploadFileToDocumentManagementService(eq(hearingId), eq(defendantId), any()),
    ).thenReturn(Mono.empty())

    webTestClient
      .mutateWith(SecurityMockServerConfigurers.mockJwt().authorities(SimpleGrantedAuthority("ROLE_PREPARE_A_CASE")))
      .post()
      .uri("/hearing/$hearingId/defendant/$defendantId/file")
      .contentType(MediaType.MULTIPART_FORM_DATA)
      .body(BodyInserters.fromMultipartData(buildMultipartData()))
      .exchange()
      .expectStatus().isNoContent

    verify(documentManagementService).uploadFileToDocumentManagementService(eq(hearingId), eq(defendantId), any())
  }

  private fun buildMultipartData() = MultipartBodyBuilder().also { builder ->
    builder.part("file", ByteArrayResource(ByteArray(0))).headers { headers ->
      headers.contentType = MediaType.APPLICATION_PDF
      headers.contentDisposition = ContentDisposition.builder("form-data")
        .name("file")
        .filename("document.pdf")
        .build()
    }
  }.build()
}
