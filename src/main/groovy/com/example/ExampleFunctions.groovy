package com.example

import nextflow.Session
import nextflow.plugin.extension.Function
import nextflow.plugin.extension.PluginExtensionPoint

class ExampleFunctions extends PluginExtensionPoint {
    private Session session

    @Override
    protected void init(Session session) {
        this.session = session
    }

    @Function
    String message() {
        "Hello"
    }
}
