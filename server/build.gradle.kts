dependencies {
    implementation(project(":shared"))
    implementation("org.xerial:sqlite-jdbc:3.45.3.0")
    implementation("io.grpc:grpc-netty:1.63.0")
    implementation("io.grpc:grpc-protobuf:1.63.0")
    implementation("io.grpc:grpc-stub:1.63.0")
}

plugins {
    application
}

application {
    mainClass.set("ser460.sundevilconnect.server.Main")
}