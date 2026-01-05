package uk.gov.justice.digital.hmpps.courtcaseserviceapi.service.listeners

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class NewOffenderDetailsEventService {

  private companion object {
    val LOG: Logger = LoggerFactory.getLogger(this::class.java)
  }
}
