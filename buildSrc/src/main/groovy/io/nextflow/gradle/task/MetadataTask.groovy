package io.nextflow.gradle.task

import groovy.json.JsonOutput
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

import java.security.MessageDigest
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

/**
 * Gradle task to create the Nextflow plugin metadata file
 */
class MetadataTask extends DefaultTask {
    private final String zipPath
    private final String metaPath

    MetadataTask() {
        group = 'Nextflow'
        dependsOn project.tasks.packagePlugin

        // define inputs and outputs
        final buildDir = project.layout.buildDirectory.get()
        zipPath = "${buildDir}/libs/${project.name}-${project.version}.zip"
        metaPath = "$buildDir/libs/${project.name}-${project.version}-meta.json"

        inputs.file(zipPath)
        outputs.file(metaPath)
    }

    @TaskAction
    void run() {
        final metadata = [
                version: "${project.version}",
                date: OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                url: "TODO url",
                requires: "TODO requires",
                sha512sum: computeSha512(project.file(zipPath))
        ]
        project.file(metaPath).text = JsonOutput.prettyPrint(JsonOutput.toJson(metadata))
    }

    private static String computeSha512(File file) {
        if (!file.exists())
            throw new GradleException("Missing file: $file -- cannot compute SHA-512")

        MessageDigest.getInstance("SHA-512").digest(file.bytes).encodeHex().toString()
    }
}
