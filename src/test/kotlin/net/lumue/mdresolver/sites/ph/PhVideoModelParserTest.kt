package net.lumue.mdresolver.sites.ph
import net.lumue.mdresolver.core.Actor
import net.lumue.mdresolver.core.Tag
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class PhVideoModelParserTest {
    val expectedActors= listOf(
        Actor("", "Jay Smooth"),
        Actor("", "Victoria Voxxx")
    )

    val expectedTags= listOf(
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
    @Test
    fun should_parse_expected_actors() {
        val pageText = this.javaClass.getResource("ph6590b4c9bef0d.html")!!.readText()
        val parser = PhPageParser()
        val videoPage=parser.fromHtml(pageText)
        val movieMetadata = videoPage.contentMetadata

        Assertions.assertThat(movieMetadata.actors).hasSameElementsAs(expectedActors)
    }

    @Test
    fun should_parse_expected_tags() {
        val pageText = this.javaClass.getResource("ph6590b4c9bef0d.html")!!.readText()
        val parser = PhPageParser()
        val videoPage=parser.fromHtml(pageText)
        val movieMetadata = videoPage.contentMetadata

        Assertions.assertThat(movieMetadata.tags).hasSameElementsAs(expectedTags)
    }
}