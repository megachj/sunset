plugins {
    java
    `java-library`
    id("org.springframework.boot") version "2.6.4"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
    }

    group = "sunset"

    ext {
        set("nettyVersion", "4.1.74.Final")
    }
}

val nonLeafNodeProject = listOf(rootProject, subprojects.filter { it.subprojects.isNotEmpty() })
configure(nonLeafNodeProject) {
    tasks.bootJar { enabled = false }
    tasks.jar { enabled = false }
}

val javaProject = setOf("echo-server", "echo-client")
val leafNodeJavaProject = subprojects.filter { it.subprojects.isEmpty() && javaProject.contains(it.name) }
val leafNodeSpringProject = subprojects.filter { it.subprojects.isEmpty() && !javaProject.contains(it.name) }

configure(leafNodeJavaProject) {
    apply(plugin = "java")

    tasks.jar { enabled = true }

    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    dependencies {
        compileOnly("org.projectlombok:lombok:1.18.20")
        testCompileOnly("org.projectlombok:lombok:1.18.20")
        annotationProcessor("org.projectlombok:lombok:1.18.20")
        testAnnotationProcessor("org.projectlombok:lombok:1.18.20")
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

configure(leafNodeSpringProject) {
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
