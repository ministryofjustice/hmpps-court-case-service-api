package uk.gov.justice.digital.hmpps.courtcaseserviceapi.listeners

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class NewOffenderDetailsEventListener {

  private companion object {
    val LOG: Logger = LoggerFactory.getLogger(this::class.java)
    const val MESSAGE_AGE_THRESHOLD: Long = 2L
  }
}
