package io.github.lumue.mdresolver.sites.ph

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class PhResolverE2E {


    @Test
    fun should_resolve_movie_metadata(){
        runBlocking {
                val movieMetadata = PhResolver().resolveMetadata("http://de.pornhub.com:80/view_video.php?viewkey=6562588c230e8")
                println(movieMetadata)
                Assertions.assertNotNull(movieMetadata)
            }
        }
    }
