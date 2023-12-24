package net.lumue.mdresolver.sites.ph

import net.lumue.mdresolver.core.MovieMetadata
import net.lumue.mdresolver.core.MovieMetadataResolver
import net.lumue.mdresolver.core.ResolveException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class PhResolver : MovieMetadataResolver {

    val httpClient= PhHttpClient()
    private val logger = LoggerFactory.getLogger(this.javaClass.name)

    override fun canResolveForUrl(url: String) : Boolean{
        return url.contains("pornhub")
    }

    override suspend fun resolveMetadata(url: String): MovieMetadata {
        logger.debug("resolving metadata for location $url")

        val pageDocument = loadVideoPageDocument(url)
        val page= PhVideoPage(pageDocument)

        return page.contentMetadata
    }


    private suspend fun loadVideoPageDocument(urlString: String): Document {
        val document=Jsoup
            .connect(urlString)
            .cookie("accessAgeDisclaimerPH", "1")
            .cookie("platform","pc")
            .get()
        if(document.isPhVideoPage())
            return document
     
        throw ResolveException("Document ${urlString} does not seem to be a pornhub page")

    }


}





