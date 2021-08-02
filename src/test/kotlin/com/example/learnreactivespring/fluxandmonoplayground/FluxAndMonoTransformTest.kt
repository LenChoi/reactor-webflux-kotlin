package com.example.learnreactivespring.fluxandmonoplayground

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers.parallel
import reactor.test.StepVerifier

class FluxAndMonoTransformTest {

    val names = mutableListOf<String>("adam", "anna", "jack", "jenny")

    @Test
    fun transformUsingMap() {
        val namesFlux = Flux.fromIterable(names)
            .map{ it.uppercase() }
            .log()

        StepVerifier.create(namesFlux)
            .expectNext("ADAM", "ANNA", "JACK", "JENNY")
            .verifyComplete()
    }

    @Test
    fun transformUsingMap_Length() {
        val namesFlux = Flux.fromIterable(names)
            .map{ it.length }
            .repeat(1)
            .log()

        StepVerifier.create(namesFlux)
            .expectNext(4, 4, 4, 5)
            .expectNext(4, 4, 4, 5)
            .verifyComplete()
    }

    @Test
    fun transformUsingMap_Filter() {
        val namesFlux = Flux.fromIterable(names)
            .filter{ it.length > 4 }
            .map{it.uppercase()}
            .log()

        StepVerifier.create(namesFlux)
            .expectNext("JENNY")
            .verifyComplete()
    }

    @Test
    fun transformUsingFlatMap() {
        val stringFlux = Flux.fromIterable(mutableListOf("A", "B", "C", "D", "E", "F")) // A, B, C, ...
            .flatMap{
                Flux.fromIterable(convertToList(it)) // A -> List[A, newValue], B -> List[B, newValue]
            } //db or external service call that returns a flux -> s -> Flux<String>
            .log()

        StepVerifier.create(stringFlux)
            .expectNextCount(12)
            .verifyComplete()
    }

    @Test
    fun transformUsingFlatMap_usingParallel() {
        val stringFlux = Flux.fromIterable(mutableListOf("A", "B", "C", "D", "E", "F")) // Flux<String>
            .window(2) // 2개의 엘리먼트를 기다림, Flux<Flux<String> -> (A, B), (C, D), (E, F)
            .flatMap{
                it.map {
                    convertToList(it)
                }.subscribeOn(parallel())// Flux<List<String>
            }
            .flatMap{
                Flux.fromIterable(it) //Flux<String>
            }//db or external service call that returns a flux -> s -> Flux<String>
            .log()

        StepVerifier.create(stringFlux)
            .expectNextCount(12)
            .verifyComplete()
    }

    @Test
    fun transformUsingFlatMap_parallel_maintain_order() {
        val stringFlux = Flux.fromIterable(mutableListOf("A", "B", "C", "D", "E", "F")) // Flux<String>
            .window(2) // 2개의 엘리먼트를 기다림, Flux<Flux<String> -> (A, B), (C, D), (E, F)
//            .concatMap{ // 순차적으로 실행 쓰레드가 3개지만 이어서 실행되어 느리다
//                it.map {
//                    convertToList(it)
//                }.subscribeOn(parallel())// Flux<List<String>
//            }
            .flatMapSequential{ // 순차적으로 실행하지만 병렬적으로 실행
                it.map {
                    convertToList(it)
                }.subscribeOn(parallel())// Flux<List<String>
            }
            .flatMap{
                Flux.fromIterable(it) //Flux<String>
            }//db or external service call that returns a flux -> s -> Flux<String>
            .log()

        StepVerifier.create(stringFlux)
            .expectNextCount(12)
            .verifyComplete()
    }

    private fun convertToList(it: String): MutableList<String> {
        Thread.sleep(1000)
        return mutableListOf(it, "newValue")
    }
}