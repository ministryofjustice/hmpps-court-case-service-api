package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.exceptions

class HearingCaseNoteDraftNotFoundException(message: String, vararg args: Any) : RuntimeException(String.format(message, *args))
