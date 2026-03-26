plugins {
    id("java")
}

group = "ser460"
version = "1.0-SNAPSHOT"

subprojects {
    apply(plugin = "java")

    group = "ser460"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    dependencies {
        testImplementation(platform("org.junit:junit-bom:5.10.0"))
        testImplementation("org.junit.jupiter:junit-jupiter")
    }

    tasks.test {
        useJUnitPlatform()
    }
}

tasks.register("runAll") {
    group = "application"
    description = "Starts the server and client as separate processes"
    dependsOn(":server:classes", ":client:classes")

    doLast {
        val isWindows = System.getProperty("os.name").lowercase().contains("windows")
        println("Windows: $isWindows")
        val gradlew = if (isWindows) "gradlew.bat" else "./gradlew"

        val serverProcess = ProcessBuilder(gradlew, ":server:run")
            .inheritIO()
            .start()

        Thread.sleep(2000)

        val clientProcess = ProcessBuilder(gradlew, ":client:run")
            .inheritIO()
            .start()

        clientProcess.waitFor()
        serverProcess.destroy()
    }
}