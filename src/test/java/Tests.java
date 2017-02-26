import java.net.URL;

import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import static org.openqa.selenium.ie.InternetExplorerDriver.*;

public class Tests
{
    private int N = 0;

    //                    consider GRID limit
    @Test( priority = 10, invocationCount = 6, threadPoolSize = 6 )
    public void Selenium_Grid_Chrome() throws Exception
    {
        int n = ++N, m = n;
        System.out.println("\tSelenium Chrome "+ n );

        By              spot;
        WebDriver       driver;
        WebElement      item;
        WebDriverWait   wait;

        driver = new RemoteWebDriver(
            new URL("http://localhost:8844/wd/hub"),
            DesiredCapabilities.chrome() );
        wait = new WebDriverWait( driver, 60 );

        m %= 8; m *= 8;
        driver.manage().window().setSize( new Dimension(800, 600 ) );
        driver.manage().window().setPosition( new Point(32 + m, 64 + m ) );

        driver.get("https://www.google.com");
        wait . until( ExpectedConditions.titleIs("Google") );
        spot = By.name("q");
        wait . until( ExpectedConditions.visibilityOfElementLocated( spot) );
        item = driver.findElement( spot );
        item . clear();
        item . sendKeys("grid");
        item . submit();
        wait . until( new ExpectedCondition<Boolean>()
        {
            public Boolean apply( WebDriver wd )
            {
                return wd.getTitle().startsWith("grid");
            }
        } );
        Assert.assertEquals( driver.getTitle().startsWith("grid"), true );

        System.out.println("\t\t"+ n +" "+ driver.toString() +" closed");

        driver.quit();
        Reporter.log("<i>Thread&nbsp;<b>"+ n +"</b></i>");
    }

    //                    consider GRID limit
    @Test( priority = 10, invocationCount = 2, threadPoolSize = 6 )
    public void Selenium_Grid_InternetExplorer() throws Exception
    {
        int n = ++N, m = n;
        System.out.println("\tSelenium InernetExplorer "+ n );

        By                  spot;
        WebDriver           driver;
        WebElement          item;
        WebDriverWait       wait;
    /*
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setBrowserName("internet explorer");
        caps.setPlatform(org.openqa.selenium.Platform.WINDOWS);
        caps.setCapability(NATIVE_EVENTS, true);
        caps.setCapability(IGNORE_ZOOM_SETTING, true);
        caps.setCapability(REQUIRE_WINDOW_FOCUS, true);
        caps.setCapability(IE_ENSURE_CLEAN_SESSION, true);
        caps.setCapability(UNEXPECTED_ALERT_BEHAVIOR, "Accept");
        caps.setCapability(ENABLE_PERSISTENT_HOVERING, true);
        caps.setCapability(INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
    */
        driver = new RemoteWebDriver(
            new URL("http://localhost:8844/wd/hub"),
            DesiredCapabilities.internetExplorer() );
        wait = new WebDriverWait( driver, 60 );

        m %= 8; m %= 8;
        driver.manage().window().setSize( new Dimension(800, 600 ) );
        driver.manage().window().setPosition( new Point(32 + m, 64 + m ) );

        driver.get("https://www.google.com");
        wait . until( ExpectedConditions.titleIs("Google") );
        spot = By.name("q");
        wait . until( ExpectedConditions.visibilityOfElementLocated( spot) );
        item = driver.findElement( spot );
        item . clear();
        item . sendKeys("grid");
        item . submit();
        wait . until( new ExpectedCondition<Boolean>()
        {
            public Boolean apply( WebDriver wd )
            {
                return wd.getTitle().startsWith("grid");
            }
        } );
        Assert.assertEquals( driver.getTitle().startsWith("grid"), true );

        System.out.println("\t\t"+ n +" "+ driver.toString() +" closed");

        driver.quit();
        Reporter.log("<i>Thread&nbsp;<b>"+ n +"</b></i>");
    }

    @Test( priority = 10, invocationCount = 4, threadPoolSize = 6 )
    public void Http_Request() throws Exception
    {
        int n = ++N;
        System.out.println("\tHttpGET "+ n );

        HttpGet         hget = new HttpGet("https://jsonplaceholder.typicode.com/posts"); // System.getenv("TEST_URL") );
                        hget . addHeader("Content-Type", "application/json");
        HttpResponse    resp = HttpClientBuilder.create().build().execute( hget );
        Assert.assertEquals( resp.getStatusLine().getStatusCode(), 200 );

        String          jstr = EntityUtils.toString( resp.getEntity() );
        JSONArray       jarr = (JSONArray) (new JSONParser()).parse( jstr );
        //              jarr . forEach( System.out::println );
        Assert.assertEquals( jarr.size() > 0, true );

        JSONObject      json;
        int i = 0;
        for( Object item : jarr ) // id, userId, title, body
            if( ++i == 8 ) {
                json = (JSONObject) (new JSONParser()).parse( item.toString() );
                System.out.println("\t\tid: "+ json.get("id") );
                break;
            }
        Reporter.log("<i>Thread&nbsp;<b>"+ n +"</b></i>");
    }
}
