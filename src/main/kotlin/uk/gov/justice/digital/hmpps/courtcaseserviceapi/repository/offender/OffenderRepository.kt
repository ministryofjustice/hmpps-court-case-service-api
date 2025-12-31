package uk.gov.justice.digital.hmpps.courtcaseserviceapi.repository.offender

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.offender.Offender
import java.util.UUID

@Repository
interface OffenderRepository : ReactiveCrudRepository<Offender, UUID> {
  fun findByCrn(crn: String): Mono<Offender>
}
