package io.github.lumue.mdresolver.sites.ph

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class PhResolverE2E {


    @ParameterizedTest(name = "resolveMetadata should return MovieMetadata for {0}")
    @MethodSource("provideUrls")
    fun should_resolve_movie_metadata(url:String) {
        runBlocking {
            val resolver = PhResolver()
            for(i in 1..10) {
                val movieMetadata =
                    resolver.resolveMetadata(url)
                println(movieMetadata)
                Assertions.assertNotNull(movieMetadata)
            }
        }
    }

    companion object {
        @JvmStatic
        fun provideUrls(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("https://www.pornhub.com/view_video.php?viewkey=63fc6e9814a82")
                )
        }
    }

}