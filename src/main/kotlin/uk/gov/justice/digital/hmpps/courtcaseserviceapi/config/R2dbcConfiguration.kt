package uk.gov.justice.digital.hmpps.courtcaseserviceapi.config

import com.fasterxml.jackson.databind.ObjectMapper
import io.r2dbc.spi.ConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters.AddressDecoder
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters.AddressEncoder
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters.CaseDocumentDecoder
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters.CaseDocumentEncoder
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters.CaseMarkerDecoder
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters.CaseMarkerEncoder
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters.CaseURNDecoder
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters.CaseURNsEncoder
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters.CourtRoomDecoder
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters.CourtRoomEncoder
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters.HearingCaseNoteDecoder
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters.HearingCaseNoteEncoder
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters.HearingOutcomeDecoder
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters.HearingOutcomeEncoder
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters.JudicialResultDecoder
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters.JudicialResultEncoder
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters.PersonDecoder
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters.PersonEncoder
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters.PleaDecoder
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters.PleaEncoder
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters.VerdictDecoder
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters.VerdictEncoder

@Configuration
@EnableR2dbcRepositories
class R2dbcConfiguration(
  private val connectionFactory: ConnectionFactory,
  private val objectMapper: ObjectMapper,
) : AbstractR2dbcConfiguration() {

  override fun connectionFactory(): ConnectionFactory = connectionFactory

  @Bean
  override fun r2dbcCustomConversions(): R2dbcCustomConversions = R2dbcCustomConversions(storeConversions, addCustomConverters())

  private fun addCustomConverters(): MutableList<Converter<*, *>> {
    val converters: MutableList<Converter<*, *>> = ArrayList()
    converters.add(HearingCaseNoteEncoder(objectMapper))
    converters.add(HearingCaseNoteDecoder(objectMapper))
    converters.add(HearingOutcomeEncoder(objectMapper))
    converters.add(HearingOutcomeDecoder(objectMapper))
    converters.add(CaseDocumentEncoder(objectMapper))
    converters.add(CaseDocumentDecoder(objectMapper))
    converters.add(VerdictEncoder(objectMapper))
    converters.add(VerdictDecoder(objectMapper))
    converters.add(PleaEncoder(objectMapper))
    converters.add(PleaDecoder(objectMapper))
    converters.add(JudicialResultEncoder(objectMapper))
    converters.add(JudicialResultDecoder(objectMapper))
    converters.add(PersonEncoder(objectMapper))
    converters.add(PersonDecoder(objectMapper))
    converters.add(CaseMarkerEncoder(objectMapper))
    converters.add(CaseMarkerDecoder(objectMapper))
    converters.add(CourtRoomEncoder(objectMapper))
    converters.add(CourtRoomDecoder(objectMapper))
    converters.add(CaseURNsEncoder(objectMapper))
    converters.add(CaseURNDecoder(objectMapper))
    converters.add(AddressEncoder(objectMapper))
    converters.add(AddressDecoder(objectMapper))
    return converters
  }
}
