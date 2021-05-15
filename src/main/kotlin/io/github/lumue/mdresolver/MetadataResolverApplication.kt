package io.github.lumue.mdresolver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MetadataResolverApplication

fun main(args: Array<String>) {
    runApplication<MetadataResolverApplication>(*args)
}
