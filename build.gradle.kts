plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "9.3.0"
  id("io.freefair.lombok") version "9.1.0"
  kotlin("plugin.spring") version "2.3.0"
  jacoco
}

configurations {
  implementation { exclude(module = "spring-boot-starter-web") }
  implementation { exclude(module = "spring-boot-starter-tomcat") }
  testImplementation { exclude(group = "org.junit.vintage") }
}

dependencies {
  implementation("uk.gov.justice.service.hmpps:hmpps-kotlin-spring-boot-starter:1.8.2") {
    exclude(module = "spring-boot-starter-oauth2-resource-server")
    exclude(module = "spring-boot-starter-oauth2-client")
  }
  implementation("org.springframework.security:spring-security-oauth2-resource-server:7.0.2")
  implementation("name.nkonev.r2dbc-migrate:r2dbc-migrate-spring-boot-starter:3.3.0")
  implementation("org.springframework.security:spring-security-oauth2-client:7.0.2")
  implementation("uk.gov.justice.service.hmpps:hmpps-sqs-spring-boot-starter:5.6.3")
  implementation("org.springframework.security:spring-security-oauth2-jose:7.0.2")
  implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:3.0.0")
  implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("com.fasterxml.uuid:java-uuid-generator:5.2.0")
  implementation("org.postgresql:r2dbc-postgresql")
  runtimeOnly("org.postgresql:postgresql")

  testImplementation("uk.gov.justice.service.hmpps:hmpps-kotlin-spring-boot-starter-test:1.8.2")
  testImplementation("io.swagger.parser.v3:swagger-parser:2.1.35") {
    exclude(group = "io.swagger.core.v3")
  }
  testImplementation("org.springframework.boot:spring-boot-starter-data-r2dbc-test:4.0.1")
  testImplementation("org.springframework.boot:spring-boot-starter-security-test:4.0.1")
  testImplementation("org.mockito.kotlin:mockito-kotlin:6.1.0")
  testImplementation("org.wiremock:wiremock-standalone:3.13.1")
  testImplementation("org.testcontainers:postgresql:1.21.3")
  testImplementation("io.projectreactor:reactor-test")
}

kotlin {
  jvmToolchain(25)
  compilerOptions {
    freeCompilerArgs.addAll("-Xannotation-default-target=param-property")
  }
}

tasks {
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions.jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25
  }
}

jacoco {
  toolVersion = "0.8.13"
}

tasks.jacocoTestCoverageVerification {
  violationRules {
    rule {
      isEnabled = true
      element = "CLASS"
      includes = listOf("uk.gov.justice.probation.*")
      excludes = listOf("uk.gov.justice.probation.courtcaseservice.jpa.repository.*")

      limit {
        counter = "LINE"
        value = "TOTALCOUNT"
        minimum = "0.8".toBigDecimal()
      }
      limit {
        counter = "METHOD"
        value = "TOTALCOUNT"
        minimum = "0.8".toBigDecimal()
      }
    }
  }
}
