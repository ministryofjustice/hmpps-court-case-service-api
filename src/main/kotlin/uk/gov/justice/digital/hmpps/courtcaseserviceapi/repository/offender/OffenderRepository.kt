package uk.gov.justice.digital.hmpps.courtcaseserviceapi.repository.offender

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.offender.Offender

@Repository
interface OffenderRepository: ReactiveCrudRepository<Offender, Long> {
}