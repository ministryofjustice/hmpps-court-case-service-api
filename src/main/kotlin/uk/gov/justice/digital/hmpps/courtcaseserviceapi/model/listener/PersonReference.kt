package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.listener

data class PersonReference(
  val identifiers: List<PersonIdentifier>? = emptyList(),
)
