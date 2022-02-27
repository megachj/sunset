plugins {
    java
    `java-library`
    id("org.springframework.boot") version "2.4.5"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
    }

    group = "sunset"

    ext {
        set("springAutoRestDocsVersion", "2.0.7")
        set("springDocOpenApiUiVersion", "1.5.2") // Swagger

        set("hibernateValidatorVersion", "7.0.0.Final")
        set("resilience4jVersion", "1.6.1")
    }
}

val jarProject = listOf(rootProject)
configure(jarProject) {
    tasks.bootJar { enabled = false }
    tasks.jar { enabled = true }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library") // dependency api 사용
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    tasks.bootJar { enabled = true }
    tasks.jar { enabled = false }

    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    dependencies {
        compileOnly("org.projectlombok:lombok:1.18.20")
        testCompileOnly("org.projectlombok:lombok:1.18.20")
        annotationProcessor("org.projectlombok:lombok:1.18.20")
        testAnnotationProcessor("org.projectlombok:lombok:1.18.20")

        implementation("org.springframework.boot:spring-boot-starter")

        annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
        testAnnotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

        testImplementation("org.springframework.boot:spring-boot-starter-test")
    }

    tasks {
        "test"(Test::class) {
            useJUnitPlatform()
            testLogging {
                events
            }
        }
    }
}
