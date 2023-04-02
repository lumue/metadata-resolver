package io.github.lumue.mdresolver.sites.ph

import io.github.lumue.mdresolver.core.ResolveException
import org.jsoup.nodes.Document
import javax.script.Invocable
import javax.script.ScriptEngineManager

class RnCookiePage(document: Document) {

    val rnKeyScript by lazy { document.extractRnKeyJavascript() }

    init {
        if(!document.isRnCookiePage())
            throw ResolveException("Document ${document.pretty} does not seem to be a pornhub rncookie page")
    }


    private fun Document.extractRnKeyJavascript(): RnKeyJavascript {
        return RnKeyJavascript(
            select("head")
                .select("script").first()!!.dataNodes().first().wholeData
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


}