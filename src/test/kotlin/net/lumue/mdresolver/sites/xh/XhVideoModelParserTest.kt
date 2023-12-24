package net.lumue.mdresolver.sites.xh

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class XhVideoModelParserTest {

    @Test
    fun fromHtml() {
        val pageText = this.javaClass.getResource("videopage_20230913.html")!!.readText()
        val xhVideoParser = XhVideoModelParser()
        val xhVideoPage=xhVideoParser.fromHtml(pageText)
        val movieMetadata = xhVideoPage.extractContentMetadata()
        assertTrue(movieMetadata.tags.size==8)
        assertTrue(movieMetadata.actors.size==1)
    }
}