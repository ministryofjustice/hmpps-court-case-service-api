package uk.gov.justice.digital.hmpps.courtcaseserviceapi.controller

import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import reactor.test.StepVerifier
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.config.security.AuthenticationExtractor
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.data.ApiTestDataBuilder
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.data.ApiTestDataBuilder.generateUUID
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.api.hearing.HearingCaseNoteRequest
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.api.hearing.HearingCaseNoteResponse
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.repository.common.DefendantHearingRepository
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.repository.hearing.HearingRepository

@AutoConfigureWebTestClient
@ActiveProfiles("test", "secured")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HearingNotesControllerIntegrationTest {

  @Autowired
  lateinit var webTestClient: WebTestClient

  @Autowired
  lateinit var hearingRepository: HearingRepository

  @Autowired
  lateinit var defendantHearingRepository: DefendantHearingRepository

  @MockitoBean
  lateinit var authenticationExtractor: AuthenticationExtractor

  private val hearingId = generateUUID()
  private val defendantId = generateUUID()
  private val userUuid = generateUUID()
  private val noteId = generateUUID()

  @BeforeEach
  fun setup() {
    StepVerifier.create(hearingRepository.deleteAll())
      .verifyComplete()

    val hearing = ApiTestDataBuilder.buildHearing(
      id = hearingId,
      hearingId = hearingId,
      defendantId = defendantId,
    )

    StepVerifier.create(hearingRepository.save(hearing))
      .expectNextCount(1)
      .verifyComplete()

    whenever(authenticationExtractor.extractAuthUserUuid(any())).thenReturn(userUuid.toString())
  }

  @AfterEach
  fun cleanup() {
    StepVerifier.create(hearingRepository.deleteAll())
      .verifyComplete()
  }

  @Test
  fun `should add hearing case note as draft successfully`() {
    val request = HearingCaseNoteRequest(
      note = "Test draft note",
      createdByUUID = null,
      author = "Test Author",
    )

    webTestClient.post()
      .uri("/hearing/$hearingId/defendant/$defendantId/note")
      .headers { it.setBearerAuth(generateMockJwt()) }
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(request)
      .exchange()
      .expectStatus().isCreated
      .expectBody<HearingCaseNoteResponse>()
      .consumeWith { response ->
        val body = response.responseBody
        assertThat(body).isNotNull
        assertThat(body?.noteId).isNotNull()
        assertThat(body?.note).isEqualTo("Test draft note")
        assertThat(body?.author).isEqualTo("Test Author")
        assertThat(body?.createdByUuid).isEqualTo(userUuid)
        assertThat(body?.isDraft).isTrue()
        assertThat(body?.isLegacy).isFalse()
      }

    StepVerifier.create(
      defendantHearingRepository.findByDefendantIdAndHearingId(defendantId, hearingId),
    )
      .assertNext { hearing ->
        assertThat(hearing.hearingCaseNote).isNotNull
        assertThat(hearing.hearingCaseNote).hasSize(1)
        val note = hearing.hearingCaseNote?.first()
        assertThat(note?.note).isEqualTo("Test draft note")
        assertThat(note?.author).isEqualTo("Test Author")
        assertThat(note?.isDraft).isTrue()
        assertThat(note?.createdByUUID).isEqualTo(userUuid)
      }
      .verifyComplete()
  }

  @Test
  fun `should return existing draft note when duplicate draft is submitted`() {
    val existingNoteId = generateUUID()
    val hearing = ApiTestDataBuilder.buildHearingWithDraftNote(
      id = hearingId,
      hearingId = hearingId,
      defendantId = defendantId,
      noteId = existingNoteId,
      note = "Existing draft",
      author = "Test Author",
      createdByUuid = userUuid,
    )

    StepVerifier.create(hearingRepository.save(hearing))
      .expectNextCount(1)
      .verifyComplete()

    val request = HearingCaseNoteRequest(
      note = "New draft note",
      createdByUUID = null,
      author = "Test Author",
    )

    webTestClient.post()
      .uri("/hearing/$hearingId/defendant/$defendantId/note")
      .headers { it.setBearerAuth(generateMockJwt()) }
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(request)
      .exchange()
      .expectStatus().isCreated
      .expectBody<HearingCaseNoteResponse>()
      .consumeWith { response ->
        val body = response.responseBody
        assertThat(body?.noteId).isEqualTo(existingNoteId)
        assertThat(body?.note).isEqualTo("Existing draft")
      }
  }

  @Test
  fun `should update hearing case note draft successfully`() {
    val existingNoteId = generateUUID()
    val hearing = ApiTestDataBuilder.buildHearingWithDraftNote(
      id = hearingId,
      hearingId = hearingId,
      defendantId = defendantId,
      noteId = existingNoteId,
      note = "Original draft",
      author = "Original Author",
      createdByUuid = userUuid,
    )

    StepVerifier.create(hearingRepository.save(hearing))
      .expectNextCount(1)
      .verifyComplete()

    val request = HearingCaseNoteRequest(
      note = "Updated draft note",
      createdByUUID = null,
      author = "Updated Author",
    )

    webTestClient.put()
      .uri("/hearing/$hearingId/defendant/$defendantId/notes/draft")
      .headers { it.setBearerAuth(generateMockJwt()) }
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(request)
      .exchange()
      .expectStatus().isOk
      .expectBody<HearingCaseNoteResponse>()
      .consumeWith { response ->
        val body = response.responseBody
        assertThat(body).isNotNull
        assertThat(body?.note).isEqualTo("Updated draft note")
        assertThat(body?.author).isEqualTo("Updated Author")
        assertThat(body?.isDraft).isTrue()
      }

    StepVerifier.create(
      defendantHearingRepository.findByDefendantIdAndHearingId(defendantId, hearingId),
    )
      .assertNext { updatedHearing ->
        val note = updatedHearing.hearingCaseNote?.first()
        assertThat(note?.note).isEqualTo("Updated draft note")
        assertThat(note?.author).isEqualTo("Updated Author")
        assertThat(note?.updatedAt).isNotNull()
      }
      .verifyComplete()
  }

  @Test
  fun `should return not found when updating non-existent draft note`() {
    val request = HearingCaseNoteRequest(
      note = "Updated note",
      createdByUUID = null,
      author = "Test Author",
    )

    webTestClient.put()
      .uri("/hearing/$hearingId/defendant/$defendantId/notes/draft")
      .headers { it.setBearerAuth(generateMockJwt()) }
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(request)
      .exchange()
      .expectStatus().isNotFound
  }

  @Test
  fun `should delete hearing case note draft successfully`() {
    val existingNoteId = generateUUID()
    val hearing = ApiTestDataBuilder.buildHearingWithDraftNote(
      id = hearingId,
      hearingId = hearingId,
      defendantId = defendantId,
      noteId = existingNoteId,
      note = "Draft to delete",
      author = "Test Author",
      createdByUuid = userUuid,
    )

    StepVerifier.create(hearingRepository.save(hearing))
      .expectNextCount(1)
      .verifyComplete()

    webTestClient.delete()
      .uri("/hearing/$hearingId/defendant/$defendantId/notes/draft")
      .headers { it.setBearerAuth(generateMockJwt()) }
      .exchange()
      .expectStatus().isOk

    StepVerifier.create(
      defendantHearingRepository.findByDefendantIdAndHearingId(defendantId, hearingId),
    )
      .assertNext { updatedHearing ->
        val note = updatedHearing.hearingCaseNote?.first()
        assertThat(note?.isSoftDeleted).isTrue()
      }
      .verifyComplete()
  }

  @Test
  fun `should return not found when deleting non-existent draft note`() {
    webTestClient.delete()
      .uri("/hearing/$hearingId/defendant/$defendantId/notes/draft")
      .headers { it.setBearerAuth(generateMockJwt()) }
      .exchange()
      .expectStatus().isNotFound
  }

  @Test
  fun `should update hearing case note successfully`() {
    val existingNoteId = generateUUID()
    val hearing = ApiTestDataBuilder.buildHearingWithPublishedNote(
      id = hearingId,
      hearingId = hearingId,
      defendantId = defendantId,
      noteId = existingNoteId,
      note = "Original published note",
      author = "Original Author",
      createdByUuid = userUuid,
    )

    StepVerifier.create(hearingRepository.save(hearing))
      .expectNextCount(1)
      .verifyComplete()

    val request = HearingCaseNoteRequest(
      note = "Updated published note",
      createdByUUID = null,
      author = "Updated Author",
    )

    webTestClient.put()
      .uri("/hearing/$hearingId/defendants/$defendantId/notes/$existingNoteId")
      .headers { it.setBearerAuth(generateMockJwt()) }
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(request)
      .exchange()
      .expectStatus().isOk

    StepVerifier.create(
      defendantHearingRepository.findByDefendantIdAndHearingId(defendantId, hearingId),
    )
      .assertNext { updatedHearing ->
        val note = updatedHearing.hearingCaseNote?.first()
        assertThat(note?.note).isEqualTo("Updated published note")
        assertThat(note?.author).isEqualTo("Updated Author")
        assertThat(note?.updatedAt).isNotNull()
        assertThat(note?.updatedBy).isEqualTo("Updated Author")
        assertThat(note?.version).isEqualTo(1)
      }
      .verifyComplete()
  }

  @Test
  fun `should return not found when updating non-existent published note`() {
    val request = HearingCaseNoteRequest(
      note = "Updated note",
      createdByUUID = null,
      author = "Test Author",
    )

    webTestClient.put()
      .uri("/hearing/$hearingId/defendants/$defendantId/notes/$noteId")
      .headers { it.setBearerAuth(generateMockJwt()) }
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(request)
      .exchange()
      .expectStatus().isNotFound
  }

  @Test
  fun `should delete hearing case note successfully`() {
    val existingNoteId = generateUUID()
    val hearing = ApiTestDataBuilder.buildHearingWithPublishedNote(
      id = hearingId,
      hearingId = hearingId,
      defendantId = defendantId,
      noteId = existingNoteId,
      note = "Published note to delete",
      author = "Test Author",
      createdByUuid = userUuid,
    )

    StepVerifier.create(hearingRepository.save(hearing))
      .expectNextCount(1)
      .verifyComplete()

    webTestClient.delete()
      .uri("/hearing/$hearingId/defendants/$defendantId/notes/$existingNoteId")
      .headers { it.setBearerAuth(generateMockJwt()) }
      .exchange()
      .expectStatus().isOk

    StepVerifier.create(
      defendantHearingRepository.findByDefendantIdAndHearingId(defendantId, hearingId),
    )
      .assertNext { updatedHearing ->
        val note = updatedHearing.hearingCaseNote?.first()
        assertThat(note?.isSoftDeleted).isTrue()
      }
      .verifyComplete()
  }

  @Test
  fun `should return not found when deleting non-existent published note`() {
    webTestClient.delete()
      .uri("/hearing/$hearingId/defendants/$defendantId/notes/$noteId")
      .headers { it.setBearerAuth(generateMockJwt()) }
      .exchange()
      .expectStatus().isNotFound
  }

  @Test
  fun `should return not found when hearing does not exist`() {
    val nonExistentHearingId = generateUUID()
    val request = HearingCaseNoteRequest(
      note = "Test note",
      createdByUUID = null,
      author = "Test Author",
    )

    webTestClient.post()
      .uri("/hearing/$nonExistentHearingId/defendant/$defendantId/note")
      .headers { it.setBearerAuth(generateMockJwt()) }
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(request)
      .exchange()
      .expectStatus().is5xxServerError
  }

  private fun generateMockJwt(): String = "Bearer mock-jwt-token"
}
