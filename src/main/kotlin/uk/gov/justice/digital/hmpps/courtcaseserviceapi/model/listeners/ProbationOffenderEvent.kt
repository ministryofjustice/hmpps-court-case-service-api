package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.listeners

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ProbationOffenderEvent(
  @JsonProperty("eventType")
  val eventType: String,
  @JsonProperty("eventDatetime")
  val eventDatetime: Instant,
  @JsonProperty("offenderId")
  val offenderId: Int,
  @JsonProperty("crn")
  val crn: String,
  @JsonProperty("sourceId")
  val sourceId: String,
)
