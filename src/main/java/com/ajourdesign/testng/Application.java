package com.ajourdesign.testng;

import java.time.ZoneId;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.*;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Application {
    private String              addr, base = System.getenv("APPLICATION_URL");
    private List<String>        listBefore, listAfter;

    private boolean             PhantomJS = true;
    private WebDriver           driver;
    private WebElement          item;
    private WebDriverWait       wait;
    private List<WebElement>    rows;
    private long                time = 1024;

    @Test(priority = 10)
    public void REST_getOrderList1_Before() throws Exception
    {
        HttpUriRequest  hget = new HttpGet( addr = base + "/order" );
        HttpResponse    resp = HttpClientBuilder.create().build().execute(hget);
        int             code = resp.getStatusLine().getStatusCode();
        Assert.assertEquals(code, 200);

        String          json = EntityUtils.toString(resp.getEntity());
        Assert.assertEquals(json.contains("]"), true);

        listBefore = getOrderIdList(json);
        System.out.println("\n\tREST_getOrderListBefore " + addr + "\t" + listBefore.size() + " orders\n");
        //  for( String s : listBefore ) System.out.println("\t\t"+ s );
        Reporter.log("Total <big><b><i>"+ listBefore.size() +"</i></b></big>");
    }

    @Test(priority = 20)
    public void Selenium_SetUp() throws Exception
    {
        if (PhantomJS)
        {
            //  Working directory (!)
            //  System.setProperty("phantomjs.binary.path",   "/Users/r00t/IdeaProjects/PhoneOrder/PhoneOrder/Mac/phantomjs");
            driver = new PhantomJSDriver();
        }
        else//Chrome
        {
            System.setProperty("webdriver.chrome.driver", "/Users/r00t/Downloads/chromedriver");
            driver = new ChromeDriver();
        }
        wait = new WebDriverWait(driver, 21);
        driver.get(base);
        Assert.assertEquals(driver.getTitle(), "Phone Orders Application");
        System.out.println("\tSelenium_SetUp");
    }

    @Test(priority = 30)
    public void Selenium_AddNew() throws Exception
    {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("new")));
        driver.findElement(By.id("new"))              .click();
        driver.findElement(By.id("newOrderFirstName")).clear();
        driver.findElement(By.id("newOrderFirstName")).sendKeys("White");
        driver.findElement(By.id("newOrderLastName")) .clear();
        driver.findElement(By.id("newOrderLastName")) .sendKeys("House");
        driver.findElement(By.id("newOrderStreet"))   .clear();
        driver.findElement(By.id("newOrderStreet"))   .sendKeys("1600 Pennsylvania Ave.");
        driver.findElement(By.id("newOrderCity"))     .clear();
        driver.findElement(By.id("newOrderCity"))     .sendKeys("Washington, DC");
        driver.findElement(By.id("newOrderZip"))      .clear();
        driver.findElement(By.id("newOrderZip"))      .sendKeys("20500");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("newOrderSubmit")));
        driver.findElement(By.id("newOrderSubmit"))   .click();
        System.out.println("\t.");
        System.out.println("\tSelenium_AddNew");
    }

    @Test(priority = 40)
    public void Selenium_Approve() throws Exception
    {
        driver.navigate().refresh();
        Thread.sleep(time);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ordersTable")));
        rows = driver.findElement(By.id("ordersTable")).findElements(By.tagName("tr"));
        for( int i = 0; i < rows.size(); i++ )
        {
            if( CheckRow(rows.get(i), "pending.approval") )
            {
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approve")));
                driver.findElement(By.id("approve")).click();
                Thread.sleep(time);
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approveOrderPhone")));
                item = driver.findElement(By.id("approveOrderPhone"));
                item.click();
                item.sendKeys("+1 (123) 456-7890");
                item.sendKeys(Keys.TAB);
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approveOrderSubmit")));
                driver.findElement(By.id("approveOrderSubmit")).click();
                System.out.println("\t\t.");
                Thread.sleep(time);
                rows = driver.findElement(By.id("ordersTable")).findElements(By.tagName("tr"));
            }
        }
        System.out.println("\tSelenium_Approve");
    }

    @Test(priority = 50)
    public void Selenium_Activate() throws Exception
    {
        driver.navigate().refresh();
        Thread.sleep(time);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ordersTable")));
        rows = driver.findElement(By.id("ordersTable")).findElements(By.tagName("tr"));
        for( int i = 0; i < rows.size(); i++ )
        {
            if( CheckRow(rows.get(i), "pending.activation") )
            {
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("activate")));
                driver.findElement(By.id("activate")).click();
                Thread.sleep(time);
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("activateOrderSubmit")));
                driver.findElement(By.id("activateOrderSubmit")).click();
                System.out.println("\t\t.");
                Thread.sleep(time);
                rows = driver.findElement(By.id("ordersTable")).findElements(By.tagName("tr"));
            }
        }
        System.out.println("\tSelenium_Activate");
    }

    @Test(priority = 60, alwaysRun = true)
    public void Selenium_TearDown() throws Exception
    {
        driver.quit();
        System.out.println("\tSelenium_TearDown");
    }

    @Test(priority = 70)
    public void REST_getOrderList2_After() throws Exception
    {
        HttpUriRequest  hget = new HttpGet(addr = base + "/order");
        HttpResponse    resp = HttpClientBuilder.create().build().execute(hget);
        int             code = resp.getStatusLine().getStatusCode();
        Assert.assertEquals(code, 200);

        String          json = EntityUtils.toString(resp.getEntity());
        Assert.assertEquals(json.contains("status"), true);

        listAfter = getOrderIdList(json);
        System.out.println("\n\tREST_getOrderListAfter " + addr + "\t" + listAfter.size() + " orders");
        Reporter.log("Total <big><b><i>"+ listAfter.size() +"</i></b></big>");
    }

    @Test(priority = 80)
    public void getNewOrderById() throws Exception
    {
        String          ordr = listAfter.size() == 1 ? listAfter.get(0) : listAfter.removeAll(listBefore) ? listAfter.get(0) : null;
        HttpUriRequest  hget = new HttpGet( addr = base + "/order/id/" + ordr );
        HttpResponse    resp = HttpClientBuilder.create().build().execute( hget );
        int code =      resp.getStatusLine().getStatusCode();
        Assert.assertEquals(code, 200);

        String          json = EntityUtils.toString(resp.getEntity());
        Assert.assertEquals(json.contains(ordr), true);

        System.out.println("\n\tREST_getNewOrderById " + addr );
        System.out.println("\t" + json );
        Reporter.log("<big><b><i>SELENIUM "+ printUnixTimeStamp( json ) +" GMT\n</i></b></big><br>"+ json );
    }

//  Utilities ----------------------------------------------------------------------------------------------------------

    private boolean CheckRow(WebElement row, String status) throws Exception
    {
        List<WebElement> cell = row.findElements(By.tagName("td"));
        for( int i = 0; i < cell.size(); i++ )
        {
            if( cell.get(i).getText().equals(status) )
            {
                System.out.print("\t\t" + status.toUpperCase());
                row.click();
                System.out.println(" selected...");
                Thread.sleep(time >> 1);
                return true;
            }
        }
        return false;
    }

    public static List<String> getOrderIdList(String s)
    {   //  ....+.
        //  "id":"baeda224-7271-4dff-b9bc-d1b0dd5d6b6c"
        //        ....+....1....+....2....+....3....+.
        List<String>    L = new ArrayList();
        int             n = 0, i;
        while ((i = s.indexOf("\"id\":\"", n)) > 0)
        {
            L.add(s.substring(i + 6, i + 42));
            n = i + 45;
        }
        return L;
    }

    public static String printUnixTimeStamp(String json) throws Exception
    {
        DateTimeFormatter   yMdH = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        int                 from = json.indexOf("\"placedOn\":");
        long                unix = Long.parseLong(json.substring(from + 11, from + 21));
        String              time = Instant.ofEpochSecond(unix).atZone(ZoneId.of("GMT-8")).format(yMdH);
        System.out.println("\t\tUNIX time: " + unix + "\tGMT: " + time );
        return              time;
    }
}
/*
    @Parameters({            "browser",      "hub",      "url",       "remote"})
    public void Selenum_SetUp( String browser, String hub, String url, boolean remote ) throws Exception
    {
        if( remote )
        {
            URL host = new URL( hub );
            DesiredCapabilities caps = new DesiredCapabilities();
            switch( browser.toUpperCase() ) {
                case "IE":
                    caps.setBrowserName("internet explorer");
                    caps.setPlatform(org.openqa.selenium.Platform.WINDOWS);
                    caps.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, true);
                    caps.setCapability(InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING, true);
                    caps.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
                    caps.setCapability(InternetExplorerDriver.NATIVE_EVENTS, true);
                    caps.setCapability(InternetExplorerDriver.UNEXPECTED_ALERT_BEHAVIOR, "Accept");
                    caps.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
                    break;
                default: throw new IllegalArgumentException("Invalid remote " + browser);
            }
            driver = new RemoteWebDriver( host, caps );
        }
        else switch( browser.toUpperCase() )
        {
            case "IE":  System.setProperty("webdriver.ie.driver",     "\\Users\\r00t\\Downloads\\IEDriverServer.exe");
                        driver = new InternetExplorerDriver();
                        break;
            case "FF":  System.setProperty("webdriver.gecko.driver",  "/Users/r00t/Downloads/geckodriver");
                        driver = new FirefoxDriver();
                        break;
            case "GC":  System.setProperty("webdriver.chrome.driver", "/Users/r00t/Downloads/chromedriver");
                        driver = new ChromeDriver();
                        break;
            case "JS":  System.setProperty("phantomjs.binary.path",   "/Users/r00t/Downloads/phantomjs-2.1.1-macosx/bin/phantomjs");
                        driver = new PhantomJSDriver();
                        break;
            case "HU":  // https://github.com/SeleniumHQ/htmlunit-driver
                        // https://github.com/SeleniumHQ/selenium/wiki
                        driver = new HtmlUnitDriver(BrowserVersion.CHROME, true);
                        break;
            case "SF":  // https://github.com/SeleniumHQ/selenium/wiki/SafariDriver
                        driver = new SafariDriver();
                        break;
            default:    throw new IllegalArgumentException("Invalid " + browser);
        }
*/
