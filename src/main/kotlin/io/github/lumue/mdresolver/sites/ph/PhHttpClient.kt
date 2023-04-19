package io.github.lumue.mdresolver.sites.ph

import io.github.lumue.mdresolver.sites.BasicHttpClient
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.zip.GZIPInputStream

class PhHttpClient(

) : BasicHttpClient(){
    init {
        addCookie("platform", "pc", "pornhub.com")
        addCookie("accessAgeDisclaimerPH", "1", "pornhub.com")
    }

}
