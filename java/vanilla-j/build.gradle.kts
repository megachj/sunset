plugins {
    java
    `java-library`
}

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
    }

    group = "sunset"

    ext {
    }
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")

    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    dependencies {
        compileOnly("org.projectlombok:lombok:1.18.20")
        testCompileOnly("org.projectlombok:lombok:1.18.20")
        annotationProcessor("org.projectlombok:lombok:1.18.20")
        testAnnotationProcessor("org.projectlombok:lombok:1.18.20")

        testImplementation(platform("org.junit:junit-bom:5.7.1"))
        testImplementation("org.junit.jupiter:junit-jupiter")
    }

    tasks {
        jar {
            enabled = true
        }

        "test"(Test::class) {
            useJUnitPlatform()
            testLogging {
                events
            }
        }
    }
}
