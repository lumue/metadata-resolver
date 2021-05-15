package io.github.lumue.mdresolver.sites.xh

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.lumue.mdresolver.core.MovieMetadata
import io.github.lumue.mdresolver.core.MovieMetadataResolver
import io.github.lumue.mdresolver.core.Tag
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.lang.RuntimeException
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset

@Component
class XhResolver : MovieMetadataResolver {

    private val xhVideoPageParser: XhVideoModelParser = XhVideoModelParser()

    private val logger = LoggerFactory.getLogger(this.javaClass.name)

    private val xhHttpClient=XhHttpClient()

    override suspend fun resolveMetadata(url: String): MovieMetadata {
        logger.debug("resolving metadata for location $url")
        val htmlAsString=xhHttpClient.getContentAsString(url.replace("//de.", "//"))
        val page=xhVideoPageParser.fromHtml(htmlAsString)
       return page.extractContentMetadata();
    }

    override fun canResolveForUrl(url: String):Boolean {
        return url.contains("xhamster")
    }


}

private fun XhVideoPage.extractContentMetadata(): MovieMetadata {
    val tagsFromInitials = this.initialsJson.videoModel.tags
    val tagsFromLdjson=this.ldJson.video?.tags?: mutableSetOf()
    return MovieMetadata(this.initialsJson.videoModel.title ?: "",
            description = this.initialsJson.videoModel.description,
            tags = tagsFromInitials.union(tagsFromLdjson),
            actors = this.initialsJson.videoModel.actors,
            duration = Duration.ofSeconds(this.initialsJson.videoModel.duration),
            views = this.initialsJson.videoModel.views,
            uploaded = LocalDateTime.ofEpochSecond(this.initialsJson.videoModel.created, 0, ZoneOffset.UTC),
            downloaded = LocalDateTime.now(),
            source = "xhamster",
            sourceURL=this.initialsJson.staticURL,
            votes = this.initialsJson.videoModel.rating?.likes,
            resolution = 0
    )
}

class XhVideoModelParser {

    private val objectMapper: ObjectMapper = ObjectMapper()

    val logger: Logger = LoggerFactory.getLogger(XhVideoModelParser::class.java)

    fun fromHtml(htmlAsString: String): XhVideoPage {
        val doc = Jsoup.parse(htmlAsString)
        val initialsJsonString = doc.initialsJsonString
        val ldJsonString = doc.ldJsonString
        return try {
            val initialsJson = objectMapper.readValue(initialsJsonString, XhVideoPage.InitialsJson::class.java)
            val ldJson = objectMapper.readValue(ldJsonString, XhVideoPage.LdJson::class.java)
            XhVideoPage(ldJson, initialsJson)
        } catch (e: Throwable) {
            val msg = "error parsing json content from page object"
            logger.error(msg, e)
            throw RuntimeException(msg, e)
        }
    }


}


private val XhVideoPage.LdJson.Video.tags: Set<Tag>
    get() {
        return keywords!!
                .filter { k-> k.isNotEmpty() }
                .map { k -> Tag("xhamster-$k", k) }
                .toCollection(mutableSetOf())
    }

private val Document.initialsJsonString: String
    get():String {
        val scriptString = run {
            //2. Parses and scrapes the HTML response
            select("#initials-script") ?.first()
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
                    ?.first()
                    ?.dataNodes()
                    ?.first()
                    ?.wholeData
        }
                ?: return "{}"
        val startOfJson = scriptString.indexOfFirst { c -> '{' == c }
        return scriptString.substring(startOfJson)
    }




private val XhVideoPage.InitialsJson.VideoModel.actors: Set<MovieMetadata.Actor>
    get() {
        return categories
                .filter { category -> category.name != null && category.url != null }
                .filter { category -> category.pornstar }
                .map { category -> MovieMetadata.Actor(category.url!!, category.name!!) }
                .toCollection(mutableSetOf())
    }

private val XhVideoPage.InitialsJson.VideoModel.tags: Set<Tag>
    get() {
        return categories
                .filter { category -> category.name != null && category.url != null }
                .filter { category -> !category.pornstar }
                .map { category -> Tag(category.url!!, category.name!!) }
                .toCollection(mutableSetOf())
    }








