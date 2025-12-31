package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.api.hearing

import java.util.UUID

data class HearingCaseNoteRequest(val note: String?, var createdByUUID: UUID?, val author: String)
