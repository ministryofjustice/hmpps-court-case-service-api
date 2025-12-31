package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters

import com.fasterxml.jackson.databind.ObjectMapper
import io.r2dbc.postgresql.codec.Json
import org.slf4j.LoggerFactory
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.court.CourtRoom

@WritingConverter
class CourtRoomEncoder : Converter<CourtRoom, Json> {
  private val objMapper = ObjectMapper()

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  override fun convert(source: CourtRoom): Json {
    log.info("Converting CourtRoom to JSON")
    return Json.of(objMapper.writeValueAsString(source))
  }
}

@ReadingConverter
class CourtRoomDecoder : Converter<Json, CourtRoom> {
  private val objectMapper = ObjectMapper()

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  override fun convert(source: Json): CourtRoom {
    log.info("Converting JSON to CourtRoom")
    return objectMapper.readValue(source.asString(), CourtRoom::class.java)
  }
}
