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
        final config = project.extensions.nextflowPlugin

        jar.manifest.attributes(
            'Plugin-Id': project.name,
            'Plugin-Version': project.version,
            'Plugin-Requires': config.nextflowVersion
        )
        if (config.className) {
            jar.manifest.attributes('Plugin-Class': config.className)
        }
        if (config.publisher) {
            jar.manifest.attributes('Plugin-Provider': config.publisher)
        }
        if (!config.requirePlugins.isEmpty()) {
            jar.manifest.attributes('Plugin-Dependencies': config.requirePlugins)
        }
    }
}
