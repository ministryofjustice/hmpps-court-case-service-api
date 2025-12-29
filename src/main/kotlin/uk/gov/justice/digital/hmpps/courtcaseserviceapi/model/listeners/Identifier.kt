package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.listeners

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Identifier(
  @JsonProperty("type")
  val type: String,
  @JsonProperty("value")
  val value: String,
)
