plugins {
    id("java")
    id("org.springframework.boot") version "3.0.0"
    id("io.spring.dependency-management") version "1.0.13.RELEASE"
    id("jacoco")
    id("checkstyle")
}

group = "ru.tbank"
version = "unspecified"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

checkstyle {
    toolVersion = "10.20.1"
}


val wiremockTestcontainersVersion: String by extra("1.0-alpha-13")

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation(project(":simple-starter"))

    implementation("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testImplementation("io.rest-assured:rest-assured")
    testImplementation("org.wiremock:wiremock-standalone:3.6.0")
    testImplementation("org.wiremock.integrations.testcontainers:wiremock-testcontainers-module:$wiremockTestcontainersVersion")
    testImplementation("org.testcontainers:junit-jupiter:1.19.7")

    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("io.github.resilience4j:resilience4j-spring-boot3:2.2.0")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")

    implementation("org.springframework.boot:spring-boot-starter-cache")


    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.12.3")
}

tasks.test {
    useJUnitPlatform()
}

jacoco {
    toolVersion = "0.8.8"
}

val jacocoExclusions = listOf(
    "ru/tbank/dto/**",
    "ru/tbank/configuration/**",
    "ru/tbank/exception/**"
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