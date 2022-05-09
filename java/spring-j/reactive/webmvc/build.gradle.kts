val nettyAllVersion: String by ext

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("io.netty:netty-all:${nettyAllVersion}")

    testImplementation("io.projectreactor:reactor-test")
}
