plugins {
    `java-library`
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("io.debezium:debezium-api:1.5.0.Final")
    implementation("org.apache.kafka:connect-api:2.7.0")
}
