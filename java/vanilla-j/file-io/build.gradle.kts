plugins {
    id("application")
}

application {
    mainClass.set("com.sunset.Main")
}

tasks.jar {
    // 실행할 main class 지정
    manifest {
        attributes["Main-Class"] = "com.sunset.Main"
    }
    // 외부 libs 를 jar 파일 안에 추가
    from(
        configurations.compileClasspath.map { config ->
            config.map { if (it.isDirectory) it else zipTree(it) }
        }
    )
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

sourceSets {
    main {
        resources {
            srcDirs("src/main/data")
        }
    }
}

dependencies {

    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.0")
}
