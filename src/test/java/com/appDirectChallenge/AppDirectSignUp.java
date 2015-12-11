
package com.appDirectChallenge;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import com.github.webdriverextensions.WebDriverExtensionsContext;
import com.github.webdriverextensions.junitrunner.WebDriverRunner;
import com.github.webdriverextensions.junitrunner.annotations.Chrome;
import com.github.webdriverextensions.junitrunner.annotations.DriverPaths;
import com.github.webdriverextensions.junitrunner.annotations.Firefox;
import com.github.webdriverextensions.junitrunner.annotations.InternetExplorer;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.*;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.interactions.Actions;

//this can also be ran on chrome and IE.  just need to put the driver files in the same folder as the pom.xml and uncomment the lines below
@RunWith(WebDriverRunner.class)
//@DriverPaths(chrome="chromedriver.exe", internetExplorer = "IEDriverServer.exe")
@FixMethodOrder(MethodSorters.NAME_ASCENDING) //the order of which the tests are run is from top to bottom
@Firefox
//@InternetExplorer
//@Chrome
public class AppDirectSignUp {
    int windowWideWidth, windowLowWidth , windowHeight;
    WebDriver driver;
    String loginTitleText, signUpTitleText;

    @Before
    public void initializeDriverAndGoLandingPage(){
        driver = WebDriverExtensionsContext.getDriver();
        windowWideWidth = 1200; //the menu width shrinks to use single a button menu around 1024
        windowLowWidth = 800;
        windowHeight = 800;
        loginTitleText = "Log In | AppDirect";
        signUpTitleText = "Sign Up for AppDirect";
    }

    /*
    Since the menu dynamically changes based on the width of the browser window, we need to test wide and short versions.
     */
   @Test
    public void test1_clickLoginButtonWithWideWindowWidth(){
        WebElement loginBtn;
        Dimension dimen;
        Actions builder = new Actions(driver);

        driver.navigate().to("http://appdirect.com");
        waitForPage();

        //since the menu interface changes based on width, we need to test both dimensions to ensure it works
        //well on devices with low resolutions
        dimen = new Dimension(windowWideWidth,windowHeight);
        driver.manage().window().setSize(dimen);
        waitSeconds(1);
        loginBtn = driver.findElement(By.xpath(".//*[@id='newnav']/header/div/menu/ul/li[4]/a/span"));
        assertTrue("Cannot find web element login button when window width is " + windowWideWidth,loginBtn.isDisplayed());
        builder.click(loginBtn).perform();

        waitForPage();
        //check to see if we are really on the login page based on title
        assertThat(driver.getTitle(), equalTo(loginTitleText));
    }

    /*
        When narrow, all the menu items are replaced with a mini nav button
      */
    @Test
    public void test1_1_clickLoginButtonWithNarrowWindowWidth(){
        WebElement loginBtn, menuNavBtn;
        Dimension dimen;
        Actions builder = new Actions(driver);

        driver.navigate().to("http://appdirect.com");
        waitForPage();

        //set the width smaller and test the login button from minimized menu
        dimen = new Dimension(windowLowWidth,windowHeight);
        driver.manage().window().setSize(dimen);
        waitSeconds(1);

        menuNavBtn = driver.findElement(By.xpath(".//*[@id='newnav']/nav/div[3]"));
        assertTrue("Cannot find web element menu nav button: ",menuNavBtn.isDisplayed());
        builder.click(menuNavBtn).perform();
        waitSeconds(1);

        loginBtn = driver.findElement(By.xpath(".//*[@id='newnav']/nav/ul[2]/li[4]/a/span"));
        assertTrue("Cannot find web element login button when window width is " +windowLowWidth,loginBtn.isDisplayed());

        builder.click(loginBtn).perform();

        waitForPage();
        //check to see if we are really on the login page based on title
        assertThat(driver.getTitle(), equalTo(loginTitleText));
    }

    /*
       Tests to see if the sign up button works and goes to the right page
     */
    @Test
    public void test2_clickSignUpLinkFromLoginPage(){
        driver.navigate().to("https://appdirect.com/login");  //already starts in login page
        waitForPage();
        Actions builder = new Actions(driver);
        //check to see if we are really on the login page based on title
        assertThat(driver.getTitle(), equalTo(loginTitleText));
        WebElement signUpBtn = driver.findElement(By.xpath(".//*[@id='id13']/div[6]/div[1]/a"));
        assertTrue("Cannot find web element sign up button: ",signUpBtn.isDisplayed());
        builder.click(signUpBtn).perform();
        assertThat(driver.getTitle(), equalTo(signUpTitleText));
    }

    @Test
    public void test3_enterValidEmailForSignUp(){
        driver.navigate().to("https://appdirect.com/signup");
        waitForPage();

        WebElement emailInputBox, signUpBtn;
        assertThat(driver.getTitle(), equalTo(signUpTitleText));
        emailInputBox = driver.findElement(By.xpath(".//*[@id='emailInput']/div[1]/input"));
        assertTrue("Email input box does not exist",emailInputBox.isDisplayed());

        emailInputBox.sendKeys("spam@gmail.com");
      //  emailInputBox.submit();  //test the button instead of pressing enter, so comment out

        signUpBtn = driver.findElement(By.xpath(".//*[@id='id12']"));
        assertTrue("Submit button does not exist",signUpBtn.isDisplayed());

        Actions builder = new Actions(driver);
        builder.click(signUpBtn).perform();
    }

    @Test
    public void test4_clickOnSignUpWithYahooLink(){
        driver.navigate().to("https://appdirect.com/signup");
        waitForPage();

        WebElement signUpWithYahooLink;
        assertThat(driver.getTitle(), equalTo(signUpTitleText));

        signUpWithYahooLink = driver.findElement(By.xpath(".//*[@id='yahooRegisterButton']/span[2]"));
        assertTrue("Yahoo link does not exist",signUpWithYahooLink.isDisplayed());

        Actions builder = new Actions(driver);
        builder.click(signUpWithYahooLink).perform();
    }

    @Test
    public void test3_1_errorTestingForEmailSignUp(){
        driver.navigate().to("https://appdirect.com/signup");
        waitForPage();

        WebElement emailInputBox, errorIcon;
        assertThat(driver.getTitle(), equalTo(signUpTitleText));
        emailInputBox = driver.findElement(By.xpath(".//*[@id='emailInput']/div[1]/input"));
        assertTrue("Email input box does not exist", emailInputBox.isDisplayed());

        errorIcon = driver.findElement(By.xpath(".//*[@id='emailInput']/div[1]/span[3]/*[name()='svg']/*[name()='path']"));
        assertFalse("Error icon should not be showing", errorIcon.isDisplayed());

        emailInputBox.sendKeys("abc123");

        errorIcon = driver.findElement(By.xpath(".//*[@id='emailInput']/div[1]/span[3]/*[name()='svg']/*[name()='path']"));
        assertTrue("Error icon does not exist", errorIcon.isDisplayed());

    }

    @After
    public void cleanUp() {

        driver.quit();
    }

    private void waitForPage(){
        driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
    }

    private void waitSeconds(int sec){
        driver.manage().timeouts().implicitlyWait(sec, TimeUnit.SECONDS);
    }
}
