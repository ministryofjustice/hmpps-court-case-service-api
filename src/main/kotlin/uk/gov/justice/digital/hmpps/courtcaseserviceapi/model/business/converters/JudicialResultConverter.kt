package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters

import com.fasterxml.jackson.databind.ObjectMapper
import io.r2dbc.postgresql.codec.Json
import org.slf4j.LoggerFactory
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.offence.JudicialResult

@WritingConverter
class JudicialResultEncoder : Converter<JudicialResult, Json> {
  private val objMapper = ObjectMapper()

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  override fun convert(source: JudicialResult): Json {
    log.info("Converting JudicialResult to JSON")
    return Json.of(objMapper.writeValueAsString(source))
  }
}

@ReadingConverter
class JudicialResultDecoder : Converter<Json, JudicialResult> {
  private val objectMapper = ObjectMapper()

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  override fun convert(source: Json): JudicialResult {
    log.info("Converting JSON to JudicialResult")
    return objectMapper.readValue(source.asString(), JudicialResult::class.java)
  }
}
