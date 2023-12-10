package net.lumue.mdresolver.sites.ph

import net.lumue.mdresolver.core.MovieMetadata
import net.lumue.mdresolver.core.MovieMetadataResolver
import net.lumue.mdresolver.core.ResolveException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.net.URL

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


    private suspend fun loadVideoPageDocument(urlString: String, headers: Map<String,String> = mapOf()): Document {
        val document=Jsoup
            .connect(urlString)
            .cookie("accessAgeDisclaimerPH", "1")
            .cookie("platform","pc")
            .get()
        if(document.isPhVideoPage())
            return document
        else if (document.isRnCookiePage())
        {
            logger.warn("need rncookie. trying to calculate one from returned page.")
            val page= RnCookiePage(document)
            val calculatedKey = page.rnKeyScript.calculatedKey
            httpClient.addCookie("RNKEY", calculatedKey,"*.pornhub.com")
            logger.warn("rncookie evaluated to $calculatedKey. setting as RNKEY cookie")
            return loadVideoPageDocument(urlString, mapOf("cookie" to "platform=pc; accessAgeDisclaimerPH=1; RNKEY=$calculatedKey"))
        }

        throw ResolveException("Document ${urlString} does not seem to be a pornhub page")

    }


}






