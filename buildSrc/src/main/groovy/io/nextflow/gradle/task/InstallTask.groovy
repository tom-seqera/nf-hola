package io.nextflow.gradle.task

import org.gradle.api.tasks.Copy


/**
 * Gradle task which 'installs' (copies) this Nextflow plugin into
 * the local Nextflow plugins dir.
 */
class InstallTask extends Copy {
    InstallTask() {
        group = 'Nextflow'
        description = 'Install this plugin into your local Nextflow plugins dir'
        dependsOn project.tasks.assemble

        final buildDir = project.layout.buildDirectory.get()
        def (pluginsDir, reason) = getNextflowPluginsDir()

        from(project.zipTree("${buildDir}/distributions/${project.name}-${project.version}.zip"))
        into("${pluginsDir}/${project.name}-${project.version}")

        doLast {
            println "Plugin ${project.name} installed successfully!"
            println "Installation location: $pluginsDir"
            println "Installation location determined by - $reason"
        }
    }

    static def getNextflowPluginsDir() {
        // first look for explicit NXF_PLUGINS_DIR
        def prop = System.getenv('NXF_PLUGINS_DIR')
        if (prop) {
            return [prop, "NXF_PLUGINS_DIR environment variable"]
        }

        // next look for explicit NXF_HOME
        prop = System.getenv('NXF_HOME')
        if (prop) {
            return ["${prop}/plugins", "NXF_HOME environment variable"]
        }

        // otherwise, assume default
        prop = System.getProperty('user.home')
        ["${prop}/.nextflow/plugins", "Default location (~/.nextflow/plugins)"]
    }
}
