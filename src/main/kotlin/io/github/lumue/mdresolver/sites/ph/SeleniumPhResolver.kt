package io.github.lumue.mdresolver.sites.ph

import io.github.lumue.mdresolver.core.MovieMetadata
import io.github.lumue.mdresolver.core.MovieMetadataResolver
import jakarta.annotation.PreDestroy
import org.jsoup.Jsoup
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.RemoteWebDriver
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.Closeable
import java.net.URL
import kotlin.properties.Delegates

@Component
class SeleniumPhResolver() : MovieMetadataResolver, Closeable {


    private val logger = LoggerFactory.getLogger(this.javaClass.name)

    var driver : WebDriver by Delegates.notNull()


    init {

        val options = ChromeOptions()
        options.addArguments("--remote-allow-origins=*", "--headless")
        driver = RemoteWebDriver(URL("http://vm-services-home:4444"),options)
        driver.startPornhubSessiond()
    }

   fun WebDriver.startPornhubSessiond() {

        get("http://de.pornhub.com")
        val clickable = findElement(By.xpath("//*[@id=\"modalWrapMTubes\"]/div/div/button"))
        clickable?.click()
        get("http://www.pornhub.com/login")
        val usernameInput = findElement(By.xpath("//*[@id=\"username\"]"))
        usernameInput.sendKeys("dirtytom74")
        val pwInput = findElement(By.xpath("//*[@id=\"password\"]"))
        pwInput.sendKeys("ddl85s")
        val submitButton=findElement(By.xpath("//*[@id=\"submit\"]"))
        submitButton.sendKeys(Keys.ENTER)
    }


    override fun canResolveForUrl(url: String): Boolean {
        return url.contains("pornhub")
    }

    override suspend fun resolveMetadata(url: String): MovieMetadata {
        logger.debug("resolving metadata for location $url")
        driver.get(url)
        val pageSource = driver.pageSource
        val page = PhVideoPage(Jsoup.parse(pageSource))
        return page.contentMetadata
    }

    @PreDestroy
    override fun close() {
        driver.quit()
    }


}

