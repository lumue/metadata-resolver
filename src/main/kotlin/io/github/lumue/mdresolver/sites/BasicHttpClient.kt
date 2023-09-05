package io.github.lumue.mdresolver.sites

import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import org.apache.http.client.CookieStore
import org.apache.http.client.HttpRequestRetryHandler
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.cookie.BasicClientCookie
import org.apache.http.message.BasicHeader
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private val contentAsStringRequestHeaders: Map<String, String> = mapOf(
        "cache-control" to "no-cache",
        "content-type" to "text/html; charset=utf-8",
        "pragma" to "no-cache",
        "user-agent" to "Mozilla/5.0 (X11; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/114.0",
        "cookie" to "accessAgeDisclaimerPH=1; platform=pc"
)

abstract class BasicHttpClient(
    val password: String = "",
    val username: String = "",
    private val cookieStore: CookieStore = BasicCookieStore(),
    val performLoginHttpCall: (p: String, u: String, hc: CloseableHttpClient) -> Any=fun(_: String, _: String, _: CloseableHttpClient) {},
    val hasAuthenticatedUserCall: (cs: CookieStore)->Boolean= fun (_: CookieStore) : Boolean{  return true}
) {

    private val logger = LoggerFactory.getLogger(BasicHttpClient::class.java)!!

    private val retryHandler = HttpRequestRetryHandler { _, executionCount, _ ->
        // 3 retries at max
        if (executionCount > 5) {
            return@HttpRequestRetryHandler false
        } else {
            // wait a second before retrying again
            Thread.sleep(1000)
            return@HttpRequestRetryHandler true
        }
    }

    private val httpClientBuilder: HttpClientBuilder = HttpClientBuilder.create()
        .setDefaultCookieStore(cookieStore)
        .setDefaultRequestConfig(
            RequestConfig.custom()
                .setConnectTimeout(10000)
                .setSocketTimeout(10000)
                .setConnectionRequestTimeout(10000)
                .build()
        )
        .setRetryHandler(retryHandler)


    private val loggedIn: Boolean
        get():Boolean {
            return hasAuthenticatedUserCall(cookieStore)
        }



    private val loggingIn: AtomicBoolean = AtomicBoolean(false)
    open suspend fun getContentAsString(urlAsString: String, additionalHeaders: Map<String,String> = mapOf()): String {

        if (username.isNotEmpty() && !loggedIn)
            login()


        val httpClient = httpClientBuilder.build()

        return httpClient.getContentAsString(urlAsString,additionalHeaders )

    }

    private suspend fun login() {

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
        basicClientCookie.path="/"
        basicClientCookie.expiryDate=Date(LocalDate.now().atStartOfDay().plusDays(365).toEpochSecond(ZoneOffset.UTC))
        cookieStore.addCookie(basicClientCookie)
    }





    private suspend fun CloseableHttpClient.getContentAsString(url: String, additionalHeaders: Map<String, String> = mapOf()): String {

        return suspendCancellableCoroutine {
            val result:String
            try {
                result=requestPageContent(url, additionalHeaders)
                it.resume(result)
            }
            catch (e:Throwable){
                logger.error("unexpected error getting content from $url as String ",e)
                it.resumeWithException(e)
            }
        }
    }

    private fun CloseableHttpClient.requestPageContent(
        url: String,
        additionalHeaders: Map<String, String>
    ): String {
        this.use { httpClient ->
            val get = HttpGet(url)
            get.addHeaders(additionalHeaders)
            get.addHeaders(contentAsStringRequestHeaders)
            return httpClient.execute(get).use { response ->
                if (response.statusLine.statusCode != 200)
                    throw RuntimeException("http error : ${response.statusLine}")
                val out = ByteArrayOutputStream()
                response.entity.writeTo(out)
                String(out.toByteArray(), Charset.forName("UTF-8"))
            }
        }
    }


}

fun HttpGet.addHeaders(headers: Map<String,String>){
    headers.entries.stream()
            .map { h -> BasicHeader(h.key, h.value) }
            .forEach { h -> addHeader(h) }
}


fun CookieStore.names(): List<String> {
    val cookienames = mutableListOf<String>()
    cookies.forEach {
        cookienames.add(it.name)
    }
    return cookienames
}

