package com.example.learnreactivespring.fluxandmonoplayground

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class FluxAndMonoTest {

    @Test
    fun fluxTest() {
        val stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
//            .concatWith(Flux.error(RuntimeException("Exception Occurred")))
            .concatWith(Flux.just("After Error"))
            .log()

        stringFlux.subscribe({ x: String? -> println(x) }, { x: Throwable? -> System.err.println(x) }
        ) { println("completed") }
    }

    @Test
    fun fluxTestElements_WithoutError() {
        val stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
            .concatWith(Flux.error(RuntimeException("Exception Occurred")))
            .log()

        StepVerifier.create(stringFlux)
            .expectNext("Spring")
            .expectNext("Spring Boot")
            .expectNext("Reactive Spring")
            .expectError(RuntimeException::class.java)
            .verify()
    }

    @Test
    fun fluxTestElementsCount_WithoutError() {
        val stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
            .concatWith(Flux.error(RuntimeException("Exception Occurred")))
            .log()

        StepVerifier.create(stringFlux)
            .expectNextCount(3)
            .expectError(RuntimeException::class.java)
            .verify()
    }

    @Test
    fun fluxTestElements_WithoutError1() {
        val stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
            .concatWith(Flux.error(RuntimeException("Exception Occurred")))
            .log()

        StepVerifier.create(stringFlux)
            .expectNext("Spring", "Spring Boot", "Reactive Spring")
            .expectError(RuntimeException::class.java)
            .verify()
    }

    @Test
    fun monoTest() {
        StepVerifier.create(Mono.error<Any>(RuntimeException("Exception Occurred")))
            .expectError(RuntimeException::class.java)
            .verify()
    }
}
