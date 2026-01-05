package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.r2dbc.postgresql.codec.Json
import org.slf4j.LoggerFactory
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.cases.CaseURN

@WritingConverter
class CaseURNsEncoder : Converter<List<CaseURN>, Json> {
  private val objMapper = ObjectMapper()

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  override fun convert(source: List<CaseURN>): Json {
    log.info("Converting Case URN's to JSON")
    return Json.of(objMapper.writeValueAsString(source))
  }
}

@ReadingConverter
class CaseURNDecoder : Converter<Json, List<CaseURN>> {
  private val objectMapper = ObjectMapper()

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  override fun convert(source: Json): List<CaseURN> {
    log.info("Converting JSON to Case URN's")
    return objectMapper.readValue(source.asString(), object : TypeReference<List<CaseURN>>() {})
  }
}
