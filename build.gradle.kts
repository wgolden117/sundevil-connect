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

        // wait for SERVER_READY signal before launching client
        val reader = serverProcess.inputStream.bufferedReader()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            println(line) // still print server output
            if (line!!.contains("SERVER_READY")) break
        }

        val clientProcess = ProcessBuilder(gradlew, ":client:run")
            .inheritIO()
            .start()

        try {
            clientProcess.waitFor()
        } catch (e: InterruptedException) {
            // client was interrupted, still kill server
        } finally {
            // tell all gradle daemons to shut down gracefully
            // which will shut down the server. This keeps the server's
            // JVM from being orphaned
            ProcessBuilder(gradlew, "--stop")
                .inheritIO()
                .start()
                .waitFor()
        }
    }
}