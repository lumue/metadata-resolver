package net.lumue.mdresolver.sites.ph

import net.lumue.mdresolver.core.MovieMetadata
import net.lumue.mdresolver.core.MovieMetadataResolver
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class PhResolver : MovieMetadataResolver {

    val httpClient = PhHttpClient()
    private val logger = LoggerFactory.getLogger(this.javaClass.name)

    override fun canResolveForUrl(url: String): Boolean {
        return url.contains("pornhub")
    }

    override suspend fun resolveMetadata(url: String): MovieMetadata {
        logger.debug("resolving metadata for location $url")
        val page = PhPageParser().fromHtml(httpClient.getContentAsString(url.withGenericPhDomain()))
        return page.contentMetadata
    }


}

class PhPageParser {
    fun fromHtml(htmlAsString: String): PhVideoPage {

        return try {
            val doc = Jsoup.parse(htmlAsString)
            PhVideoPage(doc)
        } catch (e: Throwable) {
            val msg = "error parsing json content from page object"
            throw RuntimeException(msg, e)
        }
    }
}




