package uk.gov.justice.digital.hmpps.courtcaseserviceapi.repository.hearing

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.hearing.Hearing
import java.util.UUID

@Repository
interface HearingRepository : ReactiveCrudRepository<Hearing, UUID>
