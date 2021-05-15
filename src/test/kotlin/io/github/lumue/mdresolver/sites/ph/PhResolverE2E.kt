package io.github.lumue.mdresolver.sites.ph

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class PhResolverE2E {

    @Test
    fun should_resolve_movie_metadata_for_url(){
        runBlocking {
            val resolver = PhResolver()
            val movieMetadata=resolver.resolveMetadata("https://de.pornhub.com/view_video.php?viewkey=ph5ef8728c23d34")
            println(movieMetadata)
            Assertions.assertNotNull(movieMetadata)
        }
    }

}