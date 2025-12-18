package uk.gov.justice.digital.hmpps.courtcaseserviceapi.service.hearing

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.hearing.HearingCaseNote
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.repository.hearing.HearingRepository

@Service
class HearingServiceImpl(
  val hearingRepository: HearingRepository,
): HearingService {
  override fun addNote(hearingId: String, defendantId: String, note: HearingCaseNote): Mono<HearingCaseNote> {
    //add repo logic
    val noteId = 0

    //return
    return Mono.just(note.copy(id = noteId))
  }
}