package io.github.lumue.mdresolver.restapi

import io.github.lumue.mdresolver.core.MovieMetadata
import io.github.lumue.mdresolver.core.ResolveMetadataService
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class ResolveController (
    @Autowired
    val resolveMetadataService: ResolveMetadataService
){


    @RequestMapping(
            path = ["/resolve"],
            method = [RequestMethod.GET],
            produces = ["application/json; charset=utf-8"]
    )
    fun resolveMetadataforUrlInPath(@RequestParam(required = true) url:String): ResponseEntity<MovieMetadata> {
        val result   = runBlocking {
            resolveMetadataService.resolveForUrl(url)
        }
        return ResponseEntity.ok(result)
    }
}