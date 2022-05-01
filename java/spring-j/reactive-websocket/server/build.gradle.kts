apply(from = "./docker-custom-build.gradle")

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    testImplementation("io.projectreactor:reactor-test")
}
