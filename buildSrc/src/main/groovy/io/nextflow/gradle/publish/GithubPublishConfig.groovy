package io.nextflow.gradle.publish

import groovy.transform.CompileStatic
import org.gradle.api.Project

@CompileStatic
class GithubPublishConfig {
    private final Project project

    /**
     * Github repository to upload to (eg. 'nextflow-io/nf-hello')
     */
    String repository

    /**
     * Github username
     */
    String userName

    /**
     * Github email address
     */
    String email

    /**
     * Github authentication token
     */
    String authToken

    /**
     * Overwrite existing files in the release?
     */
    boolean overwrite = false

    GithubPublishConfig(Project project) {
        this.project = project
    }

    // split the 'repository' string into (github_org, repo)
    def repositoryParts() {
        final parts = repository.tokenize('/')
        if (parts.size() != 2) {
            throw new RuntimeException("nextflow.github.repository should be of form '{github_org}/{repo}', eg 'nextflow-io/nf-hello'")
        }
        return parts
    }

    // the url the published plugin should have
    def publishedUrl() {
        final fileName = "${project.name}-${project.version}.zip"
        return "https://github.com/${repository}/releases/download/${project.version}/${fileName}"
    }
}
