package net.lumue.mdresolver.sites.ph

import kotlinx.coroutines.runBlocking
import net.lumue.mdresolver.core.Actor
import net.lumue.mdresolver.core.MovieMetadata
import net.lumue.mdresolver.core.Tag
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class PhResolverE2E {



    @Test
    fun should_resolve_movie_metadata(){
        runBlocking {
                val movieMetadata = PhResolver().resolveMetadata("https://de.pornhub.com/view_video.php?viewkey=6590b4c9bef0d")
                println(movieMetadata)
                assertNotNull(movieMetadata)
            }
        }
    }
