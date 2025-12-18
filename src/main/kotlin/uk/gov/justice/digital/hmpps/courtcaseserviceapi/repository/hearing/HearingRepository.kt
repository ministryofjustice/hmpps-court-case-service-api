package uk.gov.justice.digital.hmpps.courtcaseserviceapi.repository.hearing

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.defendant.Defendant
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.hearing.Hearing
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.hearing.HearingCaseNote
import java.util.UUID

@Repository
interface HearingRepository: ReactiveCrudRepository<Hearing, UUID> {
  fun findByHearingCaseNote(hearingId: String, defendantId: String): Mono<Hearing>
}