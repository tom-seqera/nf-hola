package io.nextflow.gradle.task


import org.gradle.jvm.tasks.Jar

/**
 * Gradle task to package a Nextflow plugin
 */
class PackageTask extends Jar {

    PackageTask() {
        group = 'Nextflow'
        description = 'Package up this Nextflow plugin for deployment'

        dependsOn project.tasks.classes

        into('classes') { with project.tasks.jar }
        into('lib') { from project.configurations.runtimeClasspath }
        archiveExtension = 'zip'
        preserveFileTimestamps = false
        reproducibleFileOrder = true

        // automatically add necessary attributes to the MANIFEST.MF file
        project.afterEvaluate {
            final nf = project.extensions.nextflow
            final plugin = nf.plugin

            manifest.attributes(
                'Plugin-Id': project.name,
                'Plugin-Version': project.version,
                'Plugin-Requires': nf.requireVersion
            )
            if (plugin.className) {
                manifest.attributes('Plugin-Class': plugin.className)
            }
            if (plugin.publisher) {
                manifest.attributes('Plugin-Provider': plugin.publisher)
            }
            if (!plugin.requirePlugins.isEmpty()) {
                manifest.attributes('Plugin-Dependencies': plugin.requirePlugins)
            }
        }
    }
}
