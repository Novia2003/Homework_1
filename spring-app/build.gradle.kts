plugins {
    id("java")
    id("org.springframework.boot") version "3.0.0"
    id("io.spring.dependency-management") version "1.0.13.RELEASE"
    id("jacoco")
    id("checkstyle")
}

checkstyle {
    toolVersion = "10.20.1"
}

group = "ru.tbank"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

val wiremockTestcontainersVersion: String by extra("1.0-alpha-13")

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation(project(":simple-starter"))

    implementation("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.18.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:postgresql:1.20.2")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("org.wiremock:wiremock-standalone:3.6.0")
    testImplementation("org.wiremock.integrations.testcontainers:wiremock-testcontainers-module:$wiremockTestcontainersVersion")
    testImplementation("org.testcontainers:junit-jupiter:1.19.7")

    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.12.3")

    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("io.projectreactor:reactor-core:3.6.10")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.liquibase:liquibase-core")
    runtimeOnly("org.postgresql:postgresql")

    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    implementation("net.logstash.logback:logstash-logback-encoder:8.0")
}

tasks.test {
    useJUnitPlatform()
}

jacoco {
    toolVersion = "0.8.8"
}

val jacocoExclusions = listOf(
    "ru/tbank/dto/**",
    "ru/tbank/model/**",
    "ru/tbank/configuration/**",
    "ru/tbank/auth/**",
    "ru/tbank/entity/**",
    "ru/tbank/pattern/**",
    "ru/tbank/exception/**",
    "ru/tbank/repository/**"
)

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    classDirectories.setFrom(
        files(
            classDirectories.files.map {
                fileTree(it) {
                    exclude(jacocoExclusions)
                }
            }
        )
    )
    reports {
        xml.required.set(true)
        csv.required.set(true)
        html.required.set(true)
    }
}
