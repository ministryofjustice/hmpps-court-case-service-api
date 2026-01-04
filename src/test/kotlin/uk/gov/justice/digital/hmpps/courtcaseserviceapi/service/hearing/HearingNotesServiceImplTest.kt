package uk.gov.justice.digital.hmpps.courtcaseserviceapi.service.hearing

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.data.TestDataBuilder
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.hearing.Hearing
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.exceptions.HearingCaseNoteDraftNotFoundException
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.exceptions.HearingCaseNoteNotFoundException
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.exceptions.HearingNotFoundException
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.repository.common.DefendantHearingRepository
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.repository.hearing.HearingRepository
import kotlin.jvm.java

class HearingNotesServiceImplTest {

  private lateinit var hearingRepository: HearingRepository
  private lateinit var defendantHearingRepository: DefendantHearingRepository
  private lateinit var hearingNotesService: HearingNotesServiceImpl

  @BeforeEach
  fun setUp() {
    hearingRepository = mockk()
    defendantHearingRepository = mockk()
    hearingNotesService = HearingNotesServiceImpl(hearingRepository)
  }

  @AfterEach
  fun tearDown() {
    clearAllMocks()
  }

  @Test
  fun `addHearingCaseNoteAsDraft should create new draft note when no existing draft`() {
    val hearingId = TestDataBuilder.generateUUID()
    val defendantId = TestDataBuilder.generateUUID()
    val request = TestDataBuilder.buildHearingCaseNoteRequest()
    val hearing = TestDataBuilder.buildHearing(hearingId = hearingId, hearingCaseNote = null)
    val savedHearing = hearing.copy(hearingCaseNote = listOf(TestDataBuilder.buildHearingCaseNote(defendantId = defendantId)))

    every { hearingRepository.findByDefendantIdAndHearingId(defendantId, hearingId) } returns Mono.just(hearing)
    every { hearingRepository.save(any<Hearing>()) } returns Mono.just(savedHearing)

    StepVerifier.create(hearingNotesService.addHearingCaseNoteAsDraft(hearingId, defendantId, request))
      .expectNextMatches { response ->
        response.noteId != null &&
          response.note == request.note &&
          response.author == request.author &&
          response.createdByUuid == request.createdByUUID &&
          response.isDraft == true
      }
      .verifyComplete()

    verify(exactly = 1) { hearingRepository.findByDefendantIdAndHearingId(defendantId, hearingId) }
    verify(exactly = 1) { hearingRepository.save(any<Hearing>()) }
  }

  @Test
  fun `addHearingCaseNoteAsDraft should return existing draft when found`() {
    val hearingId = TestDataBuilder.generateUUID()
    val defendantId = TestDataBuilder.generateUUID()
    val request = TestDataBuilder.buildHearingCaseNoteRequest()
    val existingDraftNote = TestDataBuilder.buildHearingCaseNote(
      defendantId = defendantId,
      createdByUUID = request.createdByUUID,
      isDraft = true,
    )
    val hearing = TestDataBuilder.buildHearing(hearingId = hearingId, hearingCaseNote = listOf(existingDraftNote))

    every { hearingRepository.findByDefendantIdAndHearingId(defendantId, hearingId) } returns Mono.just(hearing)

    StepVerifier.create(hearingNotesService.addHearingCaseNoteAsDraft(hearingId, defendantId, request))
      .expectNextMatches { response ->
        response.noteId == existingDraftNote.id &&
          response.isDraft == true
      }
      .verifyComplete()

    verify(exactly = 1) { hearingRepository.findByDefendantIdAndHearingId(defendantId, hearingId) }
    verify(exactly = 0) { hearingRepository.save(any<Hearing>()) }
  }

  @Test
  fun `addHearingCaseNoteAsDraft should throw HearingNotFoundException when hearing not found`() {
    val hearingId = TestDataBuilder.generateUUID()
    val defendantId = TestDataBuilder.generateUUID()
    val request = TestDataBuilder.buildHearingCaseNoteRequest()

    every { hearingRepository.findByDefendantIdAndHearingId(defendantId, hearingId) } returns Mono.empty()

    StepVerifier.create(hearingNotesService.addHearingCaseNoteAsDraft(hearingId, defendantId, request))
      .expectError(HearingNotFoundException::class.java)
      .verify()

    verify(exactly = 1) { hearingRepository.findByDefendantIdAndHearingId(defendantId, hearingId) }
  }

  @Test
  fun `updateHearingCaseNoteDraft should update existing draft note`() {
    val hearingId = TestDataBuilder.generateUUID()
    val defendantId = TestDataBuilder.generateUUID()
    val request = TestDataBuilder.buildHearingCaseNoteRequest(note = "Updated note")
    val existingDraftNote = TestDataBuilder.buildHearingCaseNote(
      defendantId = defendantId,
      createdByUUID = request.createdByUUID,
      isDraft = true,
    )
    val hearing = TestDataBuilder.buildHearing(hearingId = hearingId, hearingCaseNote = mutableListOf(existingDraftNote))
    val savedHearing = hearing.copy()

    every { hearingRepository.findByDefendantIdAndHearingId(defendantId, hearingId) } returns Mono.just(hearing)
    every { hearingRepository.save(any<Hearing>()) } returns Mono.just(savedHearing)

    StepVerifier.create(hearingNotesService.updateHearingCaseNoteDraft(hearingId, defendantId, request))
      .expectNextMatches { response ->
        response.note == request.note &&
          response.author == request.author
      }
      .verifyComplete()

    verify(exactly = 1) { hearingRepository.findByDefendantIdAndHearingId(defendantId, hearingId) }
    verify(exactly = 1) { hearingRepository.save(any<Hearing>()) }
  }

  @Test
  fun `updateHearingCaseNoteDraft should throw HearingCaseNoteDraftNotFoundException when draft not found`() {
    val hearingId = TestDataBuilder.generateUUID()
    val defendantId = TestDataBuilder.generateUUID()
    val request = TestDataBuilder.buildHearingCaseNoteRequest()
    val nonDraftNote = TestDataBuilder.buildHearingCaseNote(
      defendantId = defendantId,
      createdByUUID = request.createdByUUID,
      isDraft = false,
    )
    val hearing = TestDataBuilder.buildHearing(hearingId = hearingId, hearingCaseNote = listOf(nonDraftNote))

    every { hearingRepository.findByDefendantIdAndHearingId(defendantId, hearingId) } returns Mono.just(hearing)

    StepVerifier.create(hearingNotesService.updateHearingCaseNoteDraft(hearingId, defendantId, request))
      .expectError(HearingCaseNoteDraftNotFoundException::class.java)
      .verify()

    verify(exactly = 1) { hearingRepository.findByDefendantIdAndHearingId(defendantId, hearingId) }
  }

  @Test
  fun `deleteHearingCaseNoteDraft should soft delete draft note`() {
    val hearingId = TestDataBuilder.generateUUID()
    val defendantId = TestDataBuilder.generateUUID()
    val userUUID = TestDataBuilder.generateUUID()
    val existingDraftNote = TestDataBuilder.buildHearingCaseNote(
      defendantId = defendantId,
      createdByUUID = userUUID,
      isDraft = true,
      isSoftDeleted = false,
    )
    val hearing = TestDataBuilder.buildHearing(hearingId = hearingId, hearingCaseNote = mutableListOf(existingDraftNote))

    every { hearingRepository.findByDefendantIdAndHearingId(defendantId, hearingId) } returns Mono.just(hearing)
    every { hearingRepository.save(any<Hearing>()) } returns Mono.just(hearing)

    StepVerifier.create(hearingNotesService.deleteHearingCaseNoteDraft(hearingId, defendantId, userUUID))
      .expectNext(true)
      .verifyComplete()

    verify(exactly = 1) { hearingRepository.findByDefendantIdAndHearingId(defendantId, hearingId) }
    verify(exactly = 1) { hearingRepository.save(any<Hearing>()) }
  }

  @Test
  fun `deleteHearingCaseNoteDraft should throw HearingCaseNoteDraftNotFoundException when draft not found`() {
    val hearingId = TestDataBuilder.generateUUID()
    val defendantId = TestDataBuilder.generateUUID()
    val userUUID = TestDataBuilder.generateUUID()
    val hearing = TestDataBuilder.buildHearing(hearingId = hearingId, hearingCaseNote = null)

    every { hearingRepository.findByDefendantIdAndHearingId(defendantId, hearingId) } returns Mono.just(hearing)

    StepVerifier.create(hearingNotesService.deleteHearingCaseNoteDraft(hearingId, defendantId, userUUID))
      .expectError(HearingCaseNoteDraftNotFoundException::class.java)
      .verify()

    verify(exactly = 1) { hearingRepository.findByDefendantIdAndHearingId(defendantId, hearingId) }
  }

  @Test
  fun `updateHearingCaseNote should update non-draft note successfully`() {
    val hearingId = TestDataBuilder.generateUUID()
    val defendantId = TestDataBuilder.generateUUID()
    val noteId = TestDataBuilder.generateUUID()
    val request = TestDataBuilder.buildHearingCaseNoteRequest(note = "Updated note")
    val existingNote = TestDataBuilder.buildHearingCaseNote(
      id = noteId,
      defendantId = defendantId,
      createdByUUID = request.createdByUUID,
      isDraft = false,
    )
    val hearing = TestDataBuilder.buildHearing(hearingId = hearingId, hearingCaseNote = mutableListOf(existingNote))

    every { hearingRepository.findByDefendantIdAndHearingId(defendantId, hearingId) } returns Mono.just(hearing)
    every { hearingRepository.save(any<Hearing>()) } returns Mono.just(hearing)

    StepVerifier.create(hearingNotesService.updateHearingCaseNote(hearingId, defendantId, noteId, request))
      .expectNext(true)
      .verifyComplete()

    verify(exactly = 1) { hearingRepository.findByDefendantIdAndHearingId(defendantId, hearingId) }
    verify(exactly = 1) { hearingRepository.save(any<Hearing>()) }
  }

  @Test
  fun `updateHearingCaseNote should throw HearingCaseNoteNotFoundException when note not found`() {
    val hearingId = TestDataBuilder.generateUUID()
    val defendantId = TestDataBuilder.generateUUID()
    val noteId = TestDataBuilder.generateUUID()
    val request = TestDataBuilder.buildHearingCaseNoteRequest()
    val hearing = TestDataBuilder.buildHearing(hearingId = hearingId, hearingCaseNote = null)

    every { hearingRepository.findByDefendantIdAndHearingId(defendantId, hearingId) } returns Mono.just(hearing)

    StepVerifier.create(hearingNotesService.updateHearingCaseNote(hearingId, defendantId, noteId, request))
      .expectError(HearingCaseNoteNotFoundException::class.java)
      .verify()

    verify(exactly = 1) { hearingRepository.findByDefendantIdAndHearingId(defendantId, hearingId) }
  }

  @Test
  fun `deleteHearingCaseNote should soft delete non-draft note`() {
    val hearingId = TestDataBuilder.generateUUID()
    val defendantId = TestDataBuilder.generateUUID()
    val noteId = TestDataBuilder.generateUUID()
    val userUUID = TestDataBuilder.generateUUID()
    val existingNote = TestDataBuilder.buildHearingCaseNote(
      id = noteId,
      defendantId = defendantId,
      createdByUUID = userUUID,
      isDraft = false,
      isSoftDeleted = false,
    )
    val hearing = TestDataBuilder.buildHearing(hearingId = hearingId, hearingCaseNote = mutableListOf(existingNote))

    every { hearingRepository.findByDefendantIdAndHearingId(defendantId, hearingId) } returns Mono.just(hearing)
    every { hearingRepository.save(any<Hearing>()) } returns Mono.just(hearing)

    StepVerifier.create(hearingNotesService.deleteHearingCaseNote(hearingId, defendantId, noteId, userUUID))
      .expectNext(true)
      .verifyComplete()

    verify(exactly = 1) { hearingRepository.findByDefendantIdAndHearingId(defendantId, hearingId) }
    verify(exactly = 1) { hearingRepository.save(any<Hearing>()) }
  }

  @Test
  fun `deleteHearingCaseNote should throw HearingCaseNoteNotFoundException when note not found`() {
    val hearingId = TestDataBuilder.generateUUID()
    val defendantId = TestDataBuilder.generateUUID()
    val noteId = TestDataBuilder.generateUUID()
    val userUUID = TestDataBuilder.generateUUID()
    val hearing = TestDataBuilder.buildHearing(hearingId = hearingId, hearingCaseNote = null)

    every { hearingRepository.findByDefendantIdAndHearingId(defendantId, hearingId) } returns Mono.just(hearing)

    StepVerifier.create(hearingNotesService.deleteHearingCaseNote(hearingId, defendantId, noteId, userUUID))
      .expectError(HearingCaseNoteNotFoundException::class.java)
      .verify()

    verify(exactly = 1) { hearingRepository.findByDefendantIdAndHearingId(defendantId, hearingId) }
  }
}
