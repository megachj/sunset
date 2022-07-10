apply(from = "../../docker-build.gradle")

val blockHoundVersion: String by ext

dependencies {
    implementation("org.springframework.data:spring-data-commons")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("io.projectreactor.tools:blockhound:${blockHoundVersion}")

    testImplementation("io.projectreactor:reactor-test")
}
