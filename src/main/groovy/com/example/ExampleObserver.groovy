package com.example

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import nextflow.Session
import nextflow.trace.TraceObserver
import nextflow.trace.TraceObserverFactory

@CompileStatic
class ExampleObserver implements TraceObserverFactory {
    @Override
    Collection<TraceObserver> create(Session session) {
        return List.<TraceObserver>of(new Observer())
    }

    @Slf4j
    static class Observer implements TraceObserver {
        @Override
        void onFlowCreate(Session session) {
            log.info "Pipeline is starting! 🚀"
        }

        @Override
        void onFlowComplete() {
            log.info "Pipeline complete! 👋"
        }
    }
}
