package io.nextflow.gradle

import io.nextflow.gradle.task.ExtensionPointsTask
import io.nextflow.gradle.task.InstallTask
import io.nextflow.gradle.task.Manifest
import io.nextflow.gradle.task.MetadataTask
import io.nextflow.gradle.task.PackageTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.GroovyPlugin
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.tasks.compile.GroovyCompile
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.tasks.Jar

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

        project.tasks.withType(JavaCompile).each { task ->
            task.sourceCompatibility = 17
            task.targetCompatibility = 17
        }
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
        // Custom jar manifest
        // -----------------------------
        project.afterEvaluate {
            def manifest = new Manifest(project)
            project.tasks.withType(Jar).each(manifest::manifest)
        }

        // -----------------------------
        // Custom tasks
        // -----------------------------
        // extensionPoints - generates extensions.idx file
        project.tasks.register('extensionPoints', ExtensionPointsTask)
        project.tasks.jar.dependsOn << project.tasks.extensionPoints

        // packagePlugin - builds the zip file
        project.tasks.register('packagePlugin', PackageTask)
        project.tasks.packagePlugin.dependsOn << [
            project.tasks.extensionPoints,
            project.tasks.classes
        ]
        project.tasks.assemble.dependsOn << project.tasks.packagePlugin

        // generateMeta - creates the meta.json file
        project.tasks.register('generateMeta', MetadataTask)
        project.tasks.generateMeta.dependsOn << project.tasks.packagePlugin
        // TODO should be part of publish, not assemble
        project.tasks.assemble.dependsOn << project.tasks.generateMeta

        // installPlugin - installs plugin to (local) nextflow plugins dir
        project.tasks.register('installPlugin', InstallTask)
        project.tasks.installPlugin.dependsOn << project.tasks.assemble
    }
}
