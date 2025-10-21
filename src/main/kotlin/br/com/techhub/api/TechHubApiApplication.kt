package br.com.techhub.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TechHubApiApplication

fun main(args: Array<String>) {
    runApplication<TechHubApiApplication>(*args)
}