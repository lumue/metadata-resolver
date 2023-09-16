package io.github.lumue.mdresolver.sites.xh

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class XhResolverE2E {

    @Test
    fun should_resolve_movie_metadata_for_url(){
        runBlocking {
            val resolver = XhResolver()
            val movieMetadata=resolver.resolveMetadata("https://ge.xhamster.com/videos/erotic-cinema-3-xhucgED")
            println(movieMetadata)
            Assertions.assertNotNull(movieMetadata)
        }
    }

}