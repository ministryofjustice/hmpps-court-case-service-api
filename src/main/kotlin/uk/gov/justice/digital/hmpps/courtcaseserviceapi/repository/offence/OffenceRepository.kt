package uk.gov.justice.digital.hmpps.courtcaseserviceapi.repository.offence

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.offence.Offence

@Repository
interface OffenceRepository: ReactiveCrudRepository<Offence, Long> {
}