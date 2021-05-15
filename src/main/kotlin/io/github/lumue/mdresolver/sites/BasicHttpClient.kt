package io.github.lumue.mdresolver.sites

import kotlinx.coroutines.*
import org.apache.http.Header
import org.apache.http.HttpStatus
import org.apache.http.client.CookieStore
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.cookie.BasicClientCookie
import org.apache.http.message.BasicHeader
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private val contentAsStringRequestHeaders: Map<String, String> = mapOf(
        "cache-control" to "no-cache",
        "content-type" to "text/html; charset=utf-8",
        "pragma" to "no-cache",
        "user-agent" to "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36"
)

abstract class BasicHttpClient(val password: String = "",
                           val username: String = "",
                           private val cookieStore: CookieStore = BasicCookieStore(),
                           val performLoginHttpCall: (p: String, u: String, hc: CloseableHttpClient) -> Any=fun(_: String, _: String, _: CloseableHttpClient) {},
                           val hasAuthenticatedUserCall: (cs: CookieStore)->Boolean= fun (_: CookieStore) : Boolean{
    return true
}

) {
    val logger = LoggerFactory.getLogger(BasicHttpClient::class.java)!!


    protected val httpClientBuilder: HttpClientBuilder = HttpClientBuilder.create()
            .setDefaultCookieStore(cookieStore)
            

    val loggedIn: Boolean
        get():Boolean {
            return hasAuthenticatedUserCall(cookieStore)
        }



    val loggingIn: AtomicBoolean = AtomicBoolean(false)
    suspend fun getContentAsString(url: String,additionalHeaders: Map<String,String> = mapOf()): String {

        if (!username.isEmpty() && !loggedIn)
            login()


        val httpClient = httpClientBuilder.build()

        return httpClient.getContentAsString(url,additionalHeaders )

    }

    suspend fun login() {

        while (!loggingIn.compareAndSet(false, true)) {
            logger.debug("login already started. waiting...")
            delay(TimeUnit.SECONDS.toMillis(1))
        }

        if (!loggedIn) {
            (suspendCancellableCoroutine<Any> {
                try {
                    performLoginHttpCall(username, password, httpClientBuilder.build())
                    it.resume(Any())
                } catch (e: Throwable) {
                    logger.error(e.message, e)
                    it.resumeWithException(e)
                } finally {
                    loggingIn.set(false)
                }
            })

        } else {
            logger.debug("already logged in")
            loggingIn.set(false)
        }
    }



    fun addCookie( name: String, value: String, domain: String) {
        val basicClientCookie = BasicClientCookie(name, value)
        basicClientCookie.domain=domain
        cookieStore.addCookie(basicClientCookie)
    }





    suspend fun CloseableHttpClient.getContentAsString(url: String, additionalHeaders: Map<String, String> = mapOf()): String {
        return suspendCancellableCoroutine {
            try {
                val result=
                        this.use { httpClient ->
                            val get = HttpGet(url)
                            get.addHeaders(additionalHeaders)
                            get.addHeaders(contentAsStringRequestHeaders)
                            httpClient.execute(get).use{ response ->
                                if (response.statusLine.statusCode != 200)
                                    throw RuntimeException("http error : ${response.statusLine}")
                                val out = ByteArrayOutputStream()
                                response.entity.writeTo(out)
                                String(out.toByteArray(), Charset.forName("UTF-8"))
                            }
                        }
                it.resume(result)
            }
            catch (e:Throwable){
                logger.error("unexpected error getting content from $url as String ",e)
                it.resumeWithException(e)
            }
        }
    }



}

fun HttpGet.addHeaders(headers: Map<String,String>){
    headers.entries.stream()
            .map { h -> BasicHeader(h.key, h.value) }
            .forEach { h -> addHeader(h) }
}


private var HttpGet.resumeAt: Long
    get() {
        val rangeHeader = findResumeAtRangeHeader()
        return if(rangeHeader!=null) {
            rangeHeader.value
                    .removeSuffix("-")
                    .removePrefix("bytes=")
                    .toLong()
        }
        else
            0L
    }
    set(value) {
        val header = findResumeAtRangeHeader()
        if(header !=null)
            removeHeader(header)
        if(value>0)
            addHeader("Range", "bytes=$value-")
    }

private fun HttpGet.findResumeAtRangeHeader(): Header? {
    return getHeaders("Range")
            .filter { header -> header.value.startsWith("bytes=") && header.value.endsWith("-") }
            .firstOrNull()
}

fun CookieStore.names(): List<String> {
    val cookienames = mutableListOf<String>()
    cookies.forEach({
        cookienames.add(it.name)
    })
    return cookienames
}

