package uk.gov.justice.digital.hmpps.courtcaseserviceapi.service.listeners

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.client.NDeliusClient
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.offender.Offender
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.repository.offender.OffenderRepository

@Service
class ProbationOffenderDetailsEventService(
  val offenderRepository: OffenderRepository,
  val nDeliusClient: NDeliusClient,
) {

  private companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  fun updateOffenderProbationStatus(crn: String): Mono<Offender> {
    log.info("Updating probation offender details")
    return offenderRepository.findByCrn(crn)
      .doOnNext { crn -> log.info("Fetching probation status for crn $crn") }
  }
}
