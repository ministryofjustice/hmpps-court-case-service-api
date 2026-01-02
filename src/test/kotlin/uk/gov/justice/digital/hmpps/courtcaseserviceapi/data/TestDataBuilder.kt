package uk.gov.justice.digital.hmpps.courtcaseserviceapi.data

import com.fasterxml.uuid.Generators
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.api.hearing.HearingCaseNoteRequest
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.api.hearing.HearingCaseNoteResponse
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.hearing.Hearing
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.hearing.HearingCaseNote
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.hearing.HearingOutcome
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

object TestDataBuilder {

  fun generateUUID(): UUID = Generators.timeBasedEpochGenerator().generate()

  fun createHearingCaseNoteRequest(
    note: String? = "Test note",
    userUUID: UUID? = generateUUID(),
    author: String = "Test Author",
  ): HearingCaseNoteRequest = HearingCaseNoteRequest(
    note = note,
    createdByUUID = userUUID,
    author = author,
  )

  fun createHearingCaseNote(
    id: UUID? = generateUUID(),
    note: String? = "Test hearing case note",
    author: String? = "Test Author",
    isDraft: Boolean? = true,
    isLegacy: Boolean? = false,
    createdByUUID: UUID? = generateUUID(),
    createdAt: OffsetDateTime? = OffsetDateTime.now(ZoneOffset.UTC),
    createdBy: String? = "Test Author",
    updatedAt: OffsetDateTime? = null,
    updatedBy: String? = null,
    isSoftDeleted: Boolean? = false,
    version: Int? = 0,
  ): HearingCaseNote = HearingCaseNote(
    id = id,
    note = note,
    author = author,
    isDraft = isDraft,
    isLegacy = isLegacy,
    createdByUUID = createdByUUID,
    createdAt = createdAt,
    createdBy = createdBy,
    updatedAt = updatedAt,
    updatedBy = updatedBy,
    isSoftDeleted = isSoftDeleted,
    version = version,
  )

  fun createHearing(
    id: UUID? = generateUUID(),
    hearingID: UUID = generateUUID(),
    legacyID: Int? = 12345,
    type: String? = "Trial",
    eventType: String? = "Sentence",
    listNumber: String? = "List-001",
    prepStatus: String? = "Prepared",
    isHearingOutcomeNotRequired: Boolean? = false,
    firstCreated: OffsetDateTime? = OffsetDateTime.now(ZoneOffset.UTC),
    hearingOutcome: HearingOutcome? = null,
    hearingCaseNote: HearingCaseNote? = null,
    createdAt: OffsetDateTime? = OffsetDateTime.now(ZoneOffset.UTC),
    createdBy: String? = "Test User",
    updatedAt: OffsetDateTime? = null,
    updatedBy: String? = null,
    isSoftDeleted: Boolean? = false,
    version: Int? = 1,
  ): Hearing = Hearing(
    id = id,
    hearingID = hearingID,
    legacyID = legacyID,
    type = type,
    eventType = eventType,
    listNumber = listNumber,
    prepStatus = prepStatus,
    isHearingOutcomeNotRequired = isHearingOutcomeNotRequired,
    firstCreated = firstCreated,
    hearingOutcome = hearingOutcome,
    hearingCaseNote = hearingCaseNote,
    createdAt = createdAt,
    createdBy = createdBy,
    updatedAt = updatedAt,
    updatedBy = updatedBy,
    isSoftDeleted = isSoftDeleted,
    version = version,
  )

  fun createHearingCaseNoteResponse(
    noteId: UUID? = generateUUID(),
    note: String? = "Test note",
    createdAt: OffsetDateTime? = OffsetDateTime.now(ZoneOffset.UTC),
    author: String? = "Test Author",
    createdByUuid: UUID? = generateUUID(),
    isDraft: Boolean? = true,
    isLegacy: Boolean? = false,
  ): HearingCaseNoteResponse = HearingCaseNoteResponse(
    noteId = noteId,
    note = note,
    createdAt = createdAt,
    author = author,
    createdByUuid = createdByUuid,
    isDraft = isDraft,
    isLegacy = isLegacy,
  )
}
