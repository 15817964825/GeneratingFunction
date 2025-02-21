plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.13.3"
}

group = "com.ayPlugins"
version = "1.0"



// Configure Gradle IntelliJ Plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
intellij {
    version.set("2022.1")
    type.set("IC") // Target IDE Platform
    plugins.set(listOf("com.intellij.java"))
    pluginName.set("AGF") // Plugin Name
}

repositories {
    mavenCentral()
}

dependencies {
    implementation ("org.jetbrains:annotations:23.0.0")
    testImplementation ("junit:junit:4.13.2")
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    patchPluginXml {
        sinceBuild.set("212")
        untilBuild.set("222.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
