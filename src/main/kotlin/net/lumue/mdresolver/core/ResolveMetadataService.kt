package net.lumue.mdresolver.core

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ResolveMetadataService(
        @Autowired
        val resolvers : Set<MovieMetadataResolver>
        ) {

    suspend fun resolveForUrl(url:String): MovieMetadata {
        val siteResolver=findResolver(url)
        return siteResolver.resolveMetadata(url)
    }

    private fun findResolver(url: String): MovieMetadataResolver {
        val matchedResolver = resolvers.filter { r -> r.canResolveForUrl(url) }.firstOrNull()
        if(matchedResolver==null)
            throw NoResolverFound("no resolver found for "+url)
        return matchedResolver
    }

}