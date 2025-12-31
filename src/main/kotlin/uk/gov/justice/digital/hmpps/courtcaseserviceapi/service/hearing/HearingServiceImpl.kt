package uk.gov.justice.digital.hmpps.courtcaseserviceapi.service.hearing

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.repository.hearing.HearingRepository

@Service
class HearingServiceImpl(private val hearingRepository: HearingRepository) : HearingService
