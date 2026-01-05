package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters

import com.fasterxml.jackson.databind.ObjectMapper
import io.r2dbc.postgresql.codec.Json
import org.slf4j.LoggerFactory
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.offence.Verdict

@WritingConverter
class VerdictEncoder : Converter<Verdict, Json> {
  private val objMapper = ObjectMapper()

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  override fun convert(source: Verdict): Json {
    log.info("Converting Verdict to JSON")
    return Json.of(objMapper.writeValueAsString(source))
  }
}

@ReadingConverter
class VerdictDecoder : Converter<Json, Verdict> {
  private val objectMapper = ObjectMapper()

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  override fun convert(source: Json): Verdict {
    log.info("Converting JSON to Verdict")
    return objectMapper.readValue(source.asString(), Verdict::class.java)
  }
}
