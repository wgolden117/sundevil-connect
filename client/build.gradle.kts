plugins {
    id("org.openjfx.javafxplugin") version "0.1.0"
    application
}

dependencies {
    implementation(project(":shared"))
    implementation("io.grpc:grpc-netty:1.63.0")
    implementation("io.grpc:grpc-protobuf:1.63.0")
    implementation("io.grpc:grpc-stub:1.63.0")
}

javafx {
    version = "21"
    modules = listOf("javafx.controls", "javafx.fxml")
}

application {
    mainClass.set("ser460.sundevilconnect.client.Main")
}
