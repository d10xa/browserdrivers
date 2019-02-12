package browserdrivers

import geb.Browser
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.chrome.ChromeOptions

class ChromeProxy {

    private final URL proxy

    List<String> ipUrls = [
            "https://ifconfig.io/ip",
            "https://ifconfig.co/ip",
            "https://ifconfig.me/ip",
            "https://icanhazip.com",
            "https://checkip.amazonaws.com/",
            "https://api.ipify.org/",
    ]

    String getRandomIpUrl() {
        ipUrls.get(new Random().nextInt(ipUrls.size()))
    }

    ChromeProxy(URL proxy) {
        this.@proxy = proxy
        assert proxy.userInfo.split(":").every { !it.empty },
                "login and password must be defined for proxy"
    }

    static void addExtensionProxyAutoAuth(ChromeOptions chromeOptions) {
        def tmpFile = File.createTempFile("browserdrivers", "Proxy-Auto-Auth_v2.0.crx")
        ChromeProxy
                .getClassLoader()
                .getResourceAsStream("Proxy-Auto-Auth_v2.0.crx")
                .withCloseable { input ->
            tmpFile.deleteOnExit()
            tmpFile.withOutputStream { output ->
                input.transferTo(output)
            }
        }
        chromeOptions.addExtensions(tmpFile)
    }

    void addArgumentProxyServer(ChromeOptions chromeOptions) {
        chromeOptions.addArguments(
                "--proxy-server=${proxy.protocol}://${proxy.host}:${proxy.port}"
        )
    }

    void ensureProxyAuth(Browser browser) {
        browser.with {
            initProxyAuth(browser)
            go randomIpUrl
            def myIp = $("pre").text()
            boolean authorized = myIp == proxy.host
            if (!authorized) {
                initProxyAuth(browser)
            }
        }
    }

    void initProxyAuth(Browser browser) {
        def userInfo = proxy.userInfo.split(":")
        def login = userInfo[0]
        def password = userInfo[1]
        def driver = browser.driver
        def js = driver as JavascriptExecutor
        js.executeScript("window.localStorage.setItem('proxy_login', '$login');")
        js.executeScript("window.localStorage.setItem('proxy_password', '$password');")
        js.executeScript("window.localStorage.setItem('proxy_retry', 5);")
        js.executeScript("window.localStorage.setItem('proxy_locked', 'false');")
        driver.windowHandles.each { tab ->
            driver.switchTo().window(tab)
            if(driver.currentUrl == "chrome-extension://ggmdpepbjljkkkdaklfihhngmmgmpggp/options.html") {
                driver.close()
            }
            driver.switchTo().window(driver.windowHandles.first())
        }
    }
}
