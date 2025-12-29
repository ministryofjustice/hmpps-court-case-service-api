package uk.gov.justice.digital.hmpps.courtcaseserviceapi.mappers

import com.fasterxml.uuid.Generators
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.api.hearing.HearingCaseNoteRequest
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.api.hearing.HearingCaseNoteResponse
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.hearing.HearingCaseNote
import java.time.OffsetDateTime
import java.time.ZoneOffset

class HearingMapper {
  companion object {

    fun convertRequestNoteToBusiness(hearingCaseNoteApi: HearingCaseNoteRequest): HearingCaseNote = HearingCaseNote(
      id = Generators.timeBasedEpochGenerator().generate(),
      note = hearingCaseNoteApi.note,
      author = hearingCaseNoteApi.author,
      isDraft = true,
      isLegacy = false,
      createdByUUID = hearingCaseNoteApi.createdByUUID,
      createdAt = OffsetDateTime.now(ZoneOffset.UTC),
      createdBy = hearingCaseNoteApi.author,
      updatedAt = null,
      updatedBy = null,
      isSoftDeleted = false,
      version = 0,
    )

    fun convertBusinessNoteToResponse(hearingCaseNote: HearingCaseNote?): HearingCaseNoteResponse = HearingCaseNoteResponse(
      noteId = hearingCaseNote?.id,
      note = hearingCaseNote?.note,
      createdAt = hearingCaseNote?.createdAt,
      author = hearingCaseNote?.author,
      createdByUuid = hearingCaseNote?.createdByUUID,
      isDraft = hearingCaseNote?.isDraft,
      isLegacy = hearingCaseNote?.isLegacy,
    )
  }
}
