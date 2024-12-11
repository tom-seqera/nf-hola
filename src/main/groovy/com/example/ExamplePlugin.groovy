package com.example

import groovy.util.logging.Slf4j
import nextflow.plugin.BasePlugin
import org.pf4j.PluginWrapper

@Slf4j
class ExamplePlugin extends BasePlugin {

    ExamplePlugin(PluginWrapper wrapper) {
        super(wrapper);
        log.info("${this.class.name} plugin initialized")
    }
}
