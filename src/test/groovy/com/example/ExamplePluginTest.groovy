package com.example

import nextflow.plugin.extension.PluginExtensionProvider
import test.Dsl2Spec

class ExamplePluginTest extends Dsl2Spec {
    def setup() {
        PluginExtensionProvider.reset()
    }
}
