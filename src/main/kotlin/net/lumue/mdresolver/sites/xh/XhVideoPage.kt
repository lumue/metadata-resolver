package net.lumue.mdresolver.sites.xh

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import net.lumue.mdresolver.core.Actor
import net.lumue.mdresolver.core.MovieMetadata
import net.lumue.mdresolver.core.Tag
import org.jsoup.nodes.Element
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset

@Suppress("unused")
data class XhVideoPage(
    val ldJson : LdJson = LdJson(),
    val initialsJson: InitialsJson = InitialsJson(),
    val metaTags: List<Element>,
    val videoTags: List<Element>
){

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class LdJson(
            @JsonProperty("video") val video: Video? = Video()
    ) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        data class Video (
            @JsonProperty("keywords") val keywords: List<String>? = listOf()
        )
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class InitialsJson(
        @JsonProperty("visitFavorite") val visitFavorite: Boolean = false,
        @JsonProperty("videoModel") val videoModel: VideoModel = VideoModel(),
        @JsonProperty("xplayerSettings") val xplayerSettings: JsonNode? = null,
        @JsonProperty("xplayerPluginSettings") val xplayerPluginSettings: JsonNode? = null,
        @JsonProperty("isVideoViewDurationNeeded") val isVideoViewDurationNeeded: Boolean = false,
        @JsonProperty("partnersCounter") val partnersCounter: JsonNode? = null,
        @JsonProperty("downloadNoDialog") val downloadNoDialog: Boolean = false,
        @JsonProperty("downloadBanner") val downloadBanner: JsonNode? = null,
        @JsonProperty("plAds") val plAds:  JsonNode? = null,
        @JsonProperty("preroll") val preroll: Boolean = false,
        @JsonProperty("vr") val vr:  JsonNode? = null,
        @JsonProperty("vrStats") val vrStats: Boolean = false,
        @JsonProperty("urls") val urls:  JsonNode? = null,
        @JsonProperty("camsData") val camsData: JsonNode? = null,
        @JsonProperty("camsDataTagName") val camsDataTagName: String? = "",
        @JsonProperty("stripchatWidgetSimilarModelsExperiment") val stripchatWidgetSimilarModelsExperiment:  JsonNode? = null,
        @JsonProperty("downloadVRApp") val downloadVRApp: Boolean = false,
        @JsonProperty("favoritesVideoCollectionsCollection") val favoritesVideoCollectionsCollection:  JsonNode? = null,
        @JsonProperty("favoriteEntity") val favoriteEntity: Any? = Any(),
        @JsonProperty("defaultVideoCollectionId") val defaultVideoCollectionId: String? = "",
        @JsonProperty("options") val options: JsonNode? = null,
        @JsonProperty("mlVideoRelated") val mlVideoRelated: MlVideoRelated? = MlVideoRelated(),
        @JsonProperty("userComment") val userComment: UserComment? = UserComment(),
        @JsonProperty("gaSettings") val gaSettings: GaSettings? = GaSettings(),
        @JsonProperty("staticURL") val staticURL: String = "",
    ) {


        @JsonIgnoreProperties(ignoreUnknown = true)
        data class MlVideoRelated(
                @JsonProperty("is_ml_related") val isMlRelated: Boolean = false,
                @JsonProperty("is_sponsor") val isSponsor: Boolean = false,
                @JsonProperty("categories") val categories: List<Category> = listOf()
        ) {
            @JsonIgnoreProperties(ignoreUnknown = true)
            data class Category(
                    @JsonProperty("name") val name: String? = "",
                    @JsonProperty("url") val url: String? = "",
                    @JsonProperty("sponsor-tag") val sponsorTag: Boolean = false,
                    @JsonProperty("pornstar") val pornstar: Boolean = false,
                    @JsonProperty("id") val id: String? = "",
                    @JsonProperty("description") val description: Any? = Any()
            )
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        data class UserComment(
            @JsonProperty("lastActive") val lastActive: Int? = 0,
            @JsonProperty("editable") val editable: Boolean? = false,
            @JsonProperty("vip") val vip: Boolean? = false,
            @JsonProperty("personalInfo") val personalInfo: PersonalInfo? = PersonalInfo(),
            @JsonProperty("modelName") val modelName: String? = "",
            @JsonProperty("thumbURL") val thumbURL: String? = "",
            @JsonProperty("isOnline") val isOnline: Boolean? = false,
            @JsonProperty("id") val id: Int? = 0,
            @JsonProperty("pageURL") val pageURL: String? = "",
            @JsonProperty("retired") val retired: Boolean? = false,
            @JsonProperty("verified") val verified: Boolean? = false,
            @JsonProperty("name") val name: String? = ""
        ) {
            @JsonIgnoreProperties(ignoreUnknown = true)
            data class PersonalInfo(
                @JsonProperty("gender") val gender: Gender? = Gender(),
                @JsonProperty("orientation") val orientation: Orientation? = Orientation(),
                @JsonProperty("geo") val geo: Geo? = Geo(),
                @JsonProperty("birthday") val birthday: Long? = 0
            ) {
                @JsonIgnoreProperties(ignoreUnknown = true)
                data class Gender(
                        @JsonProperty("name") val name: String? = "",
                        @JsonProperty("icon") val icon: String? = "",
                        @JsonProperty("label") val label: String? = ""
                )

                @JsonIgnoreProperties(ignoreUnknown = true)
                data class Geo(
                        @JsonProperty("countryCode") val countryCode: String? = "",
                        @JsonProperty("countryName") val countryName: String? = ""
                )

                @JsonIgnoreProperties(ignoreUnknown = true)
                data class Orientation(
                        @JsonProperty("name") val name: String? = "",
                        @JsonProperty("icon") val icon: String? = "",
                        @JsonProperty("label") val label: String? = ""
                )
            }
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        data class GaSettings(
                @JsonProperty("events") val events: List<Any> = listOf(),
                @JsonProperty("dimensions") val dimensions: Map<String, Any> = mapOf(),
                @JsonProperty("sampling") val sampling: Int = 0,
                @JsonProperty("trackerId") val trackerId: String = "",
                @JsonProperty("amp") val amp: Boolean = false,
                @JsonProperty("fields") val fields: Fields? = Fields()
        ) {

            data class Fields(
                    @JsonProperty("anonymizeIp") val anonymizeIp: Boolean = false
            )


        }


        @JsonIgnoreProperties(ignoreUnknown = true)
        data class VideoModel(
            @JsonProperty("duration") val duration: Long = 0,
            @JsonProperty("title") val title: String? = "",
            @JsonProperty("pageURL") val pageURL: String = "",
            @JsonProperty("icon") val icon: Any? = Any(),
            @JsonProperty("spriteURL") val spriteURL: String = "",
            @JsonProperty("trailerURL") val trailerURL: String = "",
            @JsonProperty("rating") val rating: Rating? = Rating(),
            @JsonProperty("isVR") val isVR: Boolean = false,
            @JsonProperty("isHD") val isHD: Boolean = false,
            @JsonProperty("isUHD") val isUHD: Boolean = false,
            @JsonProperty("created") val created: Long = 0,
            @JsonProperty("modelName") val modelName: String = "",
            @JsonProperty("thumbURL") val thumbURL: String = "",
            @JsonProperty("id") val id: Int = 0,
            @JsonProperty("views") val views: Int = 0,
            @JsonProperty("comments") val comments: Int = 0,
            @JsonProperty("modified") val modified: Int = 0,
            @JsonProperty("orientation") val orientation: String = "",
            @JsonProperty("secured") val secured: Int = 0,
            @JsonProperty("status") val status: Int = 0,
            @JsonProperty("description") val description: String = "",
            @JsonProperty("mp4File") val mp4File: String = "",
            @JsonProperty("spriteCount") val spriteCount: Int = 0,
            @JsonProperty("sources") val sources: Sources = Sources(),
            @JsonProperty("dimensions") val dimensions: JsonNode? = null,
            @JsonProperty("categories",required=false,defaultValue="[]")  val categories: List<Category>? = listOf(),
            @JsonProperty("sponsor") val sponsor: JsonNode? = null,
            @JsonProperty("reported") val reported: Boolean = false,
            @JsonProperty("editable") val editable: Boolean = false
        ) {


            @JsonIgnoreProperties(ignoreUnknown = true)
            data class Category(
                    @JsonProperty("name") val name: String? = "",
                    @JsonProperty("url") val url: String? = "",
                    @JsonProperty("pornstar") val pornstar: Boolean = false,
                    @JsonProperty("id") val id: String? = "",
                    @JsonProperty("description") val description: Any? = Any()
            )


            @JsonIgnoreProperties(ignoreUnknown = true)
            data class Sources(
                @JsonProperty("download") val download: Map<String, Download> = mapOf(),
                @JsonProperty("mp4") val mp4: Map<String, String> = mapOf(),
                @JsonProperty("flv") val flv: Map<String, String>? = mapOf()
            ) {


                @JsonIgnoreProperties(ignoreUnknown = true)
                data class Download(
                        @JsonProperty("link") val link: String = "",
                        @JsonProperty("size") val size: Double = 0.0
                )

            }


            @JsonIgnoreProperties(ignoreUnknown = true)
            data class Rating(
                    @JsonProperty("likes") val likes: Int = 0,
                    @JsonProperty("dislikes") val dislikes: Int = 0
            )




        }
    }

    internal fun extractContentMetadata(): MovieMetadata {
        return MovieMetadata(
            this.initialsJson.videoModel.title ?: "",
            description = this.initialsJson.videoModel.description,
            tags = videoTags
                .filter {t-> t.attr("href").contains("/categories")}
                .map { t-> Tag(t.attr("href"),t.text().trim()) }
                .toSet(),
            actors = videoTags
                    .filter {t-> t.attr("href").contains("/pornstars")}
                .map { t-> Actor(t.attr("href"), t.text().trim()) }
                .toSet(),
            duration = Duration.ofSeconds(this.initialsJson.videoModel.duration),
            views = this.initialsJson.videoModel.views,
            uploaded = LocalDateTime.ofEpochSecond(this.initialsJson.videoModel.created, 0, ZoneOffset.UTC),
            uploader= videoTags
                .filter { t-> t.attr("href").contains("/channels")}
                .map { t-> t.text() }
                .firstOrNull(),
            downloaded = LocalDateTime.now(),
            source = "xhamster",
            sourceURL = this.metaTags.filter { t-> t.attr("property") == "og:url" }.map { t->t.attr("content") }.first(),
            votes = this.initialsJson.videoModel.rating?.likes,
            resolution = 0
        )


}

    private val LdJson.Video.tags: Set<Tag>
        get() {
            return keywords!!
                .filter { k-> k.isNotEmpty() }
                .map { k -> Tag("xhamster-$k", k) }
                .toCollection(mutableSetOf())
        }

    private val InitialsJson.VideoModel.actors: Set<Actor>
        get() {
            if(categories==null)
                return setOf()

            return categories
                .filter { category -> category.name != null && category.url != null }
                .filter { category -> category.pornstar }
                .map { category -> Actor(category.url!!, category.name!!) }
                .toCollection(mutableSetOf())
        }
    private val InitialsJson.VideoModel.tags: Set<Tag>
        get() {

            if(categories==null)
                return setOf()
            return categories
                .filter { category -> category.name != null && category.url != null }
                .filter { category -> !category.pornstar }
                .map { category -> Tag(category.url!!, category.name!!) }
                .toCollection(mutableSetOf())
        }

}

