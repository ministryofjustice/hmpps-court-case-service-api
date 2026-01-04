package uk.gov.justice.digital.hmpps.courtcaseserviceapi.repository.common

import reactor.core.publisher.Mono
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.common.caseDocuments.CaseHearingDefendant
import java.util.UUID

interface CaseHearingDefendantRepository {
  fun findByHearingIdAndDefendantId(
    hearingId: UUID,
    defendantId: UUID,
  ): Mono<CaseHearingDefendant>
}
