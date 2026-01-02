package uk.gov.justice.digital.hmpps.courtcaseserviceapi.data

import com.fasterxml.uuid.Generators
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.api.hearing.HearingCaseNoteRequest
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.hearing.Hearing
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.hearing.HearingCaseNote
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

object TestDataBuilder {

  fun generateUUID(): UUID = Generators.timeBasedEpochGenerator().generate()

  fun buildHearingCaseNoteRequest(
    note: String? = "Test note",
    createdByUUID: UUID? = generateUUID(),
    author: String = "Test Author",
  ): HearingCaseNoteRequest = HearingCaseNoteRequest(
    note = note,
    createdByUUID = createdByUUID,
    author = author,
  )

  fun buildHearingCaseNote(
    id: UUID = generateUUID(),
    defendantId: UUID = generateUUID(),
    note: String? = "Test note",
    author: String = "Test Author",
    isDraft: Boolean = true,
    isLegacy: Boolean = false,
    createdByUUID: UUID? = generateUUID(),
    createdAt: OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC),
    isSoftDeleted: Boolean = false,
    version: Int = 0,
  ): HearingCaseNote = HearingCaseNote(
    id = id,
    legacyId = null,
    defendantId = defendantId,
    note = note,
    author = author,
    isDraft = isDraft,
    isLegacy = isLegacy,
    createdByUUID = createdByUUID,
    createdAt = createdAt,
    createdBy = author,
    updatedAt = null,
    updatedBy = null,
    isSoftDeleted = isSoftDeleted,
    version = version,
  )

  fun buildHearing(
    id: UUID = generateUUID(),
    hearingId: UUID = generateUUID(),
    type: String? = "Test Type",
    hearingCaseNote: List<HearingCaseNote>? = null,
    createdAt: OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC),
    isSoftDeleted: Boolean = false,
    version: Int = 0,
  ): Hearing = Hearing(
    id = id,
    legacyId = null,
    hearingId = hearingId,
    type = type,
    eventType = "Test Event",
    listNumber = "1",
    prepStatus = "PREPARED",
    isHearingOutcomeNotRequired = false,
    firstCreated = createdAt,
    hearingOutcome = null,
    hearingCaseNote = hearingCaseNote,
    createdAt = createdAt,
    createdBy = "Test User",
    updatedAt = null,
    updatedBy = null,
    isSoftDeleted = isSoftDeleted,
    version = version,
  )
}
