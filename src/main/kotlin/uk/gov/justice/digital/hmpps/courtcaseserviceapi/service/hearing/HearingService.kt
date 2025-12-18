package uk.gov.justice.digital.hmpps.courtcaseserviceapi.service.hearing

import reactor.core.publisher.Mono
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.hearing.HearingCaseNote

interface HearingService {
  fun addNote(hearingId: String, defendantId: String, note: HearingCaseNote): Mono<HearingCaseNote>
}