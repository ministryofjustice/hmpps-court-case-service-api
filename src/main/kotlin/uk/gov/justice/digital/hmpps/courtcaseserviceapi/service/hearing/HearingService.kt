package uk.gov.justice.digital.hmpps.courtcaseserviceapi.service.hearing

import reactor.core.publisher.Mono
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.api.hearing.HearingCaseNoteRequest
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.api.hearing.HearingCaseNoteResponse
import java.util.UUID

interface HearingService {
  fun addHearingCaseNoteAsDraft(hearingId: UUID, defendantId: UUID, requestNote: HearingCaseNoteRequest): Mono<HearingCaseNoteResponse>
  fun updateHearingCaseNoteDraft(hearingId: UUID, defendantId: UUID, requestNote: HearingCaseNoteRequest): Mono<HearingCaseNoteResponse>
  fun deleteHearingCaseNoteDraft(hearingId: UUID, defendantId: UUID, userUUID: UUID): Mono<Boolean>
}
