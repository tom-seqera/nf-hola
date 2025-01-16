package io.nextflow.gradle.task

import groovy.json.JsonOutput
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

import java.security.MessageDigest
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

/**
 * Gradle task to create the Nextflow plugin metadata file
 */
class MetadataTask extends DefaultTask {
    @InputFile
    final RegularFileProperty inputFile
    @OutputFile
    final RegularFileProperty outputFile

    MetadataTask() {
        group = 'Nextflow'

        final buildDir = project.layout.buildDirectory.get()
        inputFile = project.objects.fileProperty()
        inputFile.convention(project.provider {
            buildDir.file("distributions/${project.name}-${project.version}.zip")
        })

        outputFile = project.objects.fileProperty()
        outputFile.convention(project.provider {
            buildDir.file("distributions/${project.name}-${project.version}-meta.json")
        })
    }

    @TaskAction
    void run() {
        // TODO make configurable
        final githubOrg = 'nexflow-io'

        final extension = project.extensions.nextflow
        final metadata = [
            version  : "${project.version}",
            date     : OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
            url      : "https://github.com/${githubOrg}/${project.name}/releases/download/${project.version}/${project.name}-${project.version}.zip",
            requires : ">=${extension.requireVersion}",
            sha512sum: computeSha512(project.file(inputFile))
        ]
        project.file(outputFile).text = JsonOutput.prettyPrint(JsonOutput.toJson(metadata))
    }

    private static String computeSha512(File file) {
        if (!file.exists())
            throw new GradleException("Missing file: $file -- cannot compute SHA-512")

        MessageDigest.getInstance("SHA-512").digest(file.bytes).encodeHex().toString()
    }
}
