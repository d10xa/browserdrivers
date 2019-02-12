package browserdrivers

import geb.Browser
import groovy.transform.CompileStatic
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.DesiredCapabilities

@CompileStatic
class ChromeWithProxyInitBrowser {

    URL proxy

    ChromeOptions options

    DesiredCapabilities desiredCapabilities

    File userDataDir = new File("${System.getProperty('user.home')}/browserdrivers/chrome-user-data-dir")

    File webdriverChromeDriver =
            new File("${System.getProperty('user.home')}/browserdrivers/chromedriver/current/chromedriver")

    ChromeProxy getChromeProxy(){
        new ChromeProxy(proxy)
    }

    ChromeDriver mkDriver(){
        assert proxy != null
        assert userDataDir != null
        assert webdriverChromeDriver != null
        assert options != null
        assert desiredCapabilities != null
        System.setProperty("webdriver.chrome.driver", webdriverChromeDriver.absolutePath)
        ChromeProxy.addExtensionProxyAutoAuth(options)
        chromeProxy.addArgumentProxyServer(options)
        options.addArguments("--user-data-dir=${userDataDir.absolutePath}")
        desiredCapabilities.setCapability(ChromeOptions.CAPABILITY, options)
        new ChromeDriver(desiredCapabilities)
    }

    Browser fillAndInitBrowser() {
        Browser browser = new Browser(driver: mkDriver())
        try {
            chromeProxy.ensureProxyAuth(browser)
        } catch (Exception e) {
            browser.driver.quit()
            throw e
        }
        browser
    }
}
