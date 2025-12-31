package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters

import com.fasterxml.jackson.databind.ObjectMapper
import io.r2dbc.postgresql.codec.Json
import org.slf4j.LoggerFactory
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.hearing.HearingOutcome

@WritingConverter
class HearingOutcomeEncoder : Converter<HearingOutcome, Json> {
  private val objMapper = ObjectMapper()

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  override fun convert(source: HearingOutcome): Json {
    log.info("Converting HearingOutcome to JSON")
    return Json.of(objMapper.writeValueAsString(source))
  }
}

@ReadingConverter
class HearingOutcomeDecoder : Converter<Json, HearingOutcome> {
  private val objectMapper = ObjectMapper()

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  override fun convert(source: Json): HearingOutcome {
    log.info("Converting JSON to HearingOutcome")
    return objectMapper.readValue(source.asString(), HearingOutcome::class.java)
  }
}
