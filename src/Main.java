import java.awt.MouseInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

public class Main {

  public static void main(String[] args) throws InterruptedException {
    User me = new PersonZero(args[0] + args[1], args[2], args[3]);

    long startTime = System.nanoTime();

    System.setProperty("webdriver.chrome.driver", "/Users/saif/Downloads/chromedriver");

    WebDriver driver = new ChromeDriver();
    Actions actions = new Actions(driver);

    //driver.manage().window().maximize();
    driver.get("https://www.instagram.com");

    pause(1);

    WebElement ele = driver.findElement(By.xpath("//*[@id=\"react-root\"]/section/main/article" +
            "/div[2]/div[2]/p/a"));
    actions.moveToElement(ele).click(ele).build().perform();

    pause(1);

    ArrayList<WebElement> fields = (ArrayList<WebElement>) driver.findElements(By.tagName("input"));
    fields.get(0).sendKeys(args[2]);
    fields.get(1).sendKeys(args[3]);

    /*for(WebElement el: fields) {
      System.out.print(el.getAttribute("id") + " " + el.getTagName() + " " + el.getText()
              + " " + el.getSize() + " "
              + el.getLocation() + " " + el.isDisplayed() + " " + el.isEnabled() + "\n");
    }*/

    driver.findElement(By.tagName("button")).click();

    pause(1);

    driver.findElement(By.linkText("Profile")).click();

    pause(2);

    List<WebElement> followLinks = driver.findElements(By.partialLinkText("follow"));
    int numFollowers = Integer.parseInt(followLinks.get(0).getText().substring(0,
            followLinks.get(0).getText().indexOf(" ")));
    int numFollowing = Integer.parseInt(followLinks.get(1).getText().substring(0,
            followLinks.get(1).getText().indexOf(" ")));
    System.out.println(numFollowers);
    System.out.println(numFollowing);

    pause(1);
numFollowers = 4;
numFollowing = 3;
    scrapeFollowList(driver, me, 2, numFollowers);
    //System.out.println(me.getFollowers());
    //System.out.println(me.getFollowers().size());

    driver.navigate().back();

    scrapeFollowList(driver, me, 3, numFollowing);
    //System.out.println(me.getFollowing());
    //System.out.println(me.getFollowing().size());

    pause(1);

    for (int i = 1, k = 3; i <= numFollowing; i++) { //iterate through the people I follow
      driver.findElement(By.xpath("/html/body/div[" + k + "]/div/div[2]/div/div[2]/ul/div/li[" + i +
              "]/div/div[1]/div/div[1]/a")).click();

      pause(1);

      scrapeFollowList(driver, me.getFollowing().get(i - 1), 2, numFollowers);
      //numFollowers should be that of person being followed

      driver.navigate().back();

      scrapeFollowList(driver, me.getFollowing().get(i - 1), 3, numFollowing);
      //numFollowing should be that of person being followed

      driver.navigate().back();
      driver.navigate().back();

      if (i == 1) {
        k--;
      }
    }
    driver.quit();
    System.out.printf("\nRun time: %.3f sec", (System.nanoTime() - startTime) / Math.pow(10, 9));
  }

  public static void scrapeFollowList(WebDriver driver, User u, int elementNum, int numFollow)
          throws InterruptedException {
    driver.findElement(By.xpath("//*[@id=\"react-root\"]/section/main/div/header/section/ul/li[" +
            elementNum + "]/a" )).click();

    pause(1);

    for (int i = 1; i <= numFollow; i++) {
      try {
        List<String> nameInfo = new ArrayList<>(Arrays.asList(driver.findElement(By.xpath("/html" +
                "/body/div[3]/div/div[2]/div/div[2]/ul/div/li[" + i + "]")).getText().split("\n")));

        if (nameInfo.size() == 2) { //if user has no name, assign it as empty string
          nameInfo.add(1, "");
        }
        //System.out.println("\n-----------------\nnameinfo: "+nameInfo+"\n--------------\n");

        if (elementNum == 2) {
          u.addFollower(new User(nameInfo.get(1), nameInfo.get(0)));
        }
        else if (elementNum == 3) {
          u.addFollowing(new User(nameInfo.get(1), nameInfo.get(0)));
        }
      }
      catch (NoSuchElementException e) {
        i--;

        JavascriptExecutor jse = (JavascriptExecutor)driver;
        jse.executeScript("y = document.body.getElementsByClassName(\"j6cq2\");" +
                "var x = y[0];x.scrollTo(0,x.scrollHeight);");
      }

      pause(.25);
    }
    //System.out.println("ers: " + u.getFollowers() + "\n  |  ing :" + u.getFollowing());
  }

  public static void pause(double sec) throws InterruptedException {
    TimeUnit.MILLISECONDS.sleep((int) sec * 1000);
  }

  public static void mousePos() throws InterruptedException {
    for (int i = 0; i < 99; i++) {
      int mouseY = MouseInfo.getPointerInfo().getLocation().y;
      int mouseX = MouseInfo.getPointerInfo().getLocation().x;
      System.out.println(mouseX + ", " + mouseY);
      pause(1);
    }
    System.out.println();
  }

  public static void hoverClick(WebDriver driver, WebElement element) {
    Actions action = new Actions(driver);
    action.moveToElement(element).click(element).build().perform();
  }
}

/**
 import java.awt.*;
 import java.io.PrintWriter;
 import java.io.StringWriter;
 import java.text.SimpleDateFormat;
 import java.util.*;
 import java.util.concurrent.*;

 import org.openqa.selenium.By;
 import org.openqa.selenium.JavascriptExecutor;
 import org.openqa.selenium.Keys;
 import org.openqa.selenium.WebDriver;
 import org.openqa.selenium.WebElement;
 import org.openqa.selenium.chrome.ChromeDriver;
 import org.openqa.selenium.interactions.Actions;
 import org.openqa.selenium.support.ui.Select;

 public class prelimTest {

 public static void main(String[] args) throws InterruptedException {
 mousePos();
 long startTime = System.nanoTime();

 System.setProperty("webdriver.chrome.driver",
 "C:\\Users\\\\Downloads\\chromedriver_win32\\chromedriver.exe");
 WebDriver driver = new ChromeDriver();

 driver.manage().window().maximize();
 System.out.println(driver.getTitle() + "\n");

 login(driver);
 pause(3);
 WebElement ele = driver.findElement(By.id("studio_header_top_profile"));
 HoverAndClick(driver, ele, ele);

 pause(2);
 driver.findElement(By.id("sort_icon_name")).click();

 ArrayList <WebElement> links = (ArrayList<WebElement>) driver.findElements(By.tagName("a"));
 for(WebElement el: links)
 System.out.print(el.getTagName() + " " + el.getText() + " " + el.getSize() + " " + el.getLocation()
 + " " + el.isDisplayed() + " " + el.isEnabled() + "\n");
 links.get(0).click();
 pause(3);

 ArrayList<String> tabs = new ArrayList<String> (driver.getWindowHandles());
 driver.switchTo().window(tabs.get(1));

 driver.navigate().to("https://www.google.com/?gws_rd=ssl");
 String search = Keys.chord(Keys.ENTER);
 pause(1);

 driver.findElement(By.className("r")).click();
 JavascriptExecutor jse = (JavascriptExecutor)driver;
 jse.executeScript("window.scrollBy(0,200)", "");
 pause(3);
 driver.close();
 pause(1);
 driver.navigate().back();
 pause(1);
 pause(3);
 driver.quit();
 long endTime = System.nanoTime();
 System.out.printf("\nRun time: %.3f sec", (endTime - startTime) / Math.pow(10, 9));
 }

 public static void login(WebDriver driver) {
 driver.findElement(By.id("chkRemember")).click();
 driver.findElement(By.className("btn-wrapper")).submit();
 }

 public static void pause(int sec) throws InterruptedException {
 TimeUnit.SECONDS.sleep(sec);
 }

 public static void mousePos() throws InterruptedException {
 for(int i=0;i<99;i++) {
 int mouseY = MouseInfo.getPointerInfo().getLocation().y;
 int mouseX = MouseInfo.getPointerInfo().getLocation().x;
 System.out.println(mouseX + ", "+mouseY);
 pause(1);
 }
 System.out.println();
 }

 public static void HoverAndClick(WebDriver driver,WebElement elementToHover,WebElement elementToClick) {
 Actions action = new Actions(driver);
 action.moveToElement(elementToHover).click(elementToClick).build().perform();
 }
 }
 */