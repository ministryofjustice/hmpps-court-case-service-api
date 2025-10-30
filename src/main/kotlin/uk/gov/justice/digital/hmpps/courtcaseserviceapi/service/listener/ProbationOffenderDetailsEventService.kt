package uk.gov.justice.digital.hmpps.courtcaseserviceapi.service.listener

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ProbationOffenderDetailsEventService {

  private companion object {
    val LOG: Logger = LoggerFactory.getLogger(this::class.java)
  }

  fun updateOffenderProbationStatus(crn: String){
    LOG.info("Updating probation offender details")
  }
}