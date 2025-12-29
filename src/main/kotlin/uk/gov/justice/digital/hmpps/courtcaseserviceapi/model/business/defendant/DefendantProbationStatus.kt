package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.defendant

import com.fasterxml.jackson.annotation.JsonValue
import org.slf4j.Logger
import org.slf4j.LoggerFactory

enum class DefendantProbationStatus(private val status: String) {
  CURRENT("Current"),
  PREVIOUSLY_KNOWN("Previously known"),
  NOT_SENTENCED("Pre-sentence record"),
  UNCONFIRMED_NO_RECORD("No record"),
  CONFIRMED_NO_RECORD("No record"),
  ;

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
    private val DEFAULT = UNCONFIRMED_NO_RECORD

    fun of(status: String?): DefendantProbationStatus {
      val probationStatus = status?.trim()?.uppercase() ?: DEFAULT.status
      return try {
        val formattedString = probationStatus.replace(" ", "_")
        if (formattedString == "NO_RECORD") {
          UNCONFIRMED_NO_RECORD
        } else {
          valueOf(formattedString)
        }
      } catch (ex: RuntimeException) {
        log.error("Unable to map {} to a known ProbationStatus enum value", status, ex)
        DEFAULT
      }
    }
  }

  @JsonValue
  fun getStatus(): String = status
}
