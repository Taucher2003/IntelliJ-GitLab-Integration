import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.Optional

plugins {
    id("org.jetbrains.intellij") version "1.1.6"
    id("org.kordamp.gradle.markdown") version "2.2.0"
    java
}

group = "com.gitlab.taucher2003"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.2")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version.set("2021.2.1")
}

tasks.register<Copy>("copyChangelog") {
    from("CHANGELOG.md")
    into("src/markdown")
}

tasks.register<Delete>("deleteCopiedChangelog") {
    dependsOn(tasks.getByName<Copy>("copyChangelog"))
    delete("src/markdown/CHANGELOG.md")
    delete("src/markdown")
}

abstract class PublishChangelogTask : DefaultTask() {
    @TaskAction
    fun action() {
        val projectId = System.getProperty("project_id")
        val token = System.getProperty("token")
        val tokenType = System.getProperty("token_type", "Job-Token")
        val tag = System.getProperty("tag")
        val client = HttpClient.newBuilder().build();
        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://gitlab.com/api/v4/projects/$projectId/repository/changelog"))
            .POST(HttpRequest.BodyPublishers.ofString("""{"version":"$tag"}"""))
            .header(tokenType, token)
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.discarding())
        if(response.statusCode() != 200) {
            throw Exception("Changelog creation failed with code ${response.statusCode()}")
        }
    }
}

tasks.register<PublishChangelogTask>("publishChangelog")

tasks {
    patchPluginXml {
        dependsOn(markdownToHtml)
        readChangelog().ifPresent { changeNotes.set(it) }
    }
    build {
        dependsOn(patchPluginXml)
        dependsOn(getByName<Delete>("deleteCopiedChangelog"))
    }
    test {
        useJUnitPlatform()
    }
    markdownToHtml {
        dependsOn(getByName<Copy>("copyChangelog"))
    }
}

fun readChangelog(): Optional<String> {
    val file = File("build/gen-html/CHANGELOG.html")
    if(file.exists()) {
        val fileContent = file.inputStream().readBytes().toString(Charsets.UTF_8)
        return Optional.of(fileContent)
    }
    return Optional.empty()
}
