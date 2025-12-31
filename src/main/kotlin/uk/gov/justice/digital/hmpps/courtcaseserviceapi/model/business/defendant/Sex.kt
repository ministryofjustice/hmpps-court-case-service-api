package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.defendant

import org.slf4j.Logger
import org.slf4j.LoggerFactory

enum class Sex(private val sex: String) {
  MALE("M"),
  FEMALE("F"),
  NOT_KNOWN("N"),
  NOT_SPECIFIED("NS"),
  ;

  fun getSex(): String = sex

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)

    fun fromString(sex: String?): Sex {
      val sex = sex?.trim()?.uppercase() ?: NOT_KNOWN.sex

      return when (sex) {
        "MALE", "M" -> MALE
        "FEMALE", "F" -> FEMALE
        "NOT_KNOWN", "N" -> NOT_SPECIFIED
        "NOT_SPECIFIED", "NS" -> NOT_SPECIFIED
        else -> {
          log.error("Received an unexpected value to map for sex {}", sex)
          NOT_KNOWN
        }
      }
    }
  }
}
