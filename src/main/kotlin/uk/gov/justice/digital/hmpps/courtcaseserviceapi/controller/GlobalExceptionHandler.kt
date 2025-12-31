package uk.gov.justice.digital.hmpps.courtcaseserviceapi.controller

import jakarta.validation.ValidationException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.reactive.resource.NoResourceFoundException
import reactor.core.publisher.Mono

@RestControllerAdvice
class GlobalExceptionHandler {

  private companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  @ExceptionHandler(ValidationException::class)
  fun handleValidationException(ex: ValidationException): Mono<ResponseEntity<ProblemDetail>> {
    log.info("Validation exception: {}", ex.message)

    val problemDetail = ProblemDetail.forStatusAndDetail(
      HttpStatus.BAD_REQUEST,
      ex.message,
    )
    problemDetail.title = "Validation Error"
    return Mono.just(ResponseEntity(problemDetail, HttpStatus.BAD_REQUEST))
  }

  @ExceptionHandler(NoResourceFoundException::class)
  fun handleNoResourceFoundException(ex: NoResourceFoundException): Mono<ResponseEntity<ProblemDetail>> {
    log.info("No resource found exception: {}", ex.message)

    val problemDetail = ProblemDetail.forStatusAndDetail(
      HttpStatus.NOT_FOUND,
      ex.message,
    )
    problemDetail.title = "Resource Not Found"
    return Mono.just(ResponseEntity(problemDetail, HttpStatus.NOT_FOUND))
  }

  @ExceptionHandler(AccessDeniedException::class)
  fun handleAccessDeniedException(ex: AccessDeniedException): Mono<ResponseEntity<ProblemDetail>> {
    log.debug("Forbidden (403) returned: {}", ex.message)

    val problemDetail = ProblemDetail.forStatusAndDetail(
      HttpStatus.FORBIDDEN,
      ex.message,
    )
    problemDetail.title = "Access Denied"
    return Mono.just(ResponseEntity(problemDetail, HttpStatus.FORBIDDEN))
  }

  @ExceptionHandler(Exception::class)
  fun handleGenericException(ex: Exception): Mono<ResponseEntity<ProblemDetail>> {
    log.error("Unexpected exception", ex)

    val problemDetail = ProblemDetail.forStatusAndDetail(
      HttpStatus.INTERNAL_SERVER_ERROR,
      "An unexpected error occurred",
    )
    problemDetail.title = "Internal Server Error"
    return Mono.just(ResponseEntity(problemDetail, HttpStatus.INTERNAL_SERVER_ERROR))
  }
}
