package browserdrivers

import groovy.transform.CompileStatic
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.OutputType
import org.openqa.selenium.TakesScreenshot
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.interactions.Actions
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

@CompileStatic
class BrowserExtras {

    private static final Logger logger = LoggerFactory.getLogger(BrowserExtras.class)

    private WebDriver driver

    BrowserExtras(WebDriver webDriver){
        this.@driver = webDriver
    }

    TakesScreenshot getScreenshotDriver() {
        driver as TakesScreenshot
    }

    JavascriptExecutor getJs() {
        (JavascriptExecutor) driver
    }

    int getDevicePixelRatio() {
        js.executeScript("return window.devicePixelRatio") as Integer
    }

    int getPageYOffset() {
        js.executeScript("return window.pageYOffset") as Integer
    }

    byte[] screenshot(WebElement el) {
        Actions actions = new Actions(driver)
        actions.moveToElement(el)
        actions.perform()

        def decoded = screenshotDriver.getScreenshotAs(OutputType.BYTES)
        def fullImg = ImageIO.read(new ByteArrayInputStream(decoded))
        def pixelRatio = devicePixelRatio
        int imgWidth = el.size.width
        int imgHeight = el.size.height
        def x = el.location.x * pixelRatio
        def y = (el.location.y - pageYOffset) * pixelRatio
        def w = imgWidth * pixelRatio
        def h = imgHeight * pixelRatio
        logger.debug("screenshot info. " +
                "full screenshot size: ${fullImg.width}x${fullImg.height}, " +
                "pixel ratio: $pixelRatio, " +
                "subimage (x:$x, y:$y, w:$w, h:$h)")
        BufferedImage bufferedImage = fullImg.getSubimage(x, y, w, h)
        def baos = new ByteArrayOutputStream()
        if(!ImageIO.write(bufferedImage, "png", baos)){
            throw new RuntimeException("no appropriate writer is found")
        }
        baos.toByteArray()
    }

    static def humanTyping(WebElement e, String value) {
        for (String i in value) {
            e.sendKeys(i)
            Thread.sleep(30 + new Random().nextInt(250))
        }
    }

}
