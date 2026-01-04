package uk.gov.justice.digital.hmpps.courtcaseserviceapi.repository.defendant

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.defendant.Defendant
import java.util.UUID

@Repository
interface DefendantRepository : ReactiveCrudRepository<Defendant, UUID>
