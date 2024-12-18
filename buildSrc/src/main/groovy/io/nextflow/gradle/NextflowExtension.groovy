package io.nextflow.gradle

import org.gradle.api.Project

/**
 * A gradle 'extension' to provide configuration to the
 * Nextflow gradle plugin.
 *
 * Usage in 'build.gradle'
 * <pre>
 * nextflow {
 *     requireVersion = '24.10.0'
 *     plugin {
 *         publisher = 'nextflow'
 *         className = 'com.example.ExamplePlugin'
 *     }
 * }
 * </pre>
 */
class NextflowExtension {
    private final Project project

    /**
     * Required nextflow version
     */
    String requireVersion = '24.11.0-edge'

    /**
     * Plugin-specific configuration
     */
    NextflowPluginConfig plugin = new NextflowPluginConfig()

    NextflowExtension(Project project) {
        this.project = project
    }

    // A method with a closure argument is needed to
    // support nested 'config' blocks in gradle.
    def plugin(Closure config) {
        project.configure(plugin, config)

        if (!plugin.className) {
            throw new RuntimeException('nextflow.plugin.className not specified')
        }
        if (plugin.publisher == null) {
            throw new RuntimeException('nextflow.plugin.publisher not specified')
        }
    }

    class NextflowPluginConfig {
        /**
         * Who created/maintains this plugin?
         */
        String publisher

        /**
         * What class should be created when the plugin is loaded?
         */
        String className

        /**
         * Does this plugin require any other plugins to function?
         * (optional)
         */
        List<String> requirePlugins = []
    }
}
