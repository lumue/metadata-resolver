package net.lumue.mdresolver.sites.ph
import net.lumue.mdresolver.core.Actor
import net.lumue.mdresolver.core.MovieMetadata
import net.lumue.mdresolver.core.Tag
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PhVideoModelParserTest {

    private val expectedUploader="Pure Taboo"

    private val expectedSource="pornhub"

    private val expectedSourceUrl="https://www.pornhub.com/view_video.php?viewkey=6590b4c9bef0d"

    private lateinit var movieMetadata: MovieMetadata

    private val expectedActors= listOf(
        Actor("", "Jay Smooth"),
        Actor("", "Victoria Voxxx")
    )

    private val expectedTags= listOf(
        Tag("","Big Ass"), Tag("","Blowjob"),
        Tag("","Cumshot"), Tag("","HD Porn"),
        Tag("","Pornstar"), Tag("","Reality"),
        Tag("","Small Tits"), Tag("","puretaboo"),
        Tag("","reality"), Tag("","victoria voxxx"),
        Tag("","seduction"), Tag("","caught"),
        Tag("","deepthroat"), Tag("","hard rough sex"),
        Tag("","natural tits"), Tag("","choking"),
        Tag("","face fucking"), Tag("","choking on dick"),
        Tag("","reverse cowgirl"), Tag("","extreme deepthroat"),
        Tag("","riding dick"), Tag("","facial"),
        Tag("","petite")
    )

    @BeforeAll
    fun parseMovieMetadata() {
        val pageText =   this.javaClass.getResource("ph6590b4c9bef0d.html")!!.readText()
        val parser = PhPageParser()
        val videoPage = parser.fromHtml(pageText)
        movieMetadata = videoPage.contentMetadata
    }

    @Test
    fun should_parse_expected_actors() {
        Assertions.assertThat(movieMetadata.actors).hasSameElementsAs(expectedActors)
    }

    @Test
    fun should_parse_expected_tags() {
        Assertions.assertThat(movieMetadata.tags).hasSameElementsAs(expectedTags)
    }

    @Test
    fun should_parse_expected_source() {
        Assertions.assertThat(movieMetadata.source).isEqualTo(expectedSource)
    }

    @Test
    fun should_parse_expected_sourceURL() {
        Assertions.assertThat(movieMetadata.sourceURL).isEqualTo(expectedSourceUrl)
    }


    @Test
    fun should_parse_expected_Uploader() {
        Assertions.assertThat(movieMetadata.uploader).isEqualTo(expectedUploader)
    }


}