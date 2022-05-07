apply(from = "../../docker-build.gradle")

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    testImplementation("io.projectreactor:reactor-test")
}
