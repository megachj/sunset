val springDocOpenApiUiVersion: String by ext

dependencies {

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    implementation("org.springdoc:springdoc-openapi-ui:${springDocOpenApiUiVersion}") // swagger
}
