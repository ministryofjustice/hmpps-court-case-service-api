package uk.gov.justice.digital.hmpps.courtcaseserviceapi.service.hearing

import com.fasterxml.uuid.Generators
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
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
  private lateinit var service: HearingNotesServiceImpl

  private val testHearingId = TestDataBuilder.generateUUID()
  private val testDefendantId = TestDataBuilder.generateUUID()
  private val testNoteId = TestDataBuilder.generateUUID()
  private val testUserUUID = TestDataBuilder.generateUUID()

  @BeforeEach
  fun setup() {
    hearingRepository = mockk()
    defendantHearingRepository = mockk()
    service = HearingNotesServiceImpl(hearingRepository, defendantHearingRepository)
  }

  @AfterEach
  fun tearDown() {
    clearAllMocks()
  }

  @Test
  fun `addHearingCaseNoteAsDraft should throw HearingNotFoundException when hearing does not exist`() {
    val request = TestDataBuilder.createHearingCaseNoteRequest()

    every { defendantHearingRepository.findByDefendantIdAndHearingId(testDefendantId, testHearingId) } returns Mono.empty()

    StepVerifier.create(service.addHearingCaseNoteAsDraft(testHearingId, testDefendantId, request))
      .expectError(HearingNotFoundException::class.java)
      .verify()

    verify { defendantHearingRepository.findByDefendantIdAndHearingId(testDefendantId, testHearingId) }
  }

  @Test
  fun `addHearingCaseNoteAsDraft should save draft note when hearing has no existing note`() {
    val request = TestDataBuilder.createHearingCaseNoteRequest(userUUID = testUserUUID)
    val hearing = TestDataBuilder.createHearing(hearingID = testHearingId, hearingCaseNote = null)
    val savedHearing = hearing.copy(
      hearingCaseNote = TestDataBuilder.createHearingCaseNote(
        isDraft = true,
        note = request.note,
        author = request.author,
        createdByUUID = request.createdByUUID,
      ),
    )

    every { defendantHearingRepository.findByDefendantIdAndHearingId(testDefendantId, testHearingId) } returns Mono.just(hearing)
    every { hearingRepository.save(any<Hearing>()) } returns Mono.just(savedHearing)

    StepVerifier.create(service.addHearingCaseNoteAsDraft(testHearingId, testDefendantId, request))
      .assertNext { response ->
        assertThat(response.isDraft).isTrue
        assertThat(request.note).isEqualTo(response.note)
        assertThat(request.author).isEqualTo(response.author)
      }
      .verifyComplete()

    verify { hearingRepository.save(any<Hearing>()) }
  }

  @Test
  fun `addHearingCaseNoteAsDraft should filter out hearings with existing case notes`() {
    val request = TestDataBuilder.createHearingCaseNoteRequest()
    val existingNote = TestDataBuilder.createHearingCaseNote(isDraft = true)
    val hearing = TestDataBuilder.createHearing(hearingID = testHearingId, hearingCaseNote = existingNote)

    every { defendantHearingRepository.findByDefendantIdAndHearingId(testDefendantId, testHearingId) } returns Mono.just(hearing)

    StepVerifier.create(service.addHearingCaseNoteAsDraft(testHearingId, testDefendantId, request))
      .verifyComplete()

    verify(exactly = 0) { hearingRepository.save(any<Hearing>()) }
  }

  @Test
  fun `updateHearingCaseNoteDraft should throw HearingNotFoundException when hearing does not exist`() {
    val request = TestDataBuilder.createHearingCaseNoteRequest()

    every { defendantHearingRepository.findByDefendantIdAndHearingId(testDefendantId, testHearingId) } returns Mono.empty()

    StepVerifier.create(service.updateHearingCaseNoteDraft(testHearingId, testDefendantId, request))
      .expectError(HearingNotFoundException::class.java)
      .verify()
  }

  @Test
  fun `updateHearingCaseNoteDraft should update existing draft note`() {
    val request = TestDataBuilder.createHearingCaseNoteRequest(note = "Updated note", userUUID = testUserUUID)
    val existingNote = TestDataBuilder.createHearingCaseNote(
      id = testNoteId,
      note = "Original note",
      isDraft = true,
      isSoftDeleted = false,
      createdByUUID = testUserUUID,
    )
    val hearing = TestDataBuilder.createHearing(hearingID = testHearingId, hearingCaseNote = existingNote)
    val updatedHearing = hearing.copy()

    every { defendantHearingRepository.findByDefendantIdAndHearingId(testDefendantId, testHearingId) } returns Mono.just(hearing)
    every { hearingRepository.save(any<Hearing>()) } returns Mono.just(updatedHearing)

    StepVerifier.create(service.updateHearingCaseNoteDraft(testHearingId, testDefendantId, request))
      .assertNext { response ->
        assertThat(request.note).isEqualTo(response.note)
        assertThat(response.isDraft).isTrue
      }
      .verifyComplete()

    verify { hearingRepository.save(any<Hearing>()) }
  }

  @Test
  fun `updateHearingCaseNoteDraft should not update when note is null`() {
    val request = TestDataBuilder.createHearingCaseNoteRequest()
    val hearing = TestDataBuilder.createHearing(hearingID = testHearingId, hearingCaseNote = null)

    every { defendantHearingRepository.findByDefendantIdAndHearingId(testDefendantId, testHearingId) } returns Mono.just(hearing)

    StepVerifier.create(service.updateHearingCaseNoteDraft(testHearingId, testDefendantId, request))
      .verifyComplete()

    verify(exactly = 0) { hearingRepository.save(any<Hearing>()) }
  }

  @Test
  fun `updateHearingCaseNoteDraft should not update when note is soft deleted`() {
    val request = TestDataBuilder.createHearingCaseNoteRequest()
    val existingNote = TestDataBuilder.createHearingCaseNote(isDraft = true, isSoftDeleted = true)
    val hearing = TestDataBuilder.createHearing(hearingID = testHearingId, hearingCaseNote = existingNote)

    every { defendantHearingRepository.findByDefendantIdAndHearingId(testDefendantId, testHearingId) } returns Mono.just(hearing)

    StepVerifier.create(service.updateHearingCaseNoteDraft(testHearingId, testDefendantId, request))
      .verifyComplete()

    verify(exactly = 0) { hearingRepository.save(any<Hearing>()) }
  }

  @Test
  fun `updateHearingCaseNoteDraft should not update when note is not a draft`() {
    val request = TestDataBuilder.createHearingCaseNoteRequest()
    val existingNote = TestDataBuilder.createHearingCaseNote(isDraft = false, isSoftDeleted = false)
    val hearing = TestDataBuilder.createHearing(hearingID = testHearingId, hearingCaseNote = existingNote)

    every { defendantHearingRepository.findByDefendantIdAndHearingId(testDefendantId, testHearingId) } returns Mono.just(hearing)

    StepVerifier.create(service.updateHearingCaseNoteDraft(testHearingId, testDefendantId, request))
      .verifyComplete()

    verify(exactly = 0) { hearingRepository.save(any<Hearing>()) }
  }

  @Test
  fun `deleteHearingCaseNoteDraft should throw HearingNotFoundException when hearing does not exist`() {
    every { defendantHearingRepository.findByDefendantIdAndHearingId(testDefendantId, testHearingId) } returns Mono.empty()

    StepVerifier.create(service.deleteHearingCaseNoteDraft(testHearingId, testDefendantId, testUserUUID))
      .expectError(HearingNotFoundException::class.java)
      .verify()
  }

  @Test
  fun `deleteHearingCaseNoteDraft should soft delete draft note when user matches`() {
    val existingNote = TestDataBuilder.createHearingCaseNote(
      isDraft = true,
      isSoftDeleted = false,
      createdByUUID = testUserUUID,
    )
    val hearing = TestDataBuilder.createHearing(hearingID = testHearingId, hearingCaseNote = existingNote)

    every { defendantHearingRepository.findByDefendantIdAndHearingId(testDefendantId, testHearingId) } returns Mono.just(hearing)
    every { hearingRepository.save(any<Hearing>()) } returns Mono.just(hearing)

    StepVerifier.create(service.deleteHearingCaseNoteDraft(testHearingId, testDefendantId, testUserUUID))
      .assertNext { result ->
        assertThat(result).isTrue
      }
      .verifyComplete()

    verify { hearingRepository.save(any<Hearing>()) }
  }

  @Test
  fun `deleteHearingCaseNoteDraft should throw exception when user does not match creator`() {
    val existingNote = TestDataBuilder.createHearingCaseNote(
      isDraft = true,
      isSoftDeleted = false,
      createdByUUID = Generators.timeBasedEpochGenerator().generate(),
    )
    val hearing = TestDataBuilder.createHearing(hearingID = testHearingId, hearingCaseNote = existingNote)

    every { defendantHearingRepository.findByDefendantIdAndHearingId(testDefendantId, testHearingId) } returns Mono.just(hearing)

    StepVerifier.create(service.deleteHearingCaseNoteDraft(testHearingId, testDefendantId, testUserUUID))
      .expectError(HearingCaseNoteDraftNotFoundException::class.java)
      .verify()

    verify(exactly = 0) { hearingRepository.save(any<Hearing>()) }
  }

  @Test
  fun `updateHearingCaseNote should update published note when user matches`() {
    val request = TestDataBuilder.createHearingCaseNoteRequest(note = "Updated published note", userUUID = testUserUUID)
    val existingNote = TestDataBuilder.createHearingCaseNote(
      id = testNoteId,
      isDraft = false,
      isSoftDeleted = false,
      createdByUUID = testUserUUID,
      version = 1,
    )
    val hearing = TestDataBuilder.createHearing(hearingID = testHearingId, hearingCaseNote = existingNote)

    every { defendantHearingRepository.findByDefendantIdAndHearingId(testDefendantId, testHearingId) } returns Mono.just(hearing)
    every { hearingRepository.save(any<Hearing>()) } returns Mono.just(hearing)

    StepVerifier.create(service.updateHearingCaseNote(testHearingId, testDefendantId, testNoteId, request))
      .assertNext { result ->
        assertThat(result).isTrue
      }
      .verifyComplete()

    verify { hearingRepository.save(any<Hearing>()) }
  }

  @Test
  fun `updateHearingCaseNote should throw exception when user does not match creator`() {
    val request = TestDataBuilder.createHearingCaseNoteRequest(userUUID = testUserUUID)
    val existingNote = TestDataBuilder.createHearingCaseNote(
      id = testNoteId,
      isDraft = false,
      isSoftDeleted = false,
      createdByUUID = Generators.timeBasedEpochGenerator().generate(),
    )
    val hearing = TestDataBuilder.createHearing(hearingID = testHearingId, hearingCaseNote = existingNote)

    every { defendantHearingRepository.findByDefendantIdAndHearingId(testDefendantId, testHearingId) } returns Mono.just(hearing)

    StepVerifier.create(service.updateHearingCaseNote(testHearingId, testDefendantId, testNoteId, request))
      .expectError(HearingCaseNoteNotFoundException::class.java)
      .verify()
  }

  @Test
  fun `updateHearingCaseNote should throw exception when noteId does not match`() {
    val request = TestDataBuilder.createHearingCaseNoteRequest(userUUID = testUserUUID)
    val existingNote = TestDataBuilder.createHearingCaseNote(
      id = Generators.timeBasedEpochGenerator().generate(),
      isDraft = false,
      isSoftDeleted = false,
      createdByUUID = testUserUUID,
    )
    val hearing = TestDataBuilder.createHearing(hearingID = testHearingId, hearingCaseNote = existingNote)

    every { defendantHearingRepository.findByDefendantIdAndHearingId(testDefendantId, testHearingId) } returns Mono.just(hearing)

    StepVerifier.create(service.updateHearingCaseNote(testHearingId, testDefendantId, testNoteId, request))
      .expectError(HearingCaseNoteNotFoundException::class.java)
      .verify()
  }

  @Test
  fun `deleteHearingCaseNote should soft delete published note when user matches`() {
    val existingNote = TestDataBuilder.createHearingCaseNote(
      id = testNoteId,
      isDraft = false,
      isSoftDeleted = false,
      createdByUUID = testUserUUID,
    )
    val hearing = TestDataBuilder.createHearing(hearingID = testHearingId, hearingCaseNote = existingNote)

    every { defendantHearingRepository.findByDefendantIdAndHearingId(testDefendantId, testHearingId) } returns Mono.just(hearing)
    every { hearingRepository.save(any<Hearing>()) } returns Mono.just(hearing)

    StepVerifier.create(service.deleteHearingCaseNote(testHearingId, testDefendantId, testNoteId, testUserUUID))
      .assertNext { result ->
        assertThat(result).isTrue
      }
      .verifyComplete()

    verify { hearingRepository.save(any<Hearing>()) }
  }

  @Test
  fun `deleteHearingCaseNote should throw exception when user does not match creator`() {
    val existingNote = TestDataBuilder.createHearingCaseNote(
      id = testNoteId,
      isDraft = false,
      isSoftDeleted = false,
      createdByUUID = Generators.timeBasedEpochGenerator().generate(),
    )
    val hearing = TestDataBuilder.createHearing(hearingID = testHearingId, hearingCaseNote = existingNote)

    every { defendantHearingRepository.findByDefendantIdAndHearingId(testDefendantId, testHearingId) } returns Mono.just(hearing)

    StepVerifier.create(service.deleteHearingCaseNote(testHearingId, testDefendantId, testNoteId, testUserUUID))
      .expectError(HearingCaseNoteNotFoundException::class.java)
      .verify()

    verify(exactly = 0) { hearingRepository.save(any<Hearing>()) }
  }
}
