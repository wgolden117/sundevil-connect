plugins {
    id("org.openjfx.javafxplugin") version "0.1.0"
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

dependencies {
    implementation(project(":shared"))
    implementation(project(":server"))
    implementation("org.xerial:sqlite-jdbc:3.45.3.0")
    implementation("io.grpc:grpc-netty:1.63.0")
    implementation("io.grpc:grpc-protobuf:1.63.0")
    implementation("io.grpc:grpc-stub:1.63.0")
}

javafx {
    version = "21"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.graphics")
}

application {
    mainClass.set("ser460.sundevilconnect.client.Main")
}

tasks.shadowJar {
    archiveBaseName.set("client")
    archiveClassifier.set("")
    archiveVersion.set("")

    manifest {
        attributes["Main-Class"] = "ser460.sundevilconnect.client.Main"
    }

    mergeServiceFiles()  // important for gRPC - merges META-INF/services files
}