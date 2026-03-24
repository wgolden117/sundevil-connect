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