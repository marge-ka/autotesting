import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

public class IntershopAuthTests {
    private WebDriver driver;
    private WebDriverWait wait;
    String url = "https://intershop5.skillbox.ru";

    @Before
    public void setup() {
        System.setProperty("webdriver.chrome.driver", "drivers\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    }

    @Rule
    public TestWatcher watchman = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
            takeScreenshotOnFailure(description.getMethodName());
        }

        @Override
        protected void finished(Description description) {
            if (driver != null) {
                driver.quit();
            }
        }

        private void takeScreenshotOnFailure(String testName) {
            try {
                File sourceFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                FileUtils.copyFile(sourceFile, new File("screenshots/" + testName + "_" + timestamp + ".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    @Test
    public void testSuccessRegistrationInIntershop() {
        driver.navigate().to(url);
        driver.findElement(By.className("account")).click();
        driver.findElement(By.cssSelector("[class*='register']")).click();
        String user = System.currentTimeMillis() / 60_000 + "@test.ru";
        driver.findElement(By.id("reg_username")).sendKeys(user);
        driver.findElement(By.id("reg_email")).sendKeys(user);
        driver.findElement(By.id("reg_password")).sendKeys(user);
        driver.findElement(By.cssSelector("button[name='register']")).click();
        String message = "Регистрация завершена";
        Assert.assertEquals("Нет сообщения об успешной регистрации", message,
                driver.findElement(By.cssSelector(".content-page div")).getText());
    }

    @Test
    public void testRegistrationFailsWithLongNameEmailPassword() {
        driver.navigate().to(url);
        driver.findElement(By.className("account")).click();
        driver.findElement(By.cssSelector("[class*='register']")).click();
        String user = System.currentTimeMillis()  + "@test.ru";
        driver.findElement(By.id("reg_username")).sendKeys(user);
        driver.findElement(By.id("reg_email")).sendKeys(user);
        driver.findElement(By.id("reg_password")).sendKeys(user);
        driver.findElement(By.cssSelector("button[name='register']")).click();
        String message = "Error: Максимальное допустимое количество символов: 20";
        Assert.assertEquals("Нет сообщения о превышении количества символов", message,
                driver.findElement(By.className("woocommerce-error")).getText());
    }

    @Test
    public void testRegistrationWithExistingEmail() {
        driver.navigate().to(url);
        driver.findElement(By.className("account")).click();
        driver.findElement(By.cssSelector("[class*='register']")).click();
        driver.findElement(By.id("reg_username")).sendKeys("test");
        driver.findElement(By.id("reg_email")).sendKeys("test@test.ru");
        driver.findElement(By.id("reg_password")).sendKeys("test");
        driver.findElement(By.cssSelector("button[name='register']")).click();
        String message = "Error: Учетная запись с такой почтой уже зарегистрирована. Пожалуйста авторизуйтесь.";
        Assert.assertEquals("Не отображается/отображается с ошибкой сообщение об успешной регистрации или ", message,
                driver.findElement(By.className("woocommerce-error")).getText());
    }

    @Test
    public void testSuccessAuthorizationInIntershop() {
        driver.navigate().to(url);
        driver.findElement(By.className("account")).click();
        driver.findElement(By.id("username")).sendKeys("user29497694");
        driver.findElement(By.id("password")).sendKeys("29497694@test.ru");
        driver.findElement(By.cssSelector("[name='login']")).click();
        Assert.assertTrue("Нет приветствия при авторизации",
                driver.findElement(By.xpath("//p[contains(., 'Привет')]")).isDisplayed());
    }

    @Test
    public void testAuthorizationWithInvalidPassword() {
        driver.navigate().to(url);
        driver.findElement(By.className("account")).click();
        driver.findElement(By.id("username")).sendKeys("test");
        driver.findElement(By.id("password")).sendKeys("test");
        driver.findElement(By.cssSelector("[name='login']")).click();
        String message = "Введённый пароль для пользователя test неверный. Забыли пароль?";
        Assert.assertEquals("Не отображается/отображается с ошибкой сообщение о неверном пароле", message,
                driver.findElement(By.className("woocommerce-error")).getText());
    }
    @Test
    public void testPasswordRecovery() {
        driver.navigate().to(url);
        driver.findElement(By.className("account")).click();
        driver.findElement(By.id("username")).sendKeys("test");
        driver.findElement(By.id("password")).sendKeys("test");
        driver.findElement(By.cssSelector("[name='login']")).click();
        driver.findElement(By.cssSelector("li > a[href*='lost-password']")).click();
        driver.findElement(By.id("user_login")).sendKeys("test");
        driver.findElement(By.cssSelector("[value='Reset password']")).click();
        String message = "Password reset email has been sent.";
        Assert.assertEquals("Нет текста об отправке письма для сброса пароля", message, driver.findElement(
                By.className("woocommerce-message")).getText());
    }


    @Test
    public void testDeauthorizationInIntershop() {
        driver.navigate().to(url);
        driver.findElement(By.className("account")).click();
        driver.findElement(By.id("username")).sendKeys("user29497694");
        driver.findElement(By.id("password")).sendKeys("29497694@test.ru");
        driver.findElement(By.cssSelector("[name='login']")).click();
        driver.findElement(By.cssSelector(".login-woocommerce a")).click();
        String text = "Войти";
        Assert.assertEquals("Не происходит выход из аккаунта", text,
                driver.findElement(By.className("account")).getText());
    }


    @Test
    public void testShortMessagePasswordRecoveryPage() {
        driver.navigate().to("https://intershop5.skillbox.ru/my-account/lost-password/?reset-link-sent=true");
        String shortMessage = "Письмо для сброса пароля отправлено.";
        Assert.assertEquals("Некорректно отображается текст об отправке письма для сброса пароля",
                shortMessage, driver.findElement(By.className("woocommerce-message"))
                        .getText());
    }

    @Test
    public void testMessagePasswordRecoveryPage() {
        driver.navigate().to("https://intershop5.skillbox.ru/my-account/lost-password/?reset-link-sent=true");
        String message = "На указанный в вашей учетной записи адрес электронной почты было отправлено письмо" +
                " для сброса пароля, но его появление в папке «Входящие» может занять несколько минут. " +
                "Пожалуйста, подождите не менее 10 минут, прежде чем пытаться сбросить пароль еще раз.";
        Assert.assertEquals("Некорректно отображается сообщение об деталях отправки письма для сброса пароля",
                message, driver.findElement(By.cssSelector(".woocommerce-message + p"))
                        .getText());
    }
}
