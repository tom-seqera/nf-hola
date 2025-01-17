package com.example

import nextflow.Session
import spock.lang.Specification

class ExampleFunctionsTest extends Specification {

    def 'should return greeting'() {
        given:
        def example = new ExampleFunctions()
        example.init(new Session([:]))

        when:
        def result = example.message()
        then:
        result == "Hello"
    }
}