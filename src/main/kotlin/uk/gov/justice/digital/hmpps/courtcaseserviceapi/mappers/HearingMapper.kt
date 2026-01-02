package uk.gov.justice.digital.hmpps.courtcaseserviceapi.mappers

import com.fasterxml.uuid.Generators
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.api.hearing.HearingCaseNoteRequest
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.api.hearing.HearingCaseNoteResponse
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.hearing.HearingCaseNote
import java.time.OffsetDateTime
import java.util.UUID

class HearingMapper {
  companion object {

    fun mapRequestToBusinessNote(defendantId: UUID, requestNote: HearingCaseNoteRequest): HearingCaseNote {
      val newCaseNote = HearingCaseNote(
        id = Generators.timeBasedEpochGenerator().generate(),
        legacyId = null,
        defendantId = defendantId,
        note = requestNote.note,
        author = requestNote.author,
        isDraft = true,
        isLegacy = false,
        createdByUUID = requestNote.createdByUUID,
        createdAt = OffsetDateTime.now(),
        createdBy = requestNote.author,
        updatedAt = null,
        updatedBy = null,
        isSoftDeleted = false,
        version = 0,
      )
      return newCaseNote
    }

    fun mapBusinessNoteToResponse(caseNote: HearingCaseNote): HearingCaseNoteResponse = HearingCaseNoteResponse(
      noteId = caseNote.id,
      note = caseNote.note,
      createdAt = caseNote.createdAt,
      author = caseNote.author,
      createdByUuid = caseNote.createdByUUID,
      isDraft = caseNote.isDraft,
      isLegacy = caseNote.isLegacy,
    )
  }
}
