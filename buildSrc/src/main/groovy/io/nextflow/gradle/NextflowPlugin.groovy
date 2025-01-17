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
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.tasks.Jar
import org.gradle.jvm.toolchain.JavaLanguageVersion

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

        project.java {
            toolchain.languageVersion = JavaLanguageVersion.of(21)
            sourceCompatibility = 17    // javac --source
            targetCompatibility = 17    // javac --target
        }
        project.tasks.withType(JavaCompile).configureEach {
            options.release = 17        // javac --release
        }

        // -----------------------------
        // Common dependencies
        // -----------------------------
        project.repositories {
            mavenLocal()
            mavenCentral()
        }

        project.afterEvaluate {
            final nextflowVersion = extension.nextflowVersion

            project.dependencies {
                // required compile-time dependencies for nextflow plugins
                compileOnly "io.nextflow:nextflow:${nextflowVersion}"
                compileOnly "org.slf4j:slf4j-api:1.7.10"
                compileOnly "org.pf4j:pf4j:3.4.1"

                // see https://docs.gradle.org/4.1/userguide/dependency_management.html#sec:module_replacement
                modules {
                    module("commons-logging:commons-logging") { replacedBy("org.slf4j:jcl-over-slf4j") }
                }

                // test-only dependencies (for writing tests)
                testImplementation "org.apache.groovy:groovy:4.0.18"
                testImplementation "io.nextflow:nextflow:${nextflowVersion}"
                testImplementation("org.spockframework:spock-core:2.3-groovy-4.0") {
                    exclude group: 'org.codehaus.groovy';
                    exclude group: 'net.bytebuddy'
                }
                testImplementation('org.spockframework:spock-junit4:2.3-groovy-4.0') {
                    exclude group: 'org.codehaus.groovy';
                    exclude group: 'net.bytebuddy'
                }
                testImplementation(testFixtures("io.nextflow:nextflow:${nextflowVersion}"))
                testImplementation(testFixtures("io.nextflow:nf-commons:${nextflowVersion}"))
            }
        }
        // use JUnit 5 platform
        project.test.useJUnitPlatform()

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
        project.tasks.compileTestGroovy.dependsOn << project.tasks.extensionPoints

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
