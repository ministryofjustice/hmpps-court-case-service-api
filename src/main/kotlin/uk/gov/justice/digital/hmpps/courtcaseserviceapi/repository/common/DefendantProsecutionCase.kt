package uk.gov.justice.digital.hmpps.courtcaseserviceapi.repository.common

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.common.DefendantProsecution
import java.util.UUID

@Repository
interface DefendantProsecutionCase : ReactiveCrudRepository<DefendantProsecution, UUID>
