package io.github.lumue.mdresolver.sites.ph

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.lumue.mdresolver.core.MovieMetadata
import io.github.lumue.mdresolver.core.MovieMetadataResolver
import io.github.lumue.mdresolver.core.ResolveException
import io.github.lumue.mdresolver.core.Tag
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import javax.script.Invocable
import javax.script.ScriptEngineManager

@Component
class PhResolver : MovieMetadataResolver {

    val httpClient=PhHttpClient()
    private val logger = LoggerFactory.getLogger(this.javaClass.name)

    override fun canResolveForUrl(url: String) : Boolean{
        return url.contains("pornhub")
    }

    override suspend fun resolveMetadata(url: String): MovieMetadata {
        logger.debug("resolving metadata for location $url")
        val pageDocument = loadVideoPageDocument(url)
        val page=PhVideoPage(pageDocument)

        return page.contentMetadata
    }


    private suspend fun loadVideoPageDocument(url: String, headers: Map<String,String> = mapOf()): Document {
        val contentAsString = httpClient.getContentAsString(url)
        val document = Jsoup.parse(contentAsString)
        if(document.isPhVideoPage())
            return document
        else if (document.isRnCookiePage())
        {
            logger.warn("need rncookie. trying to calculate one from returned page.")
            val page=RnCookiePage(document)
            val calculatedKey = page.rnKeyScript.calculatedKey
            httpClient.addCookie("RNKEY", calculatedKey,"*.pornhub.com")
            logger.warn("rncookie evaluated to $calculatedKey. setting as RNKEY cookie")
            return loadVideoPageDocument(url, mapOf("Cookie" to "RNKEY=$calculatedKey"))
        }

        throw ResolveException("Document ${url} does not seem to be a pornhub page")

    }


}

class RnCookiePage(document: Document) {

    val rnKeyScript by lazy { document.extractRnKeyJavascript() }

    init {
        if(!document.isRnCookiePage())
            throw ResolveException("Document ${document.pretty} does not seem to be a pornhub rncookie page")
    }


}

private fun Document.extractRnKeyJavascript(): RnKeyJavascript {
    return RnKeyJavascript(
            select("head")
                    .select("script").first().dataNodes().first().wholeData
                    .replace("<!--","")
                    .replace("-->","")
    )
}

class RnKeyJavascript(val source :String) {

    val calculatedKey by lazy { calculateKey() }

    val generatedSource by lazy { generateSource() }

    val invocableJavaScript by lazy { evaluateGeneratedSource() }

    private fun evaluateGeneratedSource(): Invocable {
        val scriptEngine = ScriptEngineManager().getEngineByName("nashorn")
        scriptEngine.eval(generatedSource)
        return scriptEngine as Invocable
    }

    private fun generateSource(): String {
        return source
                .replace("document.location.reload(true);","")
                .replace("document.cookie = \"RNKEY=","return \"")
                .replace("document.cookie=\"RNKEY=","return \"")
    }

    private fun calculateKey(): String {
        return invocableJavaScript.invokeFunction("go") as String
    }

}

private fun Document.isRnCookiePage(): Boolean {
    val attr = select("body").attr("onload")
    return attr!=null&&attr==("go()")
}

private fun Document.isPhVideoPage() = select("#player").isNotEmpty()


class PhVideoPage(val document: Document){

    init {
        if(!document.isPhVideoPage())
            throw ResolveException("Document ${document.pretty} does not seem to be a pornhub videopage")
    }



    val contentMetadata by lazy { document.extractMovieMetadata() }
}

private val Document.pretty: String
    get() {
        outputSettings().prettyPrint(true)
        return html()
    }



private fun Document.extractPlayerJson(): PlayerJson {
    val playerDiv = select("#player")
    val scriptElements = playerDiv.select("script")
    val scriptElement = scriptElements.first()
    val playerScript = scriptElement.dataNodes().first().wholeData
    val from=playerScript.indexOfFirst { c -> '{' == c}
    val to=playerScript.indexOf("};")+1
    val jsonString=playerScript.substring(from,to)
    try {
        return ObjectMapper().readValue(jsonString, PlayerJson::class.java)
    } catch (e: Throwable) {
        throw ResolveException("error parsing $jsonString to json ", e)
    }
}


private fun Document.extractMovieMetadata(): MovieMetadata {
    return MovieMetadata(
            title = extractTitle(),
            description = extractDescription(),
            source = "pornhub",
            tags = extractTags(),
            actors = extractActors(),
            resolution = 0
    )
}

private fun Document.extractActors(): Set<MovieMetadata.Actor> {
    val actors = select(".pornstarsWrapper").select("a")
            .map { a -> MovieMetadata.Actor(a.attr("href").toString(), a.text()) }
    return actors.toMutableSet()
}

private fun Document.extractTags(): Set<Tag> {
    val categories = select(".categoriesWrapper").select("a")
            .map { a -> Tag(a.attr("href").toString(), a.text()) }
    val tags=select(".tagsWrapper").select("a")
            .map { a -> Tag(a.attr("href").toString(), a.text()) }
    return  (categories+tags).filter { t-> "" != t.id }.toMutableSet()
}

private fun Document.extractTitle(): String {
    return select("meta[property=\"og:title\"]").attr("content").toString()
}

private fun Document.extractDescription(): String {
    return select("meta[property=\"og:description\"]").attr("content").toString()
}

@JsonIgnoreProperties(ignoreUnknown = true)
private data class PlayerJson(
        @JsonProperty("actionTags")
        val actionTags: String = "",
        @JsonProperty("appId")
        val appId: String = "",
        @JsonProperty("autoplay")
        val autoplay: String = "",
        @JsonProperty("autoreplay")
        val autoreplay: String = "",
        @JsonProperty("cdn")
        val cdn: String = "",
        @JsonProperty("cdnProvider")
        val cdnProvider: String = "",
        @JsonProperty("defaultQuality")
        val defaultQuality: List<Int> = listOf(),
        @JsonProperty("disable_sharebar")
        val disableSharebar: Int = 0,
        @JsonProperty("embedCode")
        val embedCode: String = "",
        @JsonProperty("errorReports")
        val errorReports: String = "",
        @JsonProperty("hidePostPauseRoll")
        val hidePostPauseRoll: String = "",
        @JsonProperty("hotspots")
        val hotspots: List<String>? = listOf(),
        @JsonProperty("htmlPauseRoll")
        val htmlPauseRoll: String = "",
        @JsonProperty("htmlPostRoll")
        val htmlPostRoll: String = "",
        @JsonProperty("image_url")
        val imageUrl: String = "",
        @JsonProperty("isHD")
        val isHD: String = "",
        @JsonProperty("language")
        val language: String = "",
        @JsonProperty("link_url")
        val linkUrl: String = "",
        @JsonProperty("mediaDefinitions")
        val mediaDefinitions: List<MediaDefinition> = listOf(),
        @JsonProperty("mostviewed_url")
        val mostviewedUrl: String = "",
        @JsonProperty("mp4_seek")
        val mp4Seek: String = "",
        @JsonProperty("nextVideo")
        val nextVideo: NextVideo = NextVideo(),
        @JsonProperty("options")
        val options: String = "",
        @JsonProperty("outBufferLagThreshold")
        val outBufferLagThreshold: Int = 0,
        @JsonProperty("pauseroll_url")
        val pauserollUrl: String = "",
        @JsonProperty("postroll_url")
        val postrollUrl: String = "",
        @JsonProperty("related_url")
        val relatedUrl: String = "",
        @JsonProperty("service")
        val service: String = "",
        @JsonProperty("startLagThreshold")
        val startLagThreshold: Int = 0,
        @JsonProperty("thumbs")
        val thumbs: Thumbs = Thumbs(),
        @JsonProperty("toprated_url")
        val topratedUrl: String = "",
        @JsonProperty("vcServerUrl")
        val vcServerUrl: String = "",
        @JsonProperty("video_duration")
        val videoDuration: String = "",
        @JsonProperty("video_title")
        val videoTitle: String = "",
        @JsonProperty("video_unavailable")
        val videoUnavailable: String = "",
        @JsonProperty("video_unavailable_country")
        val videoUnavailableCountry: String = ""
) {
    data class MediaDefinition(
            @JsonProperty("defaultQuality")
            val defaultQuality: Boolean = false,
            @JsonProperty("format")
            val format: String = "",
            @JsonProperty("quality")
            val quality: String = "",
            @JsonProperty("videoUrl")
            val videoUrl: String = ""
    )
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class NextVideo(
            @JsonProperty("duration")
            val duration: String = "",
            @JsonProperty("isHD")
            val isHD: String = "",
            @JsonProperty("nextUrl")
            val nextUrl: String = "",
            @JsonProperty("thumb")
            val thumb: String = "",
            @JsonProperty("title")
            val title: String = ""
    )

    data class Thumbs(
            @JsonProperty("cdnType")
            val cdnType: String = "",
            @JsonProperty("samplingFrequency")
            val samplingFrequency: Int = 0,
            @JsonProperty("thumbHeight")
            val thumbHeight: String = "",
            @JsonProperty("thumbWidth")
            val thumbWidth: String = "",
            @JsonProperty("type")
            val type: String = "",
            @JsonProperty("urlPattern")
            val urlPattern: String = ""
    )
}


