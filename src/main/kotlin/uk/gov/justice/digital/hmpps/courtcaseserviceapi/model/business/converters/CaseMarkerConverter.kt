package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters

import com.fasterxml.jackson.databind.ObjectMapper
import io.r2dbc.postgresql.codec.Json
import org.slf4j.LoggerFactory
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.cases.CaseMarker

@WritingConverter
class CaseMarkerEncoder : Converter<CaseMarker, Json> {
  private val objMapper = ObjectMapper()

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  override fun convert(source: CaseMarker): Json {
    log.info("Converting Case Marker to JSON")
    return Json.of(objMapper.writeValueAsString(source))
  }
}

@ReadingConverter
class CaseMarkerDecoder : Converter<Json, CaseMarker> {
  private val objectMapper = ObjectMapper()

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  override fun convert(source: Json): CaseMarker {
    log.info("Converting JSON to Case Marker")
    return objectMapper.readValue(source.asString(), CaseMarker::class.java)
  }
}
