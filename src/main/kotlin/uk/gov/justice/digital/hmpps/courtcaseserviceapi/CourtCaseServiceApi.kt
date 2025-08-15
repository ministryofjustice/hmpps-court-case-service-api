package uk.gov.justice.digital.hmpps.courtcaseserviceapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CourtCaseServiceApi

fun main(args: Array<String>) {
  runApplication<CourtCaseServiceApi>(*args)
}
