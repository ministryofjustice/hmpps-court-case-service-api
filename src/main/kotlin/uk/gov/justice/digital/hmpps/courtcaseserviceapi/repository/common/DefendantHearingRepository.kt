package uk.gov.justice.digital.hmpps.courtcaseserviceapi.repository.common

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.common.DefendantHearing
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.hearing.Hearing
import java.util.UUID

@Repository
interface DefendantHearingRepository : ReactiveCrudRepository<DefendantHearing, UUID> {

  @Query(
    """ SELECT h.* FROM hearing h 
    |INNER JOIN defendant_hearing dh ON h.id = dh.id 
    |INNER JOIN defendant d ON dh.defendant_id = d.id 
    |WHERE d.id = :defendantId 
    |AND h.id = :hearingId
    |AND d.is_soft_deleted is false
    |AND h.is_soft_deleted is false
    |""",
  )
  fun findByDefendantIdAndHearingId(defendantId: UUID, hearingId: UUID): Mono<Hearing>
}
