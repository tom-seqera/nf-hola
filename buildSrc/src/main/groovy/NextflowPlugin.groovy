import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.GroovyPlugin
import org.gradle.api.plugins.JavaLibraryPlugin

class NextflowPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.plugins.apply(GroovyPlugin)
        project.plugins.apply(JavaLibraryPlugin)

        project.repositories {
            mavenLocal()
            mavenCentral()
        }
    }
}
