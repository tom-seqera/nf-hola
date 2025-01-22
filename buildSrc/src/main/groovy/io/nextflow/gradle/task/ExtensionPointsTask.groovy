package io.nextflow.gradle.task

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * Gradle task to generate extensions.idx file from the list
 * of classnames specified in build.gradle.
 */
class ExtensionPointsTask extends DefaultTask {
    @OutputFile
    final RegularFileProperty outputFile

    ExtensionPointsTask() {
        final buildDir = project.layout.buildDirectory.get()
        outputFile = project.objects.fileProperty()
        outputFile.convention(project.provider {
            buildDir.file("resources/main/META-INF/extensions.idx")
        })
    }

    @TaskAction
    def run() {
        final plugin = project.extensions.nextflowPlugin

        // write the list of extension points from build.gradle
        // to extensions.idx file
        if (plugin.extensionPoints) {
            def index = project.file(outputFile)
            index.parentFile.mkdirs()
            index.text = plugin.extensionPoints.join("\n") + "\n"
        }
    }
}
