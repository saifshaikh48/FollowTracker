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
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

public class Main {
  //private static User me;

  public static void main(String[] args) throws InterruptedException {
    User me = new PersonZero(args[0] + args[1], args[2], args[3]);

    long startTime = System.nanoTime();

    System.setProperty("webdriver.chrome.driver", "\\Users\\Sajjad\\Downloads\\chromedriver_win32\\chromedriver.exe");
    //"/Users/saif/Downloads/chromedriver"

    //test in private session
    ChromeOptions options = new ChromeOptions();
    options.addArguments("incognito");
    DesiredCapabilities.chrome().setCapability(ChromeOptions.CAPABILITY, options);

    WebDriver driver = new ChromeDriver();

    Actions actions = new Actions(driver);

    //driver.manage().window().maximize();
    driver.get("https://www.instagram.com");

    pause(2);

    driver.findElement(By.xpath("//*[@id=\"react-root\"]/section/main/article" +
            "/div[2]/div[2]/p/a")).click();
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

    pause(2);

    driver.findElement(By.linkText("Profile")).click();

    pause(2);

//--------------------------------------------------------------------------------------------------
    //TO DO: avoid adding same person as two separate users in followers/following lists
    List<WebElement> myFollowLinks = driver.findElements(By.partialLinkText("follow"));

    int myNumFollowers = Integer.parseInt(myFollowLinks.get(0).getText().substring(0,
            myFollowLinks.get(0).getText().indexOf(" ")).replaceAll(",", ""));

    int myNumFollowing = Integer.parseInt(myFollowLinks.get(1).getText().substring(0,
            myFollowLinks.get(1).getText().indexOf(" ")).replaceAll(",", ""));
/*System.out.println(myNumFollowers);
System.out.println(myNumFollowing);*/
myNumFollowers = 14;
myNumFollowing = 3;

    pause(1);

    scrapeFollowList(driver, me, 2, myNumFollowers);
/*System.out.println(me.getFollowers());
System.out.println(me.getFollowers().size());*/

    pause(1);

    scrapeOtherFollowLists(driver, me, 2, myNumFollowers);

    driver.navigate().back();

    scrapeFollowList(driver, me, 3, myNumFollowing);
/*System.out.println(me.getFollowing());
System.out.println(me.getFollowing().size());*/

    pause(1);

    scrapeOtherFollowLists(driver, me, 2, myNumFollowing);

    driver.navigate().back();

    driver.quit();


    List<User> allNodes = new ArrayList<>();
    for (User u : me.getFollowers()) {
      allNodes.add(u);
    }
    for (User u : me.getFollowing()) {
      if (!me.getFollowers().contains(u)) {
        allNodes.add(u);
      }
    }
    System.out.println(nodesToCSV(allNodes));

    System.out.printf("\nRun time: %.3f sec", (System.nanoTime() - startTime) / Math.pow(10, 9));
  }

  public static String nodesToCSV(List<User> nodes) {
    StringBuilder csvOutput = new StringBuilder();

    csvOutput.append("name,username,numFollowers,numFollowing");
    for (User u : nodes) {
      csvOutput.append(u.getName() + "," + u.getUsername() + ","
              + u.getFollowers().size() + "," + u.getFollowing().size());
    }

    return csvOutput.toString();
  }

  /**
   * Gets follower/followee lists for my followers/followees.
   * @param driver
   * @param me
   * @param elementNum
   * @param numFollow
   * @throws InterruptedException
   */
    private static void scrapeOtherFollowLists(WebDriver driver, User me, int elementNum,
                                               int numFollow) throws InterruptedException {
      for (int i = 1, k = 3; i <= numFollow; i++) {
        //System.out.println(driver.findElement(By.xpath("/html/body/div["+ k + "]/div/div[2]/div/div[2]/ul/div/li["
        // + i + "]/div/div[2]/span/button")).getText());
        pause(1);

        if (driver.findElement(By.xpath("/html/body/div[" + k + "]/div/div[2]/div/div[2]/ul/div/li["
                + i + "]/div/div[2]/span/button")).getText().equals("Following")) {
          driver.findElement(By.xpath("/html/body/div[" + k + "]/div/div[2]/div/div[2]/ul/div/li[" +
                  i + "]/div/div[1]/div/div[1]/a")).click();
          //System.out.println("occurred " + i);

          pause(1);

          List<WebElement> followLinks = driver.findElements(By.partialLinkText("follow"));

          int numFollowers = Integer.parseInt(followLinks.get(0).getText().substring(0,
                  followLinks.get(0).getText().indexOf(" ")).replaceAll(",", ""));

          int numFollowing = Integer.parseInt(followLinks.get(1).getText().substring(0,
                  followLinks.get(1).getText().indexOf(" ")).replaceAll(",", ""));
/*System.out.println(numFollowers);
System.out.println(numFollowing);*/
          numFollowers = 2;
          numFollowing = 6;

          pause(1);

          User u = null;
          if (elementNum == 2) {
            u = me.getFollowers().get(i - 1);
          }
          else if (elementNum == 3) {
            u = me.getFollowing().get(i - 1);
          }

          scrapeFollowList(driver, u, 2, numFollowers);

          driver.navigate().back();

          scrapeFollowList(driver, u, 3, numFollowing);

          driver.navigate().back();
          driver.navigate().back();

        }

        if (i == 1) {
          k--;
        }

        pause(.25);
      }
    }

  /**
   * Gets user information for all people in a follower/followee list.
   * @param driver
   * @param u
   * @param elementNum
   * @param numFollow
   * @throws InterruptedException
   */
  private static void scrapeFollowList(WebDriver driver, User u, int elementNum, int numFollow)
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

  /**
   *
   * @param sec
   * @throws InterruptedException
   */
  public static void pause(double sec) throws InterruptedException {
    TimeUnit.MILLISECONDS.sleep((int) sec * 1000);
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