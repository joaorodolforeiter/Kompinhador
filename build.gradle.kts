plugins {
    kotlin("jvm") version "2.2.0"
    id("com.gradleup.shadow") version "8.3.0"
    application
    antlr
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.formdev:flatlaf:3.6")
    implementation("com.fifesoft:rsyntaxtextarea:3.6.0")
    implementation("org.antlr:antlr4-runtime:4.13.1")

    antlr("org.antlr:antlr4:4.5")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin { jvmToolchain(17) }

tasks.withType<JavaExec> {
    jvmArgs("--enable-native-access=ALL-UNNAMED")
}

tasks.compileKotlin {
    dependsOn(tasks.generateGrammarSource)
}

tasks.shadowJar {
    archiveClassifier.set("all")
    manifest {
        attributes["Main-Class"] = "compiler.MainKt"
    }
}

application {
    mainClass.set("compiler.MainKt")
}
