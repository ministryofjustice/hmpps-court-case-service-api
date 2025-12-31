package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters

import com.fasterxml.jackson.databind.ObjectMapper
import io.r2dbc.postgresql.codec.Json
import org.slf4j.LoggerFactory
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.offence.Plea

@WritingConverter
class PleaEncoder : Converter<Plea, Json> {
  private val objMapper = ObjectMapper()

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  override fun convert(source: Plea): Json {
    log.info("Converting Plea to JSON")
    return Json.of(objMapper.writeValueAsString(source))
  }
}

@ReadingConverter
class PleaDecoder : Converter<Json, Plea> {
  private val objectMapper = ObjectMapper()

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  override fun convert(source: Json): Plea {
    log.info("Converting JSON to Plea")
    return objectMapper.readValue(source.asString(), Plea::class.java)
  }
}
