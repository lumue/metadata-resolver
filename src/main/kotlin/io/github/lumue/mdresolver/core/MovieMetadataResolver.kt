package io.github.lumue.mdresolver.core

interface MovieMetadataResolver {
    suspend fun resolveMetadata(url: String) : MovieMetadata
    fun canResolveForUrl(url: String) : Boolean
}