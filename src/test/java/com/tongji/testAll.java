package com.tongji;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class testAll {

    private static WebDriver driver;
    private String projectName;
    @BeforeClass
    public void beforeClass() throws InterruptedException {
        String localFirfoxPath = "E:\\test\\fire\\firefox.exe";
        System.setProperty("webdriver.firefox.bin", localFirfoxPath);
        driver = new FirefoxDriver();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    @Test(groups = {"testAll"})
    public void testAll() throws Exception {
        String url = "http://localhost/testlink/login.php";
        driver.get(url);
        String pageTitle = driver.getTitle();
        Assert.assertTrue(pageTitle.toLowerCase().contains("testlink"),
                "Expected title to contain 'TestLink', but got: " + pageTitle);
    }

    @Test(dependsOnGroups = "testAll")
    public void testCreateProject() throws InterruptedException{
// ERROR: Caught exception [ERROR: Unsupported command [selectFrame | mainframe | ]]
        driver.findElement(By.id("login")).clear();
        driver.findElement(By.id("login")).sendKeys("admin");
        driver.findElement(By.name("tl_password")).clear();
        driver.findElement(By.name("tl_password")).sendKeys("admin");
        driver.findElement(By.name("login_submit")).click();
        Thread.sleep(2000);
        driver.get("http://localhost/testlink/lib/project/projectView.php");
        Thread.sleep(1000);
        driver.findElement(By.id("create")).click();
        Thread.sleep(1000);
        driver.findElement(By.name("tprojectName")).clear();
        projectName = "Project" + (new Random().nextInt(10000));
        driver.findElement(By.name("tprojectName")).sendKeys(projectName);
        driver.findElement(By.name("tcasePrefix")).clear();
        driver.findElement(By.name("tcasePrefix")).sendKeys(projectName);
// ERROR: Caught exception [ERROR: Unsupported command [selectWindow | name=mainframe | ]]
        driver.findElement(By.name("doActionButton")).click();
        Thread.sleep(1000);
    }

    @Test(dependsOnMethods = "testCreateProject")
        public void testDeleProject() throws InterruptedException{
        driver.get("http://localhost/testlink/lib/project/projectView.php");
        Thread.sleep(1000);
        String xpathDeleButton = "//tr/td[1]/a[contains(text(),'" + projectName + "')]/../following-sibling::td[7]";
        driver.findElement(By.xpath(xpathDeleButton)).click();
        Thread.sleep(1000);
        driver.findElement(By.id("ext-gen20")).click();
        Thread.sleep(1000);
    }

    @Test
    public void login() throws InterruptedException{
        String url = "http://localhost/testlink/login.php";
        driver.get(url);
        driver.findElement(By.id("login")).clear();
        driver.findElement(By.id("login")).sendKeys("admin");
        driver.findElement(By.name("tl_password")).clear();
        driver.findElement(By.name("tl_password")).sendKeys("admin");
        driver.findElement(By.name("tl_password")).sendKeys(Keys.TAB);
        driver.findElement(By.name("login_submit")).click();
        Thread.sleep(1000);
        for(int second = 0;;second++){
            if(second>=60) throw new Error();
            try{if(driver.getCurrentUrl().indexOf("caller=login")>-1)break;}catch (Exception e){
        }Thread.sleep(1000);
        }
    }

    @DataProvider(name = "mainData")
    public static Object[][] mailRightAndWrong(){
        return new Object[][]{{"11111",false} , {"ABC"+(new Random().nextInt(10000))+"@hello.com",true}};
    }

    @Test(dependsOnMethods = {"login", },dataProvider = "mailData")
    public void testCreateUser(String mail,boolean isMailOK)throws InterruptedException {
        driver.navigate().to("http://localhost/testlink/index.php?caller=login");
        Thread.sleep(1000);
        driver.switchTo().defaultContent().switchTo().frame(0);
        driver.findElement(By.xpath("//div[3]/a[3]/img")).click();
        Thread.sleep(1000);
        driver.switchTo().defaultContent().switchTo().frame(1);
        driver.findElement(By.name("doCreate")).click();
        Thread.sleep(1000);
        driver.findElement(By.name("login")).clear();
        driver.findElement(By.name("login")).sendKeys(mail);
        driver.findElement(By.name("firstName")).clear();
        driver.findElement(By.name("firstName")).sendKeys("111");
        driver.findElement(By.name("lastName")).clear();
        driver.findElement(By.name("lastName")).sendKeys("111");
        driver.findElement(By.id("password")).clear();
        driver.findElement(By.id("password")).sendKeys("Admin1111");
        driver.findElement(By.id("email")).clear();
        driver.findElement(By.id("email")).sendKeys(mail);
        driver.findElement(By.name("do_update")).click();
        Thread.sleep(2000);

        if (!isMailOK) {
            for (int second = 0; ; second++) {
                if (second >= 60) throw new Error();
                try {
                    if ("OK".equals(driver.findElement(By.cssSelector("td.x-btn-mc")).getText())) ;
                    break;
                } catch (Exception e) {
                }
                Thread.sleep(1000);
            }
            driver.findElement(By.cssSelector("td.x-btn-mc")).click();
        } else {
            Assert.assertTrue(driver.findElements(By.xpath("//tr/td/div[contains(text(),'" + mail + "')]")).size() != 0);
        }
    }

    @BeforeSuite
    @Parameters({"testEnv"})
    public void beforeSuite(@Optional("insideTestEnv")String testEnv)throws InterruptedException, IOException{
        System.out.println("对应测试环境:"+testEnv);
    }

}