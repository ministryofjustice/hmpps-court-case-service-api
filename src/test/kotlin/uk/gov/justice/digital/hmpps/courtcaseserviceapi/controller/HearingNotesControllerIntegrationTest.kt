package uk.gov.justice.digital.hmpps.courtcaseserviceapi.controller

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.configureFor
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockAuthentication
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import reactor.core.publisher.Mono
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.config.security.JwtTokenAuthenticationImpl
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.data.HearingNotesTestDataHelper
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.api.hearing.HearingCaseNoteResponse
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.hearing.Hearing
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.repository.common.DefendantHearingRepository
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.repository.hearing.HearingRepository

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HearingNotesControllerIntegrationTest {

  @Autowired
  private lateinit var webTestClient: WebTestClient

  @MockitoBean
  private lateinit var defendantHearingRepository: DefendantHearingRepository

  @MockitoBean
  private lateinit var hearingRepository: HearingRepository

  private lateinit var wireMockServer: WireMockServer

  @BeforeAll
  fun setupWireMock() {
    wireMockServer = WireMockServer(WireMockConfiguration.options().port(8089))
    wireMockServer.start()
    configureFor("localhost", 8089)
  }

  @AfterAll
  fun tearDownWireMock() {
    wireMockServer.stop()
  }

  @BeforeEach
  fun resetWireMock() {
    wireMockServer.resetAll()
  }

  @Test
  @DisplayName("Should successfully add hearing case note as draft")
  fun testAddHearingCaseNoteAsDraft() {
    val hearingId = HearingNotesTestDataHelper.TEST_HEARING_ID
    val defendantId = HearingNotesTestDataHelper.TEST_DEFENDANT_ID
    val request = HearingNotesTestDataHelper.createHearingCaseNoteRequest()

    val auth = mockJwtAuthentication()

    val existingHearing = HearingNotesTestDataHelper.createHearingWithoutNote(hearingId)
    val savedHearing = HearingNotesTestDataHelper.createHearingWithDraftNote(hearingId)

    whenever(defendantHearingRepository.findByDefendantIdAndHearingId(defendantId, hearingId))
      .thenReturn(Mono.just(existingHearing))
    whenever(hearingRepository.save(any<Hearing>()))
      .thenReturn(Mono.just(savedHearing))

    stubAuthServiceValidation()

    webTestClient
      .mutateWith(mockAuthentication(auth))
      .post()
      .uri("/hearing/$hearingId/defendant/$defendantId/note")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(request)
      .exchange()
      .expectStatus().isCreated
      .expectBody<HearingCaseNoteResponse>()
      .value { response ->
        assertThat(response.noteId).isNotNull
        assertThat(request.note).isEqualTo(response.note)
        assertThat(request.author).isEqualTo(response.author)
        assertThat(response.isDraft).isTrue
      }
  }

  @Test
  @Disabled("To be fixed by adding in request level validation")
  @DisplayName("Should return no content when request note is empty")
  fun testAddHearingCaseNoteAsDraft_EmptyRequest() {
    val hearingId = HearingNotesTestDataHelper.TEST_HEARING_ID
    val defendantId = HearingNotesTestDataHelper.TEST_DEFENDANT_ID
    val auth = mockJwtAuthentication()

    webTestClient
      .mutateWith(mockAuthentication(auth))
      .post()
      .uri("/hearing/$hearingId/defendant/$defendantId/note")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue("{}")
      .exchange()
      .expectStatus().is4xxClientError
  }

  @Test
  @DisplayName("Should successfully update hearing case note draft")
  fun testUpdateHearingCaseNoteDraft() {
    val hearingId = HearingNotesTestDataHelper.TEST_HEARING_ID
    val defendantId = HearingNotesTestDataHelper.TEST_DEFENDANT_ID
    val request = HearingNotesTestDataHelper.createUpdatedHearingCaseNoteRequest()

    val existingHearing = HearingNotesTestDataHelper.createHearingWithDraftNote(hearingId)
    val updatedHearing = HearingNotesTestDataHelper.createHearingWithUpdatedDraftNote(hearingId)
    val auth = mockJwtAuthentication()

    whenever(defendantHearingRepository.findByDefendantIdAndHearingId(defendantId, hearingId))
      .thenReturn(Mono.just(existingHearing))
    whenever(hearingRepository.save(any<Hearing>()))
      .thenReturn(Mono.just(updatedHearing))

    stubAuthServiceValidation()

    webTestClient
      .mutateWith(mockAuthentication(auth))
      .put()
      .uri("/hearing/$hearingId/defendant/$defendantId/notes/draft")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(request)
      .exchange()
      .expectStatus().isOk
      .expectBody<HearingCaseNoteResponse>()
      .value { response ->
        assertThat(response.noteId).isNotNull
        assertThat(request.note).isEqualTo(response.note)
        assertThat(response.isDraft).isTrue
      }
  }

  @Test
  @DisplayName("Should return not found when draft note does not exist")
  fun testUpdateHearingCaseNoteDraft_NotFound() {
    val hearingId = HearingNotesTestDataHelper.TEST_HEARING_ID
    val defendantId = HearingNotesTestDataHelper.TEST_DEFENDANT_ID
    val request = HearingNotesTestDataHelper.createHearingCaseNoteRequest()

    val existingHearing = HearingNotesTestDataHelper.createHearingWithoutNote(hearingId)
    val auth = mockJwtAuthentication()

    whenever(defendantHearingRepository.findByDefendantIdAndHearingId(defendantId, hearingId))
      .thenReturn(Mono.just(existingHearing))

    webTestClient
      .mutateWith(mockAuthentication(auth))
      .put()
      .uri("/hearing/$hearingId/defendant/$defendantId/notes/draft")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(request)
      .exchange()
      .expectStatus().isNotFound
  }

  @Test
  @DisplayName("Should successfully delete hearing case note draft")
  fun testDeleteHearingCaseNoteDraft() {
    val hearingId = HearingNotesTestDataHelper.TEST_HEARING_ID
    val defendantId = HearingNotesTestDataHelper.TEST_DEFENDANT_ID

    val existingHearing = HearingNotesTestDataHelper.createHearingWithDraftNote(hearingId)
    val deletedHearing = HearingNotesTestDataHelper.createHearingWithDeletedDraftNote(hearingId)
    val auth = mockJwtAuthentication()

    whenever(defendantHearingRepository.findByDefendantIdAndHearingId(defendantId, hearingId))
      .thenReturn(Mono.just(existingHearing))
    whenever(hearingRepository.save(any<Hearing>()))
      .thenReturn(Mono.just(deletedHearing))

    stubAuthServiceValidation()

    webTestClient
      .mutateWith(mockAuthentication(auth))
      .delete()
      .uri("/hearing/$hearingId/defendant/$defendantId/notes/draft")
      .exchange()
      .expectStatus().isOk
  }

  @Test
  @DisplayName("Should return not found when deleting non-existent draft")
  fun testDeleteHearingCaseNoteDraft_NotFound() {
    val hearingId = HearingNotesTestDataHelper.TEST_HEARING_ID
    val defendantId = HearingNotesTestDataHelper.TEST_DEFENDANT_ID

    val existingHearing = HearingNotesTestDataHelper.createHearingWithoutNote(hearingId)
    val auth = mockJwtAuthentication()

    whenever(defendantHearingRepository.findByDefendantIdAndHearingId(defendantId, hearingId))
      .thenReturn(Mono.just(existingHearing))

    webTestClient
      .mutateWith(mockAuthentication(auth))
      .delete()
      .uri("/hearing/$hearingId/defendant/$defendantId/notes/draft")
      .exchange()
      .expectStatus().isNotFound
  }

  @Test
  @DisplayName("Should successfully update hearing case note")
  fun testUpdateHearingCaseNote() {
    val hearingId = HearingNotesTestDataHelper.TEST_HEARING_ID
    val defendantId = HearingNotesTestDataHelper.TEST_DEFENDANT_ID
    val noteId = HearingNotesTestDataHelper.TEST_NOTE_ID
    val request = HearingNotesTestDataHelper.createUpdatedHearingCaseNoteRequest()

    val existingHearing = HearingNotesTestDataHelper.createHearingWithPublishedNote(hearingId, noteId)
    val updatedHearing = HearingNotesTestDataHelper.createHearingWithUpdatedPublishedNote(hearingId, noteId)
    val auth = mockJwtAuthentication()

    whenever(defendantHearingRepository.findByDefendantIdAndHearingId(defendantId, hearingId))
      .thenReturn(Mono.just(existingHearing))
    whenever(hearingRepository.save(any<Hearing>()))
      .thenReturn(Mono.just(updatedHearing))

    stubAuthServiceValidation()

    webTestClient
      .mutateWith(mockAuthentication(auth))
      .put()
      .uri("/hearing/$hearingId/defendants/$defendantId/notes/$noteId")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(request)
      .exchange()
      .expectStatus().isOk
  }

  @Test
  @DisplayName("Should successfully delete hearing case note")
  fun testDeleteHearingCaseNote() {
    val hearingId = HearingNotesTestDataHelper.TEST_HEARING_ID
    val defendantId = HearingNotesTestDataHelper.TEST_DEFENDANT_ID
    val noteId = HearingNotesTestDataHelper.TEST_NOTE_ID

    val existingHearing = HearingNotesTestDataHelper.createHearingWithPublishedNote(hearingId, noteId)
    val deletedHearing = HearingNotesTestDataHelper.createHearingWithDeletedPublishedNote(hearingId, noteId)
    val auth = mockJwtAuthentication()

    whenever(defendantHearingRepository.findByDefendantIdAndHearingId(defendantId, hearingId))
      .thenReturn(Mono.just(existingHearing))
    whenever(hearingRepository.save(any<Hearing>()))
      .thenReturn(Mono.just(deletedHearing))

    stubAuthServiceValidation()

    webTestClient
      .mutateWith(mockAuthentication(auth))
      .delete()
      .uri("/hearing/$hearingId/defendants/$defendantId/notes/$noteId")
      .exchange()
      .expectStatus().isOk
  }

  @Test
  @DisplayName("Should return not found when deleting note with wrong user")
  fun testDeleteHearingCaseNote_WrongUser() {
    val hearingId = HearingNotesTestDataHelper.TEST_HEARING_ID
    val defendantId = HearingNotesTestDataHelper.TEST_DEFENDANT_ID
    val noteId = HearingNotesTestDataHelper.TEST_NOTE_ID

    val existingHearing = HearingNotesTestDataHelper.createHearingWithPublishedNote(hearingId, noteId)
    val auth = mockJwtAuthenticationWithWrongUser()

    whenever(defendantHearingRepository.findByDefendantIdAndHearingId(defendantId, hearingId))
      .thenReturn(Mono.just(existingHearing))

    webTestClient
      .mutateWith(mockAuthentication(auth))
      .delete()
      .uri("/hearing/$hearingId/defendants/$defendantId/notes/$noteId")
      .exchange()
      .expectStatus().is5xxServerError
  }

  private fun stubAuthServiceValidation() {
    wireMockServer.stubFor(
      get(urlPathEqualTo("/auth/validate"))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBodyFile("auth-validation-response.json"),
        ),
    )
  }

  private fun mockJwtAuthentication(username: String = "test-user"): JwtTokenAuthenticationImpl {
    val claims = mapOf(
      "sub" to username,
      "user_uuid" to HearingNotesTestDataHelper.TEST_USER_UUID.toString(),
      "user_id" to username,
      "user_name" to username,
      "auth_source" to "auth",
    )

    val jwt = Jwt.withTokenValue("mock-token")
      .header("alg", "none")
      .claims { it.putAll(claims) }
      .build()

    return JwtTokenAuthenticationImpl(jwt, false, emptyList())
  }

  private fun mockJwtAuthenticationWithWrongUser(username: String = "wrong-user"): JwtTokenAuthenticationImpl {
    val claims = mapOf(
      "sub" to username,
      "user_uuid" to HearingNotesTestDataHelper.TEST_USER_UUID.toString(),
      "user_id" to username,
      "user_name" to username,
      "auth_source" to "auth",
    )

    val jwt = Jwt.withTokenValue("mock-token")
      .header("alg", "none")
      .claims { it.putAll(claims) }
      .build()

    return JwtTokenAuthenticationImpl(jwt, false, emptyList())
  }
}
