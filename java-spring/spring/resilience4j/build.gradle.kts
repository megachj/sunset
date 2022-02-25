val resilience4jVersion: String by ext

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-aop") // annotation 기반 사용시 필요

    implementation("io.github.resilience4j:resilience4j-spring-boot2:${resilience4jVersion}")
    implementation("io.github.resilience4j:resilience4j-reactor:${resilience4jVersion}")
    implementation("io.github.resilience4j:resilience4j-all:${resilience4jVersion}") // Decorators class 로 사용하는 경우 필요
    implementation("io.micrometer:micrometer-registry-prometheus")

    implementation("de.codecentric:chaos-monkey-spring-boot:2.2.0") // 카오스 몽키: 카오스 엔지니어링 테스트용

    testImplementation("io.projectreactor:reactor-test")
}
