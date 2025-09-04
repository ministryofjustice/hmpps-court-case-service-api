plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "9.0.0"
  kotlin("plugin.spring") version "2.2.0"
  jacoco
}

configurations {
  implementation { exclude(module = "spring-boot-starter-web") }
  implementation { exclude(module = "spring-boot-starter-tomcat") }
  testImplementation { exclude(group = "org.junit.vintage") }
}

dependencies {
  implementation("uk.gov.justice.service.hmpps:hmpps-kotlin-spring-boot-starter:1.4.11")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.8.11")
  implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
  implementation("org.postgresql:r2dbc-postgresql")
  implementation("name.nkonev.r2dbc-migrate:r2dbc-migrate-spring-boot-starter:3.3.0")
  runtimeOnly("org.postgresql:postgresql")
  runtimeOnly("org.flywaydb:flyway-database-postgresql:11.9.1")

  testImplementation("uk.gov.justice.service.hmpps:hmpps-kotlin-spring-boot-starter-test:1.4.11")
  testImplementation("org.wiremock:wiremock-standalone:3.13.1")
  testImplementation("io.swagger.parser.v3:swagger-parser:2.1.31") {
    exclude(group = "io.swagger.core.v3")
  }
  testImplementation("io.projectreactor:reactor-test")
  testImplementation("org.testcontainers:postgresql:1.21.3")
}

kotlin {
  jvmToolchain(21)
}

tasks {
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions.jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
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
