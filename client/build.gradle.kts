plugins {
    id("org.openjfx.javafxplugin") version "0.1.0"
}

dependencies {
    implementation(project(":shared"))

}

javafx {
    version = "21"
    modules = listOf("javafx.controls", "javafx.fxml")
}
