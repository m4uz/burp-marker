plugins {
    id("java")
}

group = "m4uz"
version = "0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.portswigger.burp.extensions:montoya-api:2026.4")
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.jar {
    from({
        configurations.runtimeClasspath.get().map {
            if (it.isDirectory) it else zipTree(it)
        }
    })

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.test {
    useJUnitPlatform()
}
