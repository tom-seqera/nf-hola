package io.nextflow.gradle

import io.nextflow.gradle.task.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.GroovyPlugin
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.tasks.compile.GroovyCompile

class NextflowPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        // TODO read from 'build.gradle'
        final nextflowVersion = '24.11.0-edge'
        project.version = '0.0.1'

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

        project.dependencies {
            // required compile-time dependencies for nextflow plugins
            compileOnly "io.nextflow:nextflow:${nextflowVersion}"
            compileOnly 'org.slf4j:slf4j-api:1.7.10'
            compileOnly 'org.pf4j:pf4j:3.4.1'
        }

        // -----------------------------
        // Custom tasks
        // -----------------------------
        project.tasks.register('packagePlugin', PackageTask)
        project.tasks.assemble.dependsOn << project.tasks.packagePlugin

        project.tasks.register('generateMeta', MetadataTask)

        project.tasks.register('installPlugin', InstallTask)
    }
}
