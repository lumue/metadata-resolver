package net.lumue.mdresolver.sites.ph

import net.lumue.mdresolver.core.Actor
import net.lumue.mdresolver.core.MovieMetadata
import net.lumue.mdresolver.core.ResolveException
import net.lumue.mdresolver.core.Tag
import org.jsoup.nodes.Document

class PhVideoPage(val document: Document){

    init {
        if(!isPhVideoPage())
            throw ResolveException("Document ${document.pretty} does not seem to be a pornhub videopage")
    }



    val contentMetadata by lazy { document.extractMovieMetadata() }
}

val Document.pretty: String
    get() {
        outputSettings().prettyPrint(true)
        return html()
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

private fun Document.extractActors(): Set<Actor> {
    val actors = select(".pornstarsWrapper")
        .select(".pstar-list-btn")
        .map { a -> Actor("", a.text()) }
    return actors.toMutableSet()
}

private fun Document.extractTags(): Set<Tag> {
    val categories = select(".categoriesWrapper")
                    .select(".item")
                    .map { a -> Tag("", a.text()) }
    val tags=select(".tagsWrapper")
            .select(".item")
            .map { a -> Tag("", a.text()) }
    return  (categories+tags).filter { t-> "" != t.name }.toMutableSet()
}

private fun Document.extractTitle(): String {
    return select("meta[property=\"og:title\"]").attr("content")
}

private fun Document.extractDescription(): String {
    return select("meta[property=\"og:description\"]").attr("content")
}

fun Document.isRnCookiePage(): Boolean {
    val attr = select("body").attr("onload")
    return attr==("go()")
}

fun isPhVideoPage() = true