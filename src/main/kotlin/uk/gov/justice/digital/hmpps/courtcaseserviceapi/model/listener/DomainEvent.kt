package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.listener

data class DomainEvent(
  val eventType: String,
  val detailUrl: String,
  val personReference: PersonReference? = null,
)