package io.nextflow.gradle

import io.nextflow.gradle.task.InstallTask
import io.nextflow.gradle.task.MetadataTask
import io.nextflow.gradle.task.PackageTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.GroovyPlugin
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.tasks.compile.GroovyCompile

/**
 * A gradle plugin for nextflow plugin projects.
 *
 * Sets some standard build configuration and adds
 * some plugin-specific tasks.
 */
class NextflowPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        final extension = project.extensions.create('nextflow', NextflowExtension)

        // -----------------------------
        // Java/Groovy config
        // -----------------------------
        project.plugins.apply(GroovyPlugin)
        project.plugins.apply(JavaLibraryPlugin)

        project.tasks.withType(GroovyCompile).each { task ->
            task.sourceCompatibility = 17
            task.targetCompatibility = 17
        }

        // -----------------------------
        // Common dependencies
        // -----------------------------
        project.repositories {
            mavenLocal()
            mavenCentral()
        }

        project.afterEvaluate {
            project.dependencies {
                // required compile-time dependencies for nextflow plugins
                compileOnly "io.nextflow:nextflow:${extension.requireVersion}"
                compileOnly 'org.slf4j:slf4j-api:1.7.10'
                compileOnly 'org.pf4j:pf4j:3.4.1'
            }
        }

        // -----------------------------
        // Custom tasks
        // -----------------------------
        // packagePlugin - builds the zip file
        project.tasks.register('packagePlugin', PackageTask)
        project.tasks.assemble.dependsOn << project.tasks.packagePlugin

        // generateMeta - creates the meta.json file
        project.tasks.register('generateMeta', MetadataTask)
        // TODO should be part of publish, not assemble
        project.tasks.assemble.dependsOn << project.tasks.generateMeta

        // installPlugin - installs plugin to (local) nextflow plugins dir
        project.tasks.register('installPlugin', InstallTask)
    }
}
