package io.nextflow.gradle.task

import org.gradle.api.tasks.bundling.Zip

/**
 * Gradle task to package a Nextflow plugin
 */
class PackageTask extends Zip {

    PackageTask() {
        group = 'Nextflow Plugin'
        description = 'Package up this Nextflow plugin for deployment'

        into('classes') { with project.tasks.jar }
        into('lib') { from project.configurations.runtimeClasspath }
        preserveFileTimestamps = false
        reproducibleFileOrder = true
    }
}
