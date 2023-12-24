package net.lumue.mdresolver.sites.xh

import com.fasterxml.jackson.databind.ObjectMapper
import net.lumue.mdresolver.core.MovieMetadata
import net.lumue.mdresolver.core.MovieMetadataResolver
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.lang.RuntimeException

@Component
class XhResolver : MovieMetadataResolver {

    private val xhVideoPageParser: XhVideoModelParser = XhVideoModelParser()

    private val logger = LoggerFactory.getLogger(this.javaClass.name)

    private val xhHttpClient= XhHttpClient()

    override suspend fun resolveMetadata(url: String): MovieMetadata {
        logger.debug("resolving metadata for location $url")
        xhHttpClient.addCookie("lang","en",".xhamster.com")
        val htmlAsString=xhHttpClient.getContentAsString(url.withGenericXhamsterDomain())
        val page=xhVideoPageParser.fromHtml(htmlAsString)

       return page.extractContentMetadata()
    }

    override fun canResolveForUrl(url: String):Boolean {
        return url.contains("xhamster")
    }


}

class XhVideoModelParser {

    private val objectMapper: ObjectMapper = ObjectMapper()

    val logger: Logger = LoggerFactory.getLogger(XhVideoModelParser::class.java)

    fun fromHtml(htmlAsString: String): XhVideoPage {

        return try {
            val doc = Jsoup.parse(htmlAsString)
            XhVideoPage(doc)
        } catch (e: Throwable) {
            val msg = "error parsing json content from page object"
            logger.error(msg, e)
            throw RuntimeException(msg, e)
        }
    }

    private fun XhVideoPage(doc: Document): XhVideoPage {
        val initialsJsonString = doc.initialsJsonString
        val ldJsonString = doc.ldJsonString
        val metadata=doc.meta
        val videoTagElements=doc.videoTags
        val initialsJson = objectMapper.readValue(initialsJsonString, XhVideoPage.InitialsJson::class.java)
        val ldJson = objectMapper.readValue(ldJsonString, XhVideoPage.LdJson::class.java)
        return XhVideoPage(ldJson, initialsJson,metadata,videoTagElements)
    }

    private val Document.initialsJsonString: String
        get():String {
            val scriptString = run {
                //2. Parses and scrapes the HTML response
                select("#initials-script").first()
                    ?.dataNodes()
                    ?.first()
                    ?.wholeData
            }
                ?: return "{}"
            val startOfInitialsJson = scriptString.indexOfFirst { c -> '{' == c }
            return scriptString.substring(startOfInitialsJson)
        }
    private val Document.ldJsonString: String
        get():String {
            val scriptString = run {
                //2. Parses and scrapes the HTML response
                getElementsByAttributeValue("type", "application/ld+json")
                    .first()
                    ?.dataNodes()
                    ?.first()
                    ?.wholeData
            }
                ?: return "{}"
            val startOfJson = scriptString.indexOfFirst { c -> '{' == c }
            return scriptString.substring(startOfJson)
        }

    private val Document.meta: List<Element>
        get():List<Element> {
                return run{getElementsByTag("meta") }
        }

    private val Document.videoTags: List<Element>
        get():List<Element> {
            return run{getElementsByClass("video-tag")}
        }
}








