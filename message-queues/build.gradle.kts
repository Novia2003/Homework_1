plugins {
    id("java")
    id("me.champeau.jmh") version "0.7.2"
}

group = "ru.tbank"
version = "unspecified"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.kafka:kafka-clients:3.6.0")

    implementation("com.rabbitmq:amqp-client:5.18.0")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}
