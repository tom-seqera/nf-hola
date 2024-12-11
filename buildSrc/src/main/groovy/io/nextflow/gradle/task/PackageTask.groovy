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
        // TODO manifest

        final buildDir = project.layout.buildDirectory.get()
        archiveExtension = 'zip'
        preserveFileTimestamps = false
        reproducibleFileOrder = true
        outputs.file("${buildDir}/libs/${project.name}-${project.version}.zip")
    }
}
