/*
 * Copyright 2021 Niklas van Schrick and the IntelliJ GitLab Integration contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.5")
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.0")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version.set("2021.2.1")
    plugins.add("Git4Idea")
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
        val client = HttpClient.newBuilder().build()
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
        finalizedBy(getByName<Delete>("deleteCopiedChangelog"))
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
