package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import io.r2dbc.postgresql.codec.Json
import org.slf4j.LoggerFactory
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.cases.CaseMarker
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.cases.CaseMarkerWrapper

@WritingConverter
class CaseMarkerEncoder(
  private val objectMapper: ObjectMapper,
) : Converter<List<CaseMarker>, Json> {

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  override fun convert(source: List<CaseMarker>): Json = try {
    log.info("Converting Case Marker to JSON")
    val wrapper = CaseMarkerWrapper(source)
    val json = objectMapper.writeValueAsString(wrapper)
    Json.of(json)
  } catch (ex: JsonProcessingException) {
    throw IllegalArgumentException("Error converting Case Marker list to JSON", ex)
  }
}

@ReadingConverter
class CaseMarkerDecoder(
  private val objectMapper: ObjectMapper,
) : Converter<Json, List<CaseMarker>> {

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  override fun convert(source: Json): List<CaseMarker>? {
    log.info("Converting JSON to Case Marker")
    try {
      val wrapper = objectMapper.readValue(
        source.asString(),
        CaseMarkerWrapper::class.java,
      )
      return wrapper.caseMarkers
    } catch (ex: JsonProcessingException) {
      throw IllegalArgumentException("Error converting JSON to Case Marker list", ex)
    }
  }
}
