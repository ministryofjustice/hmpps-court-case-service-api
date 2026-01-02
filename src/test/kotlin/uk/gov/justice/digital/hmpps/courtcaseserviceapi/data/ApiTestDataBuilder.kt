package uk.gov.justice.digital.hmpps.courtcaseserviceapi.data

import com.fasterxml.uuid.Generators
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.hearing.Hearing
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.hearing.HearingCaseNote
import java.time.OffsetDateTime
import java.util.UUID

object ApiTestDataBuilder {

  fun generateUUID(): UUID = Generators.timeBasedEpochGenerator().generate()

  fun buildHearing(
    id: UUID,
    hearingId: UUID,
    defendantId: UUID,
  ): Hearing = Hearing(
    id = id,
    hearingId = hearingId,
    hearingCaseNote = emptyList(),
  )

  fun buildHearingWithDraftNote(
    id: UUID,
    hearingId: UUID,
    defendantId: UUID,
    noteId: UUID,
    note: String,
    author: String,
    createdByUuid: UUID,
  ): Hearing = Hearing(
    id = id,
    hearingId = hearingId,
    hearingCaseNote = listOf(
      HearingCaseNote(
        id = noteId,
        legacyId = 123,
        defendantId = defendantId,
        note = note,
        author = author,
        isDraft = true,
        isLegacy = true,
        createdByUUID = createdByUuid,
        createdAt = OffsetDateTime.now(),
        createdBy = author,
        updatedAt = null,
        updatedBy = null,
        isSoftDeleted = false,
        version = 0,
      ),
    ),
  )

  fun buildHearingWithPublishedNote(
    id: UUID,
    hearingId: UUID,
    defendantId: UUID,
    noteId: UUID,
    note: String,
    author: String,
    createdByUuid: UUID,
  ): Hearing = Hearing(
    id = id,
    hearingId = hearingId,
    hearingCaseNote = listOf(
      HearingCaseNote(
        id = noteId,
        legacyId = 123,
        defendantId = defendantId,
        note = note,
        author = author,
        isDraft = false,
        isLegacy = false,
        createdByUUID = createdByUuid,
        createdAt = OffsetDateTime.now(),
        createdBy = author,
        updatedAt = null,
        updatedBy = author,
        isSoftDeleted = false,
        version = 0,
      ),
    ),
  )
}
