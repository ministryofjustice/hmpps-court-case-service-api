package uk.gov.justice.digital.hmpps.courtcaseserviceapi.repository.cases

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.cases.ProsecutionCase
import java.util.UUID

@Repository
interface ProsecutionCaseRepository : ReactiveCrudRepository<ProsecutionCase, UUID> {
  fun findByCaseId(caseId: String): Mono<ProsecutionCase>
}
