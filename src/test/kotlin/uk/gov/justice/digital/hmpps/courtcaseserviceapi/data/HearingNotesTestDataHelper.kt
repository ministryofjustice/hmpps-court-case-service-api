package uk.gov.justice.digital.hmpps.courtcaseserviceapi.data

import com.fasterxml.uuid.Generators
import com.fasterxml.uuid.impl.UUIDUtil
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.api.hearing.HearingCaseNoteRequest
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.hearing.Hearing
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.hearing.HearingCaseNote
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

object HearingNotesTestDataHelper {

  val TEST_HEARING_ID: UUID = UUIDUtil.uuid("550e8400-e29b-41d4-a716-446655440000")
  val TEST_DEFENDANT_ID: UUID = UUIDUtil.uuid("550e8400-e29b-41d4-a716-446655440001")
  val TEST_NOTE_ID: UUID = Generators.timeBasedEpochGenerator().generate()
  val TEST_USER_UUID: UUID = UUIDUtil.uuid("550e8400-e29b-41d4-a716-446655440002")
  const val TEST_AUTHOR = "Test Author"
  const val TEST_NOTE_CONTENT = "This is a test note"
  const val UPDATED_NOTE_CONTENT = "This is an updated test note"

  fun createHearingCaseNoteRequest(
    note: String = TEST_NOTE_CONTENT,
    author: String = TEST_AUTHOR,
    createdByUUID: UUID? = null,
  ): HearingCaseNoteRequest = HearingCaseNoteRequest(
    note = note,
    createdByUUID = createdByUUID ?: TEST_USER_UUID,
    author = author,
  )

  fun createUpdatedHearingCaseNoteRequest(): HearingCaseNoteRequest = HearingCaseNoteRequest(
    note = UPDATED_NOTE_CONTENT,
    createdByUUID = TEST_USER_UUID,
    author = TEST_AUTHOR,
  )

  fun createHearingCaseNote(
    id: UUID = TEST_NOTE_ID,
    note: String = TEST_NOTE_CONTENT,
    isDraft: Boolean = true,
    isSoftDeleted: Boolean = false,
    createdByUUID: UUID = TEST_USER_UUID,
  ): HearingCaseNote = HearingCaseNote(
    id = id,
    note = note,
    author = TEST_AUTHOR,
    isDraft = isDraft,
    isLegacy = false,
    createdByUUID = createdByUUID,
    createdAt = OffsetDateTime.now(ZoneOffset.UTC),
    createdBy = TEST_AUTHOR,
    updatedAt = null,
    updatedBy = null,
    isSoftDeleted = isSoftDeleted,
    version = 0,
  )

  fun createHearingWithoutNote(hearingId: UUID): Hearing = Hearing(
    id = hearingId,
    hearingID = hearingId,
    legacyID = 12345,
    type = "TRIAL",
    eventType = "CONFIRMED",
    listNumber = "LIST-001",
    prepStatus = "PREPARED",
    isHearingOutcomeNotRequired = false,
    firstCreated = OffsetDateTime.now(ZoneOffset.UTC),
    hearingOutcome = null,
    hearingCaseNote = null,
    createdAt = OffsetDateTime.now(ZoneOffset.UTC),
    createdBy = TEST_AUTHOR,
    updatedAt = null,
    updatedBy = null,
    isSoftDeleted = false,
    version = 0,
  )

  fun createHearingWithDraftNote(hearingId: UUID): Hearing {
    val hearing = createHearingWithoutNote(hearingId)
    return hearing.copy(
      hearingCaseNote = createHearingCaseNote(isDraft = true),
    )
  }

  fun createHearingWithUpdatedDraftNote(hearingId: UUID): Hearing {
    val hearing = createHearingWithoutNote(hearingId)
    return hearing.copy(
      hearingCaseNote = createHearingCaseNote(
        note = UPDATED_NOTE_CONTENT,
        isDraft = true,
      ),
    )
  }

  fun createHearingWithDeletedDraftNote(hearingId: UUID): Hearing {
    val hearing = createHearingWithoutNote(hearingId)
    return hearing.copy(
      hearingCaseNote = createHearingCaseNote(
        isDraft = true,
        isSoftDeleted = true,
      ),
    )
  }

  fun createHearingWithPublishedNote(hearingId: UUID, noteId: UUID): Hearing {
    val hearing = createHearingWithoutNote(hearingId)
    return hearing.copy(
      hearingCaseNote = createHearingCaseNote(
        id = noteId,
        isDraft = false,
      ),
    )
  }

  fun createHearingWithUpdatedPublishedNote(hearingId: UUID, noteId: UUID): Hearing {
    val hearing = createHearingWithoutNote(hearingId)
    return hearing.copy(
      hearingCaseNote = createHearingCaseNote(
        id = noteId,
        note = UPDATED_NOTE_CONTENT,
        isDraft = false,
      ),
    )
  }

  fun createHearingWithDeletedPublishedNote(hearingId: UUID, noteId: UUID): Hearing {
    val hearing = createHearingWithoutNote(hearingId)
    return hearing.copy(
      hearingCaseNote = createHearingCaseNote(
        id = noteId,
        isDraft = false,
        isSoftDeleted = true,
      ),
    )
  }

  fun loadJsonFile(fileName: String): String = this::class.java.classLoader
    .getResource("wiremock/__files/$fileName")
    ?.readText()
    ?: throw IllegalArgumentException("File not found: $fileName")
}
