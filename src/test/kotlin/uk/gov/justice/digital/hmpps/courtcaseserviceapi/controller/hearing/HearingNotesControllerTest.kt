package uk.gov.justice.digital.hmpps.courtcaseserviceapi.controller.hearing

import com.fasterxml.uuid.Generators
import org.assertj.core.api.AssertionsForClassTypes
import org.assertj.core.api.AssertionsForInterfaceTypes
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import reactor.core.publisher.Mono
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.config.security.ApplicationSecurityConfiguration
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.config.security.AuthenticationExtractor
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.api.hearing.HearingCaseNoteRequest
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.api.hearing.HearingCaseNoteResponse
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.service.hearing.HearingNotesService
import java.security.Principal
import java.time.OffsetDateTime
import java.util.UUID

@ActiveProfiles("test", "secured")
@WebFluxTest(HearingNotesController::class)
@Import(ApplicationSecurityConfiguration::class)
class HearingNotesControllerTest {

  @Autowired
  private lateinit var webTestClient: WebTestClient

  @MockitoBean
  private lateinit var hearingNotesService: HearingNotesService

  @MockitoBean
  private lateinit var authenticationExtractor: AuthenticationExtractor

  private lateinit var hearingId: UUID
  private lateinit var defendantId: UUID
  private lateinit var noteId: UUID
  private lateinit var userUuid: UUID
  private lateinit var authUserUuid: String
  private lateinit var request: HearingCaseNoteRequest
  private lateinit var response: HearingCaseNoteResponse

  @BeforeEach
  fun setUp() {
    hearingId = Generators.timeBasedEpochRandomGenerator().generate()
    defendantId = Generators.timeBasedEpochRandomGenerator().generate()
    noteId = Generators.timeBasedEpochRandomGenerator().generate()
    userUuid = Generators.timeBasedEpochRandomGenerator().generate()
    authUserUuid = userUuid.toString()

    request = HearingCaseNoteRequest("Test note", null, "Important Person")

    response = HearingCaseNoteResponse(
      noteId,
      note = "Test note",
      createdAt = OffsetDateTime.now(),
      author = "Important Person",
      createdByUuid = userUuid,
      isDraft = true,
      isLegacy = false,
    )
    whenever(authenticationExtractor.extractAuthUserUuid(any<Principal>()))
      .thenReturn(authUserUuid)
  }

  @Test
  fun addHearingCaseNoteAsDraftToHearing_shouldReturnCreatedWithResponse() {
    whenever(
      hearingNotesService.addHearingCaseNoteAsDraft(
        eq(hearingId),
        eq(defendantId),
        any<HearingCaseNoteRequest>(),
      ),
    )
      .thenReturn(Mono.just(response))

    webTestClient
      .mutateWith(SecurityMockServerConfigurers.mockJwt().authorities(SimpleGrantedAuthority("ROLE_PREPARE_A_CASE")))
      .post()
      .uri("/hearing/{hearingId}/defendant/{defendantId}/note", hearingId, defendantId)
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(request)
      .exchange()
      .expectStatus().isCreated()
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody<HearingCaseNoteResponse>()
      .value { resp ->
        AssertionsForInterfaceTypes.assertThat(resp.noteId).isEqualTo(noteId)
        AssertionsForClassTypes.assertThat(resp.note).isEqualTo("Test note")
        AssertionsForClassTypes.assertThat(resp.author).isEqualTo("Important Person")
        AssertionsForInterfaceTypes.assertThat(resp.createdByUuid).isEqualTo(userUuid)
      }

    verify(authenticationExtractor).extractAuthUserUuid(any<Principal>())
    verify(hearingNotesService).addHearingCaseNoteAsDraft(
      eq(hearingId),
      eq(defendantId),
      any<HearingCaseNoteRequest>(),
    )
  }

  @Test
  fun addHearingCaseNoteAsDraftToHearing_shouldReturnBadRequestWhenEmpty() {
    whenever(
      hearingNotesService.addHearingCaseNoteAsDraft(
        eq(hearingId),
        eq(defendantId),
        any<HearingCaseNoteRequest>(),
      ),
    )
      .thenReturn(Mono.empty())

    webTestClient
      .mutateWith(SecurityMockServerConfigurers.mockJwt().authorities(SimpleGrantedAuthority("ROLE_PREPARE_A_CASE")))
      .post()
      .uri("/hearing/{hearingId}/defendant/{defendantId}/note", hearingId, defendantId)
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(request)
      .exchange()
      .expectStatus().isBadRequest
      .expectBody().isEmpty

    verify(hearingNotesService).addHearingCaseNoteAsDraft(
      eq(hearingId),
      eq(defendantId),
      any<HearingCaseNoteRequest>(),
    )
  }

  @Test
  fun updateHearingCaseNoteDraftToHearing_shouldReturnOkWithResponse() {
    whenever(
      hearingNotesService.updateHearingCaseNoteDraft(
        eq(hearingId),
        eq(defendantId),
        any<HearingCaseNoteRequest>(),
      ),
    )
      .thenReturn(Mono.just(response))

    webTestClient
      .mutateWith(SecurityMockServerConfigurers.mockJwt().authorities(SimpleGrantedAuthority("ROLE_PREPARE_A_CASE")))
      .put()
      .uri("/hearing/{hearingId}/defendant/{defendantId}/notes/draft", hearingId, defendantId)
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(request)
      .exchange()
      .expectStatus().isOk()
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody<HearingCaseNoteResponse>()
      .value { resp ->
        AssertionsForInterfaceTypes.assertThat(resp.noteId).isEqualTo(noteId)
        AssertionsForClassTypes.assertThat(resp.note).isEqualTo("Test note")
      }

    verify(hearingNotesService).updateHearingCaseNoteDraft(
      eq(hearingId),
      eq(defendantId),
      any<HearingCaseNoteRequest>(),
    )
  }

  @Test
  fun updateHearingCaseNoteDraftToHearing_shouldReturnNotFoundWhenEmpty() {
    whenever(
      hearingNotesService.updateHearingCaseNoteDraft(
        eq(hearingId),
        eq(defendantId),
        any<HearingCaseNoteRequest>(),
      ),
    )
      .thenReturn(Mono.empty())

    webTestClient
      .mutateWith(SecurityMockServerConfigurers.mockJwt().authorities(SimpleGrantedAuthority("ROLE_PREPARE_A_CASE")))
      .put()
      .uri("/hearing/{hearingId}/defendant/{defendantId}/notes/draft", hearingId, defendantId)
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(request)
      .exchange()
      .expectStatus().isNotFound()
      .expectBody().isEmpty

    verify(hearingNotesService).updateHearingCaseNoteDraft(
      eq(hearingId),
      eq(defendantId),
      any<HearingCaseNoteRequest>(),
    )
  }

  @Test
  fun deleteHearingCaseNoteDraftFromHearing_shouldReturnOkWhenSuccessful() {
    whenever(
      hearingNotesService.deleteHearingCaseNoteDraft(
        eq(hearingId),
        eq(defendantId),
        eq(userUuid),
      ),
    )
      .thenReturn(Mono.just(true))

    webTestClient
      .mutateWith(SecurityMockServerConfigurers.mockJwt().authorities(SimpleGrantedAuthority("ROLE_PREPARE_A_CASE")))
      .delete()
      .uri("/hearing/{hearingId}/defendant/{defendantId}/notes/draft", hearingId, defendantId)
      .exchange()
      .expectStatus().isOk()
      .expectBody().isEmpty

    verify(hearingNotesService).deleteHearingCaseNoteDraft(hearingId, defendantId, userUuid)
  }

  @Test
  fun deleteHearingCaseNoteDraftFromHearing_shouldReturnNotFoundWhenNotSuccessful() {
    whenever(
      hearingNotesService.deleteHearingCaseNoteDraft(
        eq(hearingId),
        eq(defendantId),
        eq(userUuid),
      ),
    )
      .thenReturn(Mono.just(false))

    webTestClient
      .mutateWith(SecurityMockServerConfigurers.mockJwt().authorities(SimpleGrantedAuthority("ROLE_PREPARE_A_CASE")))
      .delete()
      .uri("/hearing/{hearingId}/defendant/{defendantId}/notes/draft", hearingId, defendantId)
      .exchange()
      .expectStatus().isNotFound()
      .expectBody().isEmpty

    verify(hearingNotesService).deleteHearingCaseNoteDraft(hearingId, defendantId, userUuid)
  }

  @Test
  fun updateHearingCaseNoteToHearing_shouldReturnOkWhenSuccessful() {
    whenever(
      hearingNotesService.updateHearingCaseNote(
        eq(hearingId),
        eq(defendantId),
        eq(noteId),
        any(),
      ),
    ).thenReturn(Mono.just(true))

    webTestClient
      .mutateWith(SecurityMockServerConfigurers.mockJwt().authorities(SimpleGrantedAuthority("ROLE_PREPARE_A_CASE")))
      .put()
      .uri(
        "/hearing/{hearingId}/defendants/{defendantId}/notes/{noteId}",
        hearingId,
        defendantId,
        noteId,
      )
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(request)
      .exchange()
      .expectStatus().isOk()
      .expectBody().isEmpty

    verify(hearingNotesService).updateHearingCaseNote(
      eq(hearingId),
      eq(defendantId),
      eq(noteId),
      any(),
    )
  }

  @Test
  fun updateHearingCaseNoteToHearing_shouldReturnNotFoundWhenNotSuccessful() {
    whenever(
      hearingNotesService.updateHearingCaseNote(
        eq(hearingId),
        eq(defendantId),
        eq(noteId),
        any<HearingCaseNoteRequest>(),
      ),
    )
      .thenReturn(Mono.just(false))

    webTestClient
      .mutateWith(SecurityMockServerConfigurers.mockJwt().authorities(SimpleGrantedAuthority("ROLE_PREPARE_A_CASE")))
      .put()
      .uri("/hearing/{hearingId}/defendants/{defendantId}/notes/{noteId}", hearingId, defendantId, noteId)
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(request)
      .exchange()
      .expectStatus().isNotFound()
      .expectBody().isEmpty

    verify(hearingNotesService).updateHearingCaseNote(
      eq(hearingId),
      eq(defendantId),
      eq(noteId),
      any<HearingCaseNoteRequest>(),
    )
  }

  @Test
  fun deleteHearingCaseNoteFromHearing_shouldReturnOkWhenSuccessful() {
    whenever(hearingNotesService.deleteHearingCaseNote(hearingId, defendantId, noteId, userUuid))
      .thenReturn(Mono.just(true))

    webTestClient
      .mutateWith(SecurityMockServerConfigurers.mockJwt().authorities(SimpleGrantedAuthority("ROLE_PREPARE_A_CASE")))
      .delete()
      .uri("/hearing/{hearingId}/defendants/{defendantId}/notes/{noteId}", hearingId, defendantId, noteId)
      .exchange()
      .expectStatus().isOk()
      .expectBody().isEmpty

    verify(hearingNotesService).deleteHearingCaseNote(hearingId, defendantId, noteId, userUuid)
  }

  @Test
  fun deleteHearingCaseNoteFromHearing_shouldReturnNotFoundWhenNotSuccessful() {
    whenever(hearingNotesService.deleteHearingCaseNote(hearingId, defendantId, noteId, userUuid))
      .thenReturn(Mono.just(false))

    webTestClient
      .mutateWith(SecurityMockServerConfigurers.mockJwt().authorities(SimpleGrantedAuthority("ROLE_PREPARE_A_CASE")))
      .delete()
      .uri("/hearing/{hearingId}/defendants/{defendantId}/notes/{noteId}", hearingId, defendantId, noteId)
      .exchange()
      .expectStatus().isNotFound()
      .expectBody().isEmpty

    verify(hearingNotesService).deleteHearingCaseNote(hearingId, defendantId, noteId, userUuid)
  }

  @Test
  fun addHearingCaseNoteAsDraftToHearing_shouldSetCreatedByUUID() {
    whenever(
      hearingNotesService.addHearingCaseNoteAsDraft(
        eq(hearingId),
        eq(defendantId),
        any<HearingCaseNoteRequest>(),
      ),
    )
      .thenReturn(Mono.just(response))

    val requestCaptor = argumentCaptor<HearingCaseNoteRequest>()

    webTestClient
      .mutateWith(SecurityMockServerConfigurers.mockJwt().authorities(SimpleGrantedAuthority("ROLE_PREPARE_A_CASE")))
      .post()
      .uri("/hearing/{hearingId}/defendant/{defendantId}/note", hearingId, defendantId)
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(request)
      .exchange()
      .expectStatus().isCreated()

    verify(hearingNotesService).addHearingCaseNoteAsDraft(
      eq(hearingId),
      eq(defendantId),
      requestCaptor.capture(),
    )
    AssertionsForInterfaceTypes.assertThat(requestCaptor.firstValue.createdByUUID).isEqualTo(userUuid)
  }
}
