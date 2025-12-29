package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.listeners

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class NewOffenderEvent(
  @JsonProperty("eventType")
  val eventType: String,
  @JsonProperty("version")
  val version: Int,
  @JsonProperty("detailUrl")
  val detailUrl: String,
  @JsonProperty("occurredAt")
  val occurredAt: OffsetDateTime,
  @JsonProperty("description")
  val description: String,
  @JsonProperty("additionalInformation")
  val additionalInformation: AdditionalInformation,
  @JsonProperty("personReference")
  val personReference: PersonReference,
)
