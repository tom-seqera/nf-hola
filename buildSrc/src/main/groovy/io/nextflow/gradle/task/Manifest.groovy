package io.nextflow.gradle.task

import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar

/**
 * Utility class to customise manifest file of
 * Gradle jar task
 */
class Manifest {
    private final Project project

    Manifest(Project project) {
        this.project = project
    }

    void manifest(Jar jar) {
        final nf = project.extensions.nextflow
        final plugin = nf.plugin

        jar.manifest.attributes(
            'Plugin-Id': project.name,
            'Plugin-Version': project.version,
            'Plugin-Requires': nf.requireVersion
        )
        if (plugin.className) {
            jar.manifest.attributes('Plugin-Class': plugin.className)
        }
        if (plugin.publisher) {
            jar.manifest.attributes('Plugin-Provider': plugin.publisher)
        }
        if (!plugin.requirePlugins.isEmpty()) {
            jar.manifest.attributes('Plugin-Dependencies': plugin.requirePlugins)
        }
    }
}
