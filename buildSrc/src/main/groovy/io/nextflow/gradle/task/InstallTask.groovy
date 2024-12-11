package io.nextflow.gradle.task

import org.gradle.api.tasks.Copy


/**
 * Gradle task which 'installs' (copies) this Nextflow plugin into the Nextflow plugins dir.
 */
// TODO implement me
class InstallTask extends Copy {
    InstallTask() {
        group = 'Nextflow'
        description = 'Install this plugin into your local Nextflow plugins dir'

        dependsOn project.tasks.assemble
    }
}
