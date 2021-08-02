package com.example.learnreactivespring.fluxandmonoplayground

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.function.Supplier

class FluxAndMonoFactoryTest {

    val names = mutableListOf<String>("adam", "anna", "jack", "jenny")

    @Test
    fun fluxUsingIterable() {
        val namesFLux = Flux.fromIterable(names)
            .log()

        StepVerifier.create(namesFLux)
            .expectNext("adam", "anna", "jack", "jenny")
            .verifyComplete()
    }

    @Test
    fun fluxUsingArray() {
        val names = arrayOf("adam", "anna", "jack", "jenny")
        val nameFlux = Flux.fromArray(names)
        StepVerifier.create(nameFlux)
            .expectNext("adam", "anna", "jack", "jenny")
            .verifyComplete()
    }

    @Test
    fun fluxUsingStream() {
        val nameFlux = Flux.fromStream(names.stream())
        StepVerifier.create(nameFlux)
            .expectNext("adam", "anna", "jack", "jenny")
            .verifyComplete()
    }

    @Test
    fun monoUsingJustOrEmpty() {
        val mono = Mono.justOrEmpty<String>(null)

        StepVerifier.create(mono.log())
            .verifyComplete()
    }

    @Test
    fun monoUsingSupplier() {
        val stringSupplier = Supplier { "adam" }

        val stringMono = Mono.fromSupplier(stringSupplier)

        println(stringSupplier.get())

        StepVerifier.create(stringMono.log())
            .expectNext("adam")
            .verifyComplete()
    }

    @Test
    fun fluxUsingRange() {
         val integerFlux = Flux.range(1, 5).log()

        StepVerifier.create(integerFlux)
            .expectNext(1, 2, 3, 4, 5)
            .verifyComplete()
    }
}