package uk.gov.justice.digital.hmpps.courtcaseserviceapi.repository.cases

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.cases.ProsecutionCase

@Repository
interface ProsecutionRepository: ReactiveCrudRepository<ProsecutionCase, Long> {
}